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
package glimpseUtil;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class GLIMPSEFiles {

	public GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
	public GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

	public final static GLIMPSEFiles instance = new GLIMPSEFiles();

	public ArrayList<String> optionsFileContent = new ArrayList<>();
	public ArrayList<String> glimpseCSVColumnsFileContent = new ArrayList<>();
	ArrayList<String> glimpseXMLHeadersFileContent = new ArrayList<>();
	public ArrayList<String> glimpseTechBoundFileContent = new ArrayList<>();
	ArrayList<String> gCamConfigurationTemplateFileContent = new ArrayList<>();
	ArrayList<String> resPolicyTemplateFileContent = new ArrayList<>();
	ArrayList<String> monetaryConversionsFileContent = new ArrayList<>();

	public GLIMPSEFiles() {

	}

	public static GLIMPSEFiles getInstance() {
		return instance;
	}

	public void init(GLIMPSEUtils u, GLIMPSEVariables v, GLIMPSEStyles s, GLIMPSEFiles f) {
		utils = u;
		vars = v;
		// styles = s;
		// files = f;
	}

	public boolean moveFile(Path source, Path destination) {
		boolean b = false;
		try {
			// Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			// Files.deleteIfExists(source);
			Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
			b = true;
		} catch (Exception e) {
			System.out.println("Error moving file:" + source.getFileName() + " ... " + e);
		}
		return b;
	}

	public boolean copyFile(Path source, Path destination) {
		boolean b = false;
		try {
			Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			b = true;
		} catch (Exception e) {
			;
		}
		return b;
	}

	public void loadFiles() {
		try {
			glimpseXMLHeadersFileContent = getStringArrayFromFile(vars.getXmlHeaderFile(), "#");
		} catch (Exception e) {
			System.out.println("\nError opening files needed by GLIMPSE.");
			System.out.println("Exception " + e);
		}
		try {
			glimpseTechBoundFileContent = getStringArrayFromFile(vars.getTchBndListFile(), "#");
		} catch (Exception e) {
			System.out.println("Error opening files needed by GLIMPSE.");
			System.out.println("Exception " + e);
		}

		try {
			gCamConfigurationTemplateFileContent = getStringArrayFromFile(vars.getConfigurationTemplate(), "#");
		} catch (Exception e) {
			System.out.println("Error opening files needed by GLIMPSE.");
			System.out.println("Exception " + e);
		}

		/*
		 * try { monetaryConversionsFileContent =
		 * getStringArrayFromFile(vars.getMonetaryConversionsFile(), "#"); } catch
		 * (Exception e) { System.out.println("Error opening files needed by GLIMPSE.");
		 * System.out.println("Exception " + e); }
		 */
	}

	public ArrayList<String> getStringArrayWithPrefix(String filename, String prefix) {
		// System.out.println("loading data from file: " + filename);
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (String line; (line = br.readLine()) != null;) {
				line = line.trim();
				if (line.length() > 0) {
					if (line.startsWith(prefix)) {
						arrayList.add(line);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			String msg = "Error reading file " + filename + ". Attempting to continue.";
			String msg2 = "   exception: " + e;
			System.out.println(msg + msg2);
			//utils.warningMessage(msg);
		}
		return arrayList;
	}

	public ArrayList<String> getStringArrayWithPrefix(String filename, String[] prefixes) {
		// System.out.println("loading data from file: " + filename);
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (String line; (line = br.readLine()) != null;) {
				line = line.trim();
				if (line.length() > 0) {
					for (int i = 0; i < prefixes.length; i++) {
						if (line.startsWith(prefixes[i])) {
							arrayList.add(line);
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			String msg = "Error reading file " + filename + ". Attempting to continue.";
			String msg2 = "   exception:" + e;
			System.out.println(msg + msg2);
			//utils.warningMessage(msg);
		}
		return arrayList;
	}

	public ArrayList<String> getStringArrayFromFile(String filename, String commentChar) {
		// System.out.println("loading data from file: " + filename);
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (String line; (line = br.readLine()) != null;) {
				line = line.trim();
				if (line.length() > 0) {
					if ((commentChar == null) || (!line.startsWith(commentChar))) {
						arrayList.add(line);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			String msg = "Error reading file " + filename + ". Attempting to continue.";
			String msg2 = "   exception:" + e;
			System.out.println(msg + msg2);
			//utils.warningMessage(msg);//Dan:Disabled. These messages were annoying
		}
		return arrayList;
	}

	public String getLineXFromFile(String filename, int x, String comment) {
		System.out.println("loading line " + x + " from file: " + filename);
		String s = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < x; i++) {
				s = br.readLine();
				if (s.startsWith(comment))
					i -= 1;
			}
			br.close();
		} catch (Exception e) {
			String msg = "Error reading file " + filename + ". Attempting to continue.";
			String msg2 = "   exception:" + e;
			System.out.println(msg + msg2);
			//utils.warningMessage(msg);
		}
		return s;
	}

	public ArrayList<String> loadFileListFromFile(String filename, String typeString) {
		ArrayList<String> fileList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null;) {
				if ((line.indexOf(typeString) < 0) && (line.length() > 0)) {
					String fileInList = line.trim();
					try {
						File f = new File(fileInList);
						if (/* f.exists() && */!f.isDirectory()) {

							fileList.add(fileInList);
						}
					} catch (Exception e1) {
						System.out.println(fileInList + " is not a file and will not be added to the list. " + e1);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			utils.warningMessage("Problem reading component file " + filename + " to determine type.");
			System.out.println("Error reading scenario component file to determine typ:" + e);

		}
		return fileList;
	}

	public void showFileInTextEditor(String filename) {
		GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
		
		File f=new File(filename);
		if (!f.exists()) {
			String msg="File does not exist:"+filename;
			utils.warningMessage(msg);
			return;
		}
		
		String cmd = vars.get("textEditor") + " " + filename;

		try {
			java.lang.Runtime rt = java.lang.Runtime.getRuntime();
			rt.exec(cmd); 

		} catch (Exception e) {
			utils.warningMessage("Could not use text editor specified in options file. Using system default.");
			System.out.println("Error trying to open file to view with editor.");
			System.out.println("   file: " + filename);
			System.out.println("   editor: " + vars.getTextEditor());
			System.out.println("Error: " + e);
			try {
				File file = new File(filename);
				java.awt.Desktop.getDesktop().edit(file);

			} catch (Exception e1) {
				utils.warningMessage("Problem trying to open file with system text editor.");
				System.out.println("Error trying to open file to view with system text editor.");
				System.out.println("Error: " + e1);
			}
		}
	}

	public void showFileInXmlEditor(String filename) {
		GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
		String cmd = vars.get("xmlEditor") + " " + filename;

		try {
			java.lang.Runtime rt = java.lang.Runtime.getRuntime();
			rt.exec(cmd);
		} catch (Exception e) {
			utils.warningMessage("Could not use text editor specified in options file. Using system default.");
			System.out.println("Error trying to open file to view with editor.");
			System.out.println("   file: " + filename);
			System.out.println("   editor: " + vars.getTextEditor());
			System.out.println("Error: " + e);
			try {
				File file = new File(filename);
				java.awt.Desktop.getDesktop().edit(file);

			} catch (Exception e1) {
				utils.warningMessage("Problem trying to open file with system text editor.");
				System.out.println("Error trying to open file to view with system text editor.");
				System.out.println("Error: " + e1);
			}
		}
	}

	public ArrayList<String[]> loadKeyValuePairsFromFile(String filename, String delimiter) {
		ArrayList<String[]> keywordValuePairs = new ArrayList<String[]>();

		try {
			File file = new File(filename);
			Scanner scan = new Scanner(file);
			scan.useDelimiter(delimiter);

			while (scan.hasNext()) {
				int n = -1;
				String str = scan.nextLine();
				if (!str.startsWith("#")) {
					if (str.indexOf(delimiter) > -1) {
						n = str.indexOf(delimiter);
						String[] s = new String[2];
						if (n > -0) {

							s[0] = str.substring(0, n).trim();
							s[1] = str.substring(n + 1).trim();
							keywordValuePairs.add(s);

						}
					}
				}
			}
			scan.close();
		} catch (Exception e) {
			utils.warningMessage("Problem reading keyword-value pairs.");
			System.out.println("Error reading keyword-value pairs:" + e);
			utils.exitOnException();
		}

		return keywordValuePairs;
	}

	public void saveFile(String content, String filename) {
		File file = new File(filename);
		saveFile(content, file);
	}

	public void saveFile(ArrayList<String> orig_content, String filename) {
		File file = new File(filename);
		try {
			PrintStream filestream = new PrintStream(file);

			for (int i = 0; i < orig_content.size(); i++) {

				String str = orig_content.get(i);

				filestream.print(str + "\r\n");
			}

			filestream.close();

		} catch (Exception e) {
			utils.warningMessage("Problem writing file to disk.");
			System.out.println("Problem writing file " + filename);
			System.out.println("Error:" + e);
			System.out.println("Trying to continue...");
		}
	}

	public void saveFile(String content, File file) {

		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			utils.warningMessage("Error writing file:" + ex);
			System.out.println("Error writing file");
		}

	}

	public void openFileExplorer(String filename) {
		File file = new File(filename);

		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(file);
		} catch (Exception e1) {
			utils.warningMessage("Problem opening system file browser.");
			System.out.println("Error opening system file browser.");
			e1.printStackTrace();
			// Still needs to fix this catch
		}
	}

	public void copyFile(String origFilename, String newFilename) {
		try {
			File origFile = new File(origFilename);
			File newFile = new File(newFilename);

			if (!newFile.exists()) {
				File parentFile = newFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				newFile.createNewFile();
			}

			InputStream oInStream = new FileInputStream(origFile);
			OutputStream oOutStream = new FileOutputStream(newFile);

			byte[] oBytes = new byte[1024];
			int nLength;
			BufferedInputStream oBuffInputStream = new BufferedInputStream(oInStream);
			while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
				oOutStream.write(oBytes, 0, nLength);
			}
			oInStream.close();
			oOutStream.close();
		} catch (Exception e) {
			System.out.println("Error trying to back up configuration file for scenario.");
			System.out.println("error: " + e);
			utils.exitOnException();
		}
	}

	public boolean trash(File toTrash) {
		Path trashPath = new File(vars.getTrashDir()).toPath();

		if (toTrash.isDirectory()) {
			for (File file : toTrash.listFiles()) {
				trash(file);
			}
		} else {
			try {
				Files.move(Paths.get(toTrash.getPath()), trashPath, StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	public void trashFile(File origFile) {
		trashFile(origFile.getAbsolutePath());
	}

	public void trashFile(String origFilename) {
		try {
			File origFile = new File(origFilename);
			String name = origFile.getName();

			String trashFilename = vars.getTrashDir() + File.separator + name;
			File newFile = new File(trashFilename);

			if (!newFile.exists())
				newFile.createNewFile();

			InputStream oInStream = new FileInputStream(origFile);
			OutputStream oOutStream = new FileOutputStream(newFile);

			byte[] oBytes = new byte[1024];
			int nLength;
			BufferedInputStream oBuffInputStream = new BufferedInputStream(oInStream);
			while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
				oOutStream.write(oBytes, 0, nLength);
			}
			oInStream.close();
			oOutStream.close();

			origFile.delete();
		} catch (Exception e) {
			System.out.println("Error trying to back up configuration file for scenario.");
			System.out.println("error: " + e);
			utils.exitOnException();
		}
	}



	
	public void copyAndMoveFile(String origFilename, String newFilename) {
		try {
			File origFile = new File(origFilename);
			File newFile = new File(newFilename);

			if (!newFile.exists())
				newFile.createNewFile();

			InputStream oInStream = new FileInputStream(origFile);
			OutputStream oOutStream = new FileOutputStream(newFile);

			byte[] oBytes = new byte[1024];
			int nLength;
			BufferedInputStream oBuffInputStream = new BufferedInputStream(oInStream);
			while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
				oOutStream.write(oBytes, 0, nLength);
			}
			oInStream.close();
			oOutStream.close();
		} catch (Exception e) {
			System.out.println("Error trying to back up configuration file for scenario.");
			System.out.println("error: " + e);
			utils.exitOnException();
		}
	}

	public boolean searchForTextInFile(File f, String text, String comment) {
		boolean b = false;

		if (f.exists()) {
			ArrayList<String> arrayListTxt = new ArrayList<String>();
			arrayListTxt = this.getStringArrayFromFile(f.getPath(), comment);
			for (int i = 0; i < arrayListTxt.size(); i++) {
				String str = arrayListTxt.get(i);
				if (str.indexOf(text) > -1)
					b = true;
			}
		}

		return b;
	}
	
	

	public boolean searchForTextAtStartOfLinesInFile(File f, String text, String comment) {
		boolean b = false;

		if (f.exists()) {
			ArrayList<String> arrayListTxt = new ArrayList<String>();
			arrayListTxt = this.getStringArrayFromFile(f.getPath(), comment);
			for (int i = 0; i < arrayListTxt.size(); i++) {
				String str = arrayListTxt.get(i);
				if (str.startsWith(text)) {
					b = true;
					break;
				}
			}
		}

		return b;
	}

	public String searchForTextInFileS(File f, String text, String comment) {
		String s = "";

		if (f.exists()) {
			ArrayList<String> arrayListTxt = new ArrayList<String>();
			arrayListTxt = this.getStringArrayFromFile(f.getPath(), comment);
			for (int i = 0; i < arrayListTxt.size(); i++) {
				String str = arrayListTxt.get(i);
				if (str.indexOf(text) > -1)
					s = str;
			}
		}

		return s;
	}

	public ArrayList<String> searchForTextInFileA(String filename, String text, String comment) {
		ArrayList<String> list = new ArrayList<String>();

		File f = new File(filename);

		if (f.exists()) {
			ArrayList<String> arrayListTxt = new ArrayList<String>();
			arrayListTxt = this.getStringArrayFromFile(f.getPath(), comment);
			for (int i = 0; i < arrayListTxt.size(); i++) {
				String str = arrayListTxt.get(i);
				if (str.indexOf(text) > -1)
					list.add(str);
			}
		}

		return list;
	}

	public int countLinesWithTextInFile(File f, String text, String comment) {
		int count = 0;

		if (f.exists()) {
			ArrayList<String> arrayListTxt = new ArrayList<String>();
			arrayListTxt = this.getStringArrayFromFile(f.getPath(), comment);
			for (int i = 0; i < arrayListTxt.size(); i++) {
				String str = arrayListTxt.get(i);
				// Dan Modified on 3-6-2021 to skip lines starting with comment string
				if (!str.startsWith(comment)) {
					if (str.indexOf(text) > -1)
						count++;
				}
			}
		}

		return count;
	}
	
    public void zipDirectoryBadMerge(File dir, String zipDirName) {
        try {
        	List<String> filesListInDir = new ArrayList<String>();
            filesListInDir=populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }	
    private List<String> populateFilesListBadMerge(File dir) throws IOException {
        File[] files = dir.listFiles();
        List<String> listInDir=new ArrayList<String>();
        for(File file : files){
            if(file.isFile()) listInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
        return listInDir;
    }
    
    public boolean doesFileExistBadMerge(String filename) {
    	File file=null; 
    	
    	try {
    	   file=new File(filename);
    	} catch(Exception e) {
    		
    	}
    	return file.exists();
    }
    
    public void deleteFileBadMerge(String filename) {
    	try {
    	File file=new File(filename);
    	if (file.exists()) deleteFile(file);
    	} catch(Exception e) {
    		System.out.println("error deleting "+filename);
    	}
    }
    
    public void deleteFileBadMerge(File file) {
        if (file.exists()) {
        	file.delete();
        }
    }    
    
    public void deleteFilesBadMerge(String directoryName,String extension) {
        final File dir = new File(directoryName);
        final String[] allFiles = dir.list();
        for (final String file : allFiles) {
            if (file.endsWith(extension)) {
                new File(directoryName + File.pathSeparator  + file).delete();
            }
        }
    }


	public void zipDirectory(File dir, String zipDirName) {
		try {
			List<String> filesListInDir = new ArrayList<String>();
			filesListInDir = populateFilesList(dir);
			// now zip files one by one
			// create ZipOutputStream to write to the zip file
			FileOutputStream fos = new FileOutputStream(zipDirName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String filePath : filesListInDir) {
				System.out.println("Zipping " + filePath);
				// for ZipEntry we need to keep only relative file path, so we used substring on
				// absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				// read the file and write to ZipOutputStream
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> populateFilesList(File dir) throws IOException {
		File[] files = dir.listFiles();
		List<String> listInDir = new ArrayList<String>();
		for (File file : files) {
			if (file.isFile())
				listInDir.add(file.getAbsolutePath());
			else
				populateFilesList(file);
		}
		return listInDir;
	}

	public boolean doesFileExist(String filename) {
		boolean does_it_exist = false;
		File file = null;

		try {
			file = new File(filename);

		    does_it_exist= file.exists();
		} catch (Exception e) {
			System.out.println("Exception occurred accessing "+filename+": "+e+". Attempting to continue.");
		}
		return does_it_exist;
	}

	public void deleteFile(String filename) {
		try {
			File file = new File(filename);
			if (file.exists())
				deleteFile(file);
		} catch (Exception e) {
			System.out.println("error deleting " + filename);
		}
	}

	public void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}

	public void deleteFiles(String directoryName, String extension) {
		final File dir = new File(directoryName);
		final String[] allFiles = dir.list();
		for (final String file : allFiles) {
			if (file.endsWith(extension)) {
				new File(directoryName + File.pathSeparator + file).delete();
			}
		}
	}

	public void deleteDirectory(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDirectory(f);
			}
		}
		file.delete();
	}

	public String getLastModifiedInfoForFile(String filename) {
		String str = "";
		long long_date = 0;

		try {
			File f = new File(filename);
			if (f.exists())
				long_date = f.lastModified();
		} catch (Exception e) {
			System.out.println("Error getting last modified date of file " + filename + vars.getEol() + e);
		}

		if (long_date == 0) {
			str = "Unknown";
		} else {
			Date date = new Date(long_date);
			SimpleDateFormat df2 = new SimpleDateFormat("yy.MM.dd");
			str = df2.format(date);
		}

		return str;
	}

	public String getRelativePath(String base_dir, String filename) {
		String relFilename = filename;

		if (!filename.startsWith("..")) {
			try {
				Path base_path = Paths.get(base_dir);
				Path file_path = Paths.get(filename);
				Path relative_path = base_path.relativize(file_path);
				relFilename = relative_path.toString();
			} catch (Exception e) {
				System.out.println("error calculating relative filename. Using full path.");
				relFilename = filename;
			}
		}
		return relFilename;
	}

	public String getResolvedPath(String base_dir, String filename) {
		String relFilename = null;
		try {
			Path base_path = Paths.get(base_dir);
			Path file_path = Paths.get(filename);
			Path resolved_path = base_path.resolve(file_path).normalize();
			relFilename = resolved_path.toString();
		} catch (Exception e) {
			System.out.println("error calculating resolved filename. Using full path.");
			relFilename = filename;
		}
		return relFilename;
	}

	public BufferedWriter initializeBufferedFile(String dirName, String filename) {
		BufferedWriter bw = null;
		try {
			File dir = new File(dirName);
			if (!dir.exists())
				dir.mkdir();
			File file = new File(dirName + File.separator + filename);
			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (Exception e) {
			System.out.println("difficulty setting up temp file:" + e);
		}
		return bw;
	}

	public void closeBufferedFile(BufferedWriter bw) {
		try {
			bw.close();
		} catch (Exception e) {
			System.out.println("Problem closing temp file.");
		}
	}

	public void writeToBufferedFile(BufferedWriter bw, String s) {
		try {
			bw.write(s);
		} catch (Exception e) {
			System.out.println("Error writing to temp policy file:" + e);
		}
	}

	public void concatDestSource(String dest_filename, String src_filename) {
		ArrayList<String> src_list = new ArrayList<String>();
		src_list.add(src_filename);
		concatDestSources(dest_filename, src_list);
	}

	public void concatDestSources(String dest_filename, ArrayList<String> src_filenames) {
		FileWriter output;
		try {
			output = new FileWriter(dest_filename);

			Scanner[] sc = new Scanner[src_filenames.size()];
			for (int i = 0; i < sc.length; i++) {
				sc[i] = new Scanner(new File(src_filenames.get(i)));
				while (sc[i].hasNextLine()) {
					String str = sc[i].nextLine();
					output.append(str + vars.getEol());
				}
				output.flush();
				sc[i].close();
			}
			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<String> getMatchingTextArrayInFile(String filename, ArrayList<String> array) {
		ArrayList<String> content = this.getStringArrayFromFile(filename, "#");

		boolean[] b = new boolean[array.size()];
		for (int j = 0; j < b.length; j++)
			b[j] = false;

		int count_true = 0;
		for (int j = 0; j < array.size(); j++) {
			if (b[j]) {
				count_true++;
			} else {
				for (int i = content.size() - 1; i >= 0; i--) {
					String line = content.get(i);
					if (line.startsWith(array.get(j))) {
						b[j] = true;
						count_true++;
						array.set(j, line);
						break;
					}
				}
				if (b[j] == false)
					array.set(j, "");
			}
			if (count_true == b.length)
				break;
		}

		return array;
	}

	public void deleteDirectoryStream(Path path) throws IOException {
		Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

	// size of directory in bytes
	public long getDirectorySize(Path path) {

		long size = 0;

		// need close Files.walk
		try (Stream<Path> walk = Files.walk(path)) {

			size = walk

					.filter(Files::isRegularFile).mapToLong(p -> {
						try {
							return Files.size(p);
						} catch (IOException e) {
							System.out.printf("Failed to get size of %s%n%s", p, e);
							return 0L;
						}
					}).sum();

			walk.close();
		} catch (IOException e) {
			System.out.println("IO error: "+ e);
		}

		return size;

	}

	public boolean testFolderExists(String filename) {
		boolean b = false;

		if(filename!=null) {
			File f = new File(filename);
			if ((f.isDirectory()) && (f.exists()))
				b = true;
			}

		return b;
	}

	public boolean appendTextToFile(String txt, String filename) {
		boolean b = false;

		try {
			if (this.doesFileExist(filename)) {

				File file = new File(filename);
				FileWriter fr = new FileWriter(file, true);
				fr.write(txt);
				fr.close();

			}
		} catch (Exception e) {
			System.out.println("Error appending text to file.");
		}

		return b;
	}

}