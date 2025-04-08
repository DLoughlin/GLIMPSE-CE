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
package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

class RunnableCmd implements Runnable {

	String cmd = null;
	String[] cmdArray = null;
	File dir = null;

	public void setCmd(String commandText) {
		cmd = commandText;
	}
	
	public void setCmd(String[] commandText) {
		cmdArray = commandText;
	}

	public void setCmd(String commandText, String directory) {
		cmd = commandText;
		dir = new File(directory);
		if (!dir.isDirectory())
			System.out.println("specified directory " + dir + " does not exist.");
	}

	public void setCmd(String[] commandText, String directory) {
		cmdArray = commandText;
		dir = new File(directory);
		if (!dir.isDirectory())
			System.out.println("specified directory " + dir + " does not exist.");
	}

	//Dan reverted the following
	@Override
	public void run() {
		// System.out.println("is dir?" + dir.isDirectory());
		java.lang.Runtime rt = java.lang.Runtime.getRuntime();
		try {
			java.lang.Process p = null;
			if (dir == null) {
				p = rt.exec(cmd);
			} else if (cmd == null) {
				p = rt.exec(cmdArray, null, dir);
			} else {
				p = rt.exec(cmd, null, dir);
			}

			String line;
			InputStream stdout = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			while ((line = reader.readLine()) != null) {
				System.out.println("Stdout: " + line);
			}
			p.waitFor();
			p.destroy();
			stdout.close();
			reader.close();
		} catch (Exception e) {
			System.out.println("problem starting \"" + cmd + "\".");
			System.out.println("Error: " + e);
		}
	}


}
