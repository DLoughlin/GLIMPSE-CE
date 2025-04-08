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

import java.awt.Color;
import java.awt.Font;
import java.awt.TexturePaint;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;

import chartOptions.FileUtil;
import listener.ChartTitleChangeListener;

/**
 * The base class for all JFreeChart. Subclasses are divided into Category and
 * XY JFreeChart. It holds meta data (selection criteria), legend properties,
 * and basic chart information.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class Chart {
	// basic information
	protected JFreeChart chart;
	protected String chartClassName;
	protected String[] axis_name_unit;
	protected String graphName;
	protected String[] titles;
	protected String chartColumn;
	protected String chartRow;
	// meta data
	protected String meta;
	protected String metaCol;
	// legend information
	private String path;
	protected String legend;
	protected TexturePaint[] paint;
	protected int[] color;
	protected int[] pColor;
	protected int[] pattern;
	protected int[] lineStrokes;
	// function applied
	protected int relativeColIndex;
	protected Map<String, Marker> markerMap = new HashMap<String, Marker>();
	protected String[][] annotationText;
	protected boolean showLineAndShape;
	protected boolean debug = false;
	
	public LegendTitle myLegend=null;
	
	private HashMap<String,String> unitsLookup=null;

	// empty chart with description
	public Chart(String[] titles) {
		this.titles = titles.clone();
	}
	
	public void setUnitsLookup(HashMap<String,String> unitsLookup) {
		this.unitsLookup=unitsLookup;
	}
	
	public HashMap<String,String>  getUnitsLookup() {
		return this.unitsLookup;
	}
	
	public Chart(String path, String graphName, String meta, String[] titles, String[] axisName_unit, String legend,
			int[] color, int[] pColor, int[] pattern, int[] lineStrokes, String[][] annotationText,
			boolean ShowLineAndShape) {//, int thumbnailPos) {

		this.legend = legend;
		this.color = color == null ? null : color.clone();
		initLegendPattern(pColor, pattern, lineStrokes);
		this.showLineAndShape = ShowLineAndShape;
		init(path, graphName, meta, titles, axisName_unit, annotationText);
	}

	public Chart(String path, String graphName, String meta, String[] titles, String[] axisName_unit, String legend,
			String[][] annotationText) {

		this.legend = legend;
		init(path, graphName, meta, titles, axisName_unit, annotationText);
	}

	protected void initLegendPattern(int[] pColor, int[] pattern, int[] lineStrokes) {
		this.pColor = pColor == null ? this.color.clone() : pColor.clone();
		this.pattern = pattern == null ? null : pattern.clone();
		this.lineStrokes = lineStrokes == null ? null : lineStrokes.clone();

		if (pattern == null) {
			this.pattern = new int[this.pColor.length];
			for (int i = 0; i < this.pattern.length; i++)
				this.pattern[i] = 0;
		}

		if (lineStrokes == null) {
			this.lineStrokes = new int[this.pColor.length];
			for (int i = 0; i < this.lineStrokes.length; i++)
				this.lineStrokes[i] = 5;
		}
	}

	private void init(String path, String graphName, String meta, String[] titles, String[] axisName_unit,
			String[][] annotationText) {

		this.path = path;
		try {
		this.meta = meta.split("\\|")[0];
		this.metaCol = meta.split("\\|")[1];
		} catch(Exception e) {
			System.out.println("error processing meta: "+meta+" in init in Chart.java");
		}
		if (debug)
			System.out.println("Chart::init:meta: " + this.meta + " col: " + metaCol + "  " + meta);
		this.graphName = graphName;
		this.titles = titles.clone();
		this.axis_name_unit = (axisName_unit == null ? null : axisName_unit.clone());
		this.annotationText = annotationText == null ? null : annotationText.clone();

		if (debug)
			System.out.println("Chart::init:graphName: " + graphName+" legend: "+legend);
	}

	/**
	 * Get legend information from a property file, then store in CHart object.
	 *
	 * @param legends
	 *            legend label of a JFreeChart ({@code null} not permitted).
	 */
    //YD edits, Jan-2025
	protected void getlegendInfo(String[] legends) {
		if (path == null)
			return;
		// read in data
		Object[] temp = legendInfoFromProperties(path);
		if (temp.length == 0)
			return;
		String[] tempStr = new String[temp.length];
		String[] queryStr = new String[temp.length];
		tempStr = readLegendItemsFromProperties();
		queryStr = readQueryInfoFromProperties();
		String queryNameForChart = (String)"\"" + chart.getTitle().getText()+ "\"" ;
		
		//System.out.println("Chart::getlegendInfo:legend:check tempStr length vs legends length * " + tempStr.length +":"+legends.length);   
		// store local
		for (int i = 0; i < legends.length; i++) {
			int idx = Arrays.asList(tempStr).indexOf(legends[i].trim());
			int idx_last = Arrays.asList(tempStr).lastIndexOf(legends[i].trim());

			String[] o = new String[4];
			if (idx > -1 & idx == idx_last ) {
				String queryNameInLine =Arrays.asList(queryStr).get(idx);
				if (queryNameInLine.equals(queryNameForChart)){
					o = ((String) temp[idx]).split("=")[1].split(",");
					color[i] = Integer.valueOf(o[0].trim());
					pColor[i] = Integer.valueOf(o[1].trim());
					pattern[i] = Integer.valueOf(o[2].trim());
					lineStrokes[i] = Integer.valueOf(o[3].trim());
					if (debug)
						System.out.println("Chart::getlegendInfo:legend: " + legends[i] + " color: " + color[i]
							+ " pattern: " + pattern[i] + " lineStrokes: " + lineStrokes[i]);
				}else if(queryNameInLine.equals("*")) {
					o = ((String) temp[idx]).split("=")[1].split(",");
					color[i] = Integer.valueOf(o[0].trim());
					pColor[i] = Integer.valueOf(o[1].trim());
					pattern[i] = Integer.valueOf(o[2].trim());
					lineStrokes[i] = Integer.valueOf(o[3].trim());
				}
			}else if(idx > -1 & idx != idx_last) {
				//System.out.println("Chart::getlegendInfo:legend:this legend occurs more than once in file: " + legends[i].trim()); 
				for (int idxN = idx; idxN <= idx_last;idxN++) {			
					String queryNameAtIdx = Arrays.asList(queryStr).get(idxN);
					String legendInFile = Arrays.asList(tempStr).get(idxN);
					if (queryNameAtIdx.equals(queryNameForChart) & legendInFile.equals(legends[i].trim())) {
						o = ((String) temp[idxN]).split("=")[1].split(",");
						color[i] = Integer.valueOf(o[0].trim());
						pColor[i] = Integer.valueOf(o[1].trim());
						pattern[i] = Integer.valueOf(o[2].trim());
						lineStrokes[i] = Integer.valueOf(o[3].trim());
						
					}else if (queryNameAtIdx.equals("*") & legendInFile.equals(legends[i].trim())) {
						//the global line contains the legend item
						o = ((String) temp[idxN]).split("=")[1].split(",");
						color[i] = Integer.valueOf(o[0].trim());
						pColor[i] = Integer.valueOf(o[1].trim());
						pattern[i] = Integer.valueOf(o[2].trim());
						lineStrokes[i] = Integer.valueOf(o[3].trim());
					}
				}
			}//outer check idx if loop end
			
		}// for legends loop end
	}

	/**
	 * Store legend information to a property file.
	 *
	 * @param legends
	 *            legend label of a JFreeChart ({@code null} not permitted).
	 * @param color
	 *            colors of each legend
	 */

	public void storelegendInfo(String[] legends, String[] color) {

		if (path == null)
			return;

		if (debug)
			System.out.println("Chart::storelegendInfo:path: " + path);

		ArrayList<String> tempstr = legendInfoFromProperties(legends, color);
		String[] writestr = tempstr.toArray(new String[0]);
		FileOutputStream fos = FileUtil.initOutFile(path);
		FileUtil.writetofile(fos, writestr);
	}
	
	//YD edits, Jan-2025
	public void storelegendInfoGlobal(String[] legends, String[] color) {

		if (path == null) {
			System.out.println("Null path to legend color file");
			return;
		}
		//read in all legends so we wont' duplicate
		List<String> lines=null;
		try {
            lines = Files.readAllLines(Paths.get(path));
            
        } catch (IOException e) {
            System.out.println("Could not read in any previous color data.");
        }
			
		ArrayList<Boolean> done=new ArrayList<Boolean>();
		for(int i=0;i<legends.length;i++) {
			done.add(false);
		}
		
		//we want to only add the values to end if they are not in there
		//check each item as we go
		String firstPart=null;
		String queryNameForChart = (String)"\"" + chart.getTitle().getText()+ "\"" ;
		
		for(int i=0;i<lines.size();i++) {
			//this is local, so we can skip star ones
			
			if(!lines.get(i).startsWith("*")) {
				//also need to support backwards compatibility (no ":")
				if(lines.get(i).contains(":")) {
					continue;
				}
			}
			
			//chop up the name so we can check the exact query name
			//two cases, with start and without
			if(lines.get(i).contains("*")) {
				firstPart=lines.get(i).split(":")[1];
			}else {
				firstPart=lines.get(i);
			}
			
			//either way, peel off colors
			firstPart=firstPart.split("=")[0];
			
			
			//now check the new ones we are saving
			for(int j=0;j<legends.length;j++) {
				//want an exact match
				if(firstPart.compareTo(legends[j])==0) {
					//need to replace this one with new color value
					lines.set(i, "*:"+legends[j].trim() + "=" + color[j]);
					//also mark it complete so we don't add at end
					done.set(j,true);
					//no need to loop more, this line is handled
					break;
				}
			}
		}
		
		//now add all the ones that haven't been edited
		for(int i=0;i<done.size();i++) {
			if(!done.get(i)) {
				lines.add(queryNameForChart+":"+legends[i].trim() + "=" + color[i]);
			}
		}
		
		
		if (debug)
			System.out.println("Chart::storelegendInfoGlobal:path: " + path);

		//ArrayList<String> tempstr = legendInfoFromProperties(legends, color);
		/*ArrayList<String> tempstr = new ArrayList<String>();
		for (int i = 0; i < legends.length; i++)
			tempstr.add(legends[i].trim() + "=" + color[i]);
		for (int i=0;i<tempstr.size();i++) {
			String item = tempstr.get(i);
			tempstr.set(i, "*:"+item);
		}*/
		String[] writestr = lines.toArray(new String[0]);
		//System.out.println("Chart::storelegendInfoGlobal:Size " + writestr.length +"the first one:"+ writestr[0]);
		FileOutputStream fos = FileUtil.initOutFile(path,false);
		FileUtil.writetofile(fos, writestr);
	}
	
	//YD edits, Feb-2025
	public void storelegendInfoLocal(String[] legends, String[] color) {

		if (path == null) {
			System.out.println("Null path to legend color file");
			return;
		}
		//read in all legends so we wont' duplicate
		List<String> lines=null;
		try {
            lines = Files.readAllLines(Paths.get(path));
            
        } catch (IOException e) {
            System.out.println("Could not read in any previous color data.");
        }
			
		ArrayList<Boolean> done=new ArrayList<Boolean>();
		for(int i=0;i<legends.length;i++) {
			done.add(false);
		}
		
		//we want to only add the values to end if they are not in there
		//check each item as we go
		String firstPart=null;
		String secondPart=null;
		String queryNameForChart = (String)"\"" + chart.getTitle().getText()+ "\"" ;
		
		for(int i=0;i<lines.size();i++) {
			//this is local, so we can skip star ones:
			if(lines.get(i).startsWith("*")) {
				continue;
			}
			//for backwards compatibility
			if(!lines.get(i).contains(":")) {
				continue;
			}
			//chop up the name so we can check the exact query name
			firstPart=lines.get(i).split(":")[0];
			secondPart=lines.get(i).split(":")[1].split("=")[0];
			//now check the new ones we are saving
			for(int j=0;j<legends.length;j++) {
				//want an exact match
				if(secondPart.compareTo(legends[j])==0 && firstPart.compareTo(queryNameForChart)==0) {
					//need to replace this one with new color value
					lines.set(i, queryNameForChart+":"+legends[j].trim() + "=" + color[j]);
					//also mark it complete so we don't add at end
					done.set(j,true);
					//no need to loop more, this line is handled
					break;
				}
			}
		}
		
		//now add all the ones that haven't been edited
		for(int i=0;i<done.size();i++) {
			if(!done.get(i)) {
				lines.add(queryNameForChart+":"+legends[i].trim() + "=" + color[i]);
			}
		}

		if (debug)
			System.out.println("Chart::storelegendInfoLocal:path: " + path);
		//System.out.println("Chart::storelegendInfoLocal:chart title: " + chart.getTitle().getText());
		/*ArrayList<String> tempstr = new ArrayList<String>();
		for (int i = 0; i < legends.length; i++)
			tempstr.add(legends[i].trim() + "=" + color[i]);
		//System.out.println("Chart::storelegendInfoLocal:check tempstr size: " + tempstr.size());
		for (int i=0;i<tempstr.size();i++) {
			String item = tempstr.get(i);
			tempstr.set(i, queryNameForChart+":"+item);
		}*/
		String[] writestr = lines.toArray(new String[0]);
		FileOutputStream fos = FileUtil.initOutFile(path,false);  //do not append
		FileUtil.writetofile(fos, writestr);
	}

	/**
	 * Get legend information from input legend and color.
	 *
	 * @param legends
	 *            legend label of a JFreeChart ({@code null} not permitted).
	 * @param color
	 *            colors of each legend.
	 * @return An array of list legend data string
	 */
    //YD edits, Jan-2025
	public ArrayList<String> legendInfoFromProperties(String[] legends, String[] color) {
		ArrayList<String> tempstrAl = new ArrayList<String>();
		Object[] temp = legendInfoFromProperties(path);
		String queryNameForChart = (String)"\"" + chart.getTitle().getText()+ "\"" ;
		if (temp.length == 0) // file is empty
			for (int i = 0; i < legends.length; i++)
				tempstrAl.add(legends[i].trim() + "=" + color[i]);//.replaceAll(" ", "_")
		else {
			for (int i = 0; i < temp.length; i++) {
				
				String myLine = (String)temp[i];
				if (myLine.contains(":")&!myLine.contains("*")) {
					//System.out.println("Chart::legendInfoFromProperties:this line is for local!!!");
					String queryNameInFile = ((String) temp[i]).split(":")[0].trim();
					//System.out.println("Chart::legendInfoFromProperties:queryName is:" +queryNameInFile);
					if (queryNameInFile.equals(queryNameForChart)) {  //need more coding to consider other legend items
					System.out.println("Chart::legendInfoFromProperties:queryName in File and Chart matched!!!");	
					String secondPart = ((String) temp[i]).split(":")[1].trim();
					}else {
					// do nothing, skip those lines
					}
				}else {
					 System.out.println("Chart::legendInfoFromProperties:inside else loop now...");	
					 String firstPart = ((String) temp[i]).split("=")[0];
					 String colorPart = ((String) temp[i]).split("=")[1].trim();
					  if (firstPart.startsWith("*:")) {
						 String keyInLine = firstPart.replace("*:", "");
						 //System.out.println("Chart::legendInfoFromProperties:check colorPart in file:"+colorPart);
						 int idx = legendResourceExist(legends, keyInLine);
					     if (idx > -1) {
					    	//only need to save to the file after the colors are changed 
					    	if (!color[idx].trim().equals(colorPart)) {
						    tempstrAl.add(legends[idx].trim() + "=" + color[idx]);
						  //System.out.println("Chart::legendInfoFromProperties:add this legend and this color:"+legends[idx]+":"+color[idx]);
					    	}
					     }else {
						   //System.out.println("Chart::legendInfoFromProperties:inside else loop now for this key:"+keyInLine);
						   tempstrAl.add((String) temp[i]);
					     }
					  }else {
						  //do nothing for now
						  //the line is not in the right format
					  }
				}//outer lf else loop end    
			}// for loop end
			String[] tempStr = new String[temp.length];
			tempStr = readLegendItemsFromProperties();

			for (int i = 0; i < legends.length; i++) {
				String key = legends[i].trim();
				if (!Arrays.asList(tempStr).contains(key))
					tempstrAl.add(key + "=" + color[i]);
			}
		}
		return tempstrAl;
	}
	
	//YD edits,Feb-2025
	private String[] readLegendItemsFromProperties() {
		Object[] temp = legendInfoFromProperties(path);
		String[] tempStr = new String[temp.length];
		
		for (int i = 0; i < temp.length; i++) {
			String myLine = (String)temp[i];
			if (myLine.contains(":")&!myLine.contains("*")) {
				String secondPart = ((String) temp[i]).split(":")[1].trim();
				tempStr[i] = ((String)secondPart).split("=")[0].trim();
				if (debug)
					System.out.println("Chart::readLegendItemsFromProperties:read a local line.");	
			}else {
			  String firstPart = ((String) temp[i]).split("=")[0].trim();
			  if (firstPart.startsWith("*:")) {
				 tempStr[i] = firstPart.replace("*:", "");
			  }else {
				  //this is for backwards compatibility with original format
				  tempStr[i] = firstPart;
			  }
			}
		}
		return tempStr;
	}
	
	private String[] readQueryInfoFromProperties() {
		Object[] temp = legendInfoFromProperties(path);
		String[] queryStr = new String[temp.length];
		for (int i = 0; i < temp.length; i++) {
			String myLine = (String)temp[i];
			if (myLine.contains(":")&!myLine.contains("*")) {
				String queryNameInFile = ((String) temp[i]).split(":")[0].trim();
				queryStr[i]= queryNameInFile;
				
			}else {
			String firstPart = ((String) temp[i]).split("=")[0].trim();
			  if (firstPart.startsWith("*:")) {
				 queryStr[i] = "*";
			  }
			  else {
					// this  the case of the older formatted files:
					// global: a oil=-43691,-43691,0,0, in this case, just use global form "*:
					queryStr[i] = "*";
					// this is will only be needed until all old color files are replaced.

				}
			}
		}
		return queryStr;
	}


	/**
	 * Get legend information from a property file.
	 *
	 * @param path
	 *            legend data property file location ({@code null} not
	 *            permitted).
	 * @return An array of legend data
	 */

	public Object[] legendInfoFromProperties(String path) {
		LineNumberReader lineReader = null;
		Object[] temp = null;
		try {
			DataInputStream dis = FileUtil.initInFile(path);
			lineReader = new LineNumberReader(new InputStreamReader(dis));
			temp = lineReader.lines().toArray();
			lineReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Legend info not read (FNF): "+e.toString());
		} catch (IOException e) {
			System.out.println("Legend info not read (e): "+e.toString());
		}
		return temp;
	}

	protected int legendResourceExist(String[] legends, String key) {
		for (int i = 0; i < legends.length; i++)
			legends[i] = legends[i].trim();
		int idx = Arrays.asList(legends).indexOf(key);
		return idx;
	}

	/**
	 * Build texture paint with pattern and stroke information and store in
	 * Chart object.
	 * 
	 */

	protected void buildPaint() {
		if (color == null)
			return;

		paint = new TexturePaint[color.length];
		for (int i = paint.length - 1; i > -1; i--) {
			paint[i] = LegendUtil.getTexturePaint(new Color(color[i]), new Color(pColor[i]), pattern[i],
					lineStrokes[i]);// 0);
			if (debug)
				System.out
						.println("Chart::buildPaint:pattern: " + pattern[i] + " line: " + lineStrokes[i] + " i: " + i);
		}
	}

	protected String verifyAxisName_unit(int i) {
		String axis = "";
		if (axis_name_unit != null)
			if (i < axis_name_unit.length)
				axis = axis_name_unit[i];
		return axis;
	}

	protected void setChartProperty() {

		if (markerMap != null)
			MarkerUtil.createMarker(chart, markerMap);
		ChartUtil.setSubTitle(chart, titles);
	
		//chart.getLegend().setVisible(false);
		int lMax = 0;
		String[] temp = legend.split(",");
		for (int i = 0; i < temp.length; i++) {
			lMax=Math.max(lMax, temp[i].length());
		}
		myLegend=chart.getLegend();
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		if (legend.split(",").length < 15 && lMax < 30) {
			
			//chart.getLegend().setVisible(true);
		}
		else {
			
			chart.removeLegend();
			//chart.getLegend().setPosition(RectangleEdge.BOTTOM);
			//chart.getLegend().setVisible(false);
			
		}


		
		setTitleChangeListener();
		ChartUtils.applyCurrentTheme(chart);
		//chart.removeLegend();
		
	}

	protected void setTitleChangeListener() {
		ChartTitleChangeListener mctcl = new ChartTitleChangeListener(this);
		if (chartClassName.contains("Line"))
			chart.getSubtitle(0).addChangeListener(mctcl);
		else
			chart.getTitle().addChangeListener(mctcl);
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setColor(int[] color) {
		this.color = color.clone();
	}

	public void setColor(int color, int idx) {
		this.color[idx] = color;
	}
	
	public void setPattern(int pattern, int idx) {
		this.pattern[idx] = pattern;
	}

	public void setPattern(int[] pattern) {
		this.pattern = pattern.clone();
	}
	
	public void setLineStrokes(int[] lineStrokes) {
		this.lineStrokes = lineStrokes.clone();
	}

	public JFreeChart getChart() {
		return chart;
	}

	public String[] getTitles() {
		return titles;
	}

	public String[] getAxis_name_unit() {
		return axis_name_unit;
	}

	public String getChartClassName() {
		return chartClassName;
	}

	public String getGraphName() {
		return graphName;
	}

	public String getMeta() {
		return meta;
	}

	public String getLegend() {
		return legend;
	}

	public int[] getColor() {
		return color;
	}

	public int[] getPattern() {
		return pattern;
	}

	public int[] getLineStrokes() {
		return lineStrokes;
	}

	public int getRelativeColIndex() {
		return relativeColIndex;
	}

	public int[] getpColor() {
		return pColor;
	}

	public void setpColor(int[] pColor) {
		this.pColor = pColor.clone();
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public void setTitles(String[] titles) {
		this.titles = titles.clone();
	}

	public void setTitles(String title, int idx) {
		this.titles[idx] = title;
	}

	public TexturePaint[] getPaint() {
		return paint;
	}

	public void setPaint(TexturePaint[] paint) {
		this.paint = paint.clone();
	}

	public Map<String, Marker> getMarkerMap() {
		return markerMap;
	}

	public void setMarkerMap(Map<String, Marker> markerMap) {
		this.markerMap = markerMap;
	}

	public String[][] getAnnotationText() {
		return annotationText;
	}

	public void setAnnotationText(String[][] annotationText) {
		this.annotationText = annotationText.clone();
	}

	public void setChartClassName(String chartClassName) {
		this.chartClassName = chartClassName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isShowLineAndShape() {
		return showLineAndShape;
	}

	public void setShowLineAndShape(boolean showLineAndShape) {
		this.showLineAndShape = showLineAndShape;
	}

	public String getMetaCol() {
		return metaCol;
	}

	public String getChartColumn() {
		return chartColumn;
	}

	public String getChartRow() {
		return chartRow;
	}	
}
