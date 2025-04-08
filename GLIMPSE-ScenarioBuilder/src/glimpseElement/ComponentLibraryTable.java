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
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
* Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package glimpseElement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ComponentLibraryTable {

	public static TableView<ComponentRow> tableComponents;
	public static TableView<ComponentRow> tableCreateScenario;
	public static TextField filterComponentsTextField;
	
	public static ObservableList<ComponentRow> listOfFiles = FXCollections.observableArrayList();
	public static ObservableList<ComponentRow> listOfFilesCreateScenario = FXCollections.observableArrayList();

	public static void addToListOfFiles(ComponentRow[] fileArray) {
		boolean match = false;
		for (ComponentRow j : listOfFiles) {
			for (ComponentRow i : fileArray) {
				//TODO:  String comparison with .equals
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

	public static void createListOfFiles(ComponentRow[] fileArray) {
		listOfFiles.clear();
		for (ComponentRow i : fileArray) {
			listOfFiles.add(i);
		}
	}

	public static void addToListOfFilesCreatePolicyScenario(ObservableList<ComponentRow> fileArray) {
		for (ComponentRow i : fileArray) {
			if (!listOfFilesCreateScenario.contains(i)) {
				listOfFilesCreateScenario.add(i);
			}
		}
	}

	public static void createListOfFilesCreatePolicyScenario(ComponentRow[] fileArray) {
		listOfFilesCreateScenario.clear();

		if (fileArray!=null) {
			for (ComponentRow i : fileArray) {
				listOfFilesCreateScenario.add(i);
			}
		}
	}

	public static void removeFromListOfFilesCreatePolicyScenario(ObservableList<ComponentRow> fileArray) {
		ObservableList<ComponentRow> copy = FXCollections.observableArrayList();
		for (ComponentRow i : fileArray) {
			copy.add(i);
		}
		for (ComponentRow i : copy) {
			listOfFilesCreateScenario.remove(i);
		}
	}

	public static void removeFromListOfFiles(ObservableList<ComponentRow> fileArray) {
		ObservableList<ComponentRow> copy = FXCollections.observableArrayList();
		for (ComponentRow i : fileArray) {
			copy.add(i);
		}
		for (ComponentRow i : copy) {
			listOfFiles.remove(i);
		}
	}

	/* Returns File Id TableColumn */
	public static TableColumn<ComponentRow, Integer> getIdColumn() {
		TableColumn<ComponentRow, Integer> FileIdCol = new TableColumn<>("Id");
		FileIdCol.setCellValueFactory(new PropertyValueFactory<>("FileId"));
		return FileIdCol;
	}

	/* Returns First Name TableColumn */
	public static TableColumn<ComponentRow, String> getFileNameColumn() {
		TableColumn<ComponentRow, String> fNameCol = new TableColumn<>("Component Name");
		fNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		return fNameCol;
	}

	/* Returns Last Name TableColumn */
	public static TableColumn<ComponentRow, String> getAddressColumn() {
		TableColumn<ComponentRow, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
		return addressCol;
	}

	/* Returns Birth Date TableColumn */
	public static TableColumn<ComponentRow, Date> getBirthDateColumn() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd: HH:mm", Locale.ENGLISH);
		TableColumn<ComponentRow, Date> bDateCol = new TableColumn<>("Created");
		bDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		bDateCol.setStyle("-fx-alignment: CENTER;");
		bDateCol.setCellFactory(column -> {
        return new TableCell<ComponentRow, Date>() {
            @Override
            protected void updateItem(Date date, boolean dateIsEmpty) {
                super.updateItem(date, dateIsEmpty);
                if (date == null || dateIsEmpty) {
                    setText(null);
                } else {
                    setText(format.format(date));
                }
            }
        };
    }); 
		return bDateCol;
	}
	

}
