library(gcamdata)
source("./R/constants.R")
source("./R/module-helpers.R")
source("./R/pipeline-helpers.R")
source("./R/utils-data.R")
library(dplyr)
library(tidyr)
library(tibble)
library(assertthat)
library(magrittr)
library(readr)

# Yang Ou
# Sep 2022
# yang.ou@pnnl.gov
# shift livestock MAC curve for IRA


# import data
# .csv.gz files are source data used in pre-built data

EPA_master <- read_csv("./inst/extdata/emissions/EPA/EPA_2019_raw.csv.gz", comment = "#")
EPA_ag <- read_csv("./inst/extdata/emissions/EPA/EPA_2019_MACC_Ag_updated_baseline.csv", comment = "#")
EPA_MACC_master <- read_csv("./inst/extdata/emissions/EPA/EPA_2019_MACC_raw.csv.gz", comment = "#")
EPA_MACC_mapping <- read_csv("./inst/extdata/emissions/EPA_MACC_mapping.csv", comment = "#")
EPA_MACC_control_mapping <- read_csv("./inst/extdata/emissions/EPA_MACC_control_mapping.csv", comment = "#")
EPA_country_map <- read_csv("./inst/extdata/emissions/EPA_country_map.csv", comment = "#")

L252.MAC_an <- read_csv("./outputs/L252.MAC_an.csv", comment = "#")

# updated agriculture data
# EPA provides a separate baseline agriculture data for MAC calculation
EPA_ag %>%
  left_join_error_no_match(EPA_MACC_control_mapping, by = "source") %>%
  left_join_error_no_match(EPA_country_map %>% select(-iso) %>% rename(country = EPA_country), by = "country") %>%
  group_by(GCAM_region_ID, Sector, Process, year) %>%
  summarise(value = sum(value)) %>%
  ungroup() %>%
  filter(year %in% emissions.EPA_MACC_YEAR) ->
  EPA_ag_update

# baseline data
EPA_master %>%
  left_join_error_no_match(EPA_country_map %>% select(-iso) %>% rename(country = EPA_country), by = "country") %>%
  left_join(EPA_MACC_mapping %>% select(-sector), by = c("source", "subsource")) %>%
  filter(!is.na(Process)) %>%
  rename(Sector = sector) %>%
  group_by(GCAM_region_ID, Sector, Process, year) %>%
  summarise(value = sum(value)) %>%
  ungroup() %>%
  filter(year %in% emissions.EPA_MACC_YEAR) %>%
  filter(Sector != "Agriculture") %>%
  bind_rows(EPA_ag_update) ->
  EPA_MACC_baselines_MtCO2e

# mac data
# Convert from 2010$/tCO2e to 1990$/tC
EPA_MACC_master %>%
  left_join(EPA_country_map %>% select(-iso) %>% rename(country = EPA_country), by = "country") %>%
  left_join_error_no_match(EPA_MACC_control_mapping, by = c("sector", "source")) %>%
  rename(cost_2010USD_tCO2e = p, reduction_MtCO2e = q) %>%
  select(GCAM_region_ID, Sector, Process, year, cost_2010USD_tCO2e, reduction_MtCO2e) %>%
  mutate(cost_2010USD_tCO2e = as.numeric(cost_2010USD_tCO2e),
         cost_1990USD_tCe = round(cost_2010USD_tCO2e * emissions.CONV_C_CO2 * gdp_deflator(1990, base_year = 2010), 0)) %>%
  select(-cost_2010USD_tCO2e) ->
  L152.EPA_MACC_MtCO2e_ungrouped

# For in abatement and basebline data:
# Combine aluminum and magnesium processes: define function, then call in both instances
combine_Al_Mg <- function(x) {
  x %>%
    mutate(Process = sub("Primary Aluminum Production", "Aluminum and Magnesium Production", Process),
           Process = sub("Magnesium Manufacturing", "Aluminum and Magnesium Production", Process))
}

# Abatement data
L152.EPA_MACC_MtCO2e_ungrouped %>%
  ungroup %>%
  combine_Al_Mg %>%
  group_by(Sector, Process, GCAM_region_ID, year, cost_1990USD_tCe) %>%
  summarize_at(vars(reduction_MtCO2e), sum) %>%
  ungroup() %>%
  group_by(Sector, Process, GCAM_region_ID, year) %>%
  mutate(cum_reduction_MtCO2e = cumsum(reduction_MtCO2e)) %>%
  ungroup() %>%
  replace_na(list(cum_reduction_MtCO2e = 0)) ->
  L152.EPA_MACC_MtCO2e

# Baseline data
# Also filter for only EPA MACC year
EPA_MACC_baselines_MtCO2e %>%
  combine_Al_Mg %>%
  group_by(GCAM_region_ID, Sector, Process, year) %>%
  summarise(baseline_MtCO2e = sum(value)) %>%
  replace_na(list(baseline_MtCO2e = 0)) %>%
  ungroup() ->
  L152.EPA_MACC_baselines_MtCO2e

# Match in the baseline emissions quantities to abatement tibble then calculate abatement percentages
# Use left_join - there should be NAs (i.e., there are sectors where the baseline is zero) - then drop those NAs
# (ie. MAC curves in regions where the sector/process does not exist - the baseline is zero)
# emissions.MAC_HIGHESTREDUCTION is 0.95, defined in constant.R

