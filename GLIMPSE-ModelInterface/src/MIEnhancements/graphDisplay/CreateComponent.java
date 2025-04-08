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
package graphDisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class CreateComponent {
	private static boolean debug = false;

	public static JDialog crtJDialog(String name) {
		JDialog dialog = new JDialog();
		dialog.setResizable(true);
		dialog.setTitle(name);
		dialog.setFocusable(true);

		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				JDialog dialog = (JDialog) e.getSource();
				if (debug)
					System.out.println("CreateComponent::crtJDialog:windowCloseing:name " + dialog.getTitle());
			}

			@Override
			public void windowOpened(WindowEvent e) {
				JDialog dialog = (JDialog) e.getSource();
				if (debug)
					System.out.println("CreateComponent::crtJDialog:windowOpened:name " + dialog.getTitle());
			}
		});

		dialog.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				JDialog dialog = (JDialog) e.getSource();
				if (e.getNewState() == WindowEvent.WINDOW_ACTIVATED)
					dialog.setVisible(true);
				else if (e.getNewState() == WindowEvent.WINDOW_DEACTIVATED)
					dialog.setVisible(false);
				if (debug)
					System.out.println("CreateComponent::crtJDialog:windowStateChanged:name " + dialog.getTitle());
			}
		});

		return dialog;
	}

	public static JTextField crtJTextField(String name, String text, int index) {
		JTextField Jtf = new JTextField(text);
		Jtf.setName(name);
		Jtf.setFont(new Font("Verdana", 0, 9));
		Jtf.getDocument().putProperty("colIndex", Integer.valueOf(index));
		Jtf.setMaximumSize(new Dimension(300, 20));
		Jtf.setMinimumSize(new Dimension(80, 20));
		Jtf.setEditable(true);
		Jtf.setDragEnabled(true);
		return Jtf;
	}

	public static JLabel crtJLabel(String name, String text, int fontSize) {
		JLabel jl = new JLabel(text);
		jl.setName(name);
		jl.setFont(new Font("Arial", 0, fontSize));
		return jl;
	}

	public static JLabel crtJLabel(String name, String text, int fontSize, int orintation, Dimension labSize) {
		JLabel jl = new JLabel(text, orintation);
		jl.setName(name);
		jl.setFont(new Font("Arial", 0, fontSize));
		return jl;
	}

	public static JList<?> dataList(String name, String data[], int selectionMode, Dimension listSize) {
		JList<?> list = new JList<Object>(data);
		list.setName(name);
		list.setFont(new Font("Verdana", 0, 10));
		list.setVisibleRowCount(3);
		list.setSelectionMode(selectionMode);
		return list;
	}

	public static JComboBox<?> dataCombo(String name, String data[], int selIndex, Dimension comboSize) {
		JComboBox<?> cb = new JComboBox<Object>(data);
		cb.setName(name);
		cb.setFont(new Font("Verdana", 0, 12));
		cb.setSize(comboSize);
		cb.setSelectedIndex(selIndex);
		return cb;
	}

	public static JButton crtJButton(String name, Dimension butSize) {
		JButton jb = new JButton(name);
		jb.setName(name);
		jb.setPreferredSize(butSize);
		return jb;
	}

	public static JButton crtJButton(String name, Color color) {
		JButton jb = new JButton();
		jb.setBackground(color);
		jb.setName(name);
		jb.setPreferredSize(new Dimension(80, 20));
		return jb;
	}

	public static JButton crtJButton(String name, ImageIcon icon) {
		JButton jb = new JButton(icon);
		jb.setName(name);
		return jb;
	}

	public static JRadioButton crtJButGroup(String name, boolean selected) {
		JRadioButton radio = new JRadioButton(name);
		radio.setName(name);
		radio.setSelected(selected);
		return radio;
	}

	public static JDialog crtDialog(JComponent component, String title) {
		JDialog dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setContentPane(component);
		dialog.pack();
		dialog.setPreferredSize(new Dimension(400, 300));
		dialog.setVisible(true);
		return dialog;
	}
}
