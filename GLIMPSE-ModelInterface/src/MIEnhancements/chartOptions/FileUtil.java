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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import conversionUtil.ArrayConversion;

/**
 * The class to handle utility functions for a file.
 * 
 *    Author			Action						Date		Flag
 *  ======================================================================= 			
 *	TWU				created 						1/2/2016	
 */

public class FileUtil {
	
//	public static String getSaveFilePathFromChooser(String desc, String extension) {
//		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(desc, extension);
//		//FileSystemView fsv = FileSystemView.getFileSystemView();
//		FileChooser chooser = new FileChooser();
//		chooser.setTitle("Select a file location");
//
//		chooser.setSelectedExtensionFilter(filter);
//
//		File selectedFile = chooser.showSaveDialog(null); //testing mod to see if can avoid blocking
//		
//		if (selectedFile==null) {
//			return null;
//		} else {
//			return selectedFile.getAbsolutePath();
//		}
//	}
		
	public static String getSaveFilePathFromChooser(String desc, String extension) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(desc, extension);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select a file location");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(filter);
		//int returnVal = chooser.showSaveDialog(null);
		int returnVal = chooser.showSaveDialog(chooser); //testing mod to see if can avoid blocking
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}

	public static String getSaveFilePathFromChooser() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select a file location");
		int returnVal = chooser.showSaveDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}
	
	public static String getOpenFilePathFromChooser() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select a file location");
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}

	public static File[] getOpenFileFromChooser() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select files location");
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFiles();
		else
			return null;
	}

	public static String getOpenDirectoryPathFromChooser() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select a directory location");
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}

	public static String[] getOpenDirectory(String path) {
		return (new File(path)).list();
	}

	public static String getOpenFilePathFromChooser(String nameFilter, String extension) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(nameFilter, extension);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Select a file location");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}

	public static File[] getOpenFileFromChooser(String nameFilter, String extension) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(nameFilter, extension);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Select a file location");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFiles();
		else
			return null;
	}

	public static String getOpenFilePathFromChooser(String desc, String extension[]) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(desc, extension);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		JFileChooser chooser = new JFileChooser(fsv);
		chooser.setDialogTitle("Select a file location");
		chooser.addChoosableFileFilter(filter);
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == 0)
			return chooser.getSelectedFile().getAbsolutePath();
		else
			return null;
	}

	public static DataInputStream initInFile(String path) {
		DataInputStream dis = null;
		FileInputStream fis = null;

		try {
			new File(path).createNewFile();
			fis = new FileInputStream(path);
			dis = new DataInputStream(fis);
		} catch (FileNotFoundException fnf) {
			System.out.println(fnf.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dis;
	}

	public static String[] parseFile2RevArray(LineNumberReader reader) {
		ArrayList<String> as = new ArrayList<String>();
		String lineString = null;
		try {
			lineString = reader.readLine();
			while (lineString != null && !lineString.equals("")) {
				as.add(lineString.trim());
				lineString = reader.readLine();
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return ArrayConversion.array2to1Conversion(ArrayConversion
				.arrayDimReverse(ArrayConversion.array1to2Conversion(as.toArray(new String[as.size()]))));
	}

	public static String[] parseFile2Array(LineNumberReader reader) {
		ArrayList<String> as = new ArrayList<String>();
		String lineString = null;
		try {
			lineString = reader.readLine();
			lineString = reader.readLine();
			for (lineString = reader.readLine(); lineString != null
					&& !lineString.equals(""); lineString = reader.readLine())
				as.add(lineString.trim());

		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return (String[]) as.toArray(new String[as.size()]);
	}

	public static String[] parseFile2Array(LineNumberReader reader, int lineSkip) {
		ArrayList<String> as = new ArrayList<String>();
		String lineString = null;
		try {
			for (int i = 0; i < lineSkip; i++)
				lineString = reader.readLine();

			lineString = reader.readLine();
			as.add(lineString.trim());
			lineString = reader.readLine();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return (String[]) as.toArray(new String[as.size()]);
	}

	public static String[] parseFile2Array(LineNumberReader reader, int lineSkip, int markLine) {
		ArrayList<String> as = new ArrayList<String>();
		String lineRead = null;
		try {
			reader.mark(markLine);
			for (int k = markLine; (lineRead = reader.readLine()) != null && k >= markLine; k++) {
				if (k >= markLine + 50)
					break;
				as.add(lineRead.trim());
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return (String[]) as.toArray(new String[as.size()]);
	}

	public static void mark(LineNumberReader reader, int m) throws Exception {
		reader.mark(m);
	}

	public static String[] parseFileHeader(LineNumberReader reader) {
		ArrayList<?> as = new ArrayList<Object>();
		String lineString = null;
		try {
			lineString = reader.readLine();
			String h[] = lineString.split(",");
			System.out
					.println((new StringBuilder(String.valueOf(h.length))).append("  ").append(lineString).toString());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return (String[]) as.toArray(new String[as.size()]);
	}

	public static String[] parseLine2Array(String line, String expression) {
		return line.split(expression);
	}

	public static FileOutputStream initOutFile(String path) {
		FileOutputStream fos = null;
		try {
			if ((new File(path)).exists())
				(new File(path)).delete();
			fos = new FileOutputStream(path);
		} catch (FileNotFoundException fnf) {
			System.out.println(fnf.getMessage());
		}
		return fos;
	}

	public static FileOutputStream initOutFile(File file) {
		FileOutputStream fos = null;
		try {
			if (file.exists())
				file.delete();
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException fnf) {
			System.out.println(fnf.getMessage());
		}
		return fos;
	}

	public static FileOutputStream initOutFile(String path, boolean append) {
		FileOutputStream fos = null;
		try {
			if (new File(path).exists())
				if (!append)
					new File(path).delete();
			fos = new FileOutputStream(path, append);
		} catch (FileNotFoundException fnf) {
			System.out.println(fnf.getMessage());
		}
		return fos;
	}

	public static void writetofile(FileOutputStream fos, String writestr) {
		try {
			fos.write(writestr.getBytes());
			fos.close();
		} catch (IOException ioe) {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String[] arrayDimReverseWrite(String[] in) {
		String[][] s1 = ArrayConversion.array1to2Conversion(in);
		String[] out = new String[in[0].split(",").length];

		for (int i = 0; i < s1[0].length; i++) {
			String s = "";
			for (int j = 0; j < s1.length; j++) {
				s = s + "," + s1[j][i];
			}
			out[i] = s.substring(1);
		}
		return out;
	}

	public static void writetofile(FileOutputStream fos, String[] writestr) {
		try {
			for (int i = 0; i < writestr.length; i++) {
				fos.write(writestr[i].getBytes());
				fos.write("\n".getBytes());
			}
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}

	public static ImageIcon getIcon(String s) {
		ImageIcon icon = null;
		URL imgURL = null;
		try {
			imgURL = new URL(s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (imgURL != null)
			icon = new ImageIcon(imgURL);
		else
			System.err.println((new StringBuilder("Couldn't find file: ")).append(s).toString());
		return icon;
	}

}
