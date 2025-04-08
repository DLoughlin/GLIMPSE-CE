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
package conversionUtil;

import java.awt.Dimension;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The class to handle a conversion between data types.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class DataConversion {
	public static String[] object2String(Object[] o) {
		String[] s = new String[o.length];
		for (int i = 0; i < o.length; i++)
			s[i] = ((String) o[i]).trim();
		return s;
	}

	public static double[][] String2Double(String[][] data) {
		double[][] d = new double[data.length][data[0].length];
		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d[i].length; j++)
				d[i][j] = Double.valueOf(data[i][j]).doubleValue();
		return d;
	}

	public static String[][] Double2String(double[][] data) {
		String[][] d = new String[data.length][data[0].length];
		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d[i].length; j++)
				d[i][j] = String.valueOf(data[i][j]);
		return d;
	}

	public static String[] Double2String(double[] data) {
		String[] d = new String[data.length];
		for (int i = 0; i < d.length; i++)
			d[i] = String.valueOf(data[i]);
		return d;
	}

	public static int[][] String2Int(String[][] data) {
		int[][] d = new int[data.length][data[0].length];
		for (int i = 0; i < d.length; i++)
			for (int j = 0; j < d[i].length; j++)
				d[i][j] = Integer.valueOf(data[i][j]).intValue();
		return d;
	}

	public static int[] String2Int(String[] data) {
		int[] d = new int[data.length];
		for (int i = 0; i < d.length; i++)
			d[i] = Integer.valueOf(data[i]).intValue();
		return d;
	}

	public static String[] int2String(int[] data) {
		String[] s = new String[data.length];
		for (int i = 0; i < s.length; i++)
			s[i] = String.valueOf(data[i]);
		return s;
	}

	public static double roundDouble(double d, int digit) {
		return Math.rint(d * Math.pow(10D, digit)) / Math.pow(10D, digit);
	}

	public static String getTheDay() {
		Calendar rightNow = Calendar.getInstance();
		java.util.Date d = rightNow.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String ds = sdf.format(d);
		return ds;
	}

	public static String getFormatDay(String s) {
		String ds = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (s != null && !s.equals("")) {
				java.util.Date d = sdf.parse(s);
				ds = sdf.format(d);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ds;
	}

	public static Dimension measureString(String s, Font font) {
		return new Dimension(font.getSize() * Character.SIZE * s.length(), font.getSize() * Character.SIZE);
	}

	public static Dimension measureString(String[] s, Font font) {
		int size = 5;
		for (String s1:s)
			size = Math.min(size, 5 * s1.length());
		return new Dimension(size<20?20:size, 20);
	}
}
