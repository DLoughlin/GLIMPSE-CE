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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class GLIMPSEVariables {

	protected static final GLIMPSEVariables instance = new GLIMPSEVariables();
	
	private GLIMPSEUtils utils;
	private GLIMPSEFiles files;
	private GLIMPSEStyles styles;

    private String GLIMPSEVersion="GLIMPSE v1.1-2024.12.12";
	
	//other parameters
	public int ScenarioBuilderWidth = 1200;
	public int ScenarioBuilderHeight = 800;
	public float maxDatabaseSizeGB=40;
	private String executeCmdShort = "cmd /C ";
	private String executeCmd = "cmd /C start ";
	private String buildInfo = GLIMPSEVersion;
	private String runQueueStr = "Queue is empty.";
	private String eol = System.lineSeparator();
	private String debugCreate = "0";
	private String debugRename = "0";
	private String startYearForShare = "2010";
	public String[][] tech_info = null; 
	public String[][] sector_info = null;
	private String allowablePolicyYears = "2020,2025,2030,2035,2040,2045,2050,2055,2060,2065,2070,2075,2080,2085,2090,2095,2100";
	
	//parameters from options file
	private String preferredFontSize = "12";
	private String useIcons = "false";
	private String debugRegion = "USA";
	private boolean isGcamUSA = false;
	private boolean showSplash = true;
	private boolean useAllAvailableProcessors = true;
	private String glimpseDir = null;
	private String glimpseDocDir = null;
	private String gCamHomeDir = null;
	private String gCamSolver = null;
	private String gCamGUIDir = null;
	private String gCamGUIJarDir = null;
	private String gCamGUIJar = null;
	private String gCamExecutable = null;
	private String gCamExecutableArgs = " -C ";
	private String gCamExecutableDir = null;
	private String gCamPPExecutable = null;
	private String gCamPPExecutableDir = null;
	private String filesToSave = null;
	private String scenarioComponentsDir = null;
	private String scenarioDir = null;
	private String gCamGUILogDir = null;
	//private String gCamGUIBatDir = null;
	private String resourceDir = null;
	private String trashDir = null;
	private String configurationTemplate = null;
	private String trnVehInfoFile = null;
	private String tchBndListFile = null;
	private String csvColumnFile = null;
	private String xmlHeaderFile = null;
	//private String monetaryConversionsFile = null;
	private String presetRegionsFilename = null; 
	private String gCamDataDir = null;
	private String gCamOutputDatabase = null;
	private String optionsFilename = null;
	private String xmlLibrary = null;
	private String queryFile = null;
	private String textEditor = null;
	private String xmlEditor = null;
	private String descriptionText = "";
	private String stopPeriod = null;
	private String unitConversionsFile = "../../../GLIMPSE-ModelInterface/exe/units_rules.csv"; 
	private int simulationStartYear = 2015;
	private int simulationLastYear = 2100;
	private int simulationYearIncrement = 5;



	private GLIMPSEVariables() {
	}

	public static GLIMPSEVariables getInstance() {

		return instance;
	}

	public void init(GLIMPSEUtils u, GLIMPSEVariables v, GLIMPSEStyles s, GLIMPSEFiles f) {
		utils = u;
		styles = s;
		files = f;
	}
	
	public String getGLIMPSEVersion() {
		return GLIMPSEVersion;
	}
	
	public String getPresetRegionsFilename() {
		return presetRegionsFilename; 
	}
	
	public void setPresetRegionsFilename(String s) {
		presetRegionsFilename=s; 
	}
	
	public int getSimulationStartYear() {
		return simulationStartYear;
	}
	
	public void setSimulationStartYear(String startYear) {
		simulationStartYear = utils.convertStringToInt(startYear);
		return;
	}
	
	public int getSimulationLastYear() {
		return simulationLastYear;
	}
	
	public String getAllowablePolicyYears() {
		return allowablePolicyYears;
	}
	
	public void setAllowablePolicyYears(String year_list) {
		this.allowablePolicyYears=year_list; 
	}	
	
	public void setSimulationLastYear(String lastYear) {
		simulationLastYear = utils.convertStringToInt(lastYear);
		return;
	}
	
	public int getSimulationYearIncrement() {
		return simulationYearIncrement;
	}

	public void setSimulationYearIncrement(String yearIncrement) {
		simulationYearIncrement = utils.convertStringToInt(yearIncrement);
		return;
	}
	
	public boolean getUseAllAvailableProcessors() {
		return useAllAvailableProcessors;
	}
	
	public void setUseAllAvailableProcessors(boolean b) {
		useAllAvailableProcessors=b;
	}
	
	public void setUseAllAvailableProcessors(String str) {
		boolean b=false;
		if ((str.toLowerCase().equals("true"))||(str.toLowerCase().equals("yes"))) b=true;
		useAllAvailableProcessors=b;
	}
	
	public boolean getShowSplash() {
		return showSplash;
	}
	
	public void setShowSplash(boolean b) {
		showSplash=b;
	}

	
	public void setShowSplash(String str) {
		boolean b=false;
		if ((str.toLowerCase().equals("true"))||(str.toLowerCase().equals("yes"))) b=true;
		showSplash=b;
	}

	public String getDebugRegion() {
		return debugRegion;
	}

	public void setDebugRegion(String s) {
		this.debugRegion = s;
	}
	
	public String getStartYearForShare() {
		return startYearForShare;
	}
	
	public void setStartYearForShare(String s) {
		startYearForShare=s;
	}
	
	public String getDebugCreate() {
		return debugCreate;
	}

	public void setDebugCreate(String s) {
		if (s.toLowerCase().equals("true")) { 
			s="1";
		} else if (s.toLowerCase().equals("false")) { 
			s="0";
		}
		this.debugCreate = s;
	}
	
	public String getDebugRename() {
		return debugRename;
	}

	public void setDebugRename(String s) {
		this.debugRename = s;
	}
	
	
	public String getBuildInfo() {
		return buildInfo;
	}

	public void setBuildInfo(String s) {
		this.buildInfo = s;
	}

	public boolean isGcamUSA() {
		return isGcamUSA;
	}

	public void setGcamUSA(boolean b) {
		this.isGcamUSA = b;
	}

	public String getExecuteCmdShort() {
		return executeCmdShort;
	}

	public void setExecuteCmdShort(String s) {
		this.executeCmdShort = s;
	}

	public String getExecuteCmd() {
		return executeCmd;
	}

	public void setExecuteCmd(String s) {
		this.executeCmd = s;
	}

	public String getGlimpseDir() {
		return glimpseDir;
	}

	public void setGlimpseDir(String s) {
		this.glimpseDir = s;
	}

	public String getGlimpseDocDir() {
		return glimpseDocDir;
	}

	public void setGlimpseDocDir(String s) {
		this.glimpseDocDir = s;
	}
	
	public String getgCamHomeDir() {
		return gCamHomeDir;
	}

	public void setgCamHomeDir(String s) {
	
		this.gCamHomeDir = s;
	}

	public String getgCamSolver() {
		return gCamSolver;
	}

	public void setgCamSolver(String s) {
		this.gCamSolver = s;
	}

	public String getgCamGUIDir() {
		return gCamGUIDir;
	}

	public void setgCamGUIDir(String s) {
		this.gCamGUIDir = s;
	}

	public String getgCamGUIJarDir() {
		return gCamGUIJarDir;
	}

	public void setgCamGUIJarDir(String s) {
		this.gCamGUIJarDir = s;
	}
	
	public String getgCamGUIJar() {
		return gCamGUIJar;
	}

	public void setgCamGUIJar(String s) {
		this.gCamGUIJar = s;
	}
	
	public String getgCamExecutable() {
		return gCamExecutable;
	}

	public void setgCamExecutable(String s) {
		this.gCamExecutable = s;
	}

	public String getgCamExecutableArgs() {
		return gCamExecutableArgs;
	}

	public void setgCamExecutableArgs(String s) {
		this.gCamExecutableArgs = s;
	}
	
	public String getgCamExecutableDir() {
		return gCamExecutableDir;
	}

	public void setgCamExecutableDir(String s) {
		this.gCamExecutableDir = s;
	}

	public String getgCamPPExecutable() {
		return gCamPPExecutable;
	}

	public void setgCamPPExecutable(String s) {
		this.gCamPPExecutable = s;
	}

	public String getgCamPPExecutableDir() {
		return gCamPPExecutableDir;
	}

	public void setgCamPPExecutableDir(String s) {
		this.gCamPPExecutableDir = s;
	}

	public String getFilesToSave() {
		return filesToSave;
	}

	
	public void setFilesToSave(String s) {
		this.filesToSave = s;
	}

	public String getScenarioComponentsDir() {
		return scenarioComponentsDir;
	}

	public void setScenarioComponentsDir(String s) {
		this.scenarioComponentsDir = s;
	}

	public String getScenarioDir() {
		return scenarioDir;
	}

	public void setScenarioDir(String s) {
		this.scenarioDir = s;
	}

	public String getgCamGUILogDir() {
		return gCamGUILogDir;
	}
	
	public String getQueryFile() {
		return queryFile;
	}

	public void setgCamGUILogDir(String s) {
		this.gCamGUILogDir = s;
	}
	
	public void setQueryFile(String s) {
		this.queryFile=s;
	}
	
	public void setUnitConversionsFile(String s) {
		this.unitConversionsFile=s;
	}

	public String getUnitConversionsFile() {
		return unitConversionsFile;
	}
	
//	public String getgCamGUIBatDir() {
//		return gCamGUIBatDir;
//	}
//
//	public void setgCamGUIBatDir(String s) {
//		this.gCamGUIBatDir = s;
//	}

	public String getResourceDir() {
		return resourceDir;
	}

	public void setResourceDir(String s) {
		this.resourceDir = s;
	}

	public String getTrashDir() {
		return trashDir;
	}

	public void setTrashDir(String s) {
		this.trashDir = s;
	}

	public String getConfigurationTemplate() {
		return configurationTemplate;
	}

	public void setConfigurationTemplate(String s) {
		this.configurationTemplate = s;
	}

	public String getTrnVehInfoFile() {
		return trnVehInfoFile;
	}

	public void setTrnVehInfoFile(String s) {
		this.trnVehInfoFile = s;
	}

	public String getTchBndListFile() {
		return tchBndListFile;
	}

	public void setTchBndListFile(String s) {
		this.tchBndListFile = s;
	}

	public String getgCamDataDir() {
		return gCamDataDir;
	}

	public void setgCamDataDir(String s) {
		this.gCamDataDir = s;
	}

	public String getgCamOutputDatabase() {
		return gCamOutputDatabase;
	}

	public void setgCamOutputDatabase(String s) {
		this.gCamOutputDatabase = s;
	}
	

	public float getMaxDatabaseSize() {
		return maxDatabaseSizeGB;
	}

	public void setMaxDatabaseSizeGB(float f) {
		this.maxDatabaseSizeGB = f;
	}
	
	public String getOptionsFilename() {
		return optionsFilename;
	}

	public void setOptionsFilename(String s) {
		this.optionsFilename = s;
	}

	public String getXmlLibrary() {
		return xmlLibrary;
	}

	public void setXmlLibrary(String s) {
		this.xmlLibrary = s;
	}

	public String getTextEditor() {
		return textEditor;
	}

	public void setTextEditor(String s) {
		this.textEditor = s;
	}

	public String getXmlEditor() {
		return xmlEditor;
	}

	public void setXmlEditor(String s) {
		this.xmlEditor = s;
	}

	//public String getMonetaryConversionsFile() {
	//	return this.monetaryConversionsFile;
	//}

	//public void setMonetaryConversionsFile(String s) {
	//	this.monetaryConversionsFile = s;
	//}

	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String s) {
		this.descriptionText = s;
	}

	public String getStopPeriod() {
		return stopPeriod;
	}

	public void setStopPeriod(String s) {
		this.stopPeriod = s;
	}

	public String getRunQueueStr() {
		return runQueueStr;
	}

	public void setRunQueueStr(String s) {
		this.runQueueStr = s;
	}

	public String getEol() {
		return eol;
	}

	public void setEol(String s) {
		this.eol = s;
	}

	public String getCsvColumnFile() {
		return csvColumnFile;
	}

	public void setCsvColumnFile(String s) {
		csvColumnFile = s;
	}

	public String getUseIcons() {
		return useIcons;
	}

	public void setUseIcons(String str) {
		if ((str.toLowerCase().equals("yes")) || (str.toLowerCase().equals("true")) || (str.toLowerCase().equals("1"))) {
			useIcons = "true";
		} else {
			useIcons = "false";
		}
	}

	public String getXmlHeaderFile() {
		return xmlHeaderFile;
	}

	public void setXmlHeaderFile(String s) {
		xmlHeaderFile = s;
	}

	public String getPreferredFontSize() {
		return preferredFontSize;
	}

	public void setPreferredFontSize(String s) {
		try {
			int size = Integer.parseInt(s);
			preferredFontSize = s;
			styles.setFontSize(size);
		} catch (Exception e) {
			System.out.println("Could not convert font size string " + s + " to double.");
		}
	}

	public String get(String param) {
		String returnVal = "";

		param = param.toLowerCase();

		switch (param) {
		case "allowablepolicyyears":
			returnVal = ""+allowablePolicyYears;
			break;
		case "useallavailableprocessors":
			returnVal = ""+useAllAvailableProcessors;
			break;
		case "showsplash":
			returnVal = ""+showSplash;
			break;
		case "buildinfo":
			returnVal = buildInfo;
			break;
		case "executecmdshort":
			returnVal = executeCmdShort;
			break;
		case "executecmd":
			returnVal = executeCmd;
			break;
		case "glimpsedir":
			returnVal = glimpseDir;
			break;
		case "glimpsedocdir":
			returnVal = glimpseDocDir;
			break;
		case "gcamhomedir":
			returnVal = gCamHomeDir;
			break;
		case "solver":
			returnVal = gCamSolver;
			break;
		case "gcamsolver":
			returnVal = gCamSolver;
			break;
		case "gcamguidir":
			returnVal = gCamGUIDir;
			break;
		case "gcamexecutable":
			returnVal = gCamExecutable;
			break;
		case "gcamexecutableargs":
			returnVal = gCamExecutableArgs;
			break;
		case "gcamexecutabledir":
			returnVal = gCamExecutableDir;
			break;
		case "gcamppexecutable":
			returnVal = gCamPPExecutable;
			break;
		case "gcamppexecutabledir":
			returnVal = gCamPPExecutableDir;
			break;
		case "filestosave":
			returnVal = filesToSave;
			break;
		case "gcamoutputtosave":
			returnVal = filesToSave;
			break;
		case "scenariocomponentsdir":
			returnVal = scenarioComponentsDir;
			break;
		case "scenarioxmldir":
			returnVal = scenarioDir;
			break;
		case "scenariodir":
			returnVal = scenarioDir;
			break;
		case "gcamguilogdir":
			returnVal = gCamGUILogDir;
			break;
		case "queryfile":
			returnVal = queryFile;
			break;
		case "gcamguijardir":
			returnVal = gCamGUIJarDir;
			break;
		case "gcamguijar":
			returnVal = gCamGUIJar;
			break;
		//case "gcamguibatdir":
		//	returnVal = gCamGUIBatDir;
		//	break;
		case "resourcedir":
			returnVal = resourceDir;
			break;
		case "presetregionsfilename":
			returnVal = presetRegionsFilename;
			break;
		case "trashdir":
			returnVal = trashDir;
			break;
		case "configurationtemplate":
			returnVal = configurationTemplate;
			break;
		case "tranloadfactorsfile":
			returnVal = trnVehInfoFile;
			break;
		case "trnvehinfofile":
			returnVal = trnVehInfoFile;
			break;
		case "tchbndlistfile":
			returnVal = tchBndListFile;
			break;
		case "gcamdatadir":
			returnVal = gCamDataDir;
			break;
		case "gcamoutputdatabase":
			returnVal = gCamOutputDatabase;
			break;
		case "maxdatabasesizegb":
			returnVal = ""+maxDatabaseSizeGB;
			break;
		case "optionsfilename":
			returnVal = optionsFilename;
			break;
		case "xmllibrary":
			returnVal = xmlLibrary;
			break;
		case "texteditor":
			returnVal = textEditor;
			break;
		case "xmleditor":
			returnVal = xmlEditor;
			break;
		case "descriptiontext":
			returnVal = descriptionText;
			break;
		case "stopperiod":
			returnVal = stopPeriod;
			break;
		case "stop-period":
			returnVal = stopPeriod;
			break;
		case "runqueuestr":
			returnVal = runQueueStr;
			break;
		case "eol":
			returnVal = eol;
			break;
		case "isgcamusa":
			returnVal = String.valueOf(isGcamUSA);
			break;
		case "csvcolumnfile":
			returnVal = csvColumnFile;
			break;
		case "xmlheaderfile":
			returnVal = xmlHeaderFile;
			break;
		case "preferredfontsize":
			returnVal = preferredFontSize;
			break;
		case "useicons":
			returnVal = useIcons;
			break;
		case "use_icons":
			returnVal = useIcons;
			break;
		case "unitconversionsfile":
			returnVal = unitConversionsFile;
			break;
		//case "monetaryconversionsfile":
		//	returnVal = monetaryConversionsFile;
		//	break;
		case "debugregion":
			returnVal = debugRegion;
			break;
		case "debugcreate":
			returnVal = debugCreate;
			break;
		case "startyearforshare":
			returnVal = startYearForShare;
			break;
		case "debugrename":
			returnVal = debugRename;
		}
		if ((returnVal==null)||(returnVal.equals(""))) System.out.println("No match for "+param);
		return returnVal;
	}

	public void set(String param, String val) {

		param = param.toLowerCase();
		if (val.indexOf("#") > -1){
			val = fixDir(val);
		}


		switch (param) {

		case "glimpsedir":
			String current_dir=System.getProperty("user.dir");
			glimpseDir = fixDir(val);
			if (glimpseDir.startsWith(".")) glimpseDir=current_dir;

			break;
		case "gcamhomedir":
			gCamHomeDir = fixDir(val);
			break;		
		case "allowablepolicyyears":
			setAllowablePolicyYears(val);
			break;
		case "useallavailableprocessors":
			setUseAllAvailableProcessors(val);
			break;
		case "showsplash":
			setShowSplash(val);
			break;
		case "buildinfo":
			buildInfo = val;
			break;
		case "executecmdshort":
			executeCmdShort = val;
			break;
		case "executecmd":
			executeCmd = val;
			break;

		case "glimpsedocdir":
			glimpseDocDir = fixDir(val);
			break;

		case "solver":
			gCamSolver = fixDir(val);
			break;
		case "gcamsolver":
			gCamSolver = fixDir(val);
			break;
		case "gcamguidir":
			gCamGUIDir = fixDir(val);
			break;
		case "gcamguijardir":
			gCamGUIJarDir = fixDir(val);
			break;
		case "gcamguijar":
			gCamGUIJar = val;
			break;
		case "gcamexecutable":
			gCamExecutable = fixDir(val);
			break;
		case "gcamexecutableargs":
			gCamExecutableArgs = val;
			break;
		case "gcamexecutabledir":
			gCamExecutableDir = fixDir(val);
			break;
		case "gcamppexecutable":
			gCamPPExecutable = fixDir(val);
			break;
		case "gcamppexecutabledir":
			gCamPPExecutableDir = fixDir(val);
			break;
		case "gcamoutputtosave":
			filesToSave = fixDir(val);
			break;
		case "filestosave":
			filesToSave = fixDir(val);
			break;
		case "scenariocomponentsdir":
			scenarioComponentsDir = fixDir(val);
			break;
		case "scenarioxmldir":
			scenarioDir = fixDir(val);
			break;
		case "scenariodir":
			scenarioDir = fixDir(val);
			break;
		case "gcamguilogdir":
			gCamGUILogDir = fixDir(val);
			break;
		case "queryfile":
			queryFile = fixDir(val);
			break;
		case "unitconversionsfile":
			unitConversionsFile = fixDir(val);
			break;
			//case "gcamguibatdir":
		//	gCamGUIBatDir = fixDir(val);
		//	break;
		case "resourcedir":
			resourceDir = fixDir(val);
			break;
		case "presetregionsfilename":
			presetRegionsFilename = fixDir(val);
			break;
		case "trashdir":
			trashDir = fixDir(val);
			break;
		case "configurationtemplate":
			configurationTemplate = fixDir(val);
			break;
		case "tranloadfactorsfile":
			trnVehInfoFile = fixDir(val);
			break;
		case "trnvehinfofile":
			trnVehInfoFile = fixDir(val);
			break;
		case "tchbndlistfile":
			tchBndListFile = fixDir(val);
			break;
		case "gcamdatadir":
			gCamDataDir = fixDir(val);
			break;
		case "gcamoutputdatabase":
			gCamOutputDatabase = fixDir(val);
			break;
		case "maxdatabasesizegb":
			maxDatabaseSizeGB = Float.parseFloat(val);
			break;
		case "optionsfilename":
			optionsFilename = fixDir(val);
			break;
		case "xmllibrary":
			xmlLibrary = fixDir(val);
			break;
		case "texteditor":
			textEditor = fixDir(val);
			break;
		case "xmleditor":
			xmlEditor = fixDir(val);
			break;
		case "descriptiontext":
			descriptionText = fixDir(val);
			break;
		case "stopperiod":
			stopPeriod = val;
			break;
		case "stop-period":
			stopPeriod = val;
			break;
		case "runqueuestr":
			runQueueStr = fixDir(val);
			break;
		case "eol":
			eol = val;
			break;
		case "isgcamusa":
			isGcamUSA = false;
			if (val.toLowerCase().trim().equals("true"))
				isGcamUSA = true;
			break;
		case "csvcolumnfile":
			csvColumnFile = fixDir(val);
			break;
		case "xmlheaderfile":
			xmlHeaderFile = fixDir(val);
			break;
		case "preferredfontsize":
			setPreferredFontSize(val);
			break;
		case "useicons":
			setUseIcons(val);
			break;
		case "use_icons":
			setUseIcons(val);
			break;
		//case "monetaryconversionsfile":
		//	setMonetaryConversionsFile(val);
		//	break;
		case "debugregion":
			setDebugRegion(val);
			break;
		case "debugcreate":
			setDebugCreate(val);
			break;
		case "debugrename":
			setDebugRename(val);
			break;	
		case "startYearForShare":
			setStartYearForShare(val);
			break;	
			}

		if (param.indexOf("dir") > 0) {
			testDirExists(val);
		}

		return;
	}

	private String fixDir(String filename) {

		//for backwards compatibility on options file... wildcards surrounded by #
		if (filename.indexOf("#glimpseDir#") > -1) {
			filename = filename.replace("#glimpseDir#", glimpseDir);
		}
		if (filename.indexOf("#gCamHomeDir#") > -1) {
			filename = filename.replace("#gCamHomeDir#", gCamHomeDir);
		}
		if (filename.indexOf("#gCamGuiDir#") > -1) {
			filename = filename.replace("#gCamGuiDir#", gCamGUIDir);
		}
		
		//for new convention with wildcards surrounded by $s
		if (filename.indexOf("$glimpseDir$") > -1) {
			filename = filename.replace("$glimpseDir$", glimpseDir);
		}
		if (filename.indexOf("$gCamHomeDir$") > -1) {
			filename = filename.replace("$gCamHomeDir$", gCamHomeDir);
		}
		if (filename.indexOf("$gCamGuiDir$") > -1) {
			filename = filename.replace("$gCamGuiDir$", gCamGUIDir);
		}

		filename = filename.replace("/", File.separator).replace("\\", File.separator).replace("\\\\", File.separator);

		return filename;
	}

	private boolean testDirExists(String pathName) {
		return testDirExists(pathName, false);
	}

	private boolean testDirExists(String pathName, boolean isFatal) {
		boolean b = true;

		try {
			File f = new File(pathName);
			b = f.isDirectory();
		} catch (Exception E) {
			System.out.println("error openning: " + pathName);
			b = false;
			if (isFatal) {
				System.out.println("exiting");
				System.exit(0);
			}
		}

		return b;
	}
	
	private boolean testFileExists(String pathName) {
		boolean b = true;

		try {
			File f = new File(pathName);
			b = f.exists();
		} catch (Exception E) {
			System.out.println("error openning: " + pathName);
			b = false;
		}

		return b;
	}

	public void loadOptions(String filename) {
		set("optionsFilename", filename);
		loadOptions();
	}

	public void loadOptions() {
		GLIMPSEUtils utils = GLIMPSEUtils.getInstance();

		ArrayList<String[]> keyValuePairs = null;
		File optionsFile = new File(getOptionsFilename());
		
		System.out.println("Loading options from " + getOptionsFilename());

		if (!optionsFile.exists()) {
			System.out.println("Specified options file does not exist.");
			//utils.warningMessage("Specified options file does not exist. Please check command-line argument.");
			return;
		}

		files.optionsFileContent = files.getStringArrayFromFile(getOptionsFilename(), "#");

		String current_line="";
		
		try {

			keyValuePairs = files.loadKeyValuePairsFromFile(getOptionsFilename(), "=");

			for (int i = 0; i < keyValuePairs.size(); i++) {

				current_line=keyValuePairs.get(i)[0];
				String[] s = keyValuePairs.get(i);
				String s0 = s[0].trim();
				String s1 = s[1].trim();
				
				if (!s0.startsWith("#")) { // pound is the symbol for a comment
					set(s0, s1);
				}
			}
		} catch (Exception e) {
			//utils.warningMessage("Problem reading options file.");
			System.out.println("Error reading options file: " + optionsFile);
			System.out.println("Line: "+current_line);
			System.out.println("Error: " + e);
			//utils.exitOnException();
		}
		return;
	}

	public ArrayList<String> getArrayListOfOptions() {

		files.optionsFileContent=files.getStringArrayFromFile(optionsFilename, "#");
		
		ArrayList<String> optionsFileContent = files.optionsFileContent;
		ArrayList<String> completed_arrayList = new ArrayList<String>();

		try {
			for (int i = 0; i < optionsFileContent.size(); i++) {
				String line = optionsFileContent.get(i);
				int loc_of_equals = line.indexOf("=");
				if (loc_of_equals > 0) {
					String s1 = line.substring(0, loc_of_equals - 1).trim();
					String s2 = line.substring(loc_of_equals + 1).trim();
					String val = this.get(s1);
					if (val != null) {
						//System.out.println("s1:"+s1+":"+s2+":"+val+":");
						completed_arrayList.add(s1 + " = " + val);
					} else {
						System.out.println("No translation for "+s1);
					}
				}
			}
		} catch (Exception e) {
			utils.warningMessage("Difficulty reading options file.");
			System.out.println("Difficulty reading options file. Attempting to continue.");
			completed_arrayList.add("");
		}

		return completed_arrayList;
	}
	
	public String[][] getTechInfo(){
		
		if (tech_info==null) {
			tech_info=getTechInfoAsMatrix();
		}
		
		return tech_info;
	}
	
	public String[][] getSectorInfo(){
		
		if (tech_info==null) {
			tech_info=getTechInfoAsMatrix();
		}
		
		ArrayList<String> sector_list=new ArrayList<String>(); 
		ArrayList<String> output_list=new ArrayList<String>(); 
		ArrayList<String> units_list=new ArrayList<String>(); 
		
		for (int i=0;i<tech_info.length;i++) {
			String sect_i=tech_info[i][0].trim();
			String output_i=tech_info[i][5].trim();
			String units_i=tech_info[i][6].trim();
			
			int current_len=sector_list.size();
			boolean match=false;
			for (int j=0;j<current_len;j++) {
				if (sector_list.get(j).equals(sect_i)) {
					match=true;
					break;
				}
			}
			if (!match) {
				sector_list.add(sect_i);
				output_list.add(output_i);
				units_list.add(units_i);
			}
		}
		
		String[][] sector_info=new String[sector_list.size()][3];
		for (int i=0;i<sector_list.size();i++) {
			sector_info[i][0]=sector_list.get(i);
			sector_info[i][1]=output_list.get(i);
			sector_info[i][2]=units_list.get(i);
		}
		
		return sector_info;
	}
	
	private String[][] getTechInfoAsMatrix() {

		int num = 0;

		String[][] returnStringMatrix = null;
		String text="";
		
		try {

			ArrayList<String> arrayList = files.glimpseTechBoundFileContent;

			if ((arrayList == null) || (arrayList.size() == 0))
				throw (new Exception("arrayList not read from file."));
			
			int size_j=0;
			
			text=arrayList.get(0);
			String[] textSplit = null;
			String delim=null;
			
			if (text.contains(",")){ 
				delim=",";
			} else {
				delim=":";
			}
			textSplit=text.split(delim);
			size_j=textSplit.length;

			returnStringMatrix = new String[arrayList.size()][size_j];

			//only reads in first 5 fields
			for (int i = 0; i < arrayList.size(); i++) {
				text = arrayList.get(i).trim();
				if (text.length()>0) {
				textSplit = text.split(delim);
			
				  for (int j = 0; j < size_j ; j++) {
					returnStringMatrix[i][j] = textSplit[j].trim();
				  }
				}
			}
			num++;

		} catch (Exception e) {
			utils.warningMessage("Problem reading tech list: "+text);
			System.out.println("Error reading tech list from " + get("tchBndListFile") + ":");
			System.out.println("  ---> " + e);
			if (num == 0) {
				System.out.println("Using defaults...");

				String[][] stringMatrix = {
						{ "trn_pass_road_LDV_4W", "Compact Car", "BEV", "elect_td_trn" },
						{ "trn_pass_road_LDV_4W", "Compact Car", "FCEV", "H2 enduse" },
						{ "trn_pass_road_LDV_4W", "Compact Car", "Hybrid Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Compact Car", "Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Compact Car", "NG", "delivered gas" },

						{ "trn_pass_road_LDV_4W", "Midsize Car", "BEV", "elect_td_trn" },
						{ "trn_pass_road_LDV_4W", "Midsize Car", "FCEV", "H2 enduse" },
						{ "trn_pass_road_LDV_4W", "Midsize Car", "Hybrid Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Midsize Car", "Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Midsize Car", "NG", "delivered gas" },

						{ "trn_pass_road_LDV_4W", "Large Car", "BEV", "elect_td_trn" },
						{ "trn_pass_road_LDV_4W", "Large Car", "FCEV", "H2 enduse" },
						{ "trn_pass_road_LDV_4W", "Large Car", "Hybrid Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Large Car", "Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Large Car", "NG", "delivered gas" },

						{ "trn_pass_road_LDV_4W", "Light Truck and SUV", "BEV", "elect_td_trn" },
						{ "trn_pass_road_LDV_4W", "Light Truck and SUV", "FCEV", "H2 enduse" },
						{ "trn_pass_road_LDV_4W", "Light Truck and SUV", "Hybrid Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Light Truck and SUV", "Liquids", "refined liquids enduse" },
						{ "trn_pass_road_LDV_4W", "Light Truck and SUV", "NG", "delivered gas" }

				};
				returnStringMatrix = stringMatrix;

			} else {
				System.out.println("Stopping with " + num + " read in.");
			}
		}
		return returnStringMatrix;
	}
	
	public String examineGLIMPSESetup() {

		String rtn_str="";
		
		//testing to make sure key parameters have been defined
		String[] params= {"glimpseDir","glimpseDocDir","gCamHomeDir","solver","gCamSolver","gCamGuiDir",
				"gCamExecutable","gCamExecutableArgs","gCamExecutableDir","gCamPPExecutable","gCamPPExecutableDir","filesToSave",
				"scenarioComponentsDir","scenarioXMLDir","scenarioDir","queryFile","gCamGuiJarDir","gCamGuiJar",
				"resourceDir","trashDir","configurationTemplate","tranLoadFactorsFile","tchBndListFile","gCamDataDir","gCamOutputDatabase",
				"maxDatabaseSizeGB","optionsFilename","xmlLibrary","textEditor","xmlEditor","stopPeriod","stop-period","runQueueStr",
				"isGcamUSA","csvColumnFile","xmlHeaderFile","preferredFontSize","useIcons","use_icons",/*"monetaryConversionsFile",*/"debugRegion",
				"debugCreate","startYearForShare","debugRename"};
		
		ArrayList<String> report=new ArrayList<String>();
		
		report.add(" ");
		report.add("------ Analysis of GLIMPSE setup --------");
		
		boolean params_correct=true;
		
		for (int i=0;i<params.length;i++) {
			String str=params[i];
			String val=get(str);
			if (val==null) {
				params_correct=false; 
				String s="*** Parameter "+str+" is undefined. ***";
				report.add(s);
			} else {
				if (str.indexOf("Dir")>0) {
					String dir_name=val;
					if (!this.testDirExists(dir_name)) {
						params_correct=false;
						String s="*** Specified folder for "+str+" does not exist: "+val+" ***";
						System.out.println("warning: "+s);
						report.add(s);
					}
					
				} 
			}			
		}
		if (params_correct) report.add("No problems found with parameters or folders.");


		//checks to see if there are spaces in path
		boolean no_spaces_in_path=true;
		String good_glimpse_folder = this.getGlimpseDir();
		
		if (good_glimpse_folder.contains(" ")) {
			no_spaces_in_path=false;
			String s="*** Potentially problematic installation location: The path to your GLIMPSE root folder includes at least one space character. This can cause problems with GLIMPSE operation. Please move GLIMPSE to a folder that does not contain the space character.";		    
		} else {
			report.add("No problem was found with spaces in the path to the GLIMPSE root folder.");
		}
		
		//checks to see if folders are nested
		boolean no_nesting=true;
		
		String bad_glimpse_path = good_glimpse_folder + File.separator
				+ good_glimpse_folder.substring(good_glimpse_folder.lastIndexOf(File.separator) + 1);
		if (this.testDirExists(bad_glimpse_path)) {
			no_nesting = false;
			String s = "*** Potentially problematic installation location: Found nesting of GLIMPSE folders. This usually occurs during unzipping or when a GLIMPSE update is placed within the " + good_glimpse_folder
					+ " as opposed to on top of it. Nesting is not neccesarily an issue, but may result in confusion in the future and, in some instances, may result in file pathnames exceeding the length that can be handled by the operating system. Please check your installation. The Installation Guide in the GLIMPSE documentation provides some instructions to address this issue. ***";
			report.add(s);
		} else {
			report.add("No problem was found with nesting of GLIMPSE folders.");
		}
		
		//checks to make sure the full path is not super long
		boolean path_len_ok=true;
		int path_len=this.getGlimpseDir().length();
		if (path_len>150) {
			path_len_ok=false; 
			String s = "*** Full path length to the GLIMPSE root folder is "+path_len+" characters. This exceeds the recommended length of 150 and, in some circumstances, may result in file access problems on operating systems that limit path length to 256 total characters. Please consider re-locating your GLIMPSE root folder such that it has a shorter path.";  
			report.add(s);
		} else {
			report.add("No problem was found with path length.");
		}
		
		//checks to see if java_home is defined 
		boolean found_java=true;
		
		String java_home_folder=System.getenv("JAVA_HOME");
		if (!files.testFolderExists(java_home_folder)) {
			found_java=false;
			String s="*** Your JAVA_HOME is set to "+java_home_folder+", but that folder does not exist. Please update the JAVA_HOME setting in the run_GCAM*.bat file you used to start GLIMPSE. If using the standard version of Java, it is typically C:/Program Files/Java/jre1.8.0_XXX, where you will need to update XXX with the version on your computer. ***";
			report.add(s);
		} else {
			String s="Your JAVA_HOME folder, "+java_home_folder+", was successfully found.";
			report.add(s); 
		}
		
		if ((found_java)&&(no_nesting)&&(params_correct)&&(path_len_ok)&&(no_spaces_in_path)) {
			report.add("Installation at location "+this.getGlimpseDir()+" appears to be succesful.");
		} else {
			report.add("*** One or more problems found with GLIMPSE installation. ***");
		}
		
		report.add(" ");
		report.add("------ Check to verify that key files exist as specified --------");
		String filename=this.getXmlHeaderFile();
		String s="XML header file: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);
		filename=this.getTchBndListFile();
		s="Tech Bound file: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);
		filename=this.getConfigurationTemplate();
		s="Configuration template file: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);		
		filename=this.getQueryFile();
		s="Query file: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);	
		filename=this.getgCamExecutableDir()+File.separator+this.getgCamExecutable();
		s="GCAM executable: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);	
		filename=this.getgCamPPExecutableDir()+File.separator+this.getgCamPPExecutable();
		s="ModelInterface executable: "+filename+" - "+files.doesFileExist(filename);
		report.add(s);			
		
		try {
			report.add(" ");
			report.add("------ Computer Information --------");
			double gb=1073741824;
		    com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
				     java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		    
			report.add("-- Memory analysis -- ");
			try {

				float physicalMemorySize = (float) (os.getTotalPhysicalMemorySize()/gb);
				float physicalMemoryFree = (float) (os.getFreePhysicalMemorySize()/gb);

				report.add(String.format("Total physical memory: %.1f GB",physicalMemorySize));	
				report.add(String.format("Free physical memory: %.1f GB",physicalMemoryFree));
				
				if ((physicalMemorySize<12.0)&&(!this.isGcamUSA)) report.add("*** At least 12 GB of RAM are recommended for GCAM. The model may stop unexpectedly if RAM is exhausted. ***");
				if ((physicalMemorySize<14.0)&&(this.isGcamUSA)) report.add("*** At least 14 GB of RAM are recommended for GCAM-USA, although 16 or more may be required when using complex policies such as RPS or CES. The model may stop unexpectedly if RAM is exhausted. ***");				
				report.add("");		
			} catch(Exception e1) {
				report.add("Java version does not support assessing physical memory.");
				report.add("");
			}
				
			Runtime rt = Runtime.getRuntime();

			report.add("-- Disk space analysis -- ");			
			File drive = new File("/");
			
			double total_space=(double)(drive.getTotalSpace() /gb);
			double free_space=(double)(drive.getFreeSpace() /gb);
			
			report.add(String.format("Total space: %.1f GB",total_space));
			report.add(String.format("Free space: %.1f GB",free_space));
			if (free_space<100) report.add("*** Warning: Free space is limited. At least 100 GB is advised. ***");

			try {

				float swapSpaceSize = (float) (os.getTotalSwapSpaceSize()/gb);
				if ((swapSpaceSize<25.0)&&(this.isGcamUSA)) report.add("*** At least 25 GB of swap space are recommended. The model may stop unexpectedly if swap space is exhausted. ***");

				float freeSwapSpace = (float) (os.getFreeSwapSpaceSize()/gb);

				report.add(String.format("Total swap space: %.1f GB",swapSpaceSize));	
				report.add(String.format("Free swap space: %.1f GB",freeSwapSpace));
				report.add("");		
			} catch(Exception e1) {
				report.add("Java version does not support assessing swap space size.");
				report.add("");
			}			
					
			report.add("-- Processor analysis -- ");
			int available_processors = rt.availableProcessors();
			report.add("Available processor cores: "+available_processors);
			float cpu_load=(float)(os.getSystemCpuLoad());
			report.add(String.format("Current usage: %.1f", (float)cpu_load*100.)+"%");
			report.add("");
						
		} catch(Exception e) {
			System.out.println("Problem checking computer attributes (e.g., RAM)");
		}
		
		//utils.displayArrayList(report,"Result of Check Installation",true);
		
		rtn_str=utils.createStringFromArrayList(report);
		
		return rtn_str;
	}


}
