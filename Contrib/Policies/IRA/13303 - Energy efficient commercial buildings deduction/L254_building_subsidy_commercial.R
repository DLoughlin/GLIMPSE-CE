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
# create approximated building credit for selected high-efficiency electric technologies
# commercial buildings for IRA-13303

# original terms and (GCAM technology implementation)
# Calculate $5/sq foot deduction at 26.5% top marginal corporate tax rate = $1.325/sq foot effective reduction at
# 50% participation rate = $0.665/sq foot reduction in cost of incremental cost of commercial HVAC, lighting and
# building shell improvements .

# GCAM approach
# we obtain the current per sq foot cost of commercial HVAC, which is ~$23 per sq foot
# https://airfixture.com/blog/cost-of-an-hvac-system-for-new-construction
# then we calculate the ratio of $0.665/sq foot to $23/sq foot = 3%
# then we created a 3% subsidy for commercial high-efficiency cooling and heating technologies, including
# comm heating - electric heat pump
# comm cooling - air conditioning hi-eff
# comm ventilation - ventilation hi-eff
# comm lighting - solid state (which has the highest efficiency among the three GCAM technologies)

# Finally we create a 3% subsidy for the four technologies above in 2025 and 2030
# import data

# commercial building hi-eff technology subsidy
IRA_building_credit_commercial <- read.csv("./IRA/IRA_building_credit_commercial.csv")

L244.GlobalTechCost_bld_gcamusa <- read_csv("./outputs/L244.GlobalTechCost_bld_gcamusa.csv", comment = "#")



# create negative "minicam-non-energy-input" as subsidies
L244.GlobalTechCost_bld_gcamusa_elec_comm_credit <- L244.GlobalTechCost_bld_gcamusa %>%
  inner_join(IRA_building_credit_commercial, by = c("sector.name", "subsector.name", "technology")) %>%
  filter(year %in% c(2025, 2030)) %>%
  mutate(input.cost = -input.cost * input.cost.subsidy.percent) %>%
  mutate(input.cost = round(input.cost, 3)) %>%
  mutate(minicam.non.energy.input = "comm credit") %>%
  select(names(L244.GlobalTechCost_bld_gcamusa))


# generate xml
create_xml("IRA/GlobalTechCost_bld_gcamusa_comm_credit.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(data = L244.GlobalTechCost_bld_gcamusa_elec_comm_credit, header = "GlobalTechCost") %>%
  run_xml_conversion()









