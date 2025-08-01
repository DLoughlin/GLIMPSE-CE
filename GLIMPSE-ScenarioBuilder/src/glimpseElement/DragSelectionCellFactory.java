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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * A factory for creating {@link DragSelectionCell} instances for a TableView.
 *
 * <p><b>Note:</b> In Java 8 and later, it is considered best practice to replace
 * explicit factory classes like this with a concise lambda expression directly
 * in the TableColumn's {@code setCellFactory} method.
 * For example: {@code column -> new DragSelectionCell()}
 */
public class DragSelectionCellFactory implements Callback<TableColumn<DataPoint, String>, TableCell<DataPoint, String>> {

    /**
     * Creates a new instance of a {@link DragSelectionCell}.
     *
     * @param column The TableColumn for which the cell is created.
     * @return A new {@code DragSelectionCell}.
     */
    @Override
    public TableCell<DataPoint, String> call(TableColumn<DataPoint, String> column) {
        return new DragSelectionCell();
    }
}