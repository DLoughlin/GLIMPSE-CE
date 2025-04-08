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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jfree.chart.JFreeChart;

import chart.Chart;
import chart.LegendUtil;
import graphDisplay.CreateComponent;
import listener.LineAndShapePopup;

public class ModifyLegend extends JDialog {
	private static final long serialVersionUID = 1L;
	private Chart chart;
	private Chart[] charts;
	private String[] legend;
	private int id;
	private JFreeChart jfchart;
	private JTextField jtf;
	// legend 0, Color 1, Pattern 2, Line Stroke
	public int eventApply;
	public JDialog cancelDialog;
	private JButton jbColor;
	private JTextField jtfChanged;
	private String changeColLegend;
	//private TexturePaint changeColPaint;
	//private int changeColColor;
	
	private HashMap<String,JComboBox> patternLookup=new HashMap<String,JComboBox>();
	private HashMap<String,JComboBox> strokeLookup=new HashMap<String,JComboBox>();
	
	TexturePaint[] tpList;
	TexturePaint[] tpStrokeList;
	BasicStroke[] bsStrokeList;
	
	//yes these are awful, no I don't know where they came from.
	int[] patternList = { 0, -4162, -4126, 11, 14, 16, 17 };
	int[] strokeList = { 0, 5, 10, 20, 30, 40 };
	

	public ModifyLegend(Chart[] charts, int id) {
		if (charts == null)
			return;
		
		this.charts = charts;// .clone();
		this.id = id;
		this.chart = charts[id];
		cancelDialog = this;
		setLegendUI();
	}
	
	class ImageComboBoxRenderer extends JPanel implements ListCellRenderer<String> {
	    private JLabel label;
	    private BufferedImage image;

	    public ImageComboBoxRenderer(BufferedImage image) {
	        this.image = image;
	        this.label = new JLabel();
	        setLayout(new BorderLayout());
	        add(label, BorderLayout.CENTER);
	    }

