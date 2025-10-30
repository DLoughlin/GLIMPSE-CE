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

L211.AnEmissions <- read_csv("./outputs/L211.AnEmissions.csv", comment = "#")
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
  select(Sector, Process, GCAM_region_ID, year, cost_1990USD_tCe, cum_reduction_MtCO2e, baseline_MtCO2e) ->
  L152.EPA_MACC_MtCO2e_US


L152.EPA_MACC_TotalCost_US_Agriculture <- L152.EPA_MACC_MtCO2e_US %>%
  filter(Sector == "Agriculture" & Process == "Livestock") %>%
  mutate(cost_1990USD_rectangular = cost_1990USD_tCe * cum_reduction_MtCO2e) %>%
  mutate(cost_billion2021USD_rectangular = cost_1990USD_rectangular / 63.631 * 118.37 / 1000) %>%
  select(Sector, Process, year, cost_1990USD_tCe, cost_billion2021USD_rectangular) %>%
  filter(year %in% c(2025)) %>%
  mutate(invest_billion2021USD = 8.5 / 9)

# interpolate the find the price that closed to total investment
L152.EPA_MACC_TotalCost_US_Agriculture_ira <- L152.EPA_MACC_TotalCost_US_Agriculture %>%
  # filter the price range that close to annual investment
  filter(cost_1990USD_tCe %in% c(24, 41)) %>%
  complete(nesting(Sector, Process, year, invest_billion2021USD), cost_1990USD_tCe = seq(24, 41, 1)) %>%
  group_by(Sector, Process, year, invest_billion2021USD) %>%
  mutate(cost_billion2021USD_rectangular = approx_fun(cost_1990USD_tCe, cost_billion2021USD_rectangular, rule = 1),
         cost_1990USD_tCe = as.integer(cost_1990USD_tCe))

# so the price of 26.5 1990$/tC is closest to the total investment


# the rest part will be similar to the CH4_Res part in the other file:

# 1) create a sudo species (CH4_livestock), we just need the placeholder to trigger the MAC, no actual emissions
L211.AnEmissions_CH4_livestock <- L211.AnEmissions %>%
  filter(region == "USA" & Non.CO2 == "CH4_AGR") %>%
  mutate(Non.CO2 = paste0(Non.CO2, "_livestock")) %>%
  mutate(input.emissions = 0)

# 2) create a new MAC for CH4 in Oil and Gas

# delete the original MAC
L252.MAC_an_US_Livestock_delete <- L252.MAC_an %>%
  filter(region == "USA" & Non.CO2 == "CH4_AGR")

# create the exactly same MAC but link to the new market
L252.MAC_an_US_Livestock_ira <- L252.MAC_an %>%
  filter(region == "USA" & Non.CO2 == "CH4_AGR") %>%
  mutate(market.name = "CH4_livestock")

# no need to create tech.change (US are all 0s), and agriculture doesn't have phase-in time


# generate xml


create_xml("IRA/US_livestock_CH4_MAC.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(L211.AnEmissions_CH4_livestock, "OutputEmissions") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_delete, header = "MAC_delete") %>%
  add_xml_data(data = L252.MAC_an_US_Livestock_ira, header = "MAC_NC") %>%
  run_xml_conversion()









