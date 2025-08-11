/*
* LEGAL NOTICE
* This computer software was prepared by US EPA.
* THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* EXPORT CONTROL
* User agrees that the Software will not be shipped, transferred or
* exported into any country or used in any manner prohibited by the
* United States Export Administration Act or any other applicable
* export laws, restrictions or regulations (collectively the "Export Laws").
* Export of the Software may require some form of license or other
* authority from the U.S. Government, and failure to obtain such
* export control license may result in criminal liability under
* U.S. laws. In addition, if the Software is identified as export controlled
* items under the Export Laws, User represents and warrants that User
* is not a citizen, or otherwise located within, an embargoed nation
* (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
*     and that User is not otherwise prohibited
* under the Export Laws from receiving the Software.
*
* SUPPORT
* GLIMPSE-CE is a derivative of the open-source USEPA GLIMPSE software.
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. 
* The lead GLIMPSE & GLIMPSE- CE developer is Dr. Dan Loughlin (formerly USEPA). 
* Contributors include Tai Wu (USEPA), Farid Alborzi (ORISE), and Aaron Parks and 
* Yadong Xu of ARA through the EPA Environmental Modeling and Visualization 
* Laboratory contract.
* 
*/
package glimpseElement;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * TabPollutantTaxCap provides the user interface and logic for creating and
 * editing pollutant tax/cap policies in the GLIMPSE Scenario Builder.
 * <p>
 * <b>Main responsibilities:</b>
 * <ul>
 * <li>Allow users to select measure type (tax or cap), pollutant, and
 * sector</li>
 * <li>Configure policy and market names (auto/manual)</li>
 * <li>Specify and populate cap/tax values over time</li>
 * <li>Validate, import, and export scenario component data as CSV</li>
 * </ul>
 * </p>
 *
 * <b>Features:</b>
 * <ul>
 * <li>Support for multiple pollutants (CO2, GHG, NOx, SO2, etc.)</li>
 * <li>Automatic and manual naming for policy and market</li>
 * <li>Dynamic enabling/disabling of UI controls based on selections</li>
 * <li>Validation of user input and units</li>
 * <li>Progress tracking for file generation</li>
 * </ul>
 *
 * <b>Usage:</b>
 * 
 * <pre>
 * TabPollutantTaxCap tab = new TabPollutantTaxCap("Pollutant Tax/Cap", stage);
 * // Add to TabPane, interact via UI
 * </pre>
 *
 * <b>Thread Safety:</b> This class is not thread-safe and should be used only
 * on the JavaFX Application Thread.
 */
public class TabPollutantTaxCap extends PolicyTab implements Runnable {
	// === Constants for UI Texts and Options ===
	private static final double MAX_WIDTH = 225;
	private static final double MIN_WIDTH = 115;
	private static final double PREF_WIDTH = 225;
	private static final String[] MEASURE_OPTIONS = { "Select One", "Emission Cap (Mt)", "Emission Tax ($/t)" };
	private static final String[] POLLUTANT_OPTIONS = { "Select One", "CO2 (MT C)", "CO2 (MT CO2)", "GHG (MT CO2E)",
			"NOx (Tg)", "SO2 (Tg)", "PM2.5 (Tg)", "NMVOC (Tg)", "CO (Tg)", "NH3 (Tg)", "CH4 (Tg)", "N2O (Tg)" };
	private static final String LABEL_MEASURE = "Measure: ";
	private static final String LABEL_CATEGORY = "Category: ";
	private static final String LABEL_POLLUTANT = "Pollutant: ";
	// === Magic String Constants for Logic ===
	private static final String TAX = "Tax";
	private static final String CAP = "Cap";
	private static final String SELECT_ONE = "Select One";
	private static final String ALL = "All";
	private static final String INPUT_TABLE = "INPUT_TABLE";
	private static final String VARIABLE_ID = "Variable ID";
	private static final String GLIMPSE_EMISSION_CAP = "GLIMPSEEmissionCap";
	private static final String GLIMPSE_EMISSION_TAX = "GLIMPSEEmissionTax";
	private static final String GLIMPSE_EMISSION_MARKET = "GLIMPSEEmissionMarket";
	private static final String GLIMPSE_ADD_CO2_SUBSPECIES_NEST = "GLIMPSEAddCO2Subspecies-Nest";
	private static final String GLIMPSE_ADD_CO2_SUBSPECIES = "GLIMPSEAddCO2Subspecies";
	private static final String GLIMPSE_GHG_EMISSION_CAP = "GLIMPSEGHGEmissionCap";
	private static final String GLIMPSE_GHG_EMISSION_TAX = "GLIMPSEGHGEmissionTax";
	private static final String GLIMPSE_LINKED_GHG_MARKET_P1 = "GLIMPSELinkedGHGEmissionMarketP1";
	private static final String GLIMPSE_LINKED_GHG_MARKET_P2 = "GLIMPSELinkedGHGEmissionMarketP2";
	private static final String GLIMPSE_EMISSION_CAP_PPS_P1 = "GLIMPSEEmissionCap-PPS-P1";
	private static final String GLIMPSE_EMISSION_CAP_PPS_P2 = "GLIMPSEEmissionCap-PPS-P2";
	private static final String EMISSION_TAX = "Emission Tax";
	private static final String GHG = "GHG";
	private static final String CO2 = "CO2";

