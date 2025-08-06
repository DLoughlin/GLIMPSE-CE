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
 * An immutable data object representing a technology's active status over a given year or range of years.
 */
public final class TechBound2 {

    private final String firstYear;
    private final String lastYear;
    private final String techName;
    private final boolean active;

    /**
     * Private constructor. Use static factory methods to create instances.
     */
    private TechBound2(String firstYear, String lastYear, String techName, boolean active) {
        this.firstYear = Objects.requireNonNull(firstYear, "First year cannot be null.");
        this.lastYear = Objects.requireNonNull(lastYear, "Last year cannot be null.");
        this.techName = Objects.requireNonNull(techName, "Tech name cannot be null.");
        this.active = active;
    }

    /**
     * Creates a TechBound2 instance for a single year.
     *
     * @param year The year for which the bound is active.
     * @param techName The name of the technology.
     * @param isActive The active status.
     * @return A new TechBound2 instance.
     */
    public static TechBound2 createForSingleYear(String year, String techName, boolean isActive) {
        return new TechBound2(year, year, techName, isActive);
    }

    /**
     * Creates a TechBound2 instance for a range of years.
     *
     * @param firstYear The first year of the range (inclusive).
     * @param lastYear The last year of the range (inclusive).
     * @param techName The name of the technology.
     * @param isActive The active status.
     * @return A new TechBound2 instance.
     */
    public static TechBound2 createForYearRange(String firstYear, String lastYear, String techName, boolean isActive) {
        return new TechBound2(firstYear, lastYear, techName, isActive);
    }

    public String getFirstYear() {
        return firstYear;
    }

    public String getLastYear() {
        return lastYear;
    }

    public String getTechName() {
        return techName;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechBound2 that = (TechBound2) o;
        return active == that.active &&
               firstYear.equals(that.firstYear) &&
               lastYear.equals(that.lastYear) &&
               techName.equals(that.techName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstYear, lastYear, techName, active);
    }

    @Override
    public String toString() {
        return String.format(
            "TechBound2{techName='%s', firstYear='%s', lastYear='%s', active=%b}",
            techName, firstYear, lastYear, active
        );
    }
}