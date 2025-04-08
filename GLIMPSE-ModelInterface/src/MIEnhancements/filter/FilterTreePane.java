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
package filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class FilterTreePane {
	private static final boolean playWithLineStyle = false;
	private static final String lineStyle = "Horizontal";
	private JTree tree;
	private Map<String, String> selOptions;
	private boolean existSel = false;
	private String chartName;
	private String[] unit;
	private String path;
	private JTable jtable;
	private JSplitPane sp;
	public JDialog dialog;
	private boolean debug = false;

	public FilterTreePane(String chartName, String[] unit, String path, JTable jtable, Map<String, String> sel,
			JSplitPane sp) {

		if (debug)
			System.out.println("jtable: " + jtable.getRowCount());
		try {
			init(chartName, unit, path, jtable, sel, sp);
			showFilter();
		} catch (Exception e) {
			System.out.println("Error launching filter panel:"+e.toString());
			dialog.dispose();
		}
	}

	private void init(String chartName, String[] unit, String path, JTable jtable, Map<String, String> sel,
			JSplitPane sp) {
		this.chartName = chartName;
		this.unit = unit.clone();
		this.path = path;
		this.jtable = jtable;
		this.selOptions = sel;
		this.sp = sp;
		if (sel != null) {
			this.selOptions = sel;
			existSel = true;
		} else
			this.selOptions = new LinkedHashMap<String, String>();
	}

	private String[] buildTreeName(JTable jtable) {

		String[] qualifier = ModelInterfaceUtil.getColumnFromTable(jtable, 5);
		String[][] listData = ArrayConversion.arrayDimReverse(ModelInterfaceUtil.getDataFromTable(jtable, 5));
		ArrayList<String[]> tableColumnUniqueValues = GraphDisplayUtil.getUniqQualifierData(qualifier, listData);

		ArrayList<String> al = buildNodesName(qualifier, tableColumnUniqueValues);
		String[] cn = al.toArray(new String[0]);
		if (debug)
			System.out.println("cn: " + Arrays.toString(cn));
		return cn;
	}

	private ArrayList<String> buildNodesName(String[] qualifier, ArrayList<String[]> tableColumnUniqueValues) {
		ArrayList<String> al = new ArrayList<String>();
		int cnt = ModelInterfaceUtil.getDoubleTypeColIndex(jtable);
		for (int n = cnt; n < jtable.getColumnCount() - 1; n++)
			al.add("Year|" + jtable.getColumnName(n));

		for (int k = 0; k < 1; k++) {
			for (int i = 0; i < qualifier.length; i++) {
				String q = qualifier[i].trim();
				String[] temp = tableColumnUniqueValues.get(i);
				for (int j = 0; j < temp.length; j++) {
					String v = q + "|" + temp[j].trim();
					al.add(v);
					if (debug)
						System.out.println("buildNodesName::al: " + Arrays.toString(al.toArray()));
				}
			}
		}
		return al;
	}

	private JScrollPane buildTree(String cn[]) {
		try {
			DefaultMutableTreeNode top = createNode("Filter All", "Root", null);
			tree = new JTree(top);
			createNodes(top, cn);
			if (existSel)
				setSelBoolean();
			tree.getSelectionModel().setSelectionMode(4);
			


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
			//Dan: replaced with text below
			//tree.setRowHeight(18);
			tree.setLargeModel(true);
			tree.setExpandsSelectedPaths(true);
			
			//Dan: testing some font options to avoid scaling problems; setup below works on EPA VM
			//tree.setFont(tree.getFont().deriveFont(14F));
			tree.setRowHeight(tree.getFont().getSize()+5);

			TreeNode root = (TreeNode) tree.getModel().getRoot();
			TreePath tPath = new TreePath(root);
			tree.expandPath(tPath);
			tree.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			tree.setAutoscrolls(true);
			tree.setScrollsOnExpand(true);
			tree.setMaximumSize(new Dimension(800, 1000));
		} catch (Exception e) {
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
					if (selOptions.isEmpty())
						JOptionPane.showMessageDialog(null, "Select filter", "Warning", JOptionPane.WARNING_MESSAGE);
					else {
						//add back in years
						//for(String key:DbViewer.getSelectedYearsFromPropFile().keySet()) {
						//	selOptions.put("Year|"+key,"Year|"+key);
						//}
						
						new FilteredTable_orig(selOptions, chartName, unit, path, jtable, sp);
						dialog.dispose();
					}
				} else
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

	private void createNodes(DefaultMutableTreeNode top, String cn[]) {

		for (int i = 0; i < cn.length; i++) {

			String temp[] = cn[i].split("\\|");
			String nodePath[] = new String[temp.length + 1];
			nodePath[0] = "Filter All";

			for (int k = 0; k < temp.length; k++)
				nodePath[k + 1] = temp[k];

			TreePath p = TreeUtil.findByName(tree, Arrays.copyOfRange(nodePath, 0, 2));
			DefaultMutableTreeNode node = null;
			if (p == null)
				node = createNode(nodePath[1], "filter", top);
			else
				node = (DefaultMutableTreeNode) p.getLastPathComponent();

			createSubNode(temp, 1, "", node);
		}
	}

	private void createSubNode(String nodename[], int level, String type, DefaultMutableTreeNode top) {
		for (int j = level; j < nodename.length; j++) {
			String temp[] = Arrays.copyOfRange(nodename, 0, j + 1);
			TreePath p = TreeUtil.findByName(tree, temp);
			DefaultMutableTreeNode node = null;
			if (p == null) {
				if (j == nodename.length - 1) {
					node = createNode(nodename[j], "Value", top);
					if (debug)
						System.out.println("createSubNode:sel: " + Arrays.toString(selOptions.values().toArray()));
				} else {
					node = createNode(nodename[j], "column", top);
				}
				top = node;
			} else {
				node = (DefaultMutableTreeNode) p.getLastPathComponent();
				top = node;
			}
		}
	}

	private DefaultMutableTreeNode createNode(String nodename, String type, DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;

		if (nodename != null) {
			if (!existSel)
				category = new DefaultMutableTreeNode(new TrNode(nodename, type, true, top));
			else
				category = new DefaultMutableTreeNode(new TrNode(nodename, type, false, top));

			String k = ((TrNode) category.getUserObject()).keyStr;// @
			if (type.equals("Value") && !existSel)
				selOptions.put(k, k);// @

			if (top != null) {
				top.add(category);
				if (top.toString().contains("Year")// @
						&& !Arrays.asList(Var.sectionYRange).contains(nodename.trim())) {
					selOptions.remove(k, k);
					((TrNode) category.getUserObject()).isSelected = false;
				} // @
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
							selOptions.put(keyStr, keyStr);// n.toString()
						else
							selOptions.remove(keyStr, keyStr);

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
					selOptions.put(keyStr, keyStr);
				else
					selOptions.remove(keyStr, keyStr);
				DefaultMutableTreeNode pn = (DefaultMutableTreeNode) node.getParent();
				checkPartial(pn, ((TrNode) pn.getUserObject()).isSelected);
			}

			if (debug)
				for (String key : selOptions.keySet())
					System.out.println("setNodeBoolean:sel: " + selOptions.get(key));
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
		String[] leaf = selOptions.keySet().toArray(new String[0]);
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreePath tPath = new TreePath(root);
		tree.expandPath(tPath);
		for (Enumeration<?> e = root.children(); e.hasMoreElements();) {
			setNodeBoolean(leaf, true, (DefaultMutableTreeNode) e.nextElement());
		}
	}

	public void showFilter() {
		dialog = new JDialog();
		dialog.setTitle(chartName + " Filter");
		dialog.setSize(300, 400);
		dialog.setLocationRelativeTo(jtable);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(buildTree(buildTreeName(jtable)), BorderLayout.CENTER);
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
