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
package glimpseBuilder;

import java.util.Objects;

/**
 * An immutable data object representing a technology's "bound" status for a given year or range of years.
 */
public final class TechBound {

    private String firstYear;
    private String lastYear;
    private String techName;
    private boolean isBoundAll;
    private boolean isBoundRange;

    /**
     * Private constructor. Use static factory methods to create instances.
     */
    public TechBound(String firstYear, String lastYear, String techName, boolean isBoundAll, boolean isBoundRange) {
        this.firstYear = Objects.requireNonNull(firstYear, "First year cannot be null.");
        this.lastYear = Objects.requireNonNull(lastYear, "Last year cannot be null.");
        this.techName = Objects.requireNonNull(techName, "Tech name cannot be null.");
        this.isBoundAll = isBoundAll;
        this.isBoundRange = isBoundRange;
    }

    /**
     * Creates a TechBound instance for a single year.
     */
    public static TechBound createForSingleYear(String year, String techName, boolean isBoundAll, boolean isBoundRange) {
        return new TechBound(year, year, techName, isBoundAll, isBoundRange);
    }

    /**
     * Creates a TechBound instance for a range of years.
     */
    public static TechBound createForYearRange(String firstYear, String lastYear, String techName, boolean isBoundAll, boolean isBoundRange) {
        return new TechBound(firstYear, lastYear, techName, isBoundAll, isBoundRange);
    }

    public String getFirstYear() {
        return firstYear;
    }

    public void setFirstYear(String val) {
    	this.firstYear=val;
    }
    
    public String getLastYear() {
        return lastYear;
    }

    public void setLastYear(String val) {
    	this.lastYear=val;
    }
    
    public String getTechName() {
        return techName;
    }

    public void setTechName(String val) {
    	this.techName=val;
    }
    
    public boolean isBoundAll() {
        return isBoundAll;
    }

    public boolean isBoundRange() {
        return isBoundRange;
    }
    
    public void setIsBoundAll(boolean isBound) {
    	this.isBoundAll=isBound;
    }

    public void setIsBoundRange(boolean isBound) {
    	this.isBoundRange=isBound;
    }
    
    /**
     * Checks if the technology is bound in any way (either for all years or for a range).
     * @return true if the technology is bound, false otherwise.
     */
    public boolean isBound() {
        return isBoundAll || isBoundRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechBound techBound = (TechBound) o;
        return isBoundAll == techBound.isBoundAll &&
               isBoundRange == techBound.isBoundRange &&
               firstYear.equals(techBound.firstYear) &&
               lastYear.equals(techBound.lastYear) &&
               techName.equals(techBound.techName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstYear, lastYear, techName, isBoundAll, isBoundRange);
    }

    @Override
    public String toString() {
        return String.format(
            "TechBound{techName='%s', firstYear='%s', lastYear='%s', isBoundAll=%b, isBoundRange=%b}",
            techName, firstYear, lastYear, isBoundAll, isBoundRange
        );
    }
}