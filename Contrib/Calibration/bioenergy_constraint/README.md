# gcam-applied

`Title`:  Biomass Constraints for GCAM-USA

`Date`:  June 19, 2020
 
`Name of Developer`:  Matthew Binsted
 
`Objective of the Material`:  Constrain biomass consumption in the USA to roughly a billion tons / year in 2050 (consistent with the DOE Billion Ton Study). Simultaneously, constrain total global bioenergy consumption to 200 EJ / year (the rest of world, or ROW, is allocated 200 EJ - GCAM-USA constraint).
 
`Description of Included Files`:
- bio_ceiling_constraint_state.xml:  establishes a biomass consumption constraint for GCAM-USA (state-level regions + USA region)
- bio_ceiling_constraint_ROW.xml:  establishes a biomass consumption constraint for the rest of the world (ROW), excluding the USA
- bio_ceiling_mkt_global.xml:  creates the input tax market corresponding to the biomass consumption constraint
 
`Version(s) of GCAM for which it has been tested`:  GCAM v5.2
 
`Instructions for Use`:  The files should be added to the end of the configuration file in the following order:
	<Value name = "bio_ceiling">../input/policy/EMRE/GCAM_USA/bio_ceiling_constraint_state.xml</Value>
	<Value name = "bio_ceiling_ROW">../input/policy/EMRE/bio_ceiling_constraint_ROW.xml</Value>
	<Value name = "bio_mkt">../input/policy/EMRE/bio_ceiling_mkt_global.xml</Value>
Note that by using the bio_ceiling_mkt_global.xml, the user must read in a constraint for all model regions or an error will result.  The user can read in a very high, non-binding constraint (e.g. 1000 EJ) for the ROW to constrain USA biomass consumption without constraining global biomass consumption or causing a model error.

`Comments/Adjustments/Additions (with Date)`
