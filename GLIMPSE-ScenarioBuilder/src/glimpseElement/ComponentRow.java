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

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

public class ComponentRow {

	private final ReadOnlyIntegerWrapper fileId =
			 new ReadOnlyIntegerWrapper(this, "fileId", fileSequence.incrementAndGet());
	private final StringProperty fileName =
			 new SimpleStringProperty(this, "fileName", null);
	private final StringProperty address =
			 new SimpleStringProperty(this, "address", null);
	private final ObjectProperty<Date> birthDate =
			 new SimpleObjectProperty<>(this, "birthDate", null);

	// Keeps track of last generated file id
	private static AtomicInteger fileSequence = new AtomicInteger(0);

	public ComponentRow() {
		this(null, null, null);
	}

	public ComponentRow(String fileName, String address, Date birthDate) {
		this.fileName.set(fileName);
		this.address.set(address);
		this.birthDate.set(birthDate);
	}

	/* personId Property */
	public final int getFileId() {
		return fileId.get();
	}

	public final ReadOnlyIntegerProperty fileIdProperty() {
		return fileId.getReadOnlyProperty();
	}

	/* firstName Property */
	public final String getFileName() {
		return fileName.get();
	}

	public final void setFileName(String fileName) {
		fileNameProperty().set(fileName);
	}

	public final StringProperty fileNameProperty() {
		return fileName;
	}


	/* lastName Property */
	public final String getAddress() {
		return address.get();
	}

	public final void setAddress(String address) {
		addressProperty().set(address);
	}

	public final StringProperty addressProperty() {
		return address;
	}

	/* birthDate Property */
	public final Date getBirthDate() {
		return birthDate.get();
	}

	public final void setBirthDate(Date birthDate) {
		birthDateProperty().set(birthDate);
	}

	public final ObjectProperty<Date> birthDateProperty() {
		return birthDate;
	}

	/* Domain specific business rules */
	public boolean isValidBirthDate(Date bdate) {
		return isValidBirthDate(bdate, new ArrayList<>());
	}
	
	/* Domain specific business rules */
	public boolean isValidBirthDate(Date bdate, List<String> errorList) {
		if (bdate == null) {
			return true;
		}

		// Birth date cannot be in the future
		Date d = new Date();
		if (bdate.getTime() > d.getTime()) {
			errorList.add("Birth date must not be in future.");
			return false;
		}

		return true;
	}

	/* Domain specific business rules */
	public boolean isValidFile(List<String> errorList) {
		return isValidFile(this, errorList);
	}

	/* Domain specific business rules */
	public boolean isValidFile(ComponentRow p, List<String> errorList) {
		boolean isValid = true;

		String fn = p.fileName.get();
		if (fn == null || fn.trim().length() == 0) {
			errorList.add("File name must contain minimum one character.");
			isValid = false;
		}

		String ln = p.address.get();
		if (ln == null || ln.trim().length() == 0) {
			errorList.add("Address must contain minimum one character.");
			isValid = false;
		}

		if (!isValidBirthDate(this.birthDate.get(), errorList)) {
			isValid = false;
		}

		return isValid;
	}

	/* Domain specific business rules */
	

	/* Domain specific business rules */
	public boolean save(List<String> errorList) {
		boolean isSaved = false;
		if (isValidFile(errorList)) {
			System.out.println("Saved " + this.toString());
			isSaved = true;
		}
		
		return isSaved;
	}

	@Override
	public String toString() {
		return "[fileId=" + fileId.get() + 
		       ", fileName=" + fileName.get() + 
		       ", address=" + address.get() + 
		       ", birthDate=" + birthDate.get() + "]";
	}
}

