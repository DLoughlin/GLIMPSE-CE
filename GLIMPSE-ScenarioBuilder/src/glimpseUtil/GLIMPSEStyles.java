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
 * and that User is not otherwise prohibited
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
package glimpseUtil;

/**
 * A singleton class that holds style information and layout constants for the application's GUI.
 * This version has been refactored to ensure styles are generated dynamically and to improve encapsulation.
 */
public final class GLIMPSEStyles {
    private static final GLIMPSEStyles INSTANCE = new GLIMPSEStyles();

    private final int bigButtonWidth = 65;
    private final int smallButtonWidth = 35;

    private int fontSize = 12;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private GLIMPSEStyles() {
    }

    /**
     * Returns the singleton instance of the GLIMPSEStyles class.
     *
     * @return The single instance of this class.
     */
    public static GLIMPSEStyles getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the current font size used in styles.
     * @return the font size in points.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size and ensures all styles will reflect the change.
     * @param size The new font size in points.
     */
    public void setFontSize(int size) {
        this.fontSize = size;
    }

    /**
     * Returns a string for setting the font size in JavaFX CSS.
     */
   public String getFontStyle() {
        return String.format("-fx-font-size: %dpx;", this.fontSize);
    }

    // --- Style Getters ---

    public String getStyle1() {
        return String.format("-fx-padding: 10; -fx-border-style: solid inside; -fx-border-width: 2; " +
            "-fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: blue; %s", getFontStyle());
    }

    public String getStyle1b() {
        return String.format("-fx-padding: 3; -fx-border-style: solid inside; -fx-border-width: 2; " +
            "-fx-border-insets: 3; -fx-border-radius: 5; -fx-border-color: red; %s", getFontStyle());
    }

    public String getStyle2() {
        return String.format("-fx-padding: 10; %s", getFontStyle());
    }

    public String getStyle3() {
        return String.format("-fx-padding: 5; %s", getFontStyle());
    }

    public String getStyle4() {
        return String.format("-fx-padding: 2; %s", getFontStyle());
    }

    public String getStyle5() {
        return String.format("-fx-alignment: CENTER-RIGHT; -fx-padding: 5 20 5 5; %s", getFontStyle());
    }

    // --- Layout Constant Getters ---

    public int getBigButtonWidth() {
        return bigButtonWidth;
    }

    public int getSmallButtonWidth() {
        return smallButtonWidth;
    }
}
