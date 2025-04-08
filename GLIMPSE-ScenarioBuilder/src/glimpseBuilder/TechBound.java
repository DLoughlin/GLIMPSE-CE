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
package glimpseBuilder;

public class TechBound {

	private String firstYear;
	private String lastYear;
	private String techName;
	private boolean isBoundAll=false;
	private boolean isBoundRange=false;

	public TechBound(String lastYearA, String techNameB) {
		this.firstYear = lastYearA;
		this.lastYear = lastYearA;
		this.techName = techNameB;
		this.isBoundAll = false;
		this.isBoundRange = false;
	}
	
	public TechBound(String lastYearA, String techNameB, boolean activeC, boolean activeD) {
		this.firstYear = lastYearA;
		this.lastYear = lastYearA;
		this.techName = techNameB;
		this.isBoundAll = activeC;
		this.isBoundRange = activeD;
	}

	public TechBound(String firstYearA, String lastYearA, String techNameB, boolean activeC, boolean activeD) {
		this.firstYear = firstYearA;
		this.lastYear = lastYearA;
		this.techName = techNameB;
		this.isBoundAll = activeC;
	}

	public String getFirstYear() {
		String rtn_val=firstYear;
		return rtn_val;
	}

	public void setFirstYear(String yearA) {
		this.firstYear = yearA;
	}

	public String getLastYear() {
		String rtn_val=lastYear;
		return rtn_val;
	}

	public void setLastYear(String yearA) {
		this.lastYear = yearA;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techNameA) {
		this.techName = techNameA;
	}

	public boolean isBoundAll() {
		return isBoundAll;
	}

	public void setBoundAll(boolean activeA) {
		this.isBoundAll = activeA;
	}
	
	public boolean isBoundRange() {
		return isBoundRange;
	}

	public void setBoundRange(boolean activeA) {
		this.isBoundRange = activeA;
	}
	
	public boolean isBound() {
		boolean isBound=false;
		if (this.isBoundRange||this.isBoundAll) isBound=true;
		return isBound;
	}

	
}