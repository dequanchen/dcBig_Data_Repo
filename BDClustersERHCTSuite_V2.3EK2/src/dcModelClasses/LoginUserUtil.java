package dcModelClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 03/22/2016 
*/ 

public class LoginUserUtil {
	
	public LoginUserUtil() {	
		
	}//end default constructor
	
////////////////////////////////////////////////////////////////////////////////////////////////////////
//V. Hive Beeline Utilities By Login User
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//===========================================
	
	public static ArrayList <String> obtainHiveDatabaseList (String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveDatabaseListingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveDatabase_Listing_TempScriptFile.sh";			
		//prepareFile (localHiveDatabaseListingScriptFilePathAndName,  "Script File For Hive Database Listing ....");
		File aFile = new File (localHiveDatabaseListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Database Listing ....: \n" + localHiveDatabaseListingScriptFilePathAndName);
		}
		
  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseList.txt";
  		String enSvrHiveDatabaseListingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//   -e "show databases;" | grep -v "WARNING\|+-\|database_name" | sed -e 's/\s*//g' -e 's/|//g' > dcTemp.txt;
  		//cat dcTemp.txt
  	    
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");
  		String hiveDbListCmd = hiveServer2BeelineConnString + " -e \"show databases;\" | grep -v \"WARNING\\|+-\\|database_name\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveDatabaseListingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbListCmd: " + hiveDbListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbListCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveDatabaseListingCmds = sb.toString();
		writeDataToAFile(localHiveDatabaseListingScriptFilePathAndName, hiveDatabaseListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveDatabaseListingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Database Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Database Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Database Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Database Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveDatabaseListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveDatabaseListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList <String> currHiveDatabaseList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHiveDatabaseList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHiveDatabaseList.size(): " + currHiveDatabaseList.size());
		
		return currHiveDatabaseList;
	}//end obtainHiveDatabaseList
	
	public static ArrayList <String> obtainHiveSingleDatabaseTableList (String currHiveDatabase, String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableListingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_Listing_TempScriptFile.sh";			
		//prepareFile (localHiveTableListingScriptFilePathAndName,  "Script File For Hive Table Listing ....");
		File aFile = new File (localHiveTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Table Listing ....: \n" + localHiveTableListingScriptFilePathAndName);
		}
		
  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseTableList.txt";
  		String enSvrHiveTableListingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//        -e "use default; show tables;" | grep -v "WARNING\|+-\|tab_name" | sed -e 's/\s*//g' -e 's/|//g' > dcTemp.txt;
  		//cat dcTemp.txt;	
  		
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
  		String hiveDbTableListCmd = hiveServer2BeelineConnString + " -e \"use " + currHiveDatabase + ";show tables;\" | grep -v \"WARNING\\|+-\\|tab_name\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveTableListingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbTableListCmd: " + hiveDbTableListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbTableListCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveTableListingCmds = sb.toString();
		writeDataToAFile(localHiveTableListingScriptFilePathAndName, hiveTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableListingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList <String> currHiveDatabaseTableList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHiveDatabaseTableList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHiveDatabaseTableList.size(): " + currHiveDatabaseTableList.size());
		
		return currHiveDatabaseTableList;
	}//end obtainHiveSingleDatabaseTableList
	
	
	public static long obtainHiveSingleDatabaseTableRowCount (String currHiveDatabaseTable, String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableRowCountingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_RowCounting_TempScriptFile.sh";			
		//prepareFile (localHiveTableRowCountingScriptFilePathAndName,  "Script File For Hive Table RowCounting ....");
		File aFile = new File (localHiveTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Table RowCounting ....: \n" + localHiveTableRowCountingScriptFilePathAndName);
		}
		
  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseTableRowCount.txt";
  		String enSvrHiveTableRowCountingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//       -e "select count(*) from default.employee1;" | grep -v "WARNING\|+-\|_c0 " | sed -e 's/\s*//g' -e 's/|//g' > dcTemp.txt;
  		//cat dcTemp.txt;	
  		
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
  		String hiveDbTableRowCountingCmd = hiveServer2BeelineConnString + " -e \"select count(*) from " + currHiveDatabaseTable + ";\" | grep -v \"WARNING\\|+-\\|_c0\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveTableRowCountingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbTableRowCountingCmd: " + hiveDbTableRowCountingCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbTableRowCountingCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHiveTableRowCountingScriptFilePathAndName, hiveTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableRowCountingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table RowCounting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table RowCounting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Table RowCounting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Table RowCounting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHiveTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHiveTableRowCount = Long.valueOf(line);
			}		
		}		
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHiveTableRowCount: " + currHiveTableRowCount);
		
		return currHiveTableRowCount;
	}//end obtainHiveSingleDatabaseTableRowCount
	
