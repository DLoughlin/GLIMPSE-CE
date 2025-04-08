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
package filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ModelInterface.InterfaceMain;
import ModelInterface.ModelGUI2.DbViewer;
import conversionUtil.ArrayConversion;
import graphDisplay.GraphDisplayUtil;
import graphDisplay.ModelInterfaceUtil;

/**
 * The class to handle filter tree pane to build, manage, and display filter
 * tree.
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class FilterTreePaneYears {
	private static final boolean playWithLineStyle = false;
	private static final String lineStyle = "Horizontal";
	private JTree tree;
	//private Map<String, String> sel;
	private boolean existSel = false;
	private JTable jtable;
	public JDialog dialog;
	private boolean debug = false;
	final InterfaceMain main = InterfaceMain.getInstance();


	public FilterTreePaneYears() {

		
		try {
			showFilter();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			dialog.dispose();
		}
	}
	
	

	private JScrollPane buildTree() {
		try {
			DefaultMutableTreeNode top = createNode("Filter All", "Root", null);
			tree = new JTree(top);
			createNodes(top);
			//if (existSel)
			//	setSelBoolean();
			//tree.getSelectionModel().setSelectionMode(4);

			MouseListener ml = new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if (selPath != null) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						if (e.getClickCount() > 0) {
							if (((TrNode) selectedNode.getUserObject()).isSelected) {
								setNodeBoolean(null, false, selectedNode);
							} else {
								setNodeBoolean(null, true, selectedNode);
							}
						}
					} else {
						tree.addSelectionPath(selPath);
						tree.expandPath(selPath);
					}
				}
			};
			tree.addMouseListener(ml);
			tree.setCellRenderer(new TreeSelCellRenderer());
			tree.setRowHeight(20);
			tree.setLargeModel(true);
			tree.setExpandsSelectedPaths(true);

			TreeNode root = (TreeNode) tree.getModel().getRoot();
			TreePath tPath = new TreePath(root);
			tree.expandPath(tPath);
			tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			tree.setAutoscrolls(true);
			tree.setScrollsOnExpand(true);
			tree.setMaximumSize(new Dimension(800, 1100));
		} catch (Exception e) {
			System.out.println("Error getting data for selected year list: "+e.toString());
		}
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		if (playWithLineStyle)
			tree.putClientProperty("JTree.lineStyle", lineStyle);

		return jsp;
	}

	private Box crtButton() {
		Box box = Box.createHorizontalBox();
		JButton jb = new JButton("Ok");
		jb.setName("Ok");
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JButton but = (JButton) e.getSource();
				if (but.getName().trim().equals("Ok")) {
					// if filter all unchecked give warning message
					if (DbViewer.getSelectedYearsFromPropFile().isEmpty())
						JOptionPane.showMessageDialog(null, "Select filter", "Warning", JOptionPane.WARNING_MESSAGE);
					else {
						
						
						//get a string of all values:
						String toWrite="";
						for(String s:DbViewer.getSelectedYearsFromPropFile().keySet()) {
							toWrite=toWrite+s+";";
						}
						toWrite=toWrite.substring(0,toWrite.length()-1);
						main.getProperties().setProperty("selectedYearList", toWrite);
						
					}
				//		new FilteredTable_orig(sel, chartName, unit, path, jtable, sp);
				//		dialog.dispose();
				}
				
				dialog.dispose();
			}
		};
		jb.addMouseListener(ml);
		box.add(jb);
		jb = new JButton("Cancel");
		jb.setName("Cancel");
		jb.addMouseListener(ml);
		box.add(jb);
		return box;
	}
	
	

	private void createNodes(DefaultMutableTreeNode top) {

		List<String> years=DbViewer.getAllYearListFromPropFile();
		
		String nodePath[] = new String[years.size() + 1];
		nodePath[0] = "Filter All";
		DefaultMutableTreeNode node = null;
		for(int i=1;i<=years.size();i++) {
			node = createNode(years.get(i-1).toString(), "filter", top);
			nodePath[i]=years.get(i-1).toString();
		}
		
	}

	

	private DefaultMutableTreeNode createNode(String nodename, String type, DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;

		if (nodename != null) {
			
			if (DbViewer.getSelectedYearsFromPropFile().containsKey(nodename) || nodename.contains("Filter"))
				category = new DefaultMutableTreeNode(new TrNode(nodename, type, true, top));
			else
				category = new DefaultMutableTreeNode(new TrNode(nodename, type, false, top));

			//String k = ((TrNode) category.getUserObject()).keyStr;// @
			//if (type.equals("Value") && !existSel)
			//	DbViewer.selectedYears.put(k, k);// @

			if (top != null) {
				top.add(category);
				//if (top.toString().contains("Year")// @
				//		&& !Arrays.asList(Var.sectionYRange).contains(nodename.trim())) {
				//	DbViewer.selectedYears.remove(k, k);
				//	((TrNode) category.getUserObject()).isSelected = false;
				//} // @
			}
		}

		if (debug)
			System.out.println("nodeName: " + category.toString());
		return category;
	}

	private void setNodeBoolean(String[] leaf, boolean selected, DefaultMutableTreeNode node) {

		TreeNode tNode = node;
		String keyStr = ((TrNode) node.getUserObject()).keyStr;

		if (!node.isRoot()) {
			((TrNode) node.getUserObject()).setSelected(selected);
			TreePath tpath = new TreePath(tNode);
			tree.expandPath(new TreePath(tNode));
			if (!tNode.isLeaf()) {
				DefaultMutableTreeNode pn = ((DefaultMutableTreeNode) tpath.getLastPathComponent());

				for (Enumeration<?> e = tNode.children(); e.hasMoreElements();) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
					if (n.isLeaf()) {
						keyStr = ((TrNode) n.getUserObject()).keyStr;

						if (leaf != null)
							if (Arrays.asList(leaf).contains(keyStr.trim())) { // @1
								selected = true;
							} else
								selected = false;

						if (selected)
							DbViewer.getSelectedYearsFromPropFile().put(keyStr, keyStr);// n.toString()
						else
							DbViewer.getSelectedYearsFromPropFile().remove(keyStr, keyStr);

						((TrNode) n.getUserObject()).setSelected(selected);
					} else
						setNodeBoolean(leaf, selected, n);
				}
				checkPartial(pn, ((TrNode) pn.getUserObject()).isSelected);
			} else if (tNode.isLeaf()) {
				if (leaf != null) {
					if (Arrays.asList(leaf).contains(keyStr.trim())) {
						selected = true;
					} else
						selected = false;
				}
				if (selected)
					DbViewer.getSelectedYearsFromPropFile().put(keyStr, keyStr);
				else
					DbViewer.getSelectedYearsFromPropFile().remove(keyStr, keyStr);
				DefaultMutableTreeNode pn = (DefaultMutableTreeNode) node.getParent();
				checkPartial(pn, ((TrNode) pn.getUserObject()).isSelected);
			}

			if (debug)
				for (String key : DbViewer.getSelectedYearsFromPropFile().keySet())
					System.out.println("setNodeBoolean:sel: " + DbViewer.getSelectedYearsFromPropFile().get(key));
		} else
			selectAllBox(selected);

		tree.updateUI();
	}

	private void selectAllBox(boolean selected) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreePath tPath = new TreePath(root);
		tree.expandPath(tPath);
		setPartialParentNode(false, (DefaultMutableTreeNode) root, selected);
		for (Enumeration<?> e = root.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

			if (selected)
				setNodeBoolean(null, true, node);
			else {
				setNodeBoolean(null, false, node);
				tree.clearSelection();
			}
		}
	}

	private void checkPartial(DefaultMutableTreeNode pNode, boolean selected) {
		boolean isPartial = false;
		TreePath tPath = new TreePath(pNode);
		tree.expandPath(tPath);

		for (Enumeration<?> e = pNode.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (selected) {
				if (!((TrNode) node.getUserObject()).isSelected) {
					isPartial = true;
					break;
				}
			} else {
				if (((TrNode) node.getUserObject()).isSelected) {
					isPartial = true;
					break;
				}
			}
		}
		setPartialParentNode(isPartial, pNode, selected);
		if (pNode.getParent() != null)
			checkPartial((DefaultMutableTreeNode) pNode.getParent(), selected);
		tree.setSelectionPath(tPath);
		tree.updateUI();
	}

	private void setPartialParentNode(boolean isPartial, DefaultMutableTreeNode pNode, boolean selected) {
		((TrNode) pNode.getUserObject()).setSelected(selected);
		if (!pNode.isLeaf()) {
			if (isPartial)
				if (selected) {
					((TrNode) pNode.getUserObject()).setPartialSelectedForParent(true);
					((TrNode) pNode.getUserObject()).setSelected(false);
				} else {
					((TrNode) pNode.getUserObject()).setPartialSelectedForParent(true);
					((TrNode) pNode.getUserObject()).setSelected(false);
				}
			else if (selected) {
				((TrNode) pNode.getUserObject()).setPartialSelectedForParent(false);
				((TrNode) pNode.getUserObject()).setSelected(true);
			} else {
				((TrNode) pNode.getUserObject()).setPartialSelectedForParent(false);
				((TrNode) pNode.getUserObject()).setSelected(false);
			}
		}
	}

	public void setSelBoolean() {
		String[] leaf = DbViewer.getSelectedYearsFromPropFile().keySet().toArray(new String[0]);
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreePath tPath = new TreePath(root);
		tree.expandPath(tPath);
		for (Enumeration<?> e = root.children(); e.hasMoreElements();) {
			setNodeBoolean(leaf, true, (DefaultMutableTreeNode) e.nextElement());
		}
	}

	public void showFilter() {
		dialog = new JDialog();
		dialog.setTitle("Year Filter");
		dialog.setSize(300, 500);
		dialog.setLocationRelativeTo(jtable);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(buildTree(), BorderLayout.CENTER);
		dialog.getContentPane().add(crtButton(), BorderLayout.SOUTH);
		//dialog.pack();
		dialog.setVisible(true);
		DbViewer.openWindows.add(dialog);
	}

	public JTree getTree() {
		return tree;
	}

	public JDialog getDialog() {
		return dialog;
	}

}
