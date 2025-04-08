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
package ModelInterface;

public class UnitConversionInstance {
	private String toUnit;
	private String fromUnit;
	private Double conversionFactor;
	private String query;
	private String headingOne;
	private String valueOne;
	private String headingTwo;
	private String valueTwo;
	private String headingThree;
	private String valueThree;
	private String headingFour;
	private String valueFour;
	
	public UnitConversionInstance(String fromUnit, String toUnit, Double conversionFactor, String query, String headingOne,
			String valueOne, String headingTwo, String valueTwo, String headingThree, String valueThree,
			String headingFour, String valueFour) {
		super();
		this.toUnit = toUnit;
		this.fromUnit=fromUnit;
		this.conversionFactor = conversionFactor;
		this.query = query;
		this.headingOne = headingOne;
		this.valueOne = valueOne;
		this.headingTwo = headingTwo;
		this.valueTwo = valueTwo;
		this.headingThree = headingThree;
		this.valueThree = valueThree;
		this.headingFour = headingFour;
		this.valueFour = valueFour;
	}

	public String getToUnit() {
		return toUnit;
	}
	
	public String getFromUnit() {
		return fromUnit;
	}

	public Double getConversionFactor() {
		return conversionFactor;
	}

	public String getQuery() {
		return query;
	}

	public String getHeadingOne() {
		return headingOne;
	}

	public String getValueOne() {
		return valueOne;
	}

	public String getHeadingTwo() {
		return headingTwo;
	}

	public String getValueTwo() {
		return valueTwo;
	}

	public String getHeadingThree() {
		return headingThree;
	}

	public String getValueThree() {
		return valueThree;
	}

	public String getHeadingFour() {
		return headingFour;
	}

	public String getValueFour() {
		return valueFour;
	}
	
	
	

}