	public static String obtainHiveSingleDatabaseTableFirst2RowsData (String currHiveDatabaseTable, String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_First2RowsDataRetrieving_TempScriptFile.sh";			
		//prepareFile (localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName,  "Script File For Hive Table First2RowsDataRetrieving ....");
		File aFile = new File (localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Table First2RowsDataRetrieving ....: \n" + localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName);
		}
		
  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseTableFirst2RowsData.txt";
  		String enSvrHiveTableFirst2RowsDataRetrievingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//        -e "select * from default.employee1 limit 2;" | grep -v "WARNING\|+-\|db_name" | sed -e  's/|//g' -e 's/^\s*//'  > dcTemp.txt;
  		//cat dcTemp.txt;	
  		
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
  		String hiveDbTableFirst2RowsDataCmd = hiveServer2BeelineConnString + " -e \"select * from " + currHiveDatabaseTable + " limit 2;\" | grep -v \"WARNING\\|+-\\|db_name\" | sed -e 's/^\\s*//' -e 's/|//g' > " + enSvrHiveTableFirst2RowsDataRetrievingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbTableFirst2RowsDataCmd: " + hiveDbTableFirst2RowsDataCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbTableFirst2RowsDataCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveTableFirst2RowsDataRetrievingCmds = sb.toString();
		writeDataToAFile(localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName, hiveTableFirst2RowsDataRetrievingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableFirst2RowsDataRetrievingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table First2RowsDataRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table First2RowsDataRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Table First2RowsDataRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Table First2RowsDataRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHiveTableTargetRowsData = "";
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			lineCount++;
			if (!line.isEmpty()){
				if (lineCount == 1){
					currHiveTableTargetRowsData += line.trim();
				} else {
					currHiveTableTargetRowsData += "\n" + line.trim();
				}
				
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHiveTableTargetRowsData: \n" + currHiveTableTargetRowsData);		
		
		return currHiveTableTargetRowsData;
	}//end obtainHiveSingleDatabaseTableFirst2RowsData
	
	
	
	public static String obtainHiveSingleDatabaseSchemaString (String currHiveDatabase, String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_DatabaseSchemaRetrieving_TempScriptFile.sh";			
		//prepareFile (localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName,  "Script File For Hive DatabaseSchemaRetrieving ....");
		File aFile = new File (localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Table DatabaseSchemaRetrieving ....: \n" + localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName);
		}
		
  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseSchema.txt";
  		String enSvrHiveTableDatabaseSchemaRetrievingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//		-e "describe database webhcat_db;" | grep -v "WARNING\|+-\|db_name  " | sed -e  's/|//g' -e 's/^\s*//'  > dcTemp.txt;
  		//cat dcTemp.txt;
  		
  		
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
  		String hiveDbSchemaCmd = hiveServer2BeelineConnString + " -e \"describe database " + currHiveDatabase + ";\" | grep -v \"WARNING\\|+-\\|db_name\" | sed -e 's/^\\s*//' -e 's/|//g' > " + enSvrHiveTableDatabaseSchemaRetrievingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbSchemaCmd: " + hiveDbSchemaCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbSchemaCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveTableDatabaseSchemaRetrievingCmds = sb.toString();
		writeDataToAFile(localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName, hiveTableDatabaseSchemaRetrievingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableDatabaseSchemaRetrievingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table DatabaseSchemaRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table DatabaseSchemaRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Table DatabaseSchemaRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Table DatabaseSchemaRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHiveDBSchemaStr = "";
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			lineCount++;
			if (!line.isEmpty()){
				if (lineCount == 1){
					currHiveDBSchemaStr += line.trim();
				} else {
					currHiveDBSchemaStr += "\n" + line.trim();
				}
				
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHiveDBSchemaStr: \n" + currHiveDBSchemaStr);		
		
		return currHiveDBSchemaStr;
	}//end obtainHiveSingleDatabaseSchemaString
	
	

	public static String obtainHiveSingleDatabaseTableCreateTableString (String currHiveDatabaseTable, String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableCreateTableCmdRetrievingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_CreateTableCmdRetrieving_TempScriptFile.sh";			
		//prepareFile (localHiveTableCreateTableCmdRetrievingScriptFilePathAndName,  "Script File For Hive Table CreateTableCmdRetrieving ....");
		File aFile = new File (localHiveTableCreateTableCmdRetrievingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Table CreateTableCmdRetrieving ....: \n" + localHiveTableCreateTableCmdRetrievingScriptFilePathAndName);
		}
		
  		String tempHiveTableListRecFileName = "tempHiveCreateTableString.txt";
  		String enSvrHiveTableCreateTableCmdRetrievingFilePathAndName = enServerScriptFileDirectory + tempHiveTableListRecFileName; 
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//        -e "SHOW CREATE TABLE default.employee1;" | grep -v "WARNING\|+-\|createtab_stmt " | sed -e  's/|//g' -e 's/^\s*//'  > dcTemp.txt;
  		//cat dcTemp.txt;	
  		
  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
  		String hiveDbTableCreateTableStringCmd = hiveServer2BeelineConnString + " -e \"SHOW CREATE TABLE " + currHiveDatabaseTable + ";\" | grep -v \"WARNING\\|+-\\|createtab_stmt\" | sed -e 's/^\\s*//' -e 's/|//g' > " + enSvrHiveTableCreateTableCmdRetrievingFilePathAndName;
  		
  		System.out.println("\n*** hiveDbTableCreateTableStringCmd: " + hiveDbTableCreateTableStringCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");		
		
		sb.append(hiveDbTableCreateTableStringCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hiveTableCreateTableCmdRetrievingCmds = sb.toString();
		writeDataToAFile(localHiveTableCreateTableCmdRetrievingScriptFilePathAndName, hiveTableCreateTableCmdRetrievingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableCreateTableCmdRetrievingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table CreateTableCmdRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table CreateTableCmdRetrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveTableListRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Table CreateTableCmdRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Table CreateTableCmdRetrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHiveTableCreateTableStr = "";
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			lineCount++;
			if (!line.isEmpty()){
				if (lineCount == 1){
					currHiveTableCreateTableStr += line.trim();
				} else {
					currHiveTableCreateTableStr += "\n" + line.trim();
				}
				
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHiveTableCreateTableStr: \n" + currHiveTableCreateTableStr);		
		
		return currHiveTableCreateTableStr;
	}//end obtainHiveSingleDatabaseTableCreateTableString
	

	public static ArrayList <String> obtainHiveAllDatabaseTableList_Slow (String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		//2.
		ArrayList <String> currHiveAllDatabaseList = LoginUserUtil.obtainHiveDatabaseList (hiveServer2BeelineConnString, hiveInternalAuthCmd, enServerScriptFileDirectory,winLocalHiveRecordInfoFolder,bdENCmdFactory );
		ArrayList <String> currHiveAllDatabaseTableList = new ArrayList<String>();
		int dbCount= 0;
		for (String currHiveDatabase: currHiveAllDatabaseList){
			dbCount++;
			System.out.println("(" + dbCount + ") " + currHiveDatabase);
			
			//2.1
			String localHiveTableListingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_Listing_TempScriptFile.sh";			
			//prepareFile (localHiveTableListingScriptFilePathAndName,  "Script File For Hive Table Listing ....");
			File aFile = new File (localHiveTableListingScriptFilePathAndName);
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created script file for Temporary Hive Table Listing ....: \n" + localHiveTableListingScriptFilePathAndName);
			}
			
	  		String tempHiveBeelineCmdRecFileName = "tempHiveDatabaseTableList.txt";
	  		String enSvrHiveTableListingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineCmdRecFileName; 
	  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
	  		//        -e "use default; show tables;" | grep -v "WARNING\|+-\|tab_name" | sed -e 's/\s*//g' -e 's/|//g' > dcTemp.txt;
	  		//cat dcTemp.txt;	
	  		
	  		hiveServer2BeelineConnString = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u");  
	  		String hiveDbTableListCmd = hiveServer2BeelineConnString + " -e \"use " + currHiveDatabase + ";show tables;\" | grep -v \"WARNING\\|+-\\|tab_name\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveTableListingFilePathAndName;
	  		
	  		System.out.println("\n*** hiveDbTableListCmd: " + hiveDbTableListCmd);
	  		  		
	  		StringBuilder sb = new StringBuilder();			
					
			sb.append("cd " + enServerScriptFileDirectory + ";\n");		
			sb.append(hiveInternalAuthCmd + "; \n");		
			
			sb.append(hiveDbTableListCmd + "; \n");			
			//sb.append("kdestroy;\n");
			
			String hiveTableListingCmds = sb.toString();
			writeDataToAFile(localHiveTableListingScriptFilePathAndName, hiveTableListingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
			int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableListingScriptFilePathAndName, 
					winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

			if (exitVal1 == 0 ){
				System.out.println("\n*** Success - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
			} else {
				System.out.println("\n*** Failed - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
			}
	  		  		
	  		//2.2 	
			int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineCmdRecFileName,  
			enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
				
			if (exitVal2 == 0 ){
				System.out.println("\n*** Success - Copying Hive Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
			} else {
				System.out.println("\n*** Failed - Copying Hive Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
			}
			
			//2.3
			String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineCmdRecFileName;
			
			FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
			BufferedReader br = new BufferedReader(aFileReader);		
			
			String line = "";
			ArrayList <String> currHiveDatabaseTableList = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()){
					currHiveDatabaseTableList.add(currHiveDatabase + "." +line.trim());
				}		
			}
			br.close();
			aFileReader.close();
			System.out.println("\n*** currHiveDatabaseTableList.size(): " + currHiveDatabaseTableList.size());
			currHiveAllDatabaseTableList.addAll(currHiveDatabaseTableList);
			
		}		
		System.out.println("\n*** currHiveAllDatabaseTableList.size(): " + currHiveAllDatabaseTableList.size());
		
		return currHiveAllDatabaseTableList;
	}//end obtainHiveAllDatabaseTableList_Slow
	
	
	
	public static ArrayList <String> obtainHiveAllDatabaseTableList_Fast (String hiveServer2BeelineConnString, String hiveInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHiveRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
				
		//2
		String localHiveDbTableListingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveDbTable_Listing_TempScriptFile.sh";			
		//prepareFile (localHiveTableListingScriptFilePathAndName,  "Script File For Hive Table Listing ....");
		File aFile = new File (localHiveDbTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary Hive Db.Table Listing ....: \n" + localHiveDbTableListingScriptFilePathAndName);
		}
		
		String hiveServer2BeelineConnString_mod = hiveServer2BeelineConnString.replace("beeline -u", "beeline --silent=true --showWarnings=false -u") + " -e "; 
		
  		String tempHiveBeelineDbsCmdRecFileName = "tempHiveDatabaseList.txt";
  		String enSvrHiveDbListingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineDbsCmdRecFileName; 
  		
  		String tempHiveBeelineDbTablesCmdRecFileName = "tempHiveDatabaseTableList.txt";
  		String enSvrHiveTableListingFilePathAndName = enServerScriptFileDirectory + tempHiveBeelineDbTablesCmdRecFileName; 
  		
  		//beeline --silent=true --showWarnings=false -u  'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' 
  		//        -e "show databases;" | grep -v "WARNING\|+-\|tab_name" | sed -e 's/\s*//g' -e 's/|//g' > dcTemp.txt;
  		//cat dcTemp.txt;	
  		  		 
//  		String hiveDbListCmd_Core = "\"show databases;\" | grep -v \"WARNING\\|+-\\|database_name\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveDbListingFilePathAndName;
//  		System.out.println("\n*** hiveDbListCmd: " + hiveDbListCmd);
//  		
//  		String hiveDbListFromRecFile = "hiveDatabases=`cat enSvrHiveTableListingFilePathAndName`";
//  		
//  		String hiveDbTableListCmd = hiveServer2BeelineConnString + " -e \"use " + currHiveDatabase + ";show tables;\" | grep -v \"WARNING\\|+-\\|tab_name\" | sed -e 's/\\s*//g' -e 's/|//g' > " + enSvrHiveTableListingFilePathAndName;
//  		
//  		System.out.println("\n*** hiveDbTableListCmd: " + hiveDbTableListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hiveInternalAuthCmd + "; \n");
		
		sb.append("hiveDbListFilePathAndName=" + enSvrHiveDbListingFilePathAndName + "; \n");
		sb.append("hiveDbTableListFilePathAndName=" + enSvrHiveTableListingFilePathAndName + "; \n");
		sb.append("hiveServer2BeelineConnString=\"" + hiveServer2BeelineConnString_mod + "\"; \n");
		
		sb.append("hiveDbListCmd_Core=\"\\\"show databases;\\\" | grep -v \\\"WARNING\\|+-\\|database_name\\\" | sed -e 's/\\\\s*//g' -e 's/|//g' > $hiveDbListFilePathAndName\"; \n");
		sb.append("hiveDbListCmd=$hiveServer2BeelineConnString$hiveDbListCmd_Core; \n");
		sb.append("echo $hiveDbListCmd; \n");
		sb.append("bash -c \"$hiveDbListCmd\"; \n");
		sb.append("cat $hiveDbListFilePathAndName; \n");
		
		sb.append("hiveDatabases=`cat $hiveDbListFilePathAndName`; \n");
		sb.append("echo $hiveDatabases; \n");
		
		sb.append("i=1 \n");
		sb.append("for currHiveDB in $hiveDatabases \n");
		sb.append("do \n");
		sb.append("j=$((i++)); \n");
		sb.append("hiveDbTableListCmd_Core1=\"\\\"use $currHiveDB;show tables;\\\" | grep -v \\\"WARNING\\|+-\\|tab_name\\\" | sed -e 's/\\\\s*//g' -e 's/|//g' -e 's/^/$currHiveDB\\./g' > $hiveDbTableListFilePathAndName\"; \n");
		sb.append("hiveDbTableListCmd_Core2=\"\\\"use $currHiveDB;show tables;\\\" | grep -v \\\"WARNING\\|+-\\|tab_name\\\" | sed -e 's/\\\\s*//g' -e 's/|//g' -e 's/^/$currHiveDB\\./g' >> $hiveDbTableListFilePathAndName\"; \n");
		sb.append("if [ \"$j\" == 1 ] \n");
		sb.append(" then \n");
		sb.append("   echo \" ($j)examing currHiveDB - $currHiveDB\"; \n");
		sb.append("   hiveDbTableListCmd=$hiveServer2BeelineConnString$hiveDbTableListCmd_Core1; \n");
		sb.append("   echo $hiveDbTableListCmd; \n");
		sb.append("   bash -c \"$hiveDbTableListCmd\"; \n");
		sb.append(" else \n");
		sb.append("   echo \" ($j)examing currHiveDB - $currHiveDB\"; \n");
		sb.append("   hiveDbTableListCmd=$hiveServer2BeelineConnString$hiveDbTableListCmd_Core2; \n");
		sb.append("   echo $hiveDbTableListCmd; \n");
		sb.append("   bash -c \"$hiveDbTableListCmd\"; \n");		
		sb.append("fi \n");
		sb.append("done \n");
		sb.append("cat $hiveDbTableListFilePathAndName; \n");		
		//sb.append("kdestroy;\n");
		
		String hiveTableListingCmds = sb.toString();
		writeDataToAFile(localHiveDbTableListingScriptFilePathAndName, hiveTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localHiveDbTableListingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveDbTableListingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Db.Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Db.Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//3. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveBeelineDbTablesCmdRecFileName,  
		enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Db.Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Db.Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
		
		//4.
		String winLocalHiveTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveBeelineDbTablesCmdRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";		
		ArrayList <String> currHiveAllDatabaseTableList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHiveAllDatabaseTableList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHiveAllDatabaseTableList.size(): " + currHiveAllDatabaseTableList.size());
		
		return currHiveAllDatabaseTableList;
	}//end obtainHiveAllDatabaseTableList_Fast
	

	public static Map<String,String> obtainSrcToDestHiveDatabasesMap (ArrayList<String> srcHiveDatabaseList, 
																		 ArrayList<String> destHiveDatabaseList){
		Map<String,String> currSrcToDestHiveDatabasesMap = new LinkedHashMap<String,String>();
		for (String tempSrcDatabase : srcHiveDatabaseList){			
			String tempDestDatabase = findTargetItemrFromAList (destHiveDatabaseList, tempSrcDatabase );
			currSrcToDestHiveDatabasesMap.put(tempSrcDatabase, tempDestDatabase);
		}
		return currSrcToDestHiveDatabasesMap;
	}//end obtainSrcToDestHiveDatabasesMap
	

	public static Map<String,String> obtainSrcToDestHiveDatabaseTablesMap (ArrayList<String> srcHiveDatabaseTableList, 
																		 ArrayList<String> destHiveDatabaseTableList){
		Map<String,String> currSrcToDestHiveDatabaseTablesMap = new LinkedHashMap<String,String>();
		for (String tempSrcDatabaseTable : srcHiveDatabaseTableList){			
			String tempDestDatabaseTable = findTargetItemrFromAList (destHiveDatabaseTableList, tempSrcDatabaseTable );
			currSrcToDestHiveDatabaseTablesMap.put(tempSrcDatabaseTable, tempDestDatabaseTable);
		}
		return currSrcToDestHiveDatabaseTablesMap;
	}//end obtainSrcToDestHiveDatabaseTablesMap
	
///-------------------------------	
	public static long countHiveTableRows (String hiveTableName, String ambariQaInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHiveRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableRowCountingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_RowCounting_ScriptFile.sh";			
		//prepareFile (localHiveTableRowCountingScriptFilePathAndName,  "Script File For Hive Table Row Counting ....");
		File aFile = new File (localHiveTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Hive Table Row Counting ....: \n" + localHiveTableRowCountingScriptFilePathAndName);
		}
				
  		String tempHiveRowCountRecFileName = "tempHiveTableRowCount.txt";
  		String srcEnSvrHiveTableRowCountingFilePathAndName = enServerScriptFileDirectory + tempHiveRowCountRecFileName; 
  		//usr/bin/hive -S -e  "select count (*) from regresources_hive" | tail -2;

  		String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
  		String hbaseRowCountCmd = hiveShellInitiateStr + "\"select count(*) from " + hiveTableName + "\"  | tail -2 > " + srcEnSvrHiveTableRowCountingFilePathAndName;
  		System.out.println("\n*** hbaseRowCountCmd: " + hbaseRowCountCmd);
  		
  		StringBuilder sb = new StringBuilder();			
		
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(ambariQaInternalAuthCmd + "; \n");		
		
		sb.append("set hive.execution.engine=tez;\n");		
		sb.append(hbaseRowCountCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHiveTableRowCountingScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableRowCountingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveRowCountRecFileName,  
				enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHiveTableRowCountResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveRowCountRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHiveTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHiveTableRowCount = Long.valueOf(line);
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHiveTableRowCount: " + currHiveTableRowCount);
		
		return currHiveTableRowCount;
	}//end countHiveTableRows
	
	
	public static ArrayList<String> obtainHiveTableList (String ambariQaInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHiveRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHiveRecordInfoFolder = winLocalHiveRecordInfoFolder.replace("/", "\\");
		if (!winLocalHiveRecordInfoFolder.endsWith("\\")){
			winLocalHiveRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHiveRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHiveTableTableListingScriptFilePathAndName = winLocalHiveRecordInfoFolder + "dcMCHadoop_HiveTable_TableListing_ScriptFile.sh";			
		//prepareFile (localHiveTableTableListingScriptFilePathAndName,  "Script File For Hive Table Listing ....");
		File aFile = new File (localHiveTableTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Hive Table Listing ....: \n" + localHiveTableTableListingScriptFilePathAndName);
		}
				
  		String tempHiveTableListRecFileName = "tempHiveTableList.txt";
  		String srcEnSvrHiveTableTableListingFilePathAndName = enServerScriptFileDirectory + tempHiveTableListRecFileName; 
  		//usr/bin/hive -S -e  "select count (*) from regresources_hive" | tail -2;

  		String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
  		String hbaseTableListCmd = hiveShellInitiateStr + "\"show tables\" > " + srcEnSvrHiveTableTableListingFilePathAndName;
  		System.out.println("\n*** hbaseTableListCmd: " + hbaseTableListCmd);
  		
  		StringBuilder sb = new StringBuilder();			
		
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(ambariQaInternalAuthCmd + "; \n");		
		
		//sb.append("set hive.execution.engine=tez;\n");		
		sb.append(hbaseTableListCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		String hbaseTableTableListingCmds = sb.toString();
		writeDataToAFile(localHiveTableTableListingScriptFilePathAndName, hbaseTableTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHiveTableTableListingScriptFilePathAndName, 
				winLocalHiveRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hive Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHiveTableListRecFileName,  
				enServerScriptFileDirectory, winLocalHiveRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hive Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hive Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHiveRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHiveTableTableListResultsFileFullPathAndName = winLocalHiveRecordInfoFolder + tempHiveTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHiveTableTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList<String> hiveTableList = new  ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				hiveTableList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHiveTableTableList: " + currHiveTableTableList);
		
		return hiveTableList;
	}//end obtainHiveTableList
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//IV. HBase Utilities By Login User	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//===========================================	
	public static ArrayList <String> obtainHBaseAllNamespaceList (String hbaseInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHbaseRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseNamespaceListingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseNamespace_Listing_TempScriptFile.sh";			
		//prepareFile (localHBaseNamespaceListingScriptFilePathAndName,  "Script File For HBase Namespace Listing ....");
		File aFile = new File (localHBaseNamespaceListingScriptFilePathAndName);
		
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Namespace Listing ....: \n" + localHBaseNamespaceListingScriptFilePathAndName);
		}
		
  		String tempHBaseNamespaceListRecFileName = "tempHbaseNamespaceList.txt";
  		String enSvrHBaseNamespaceListingFilePathAndName = enServerScriptFileDirectory + tempHBaseNamespaceListRecFileName; 
  		//echo "list_namespace" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|list_namespace\|NAMESPACE\|row(s)" | sed -e '/^\s*$/d';
  		//cat dcTemp.txt
  	    

  		String hbaseListCmd = "echo \"list_namespace\" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|list_namespace\\|NAMESPACE\\|row(s)\" | sed -e '/^\\s*$/d' > " + enSvrHBaseNamespaceListingFilePathAndName;
  		
  		System.out.println("\n*** hbaseListCmd: " + hbaseListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseListCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hbaseNamespaceListingCmds = sb.toString();
		writeDataToAFile(localHBaseNamespaceListingScriptFilePathAndName, hbaseNamespaceListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseNamespaceListingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Namespace Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Namespace Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseNamespaceListRecFileName,  
		enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Namespace Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Namespace Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHBaseNamespaceListResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseNamespaceListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseNamespaceListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList <String> currHBaseNamespaceList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseNamespaceList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseNamespaceList.size(): " + currHBaseNamespaceList.size());
		
		return currHBaseNamespaceList;
	}//end obtainHBaseAllNamespaceList
	
	

	public static ArrayList <String> obtainHBaseAllNameSpaceTableList (String hbaseInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHbaseRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableListingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_Listing_TempScriptFile.sh";			
		//prepareFile (localHBaseTableListingScriptFilePathAndName,  "Script File For HBase Table Row Listing ....");
		File aFile = new File (localHBaseTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Listing ....: \n" + localHBaseTableListingScriptFilePathAndName);
		}
		
  		String tempHBaseTableListRecFileName = "tempHbaseNameSpaceTableList.txt";
  		String enSvrHBaseTableListingFilePathAndName = enServerScriptFileDirectory + tempHBaseTableListRecFileName; 
  		//echo "list" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|list\|TABLE\|row(s)" | sed -e '/^\s*$/d' >  $hbaseTableListFileName;
  		//cat $hbaseTableListFileName;
  	    

  		String hbaseListCmd = "echo \"list\" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|list\\|row(s)\" | sed -e '/^\\s*$/d' > " + enSvrHBaseTableListingFilePathAndName;
  		
  		System.out.println("\n*** hbaseListCmd: " + hbaseListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseListCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hbaseTableListingCmds = sb.toString();
		writeDataToAFile(localHBaseTableListingScriptFilePathAndName, hbaseTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableListingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseTableListRecFileName,  
		enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHBaseTableListResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList <String> currHBaseTableList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				String tempLine = line.trim();
				if (!tempLine.equals("TABLE") && !(tempLine.contains("[") && tempLine.contains(",")) ){
					currHBaseTableList.add(tempLine);
				}				
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseTableList.size(): " + currHBaseTableList.size());
		
		return currHBaseTableList;
	}//end obtainHBaseAllNameSpaceTableList
	
	public static ArrayList <String> obtainHBaseSingleNamespaceTableList (String currNamespace, String hbaseInternalAuthCmd, String enServerScriptFileDirectory,
			String winLocalHbaseRecordInfoFolder, ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableListingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_Listing_TempScriptFile.sh";			
		//prepareFile (localHBaseTableListingScriptFilePathAndName,  "Script File For HBase Table Row Listing ....");
		File aFile = new File (localHBaseTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Listing ....: \n" + localHBaseTableListingScriptFilePathAndName);
		}
		
  		String tempHBaseTableListRecFileName = "tempHbaseNamespaceTableList.txt";
  		String enSvrHBaseTableListingFilePathAndName = enServerScriptFileDirectory + tempHBaseTableListRecFileName; 
  		//echo "list_namespace_tables 'default'" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|_namespace\|TABLE\|row(s)" | sed -e '/^\s*$/d' >  dcTemp.txt;
  		//cat dcTemp.txt;
  	    

  		String hbaseListCmd = "echo \"list_namespace_tables '" + currNamespace + "' \" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|_namespace\\|TABLE\\|row(s)\" | sed -e '/^\\s*$/d' > " + enSvrHBaseTableListingFilePathAndName;
  		
  		System.out.println("\n*** hbaseListCmd: " + hbaseListCmd);
  		  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseListCmd + "; \n");			
		//sb.append("kdestroy;\n");
		
		String hbaseTableListingCmds = sb.toString();
		writeDataToAFile(localHBaseTableListingScriptFilePathAndName, hbaseTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableListingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2. 	
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseTableListRecFileName,  
		enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
			
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Table Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
		
		//3.
		String winLocalHBaseTableListResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList <String> currHBaseNamespaceTableList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseNamespaceTableList.add(line.trim());
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseNamespaceTableList.size(): " + currHBaseNamespaceTableList.size());
		
		return currHBaseNamespaceTableList;
	}//end obtainHBaseSingleNamespaceTableList
	
	public static long countHBaseTableRows (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableRowCountingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_RowCounting_ScriptFile.sh";			
		//prepareFile (localHBaseTableRowCountingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for HBase Table Row Counting ....: \n" + localHBaseTableRowCountingScriptFilePathAndName);
		}
		
  		String tempHBaseRowCountRecFileName = "tempHbaseTableCount.txt";
  		String srcEnSvrHBaseTableRowCountingFilePathAndName = enServerScriptFileDirectory + tempHBaseRowCountRecFileName; 
  		//echo "count 'registry-lock'" | hbase shell | grep 'row(s)' | cut -d' ' -f1 > /data/home/m041785/test/keytabs/tempHbaseTableCount.txt;
  		String hbaseRowCountCmd = "echo \"count '" + hbaseTableName + "'\" | hbase shell | grep 'row(s)' | cut -d' ' -f1 > " + srcEnSvrHBaseTableRowCountingFilePathAndName;
  		System.out.println("\n*** hbaseRowCountCmd: " + hbaseRowCountCmd);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseRowCountCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHBaseTableRowCountingScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableRowCountingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseRowCountRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseRowCountRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHBaseTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseTableRowCount = Long.valueOf(line);
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHBaseTableRowCount: " + currHBaseTableRowCount);
		
		return currHBaseTableRowCount;
	}//end countHBaseTableRows
	
	public static long countHBaseTableRowsByHBaseTableHiveView (String hbaseTableHiveViewName, String ambariQaInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableRowCountingByHiveViewScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_RowCounting_ByHiveView_ScriptFile.sh";			
		//prepareFile (localHBaseTableRowCountingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableRowCountingByHiveViewScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for HBase Table Row Counting By Hive View ....: \n" + localHBaseTableRowCountingByHiveViewScriptFilePathAndName);
		}
				
  		String tempHBaseRowCountRecFileName = "tempHbaseTableByHiveViewCount.txt";
  		String srcEnSvrHBaseTableRowCountingFilePathAndName = enServerScriptFileDirectory + tempHBaseRowCountRecFileName; 
  		//usr/bin/hive -S -e  "select count (*) from regresources_hive" | tail -2;

  		String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
  		String hbaseRowCountCmd = hiveShellInitiateStr + "\"select count(*) from " + hbaseTableHiveViewName + "\"  | tail -2 > " + srcEnSvrHBaseTableRowCountingFilePathAndName;
  		System.out.println("\n*** hbaseRowCountCmd: " + hbaseRowCountCmd);
  		
  		StringBuilder sb = new StringBuilder();			
		
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(ambariQaInternalAuthCmd + "; \n");		
		
		sb.append("set hive.execution.engine=tez;\n");		
		sb.append(hbaseRowCountCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHBaseTableRowCountingByHiveViewScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableRowCountingByHiveViewScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Row-Counting By Hive-View on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Row-Counting By Hive-View on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseRowCountRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseRowCountRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHBaseTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseTableRowCount = Long.valueOf(line);
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHBaseTableRowCount: " + currHBaseTableRowCount);
		
		return currHBaseTableRowCount;
	}//end countHBaseTableRowsByHBaseTableHiveView
	
	////--------------------------------------------------

	public static int getHBaseTableMaxVersionNumber (String hbaseTableSchema){
		int maxVersionNumber = 0;
		if (hbaseTableSchema.contains(",")){
			String[] lineSplit = hbaseTableSchema.split(",");							
			
			for (int i = 0; i < lineSplit.length; i++) {
				String lineSpittedField = lineSplit[i].trim();
				if (lineSpittedField.contains("VERSIONS => ")){
					String[] fieldSplit = lineSpittedField.split("VERSIONS => ");
					String tempVersionStr = fieldSplit[1].trim();
					String temp = tempVersionStr.replaceAll("'", "");
					int tempVersionNumber = Integer.valueOf(temp);
					if (tempVersionNumber > maxVersionNumber ){
						maxVersionNumber = tempVersionNumber;
					}									
				}
			}
		}
		//String maxVersionNumberStr = Integer.toString(maxVersionNumber);
		
		return maxVersionNumber;		
	}//end getHBaseTableMaxVersionNumber
	
	public static String obtainHBaseTableSchema (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableRowCountingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_Schema_TempScriptFile.sh";			
		//prepareFile (localHBaseTableRowCountingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Schema Retrieving ....: \n" + localHBaseTableRowCountingScriptFilePathAndName);
		}
		
  		String tempHBaseRowCountRecFileName = "tempHbaseTableSchema.txt";
  		String enSvrHBaseTableRowCountingFilePathAndName = enServerScriptFileDirectory + tempHBaseRowCountRecFileName; 
  		//echo "describe 'default:employee1'" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|default:employee1\|DESCRIPTION\|describe\|Table\|row(s)" | sed -e '/^\s*$/d' >  dcTemp.txt;
  		//cat dcTemp.txt; 

  		String hbaseRowCountCmd = "echo \"describe '" + hbaseTableName + "'\" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|default:employee1\\|DESCRIPTION\\|describe\\|Table\\|row(s)\" | sed -e '/^\\s*$/d' > " + enSvrHBaseTableRowCountingFilePathAndName;
  		
  		//String tempHBaseRowCountRecFileName2 = "tempHbaseTableCount2.txt";
  		//String enSvrHBaseTableRowCountingFilePathAndName2 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName2;   		
  		//String hbaseRowCountCmd2 = "cat  " + enSvrHBaseTableRowCountingFilePathAndName1 + " | grep 'ROWS=' | sed -e 's/^\\s\\+//g' -e 's/ROWS=//g' > " + enSvrHBaseTableRowCountingFilePathAndName2;
  		
  		System.out.println("\n*** hbaseRowCountCmd: " + hbaseRowCountCmd);
  		//System.out.println("\n*** hbaseRowCountCmd2: " + hbaseRowCountCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseRowCountCmd + "; \n");	
		//sb.append(hbaseRowCountCmd2 + "; \n");	
		//sb.append("rm " + enSvrHBaseTableRowCountingFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHBaseTableRowCountingScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableRowCountingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Schema-Retrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Schema-Retrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseRowCountRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Schema-Retrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Schema-Retrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseRowCountRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHBaseTableSchema = "'" + hbaseTableName + "', ";
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				if (!line.trim().equalsIgnoreCase(hbaseTableName)){
					currHBaseTableSchema += line;
				}				
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseTableSchema: " + currHBaseTableSchema);
		
		return currHBaseTableSchema;
	}//end obtainHBaseTableSchema
	
	public static String obtainHBaseTableCLICreatingString (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableSchemaRetrievingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_SchemaRetrieving_TempScriptFile.sh";			
		//prepareFile (localHBaseTableSchemaRetrievingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableSchemaRetrievingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Schema Retrieving ....: \n" + localHBaseTableSchemaRetrievingScriptFilePathAndName);
		}
		
  		String tempHBaseSchemaRetrievingRecFileName = "tempHbaseTableSchema.txt";
  		String enSvrHBaseTableSchemaRetrievingFilePathAndName = enServerScriptFileDirectory + tempHBaseSchemaRetrievingRecFileName; 
  		//echo "describe 'default:employee1'" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|default:employee1\|DESCRIPTION\|describe\|Table\|row(s)" | sed -e '/^\s*$/d' >  dcTemp.txt;
  		//cat dcTemp.txt; 

  		String hbaseSchemaRetrievingCmd = "echo \"describe '" + hbaseTableName + "'\" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|default:employee1\\|DESCRIPTION\\|describe\\|Table\\|row(s)\" | sed -e '/^\\s*$/d' > " + enSvrHBaseTableSchemaRetrievingFilePathAndName;
  		
  		//String tempHBaseSchemaRetrievingRecFileName2 = "tempHbaseTableCount2.txt";
  		//String enSvrHBaseTableSchemaRetrievingFilePathAndName2 = enServerScriptFileDirectory + tempHBaseSchemaRetrievingRecFileName2;   		
  		//String hbaseSchemaRetrievingCmd2 = "cat  " + enSvrHBaseTableSchemaRetrievingFilePathAndName1 + " | grep 'ROWS=' | sed -e 's/^\\s\\+//g' -e 's/ROWS=//g' > " + enSvrHBaseTableSchemaRetrievingFilePathAndName2;
  		
  		System.out.println("\n*** hbaseSchemaRetrievingCmd: " + hbaseSchemaRetrievingCmd);
  		//System.out.println("\n*** hbaseSchemaRetrievingCmd2: " + hbaseSchemaRetrievingCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseSchemaRetrievingCmd + "; \n");	
		//sb.append(hbaseSchemaRetrievingCmd2 + "; \n");	
		//sb.append("rm " + enSvrHBaseTableSchemaRetrievingFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableSchemaRetrievingCmds = sb.toString();
		writeDataToAFile(localHBaseTableSchemaRetrievingScriptFilePathAndName, hbaseTableSchemaRetrievingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableSchemaRetrievingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Schema-Retrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Schema-Retrieving on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseSchemaRetrievingRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Schema-Retrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Schema-Retrieving Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableSchemaRetrievingResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseSchemaRetrievingRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableSchemaRetrievingResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHBaseTableCliCreatingString = "create '" + hbaseTableName + "', ";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()){
				if (!line.trim().equalsIgnoreCase(hbaseTableName)){
					if (line.contains("'FOREVER'")){
						line = line.replaceAll("'FOREVER'", "org.apache.hadoop.hbase.HConstants::FOREVER");
					}
					if (line.startsWith("")){
						line = line.replace("{", ", {");
					}
					currHBaseTableCliCreatingString += line;
				}				
			}		
		}
		br.close();
		aFileReader.close();
		currHBaseTableCliCreatingString = currHBaseTableCliCreatingString.replace(", ,", ",");
		System.out.println("\n*** currHBaseTableCliCreatingString: " + currHBaseTableCliCreatingString);
		
		return currHBaseTableCliCreatingString;
	}//end obtainHBaseTableCLICreatingString

	public static String createHBaseTableUsingSrcSchemaOnTargetCluster (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory, String hbaseTableCliCreatingString) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableCreationScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_Creation_TempScriptFile.sh";			
		//prepareFile (localHBaseTableCreationScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableCreationScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Creation ....: \n" + localHBaseTableCreationScriptFilePathAndName);
		}
		
  		String tempHBaseTableCreationRecFileName = "tempHbaseTableCreationCreatedTable.txt";
  		String enSvrHBaseTableCreationFilePathAndName = enServerScriptFileDirectory + tempHBaseTableCreationRecFileName;
  		//echo "exists 'employee1_bkp'" | hbase shell;
  		//echo "is_enabled 'employee1_bkp'" | hbase shell;
  		//echo "describe 'employee1_bkp'" | hbase shell;
  		//echo "count 'employee1_bkp'" | hbase shell;
  		//-----------------------------------------------
  		//Create table:
  		//echo "disable 'employee1_bkp'" | hbase shell;
  		//echo "drop 'employee1_bkp'" | hbase shell;
  		//##echo "create 'employee1_bkp', {NAME => 'cfa', BLOOMFILTER => 'ROW', VERSIONS => '5', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => org.apache.hadoop.hbase.HConstants::FOREVER, COMPRESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'} " | hbase shell;
  		//echo "create 'employee1_bkp', {NAME => 'cfa', BLOOMFILTER => 'ROW', VERSIONS => '5', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', TTL => org.apache.hadoop.hbase.HConstants::FOREVER, COMPRESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'} " | hbase shell | grep "Hbase::Table" | sed -e 's/Hbase::Table//g' -e 's/\s\+-\s\+//g' > dcTemp.txt;
  		//cat dcTemp.txt; 
  		//-----------------------------------------------
  		//hbase org.apache.hadoop.hbase.mapreduce.CopyTable --new.name=employee1_bkp --peer.adr=hdpr05mn01.mayo.edu,hdpr05mn02.mayo.edu,hdpr05mn03.mayo.edu:2181:/hbase-secure employee1;
  		//	Map input records=6
  	    //	Map output records=6 
  		
  		String hbaseTableDisableCmd = "echo \"disable '" + hbaseTableName + "'\" | hbase shell";
  		String hbaseTableDropCmd = "echo \"drop '" + hbaseTableName + "'\" | hbase shell";
  		String hbaseTableCreateCmd = "echo \"" + hbaseTableCliCreatingString + "\" | hbase shell | grep \"Hbase::Table\" | sed -e 's/Hbase::Table//g' -e 's/\\s\\+-\\s\\+//g' > " + enSvrHBaseTableCreationFilePathAndName;
  		
  		//String tempHBaseRowCountRecFileName2 = "tempHbaseTableCount2.txt";
  		//String enSvrHBaseTableCreationFilePathAndName2 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName2;   		
  		//String hbaseRowCountCmd2 = "cat  " + enSvrHBaseTableCreationFilePathAndName1 + " | grep 'ROWS=' | sed -e 's/^\\s\\+//g' -e 's/ROWS=//g' > " + enSvrHBaseTableCreationFilePathAndName2;
  		
  		System.out.println("\n*** hbaseTableCreateCmd: " + hbaseTableCreateCmd);
  		//System.out.println("\n*** hbaseRowCountCmd2: " + hbaseRowCountCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseTableDisableCmd + "; \n");	
		sb.append(hbaseTableDropCmd + "; \n");	
		sb.append(hbaseTableCreateCmd + "; \n");	
		//sb.append(hbaseRowCountCmd2 + "; \n");	
		//sb.append("rm " + enSvrHBaseTableCreationFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableCreationCmds = sb.toString();
		writeDataToAFile(localHBaseTableCreationScriptFilePathAndName, hbaseTableCreationCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableCreationScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Creation on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Creation on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseTableCreationRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Creation Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Creation Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseTableCreationRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String successfullyCreatedHBaseTableName = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			System.err.println("\n*** line is " + line);
			if (line.equalsIgnoreCase(hbaseTableName)){
				successfullyCreatedHBaseTableName = line;
			}		
		}
		br.close();
		aFileReader.close();
		
		System.out.println("\n*** successfullyCreatedHBaseTableName: " + successfullyCreatedHBaseTableName);
		
		return successfullyCreatedHBaseTableName;
	}//end createHBaseTableUsingSrcSchemaOnTargetCluster
	

	public static String copySrcHBaseTableToDestByMRWithTimeStamp (String srcHBaseTableName,  String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, ULServerCommandFactory bdENCmdFactory,			    
			   String destHBaseTableName, String destZookeeperQuorun4Hbase, String startTimeStamp, String endTimeStamp ) throws IOException{		
				
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableCopyingToDestClusterScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_CopyingToDestCluster_TempScriptFile.sh";			
		//prepareFile (localHBaseTableCopyingToDestClusterScriptFilePathAndName,  "Script File For HBase Table CopyingToDestCluster ....");
		File aFile = new File (localHBaseTableCopyingToDestClusterScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table CopyingToDestCluster ....: \n" + localHBaseTableCopyingToDestClusterScriptFilePathAndName);
		}
		
  		String tempHBaseTableCopyRecFileName1 = "tempHbaseTableTimeStampCopy1.txt";
  		String enSvrHBaseTableCopyingToDestClusterFilePathAndName1 = enServerScriptFileDirectory + tempHBaseTableCopyRecFileName1; 
  		//hbase org.apache.hadoop.hbase.mapreduce.CopyTable --new.name=employee1_bkp --peer.adr=hdpr05mn01.mayo.edu,hdpr05mn02.mayo.edu,hdpr05mn03.mayo.edu:2181:/hbase-secure employee1 2>&1 | tee dcTemp.txt;
  		//cat  dcTemp.txt | grep 'records=' | sed -e 's/\s\+//g' > dcTemp2.txt;  		
  		//---or----
  		//hbase org.apache.hadoop.hbase.mapreduce.CopyTable --starttime=1483228800000 --endtime=1493769600000 --new.name=employee1_bkp --peer.adr=hdpr05mn01.mayo.edu,hdpr05mn02.mayo.edu,hdpr05mn03.mayo.edu:2181:/hbase-secure employee1 2>&1 | tee dcTemp.txt;
  		//cat  dcTemp.txt | grep 'records=' | sed -e 's/\s\+//g' > dcTemp2.txt; 
  		
  		String hbaseTableCopyCmd1L = "hbase org.apache.hadoop.hbase.mapreduce.CopyTable "; 
  		String hbaseTableCopyCmd1M = "";
  		if (!startTimeStamp.isEmpty()) {  			
  			if (hbaseTableCopyCmd1M.isEmpty()){
  				hbaseTableCopyCmd1M += "--starttime=" + startTimeStamp;
  			} else {
  				hbaseTableCopyCmd1M += " --starttime=" + startTimeStamp; 
  			}  			
  		}
  		if (!endTimeStamp.isEmpty()) {
  			if (hbaseTableCopyCmd1M.isEmpty()){
  				hbaseTableCopyCmd1M += "--endtime=" + endTimeStamp;
  			} else {
  				hbaseTableCopyCmd1M += " --endtime=" + endTimeStamp; 
  			}  			
  		}
  		if (hbaseTableCopyCmd1M.isEmpty()){
				hbaseTableCopyCmd1M += "--new.name="+ destHBaseTableName + " --peer.adr=" + destZookeeperQuorun4Hbase + " " + srcHBaseTableName ;
		} else {
			hbaseTableCopyCmd1M += " --new.name="+ destHBaseTableName + " --peer.adr=" + destZookeeperQuorun4Hbase + " " + srcHBaseTableName ;
		} 		
  		
  		
  		String hbaseTableCopyCmd1R = " 2>&1 | tee " + enSvrHBaseTableCopyingToDestClusterFilePathAndName1;
  		
  		
  		String hbaseTableCopyCmd1 = hbaseTableCopyCmd1L + hbaseTableCopyCmd1M + hbaseTableCopyCmd1R;
  		
  		String tempHBaseTableCopyRecFileName2 = "tempHbaseTableTimeStampCopy2.txt";
  		String enSvrHBaseTableCopyingToDestClusterFilePathAndName2 = enServerScriptFileDirectory + tempHBaseTableCopyRecFileName2;   
  		//cat  dcTemp.txt | grep 'records=' | sed -e 's/\s\+//g' > dcTemp2.txt; 
  		String hbaseTableCopyCmd2 = "cat  " + enSvrHBaseTableCopyingToDestClusterFilePathAndName1 + " | grep 'records=' | sed -e 's/^\\s\\+//g'  > " + enSvrHBaseTableCopyingToDestClusterFilePathAndName2;
  		
  		System.out.println("\n*** hbaseTableCopyCmd1: " + hbaseTableCopyCmd1);
  		System.out.println("\n*** hbaseTableCopyCmd2: " + hbaseTableCopyCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseTableCopyCmd1 + "; \n");	
		sb.append(hbaseTableCopyCmd2 + "; \n");	
		sb.append("rm " + enSvrHBaseTableCopyingToDestClusterFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableCopyingToDestClusterCmds = sb.toString();
		writeDataToAFile(localHBaseTableCopyingToDestClusterScriptFilePathAndName, hbaseTableCopyingToDestClusterCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localHBaseTableCopyingToDestClusterScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableCopyingToDestClusterScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table CopyingToDestCluster on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table CopyingToDestCluster on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseTableCopyRecFileName2,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase CopyingToDestCluster Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase CopyingToDestCluster Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableCopyResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseTableCopyRecFileName2;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableCopyResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String srcRecCount = "0";
		String destRecCount = "0";	
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){				
				System.out.println("\n*** line: " + line);
				if (line.contains("=")){
					String[] lineSplit = line.split("=");
					System.err.println("\n*** lineSplit[0]: " + lineSplit[0]);
					if (lineSplit[0].contains("input records")){
						srcRecCount = lineSplit[1];
					}
					if (lineSplit[0].contains("output records")){
						destRecCount = lineSplit[1];
					}					
				}
			}		
		}
		br.close();
		aFileReader.close();
		//System.err.println("*** srcRecCount: " + srcRecCount);
		//System.err.println("*** destRecCount: " + destRecCount);
		
		String srcTableRecCount_To_DestTableRecCount = srcRecCount + "===>" + destRecCount;
		System.out.println("\n*** srcTableRecCount_To_DestTableRecCount: " + srcTableRecCount_To_DestTableRecCount);		
		
		return srcTableRecCount_To_DestTableRecCount;
	}//end copySrcHBaseTableToDestByMRWithTimeStamp
	
	public static long countHBaseTableRowsByMR (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableRowCountingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_RowCounting_TempScriptFile.sh";			
		//prepareFile (localHBaseTableRowCountingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Row Counting ....: \n" + localHBaseTableRowCountingScriptFilePathAndName);
		}
		
  		String tempHBaseRowCountRecFileName1 = "tempHbaseTableCount1.txt";
  		String enSvrHBaseTableRowCountingFilePathAndName1 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName1; 
  		//hbase org.apache.hadoop.hbase.mapreduce.RowCounter $hbaseTableName 2>&1 | tee $hbaseTableCountWorkingFileName; //+- -Dhbase.client.scaner.caching=1000 +- signle quotation mark for hbase table name
  	    //rowCountNum=`cat  $hbaseTableCountWorkingFileName | grep 'ROWS=' | sed -e 's/^\s\+//g' -e 's/ROWS=//g'`; 
  	    //record=$hbaseTableName"===="$rowCountNum;
  	    //echo $record >> $hbaseTableSchemaCountFileName;

  		String hbaseRowCountCmd1 = "hbase -Dhbase.client.scaner.caching=1000 org.apache.hadoop.hbase.mapreduce.RowCounter '" + hbaseTableName + "' 2>&1 | tee " + enSvrHBaseTableRowCountingFilePathAndName1;
  		
  		String tempHBaseRowCountRecFileName2 = "tempHbaseTableCount2.txt";
  		String enSvrHBaseTableRowCountingFilePathAndName2 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName2;   		
  		String hbaseRowCountCmd2 = "cat  " + enSvrHBaseTableRowCountingFilePathAndName1 + " | grep 'ROWS=' | sed -e 's/^\\s\\+//g' -e 's/ROWS=//g' > " + enSvrHBaseTableRowCountingFilePathAndName2;
  		
  		System.out.println("\n*** hbaseRowCountCmd1: " + hbaseRowCountCmd1);
  		System.out.println("\n*** hbaseRowCountCmd2: " + hbaseRowCountCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseRowCountCmd1 + "; \n");	
		sb.append(hbaseRowCountCmd2 + "; \n");	
		sb.append("rm " + enSvrHBaseTableRowCountingFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHBaseTableRowCountingScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableRowCountingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseRowCountRecFileName2,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseRowCountRecFileName2;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHBaseTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseTableRowCount = Long.valueOf(line);
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseTableRowCount: " + currHBaseTableRowCount);
		
		return currHBaseTableRowCount;
	}//end countHBaseTableRowsByMR
	
	public static long countHBaseTableRowsByMRWithTimeStamp (String hbaseTableName, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory, String startTimeStamp, String endTimeStamp ) throws IOException{
		
				
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableRowCountingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_RowCounting_TempScriptFile.sh";			
		//prepareFile (localHBaseTableRowCountingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableRowCountingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Row Counting ....: \n" + localHBaseTableRowCountingScriptFilePathAndName);
		}
		
  		String tempHBaseRowCountRecFileName1 = "tempHbaseTableTimeStampCount1.txt";
  		String enSvrHBaseTableRowCountingFilePathAndName1 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName1; 
  		//hbase org.apache.hadoop.hbase.mapreduce.RowCounter $hbaseTableName 2>&1 | tee $hbaseTableCountWorkingFileName;
  	    //rowCountNum=`cat  $hbaseTableCountWorkingFileName | grep 'ROWS=' | sed -e 's/^\s\+//g' -e 's/ROWS=//g'`; 
  	    //record=$hbaseTableName"===="$rowCountNum;
  	    //echo $record >> $hbaseTableSchemaCountFileName;
  		//---
  		//hbase -Dhbase.client.scaner.caching=1000 org.apache.hadoop.hbase.mapreduce.RowCounter 'EpicStressAppointmentHL7' --starttime=1483228800000 --endtime=1493769600000

  		String hbaseRowCountCmd1L = "hbase -Dhbase.client.scaner.caching=1000 org.apache.hadoop.hbase.mapreduce.RowCounter '" + hbaseTableName + "' "; 
  		String hbaseRowCountCmd1M = "";
  		if (!startTimeStamp.isEmpty()) {  			
  			if (hbaseRowCountCmd1M.isEmpty()){
  				hbaseRowCountCmd1M += "--starttime=" + startTimeStamp;
  			} else {
  				hbaseRowCountCmd1M += " --starttime=" + startTimeStamp; 
  			}  			
  		}
  		if (!endTimeStamp.isEmpty()) {
  			if (hbaseRowCountCmd1M.isEmpty()){
  				hbaseRowCountCmd1M += "--endtime=" + endTimeStamp;
  			} else {
  				hbaseRowCountCmd1M += " --endtime=" + endTimeStamp; 
  			}  			
  		}
  		String hbaseRowCountCmd1R = " 2>&1 | tee " + enSvrHBaseTableRowCountingFilePathAndName1;
  		
  		
  		String hbaseRowCountCmd1 = hbaseRowCountCmd1L + hbaseRowCountCmd1M + hbaseRowCountCmd1R;
  		
  		String tempHBaseRowCountRecFileName2 = "tempHbaseTableTimeStampCount2.txt";
  		String enSvrHBaseTableRowCountingFilePathAndName2 = enServerScriptFileDirectory + tempHBaseRowCountRecFileName2;   		
  		String hbaseRowCountCmd2 = "cat  " + enSvrHBaseTableRowCountingFilePathAndName1 + " | grep 'ROWS=' | sed -e 's/^\\s\\+//g' -e 's/ROWS=//g' > " + enSvrHBaseTableRowCountingFilePathAndName2;
  		
  		System.out.println("\n*** hbaseRowCountCmd1: " + hbaseRowCountCmd1);
  		System.out.println("\n*** hbaseRowCountCmd2: " + hbaseRowCountCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseRowCountCmd1 + "; \n");	
		sb.append(hbaseRowCountCmd2 + "; \n");	
		sb.append("rm " + enSvrHBaseTableRowCountingFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableRowCountingCmds = sb.toString();
		writeDataToAFile(localHBaseTableRowCountingScriptFilePathAndName, hbaseTableRowCountingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableRowCountingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Row-Counting on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseRowCountRecFileName2,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Row Counting Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseRowCountRecFileName2;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		long currHBaseTableRowCount = 0;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseTableRowCount = Long.valueOf(line);
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseTableRowCount: " + currHBaseTableRowCount);
		
		return currHBaseTableRowCount;
	}//end countHBaseTableRowsByMRWithTimeStamp
	
	public static String obtainHbaseTableFirstRowAllVersionsData (String hbaseTableName, int maxVersionNumber, String hbaseInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			   ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHBaseTableTargetRowRetrievingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HBaseTable_TargetRowRetrieving_TempScriptFile.sh";			
		//prepareFile (localHBaseTableTargetRowRetrievingScriptFilePathAndName,  "Script File For HBase Table Row Counting ....");
		File aFile = new File (localHBaseTableTargetRowRetrievingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HBase Table Row Counting ....: \n" + localHBaseTableTargetRowRetrievingScriptFilePathAndName);
		}
		
  		String tempHBaseTargetRowVersionDataRecFileName1 = "tempHbaseTableTargetRowVersionData1.txt";
  		String enSvrHBaseTableRowTargetRowVersionDataFilePathAndName1 = enServerScriptFileDirectory + tempHBaseTargetRowVersionDataRecFileName1; 
  		//echo "scan 'registry:registry-resources',{VERSIONS => 255, LIMIT => 1}" | hbase shell | grep -v "HBase Shell;\|Type\|Version\|ROW\|row(s)" | sed -e '/^\s*$/d' >  dcTemp.txt;
  		//hbaseTableTargetRowAllVersionsData=`cat dcTemp.txt`;
  		// echo $hbaseTableTargetRowAllVersionsData > dctemp2.txt;

  		String hbaseRowTargetRowVersionDataCmd1 = "echo \"scan '" + hbaseTableName + "',{VERSIONS => " + maxVersionNumber + ", LIMIT => 1}\" | hbase shell | grep -v \"HBase Shell;\\|Type\\|Version\\|ROW\\|row(s)\" | sed -e '/^\\s*$/d'  > " + enSvrHBaseTableRowTargetRowVersionDataFilePathAndName1;
  		
  		String hbaseRowTargetRowVersionDataCmd1a = "hbaseTableTargetRowAllVersionsData=`cat " + enSvrHBaseTableRowTargetRowVersionDataFilePathAndName1 + "`";
  				
  		String tempHBaseTargetRowVersionDataRecFileName2 = "tempHbaseTableTargetRowVersionData2.txt";
  		String enSvrHBaseTableRowTargetRowVersionDataFilePathAndName2 = enServerScriptFileDirectory + tempHBaseTargetRowVersionDataRecFileName2;   		
  		String hbaseRowTargetRowVersionDataCmd2 = "echo $hbaseTableTargetRowAllVersionsData > " + enSvrHBaseTableRowTargetRowVersionDataFilePathAndName2;
  		
  		System.out.println("\n*** hbaseRowTargetRowVersionDataCmd1: " + hbaseRowTargetRowVersionDataCmd1);
  		System.out.println("\n*** hbaseRowTargetRowVersionDataCmd1a: " + hbaseRowTargetRowVersionDataCmd1a);
  		System.out.println("\n*** hbaseRowTargetRowVersionDataCmd2: " + hbaseRowTargetRowVersionDataCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hbaseInternalAuthCmd + "; \n");		
		
		sb.append(hbaseRowTargetRowVersionDataCmd1 + "; \n");	
		sb.append(hbaseRowTargetRowVersionDataCmd1a + "; \n");	
		sb.append(hbaseRowTargetRowVersionDataCmd2 + "; \n");	
		sb.append("rm " + enSvrHBaseTableRowTargetRowVersionDataFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hbaseTableTargetRowRetrievingCmds = sb.toString();
		writeDataToAFile(localHBaseTableTargetRowRetrievingScriptFilePathAndName, hbaseTableTargetRowRetrievingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHBaseTableTargetRowRetrievingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HBase Table Getting Target Row Version Data on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HBase Table Getting Target Row Version Data on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHBaseTargetRowVersionDataRecFileName2,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HBase Table Getting Target Row Version Data Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HBase Table Getting Target Row Version DataResults File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHBaseTableRowCountResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHBaseTargetRowVersionDataRecFileName2;
		
		FileReader aFileReader = new FileReader(winLocalHBaseTableRowCountResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String currHBaseTableTargetRowAllAvailableVersionData = "";
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				currHBaseTableTargetRowAllAvailableVersionData = line;
			}		
		}
		br.close();
		aFileReader.close();
		System.out.println("\n*** currHBaseTableTargetRowAllAvailableVersionData: " + currHBaseTableTargetRowAllAvailableVersionData);
		
		return currHBaseTableTargetRowAllAvailableVersionData;
	}//end obtainHbaseTableFirstRowAllVersionsData

////--------------------------------------------------------
	public static ArrayList<String> obtainHBaseTableList (String ambariQaInternalAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHbaseTableTableListingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HbaseTable_TableListing_ScriptFile.sh";			
		//prepareFile (localHbaseTableTableListingScriptFilePathAndName,  "Script File For Hbase Table Listing ....");
		File aFile = new File (localHbaseTableTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Hbase Table Listing ....: \n" + localHbaseTableTableListingScriptFilePathAndName);
		}
				
  		String tempHbaseTableListRecFileName = "tempHbaseTableList.txt";
  		String srcEnSvrHbaseTableTableListingFilePathAndName = enServerScriptFileDirectory + tempHbaseTableListRecFileName; 
  		//echo "list" | hbase shell | tail -1 | tr '"' ' '  > /data/home/m041785/test/dcHBaseTableList.txt;

  		String hbaseShellInitiateStr = " | hbase shell";
  		String hbaseTableListCmd =  "echo \"list\""  + hbaseShellInitiateStr + " | tail -1 | tr '\"' ' '  > " + srcEnSvrHbaseTableTableListingFilePathAndName;
  		System.out.println("\n*** hbaseTableListCmd: " + hbaseTableListCmd);
  		
  		StringBuilder sb = new StringBuilder();			
		
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(ambariQaInternalAuthCmd + "; \n");		
		
		//sb.append("set hbase.execution.engine=tez;\n");		
		sb.append(hbaseTableListCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		String hbaseTableTableListingCmds = sb.toString();
		writeDataToAFile(localHbaseTableTableListingScriptFilePathAndName, hbaseTableTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHbaseTableTableListingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hbase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hbase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHbaseTableListRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hbase Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hbase Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHbaseTableTableListResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHbaseTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHbaseTableTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		ArrayList<String> hbaseTableList = new  ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				line = line.replace("[", "").replace("]", "").trim();
				if (line.contains(",")){
					String[] lineSplit = line.split(",");
					for (int i = 0; i < lineSplit.length; i ++ ){
						hbaseTableList.add(lineSplit[i].trim());
					}
				}
				
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHbaseTableTableList: " + currHbaseTableTableList);
		
		return hbaseTableList;
	}//end obtainHBaseTableList
	
	public static String obtainHBaseTableCreationCmd (String hbaseTableName, String hbaseInteralAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		
		String hbaseTableColfVernsInfo =  obtainHBaseTableColFVerns (hbaseTableName, hbaseInteralAuthCmd, enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
		
		//create 'registry-lock1', {NAME => 'id', VERSIONS => '10' },  {NAME => 'lock', VERSIONS => '10'}
		String hbaseTableCreationCmd = "create '" + hbaseTableName + "', " + hbaseTableColfVernsInfo;
		
		return hbaseTableCreationCmd;				
	}//end obtainHBaseTableCreationCmd

	public static String obtainHBaseTableColFVerns (String hbaseTableName, String hbaseInteralAuthCmd, 
			String enServerScriptFileDirectory, String winLocalHbaseRecordInfoFolder, 
			ULServerCommandFactory bdENCmdFactory ) throws IOException{
		//1. 
		winLocalHbaseRecordInfoFolder = winLocalHbaseRecordInfoFolder.replace("/", "\\");
		if (!winLocalHbaseRecordInfoFolder.endsWith("\\")){
			winLocalHbaseRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHbaseRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHbaseTableTableListingScriptFilePathAndName = winLocalHbaseRecordInfoFolder + "dcMCHadoop_HbaseTable_TableDDL_ScriptFile.sh";			
		//prepareFile (localHbaseTableTableListingScriptFilePathAndName,  "Script File For Hbase Table Listing ....");
		File aFile = new File (localHbaseTableTableListingScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Hbase Table Listing ....: \n" + localHbaseTableTableListingScriptFilePathAndName);
		}
				
  		String tempHbaseTableListRecFileName = "tempHbaseTableDDL.txt";
  		String srcEnSvrHbaseTableTableListingFilePathAndName = enServerScriptFileDirectory + tempHbaseTableListRecFileName; 
  		//echo "list" | hbase shell | tail -1 | tr '"' ' '  > /data/home/m041785/test/dcHBaseTableList.txt;

  		String hbaseShellInitiateStr = " | hbase shell";
  		String hbaseTableListCmd =  "echo \"describe '" + hbaseTableName + "'\""  + hbaseShellInitiateStr + " > " + srcEnSvrHbaseTableTableListingFilePathAndName;
  		System.out.println("\n*** hbaseTableListCmd: " + hbaseTableListCmd);
  		
  		StringBuilder sb = new StringBuilder();			
		
		sb.append("cd " + enServerScriptFileDirectory + ";\n");
		sb.append("kdestroy;\n");
		sb.append(hbaseInteralAuthCmd + "; \n");		
		
		//sb.append("set hbase.execution.engine=tez;\n");		
		sb.append(hbaseTableListCmd + "; \n");		
		//sb.append("kdestroy;\n");
		
		
		String hbaseTableTableListingCmds = sb.toString();
		writeDataToAFile(localHbaseTableTableListingScriptFilePathAndName, hbaseTableTableListingCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localPigRelationTestingScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHbaseTableTableListingScriptFilePathAndName, 
				winLocalHbaseRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running Hbase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running Hbase Table Listing on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHbaseTableListRecFileName,  
				enServerScriptFileDirectory, winLocalHbaseRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying Hbase Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying Hbase Listing Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHbaseRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHbaseTableTableListResultsFileFullPathAndName = winLocalHbaseRecordInfoFolder + tempHbaseTableListRecFileName;
		
		FileReader aFileReader = new FileReader(winLocalHbaseTableTableListResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String hbaseTableColFVernsInfo = "";
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				if (line.contains(hbaseTableName) && line.contains("NAME =>")){
					hbaseTableColFVernsInfo = line.replace("} true", "}");
				}
				//line = line.replace("{", "").replace("}", "").trim();
				//if (line.contains(",")){
				//	String[] lineSplit = line.split(",");
				//	for (int i = 0; i < lineSplit.length; i ++ ){
				//		String temp = lineSplit[i].trim();
				//		if(temp.startsWith("NAME =>")){
				//			sb.append("{"  + temp + ", ");
				//		}
				//		if(temp.startsWith("VERSIONS =>")){
				//			sb.append(temp + "}");
				//		}						
				//	}
				//}
				
			}		
		}
		br.close();
		aFileReader.close();
		//System.out.println("\n*** currHbaseTableTableList: " + currHbaseTableTableList);
		
		//hbaseTableColFVernsInfo = sb.toString();
		//sb.setLength(0);		
		//hbaseTableColFVernsInfo = hbaseTableColFVernsInfo.replace("}{", "},  {");	
		
		return hbaseTableColFVernsInfo;
	}//end obtainHBaseTableColFVerns

////------------------------------

	public static Map<String,String> obtainSrcToDestHBaseNamespacesMap (ArrayList<String> srcHBaseNamespaceList, 
																		 ArrayList<String> destHBaseNamespaceList){
		Map<String,String> currSrcToDestHBaseNamespacesMap = new LinkedHashMap<String,String>();
		for (String tempSrcNamespace : srcHBaseNamespaceList){			
			String tempDestNamespace = findTargetItemrFromAList (destHBaseNamespaceList, tempSrcNamespace );
			currSrcToDestHBaseNamespacesMap.put(tempSrcNamespace, tempDestNamespace);
		}
		return currSrcToDestHBaseNamespacesMap;
	}//end obtainSrcToDestHBaseNamespacesMap
	

	public static Map<String,String> obtainSrcToDestHBaseNamespaceTablesMap (ArrayList<String> srcHBaseNamespaceTableList, 
																		 ArrayList<String> destHBaseNamespaceTableList){
		Map<String,String> currSrcToDestHBaseNamespaceTablesMap = new LinkedHashMap<String,String>();
		for (String tempSrcNamespaceTable : srcHBaseNamespaceTableList){			
			String tempDestNamespaceTable = findTargetItemrFromAList (destHBaseNamespaceTableList, tempSrcNamespaceTable );
			currSrcToDestHBaseNamespaceTablesMap.put(tempSrcNamespaceTable, tempDestNamespaceTable);
		}
		return currSrcToDestHBaseNamespaceTablesMap;
	}//end obtainSrcToDestHBaseNamespaceTablesMap
	


	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//III. Run a Script File on a Unix/Linux (U/L) Server (Entry Node) by Login User Rather than Root User
//(Login User has very limited power than Root User)	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	///////////////////////////////
//===========================================
//III.1.  Run Script File By Login User	
//===========================================
	public static int runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster (String winLocalScriptFileFullPathAndName, 
					String winLocalScriptFilesFolder, String enServerScriptFileDirectory, ULServerCommandFactory bdENCmdFactory){
		//1. Preparation
		if (!winLocalScriptFilesFolder.endsWith("\\")){
			winLocalScriptFilesFolder += "\\";
		}
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
		        
        System.out.println("--*--winLocalScriptFileFullPathAndName: \n" + winLocalScriptFileFullPathAndName + "\n");
        String scriptFileName = winLocalScriptFileFullPathAndName.replace(winLocalScriptFilesFolder, ""); 
        //Example: enServerScriptFileDirectory = "/data/home/m041785/test/"; 
         
        String enServerScriptFileFullPathAndName = enServerScriptFileDirectory + scriptFileName;
        System.out.println("--*--enServerScriptFileFullPathAndName: \n" + enServerScriptFileFullPathAndName + "\n");
        
        //2. 6 steps to run a script .sh file      
        //2.(1) Copy Script .sh file from windows local to BD Cluster Entry Node server local - '/home/hdfs/' 
        System.out.println("**Step 1:**");            
        int exitVal = copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster (scriptFileName, 
        			winLocalScriptFilesFolder, enServerScriptFileDirectory, bdENCmdFactory);
               
        //2.(2) Step #2: Activate script .sh file inside the folder - '/data/home/m041785/test/' on EntryNodeLocal of a BD Cluster
        System.out.println("**Step 2:**");  
        if (exitVal !=0 ){
			return exitVal;
		} else {
			exitVal =  activateAnyFile4ExecutionByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerScriptFileFullPathAndName, 
					bdENCmdFactory);
		}
              
        //2.(3) Step #3: Step 3 - Change ownership of script .sh file from root into hdfs inside the folder - '/data/home/m041785/test/' on EntryNodeLocal of a BD Cluster
        System.out.println("**Step 3:**"); 
        String loginUserName = bdENCmdFactory.getUsername();
        if (exitVal !=0 ){
			return exitVal;
		} else {
			String tgtNewOwnershiStr = loginUserName + ":users";
			exitVal =  changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerScriptFileFullPathAndName, tgtNewOwnershiStr,
						bdENCmdFactory);
		}
        
        //2.(4) Step #4: Step 4 - Convert script file formt to Unix format inside BD Cluster Entry Node folder - '/data/home/m041785/test/' 
        System.out.println("**Step 4:**");
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 exitVal =  convertFileFromDosToUnixFormatByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerScriptFileFullPathAndName, 
						bdENCmdFactory);
		}
       
                
        //2.(5) Step #5: Step 5 - Run script file inside BD Cluster Entry Node folder - '/data/home/m041785/test/' 
        System.out.println("**Step 5:**");
        if (exitVal !=0 ){
			return exitVal;
		} else {			
			System.out.println(" ---pwd: " + enServerScriptFileDirectory);
			//a. Alternative Method #1:
			//exitVal = runScriptFileWitinLoginUserHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerScriptFileFullPathAndName, bdENCmdFactory);
			
			//b. Alternative Method #2:
			System.out.println(" ---scriptFileName: " + scriptFileName);
			String bdClusterName = bdENCmdFactory.getBdClusterName();
			System.out.println(" ---bdClusterName = bdENCmdFactory.getBdClusterName(): " + bdClusterName);
			LoginUser tempLoginUser = new LoginUser (bdClusterName);			
			try {
				String tempHdfsScriptFile_RunScriptPathAndName = tempLoginUser.createRunServerScriptFileForPutty(scriptFileName, enServerScriptFileDirectory); //enServerScriptFilePathAndName..scriptFileName
				String puttyScriptFileRunFullCmd = bdENCmdFactory.getPuttyScriptCommandString(tempHdfsScriptFile_RunScriptPathAndName);
	    		
	    		exitVal = executePuttyCommandOnLocalMachine(puttyScriptFileRunFullCmd);    	
		    	String exeResultsMsg = "";
		    	if (exitVal != 0){
		    		exeResultsMsg = "--xxx-- Failed In Running Script File Step 5 - " + puttyScriptFileRunFullCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
				} else {
					exeResultsMsg = "--***-- Suceessfully Executed Script File Step 5 - " + puttyScriptFileRunFullCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
				}	    	
		    	System.out.println(" **5** exeResultsMsg: " + exeResultsMsg);
			} catch (IOException e) {				
				e.printStackTrace();
			}//end try			
		}
        
        //2.(6) Step #6: Step 6 - Remove script file inside BD Cluster Entry Node folder - '/data/home/m041785/test/'
        System.out.println("**Step 6:**");
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 exitVal = removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerScriptFileFullPathAndName, 
			 			bdENCmdFactory);				
		}      
        return exitVal;
	}//end runScriptFile_OnBDCluster	
	
	
	
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//II. File Copying/Moving between Windows/Mac Local and a Unix/Linux (U/L) Server (Entry Node) by Login User or Root User 
//(Login User has very limited power than Root User but Root User cannot directly cp/mv files between the Local and Server)	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	///////////////////////////////
//===========================================
//II.1.  File Copying/Moving By Login User	
//===========================================	
	//II.1.1 Copying file from Windows/Mac Local to the Login User Home Folder or Its Sub-Folder on U/L Server
	public static int copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster (String winLocalSrcFileName,	String winLocalSrcFolderName,		
			String enServerLoginUserHomeFolderName, ULServerCommandFactory bdENCmdFactory){	
		if (!winLocalSrcFolderName.endsWith("/")){
			winLocalSrcFolderName += "/";
		}
		if (!enServerLoginUserHomeFolderName.endsWith("/")){
			enServerLoginUserHomeFolderName += "/";
		}
		
		String winLocalSrcFileFullPathAndName = winLocalSrcFolderName + winLocalSrcFileName;
		String enServerFileInLoginUserHomeFolder_FullPathAndName = enServerLoginUserHomeFolderName + winLocalSrcFileName;
		
		File tempF = new File (winLocalSrcFileFullPathAndName);	
				
		int exitVal = 100;
		String exeResultsMsg = "";
		if (tempF.exists()){
			String tempEnFileCopyFromWinLocalFullPscpCmd = bdENCmdFactory.getPscpFileCopyToServerCommandString(winLocalSrcFileFullPathAndName, enServerFileInLoginUserHomeFolder_FullPathAndName);
			
			exitVal = executePuttyCommandOnLocalMachine(tempEnFileCopyFromWinLocalFullPscpCmd);			
			if (exitVal == 0){
				exeResultsMsg = "--*-- Suceessful - Copying from Windows Local to enServer file - '" + enServerFileInLoginUserHomeFolder_FullPathAndName + "' from " + bdENCmdFactory.getServerURI();
				
			} else {
				exeResultsMsg = "--*-- Failed - Copying to Windows Local fto enServer file - '" + enServerFileInLoginUserHomeFolder_FullPathAndName + "' from " + bdENCmdFactory.getServerURI();
			}
		}
		
		System.out.println(" **1 F-Copying - Step1 (LoginUser)** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;	
	}//end copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster
	
	//II.1.2 Copying file from the Login User Home Folder or Its Sub-Folder on U/L Server to Windows/Mac Local 
	public static int copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (String enServerSrcFileNme,  
			String enServerLoginUserHomeFolderName, String winLocalTgtFolderName, ULServerCommandFactory bdENCmdFactory){	
		if (!enServerLoginUserHomeFolderName.endsWith("/")){
			enServerLoginUserHomeFolderName += "/";
		}
		if (!winLocalTgtFolderName.endsWith("/")){
			winLocalTgtFolderName += "/";
		}
		String enServerFileInLoginUserHomeFolder_FullPathAndName = enServerLoginUserHomeFolderName + enServerSrcFileNme;				
		String winLocalTgtFileFullPathAndName = winLocalTgtFolderName + enServerSrcFileNme;
		prepareFolder_keepOld (winLocalTgtFolderName, "Local Target Folder" );
		prepareFile_deleteOld (winLocalTgtFileFullPathAndName,  "Local Target File");
		
		String tempEnFileCopyToWinLocalFullPscpCmd = bdENCmdFactory.getPscpFileCopyToLocalCommandString(enServerFileInLoginUserHomeFolder_FullPathAndName, winLocalTgtFileFullPathAndName);
		System.out.println("\n*** tempEnFileCopyToWinLocalFullPscpCmd: " + tempEnFileCopyToWinLocalFullPscpCmd);
		
		int exitVal = executePuttyCommandOnLocalMachine(tempEnFileCopyToWinLocalFullPscpCmd);
		String exeResultsMsg = "";
		if (exitVal == 0){
			exeResultsMsg = "--*-- Suceessful - Copying to Windows Local from enServer file - '" + enServerFileInLoginUserHomeFolder_FullPathAndName + "' from " + bdENCmdFactory.getServerURI();
			
		} else {
			exeResultsMsg = "--*-- Failed - Copying to Windows Local from enServer file - '" + enServerFileInLoginUserHomeFolder_FullPathAndName + "' from " + bdENCmdFactory.getServerURI();
		}
		System.out.println(" **2 F-Copying - Step2 (LoginUser)** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;	
	}//end copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster

	public static int copyFolder_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (String serverFolderPathAndName,  
			String winLocalTgtFolderName, ULServerCommandFactory bdENCmdFactory){	
		if (!serverFolderPathAndName.endsWith("/")){
			serverFolderPathAndName += "/";
		}
		
		prepareFolder_keepOld (winLocalTgtFolderName, "Local Target Folder" );		
		
		String tempEnFolderCopyToWinLocalFullPscpCmd = bdENCmdFactory.getPscpFolderCopyToLocalCommandString(serverFolderPathAndName, winLocalTgtFolderName);
		System.out.println(" *** tempEnFolderCopyToWinLocalFullPscpCmd: \n	---" + tempEnFolderCopyToWinLocalFullPscpCmd);
		
		int exitVal = executePuttyCommandOnLocalMachine(tempEnFolderCopyToWinLocalFullPscpCmd);
		String exeResultsMsg = "";
		if (exitVal == 0){
			exeResultsMsg = "--*-- Suceessful - Copying to Windows Local from enServer folder - '" + serverFolderPathAndName + "' from " + bdENCmdFactory.getServerURI();
			
		} else {
			exeResultsMsg = "--*-- Copying to Windows Local from enServer folder - '" + serverFolderPathAndName + "' from " + bdENCmdFactory.getServerURI();
		}
		System.out.println(" *** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;	
	}//end copyFolder_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster
	
	public static int copyFolder_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster (String winLocalSrcFolderName,
			String serverTgtFolderPathAndName, ULServerCommandFactory bdENCmdFactory){	
		if (!serverTgtFolderPathAndName.endsWith("/")){
			serverTgtFolderPathAndName += "/";
		}
		
		//prepareFolder_keepOld (serverTgtFolderPathAndName, "Local Target Folder" );
		//LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster...
		safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(serverTgtFolderPathAndName, bdENCmdFactory);
		System.out.print("*** On the server, created serverTgtFolderPathAndName: " + serverTgtFolderPathAndName);
		
		String tempEnFolderCopyFromWinLocalFullPscpCmd = bdENCmdFactory.getPscpFolderCopyToServerCommandString(winLocalSrcFolderName, serverTgtFolderPathAndName);
		System.out.println(" *** tempEnFolderCopyFromWinLocalFullPscpCmd: \n	---" + tempEnFolderCopyFromWinLocalFullPscpCmd);
		
		int exitVal = executePuttyCommandOnLocalMachine(tempEnFolderCopyFromWinLocalFullPscpCmd);
		String exeResultsMsg = "";
		if (exitVal == 0){
			exeResultsMsg = "--*-- Suceessful - Copying from Windows Local to enServer folder - '" + serverTgtFolderPathAndName + "' from " + bdENCmdFactory.getServerURI();
			
		} else {
			exeResultsMsg = "--*-- Failed - Copying from Windows Local to enServer folder - '" + serverTgtFolderPathAndName + "' from " + bdENCmdFactory.getServerURI();
		}
		System.out.println(" *** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;	
	}//end copyFolder_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster
	
//	//The following 2 methods have not been tested yet
//	public static int copyFile_ToWindowsLocal_FromEntryNodeNonLoginUserHomeFolder_OnBDCluster (String enServerSrcFileNme, String enServerSrcNonLoginUserHomeFolderName, 
//			String enServerLoginUserHomeFolderName, String winLocalTgtFolderName, 
//			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory){
//		
//		if (!enServerSrcNonLoginUserHomeFolderName.endsWith("/")){
//			enServerSrcNonLoginUserHomeFolderName += "/";
//		}
//		if (!enServerLoginUserHomeFolderName.endsWith("/")){
//			enServerLoginUserHomeFolderName += "/";
//		}
//		if (!winLocalTgtFolderName.endsWith("/")){
//			winLocalTgtFolderName += "/";
//		}
//		String enServerSrcFileInNonLoginUserHomeFolder_FullPathAndName = enServerSrcNonLoginUserHomeFolderName + enServerSrcFileNme;
//		String enServerFileInLoginUserHomeFolder_FullPathAndName = enServerLoginUserHomeFolderName + enServerSrcFileNme;				
//		//String winLocalTgtFileFullPathAndName = winLocalTgtFolderName + enServerSrcFileNme;
//		
//		//a. Copy enServerFileInNonLoginUserHomeFolder_FullPathAndName into enServerFileInLoginUserHomeFolder_FullPathAndName by root user
//		String rootPw = bdENRootCmdFactory.getRootPassword();
//		
//		String rootUserCopyCmd = "scp " + enServerSrcFileInNonLoginUserHomeFolder_FullPathAndName + " " + enServerFileInLoginUserHomeFolder_FullPathAndName;
//		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserCopyCmd, bdENCmdFactory, rootPw);    	
//    	String exeResultsMsg = "";
//    	if (exitVal != 0){
//    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Copying File To Test User Home Folder on Entry Node Local - " + rootUserCopyCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//		} else {
//			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Copying File To Test User Home Folder on Entry Node Local - " + rootUserCopyCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//		}	    	
//    	System.out.println(" **2 F-Copying - Step1 (RootUser)** exeResultsMsg: " + exeResultsMsg);
//		
//		//b.Copy enServerFileInLoginUserHomeFolder_FullPathAndName to winLocalTgtFileFullPathAndName by test user
//    	int exitVal2 = 2000;
//    	
//    	if (exitVal == 0) {
//    		exitVal2 = copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (enServerSrcFileNme,  
//        			enServerLoginUserHomeFolderName, winLocalTgtFolderName, bdENCmdFactory);
//    	}
//    	
//    	//c. remove the intermediate file in Test User Home Folder
//    	int exitVal3 = 3000;
//    	if (exitVal2 == 0){
//    		exitVal3 = removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerFileInLoginUserHomeFolder_FullPathAndName, bdENCmdFactory);
//    	}
//    			   	
//    	return exitVal3;	
//	}//end copyFile_ToWindowsLocal_FromEntryNodeNonLoginUserHomeFolder_OnBDCluster
//	
//	///////////////////////////////
//	public static int copyFile_FromWindowsLocal_ToEntryNodeNonLoginUserFolder_OnBDCluster (String winLocalSrcFileName,	String winLocalSrcFolderName,		
//			String enServerLoginUserHomeFolderName, String enServerTgtNonLoginUserHomeFolderName,  
//			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory){
//		if (!winLocalSrcFolderName.endsWith("/")){
//			winLocalSrcFolderName += "/";
//		}
//		if (!enServerLoginUserHomeFolderName.endsWith("/")){
//			enServerLoginUserHomeFolderName += "/";
//		}
//		if (!enServerTgtNonLoginUserHomeFolderName.endsWith("/")){
//			enServerTgtNonLoginUserHomeFolderName += "/";
//		}
//		//String winLocalSrcFileFullPathAndName = winLocalSrcFolderName + winLocalSrcFileName;
//		String enServerFileInLoginUserHomeFolder_FullPathAndName = enServerLoginUserHomeFolderName + winLocalSrcFileName;
//		String enServerFileInTgtNonLoginUserHomeFolder_FullPathAndName = enServerTgtNonLoginUserHomeFolderName + winLocalSrcFileName;		
//		
//		//a.Copy winLocalSrcFileFullPathAndName to dcModelClasses.LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeNonLoginUserFolder_OnBDCluster(String, String, String, ULServerCommandFactory, ULServerCommandFactory).enServerFileInLoginUserHomeFolder_FullPathAndName by test user
//    	int exitVal = 100;
//    	exitVal = copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster (winLocalSrcFileName, winLocalSrcFolderName,
//    			enServerLoginUserHomeFolderName,  bdENCmdFactory);
//    			
//		//b. Copy enServerFileInLoginUserHomeFolder_FullPathAndName into enServerFileInTgtNonLoginUserHomeFolder_FullPathAndName by root user
//		String rootPw = bdENRootCmdFactory.getRootPassword();
//		
//		String rootUserCopyCmd = "scp " + enServerFileInLoginUserHomeFolder_FullPathAndName + " " + enServerFileInTgtNonLoginUserHomeFolder_FullPathAndName;
//		int exitVal2 = 200;
//		String exeResultsMsg = "";
//		if (exitVal == 0){
//			exitVal2 = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserCopyCmd, bdENCmdFactory, rootPw);
//			
//			if (exitVal2 != 0){
//	    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Copying File To Test User Home Folder on Entry Node Local - " + rootUserCopyCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//			} else {
//				exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Copying File To Test User Home Folder on Entry Node Local - " + rootUserCopyCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//			}	    	
//	    	System.out.println(" **1 F-Copying - Step2 (RootUser)** exeResultsMsg: " + exeResultsMsg);
//    	}   	
//    	
//    	//c. remove the intermediate file in Test User Home Folder
//    	int exitVal3 = 300;    	
//    	if (exitVal2 == 0){
//    		exitVal3 = removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (enServerFileInLoginUserHomeFolder_FullPathAndName,  bdENCmdFactory);
//    	}
//    			   	
//    	return exitVal3;	
//	}//end copyFile_ToWindowsLocal_FromEntryNodeLocal_OnBDCluster



	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   I. Perform Actions or Operations on a Unix/Linux (U/L) Server (Entry Node) by Login User or Root User 
//      (Login User has very limited power than Root User)	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	///////////////////////////////
//===========================================
//   I.1. U/L Server Operations By Login User	
//===========================================
	public static int performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerLoginUserFileFolderSingleOrMultipeOpsCmd, 
			ULServerCommandFactory bdENCmdFactory){
			
		//Example: mkdir -p /data/home/m041785/temp && chown -R m041785:users /data/home/m041785/temp && chmod -R 777 /data/home/m041785/temp
		// Here enServerLoginUserFileFolderSingleOrMultipeOpsCmd: mkdir -p /home/hdfs/temp && chown -R m041785:users /home/hdfs/temp && chmod -R 777 /home/hdfs/temp
		
		String plinkLoginUserFileOrFolderOpsCmd = bdENCmdFactory.getPlinkSingleCommandString(enServerLoginUserFileFolderSingleOrMultipeOpsCmd, "");	
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkLoginUserFileOrFolderOpsCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Test User Operation(s) on Entry Node Local - " + plinkLoginUserFileOrFolderOpsCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Test User Operation(s)  on Entry Node Local - " + plinkLoginUserFileOrFolderOpsCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **I.1-Test User** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	// The following methods are frequently used Login User Operations on a U/L server
	//I.1.1 Change permission for any file/folder inside the Login User's Home folder on a U/l server
	public static int changeFileOrFolderPermissionInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, String newRecursiveOrNoRecursivePermissionModeNumStr, 
			ULServerCommandFactory bdENCmdFactory){				
		//Example: chmod -R 777 /data/home/m041785/hdfs
		//Here loginUserChangeFileOrFolderPermissionModeCmd: chmod -R 777 /data/home/m041785/hdfs
		//Here: newRecursiveOrNoRecursivePermissionModeNumStr: -R 777
		String loginUserChangeFileOrFolderPermissionModeCmd = "chmod " + newRecursiveOrNoRecursivePermissionModeNumStr + " " +  enServerFileFolderPathAndName;
		System.out.println(" **LoginUser Op: " + loginUserChangeFileOrFolderPermissionModeCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserChangeFileOrFolderPermissionModeCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end changeFileOrFolderPermissionInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	//I.1.2 Change ownership for any file/folder inside the Login User's Home folder on a U/l server
	public static int changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, String newRecursiveOrNoRecursiveOwnerGroupStr, 
			ULServerCommandFactory bdENCmdFactory){		
		//Example: chown -R m041785:users /data/home/m041785/hdfs
		//Here loginUserChangeFileOrFolderOwnershipCmd: chown -R m041785:users /data/home/m041785/hdfs
		//Here newRecursiveOrNoRecursiveOwnerGroupStr: -R m041785:users
		String loginUserChangeFileOrFolderOwnershipCmd = "chown " + newRecursiveOrNoRecursiveOwnerGroupStr + " " +  enServerFileFolderPathAndName;
		System.out.println(" **RootUser Op: " + loginUserChangeFileOrFolderOwnershipCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserChangeFileOrFolderOwnershipCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end changeFileOrFolderOwnershipInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	//I.1.3 Create (safely) any folder inside the Login User's Home folder on a U/l server
	public static int safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, 
			ULServerCommandFactory bdENCmdFactory){		
		//Example: mkdir -p /data/home/m041785/hdfs
		//Here loginUserSafelyCreateAFolderCmd: mkdir -p /data/home/m041785/hdfs
		String loginUserSafelyCreateAFolderCmd = "mkdir -p " + enServerFileFolderPathAndName;
		System.out.println(" **LoginUser Op: " + loginUserSafelyCreateAFolderCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserSafelyCreateAFolderCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster
		
	//I.1.4 Remove a file or folder in the Login User Home folder on a U/L server
	public static int removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerFileOrFolderWitinLoginUserHomeFolderName, 
			 ULServerCommandFactory bdENCmdFactory){		
		//Example: rm -f /home/hdfs/temp/dcTestdata.txt
		//Here testUserFileRemovingCmd: rm -f /home/hdfs/temp/dcTestdata.txt
		String testUserFileRemovingCmd = "rm -r -f " + enServerFileOrFolderWitinLoginUserHomeFolderName;
		System.out.println(" **LoginUser Op: " + testUserFileRemovingCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(testUserFileRemovingCmd,bdENCmdFactory);
		
		//String plinkRemoveFileOperationFullPscpCmd = bdENCmdFactory.getPlinkSingleCommandString(testUserFileRemovingCmd, "");	
		//int exitVal = executePuttyCommandOnLocalMachine(plinkRemoveFileOperationFullPscpCmd);    	
		//String exeResultsMsg = "";
		//if (exitVal != 0){
		//	exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Removing File in Test User Home Folder From Entry Node Local - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//} else {
		//	exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Removing File in Test User Home Folder From Entry Node Local - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//}	    	
		//System.out.println(" **3, 4 or 5** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end removeFileOrFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	//I.1.5 Activate file (script file) inside the Login User Home folder or sub-folder for execution on a U/L server
	public static int activateAnyFile4ExecutionByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerScriptFileWithinLoginUserHomeFolderName, ULServerCommandFactory bdENCmdFactory){
		//if (!enServerFolderWithinLoginUserHomeFolderName.endsWith("/")){
		//	enServerFolderWithinLoginUserHomeFolderName += "/";
		//}
		//String enServerScriptFileFullPathAndName = enServerFolderWithinLoginUserHomeFolderName + enServerScriptFileName;
		
		//Example: chmod +x  /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh
		//Here loginUserFileExecutionEnablingCmd: chmod +x  /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh		
		String loginUserFileExecutionEnablingCmd = "chmod +x " + enServerScriptFileWithinLoginUserHomeFolderName;
		System.out.println(" **LoginUser Op: " + loginUserFileExecutionEnablingCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserFileExecutionEnablingCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end activateAnyFile4ExecutionByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	//I.1.6 Convert the file format from Dos to Unix/Linux format  for a file (script file) inside the Login User Home folder or sub-folder for execution on a U/L server
	public static int convertFileFromDosToUnixFormatByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerScriptFileWithinLoginUserHomeFolderPathAndName, ULServerCommandFactory bdENCmdFactory){
		//Example: dos2unix /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh
		//Here loginUserFileExecutionEnablingCmd: chmod +x  /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh		
		String loginUserFileConvertionCmd = "dos2unix " + enServerScriptFileWithinLoginUserHomeFolderPathAndName;
		System.out.println(" **LoginUser Op: " + loginUserFileConvertionCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserFileConvertionCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end convertFileFromDosToUnixFormatByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	//I.1.6 Run/execute a file (script file) inside the Login User Home folder or sub-folder for execution on a U/L server
	public static int runScriptFileWithinLoginUserHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster (String enServerScriptFileWithinLoginUserHomeFolderPathAndName, ULServerCommandFactory bdENCmdFactory){
		//Example: /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh
		//Here loginUserRnScriptFileCmd: /data/home/m041785/hdfs/dcTestHDFSScriptFile.sh		
		String loginUserRnScriptFileCmd = enServerScriptFileWithinLoginUserHomeFolderPathAndName + " && sleep 1";
		System.out.println(" **LoginUser Op: " + loginUserRnScriptFileCmd);
		
		int exitVal = performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster(loginUserRnScriptFileCmd, bdENCmdFactory);    	
    	return exitVal;
	}//end runScriptFileWitinLoginUserHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster


//===========================================	
//  I.2. U/L Server Operations By Root User	
//=========================================
	//Note: All the Methods of Operations by Root User through putty were tested to fail but successfull 
	//      by copying and running the generated commands by the methods on a open Windows command line 
	public static int performOperationByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerRootUserFileFolderSingleOrMultipeOpsCmd, 
			ULServerCommandFactory bdENCmdFactory, String rootPw){
			
		//Example: echo 'tdc' | su - root -c 'mkdir -p /home/hdfs/temp && chown -R m041785:users /home/hdfs/temp && chmod -R 777 /home/hdfs/temp';
		// Here enServerRootUserFileFolderSingleOrMultipeOpsCmd: mkdir -p /home/hdfs/temp && chown -R m041785:users /home/hdfs/temp && chmod -R 777 /home/hdfs/temp
		//String sudoToRootUserFileOrFolderOpsCmd = "echo '" + rootPw + "' | su - root -c '" + enServerRootUserFileFolderSingleOrMultipeOpsCmd + "'";
		String sudoToRootUserFileOrFolderOpsCmd = "echo '" + rootPw + "' | su - root -c '" + enServerRootUserFileFolderSingleOrMultipeOpsCmd + "'";
		String plinkRootUserFileOrFolderOpsCmd = bdENCmdFactory.getPlinkSingleCommandString(sudoToRootUserFileOrFolderOpsCmd, "");	
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkRootUserFileOrFolderOpsCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Root-User Operation(s) on Entry Node Local - " + plinkRootUserFileOrFolderOpsCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Root-User Operation(s)  on Entry Node Local - " + plinkRootUserFileOrFolderOpsCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **I.2-Root User** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end performOperationByRootUser_OnEntryNodeLocal_OnBDCluster
	
	// The following 5 methods are frequently used Root User Operations on a U/L server
	//I.2.1 Change permission for any file/folder on a U/l server
	public static int changeFileOrFolderPermissionByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, String newRecursiveOrNoRecursivePermissionModeNumStr, 
			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){
		
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();
		
		//Example: echo 'tdc' | su - root -c 'chmod -R 777 /home/hdfs/temp'
		//Here rootUserChangeFileOrFolderPermissionModeCmd: chmod -R 777 /home/hdfs/temp
		//Here: newRecursiveOrNoRecursivePermissionModeNumStr: -R 777
		String rootUserChangeFileOrFolderPermissionModeCmd = "chmod " + newRecursiveOrNoRecursivePermissionModeNumStr + " " +  enServerFileFolderPathAndName;
		System.out.println(" **RootUser Op: " + rootUserChangeFileOrFolderPermissionModeCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserChangeFileOrFolderPermissionModeCmd, bdENCmdFactory, rootPw);    	
    	//String exeResultsMsg = "";
    	//if (exitVal != 0){
    	//	exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Changing File/Folder Permission Mode on Entry Node Local - " + rootUserChangeFileOrFolderPermissionModeCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//} else {
		//	exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Changing File/Folder Permission Mode on Entry Node Local - " + rootUserChangeFileOrFolderPermissionModeCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//}	    	
    	//System.out.println(" **prep-1c** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end changeFileOrFolderPermissionByRootUser_OnEntryNodeLocal_OnBDCluster
	
	//I.2.2 Change ownership for any file/folder on a U/l server
	public static int changeFileOrFolderOwnershipByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, String newRecursiveOrNoRecursiveOwnerGroupStr, 
			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){		
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getPasswordLessPuttyKeyFile();
		
		//Example: echo 'tdc' | su - root -c 'chown -R m041785:users /home/hdfs/temp'
		//Here rootUserChangeFileOrFolderOwnershipCmd: chown -R m041785:users /home/hdfs/temp
		//Here newRecursiveOrNoRecursiveOwnerGroupStr: -R m041785:users
		String rootUserChangeFileOrFolderOwnershipCmd = "chown " + newRecursiveOrNoRecursiveOwnerGroupStr + " " +  enServerFileFolderPathAndName;
		System.out.println(" **RootUser Op: " + rootUserChangeFileOrFolderOwnershipCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserChangeFileOrFolderOwnershipCmd, bdENCmdFactory, rootPw);    	
    	//String exeResultsMsg = "";
    	//if (exitVal != 0){
    	//	exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Changing File/Folder Ownership on Entry Node Local - " + rootUserChangeFileOrFolderOwnershipCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//} else {
		//	exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Changing File/Folder Ownership on Entry Node Local - " + rootUserChangeFileOrFolderOwnershipCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//}	    	
    	//System.out.println(" **prep-1b** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end changeFileOrFolderOwnershipByRootUser_OnEntryNodeLocal_OnBDCluster
	
	//I.2.3 Create (safely) any folder on a U/l server
	public static int safelyCreateAFolderByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerFileFolderPathAndName, 
			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){		
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();
		
		//Example: echo 'tdc' | su - root -c 'mkdir -p /home/hdfs/temp'
		//Here rootUserSafelyCreateAFolderCmd: mkdir -p /home/hdfs/temp
		String rootUserSafelyCreateAFolderCmd = "mkdir -p " + enServerFileFolderPathAndName;
		System.out.println(" **RootUser Op: " + rootUserSafelyCreateAFolderCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserSafelyCreateAFolderCmd, bdENCmdFactory, rootPw);    	
    	//String exeResultsMsg = "";
    	//if (exitVal != 0){
    	//	exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Safely Creating a Folder on Entry Node Local - " + rootUserSafelyCreateAFolderCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//} else {
		//	exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Safely Creating a Folder on Entry Node Local - " + rootUserSafelyCreateAFolderCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
		//}	    	
    	//System.out.println(" **prep-1a** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end safelyCreateAFolderByRootUser_OnEntryNodeLocal_OnBDCluster
	
	//I.2.4 Remove any file on a U/L server
	public static int removeAnyFileByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerFileName, 
			String enServerFolderName, ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){
		if (!enServerFolderName.endsWith("/")){
			enServerFolderName += "/";
		}
		
		String enServerFileFullPathAndName = enServerFolderName + enServerFileName;
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();
		
		//Example: echo 'tdc' | su - root -c 'rm -f /home/hdfs/temp/dcTestdata.txt'
		//Here rootUserFileRemovingCmd: rm -f /home/hdfs/temp/dcTestdata.txt		
		String rootUserFileRemovingCmd = "rm -f " + enServerFileFullPathAndName;
		System.out.println(" **RootUser Op: " + rootUserFileRemovingCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserFileRemovingCmd, bdENCmdFactory, rootPw);    	
    	return exitVal;
	}//end removeAnyFileByRootUser_OnEntryNodeLocal_OnBDCluster
	
	//I.2.5 Activate file (script file) for execution on a U/L server
	public static int activateAnyFile4ExecutionByRootUser_OnEntryNodeLocal_OnBDCluster (String enServerScriptFileFullPathAndName, 
			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){
		//if (!enServerFolderName.endsWith("/")){
		//	enServerFolderName += "/";
		//}		
		//String enServerFileFullPathAndName = enServerFolderName + enServerScriptFileName;
		
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();
		
		//Example: echo 'tdc' | su - root -c 'chmod +x  /home/hdfs/temp/dcTestHDFSScriptFile.sh'
		//Here rootUserFileExecutionEnablingCmd: chmod +x  /home/hdfs/temp/dcTestHDFSScriptFile.sh		
		String rootUserFileExecutionEnablingCmd = "chmod +x " + enServerScriptFileFullPathAndName;
		System.out.println(" **RootUser Op: " + rootUserFileExecutionEnablingCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserFileExecutionEnablingCmd, bdENCmdFactory, rootPw);    	
    	return exitVal;
	}//end removeAnyFileByRootUser_OnEntryNodeLocal_OnBDCluster

	//I.2.6 Unlock a user on a U/L server
	public static int unlockALoginUserByRootUser_OnEntryNodeLocal_OnBDCluster (String loginUserName_4Unlocking, 
			ULServerCommandFactory bdENCmdFactory, ULServerCommandFactory bdENRootCmdFactory ){		
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();
		
		//Example: echo 'tdc' | su - root -c 'mkdir -p /home/hdfs/temp'
		//Here rootUserUnlockingLoginUserCmd: pam_tally --reset --user m041785		
		String rootUserUnlockingLoginUserCmd = "pam_tally --reset --user " + loginUserName_4Unlocking;
		System.out.println(" **RootUser Op: " + rootUserUnlockingLoginUserCmd);
		
		int exitVal = performOperationByRootUser_OnEntryNodeLocal_OnBDCluster(rootUserUnlockingLoginUserCmd, bdENCmdFactory, rootPw);    	
    	return exitVal;
	}//end safelyCreateAFolderByRootUser_OnEntryNodeLocal_OnBDCluster
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public static int runScriptFileByPutty_OnWindowsLocal(String winLocalScriptFilePathAndName, 
//			ULServerCommandFactory bdENCmdFactory){
//			
//		String puttyScriptFileRunFullCmd = bdENCmdFactory.getPuttyScriptCommandString(winLocalScriptFilePathAndName);
//				
//		int exitVal = executePuttyCommandOnLocalMachine(puttyScriptFileRunFullCmd);    	
//    	String exeResultsMsg = "";
//    	if (exitVal != 0){
//    		exeResultsMsg = "--xxx-- Failed In Running Hdfs Script File Step 4 - " + puttyScriptFileRunFullCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//		} else {
//			exeResultsMsg = "--***-- Suceessfully Executed Hdfs Script File Step 4 - " + puttyScriptFileRunFullCmd + " on the Server:  " + bdENCmdFactory.getServerURI() + " \n";
//		}	    	
//    	
//    	System.out.println(" **I.1'-Test User** exeResultsMsg: " + exeResultsMsg);
//		
//		return exitVal;
//	}//end performOperationByLoginUser_OnEntryNodeLocal_OnBDCluster
	
	private static int executePuttyCommandOnLocalMachine (String fullPuttyCmd) {		
		int exitVal = executeCommandOnLocalMachine (fullPuttyCmd);		
		return exitVal;
	}//end executePuttyCommandOnLocalMachine
	
	private static int executeCommandOnLocalMachine (String fullExecutableCmd) {
		Runtime rt = Runtime.getRuntime();
		int exitVal = 10000;
		
		try {
			Process proc = rt.exec(fullExecutableCmd);
			
			exitVal = proc.waitFor();
			System.out.println("\n--- current fullExecutableCmd exitVal is: " + exitVal);			
		} catch (Exception e1) {			
			e1.printStackTrace();
		} 
		
//		}catch (IOException e1) {			
//			e1.printStackTrace();
//		} catch (InterruptedException e2) {			
//			e2.printStackTrace();
//		}
		return exitVal;
	}//end executePuttyCommandOnLocalMachine
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////
///Common Utilities	
/////////////////////

	public static String copySrcHDFSFolderToDest (String rmHdfsFolderonDestCmd, String distcpHACmd,  
										String hdfsInternalAuthCmd, String enServerScriptFileDirectory,
										String winLocalHdfsRecordInfoFolder, ULServerCommandFactory bdENCmdFactory) 
												throws IOException{					
		//1. 
		winLocalHdfsRecordInfoFolder = winLocalHdfsRecordInfoFolder.replace("/", "\\");
		if (!winLocalHdfsRecordInfoFolder.endsWith("\\")){
			winLocalHdfsRecordInfoFolder += "\\";
		}
		
		File aFolderFile = new File (winLocalHdfsRecordInfoFolder);
		if (!aFolderFile.exists()){
			aFolderFile.mkdir();
		}
		
		String localHDFSFolderCopyingToDestClusterScriptFilePathAndName = winLocalHdfsRecordInfoFolder + "dcMCHadoop_HDFSFolder_CopyingToDestCluster_TempScriptFile.sh";			
		//prepareFile (localHDFSFolderCopyingToDestClusterScriptFilePathAndName,  "Script File For HDFS Folder CopyingToDestCluster ....");
		File aFile = new File (localHDFSFolderCopyingToDestClusterScriptFilePathAndName);
		if (!aFile.exists()){
			aFile.createNewFile();
			System.out.println("\n .. Created script file for Temporary HDFS Folder CopyingToDestCluster ....: \n" + localHDFSFolderCopyingToDestClusterScriptFilePathAndName);
		}
		
  		String tempHDFSFolderCopyRecFileName1 = "tempHdfsFolderCopyToDest1.txt";
  		String enSvrHDFSFolderCopyingToDestClusterFilePathAndName1 = enServerScriptFileDirectory + tempHDFSFolderCopyRecFileName1; 
  		//hdfs dfs -rm -r -skipTrash hdfs://hdpr05mn01.mayo.edu:8020/user/m041785/test/Hive;  		
  		//hadoop distcp -Ddfs.nameservices=MAYOHADOOPDEV1,MAYOHADOOPDEV3 -Ddfs.client.failover.proxy.provider.MAYOHADOOPDEV1=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider -Ddfs.ha.namenodes.MAYOHADOOPDEV1=namenode1,namenode2 -Ddfs.namenode.rpc-address.MAYOHADOOPDEV1.namenode1=hdpr03mn01.mayo.edu:8020 -Ddfs.namenode.servicerpc-address.MAYOHADOOPDEV1.namenode1=hdpr03mn01.mayo.edu:8022 -Ddfs.namenode.http-address.MAYOHADOOPDEV1.namenode1=hdpr03mn01.mayo.edu:50070 -Ddfs.namenode.https-address.MAYOHADOOPDEV1.namenode1=hdpr03mn01.mayo.edu:50470 -Ddfs.namenode.rpc-address.MAYOHADOOPDEV1.namenode2=hdpr03mn02.mayo.edu:8020 -Ddfs.namenode.servicerpc-address.MAYOHADOOPDEV1.namenode2=hdpr03mn02.mayo.edu:8022 -Ddfs.namenode.http-address.MAYOHADOOPDEV1.namenode2=hdpr03mn02.mayo.edu:50070 -Ddfs.namenode.https-address.MAYOHADOOPDEV1.namenode2=hdpr03mn02.mayo.edu:50470 -Ddfs.client.failover.proxy.provider.MAYOHADOOPDEV3=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider -Ddfs.ha.namenodes.MAYOHADOOPDEV3=namenode1,namenode2 -Ddfs.namenode.rpc-address.MAYOHADOOPDEV3.namenode1=hdpr05mn01.mayo.edu:8020 -Ddfs.namenode.servicerpc-address.MAYOHADOOPDEV3.namenode1=hdpr05mn01.mayo.edu:8022 -Ddfs.namenode.http-address.MAYOHADOOPDEV3.namenode1=hdpr05mn01.mayo.edu:50070 -Ddfs.namenode.https-address.MAYOHADOOPDEV3.namenode1=hdpr05mn01.mayo.edu:50470 -Ddfs.namenode.rpc-address.MAYOHADOOPDEV3.namenode2=hdpr05mn02.mayo.edu:8020 -Ddfs.namenode.servicerpc-address.MAYOHADOOPDEV3.namenode2=hdpr05mn02.mayo.edu:8022 -Ddfs.namenode.http-address.MAYOHADOOPDEV3.namenode2=hdpr05mn02.mayo.edu:50070 -Ddfs.namenode.https-address.MAYOHADOOPDEV3.namenode2=hdpr05mn02.mayo.edu:50470 -Dmapreduce.job.hdfs-servers.token-renewal.exclude=MAYOHADOOPDEV1 -Dhadoop.security.key.provider.path="" -Ddfs.encryption.key.provider.uri="" -prbugpcx -m 30 hdfs://MAYOHADOOPDEV1/user/m041785/test/Hive hdfs://MAYOHADOOPDEV3/user/m041785/test/ 2>&1 | tee dcTemp.txt; 
  		//cat  dcTemp.txt 
  		 		
  		
  		String hdfsFolderCopyCmd1R = " 2>&1 | tee " + enSvrHDFSFolderCopyingToDestClusterFilePathAndName1;   		
  		String hdfsFolderCopyCmd1 = distcpHACmd + hdfsFolderCopyCmd1R;
  		
  		String tempHDFSFolderCopyRecFileName2 = "tempHdfsFolderCopyToDest2.txt";
  		String enSvrHDFSFolderCopyingToDestClusterFilePathAndName2 = enServerScriptFileDirectory + tempHDFSFolderCopyRecFileName2;   
  		//cat  dcTemp.txt | grep 'completed' > dcTemp2.txt; 
  		String hdfsFolderCopyCmd2 = "cat  " + enSvrHDFSFolderCopyingToDestClusterFilePathAndName1 + " | grep 'completed' > " + enSvrHDFSFolderCopyingToDestClusterFilePathAndName2;
  		
  		System.out.println("\n*** hdfsFolderCopyCmd1: " + hdfsFolderCopyCmd1);
  		System.out.println("\n*** hdfsFolderCopyCmd2: " + hdfsFolderCopyCmd2);
  		
  		StringBuilder sb = new StringBuilder();			
				
		sb.append("cd " + enServerScriptFileDirectory + ";\n");		
		sb.append(hdfsInternalAuthCmd + "; \n");
		
		if (!rmHdfsFolderonDestCmd.isEmpty()){
			sb.append(rmHdfsFolderonDestCmd + "; \n");	
		}
				
		sb.append(hdfsFolderCopyCmd1 + "; \n");	
		sb.append(hdfsFolderCopyCmd2 + "; \n");	
		sb.append("rm " + enSvrHDFSFolderCopyingToDestClusterFilePathAndName1  + ";\n");
		//sb.append("kdestroy;\n");
		
		String hdfsFolderCopyingToDestClusterCmds = sb.toString();
		writeDataToAFile(localHDFSFolderCopyingToDestClusterScriptFilePathAndName, hdfsFolderCopyingToDestClusterCmds, false);		
		sb.setLength(0);
		
		//Desktop.getDesktop().open(new File(localHDFSFolderCopyingToDestClusterScriptFilePathAndName));			
		int exitVal1 = LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localHDFSFolderCopyingToDestClusterScriptFilePathAndName, 
				winLocalHdfsRecordInfoFolder, enServerScriptFileDirectory, bdENCmdFactory);

		if (exitVal1 == 0 ){
			System.out.println("\n*** Success - Running HDFS Folder CopyingToDestCluster on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		} else {
			System.out.println("\n*** Failed - Running HDFS Folder CopyingToDestCluster on the '" + bdENCmdFactory.getServerURI() + "' Entry Node of '" + bdENCmdFactory.getBdClusterName() + "' Cluster");
		}
  		  		
  		//2.  		
		int exitVal2 = LoginUserUtil.copyFile_ToWindowsLocal_FromEntryNodeLoginUserHomeFolder_OnBDCluster (tempHDFSFolderCopyRecFileName2,  
				enServerScriptFileDirectory, winLocalHdfsRecordInfoFolder, bdENCmdFactory);
				
		if (exitVal2 == 0 ){
			System.out.println("\n*** Success - Copying HDFS CopyingToDestCluster Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHdfsRecordInfoFolder  + "'");
		} else {
			System.out.println("\n*** Failed - Copying HDFS CopyingToDestCluster Results File from Linux Local - '" + enServerScriptFileDirectory + "' folder to WinLocal folder - '" + winLocalHdfsRecordInfoFolder  + "'");
		}
  		
		//3.
		String winLocalHDFSFolderCopyResultsFileFullPathAndName = winLocalHdfsRecordInfoFolder + tempHDFSFolderCopyRecFileName2;
		
		FileReader aFileReader = new FileReader(winLocalHDFSFolderCopyResultsFileFullPathAndName);
		BufferedReader br = new BufferedReader(aFileReader);		
		
		String line = "";
		String distcpStatus = "";		
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){				
				System.out.println("\n*** line: " + line);
				if (line.contains("completed")){
					String[] lineSplit = line.split("completed");
					distcpStatus = lineSplit[1].trim();
				}
			}		
		}
		br.close();
		aFileReader.close();
		
		System.out.println("\n*** distcpStatus: " + distcpStatus);		
		
		return distcpStatus;
	}//end copySrcHDFSFolderToDest

	public static String createDistcpHACmdString (BdCluster srcBdCluster, BdCluster destBdCluster, 
								String srcHdfsFolder, String destHdfsFolderParent, String distcpAdditionalOps) {		
		String srcClusterId = srcBdCluster.getBdClusterIdName();
		String destClusterId = destBdCluster.getBdClusterIdName();
		
		
		String srcNameNode1IPAddress = srcBdCluster.getBdHdfs1stNnIPAddressAndPort().replace("hdfs://", "").replace(":8020", "");
		String destNameNode1IPAddress = destBdCluster.getBdHdfs1stNnIPAddressAndPort().replace("hdfs://", "").replace(":8020", "");
		
		String srcNameNode2IPAddress = srcBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace("hdfs://", "").replace(":8020", "");
		String destNameNode2IPAddress = destBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace("hdfs://", "").replace(":8020", "");
		
		System.out.println("\n*** srcClusterId: " + srcClusterId);		
		//System.out.println("*** srcNameNode1IPAddress: " + srcNameNode1IPAddress);
		//System.out.println("*** srcNameNode2IPAddress: " + srcNameNode2IPAddress);
		
		System.out.println("\n*** destClusterId: " + destClusterId);
		//System.out.println("*** destNameNode1IPAddress: " + destNameNode1IPAddress);		
		//System.out.println("*** destNameNode2IPAddress: " + destNameNode2IPAddress);
		
		//String destHdfsFolderParent =  LoginUserUtil.getHdfsFolderParent (srcHdfsFolder);
		//String distcpAdditionalOps = "-m 30 -update -overwrite";		
		String distcpHACmd = "hadoop distcp \\\n"
				+ "-Ddfs.nameservices=" + srcClusterId + "," + destClusterId + " \\\n"
				+ "-Ddfs.client.failover.proxy.provider." + srcClusterId + "=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider \\\n"
				+ "-Ddfs.ha.namenodes." + srcClusterId + "=namenode1,namenode2 \\\n"
				+ "-Ddfs.namenode.rpc-address." + srcClusterId + ".namenode1=" + srcNameNode1IPAddress + ":8020 \\\n"
				+ "-Ddfs.namenode.servicerpc-address." + srcClusterId + ".namenode1=" + srcNameNode1IPAddress + ":8022 \\\n"
				+ "-Ddfs.namenode.http-address." + srcClusterId + ".namenode1=" + srcNameNode1IPAddress + ":50070 \\\n"
				+ "-Ddfs.namenode.https-address." + srcClusterId + ".namenode1=" + srcNameNode1IPAddress + ":50470 \\\n"
				
				+ "-Ddfs.namenode.rpc-address." + srcClusterId + ".namenode2=" + srcNameNode2IPAddress + ":8020 \\\n"
				+ "-Ddfs.namenode.servicerpc-address." + srcClusterId + ".namenode2=" + srcNameNode2IPAddress + ":8022 \\\n"
				+ "-Ddfs.namenode.http-address." + srcClusterId + ".namenode2=" + srcNameNode2IPAddress + ":50070 \\\n"
				+ "-Ddfs.namenode.https-address." + srcClusterId + ".namenode2=" + srcNameNode2IPAddress + ":50470 \\\n"
				
				+ "-Ddfs.client.failover.proxy.provider." + destClusterId + "=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider \\\n"
				+ "-Ddfs.ha.namenodes." + destClusterId + "=namenode1,namenode2 \\\n"
				+ "-Ddfs.namenode.rpc-address." + destClusterId + ".namenode1=" + destNameNode1IPAddress + ":8020 \\\n"
				+ "-Ddfs.namenode.servicerpc-address." + destClusterId + ".namenode1=" + destNameNode1IPAddress + ":8022 \\\n"
				+ "-Ddfs.namenode.http-address." + destClusterId + ".namenode1=" + destNameNode1IPAddress + ":50070 \\\n"
				+ "-Ddfs.namenode.https-address." + destClusterId + ".namenode1=" + destNameNode1IPAddress + ":50470 \\\n"
				
				+ "-Ddfs.namenode.rpc-address." + destClusterId + ".namenode2=" + destNameNode2IPAddress + ":8020 \\\n"
				+ "-Ddfs.namenode.servicerpc-address." + destClusterId + ".namenode2=" + destNameNode2IPAddress + ":8022 \\\n"
				+ "-Ddfs.namenode.http-address." + destClusterId + ".namenode2=" + destNameNode2IPAddress + ":50070 \\\n"
				+ "-Ddfs.namenode.https-address." + destClusterId + ".namenode2=" + destNameNode2IPAddress + ":50470 \\\n"
				+ "-Dmapreduce.job.hdfs-servers.token-renewal.exclude=" + srcClusterId + " -Dhadoop.security.key.provider.path=\"\" -Ddfs.encryption.key.provider.uri=\"\" \\\n"
				+ "-prbugpcx \\\n";
		if (!distcpAdditionalOps.isEmpty()){
			distcpHACmd += distcpAdditionalOps + " \\\n";
		}
		distcpHACmd += "hdfs://" + srcClusterId + srcHdfsFolder + " hdfs://" + destClusterId + destHdfsFolderParent;
		
		return distcpHACmd;
	}//end createDistcpHACmdString
	
	
	public static String getHdfsFolderParent (String hdfsFolder){
		String hdfsFolderParent = "/";		
		if (hdfsFolder.contains("/")){
			String[] hdfsFolderSplit = hdfsFolder.split("/");
			for (int i = 0; i < hdfsFolderSplit.length-1; i++){
				if (!hdfsFolderSplit[i].trim().isEmpty()){
					if (i < hdfsFolderSplit.length-2){
						hdfsFolderParent +=  hdfsFolderSplit[i] + "/";
					} else {
						hdfsFolderParent +=  hdfsFolderSplit[i];
					}	
					
				}
				
			}
		}
		//System.out.println("\n*** hdfsFolderParent: " + hdfsFolderParent);
		
		return hdfsFolderParent;
	}// end getHdfsFolderParent

		
	public static boolean prepareSrcHDFSFolderParentFoldersOnDestCluster (FileSystem srcHadoopFS, FileSystem destHadoopFS, String srcHdfsFolder) {
		String srcHdfsFolderParent = "/";
		ArrayList<String> srcHdfsFolderParentFolderList = new ArrayList<String> ();
		if (srcHdfsFolder.contains("/")){
			String[] srcHdfsFolderSplit = srcHdfsFolder.split("/");
			for (int i = 0; i < srcHdfsFolderSplit.length-1; i++){
				if (!srcHdfsFolderSplit[i].trim().isEmpty()){
					if (i < srcHdfsFolderSplit.length-2){
						srcHdfsFolderParent +=  srcHdfsFolderSplit[i] + "/";
					} else {
						srcHdfsFolderParent +=  srcHdfsFolderSplit[i];
					}
					
					srcHdfsFolderParentFolderList.add(srcHdfsFolderParent);
				}
				
			}
		}
		
		String prepareSrcHDFSFolderParentFoldersOnDestClusterStatusStr = "";
		int foldercount= 0;
		for (String tempParentFolder: srcHdfsFolderParentFolderList){
			foldercount++;
			
			if (!tempParentFolder.equals("/")){
				Path hdfsFileOrFolderPath = new Path(tempParentFolder);
				
				try {
					FileStatus srcPathstatus = srcHadoopFS.getFileStatus(hdfsFileOrFolderPath);			
					
					FsPermission srcParentPerm  = srcPathstatus.getPermission();
					String srcParentOwner   = srcPathstatus.getOwner();
					String srcParentGroup   = srcPathstatus.getGroup();
					System.out.println("(" + foldercount + ")  " + tempParentFolder 
							+ ":::" + srcParentPerm
							+ ":::" + srcParentOwner
							+ ":::" + srcParentGroup					
							);	
					
					boolean tempParentFolderonDestExistingStatus = destHadoopFS.exists(hdfsFileOrFolderPath);
					if (tempParentFolderonDestExistingStatus){
						System.out.println("*** srcHdfsFolderParent exists on dest ... making persmission and ownership the same on dest as those on src... " );
						destHadoopFS.setPermission(hdfsFileOrFolderPath, srcParentPerm);
						destHadoopFS.setOwner(hdfsFileOrFolderPath, srcParentOwner, srcParentGroup);
					} else {
						System.out.println("*** srcHdfsFolderParent does not exist on dest ... making folder with the same persmission and ownership as the folder on src... " );
						destHadoopFS.mkdirs(hdfsFileOrFolderPath, srcParentPerm);
						destHadoopFS.setOwner(hdfsFileOrFolderPath, srcParentOwner, srcParentGroup);
					}//end if
					
					FileStatus destPathstatus = destHadoopFS.getFileStatus(hdfsFileOrFolderPath);
					FsPermission destParentPerm  = destPathstatus.getPermission();
					String destParentOwner   = destPathstatus.getOwner();
					String destParentGroup   = destPathstatus.getGroup();
					
					boolean tempParentFolderonDestPreparingStatus = false;
					if (srcParentPerm.equals(destParentPerm)
						&& srcParentOwner.equals(destParentOwner)
						&& srcParentGroup.equals(destParentGroup)){
						tempParentFolderonDestPreparingStatus = true;	
					}
					prepareSrcHDFSFolderParentFoldersOnDestClusterStatusStr += tempParentFolderonDestPreparingStatus +  " === ";
					
				} catch (IOException e) {				
					e.printStackTrace();
				}//end try		
			}//end if			
		}//end for
		
		boolean prepareSrcHDFSFolderParentFoldersOnDestClusterStatus = false;
		if (!prepareSrcHDFSFolderParentFoldersOnDestClusterStatusStr.contains("false")){
			prepareSrcHDFSFolderParentFoldersOnDestClusterStatus = true;
		}
		
		return prepareSrcHDFSFolderParentFoldersOnDestClusterStatus;
	}//end prepareSrcHDFSFolderParentFoldersOnDestCluster
	
	
	public static ArrayList<String> obtainHDFSFolderFastDistributedCopy_ValidSubFolderList (FileSystem currHadoopFS, String srcHdfsFolder, double sizeInTBCutOff ) {
		long sizeInBytesCuttOff = (long) (sizeInTBCutOff*1024*1024*1024*1024);	
		ArrayList<String> hdfsValidFolder4BkpList = new ArrayList<String>();
		if (srcHdfsFolder.equalsIgnoreCase("/user") 
				|| srcHdfsFolder.equalsIgnoreCase("/user/") ){
			hdfsValidFolder4BkpList = obtainHDFSUserFolderCopy_ValidSubFolderList (currHadoopFS,  srcHdfsFolder, sizeInBytesCuttOff, hdfsValidFolder4BkpList);
		} else {
			hdfsValidFolder4BkpList = obtainHDFSFolderCopy_ValidSubFolderList (currHadoopFS,  srcHdfsFolder, sizeInBytesCuttOff, hdfsValidFolder4BkpList);
		}				
		
		return hdfsValidFolder4BkpList;
	}//end obtainHDFSFolderFastCopy_ValidSubFolderList
	

	private static ArrayList<String> obtainHDFSUserFolderCopy_ValidSubFolderList (FileSystem currHadoopFS, String srcHdfsFolder,
														double sizeInBytesCuttOff, ArrayList<String> hdfsValidFolder4BkpList ) {	
		boolean srcHdfsFolderExistingStatus = false;
		try {
			Path srcHdfsFileOrFolderPath = new Path(srcHdfsFolder);
			srcHdfsFolderExistingStatus = currHadoopFS.exists(srcHdfsFileOrFolderPath);
			
			ArrayList<String> tempSrcHdfsSubFolderList = new ArrayList<String>();	
			if (srcHdfsFolderExistingStatus){				
				tempSrcHdfsSubFolderList = HdfsFileFolder.getCurrentDirLevel1FullSubDirList(currHadoopFS, srcHdfsFolder);				
			}
			for (String subSrcHdfsFolder: tempSrcHdfsSubFolderList) {
				hdfsValidFolder4BkpList.add(subSrcHdfsFolder);
			}	
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		return hdfsValidFolder4BkpList;
	}//end obtainHDFSUserFolderCopy_ValidSubFolderList
	
	
	private static ArrayList<String> obtainHDFSFolderCopy_ValidSubFolderList (FileSystem currHadoopFS, String srcHdfsFolder,
														double sizeInBytesCuttOff, ArrayList<String> hdfsValidFolder4BkpList ) {	
		boolean srcHdfsFolderExistingStatus = false;
		try {
			Path srcHdfsFileOrFolderPath = new Path(srcHdfsFolder);
			srcHdfsFolderExistingStatus = currHadoopFS.exists(srcHdfsFileOrFolderPath);
			long directSubFolderNum = 0;
			long srcFolderSizeInBytes = 0;
			ArrayList<String> tempSrcHdfsSubFolderList = new ArrayList<String>();	
			if (srcHdfsFolderExistingStatus){				
				srcFolderSizeInBytes = currHadoopFS.getContentSummary(srcHdfsFileOrFolderPath).getLength();
				tempSrcHdfsSubFolderList = HdfsFileFolder.getCurrentDirLevel1FullSubDirList(currHadoopFS, srcHdfsFolder);
				directSubFolderNum = tempSrcHdfsSubFolderList.size();
			}
			if (srcFolderSizeInBytes > sizeInBytesCuttOff && directSubFolderNum >=2 ) {
				for (String subSrcHdfsFolder: tempSrcHdfsSubFolderList) {
					obtainHDFSFolderCopy_ValidSubFolderList (currHadoopFS,  subSrcHdfsFolder, sizeInBytesCuttOff, hdfsValidFolder4BkpList);
				}				
			} else {
				hdfsValidFolder4BkpList.add(srcHdfsFolder);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		return hdfsValidFolder4BkpList;
	}//end obtainHDFSFolderCopy_ValidSubFolderList
	
	public static Map<String,String> obtainSrcToDestHdfsSubFolderMap (ArrayList<String> srcHdfsSubFolderList, String srcHdfsFolder, 
			ArrayList<String> destHdfsSubFolderList){
		Map<String,String> currSrcToDestHdfsSubFolderMap = new LinkedHashMap<String,String>();
		for (String tempSrcSubFolder : srcHdfsSubFolderList){
			String targetSubFolder = tempSrcSubFolder.replace(srcHdfsFolder, "");
			String tempDestSubFolder = findTargetItemrFromAList (destHdfsSubFolderList, targetSubFolder );
			currSrcToDestHdfsSubFolderMap.put(tempSrcSubFolder, tempDestSubFolder);
		}
		return currSrcToDestHdfsSubFolderMap;
	}//end obtainSrcToDestHdfsSubFolderMap
	
	

	

	public static String findTargetItemrFromAList (ArrayList<String> aList, String target){
		String foundTarget = "DoesNotExist";
		for (String tempItem : aList){
			if (tempItem.equals(target)){
				foundTarget=tempItem;
				break;
			}
		}
		
		return foundTarget;
	}//end findTargetItemrFromAList
	
	public static void prepareFile_keepOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			//if (aFile.exists()){
			//	aFile.delete();
			//	System.out.println("\n .. Deleted existing working file for " + fileNoticeInfo + "!!!" );
			//	aFile.createNewFile();
			//	System.out.println("\n .. Created working file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			//} 
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created working file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	
	public static void prepareFile_deleteOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				System.out.println("\n .. Deleted existing working file for " + fileNoticeInfo + "!!!" );
				aFile.createNewFile();
				System.out.println("\n .. Created working file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			} else {
				aFile.createNewFile();
				System.out.println("\n .. Created working file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}
			
			//if (!aFile.exists()){				
			//}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	
	public static void prepareFolder_keepOld(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}				
	}//end prepareFolder
	

	public static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			//if (aFile.exists()){
			//	aFile.delete();
			//	System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
			//	aFile.createNewFile();
			//	System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
			//}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	
	public static void writeDataToAFile (String recordingFile, String recordInfo, boolean AppendingStatus) throws IOException{
		FileWriter outStream;
		if (AppendingStatus == true){
			outStream = new FileWriter(recordingFile, true);
		} else {
			outStream = new FileWriter(recordingFile);
		}		
		
	    PrintWriter output = new PrintWriter (outStream);
	    output.println(recordInfo);
	    System.out.println(recordInfo);
	    output.close();
	    outStream.close();	    
		
	}//end writeDataToAFile
	
	
////////////////////////////////////////////////////////////////////////////////////////////	
	
}//end class
