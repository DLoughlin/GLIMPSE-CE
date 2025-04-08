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
package glimpseUtil;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileChooserPlus {
	public static java.io.File main(String filter1, String filter2, String startDir, String which) {

		System.out.println("filter:" + filter1 + " " + filter2);
		System.out.println("dir:" + startDir);

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(filter1, filter2);

		fileChooser.getExtensionFilters().add(extFilter);

		File startDirTest = new File(startDir);
		if (startDirTest.isDirectory()) {
			fileChooser.setInitialDirectory(startDirTest);
		}

		File returnFile = null;
		if (which.equals("Open")) {
			returnFile = fileChooser.showOpenDialog(new Stage());
		} else if (which.equals("Save")) {
			returnFile = fileChooser.showSaveDialog(new Stage());
		}

		return returnFile;

	}

	public static java.io.File main(String filter1, String filter2, String startDir, String startFile, String which) {
		System.out.println("filter:" + filter1 + " " + filter2);
		System.out.println("dir:" + startDir);

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(filter1, filter2);

		fileChooser.getExtensionFilters().add(extFilter);

		File startDirTest = new File(startDir);
		if (startDirTest.isDirectory()) {
			fileChooser.setInitialDirectory(startDirTest);
		}

		fileChooser.setInitialFileName(startFile);

		File returnFile = null;
		if (which.equals("Open")) {
			returnFile = fileChooser.showOpenDialog(new Stage());
		} else if (which.equals("Save")) {
			returnFile = fileChooser.showSaveDialog(new Stage());
		}

		return returnFile;
	}

}