	// === State ===
	public static String descriptionText = "";
	public static String runQueueStr = "Queue is empty.";

	// === UI Controls ===
	private final Label labelComboBoxMeasure = createLabel(LABEL_MEASURE, LABEL_WIDTH);
	private final ComboBox<String> comboBoxMeasure = createComboBoxString();
	private final Label labelCheckComboBoxCategory = createLabel(LABEL_CATEGORY, LABEL_WIDTH);
	private final CheckComboBox<String> checkComboBoxCategory = createCheckComboBox();
	private final Label labelComboBoxPollutant = createLabel(LABEL_POLLUTANT, LABEL_WIDTH);
	private final ComboBox<String> comboBoxPollutant = createComboBoxString();

	/**
	 * Constructs a TabPollutantTaxCap for the given title and stage. Sets up all UI
	 * controls, listeners, and default values for the pollutant tax/cap policy tab.
	 * 
	 * @param title  Tab title
	 * @param stageX JavaFX Stage (not used directly)
	 */
	public TabPollutantTaxCap(String title, Stage stageX) {
		this.setText(title);
		this.setStyle(styles.getFontStyle());
		checkBoxUseAutoNames.setSelected(true);
		textFieldPolicyName.setDisable(true);
		textFieldMarketName.setDisable(true);

		// left column
		gridPaneLeft.add(utils.createLabel("Specification:"), 0, 0, 2, 1);
		gridPaneLeft.addColumn(0, labelComboBoxMeasure, labelComboBoxPollutant, labelCheckComboBoxCategory, new Label(),
				new Separator(), labelUseAutoNames, labelPolicyName, labelMarketName, new Label(), new Separator(),
				utils.createLabel("Populate:"), labelModificationType, labelStartYear, labelEndYear, labelInitialAmount,
				labelGrowth, labelConvertFrom);
		gridPaneLeft.addColumn(1, comboBoxMeasure, comboBoxPollutant, checkComboBoxCategory, new Label(), new Separator(),
				checkBoxUseAutoNames, textFieldPolicyName, textFieldMarketName, new Label(), new Separator(),
				new Label(), comboBoxModificationType, textFieldStartYear, textFieldEndYear, textFieldInitialAmount,
				textFieldGrowth, comboBoxConvertFrom);
		gridPaneLeft.setVgap(3.);
		gridPaneLeft.setStyle(styles.getStyle2());

		// center column
		hBoxHeaderCenter.getChildren().addAll(buttonPopulate, buttonDelete, buttonClear);
		hBoxHeaderCenter.setSpacing(2.);
		hBoxHeaderCenter.setStyle(styles.getStyle3());
		vBoxCenter.getChildren().addAll(labelValue, hBoxHeaderCenter, paneForComponentDetails);
		vBoxCenter.setStyle(styles.getStyle2());

		// right column
		vBoxRight.getChildren().addAll(paneForCountryStateTree);
		vBoxRight.setStyle(styles.getStyle2());

		gridPanePresetModification.addColumn(0, gridPaneLeft);
		gridPanePresetModification.addColumn(1, vBoxCenter);
		gridPanePresetModification.addColumn(2, vBoxRight);
		gridPaneLeft.setPrefWidth(370);
		gridPaneLeft.setMinWidth(370);
		vBoxCenter.setPrefWidth(300);
		vBoxRight.setPrefWidth(300);

		// default sizing
		setComboBoxWidths(checkComboBoxCategory);
		setComboBoxWidths(comboBoxMeasure);
		setComboBoxWidths(comboBoxModificationType);
		setComboBoxWidths(comboBoxPollutant);

		for (String option : MEASURE_OPTIONS) {
			comboBoxMeasure.getItems().add(option);
		}
		for (String option : POLLUTANT_OPTIONS) {
			comboBoxPollutant.getItems().add(option);
		}
		for (String option : vars.getCategoriesFromTechBnd()) {
			checkComboBoxCategory.getItems().add(option);
		}
		for (String option : CONVERT_FROM_OPTIONS) {
			comboBoxConvertFrom.getItems().add(option);
		}
		comboBoxMeasure.getSelectionModel().selectFirst();
		comboBoxPollutant.getSelectionModel().selectFirst();
		
		checkComboBoxCategory.getCheckModel().clearChecks();
		//checkComboBoxCategory.getItems().add(ALL);
		checkComboBoxCategory.getCheckModel().check(ALL);
		checkComboBoxCategory.setDisable(true);
		
		comboBoxModificationType.getSelectionModel().selectFirst();
		comboBoxConvertFrom.getSelectionModel().selectFirst();
		comboBoxPollutant.setDisable(false);

		labelConvertFrom.setVisible(false);
		comboBoxConvertFrom.setVisible(false);

		// Action
		setOnAction(comboBoxMeasure, e -> {
			if (comboBoxMeasure.getSelectionModel().getSelectedItem().startsWith(EMISSION_TAX)) {
				labelConvertFrom.setVisible(true);
				comboBoxConvertFrom.setVisible(true);
			} else {
				labelConvertFrom.setVisible(false);
				comboBoxConvertFrom.setVisible(false);
			}
			setPolicyAndMarketNames();
		});
		setOnAction(comboBoxPollutant, e -> {
			String selectedItem = comboBoxPollutant.getSelectionModel().getSelectedItem();
			if (!SELECT_ONE.equals(selectedItem)) {
				if (selectedItem.startsWith(CO2)) {
					checkComboBoxCategory.setDisable(false);
				} else {
					checkComboBoxCategory.getCheckModel().clearChecks();
					checkComboBoxCategory.getCheckModel().check(ALL);
					checkComboBoxCategory.setDisable(true);
				}
			}
			setPolicyAndMarketNames();
		});
		// Uncomment and standardize these as needed:
		// setOnAction(checkComboBoxCategory, e -> setPolicyAndMarketNames());
		// EventHandler<TreeModificationEvent> ev = new EventHandler<TreeModificationEvent>() {
		// 	@Override
		// 	public void handle(TreeModificationEvent ae) {
		// 		ae.consume();
		// 		setPolicyAndMarketNames();
		// 	}
		// };
		// paneForCountryStateTree.addEventHandlerToAllLeafs(ev);
		setOnAction(checkBoxUseAutoNames, e -> {
			boolean selected = checkBoxUseAutoNames.isSelected();
			textFieldPolicyName.setDisable(selected);
			textFieldMarketName.setDisable(selected);
		});
		setOnAction(comboBoxModificationType, e -> {
			String selected = comboBoxModificationType.getSelectionModel().getSelectedItem();
			if (selected == null)
				return;
			switch (selected) {
			case "Initial w/% Growth/yr":
			case "Initial w/% Growth/pd":
				labelGrowth.setText("Growth (%):");
				break;
			case "Initial w/Delta/yr":
			case "Initial w/Delta/pd":
				labelGrowth.setText("Delta:");
				break;
			case "Initial and Final":
				labelGrowth.setText("Final Val:");
				break;
			}
		});
		setOnAction(buttonClear, e -> paneForComponentDetails.clearTable());
		setOnAction(buttonDelete, e -> paneForComponentDetails.deleteItemsFromTable());
		setOnAction(buttonPopulate, e -> {
			if (qaPopulate()) {
				double[][] values = calculateValues();
				paneForComponentDetails.setValues(values);
			}
		});
		setPolicyAndMarketNames();
		VBox tabLayout = new VBox();
		tabLayout.getChildren().addAll(gridPanePresetModification);
		this.setContent(tabLayout);
	}

