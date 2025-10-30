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
# Oct 2022
# yang.ou@pnnl.gov
# create building subsidies for selected high-efficiency electric technologies for IRA-50122

# original terms and (GCAM technology implementation)
# $1,750 for a heat pump water heater >>> (100% to electric heat pump water heater)
# $8,000 for a heat pump for space heating or cooling >>> (50% to resid heating/electric heat pump, GCAM has no cooling heat pump)
# $840 for an electri stove, cooktop range, or over or an electric heat pump clothes dryer >>> (50% to electric oven, 50% to clothes dryer hi-eff).
# $4,000 for an electric load service center upgrade >>> (not modelled in GCAM)
# $1,600 for insulation, air sealing, and ventilation >>> (100% to air conditioning hi-eff)
# $2,500 for electric wiring >>> (not modelled in GCAM)

# GCAM subsidyimplementation periods (2025 and 2030)
# There is a total amount of 4.5 billion for qualified rebate (we assumed it's sufficient to cover all subsidies)

# the conversion between per unit subsidy into GCAM's cost unit 1075$/GJ is conducted in S5_bld_NEMS_to_GCAM_yzEdit_noEEE.xlsx (by Ying Zhang)

# import data
IRA_building_subsidy <- read_csv("IRA/IRA_building_subsidy.csv", comment = "#") %>%
  rename(sector.name = Service,
         subsector.name = `Fuel Input`,
         technology = Technology)

L244.GlobalTechCost_bld_gcamusa <- read_csv("./outputs/L244.GlobalTechCost_bld_gcamusa.csv", comment = "#")

# create negative "minicam-non-energy-input" as subsidies
L244.GlobalTechCost_bld_gcamusa_elec_subsidy <- L244.GlobalTechCost_bld_gcamusa %>%
  inner_join(IRA_building_subsidy, by = c("sector.name", "subsector.name", "technology")) %>%
  filter(year %in% c(2025, 2030)) %>%
  mutate(input.cost = -round(subsidy, 2)) %>%
  # consider income groups
  # use DOEEEP RO2 data https://zenodo.org/record/6902358
  # d10 (the richest deciles) contribute to ~33% of GDP across all states
  # currently just use national average
  # so on average, 66% of the population are eligible for this subsidy
  mutate(input.cost = input.cost * 0.66) %>%
  mutate(minicam.non.energy.input = "subsidy") %>%
  select(names(L244.GlobalTechCost_bld_gcamusa))


# generate xml
create_xml("IRA/GlobalTechCost_bld_gcamusa_elec_subsidy.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L244.GlobalTechCost_bld_gcamusa_elec_subsidy, header = "GlobalTechCost") %>%
  run_xml_conversion()









