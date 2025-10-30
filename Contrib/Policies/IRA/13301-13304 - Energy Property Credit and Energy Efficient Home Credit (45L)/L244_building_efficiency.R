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
# update building shell efficiency to reflect IRA 13304
# Extension, increase, and modification of new energy efficient home credit (45L)
# data source: AEO 2022 Extended and sunset Tax Credit Cases
# https://www.eia.gov/outlooks/aeo/IIF_credit/pdf/Alt_Policies_Credits.pdf

# in GCAM, we updated the A44.bld_shell_conductance assumption for residential buildings to be the "extended credit" case in AEO
# AEO Table_21._Residential_Sector_Equipment_Stock_and_Efficiency_and_Distributed_Generation
# note that AEO used 2015 index = 1
# so we need scale GCAM relative to 2015 as GCAM assumed 1971 = 1



# import data
A44.gcam_consumer <- read_csv("./inst/extdata/gcam-usa/A44.gcam_consumer.csv", comment = "#")
A44.bld_shell_conductance_IRA_13304 <- read_csv("IRA/A44.bld_shell_conductance_IRA_13304.csv", comment = "#")


# L244.ShellConductance_bld_gcamusa: Shell conductance (inverse of shell efficiency)
L244.ShellConductance_bld_gcamusa <- A44.bld_shell_conductance_IRA_13304 %>%
  # Convert to long form
  gather_years() %>%
  # Interpolate to model years
  complete(gcam.consumer, year = c(year, MODEL_YEARS)) %>%
  group_by(gcam.consumer) %>%
  mutate(value = round(approx_fun(year, value), energy.DIGITS_EFFICIENCY)) %>%
  ungroup() %>%
  filter(year %in% MODEL_YEARS) %>%
  # Repeat for all states
  write_to_all_states(names = c(names(.), "region")) %>%
  # Add nodeInput and building.node.input
  left_join_error_no_match(A44.gcam_consumer, by = "gcam.consumer") %>%
  mutate(floor.to.surface.ratio = energy.FLOOR_TO_SURFACE_RATIO,
         shell.year = year) %>%
  # Rename columns
  rename(shell.conductance = value) %>%
  select(c("region", "gcam.consumer", "nodeInput", "building.node.input", "year", "shell.conductance", "shell.year", "floor.to.surface.ratio"))



# generate xml
create_xml("IRA/building_eff_improvement_IRA_13304.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L244.ShellConductance_bld_gcamusa, header = "ShellConductance") %>%
  run_xml_conversion()