	/**
	 * Sets the widths for a ComboBox for consistency.
	 * 
	 * @param comboBox ComboBox to set widths for
	 */
	private void setComboBoxWidths(ComboBox<String> comboBox) {
		comboBox.setMaxWidth(MAX_WIDTH);
		comboBox.setMinWidth(MIN_WIDTH);
		comboBox.setPrefWidth(PREF_WIDTH);
	}
	/**
     * Sets the widths for a CheckComboBox for consistency.
     *
     * @param comboBox CheckComboBox to set widths for
     */
    private void setComboBoxWidths(CheckComboBox<String> comboBox) {
        comboBox.setMaxWidth(MAX_WIDTH);
        comboBox.setMinWidth(MIN_WIDTH);
        comboBox.setPrefWidth(PREF_WIDTH);
    }

	/**
	 * Automatically sets the policy and market names based on current selections
	 * and options. If auto-naming is enabled, updates the text fields accordingly.
	 */
	private void setPolicyAndMarketNames() {
	    Platform.runLater(() -> {
	        if (checkBoxUseAutoNames.isSelected()) {
	            String policy_type = "--";
	            String pollutant = "--";
	            String category = "--";
	            String state = "--";
	            try {
	                String s = comboBoxMeasure.getValue();
	                if (s != null && s.contains("Tax"))
	                    policy_type = "Tax";
	                if (s != null && s.contains("Cap"))
	                    policy_type = "Cap";
	                int cats = checkComboBoxCategory.getCheckModel().getCheckedItems().size();
	                if (cats == 0) {
	                    category = "All";
	                } else if (cats == 1) {
	                    category = checkComboBoxCategory.getCheckModel().getCheckedItems().get(0);
	                } else {
	                    category = "Mult";
	                }
	                s = comboBoxPollutant.getValue();
	                if (s != null && !s.equals("Select One")) {
	                    pollutant = utils.splitString(s, " ")[0];
	                }
	                String[] listOfSelectedRegions = utils.getAllSelectedRegions(paneForCountryStateTree.getTree());
	                if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
	                    listOfSelectedRegions = utils.removeUSADuplicate(listOfSelectedRegions);
	                    String state_str = utils.returnAppendedString(listOfSelectedRegions).replace(",", "");
	                    if (state_str.length() < 9) {
	                        state = state_str;
	                    } else {
	                        state = "Reg";
	                    }
	                }
	                String name = policy_type + "_" + category + "_" + pollutant + "_" + state;
	                textFieldMarketName.setText(name + "_Mkt");
	                textFieldPolicyName.setText(name);
	            } catch (Exception e) {
	                System.out.println("Error trying to auto-name market");
	            }
	        }
	    });
	}


	/**
	 * Runnable implementation: triggers saving the scenario component. Calls
	 * saveScenarioComponent().
	 */
	@Override
	public void run() {
        Platform.runLater(() -> saveScenarioComponent());
    }

	/**
	 * Saves the scenario component by generating metadata and CSV content. Uses
	 * selected regions, pollutant, sector, and cap/tax values.
	 */
	@Override
	public void saveScenarioComponent() {
		saveScenarioComponent(paneForCountryStateTree.getTree());
	}

	/**
	 * Saves the scenario component using the provided region tree. Validates
	 * inputs, generates metadata and CSV, and sets fileContent/filenameSuggestion.
	 *
	 * @param tree TreeView of selected regions
	 */
	private void saveScenarioComponent(TreeView<String> tree) {
	    if (!qaInputs()) {
	        Thread.currentThread().destroy();
	        return;
	    }

	    String[] listOfSelectedRegions = utils.getAllSelectedRegions(tree);
	    listOfSelectedRegions = utils.removeUSADuplicate(listOfSelectedRegions);

	    String ID = utils.getUniqueString();
	    String policy_name = textFieldPolicyName.getText() + ID;
	    String market_name = textFieldMarketName.getText() + ID;
	    filenameSuggestion = textFieldPolicyName.getText().replaceAll("/", "-").replaceAll(" ", "_") + ".csv";

	    String category = null;
	    List<String> cats = checkComboBoxCategory.getCheckModel().getCheckedItems();
	    if (cats.size() == 0) {
	        category = ALL;
	    } else if (cats.size() == 1) {
	        category = checkComboBoxCategory.getCheckModel().getCheckedItems().get(0);
	    }

	    String measure = comboBoxMeasure.getValue();
	    measure = measure.contains(CAP) ? CAP : TAX;

	    String pol_selection = comboBoxPollutant.getSelectionModel().getSelectedItem().trim() + " ";
	    String pol = pol_selection.substring(0, pol_selection.indexOf(" ")).trim();

	    fileContent = getMetaDataContent(tree, market_name, policy_name);

	    if (pol.startsWith(CO2) && ALL.equals(category) && CAP.equals(measure)) {
	        saveScenarioComponentRobustCO2Cap(listOfSelectedRegions, pol_selection, market_name, policy_name);
	        return;
	    } else if (!pol.startsWith(GHG)) {
	        saveScenarioComponentFlexTaxOrCap(listOfSelectedRegions, measure, category, cats, pol, market_name, policy_name);
	        return;
	    } else if (pol_selection.startsWith(GHG)) {
	        saveScenarioComponentGHGTaxOrCap(listOfSelectedRegions, measure, pol, market_name, policy_name);
	        return;
	    } else {
	        System.out.println("Cap or tax type not supported!");
	    }
	}
			
	/**
	 * Special implementation for CO2 cap policies, generating robust scenario files
	 * for complex scenarios.
	 *
	 * @param listOfSelectedRegions Array of selected region names
	 * @param measure               Measure type (Tax/Cap)
	 * @param category              Selected category
	 * @param categories            List of selected categories
	 * @param pol                   Pollutant string
	 * @param market_name           Market name
	 * @param policy_name           Policy name
	 */
	private void saveScenarioComponentFlexTaxOrCap(String[] listOfSelectedRegions, String measure, String category, List<String> categories, String pol,
			String market_name, String policy_name) {	
			
		        String tempDirName = vars.getGlimpseDir() + File.separator + "GLIMPSE-Data" + File.separator + "temp"; // vars.getGlimpseDir();
		        
		        File test = new File(tempDirName);
		        if (!test.exists())
		            test.mkdir();
		        String tempFilename0 = "temp_policy_file0.txt";
		        String tempFilename1 = "temp_policy_file1.txt";
		        String tempFilename2 = "temp_policy_file2.txt";
		
		        BufferedWriter bw0 = files.initializeBufferedFile(tempDirName, tempFilename0);
		        BufferedWriter bw1 = files.initializeBufferedFile(tempDirName, tempFilename1);
		        BufferedWriter bw2 = files.initializeBufferedFile(tempDirName, tempFilename2);
			
				ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
				
				//if (!"All".equals(category)) {
				//	pol = market_name;
				//}
				pol+="_Pol";
				
				files.writeToBufferedFile(bw0, fileContent);
		        fileContent = "use temp file";
		        
				//files.writeToBufferedFile(bw0,vars.getEol());
				files.writeToBufferedFile(bw0,INPUT_TABLE + vars.getEol());
				files.writeToBufferedFile(bw0,VARIABLE_ID + vars.getEol());
				if (CAP.equals(measure)) {
					files.writeToBufferedFile(bw0,GLIMPSE_EMISSION_CAP + vars.getEol() + vars.getEol());
					files.writeToBufferedFile(bw0,"region,pollutant,market,year,cap" + vars.getEol());
				} else if (TAX.equals(measure)) {
					files.writeToBufferedFile(bw0,GLIMPSE_EMISSION_TAX + vars.getEol() + vars.getEol());
					files.writeToBufferedFile(bw0,"region,pollutant,market,year,tax" + vars.getEol());
				}
				if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
					String state = listOfSelectedRegions[0];

					for (String data_str : data) {
						data_str = data_str.replaceAll(" ", "");
						files.writeToBufferedFile(bw0,state + "," + pol + "," + market_name + "," + data_str + vars.getEol());
					}
				}
				if (listOfSelectedRegions != null && listOfSelectedRegions.length > 1) {
					files.writeToBufferedFile(bw0,vars.getEol());
					files.writeToBufferedFile(bw0,INPUT_TABLE + vars.getEol());
					files.writeToBufferedFile(bw0,VARIABLE_ID + vars.getEol());
					files.writeToBufferedFile(bw0,GLIMPSE_EMISSION_MARKET + vars.getEol());
					files.writeToBufferedFile(bw0,vars.getEol());
					files.writeToBufferedFile(bw0,"region,pollutant,market" + vars.getEol());
					for (int s = 1; s < listOfSelectedRegions.length; s++) {
						files.writeToBufferedFile(bw0,listOfSelectedRegions[s] + "," + pol + "," + market_name + vars.getEol());
						double progress = (double) s / (listOfSelectedRegions.length - 1);
						updateProgressBar(progress);
					}
				}
				
				// for CO2, specific categories may be selected. This code builds the relevant markets
				
				int start_year = 2015;
				
				if (!ALL.equals(category)) {

					files.writeToBufferedFile(bw1,vars.getEol());
					files.writeToBufferedFile(bw1,INPUT_TABLE + vars.getEol());
					files.writeToBufferedFile(bw1,VARIABLE_ID + vars.getEol());
					files.writeToBufferedFile(bw1,GLIMPSE_ADD_CO2_SUBSPECIES_NEST + vars.getEol());
					files.writeToBufferedFile(bw1,vars.getEol());
					files.writeToBufferedFile(bw1,"region,supplysector,nesting-subsector,subsector,technology,year,pollutant"
							+ vars.getEol());
					
					int nest_count = 0;
					files.writeToBufferedFile(bw2,vars.getEol());
					files.writeToBufferedFile(bw2,INPUT_TABLE + vars.getEol());
					files.writeToBufferedFile(bw2,VARIABLE_ID + vars.getEol());
					files.writeToBufferedFile(bw2,GLIMPSE_ADD_CO2_SUBSPECIES + vars.getEol());
					files.writeToBufferedFile(bw2,vars.getEol());
					files.writeToBufferedFile(bw2,"region,supplysector,subsector,technology,year,pollutant" + vars.getEol());
					int nonest_count = 0;
					int max_year = 0;
					for (String d : data) {
						int year = Integer.parseInt(d.split(",")[0].trim());
						if (year > max_year)
							max_year = year;
					}
					//DHL: todo - need to loop around selected categories
					if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
						for (String region : listOfSelectedRegions) {
							String[][] tech_list = vars.getTechInfo();
							int cols = tech_list[0].length;
							int rows = tech_list.length;
							for (int y = start_year; y <= max_year; y += 5) {
							for (String cat : categories) {

								String cat_lwc = cat.toLowerCase();
								for (int r = 0; r < rows; r++) {
									String sector_r = tech_list[r][0];
									String subsector_r = tech_list[r][1];
									String tech_r = tech_list[r][2];
									String cat_r = tech_list[r][cols - 1];
									// DHL: todo - modify to operate on year list as opposed to 5-year increments

										// DHL: added PV and wind check to avoid issue where these didn't have inputs, resulting in severe error
										if ((!tech_r.contains("PV")) && (!tech_r.contains("wind")) && (!tech_r.contains("CSP"))) {
											String cat_r_lwc = cat_r.toLowerCase();
											if ((cat_lwc.equals(cat_r_lwc))) {
												String line = region + "," + sector_r + "," + subsector_r + ","
														+ tech_r.replace("=>", ",") + "," + y + "," + pol + vars.getEol();
												if (tech_r.contains("=>")) {
													files.writeToBufferedFile(bw1,line);
													nest_count++;
												} else {
													files.writeToBufferedFile(bw2,line);
													nonest_count++;
												}
											}
										}
									}
								}

							}
						}
						double progress = (double) 1 / (listOfSelectedRegions.length - 1);
						updateProgressBar(progress);
						
					}
		            files.closeBufferedFile(bw0);
		            files.closeBufferedFile(bw1);
		            files.closeBufferedFile(bw2);

		            String temp_file = tempDirName + File.separator + "temp_policy_file.txt";

		            files.deleteFile(tempDirName);

		            String temp_file0 = tempDirName + File.separator + tempFilename0;
		            String temp_file1 = tempDirName + File.separator + tempFilename1;
		            String temp_file2 = tempDirName + File.separator + tempFilename2;

		            ArrayList<String> tempfiles = new ArrayList<String>();
		            tempfiles.add(temp_file0);

		            if (nest_count > 0)
		                tempfiles.add(temp_file1);
		            if (nonest_count > 0)
		                tempfiles.add(temp_file2);

		            files.concatDestSources(temp_file, tempfiles);

		            System.out.println("Done");
				}
	} 
	

	/**
	 * Special implementation for GHG tax/cap policies, generating scenario files for GHG policies.
	 *
	 * @param listOfSelectedRegions Array of selected region names
	 * @param measure               Measure type (Tax/Cap)
	 * @param pol                   Pollutant string
	 * @param market_name           Market name
	 * @param policy_name           Policy name
	 */
	private void saveScenarioComponentGHGTaxOrCap(String[] listOfSelectedRegions, String measure, String pol,
			String market_name, String policy_name) {

		fileContent += INPUT_TABLE + vars.getEol();
		fileContent += VARIABLE_ID + vars.getEol();

		if (CAP.equals(measure)) {
			fileContent += GLIMPSE_GHG_EMISSION_CAP + vars.getEol();
			fileContent += vars.getEol();
			fileContent += "region,GHG-Policy,GHG-Market,year,cap" + vars.getEol();
		} else if (TAX.equals(measure)) {
			fileContent += GLIMPSE_GHG_EMISSION_TAX + vars.getEol();
			fileContent += vars.getEol();
			fileContent += "region,GHG-Policy,GHG-Market,year,tax" + vars.getEol();
		}
		
		if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
			String state = listOfSelectedRegions[0];
			ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
			for (String data_str : data) {
				data_str = data_str.replace(" ", "");
				fileContent += state + "," + policy_name + "," + market_name + "," + data_str + vars.getEol();
			}
		}
		if (listOfSelectedRegions != null && listOfSelectedRegions.length > 1) {
			fileContent += vars.getEol();
			fileContent += INPUT_TABLE + vars.getEol();
			fileContent += VARIABLE_ID + vars.getEol();
			fileContent += GLIMPSE_EMISSION_MARKET + vars.getEol();
			fileContent += vars.getEol();
			fileContent += "region,pollutant,market" + vars.getEol();
			for (int s = 1; s < listOfSelectedRegions.length; s++) {
				fileContent += listOfSelectedRegions[s] + "," + policy_name + "," + market_name + vars.getEol();
				double progress = (double) s / (listOfSelectedRegions.length - 1);
				updateProgressBar(progress);
			}
		}
		String fileContent2 = "";
		fileContent2 += vars.getEol();
		fileContent2 += INPUT_TABLE + vars.getEol();
		fileContent2 += VARIABLE_ID + vars.getEol();
		fileContent2 += GLIMPSE_LINKED_GHG_MARKET_P1 + vars.getEol();
		fileContent2 += vars.getEol();
		fileContent2 += "region,pollutant,GHG-market,GHG-Policy,price-adjust,demand-adjust,price-unit,output-unit"
				+ vars.getEol();
		String[] GHGs = { "CO2", "CH4", "N2O", "C2F6", "CF4", "HFC125", "HFC134a", "HRC245fa", "SF6", "CH4_AWB",
				"CH4_AGR", "N2O_AWB", "N2O_AGR" };
		String[] price_adjust = { "1", "5.728", "84.55", "0", "0", "0", "0", "0", "0", "5.727", "5.727",
				"84.55", "84.55" };
		String[] demand_adjust = { "3.667", "21", "310", "9.2", "6.5", "2.8", "1.3", "1.03", "23.9", "21", "21",
				"310", "310" };
		String[] price_unit = { "1990$/tC", "1990$/GgCH4", "1990$/GgN2O", "1990$/MgC2F6", "1990$/MgCF4",
				"1990$/MgHFC125", "1990$/MgHFC13a", "1990$/MgHFC245fa", "1990$/MgSF6", "1990$/GgCH4",
				"1990$/GgCH4", "1990$/GgN2O", "1990$/GgN2O" };
		String[] output_unit = { "MtC", "TgCH4", "TgN2O", "GgC2F6", "GgCF4", "GgHFC125", "GgHFC134a",
				"GgHFC245fa", "GgSF6", "TgCH4", "TgCH4", "TgN2O", "TgN2O" };
		if (listOfSelectedRegions != null) {
			for (String state : listOfSelectedRegions) {
				for (int i = 0; i < GHGs.length; i++) {
					if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
						fileContent2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name + ","
								+ price_adjust[i] + "," + demand_adjust[i] + "," + price_unit[i] + ","
								+ output_unit[i] + vars.getEol();
					}
				}
			}
		}
		if (listOfSelectedRegions != null && listOfSelectedRegions.length > 1) {
			fileContent2 += vars.getEol();
			fileContent2 += INPUT_TABLE + vars.getEol();
			fileContent2 += VARIABLE_ID + vars.getEol();
			fileContent2 += GLIMPSE_LINKED_GHG_MARKET_P2 + vars.getEol();
			fileContent2 += vars.getEol();
			fileContent2 += "region,pollutant,GHG-market,GHG-Policy" + vars.getEol();
			for (int s = 1; s < listOfSelectedRegions.length; s++) {
				for (int i = 0; i < GHGs.length; i++) {
					if ((pol.equals("GHG")) || ((pol.equals("CO2")) && (GHGs[i].equals("CO2")))) {
						String state = listOfSelectedRegions[s];
						fileContent2 += state + "," + GHGs[i] + "," + market_name + "," + policy_name
								+ vars.getEol();
					}
				}
				double progress = (double) s / (listOfSelectedRegions.length - 1);
				updateProgressBar(progress);
			}
		}
	
		if (fileContent2.length() > 0)
			fileContent += fileContent2;
	}
	
	
	/**
	 * Special implementation for robust CO2 cap policies, generating scenario files for complex CO2 cap scenarios.
	 *
	 * @param listOfSelectedRegions Array of selected region names
	 * @param pol                   Pollutant string
	 * @param market_name           Market name
	 * @param policy_name           Policy name
	 */
	private void saveScenarioComponentRobustCO2Cap(String[] listOfSelectedRegions, String pol,
			String market_name, String policy_name) {

		fileContent += INPUT_TABLE + vars.getEol();
		fileContent += VARIABLE_ID + vars.getEol();
		fileContent += GLIMPSE_EMISSION_CAP_PPS_P1 + vars.getEol() + vars.getEol();
		fileContent += "region,policy,policy-type,min-price,market,year,cap" + vars.getEol();
		if (listOfSelectedRegions != null && listOfSelectedRegions.length > 0) {
			for (String state : listOfSelectedRegions) {
				ArrayList<String> data = paneForComponentDetails.getDataYrValsArrayList();
				for (String data_str : data) {
					data_str = data_str.replaceAll(" ", "");
					fileContent += state + "," + policy_name + ",tax,1," + market_name + "," + data_str + vars.getEol();
				}
			}
		}
		String dmdAdj = "1";
		if (pol.contains("(MT CO2)"))
			dmdAdj = "3.667";
		pol = pol.substring(0, pol.indexOf(" ")).trim();
		if (listOfSelectedRegions != null && listOfSelectedRegions.length >= 1) {
			fileContent += vars.getEol();
			fileContent += INPUT_TABLE + vars.getEol();
			fileContent += VARIABLE_ID + vars.getEol();
			fileContent += GLIMPSE_EMISSION_CAP_PPS_P2 + vars.getEol();
			fileContent += vars.getEol();
			fileContent += "region,linked-ghg-policy,price-adjust0,demand-adjust0,market,linked-policy,price-unit,output-unit,price-adjust1,demandAdjust1"
					+ vars.getEol();
			for (String region : listOfSelectedRegions) {
				fileContent += region + "," + pol + ",0,0," + market_name + "," + policy_name + ",1990$/Tg,Tg,1,"
						+ dmdAdj + vars.getEol();
				double progress = (double) 1 / (listOfSelectedRegions.length - 1);
				updateProgressBar(progress);
			}
		}
	}

	/**
	 * Returns metadata content for the scenario component file, including measure,
	 * pollutant, sector, regions, and table data.
	 *
	 * @param tree   TreeView of selected regions
	 * @param market Market name
	 * @param policy Policy name
	 * @return Metadata string for file header
	 */
	public String getMetaDataContent(TreeView<String> tree, String market, String policy) {
		StringBuilder rtn_str = new StringBuilder();
		rtn_str.append("########## Scenario Component Metadata ##########").append(vars.getEol());
		rtn_str.append("#Scenario component type: Pollutant Tax/Cap").append(vars.getEol());
		rtn_str.append("#Measure: ").append(comboBoxMeasure.getValue()).append(vars.getEol());
		rtn_str.append("#Pollutant: ").append(comboBoxPollutant.getValue()).append(vars.getEol());
		rtn_str.append("#Categories: ").append(utils.getStringFromList(checkComboBoxCategory.getCheckModel().getCheckedItems(),",")).append(vars.getEol());
		if (policy == null)
			market = textFieldPolicyName.getText();
		rtn_str.append("#Policy name: ").append(policy).append(vars.getEol());
		if (market == null)
			market = textFieldMarketName.getText();
		rtn_str.append("#Market name: ").append(market).append(vars.getEol());
		String[] listOfSelectedRegions = utils.getAllSelectedRegions(tree);
		listOfSelectedRegions = utils.removeUSADuplicate(listOfSelectedRegions);
		String states = utils.returnAppendedString(listOfSelectedRegions);
		rtn_str.append("#Regions: ").append(states).append(vars.getEol());
		ArrayList<String> table_content = paneForComponentDetails.getDataYrValsArrayList();
		for (String row : table_content) {
			rtn_str.append("#Table data:").append(row).append(vars.getEol());
		}
		rtn_str.append("#################################################").append(vars.getEol());
		return rtn_str.toString();
	}

	/**
	 * Loads content into the tab from a list of strings (e.g., when editing a
	 * component). Populates measure, pollutant, sector, regions, and table data
	 * from file content.
	 *
	 * @param content List of file lines to load
	 */
	@Override
	public void loadContent(ArrayList<String> content) {
		for (String line : content) {
			int pos = line.indexOf(":");
			if (line.startsWith("#") && (pos > -1)) {
				String param = line.substring(1, pos).trim().toLowerCase();
				String value = line.substring(pos + 1).trim();
				switch (param) {
				case "measure":
					comboBoxMeasure.setValue(value);
					comboBoxMeasure.fireEvent(new ActionEvent());
					break;
				case "pollutant":
					comboBoxPollutant.setValue(value);
					comboBoxPollutant.fireEvent(new ActionEvent());
					break;
				case "categories":
					checkComboBoxCategory.getCheckModel().clearChecks();
					String[] items=utils.splitString(value, ",");
					for (String item : items) {
						if (item.equals(ALL)) {
							checkComboBoxCategory.getCheckModel().check(ALL);
						} else {
							checkComboBoxCategory.getCheckModel().check(item);
						}
					}
					checkComboBoxCategory.fireEvent(new ActionEvent());
					break;
				case "policy name":
					textFieldPolicyName.setText(value);
					textFieldPolicyName.fireEvent(new ActionEvent());
					break;
				case "market name":
					textFieldMarketName.setText(value);
					textFieldMarketName.fireEvent(new ActionEvent());
					break;
				case "regions":
					String[] regions = utils.splitString(value, ",");
					paneForCountryStateTree.selectNodes(regions);
					break;
				case "table data":
					String[] s = utils.splitString(value, ",");
					paneForComponentDetails.data.add(new DataPoint(s[0], s[1]));
					break;
				}
			}
		}
		this.setPolicyAndMarketNames();
		paneForComponentDetails.updateTable();
	}

	/**
	 * Checks if all required fields for populating values are filled.
	 *
	 * @return true if all required fields are filled, false otherwise
	 */
	public boolean qaPopulate() {
		return !textFieldStartYear.getText().isEmpty() && !textFieldEndYear.getText().isEmpty()
				&& !textFieldInitialAmount.getText().isEmpty() && !textFieldGrowth.getText().isEmpty();
	}

    /**
     * Helper method to validate table data years against allowable policy years.
     * @return true if at least one year matches allowable years, false otherwise
     */
    private boolean validateTableDataYears() {
        List<Integer> listOfAllowableYears = vars.getAllowablePolicyYears();
        ObservableList<DataPoint> data = paneForComponentDetails != null ? this.paneForComponentDetails.table.getItems() : null;
        if (data == null) return false;
        for (DataPoint dp : data) {
            Integer year = Integer.parseInt(dp.getYear().trim());
            if (listOfAllowableYears.contains(year)) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * Validates all required inputs before saving the scenario component. Checks
	 * for at least one region, at least one table entry, and required selections.
	 *
	 * @return true if all inputs are valid, false otherwise
	 */
	protected boolean qaInputs() {
		TreeView<String> tree = paneForCountryStateTree.getTree();
		int error_count = 0;
		StringBuilder message = new StringBuilder();
		try {
			
			if (utils.getAllSelectedRegions(tree).length < 1) {
				message.append("Must select at least one region from tree").append(vars.getEol());
				error_count++;
			}
            if (paneForComponentDetails == null || paneForComponentDetails.table.getItems().size() == 0) {
                message.append("Data table must have at least one entry").append(vars.getEol());
                error_count++;
            } else {
                boolean match = validateTableDataYears();
                if (!match) {
                    message.append("Years specified in table must match allowable policy years (").append(vars.getAllowablePolicyYears()).append(")").append(vars.getEol());
                    error_count++;
                }
            }
			if (comboBoxMeasure.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
				message.append("Action comboBox must have a selection").append(vars.getEol());
				error_count++;
			}
			if (checkComboBoxCategory.getCheckModel().getCheckedItems().size() == 0) {
				message.append("Sector comboBox must have a selection").append(vars.getEol());
				error_count++;
			}
			if (comboBoxPollutant.getSelectionModel().getSelectedItem().equals(SELECT_ONE)) {
				message.append("Parameter comboBox must have a selection").append(vars.getEol());
				error_count++;
			}
			if (textFieldPolicyName.getText().isEmpty()) {
				message.append("A market name must be provided").append(vars.getEol());
				error_count++;
			}
		} catch (Exception e1) {
			error_count++;
			message.append("Error in QA of entries").append(vars.getEol());
		}
		if (error_count > 0) {
			if (error_count == 1) {
				utils.warningMessage(message.toString());
			} else if (error_count > 1) {
				utils.displayString(message.toString(), "Parsing Errors");
			}
		}
		return error_count == 0;
	}

	/**
	 * Updates the progress bar in a thread-safe way.
	 *
	 * @param progress Progress value between 0.0 and 1.0
	 */
	private void updateProgressBar(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }
}
