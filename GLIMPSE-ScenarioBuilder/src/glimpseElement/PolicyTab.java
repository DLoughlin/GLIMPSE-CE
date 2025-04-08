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
package glimpseElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;

public class PolicyTab extends Tab {
	public ProgressBar progress_bar = new ProgressBar(0.0);
	public String filename_suggestion = "";
	public String file_content = "";
	public ArrayList<String> market_list;

	protected GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	protected GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	protected GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	protected GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	
	//TODO:  Where is this called
	public void saveScenarioComponent() {
		// TODO Auto-generated method stub
		;
	}

	public String getMetaDataContent(TreeView<String> tree) {
		String rtn_str = "";
		return rtn_str;
	}

	public void loadContent(ArrayList<String> content) {
		;
	}

	public void setProgress(double d) {
		progress_bar.setProgress(d);
	}

	public String getFilenameSuggestion() {
		return filename_suggestion;
	}

	public String getFileContent() {
		System.out.println("Getting file content... length:"+file_content.length());
		return file_content;
	}

	public void resetFileContent() {
		file_content = "";
	}

	public void resetFilenameSuggestion() {
		filename_suggestion = "";
	}

	public void resetProgressBar() {
		progress_bar.setProgress(0.0);
	}

	PolicyTab() {
		;
	}

	public String getUniqueMarketName(String market_name) {
		String rtn_str="";
		
		File folder = new File(vars.getScenarioComponentsDir());
		String[] file_list = folder.list();

		if (market_list == null) {
			market_list = new ArrayList<String>();

			for (int i = 0; i < file_list.length; i++) {
				String filename=vars.getScenarioComponentsDir()+File.separator+file_list[i];
				File f=new File(filename);
				if (!f.isDirectory()) {
					ArrayList<String> lines = files.searchForTextInFileA(filename, "Mkt", "#");
					for (int j = 0; j < lines.size(); j++) {
						String mkt_name = utils.getTokenWithText(lines.get(j), "Mkt", ",");
						boolean match = utils.getMatch(mkt_name, market_list);
						if (!match) {
							market_list.add(mkt_name);
						} 
					}
				}
			}
		}

		int ID = 0;
		
		String orig_market_name=market_name;
		
		for (int i=0;i<market_list.size();i++) {
			String market_from_list=market_list.get(i);
			if (market_from_list.startsWith(orig_market_name)) {
				ID++;
			}
		}
		
		if (ID!=0) { 
			market_list.add(orig_market_name+=""+ID);
			rtn_str=""+ID;
		}
		
		return rtn_str; 
	}
	
}
