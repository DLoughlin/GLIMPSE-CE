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

import java.text.DecimalFormat;

import glimpseUtil.GLIMPSEUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataPoint {
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	DecimalFormat nf = new DecimalFormat("#.00");
	int signficantFigures = 3;

	private StringProperty year;
	private StringProperty value;

	public DataPoint(String yr, String val) {
		setYear(yr);
		setValue(val);
	}

	public DataPoint(int yr, double val) {
		String str_yr = "" + yr;
		setYear(str_yr);
		// setValue(nf.format(val));
		setValue(utils.toSignificantFiguresString(val, signficantFigures));
	}

	public void setYear(String yr) {
		yearProperty().set(yr);
	}

	public String getYear() {
		return yearProperty().get();
	}

	public StringProperty yearProperty() {
		if (year == null)
			year = new SimpleStringProperty(this, "year");
		return year;
	}

	public void setValue(String val) {
		valueProperty().set(val);
	}

	public String getValue() {
		return valueProperty().get();
	}

	public StringProperty valueProperty() {
		if (value == null)
			value = new SimpleStringProperty(this, "value");
		return value;
	}

	public boolean qaDataPoint(boolean isCheckYear) {
		boolean ok = true;
		if (isCheckYear) {
			try {
				int year = Integer.parseInt(getYear());
				// double value = Double.parseDouble(getValue());

				if ((year < 2015) || (year > 2100))
					ok = false;

			} catch (Exception ee) {
				ok = false;
			}
		}
		if (!ok)
			utils.warningMessage("Entry must be for year from 2015 to 2100.");
		return ok;
	}

}