L152.EPA_MACC_MtCO2e %>%
  left_join(L152.EPA_MACC_baselines_MtCO2e ,
            by = c("Sector", "Process", "GCAM_region_ID", "year")) %>%
  mutate(reduction_pct = cum_reduction_MtCO2e / baseline_MtCO2e,
         reduction_pct = if_else(is.na(reduction_pct) | is.infinite(reduction_pct), 0, reduction_pct),
         reduction_pct = if_else(reduction_pct >= 1, emissions.MAC_HIGHESTREDUCTION, reduction_pct)) %>%
  ungroup() %>%
  filter(GCAM_region_ID == 1) %>%
  select(Sector, Process, GCAM_region_ID, year, cost_1990USD_tCe, reduction_MtCO2e, baseline_MtCO2e) ->
  L152.EPA_MACC_MtCO2e_US_original

# aggreate all below-zero reductions as 0
L152.EPA_MACC_MtCO2e_US_zero <- L152.EPA_MACC_MtCO2e_US_original %>%
  filter(cost_1990USD_tCe <= 0) %>%
  mutate(cost_1990USD_tCe = 0) %>%
  group_by(Sector, Process, GCAM_region_ID, year, cost_1990USD_tCe, baseline_MtCO2e) %>%
  summarise(reduction_MtCO2e = sum(reduction_MtCO2e)) %>%
  ungroup()

L152.EPA_MACC_MtCO2e_US_updated <- L152.EPA_MACC_MtCO2e_US_original %>%
  filter(cost_1990USD_tCe > 0) %>%
  bind_rows(L152.EPA_MACC_MtCO2e_US_zero) %>%
  arrange(Sector, Process, GCAM_region_ID, year, cost_1990USD_tCe)

L152.EPA_MACC_TotalCost_US_Agriculture <- L152.EPA_MACC_MtCO2e_US_updated %>%
  filter(Sector == "Agriculture" & Process == "Livestock") %>%
  mutate(cost_1990USD_step = cost_1990USD_tCe * reduction_MtCO2e) %>%
  mutate(cost_billion2021USD_step = cost_1990USD_step / 63.631 * 118.37 / 1000) %>%
  group_by(Sector, Process, year, baseline_MtCO2e) %>%
  mutate(cost_billion2021USD_cum = cumsum(cost_billion2021USD_step)) %>%
  mutate(cum_reduction_MtCO2e = cumsum(reduction_MtCO2e)) %>%
  ungroup() %>%
  select(Sector, Process, year, cum_reduction_MtCO2e, cost_billion2021USD_cum, baseline_MtCO2e) %>%
  filter(year %in% c(2025)) %>%
  mutate(invest_billion2021USD = 8.5 / 9)

L152.EPA_MACC_TotalCost_US_Agriculture_ira <- L152.EPA_MACC_TotalCost_US_Agriculture %>%
  mutate(cost_abs = abs(cost_billion2021USD_cum -invest_billion2021USD)) %>%
  group_by(Sector, Process, year) %>%
  filter(cost_abs == min(cost_abs)) %>%
  ungroup() %>%
  mutate(pct = cum_reduction_MtCO2e / baseline_MtCO2e) %>%
  mutate(pct = round(pct , 3)) %>%
  select(Sector, Process, year, pct_ira = pct) %>%
  select(Process, pct_ira)

# original zero-cost reduction
L152.EPA_MACC_TotalCost_US_Agriculture_shift <- L252.MAC_an %>%
  filter(region == "USA" & mac.control == "Livestock" & Non.CO2 == "CH4_AGR" & tax == 0) %>%
  select(Process = mac.control, mac.reduction.original = mac.reduction) %>%
  distinct() %>%
  left_join(L152.EPA_MACC_TotalCost_US_Agriculture_ira, by = "Process") %>%
  mutate(mac_shift = pct_ira - mac.reduction.original) %>%
  select(Process, mac_shift)

# original MAC which should be deleted
L252.MAC_an_US_Livestock_delete <- L252.MAC_an %>%
  filter(region == "USA" & mac.control == "Livestock" & Non.CO2 == "CH4_AGR")

# shift the original MAC
L252.MAC_an_US_Livestock_ira <- L252.MAC_an %>%
  filter(region == "USA" & mac.control == "Livestock" & Non.CO2 == "CH4_AGR") %>%
  left_join(L152.EPA_MACC_TotalCost_US_Agriculture_shift, by = c("mac.control" = "Process")) %>%
  mutate(mac.reduction = mac.reduction + mac_shift) %>%
  select(-mac_shift)

# remove zero-cost-phase-in
L252.MAC_an_US_Livestock_ira_zeroPhaseIn <- L252.MAC_an_US_Livestock_ira %>%
  mutate(zero.cost.phase.in = "0") %>%
  select(region:mac.control, zero.cost.phase.in)


# generate xml
create_xml("all_aglu_emissions_IRR_MGMT_MAC_US_Livestock_delete.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_delete, header = "MAC_delete") %>%
  run_xml_conversion()


create_xml("all_aglu_emissions_IRR_MGMT_MAC_US_Livestock_IRA.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_delete, header = "MAC_delete") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_ira, header = "MAC_NC") %>%
  # delete the shifted one and add the orignal MAC back as the livestock incentives ends in 2030 per current IRA
  add_xml_data(data = L252.MAC_an_US_Livestock_ira %>% mutate(year = 2035), header = "MAC_delete") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_delete %>% mutate(year = 2035), header = "MAC_NC") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_ira_zeroPhaseIn, header = "MACZeroPhaseIn") %>%
  run_xml_conversion()









