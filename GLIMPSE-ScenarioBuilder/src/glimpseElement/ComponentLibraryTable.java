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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * A utility class to manage TableView components and their associated data
 * for the component library and scenario creation panes.
 */
public final class ComponentLibraryTable {

    // --- Column Header Constants ---
    private static final String ID_COLUMN_HEADER = "Id";
    private static final String FILENAME_COLUMN_HEADER = "Component Name";
    private static final String ADDRESS_COLUMN_HEADER = "Address";
    private static final String CREATED_DATE_COLUMN_HEADER = "Created";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);

    // --- Private Static Fields ---
    public static TableView<ComponentRow> tableComponents;
    public static TableView<ComponentRow> tableCreateScenario;
    private static TextField filterComponentsTextField;

    private static ObservableList<ComponentRow> listOfFiles = FXCollections.observableArrayList();
    private static ObservableList<ComponentRow> listOfFilesCreateScenario = FXCollections.observableArrayList();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ComponentLibraryTable() {
        throw new IllegalStateException("Utility class");
    }

    // --- Public Getters for UI Components and Data Lists ---

    public static TableView<ComponentRow> getTableComponents() { return tableComponents; }
    public static TableView<ComponentRow> getTableCreateScenario() { return tableCreateScenario; }
    public static TextField getFilterComponentsTextField() { return filterComponentsTextField; }
    public static ObservableList<ComponentRow> getListOfFiles() { return listOfFiles; }
    public static ObservableList<ComponentRow> getListOfFilesCreateScenario() { return listOfFilesCreateScenario; }
    
    // Setter methods to initialize the UI components from outside
    public static void setTableComponents(TableView<ComponentRow> table) { tableComponents = table; }
    public static void setTableCreateScenario(TableView<ComponentRow> table) { tableCreateScenario = table; }
    public static void setFilterComponentsTextField(TextField textField) { filterComponentsTextField = textField; }


    // --- Data Manipulation Methods for Component Library List ---
    // Note: Co-pilot replaced this method with the one below it, but I added it back to address inconsistency between this and PaneNewScenarioComponent
	public static void addToListOfFiles(ComponentRow[] fileArray) {
		boolean match = false;
		for (ComponentRow j : listOfFiles) {
			for (ComponentRow i : fileArray) {
				if (j.getFileName().equals(i.getFileName())) {
					match = true;
					j.setAddress(i.getAddress());
					j.setBirthDate(i.getBirthDate());
				}
			}
		}

		if (!match) {
			for (ComponentRow i : fileArray) {
				listOfFiles.add(i);
			}
		}
	}
    
    public static void addOrUpdateFiles(ComponentRow... fileArray) {
        if (fileArray == null || fileArray.length == 0) return;

        Map<String, ComponentRow> fileMap = listOfFiles.stream()
            .collect(Collectors.toMap(ComponentRow::getFileName, Function.identity()));

        for (ComponentRow newFile : fileArray) {
            fileMap.put(newFile.getFileName(), newFile); // Adds new or overwrites existing
        }

        listOfFiles.setAll(fileMap.values());
    }

    public static void createListOfFiles(ComponentRow... fileArray) {
        listOfFiles.clear();
        if (fileArray != null) {
            listOfFiles.setAll(fileArray);
        }
    }

    public static void removeFromListOfFiles(ObservableList<ComponentRow> filesToRemove) {
        listOfFiles.removeAll(filesToRemove);
    }

    // --- Data Manipulation Methods for Create Scenario List ---

    public static void addToListOfFilesCreatePolicyScenario(ObservableList<ComponentRow> filesToAdd) {
        filesToAdd.forEach(file -> {
            if (!listOfFilesCreateScenario.contains(file)) {
                listOfFilesCreateScenario.add(file);
            }
        });
    }

    public static void createListOfFilesCreatePolicyScenario(ComponentRow... fileArray) {
        listOfFilesCreateScenario.clear();
        if (fileArray != null) {
            listOfFilesCreateScenario.setAll(Arrays.asList(fileArray));
        }
    }

    public static void removeFromListOfFilesCreatePolicyScenario(ObservableList<ComponentRow> filesToRemove) {
        listOfFilesCreateScenario.removeAll(filesToRemove);
    }

    // --- TableColumn Factory Methods ---

    public static TableColumn<ComponentRow, Integer> getIdColumn() {
        TableColumn<ComponentRow, Integer> idCol = new TableColumn<>(ID_COLUMN_HEADER);
        idCol.setCellValueFactory(new PropertyValueFactory<>("FileId"));
        return idCol;
    }

    public static TableColumn<ComponentRow, String> getFileNameColumn() {
        TableColumn<ComponentRow, String> fileNameCol = new TableColumn<>(FILENAME_COLUMN_HEADER);
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        return fileNameCol;
    }

    public static TableColumn<ComponentRow, String> getAddressColumn() {
        TableColumn<ComponentRow, String> addressCol = new TableColumn<>(ADDRESS_COLUMN_HEADER);
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        return addressCol;
    }

    public static TableColumn<ComponentRow, Date> getBirthDateColumn() {
        TableColumn<ComponentRow, Date> bDateCol = new TableColumn<>(CREATED_DATE_COLUMN_HEADER);
        bDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        bDateCol.setStyle("-fx-alignment: CENTER;");

        bDateCol.setCellFactory(col -> new TableCell<ComponentRow, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                setText((date == null || empty) ? null : DATE_FORMAT.format(date));
            }
        });
        return bDateCol;
    }
}