	    @Override
	    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
	        label.setText(value);
	        return this;
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        if (image != null) {
	            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	        }
	    }
	}

	private void setLegendUI() {
		GridBagLayout gridbag = new GridBagLayout();
		JPanel jp = new JPanel();
		jp.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 10, 5, 50);
		c.gridheight = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		setColumnLabel(gridbag, jp);
		legend = chart.getLegend().split(",");
		
	
		ImageIcon[] iiList=new ImageIcon[patternList.length];
		tpList=new TexturePaint[patternList.length];
		for(int a=0;a<patternList.length;a++) {
			TexturePaint tp=LegendUtil.getTexturePaint(Color.BLACK,Color.GREEN,patternList[a],1);
			tpList[a]=tp;
			iiList[a]=  new ImageIcon(tp.getImage().getScaledInstance(45, 25, java.awt.Image.SCALE_SMOOTH));

		}
		

		
		ImageIcon[] iiStrokeList=new ImageIcon[strokeList.length];
		tpStrokeList=new TexturePaint[strokeList.length];
		bsStrokeList=new BasicStroke[strokeList.length];
		for(int a=0;a<strokeList.length;a++) {
			bsStrokeList[a]=LegendUtil.getLineStroke(strokeList[a]);
			TexturePaint tp=LegendUtil.getTexturePaint(Color.BLACK,Color.GREEN,11,strokeList[a]);
			tpStrokeList[a]=tp;
			iiStrokeList[a]=  new ImageIcon(tp.getImage().getScaledInstance(45, 25, java.awt.Image.SCALE_SMOOTH));

		}

		for (int j = 0; j < legend.length; j++) {
			//this is the JTF from the name 
			c = new GridBagConstraints();
			c.fill = 1;
			String name = String.valueOf(j);
			
			jtf = CreateComponent.crtJTextField(name, legend[j], j);
			//jtf.setToolTipText("0");
			jtf.setScrollOffset(10);
			//jtf.getDocument().addDocumentListener(new MyDocumentListener(jtf, jb));
			jtf.setEditable(false);
			Font font1 = new Font("SansSerif", Font.PLAIN, 14);
			jtf.setFont(font1);
			gridbag.setConstraints(jtf, c);
			jp.add(jtf);
			
			//this is the color changer Icon
			//we need to cache here
			ImageIcon icon = new ImageIcon();
			TexturePaint tpCur=LegendUtil.getTexturePaint(new Color(chart.getColor()[j]), new Color(chart.getColor()[j]), 0,
					0);// 0);
			icon.setImage(tpCur.getImage());
			Image image = icon.getImage();
			image = image.getScaledInstance(80, 20, 4);
			icon.setImage(image);
			JButton jb = CreateComponent.crtJButton(name, (ImageIcon) null);
			jb.setIcon(icon);
			//jb.setToolTipText("1");
			//jb.setActionCommand("colorChanges");
			ColorModifyActionListener mbl = new ColorModifyActionListener();
			jb.setFocusable(true);
			jb.addActionListener(mbl);
			gridbag.setConstraints(jb, c);
			jp.add(jb);

			
			int[] pattern = chart.getPattern();
			JComboBox jcb=new JComboBox(iiList);
			//jcb.setRenderer(new ImageComboBoxRenderer(image));
			//jtf = CreateComponent.crtJTextField(name, String.valueOf(pattern[j]), j);
			//jtf.setToolTipText("2");
			//jtf.getDocument().addDocumentListener(new MyDocumentListener(jtf, jb));
			//jtf.setFont(font1);
			patternLookup.put(legend[j].trim(), jcb);
			gridbag.setConstraints(jcb, c);
			jp.add(jcb);

			int[] ls = chart.getLineStrokes();
			jcb=new JComboBox(iiStrokeList);
			//jtf = CreateComponent.crtJTextField(name, String.valueOf(ls[j]), j);
			//jtf.setToolTipText("3");
			//jtf.getDocument().addDocumentListener(new MyDocumentListener(jtf, jb));
			//jtf.setFont(font1);
			//new LineAndShapePopup(jtf, chart);
			strokeLookup.put(legend[j].trim(), jcb);
			gridbag.setConstraints(jcb, c);
			jp.add(jcb);

			c.gridwidth = GridBagConstraints.REMAINDER;
			JLabel jl = new JLabel("");
			gridbag.setConstraints(jl, c);
			jp.add(jl);
			c.gridwidth = 0;
			c.weightx = 0.0;
		}
		
		//for the two combo boxes, set the index to the correct value
		for (int j = 0; j < legend.length; j++) {
			//patterns first:
			int pattern=chart.getPattern()[j];
			for(int curIDX=0;curIDX<patternList.length;curIDX++) {
				if(pattern==patternList[curIDX]) {
					patternLookup.get(legend[j].trim()).setSelectedIndex(curIDX);
				}
			}
			
			
			//now strokes
			int stroke=chart.getLineStrokes()[j];
			for(int curIDX=0;curIDX<strokeList.length;curIDX++) {
				if(stroke==strokeList[curIDX]) {
					strokeLookup.get(legend[j].trim()).setSelectedIndex(curIDX);
				}
			}
			
			
			
			
			
		}
		//YD edits, Jan-2025
		String[] options = { "Apply", "Save For Query", "Save Default","Done" };
		JButton jb1;
		Box box = Box.createHorizontalBox();
		box.add(Box.createVerticalStrut(30));
		for (int i = 0; i < options.length; i++) {
			jb1 = crtJButton(options[i], i);
			gridbag.setConstraints(jb1, c);
			box.add(jb1);
		}
		box.add(Box.createVerticalStrut(30));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		c.ipadx = 60;
		gridbag.setConstraints(box, c);
		jp.add(box);

		JScrollPane jsp = new JScrollPane(jp);
		jsp.setBorder(BorderFactory.createEmptyBorder());

		// Make this dialog display it.
		setContentPane(jsp);
		pack();
		// Handle window closing correctly.
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JButton crtJButton(String name, int i) {
		JButton jb = new JButton(name);
		jb.setName(name);
		jb.setToolTipText(String.valueOf(i));
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JButton jb1 = (JButton) e.getSource();
				if (e.getClickCount() > 0)
					jbColor=jb1;
					if (jb1.getName().equals("Apply")) {
						
						doApply();
					}
					else if (jb1.getName().equals("Save"))
						chart.storelegendInfo(chart.getLegend().split(","), getLegendInfoStr());
					else if (jb1.getName().equals("Save Default"))
						chart.storelegendInfoGlobal(chart.getLegend().split(","), getLegendInfoStr());
					else if (jb1.getName().equals("Save For Query"))
						chart.storelegendInfoLocal(chart.getLegend().split(","), getLegendInfoStr());
					else if (jb1.getName().equals("Done")) {
						//if (jbColor != null) {
						//	if (JOptionPane.showConfirmDialog(null,
						//			"Click OK, Action has not been applied will be lost", "Confirm",
						//			JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
						//		cancelDialog.dispose();
						//	else
						//		JOptionPane.showMessageDialog(null, "Click Apply Button to apply changes.",
						//				"Information", JOptionPane.INFORMATION_MESSAGE);
						//} else
							cancelDialog.dispose();
					}
			}
		};
		jb.addMouseListener(ml);
		return jb;
	}

	private void doApply() {
		//if (jbColor != null) {
			//switch (eventApply) {
			//case 0:
			//This code will specifically update the string value on the chart.  IT needs
			//to be updated to accept an array so it can change all labels if needed.
			//also setLegendChanges will need to be updated.
				//SetModifyChanges.setLegendChanges(charts, id, jtfChanged);
			//	break;
			//case 1:
				SetModifyChanges.setColorChanges(charts);
			//	break;
			//case 2:
				SetModifyChanges.setPatternChanges(charts, patternLookup,patternList);
			//	break;
			//case 3:
				SetModifyChanges.setStrokeChanges(charts, strokeLookup, bsStrokeList,strokeList);
			//	break;
			//}
			//jbColor = null;
			//jtfChanged = null;
		//}
	}

	private String[] getLegendInfoStr() {
		String[] s = new String[chart.getColor().length];
		for (int i = 0; i < s.length; i++) {
			s[i] = chart.getColor()[i] + "," + chart.getpColor()[i] + "," + chart.getPattern()[i] + ","
					+ chart.getLineStrokes()[i];
		}
		return s;
	}

	private void setColumnLabel(GridBagLayout gridbag, JPanel jp) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		String[] name = { "Legend", "Color", "Pattern", "Line Stroke"/*,"Pattern Pic"*/ };
		JLabel jl = null;

		for (int j = 0; j < name.length; j++) {
			Box box = Box.createHorizontalBox();
			if (j == 0) {
				jl = CreateComponent.crtJLabel(name[j], name[j], 10, 2, new Dimension(200, 20));
			} else {
				//if (j == 2)
				//	box.add(new LegendHelpButton("patternList", LegendUtil.patternList));
				//else if (j == 3)
				//	box.add(new LegendHelpButton("strokeList", LegendUtil.strokeList));
				//else if(j==4) {
					//AP HERE
					/*BufferedImage[] image = new BufferedImage[legendList.length];
					image[i] = (BufferedImage) LegendUtil
							.getTexturePaint(Color.green, Color.darkGray, Integer.valueOf(legendList[i]).intValue(), 0)
							.getImage();
					box.add(new JComboBox<>())*/
				//}

				jl = CreateComponent.crtJLabel(name[j], name[j], 10, 2, new Dimension(80, 20));
			}
			jl.setFont(new Font("Verdana", 1, 12));
			box.add(jl);
			gridbag.setConstraints(box, c);
			jp.add(box);
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		jl = new JLabel("");
		gridbag.setConstraints(jl, c);
		jp.add(jl);
	}

	public class ColorModifyActionListener implements ActionListener {

		public ColorModifyActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			//eventApply = 1;
			jbColor = (JButton) e.getSource();
			//changeColLegend = legend[Integer.valueOf(jbColor.getName().trim())];
			ColorChooser4DynamicModifyColor cc =  new ColorChooser4DynamicModifyColor(chart, jbColor);
			//changeColPaint = cc.getPaint();
			//changeColColor = cc.getColor();
			chart.getPaint()[Integer.valueOf(jbColor.getName().trim())]=cc.getPaint();
			chart.setColor(cc.getColor(), Integer.valueOf(jbColor.getName().trim()));
		}
	}

	class MyDocumentListener implements DocumentListener {
		JTextField jtf;
		JButton jb;

		public MyDocumentListener(JTextField jtf, JButton jb) {
			this.jtf = jtf;
			this.jb = jb;
		}

		public void changedUpdate(DocumentEvent e) {
			setFldValue(e);
		}

		public void insertUpdate(DocumentEvent e) {
			setFldValue(e);
			eventApply = Integer.valueOf(jtf.getToolTipText().trim());
			jbColor = jb;
			jtfChanged = jtf;			
		}

		public void removeUpdate(DocumentEvent e) {
			setFldValue(e);
		}

		private void setFldValue(DocumentEvent e) {
			try {
				Document doc = e.getDocument();
				int vStrLen = doc.getLength();
				doc.getText(0, vStrLen);
				changeColLegend = legend[Integer.valueOf(jb.getName().trim())];
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	public JFreeChart getJfchart() {
		return jfchart;
	}

}
