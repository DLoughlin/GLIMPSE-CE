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
# update rooftop ITC
# 2020 - 0.26 (default)
# 2025 - 0.3
# 2030 - 0.3
# 2035 - 0.22
# 2040 and after - 0.1 (default)


# import data
A23.itc_USA_IRA <- read_csv("./IRA/A23.itc_USA_IRA_13302.csv", comment = "#")
A23.elecS_inttech_mapping <- read_csv("./inst/extdata/gcam-usa/A23.elecS_inttech_mapping.csv", comment = "#")
L2234.GlobalIntTechCapital_elecS_USA <- read_csv("./outputs/L2234.GlobalIntTechCapital_elecS_USA.csv", comment = "#")


# Adjust FCRs by 1-ITC for technologies that have ITC
A23.elecS_inttech_mapping %>%
  rename(Electric.sector.technology = Electric.sector.intermittent.technology,
         technology = intermittent.technology) %>%
  select(Electric.sector.technology, technology) %>%
  # filter for technologies which are included in the ITC policy
  semi_join(A23.itc_USA_IRA, by = c("technology" = "GCAM.technology")) %>%
  # join is intended to duplicate rows (from single elec. sector techs to elec segments techs)
  # LJENM throws error, so left_join is used
  left_join(A23.itc_USA_IRA, by = c("technology" = "GCAM.technology")) %>%
  select(year, Electric.sector.technology, itc) -> A23.itc_elecS_USA


L2234.GlobalIntTechCapital_elecS_USA %>%
  # filter for technologies which are included in the ITC policy
  semi_join(A23.itc_elecS_USA,
            by = c("intermittent.technology" = "Electric.sector.technology", "year")) %>%
  left_join_error_no_match(A23.itc_elecS_USA,
                           by = c("year", "intermittent.technology" = "Electric.sector.technology")) %>%
  mutate(fixed.charge.rate = fixed.charge.rate * (1 - itc)) %>%
  rename(sector.name = supplysector, subsector.name = subsector) %>%
  mutate(subsector.name0 = subsector.name) %>%
  select(c("sector.name", "subsector.name0", "subsector.name", "intermittent.technology", "year", "input.capital", "fixed.charge.rate")) ->
  L2247.GlobalIntTechFCROnly_elecS_itc_USA



create_xml("IRA/rooftopPV_building_ITC_13302.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L2247.GlobalIntTechFCROnly_elecS_itc_USA, header = "GlobalIntTechFCROnly") %>%
  run_xml_conversion()









