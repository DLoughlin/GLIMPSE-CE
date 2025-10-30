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
# create a new CH4_Res (CH4 emissions from resource production) species so that we can apply a fee on them
# under the Methane Emissions Reduction Program (fee)

L201.ghg_res <- read_csv("./outputs/L201.ghg_res.csv", comment = "#")
L201.ResReadInControl_ghg_res <- read_csv("./outputs/L201.ResReadInControl_ghg_res.csv", comment = "#")
L252.ResMAC_fos <- read_csv("./outputs/L252.ResMAC_fos.csv", comment = "#")
L252.ResMAC_fos_phaseInTime <- read_csv("./outputs/L252.ResMAC_fos_phaseInTime.csv", comment = "#")
L252.ResMAC_fos_tc_average <- read_csv("./outputs/L252.ResMAC_fos_tc_average.csv", comment = "#")

# create a customized function to isolate US fossil energy and CH4

isolate_US_CH4 <- function(data){
  result <- data %>%
    filter(region == "USA" & Non.CO2 == "CH4" & resource != "coal") %>%
    mutate(Non.CO2 = paste0(Non.CO2, "_Res"))

  return(result)
}

# a constant to set the coverage of gas (EPA's range 43% - 49%)
# lo: 0.43
# med: 0.46
# hi: 0.49
GAS_COVERAGE <- 0.49

# Methane fee only applies to GHGRP reporting entities.
# EPA estimated 43-49% emissions covered

# 1) create a sudo species, we just need the placeholder to trigger the MAC, no actual emissions

L201.ghg_res_CH4_OG <- L201.ghg_res %>%
  isolate_US_CH4() %>%
  mutate(emiss.coef = 0)

# 2) create a new MAC for CH4 in Oil and Gas

# delete the original MAC
L252.ResMAC_fos_delete <- L252.ResMAC_fos %>%
  filter(region == "USA" & Non.CO2 == "CH4" & resource != "coal")

# scale the original MAC mitigation potential to be 46% of the original
L252.ResMAC_fos_OG <- L252.ResMAC_fos %>%
  filter(region == "USA" & Non.CO2 == "CH4" & resource != "coal") %>%
  mutate(mac.reduction = mac.reduction * GAS_COVERAGE,
         market.name = "CH4_Res")

L252.ResMAC_fos_phaseInTime_OG <- L252.ResMAC_fos_phaseInTime %>%
  filter(region == "USA" & Non.CO2 == "CH4" & resource != "coal")

L252.ResMAC_fos_tc_average_OG <- L252.ResMAC_fos_tc_average %>%
  filter(region == "USA" & Non.CO2 == "CH4" & resource != "coal")


# produce output
create_xml("IRA/US_resource_CH4_MAC_high.xml",
           mi_header = "./inst/extdata/mi_headers/ModelInterface_headers.txt") %>%
  add_xml_data(L201.ghg_res_CH4_OG, "ResEmissCoef") %>%
  add_xml_data(L252.ResMAC_fos_delete, "ResMAC_delete") %>%
  add_xml_data(L252.ResMAC_fos_OG, "ResMAC") %>%
  add_xml_data(L252.ResMAC_fos_tc_average_OG, "ResMACTC") %>%
  add_xml_data(L252.ResMAC_fos_phaseInTime_OG, "ResMACPhaseIn") %>%
  run_xml_conversion()

