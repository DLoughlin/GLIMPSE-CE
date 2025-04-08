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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TexturePaint;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import chart.Chart;
import chart.LegendUtil;

/**
 * The class to handle legend color change. Referenced classes of package
 * graphDisplay: AChartDisplay, DataPanel
 * 
 * Author Action Date Flag
 * ======================================================================= TWU
 * created 1/2/2016
 */

public class ColorChooser4DynamicModifyColor extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	private JColorChooser tcc;
	private Chart chart; // event chart
	private JButton jb;
	private int keySeries;
	private TexturePaint paint;
	private int color;
	private JDialog dialog;
	protected boolean debug = false;

	public ColorChooser4DynamicModifyColor(Chart chart, JButton jb) {
		super(new BorderLayout());
		this.chart = chart;
		keySeries = Integer.valueOf(jb.getName().trim()).intValue();
		this.jb = jb;
		tcc = new JColorChooser();
		tcc.getSelectionModel().addChangeListener(this);
		tcc.setBorder(BorderFactory.createTitledBorder("Choose Graph Color"));
		tcc.remove(1);
		add(tcc, "Last");
		Object[] options = { "Ok" };
		JOptionPane pane = new JOptionPane(tcc, -1, 0, null, options, options[0]);
		dialog = pane.createDialog("Select a legend Paint");
		dialog.setLayout(null);
		dialog.setResizable(true);
		dialog.setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
		if (chart == null)
			return;

		if (debug)
			System.out.println(
					"ColorChooser4DynamicModifyColor::stateChanged:chartColor: " + Arrays.toString(chart.getColor()));
		color = tcc.getColor().getRGB();
		paint = LegendUtil.getTexturePaint(tcc.getColor(), new Color(chart.getpColor()[keySeries]),
				chart.getPattern()[keySeries], chart.getLineStrokes()[keySeries]);

		SetModifyChanges.updateButton(jb, paint);
	}

	public TexturePaint getPaint() {
		return paint;
	}

	public int getColor() {
		return color;
	}

}
