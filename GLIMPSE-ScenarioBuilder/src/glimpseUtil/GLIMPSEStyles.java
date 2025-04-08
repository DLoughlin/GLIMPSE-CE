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

public class GLIMPSEStyles {
	public static final GLIMPSEStyles instance = new GLIMPSEStyles();

	// initiates the singleton that holds program parameters
	// GLIMPSEVariables vars = GLIMPSEVariables.getInstance();

	// Specifies style for GUI tables, such as border width and color

	public int font_size = 12;
	public String font_style = " -fx-font-size: 12";
	public String style1 = "-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
			+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;" + font_style;
	public String style1b = "-fx-padding: 3;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
			+ "-fx-border-insets: 3;" + "-fx-border-radius: 5;" + "-fx-border-color: red;" + font_style;
	public String style2 = "-fx-padding: 10;" + font_style;
	public String style3 = "-fx-padding: 5;" + font_style;
	public String style4 = "-fx-padding: 2;" + font_style;
	public String style5 = "-fx-alignment: CENTER-RIGHT; -fx-padding: 5 20 5 5;" + font_style;
	public int bigButtonWid = 70;
	public int smallButtonWid = 35;

	public GLIMPSEStyles() {
	}

	public static GLIMPSEStyles getInstance() {
		return instance;
	}

	public void setFontSize(int size) {
		font_size = size;
		font_style = " -fx-font-size: " + size;
	}

	public int getFontSize() {
		return font_size;
	}

	public String getFontStyle() {
		return font_style;
	}
}
