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

import glimpseUtil.GLIMPSEFiles;
import glimpseUtil.GLIMPSEStyles;
import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PaneForCountryStateTree extends VBox {
	private GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	private GLIMPSEStyles styles = GLIMPSEStyles.getInstance();
	private GLIMPSEFiles files = GLIMPSEFiles.getInstance();
	private GLIMPSEUtils utils = GLIMPSEUtils.getInstance();
	TreeView<String> tree_view;
	Label labelAppliedTo;
	Label labelPresetRegions;
	ComboBox<String> comboBoxPresetRegions;
	
	ArrayList<String> region_list=new ArrayList<String>();
	ArrayList<String> subregion_list=new ArrayList<String>();
	ArrayList<String> preset_region_list=new ArrayList<String>();
	
	public PaneForCountryStateTree() {
		
		loadRegionAndSubregionData();
		
		this.setStyle(styles.font_style);
		tree_view=treeViewAddRegions();
		if (vars.isGcamUSA()) tree_view = treeViewAddSubregions();

		labelAppliedTo = utils.createLabel("Select region(s):");
		labelPresetRegions = utils.createLabel("Presets:");
		labelPresetRegions.setMinWidth(50);

		TreeItem treeItem = tree_view.getRoot();
		treeItem.setExpanded(true);
		StackPane tree = new StackPane(tree_view);
		labelAppliedTo.setStyle(styles.style3);
		
		comboBoxPresetRegions=utils.createComboBoxString();
		comboBoxPresetRegions.getItems().add("Select (optional)");
		comboBoxPresetRegions.getSelectionModel().select(0);
		
		comboBoxPresetRegions.setOnAction(e -> {
			checkPresetRegions();
		});
		
		populateComboBoxPresetRegions();
		
		HBox hbox=new HBox();
		hbox.setSpacing(5.);
		hbox.setPadding(new Insets(3., 0., 0., 0.));
		hbox.getChildren().addAll(labelPresetRegions,comboBoxPresetRegions);
		hbox.setStyle(styles.style2);

		this.getChildren().addAll(labelAppliedTo, tree,hbox);
		this.setStyle(styles.style2);
		
	}
	
	public void checkPresetRegions() {
		String selection=comboBoxPresetRegions.getSelectionModel().getSelectedItem();
		for (int i=0;i<preset_region_list.size();i++) {
			String line=preset_region_list.get(i);
			int index=line.indexOf(":");
			if (index>0) {
				String name=line.substring(0,index).toLowerCase();
				if (selection.toLowerCase().equals(name)) {
					String[] subregions=utils.splitString(line.substring(index+1),",");
					selectNodes(subregions);
				}
			}
		}
	}
	
	public void populateComboBoxPresetRegions() {
		comboBoxPresetRegions.getItems().clear();
		comboBoxPresetRegions.getItems().add("Select (optional)");
		comboBoxPresetRegions.getSelectionModel().select(0);
		
		for (int i=0;i<preset_region_list.size();i++) {
			String s=preset_region_list.get(i);
			int index_of_semicolon=s.indexOf(":");
			if (index_of_semicolon>-1) {
				s=s.substring(0,index_of_semicolon);
				comboBoxPresetRegions.getItems().add(s);
			}
		}
	}
	
	public void loadRegionAndSubregionData() {
		String state_list_filename=vars.getgCamGUIDir()+File.separator+"resources"+File.separator+"subregion_list.txt";
		String region_list_filename=vars.getgCamGUIDir()+File.separator+"resources"+File.separator+"region_list.txt";
		String preset_region_list_filename=vars.getgCamGUIDir()+File.separator+"resources"+File.separator+"preset_region_list.txt";
		
		//state list
		try {
			ArrayList<String> contents = files.getStringArrayFromFile(state_list_filename, "#");

			for (int i = 0; i < contents.size(); i++) {
				String line = contents.get(i);
				if (line.indexOf(":")>0) subregion_list.add(line);
			}
		} catch (Exception e) {
			String s= "USA:AL,AK,AZ,AR,CA,CO,CT,DE,DC,FL,GA,HI,ID,IL,IN,IA,KS,KY,LA,ME,MD,MA,MI,MN,MS,MO,MT,NE,NV,NH,NJ,NM,NY,NC,ND,OH,OK,OR,PA,RI,SC,SD,TN,TX,UT,VT,VA,WA,WV,WI,WY"/*,USA*/;
			subregion_list.add(s);
		}
		
		//region list
		try {
			ArrayList<String> contents = files.getStringArrayFromFile(region_list_filename, "#");

			for (int i = 0; i < contents.size(); i++) {
				String line = contents.get(i);
				if (line.length() > 0) {
					ArrayList<String> temp_list = utils.createArrayListFromString(line, ",");
					for (int j = 0; j < temp_list.size(); j++) {
						region_list.add(temp_list.get(j));
					}
				}
			}
		} catch (Exception e) {
			String[] strs= { 
					"USA","Canada", "EU-15", "Europe_Non_EU", "European Free Trade Association", "Japan",
					"Australia_NZ", "Central Asia", "Russia", "China", "Middle East", "Africa_Eastern",
					"Africa_Northern", "Africa_Southern", "Africa_Western", "South Africa", "Brazil",
					"Central America and Caribbean", "Mexico", "South America_Northern", "South America_Southern",
					"Argentina", "Colombia", "Indonesia", "Pakistan", "South Asia", "Southeast Asia", "Taiwan",
					"Europe_Eastern", "EU-12", "South Korea", "India" 
				};
			for (String s:strs) region_list.add(s);
		}

		//preset region list
		try {
			ArrayList<String> contents = files.getStringArrayFromFile(preset_region_list_filename, "#");

			for (int i = 0; i < contents.size(); i++) {
				String line = contents.get(i);
				if (line.length() > 0) {
					preset_region_list.add(line);
				}
			}
		} catch (Exception e) {
			String[] strs= { 
					"North America:USA,Canada,Mexico,Central America and Caribbean",
					"South America:Argentina,Brazil,Colombia,South America_Northern,South America_Southern", 
					"Africa:Africa_Northern,Africa_Southern,Africa_Eastern,Africa_Western",
					"EU:EU-15,EU-12",
					"Europe:EU-15,EU-12,Europe_Eastern,European Free Trade Association,Europe_Non_EGU",
					"Asia:Japan,Central Asia,Russia,China,Middle East,Indonesia,Pakistan,South Asia,Southeast Asia,Taiwan,South Korea,India",
					"East Asia:Japan,China,Taiwan,South Korea",
					"Southeast Asia:Indonesia,Southeast Asia",
					"South Asia:Pakistan,India,South Asia",
					"West Asia:Middle East" 
				};
			for (String s:strs) preset_region_list.add(s);
		}
		
		
	}

	public void selectNodes(String[] nodes) {

		tree_view.getRoot().setExpanded(true);
		ObservableList<TreeItem<String>> list = tree_view.getRoot().getChildren();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setExpanded(true);
		}

		int num_of_leaves = tree_view.getExpandedItemCount();
		// TreeItem<String> t = null;

		for (int i = 0; i < nodes.length; i++) {

			for (int j = 0; j < num_of_leaves; j++) {

				String st_str = nodes[i].trim();
				CheckBoxTreeItem<String> treeItem = (CheckBoxTreeItem<String>) tree_view.getTreeItem(j);

				String tree_item_name = treeItem.getValue();
				if (st_str.equals(tree_item_name)) {
					treeItem.setSelected(true);
					expandTreeView(treeItem);

				}
			}
		}
	}

	public void expandTreeView(TreeItem<String> selectedItem) {
		if (selectedItem != null) {
			expandTreeView(selectedItem.getParent());

			if (!selectedItem.isLeaf()) {
				selectedItem.setExpanded(true);
			}
		}
	}

	public TreeView<String> getTree() {
		return tree_view;
	}

	
	public TreeView<String> treeViewAddSubregions(){
		
		TreeItem<String> ti=tree_view.getRoot();
		ObservableList<TreeItem<String>> ti_list=ti.getChildren();
		
		for (int i=0;i<subregion_list.size();i++) {
			String line=subregion_list.get(i);
			int index=line.indexOf(":");
			if (index>0) {
				String region=line.substring(0,index);
				String[] subregion_list=utils.splitString(line.substring(index+1),",");
				
				CheckBoxTreeItem<String> region_node=null;
				for (int k=0;k<ti_list.size();k++) {
					if (ti_list.get(k).getValue().equals(region)) { 
						region_node=(CheckBoxTreeItem<String>)ti_list.get(k);
					    break;
					}
				}
				
				if (region_node!=null) {
				for (int j=0;j<subregion_list.length;j++) {
					String subregion_name=subregion_list[j];
					CheckBoxTreeItem<String> temp_item=new CheckBoxTreeItem<>(subregion_name);
					region_node.getChildren().add(temp_item);
				}
				}
			}
			
		}
				
		return tree_view;
	}

	public TreeView<String> treeViewAddRegions() {

		CheckBoxTreeItem<String> world = new CheckBoxTreeItem<>("world");
		
		for (int i=0;i<region_list.size();i++) {
			String s=region_list.get(i);
			world.getChildren().add(new CheckBoxTreeItem<>(s));
		}

		// Craete a TreeView with depts as its root item
		TreeView<String> treeView = new TreeView<>(world);

		// Set the cell factory to draw a CheckBox in cells
		treeView.setCellFactory(CheckBoxTreeCell.<String> forTreeView());

		return treeView;
	}
	
	public ArrayList<String> getSelectedNodes() {
		ArrayList<String> selectedNodes = new ArrayList<String>();

		TreeView<String> tv = getTree();
		ObservableList<TreeItem<String>> observableNodeList = tv.getSelectionModel().getSelectedItems();

		for (int i = 0; i < observableNodeList.size(); i++) {
			String str = observableNodeList.get(i).toString();
			selectedNodes.add(str);
		}

		return selectedNodes;
	}
	
	public void addEventHandlerToAllLeafs(EventHandler ev){
		
		TreeView tv = getTree();
		
		addEventHandlerRecursively(tv.getRoot(),ev);
	
		return;
	}
	
	protected void addEventHandlerRecursively(TreeItem<?> node,EventHandler ev) {
		for (TreeItem child : node.getChildren()) {
			child.addEventHandler(CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(), ev);
			addEventHandlerRecursively(child,ev);
		}
		return;
	}	
	
	protected int countItemsInTree(TreeItem<?> node) {
		int count=1;
		for (TreeItem child : node.getChildren()) {
			count += countItemsInTree(child);
		}
		return count;
	}
	
	
}

