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
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package chartOptions;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter for file selection dialogs.
 * <p>
 * Filters files based on extension(s) and optional filename substring(s).
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *   new CustFileFilter(".txt,.csv", "report,summary")
 * </pre>
 * </p>
 *
 * @author TWU
 * @since 1/2/2016
 */
public class CustFileFilter extends FileFilter {
    /** Comma-separated list of file extensions (e.g., ".txt,.csv") */
    private final String ext;
    /** Comma-separated list of filename substrings (e.g., "report,summary") */
    private final String fileName;

    /**
     * Constructs a file filter for the given extensions and filename substrings.
     *
     * @param ext      Comma-separated file extensions (e.g., ".txt,.csv")
     * @param fileName Comma-separated filename substrings (e.g., "report,summary")
     */
    public CustFileFilter(String ext, String fileName) {
        this.ext = ext;
        this.fileName = fileName;
    }

    /**
     * Determines if the given file should be accepted by the filter.
     *
     * @param file The file to check
     * @return true if the file matches the filter criteria, false otherwise
     */
    @Override
    public boolean accept(File file) {
        String name = file.getName();
        if (ext != null) {
            String[] extensions = ext.split(",");
            for (String extension : extensions) {
                boolean matchesExtension = matchExt(extension.trim(), name);
                if (matchesExtension && fileName != null) {
                    if (matchFileName(name)) {
                        return true;
                    }
                } else if (matchesExtension) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the filename ends with the given extension.
     *
     * @param extension The file extension (e.g., ".txt")
     * @param name      The filename
     * @return true if the filename ends with the extension
     */
    private boolean matchExt(String extension, String name) {
        return name.endsWith(extension);
    }

    /**
     * Checks if the filename contains any of the specified substrings.
     *
     * @param name The filename
     * @return true if the filename contains any substring from fileName
     */
    private boolean matchFileName(String name) {
        if (fileName == null) {
            return false;
        }
        String[] substrings = fileName.split(",");
        for (String substring : substrings) {
            if (name.contains(substring.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provides a description for the file filter (for display in dialogs).
     *
     * @return Description string
     */
    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder("Files (");
        if (ext != null) {
            desc.append(ext.replace(",", ", "));
        }
        desc.append(")");
        if (fileName != null) {
            desc.append(" containing [").append(fileName.replace(",", ", ")).append("]");
        }
        return desc.toString();
    }
}