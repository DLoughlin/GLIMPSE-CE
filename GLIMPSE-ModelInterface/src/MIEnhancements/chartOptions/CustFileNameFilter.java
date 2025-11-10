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
import java.io.FilenameFilter;

/**
 * Handles filtering of files by extension and/or name for file selection dialogs.
 * <p>
 * Usage example:
 * <pre>
 *   File dir = new File("/path/to/dir");
 *   FilenameFilter filter = new CustFileNameFilter(".txt,.csv", "report,summary");
 *   File[] files = dir.listFiles(filter);
 * </pre>
 * </p>
 *
 * Author: TWU
 * Date: 1/2/2016
 */
public class CustFileNameFilter implements FilenameFilter {
    /**
     * Comma-separated list of file extensions to filter (e.g., ".txt,.csv").
     */
    private final String ext;
    /**
     * Comma-separated list of substrings to match in file names (optional).
     */
    private final String fileName;

    /**
     * Constructs a filter for files with specified extensions and/or name substrings.
     * @param ext Comma-separated extensions (e.g., ".txt,.csv").
     * @param fileName Comma-separated substrings to match in file names (optional).
     */
    public CustFileNameFilter(String ext, String fileName) {
        this.ext = ext;
        this.fileName = fileName;
    }

    /**
     * Tests if a specified file should be included in a file list.
     * @param dir the directory in which the file was found
     * @param name the name of the file
     * @return true if the file matches the filter criteria
     */
    @Override
    public boolean accept(File dir, String name) {
        if (ext == null) {
            return false;
        }
        String[] extensions = ext.split(",");
        for (String extension : extensions) {
            if (matchExt(extension.trim(), name)) {
                // If fileName filter is set, check if name contains any substring
                if (fileName != null) {
                    if (matchFileName(name)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the file name ends with the given extension.
     * @param extension the file extension to check
     * @param name the file name
     * @return true if name ends with extension
     */
    private boolean matchExt(String extension, String name) {
        return name.endsWith(extension);
    }

    /**
     * Checks if the file name contains any of the specified substrings.
     * @param name the file name
     * @return true if name contains any substring from fileName
     */
    private boolean matchFileName(String name) {
        String[] substrings = fileName.split(",");
        for (String substring : substrings) {
            if (name.contains(substring.trim())) {
                return true;
            }
        }
        return false;
    }
}