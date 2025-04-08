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
package chartOptions;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The class to handle name fileter for a file selection
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class CustFileNameFilter implements FilenameFilter
{
    String ext;
    String fileName;
    
    public CustFileNameFilter(String ext, String fileName) {
	this.ext = ext;
	this.fileName = fileName;
    }
    
    public boolean accept(File dir, String name) {
	boolean b = false;
	if (ext != null) {
	    String[] s = ext.split(",");
	    for (int i = 0; i < s.length; i++) {
		boolean me = matchExt(s[0].trim(), name);
		if (me && fileName != null) {
		    if (matchFileName(name)) {
			b = true;
			break;
		    }
		} else if (me) {
		    b = true;
		    break;
		}
	    }
	}
	return b;
    }
    
    boolean matchExt(String e, String name) {
	return name.endsWith(e.trim());
    }
    
    boolean matchFileName(String name) {
	boolean b = false;
	String[] s = fileName.split(",");
	for (int i = 0; i < s.length; i++) {
	    if (name.contains(s[i].trim())) {
		b = true;
		break;
	    }
	}
	return b;
    }
}
