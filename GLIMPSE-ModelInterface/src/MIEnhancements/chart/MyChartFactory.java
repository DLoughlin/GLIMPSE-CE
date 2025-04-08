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
package chart;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * The class handle to create a interface to create a JFreeChart with all properties stored in Chart. 
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class MyChartFactory {
	private static boolean debug = false;
	
	public static Chart createChart(String className, String path, String graphName, String meta, 
			String[] titles, String[] axis_name_unit, String legend, int[] color, int[] pColor,
			int[] pattern, int[] lineStrokes, String[][] annotationText, 
			DefaultCategoryDataset dataset, int relativeColIndex, boolean ShowLineAndShape, String graphType)
			throws ClassNotFoundException {
		
		if(titles.length>=2 && titles[0]==null) {
			titles[0]=titles[1];
			titles[1]="";
		}

		Object[] o = {path, graphName, meta, titles, axis_name_unit, legend,  color, pColor,
				pattern, lineStrokes, annotationText, dataset, relativeColIndex, ShowLineAndShape,graphType };
		Class<?> t = Class.forName(className);
		if (debug)
			System.out.println("ChartFactory::createChart1:className: " + t.getName());
		Chart chart = (Chart) ChartUtil.creatNewInstance(t, o);
		System.runFinalization();

		return chart;
	}

	public static Chart createChart(String className, String path, String graphName, String meta, String[] titles,
			String[] axis_name_unit, String legend, int[] color, int[] pColor, int[] pattern, 
			int[] lineStrokes, String[][] annotationText, DefaultXYDataset dataset, 
			int relativeColIndex, boolean ShowLineAndShape)
			throws ClassNotFoundException {

		Object[] o = {path, graphName, meta, titles, axis_name_unit, legend,  color, pColor,
				pattern, lineStrokes, annotationText, dataset, relativeColIndex, ShowLineAndShape };
		Class<?> t = Class.forName(className);
		if (debug)
			System.out.println("ChartFactory::createChart2:className: " + t.getName());

		Chart chart = (Chart) ChartUtil.creatNewInstance(t, o);

		return chart;
	}

	// Single data set called from graphDisplayUtil
	// Dan: Step 3 - Creates a new instance of a chart
	public static Chart createChart(String className, String path, String graphName, String id, String[] titles,
			String[] axisName_unit, String legend, String column, String[][] annotationText, String[][] data,
			int relativeColIndex) throws ClassNotFoundException {

		Object[] o = { path, graphName, id.trim(), titles, axisName_unit, legend, column, annotationText, data,
				Integer.valueOf(relativeColIndex) };
		Class<?> t = Class.forName(className);
		if (debug)
			System.out.println("ChartFactory::createChart3:className: " + t.getName());
		return (Chart) ChartUtil.creatNewInstance(t, o);
	}

	// Single data set for Box and Whisker
	public static Chart createChart(String className, String path, String graphName, String id, String[] titles,
			String[] axisName_unit, String column, String[][] annotation, ArrayList<List<String[]>> data)
			throws ClassNotFoundException {

		Object[] o = { path, graphName, id, titles, axisName_unit, column, annotation, data };

		Class<?> t = Class.forName(className);
		if (debug)
			System.out.println("ChartFactory::createChart4:className: " + t.getName());
		return (Chart) ChartUtil.creatNewInstance(t, o);
	}
}
