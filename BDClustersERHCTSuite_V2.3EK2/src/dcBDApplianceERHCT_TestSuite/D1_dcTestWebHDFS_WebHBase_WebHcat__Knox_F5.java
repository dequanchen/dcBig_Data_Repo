package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.Base64Str;
import dcModelClasses.DayClock;
import dcModelClasses.HiveViaKnoxOrF5ConnectionFactory;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/11/2016; 5/4/2016; 9/12-18/2017
*/ 

@SuppressWarnings("unused")
public class D1_dcTestWebHDFS_WebHBase_WebHcat__Knox_F5 {
	private static int testingTimesSeqNo = 7;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String knoxTestFolderName = "";
	private static String localKnoxTestDataFileName = "";
	private static String localKnoxTestAppendingDataFileName = "";
	private static String localKnoxTrustStorePathAndName = "";
	private static String localKnoxTrustStorePassWd = "";
	private static String internalKinitCmdStr = "";	
	private static String localKnoxLargeWebHbaseJsonDataFileName = "";		
	private static String localKnoxLargeWebHbaseXmlDataFileName = "";	
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String hdfsHiveDefaultTestDataFilePathAndName = "";
	private static int tableRowCount = 0;  
	private static String currScenarioDetailedTestingRecordInfo = "";
	private static double currScenarioSuccessRate = 0L;
			
	private static String enServerScriptFileDirectory = "/home/hdfs";
	
	public static void main(String[] args) throws Exception {
		//System.out.println("\n*** args.length is " + args.length);
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for Knox-ERHCT have not been specified yet!");
			return;
		}		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		knoxTestFolderName = args[4];
		localKnoxTestDataFileName = args[5];
		localKnoxTestAppendingDataFileName = args[6];
		localKnoxTrustStorePathAndName  = args[7];
		localKnoxTrustStorePassWd  = args[8];	
		
		internalKinitCmdStr = args[9];
		localKnoxLargeWebHbaseJsonDataFileName = args[10];		
		localKnoxLargeWebHbaseXmlDataFileName = args[11];
		
		if (!bdClusterUATestResultsParentFolder.endsWith("\\")){
			bdClusterUATestResultsParentFolder += "\\";
		}
		if (!knoxTestFolderName.endsWith("/")){
			knoxTestFolderName += "/";
		}		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
					
		run();
	}//end main
	
	
	public static void run() throws Exception {
		//1. Preparation:
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String scriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Knox\\";
	    prepareFolder(scriptFilesFoder, "Local Knox Testing Script Files");
	    
		String dcTestKnox_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKnoxF5_WritingAndReading_Records_WebHDFSWebHBaseWebHCat_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKnox_RecFilePathAndName,  "Records of Testing WebXXX/Knox/F5 on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----    Automated Knox-Gateway WebHDFS, WebHCat, WebHBase, ... Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		
		//3.Get cluster FileSystem and other information for testing	 		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//String bdKnoxClusterIdName = currBdCluster.getBdClusterIdName();
		
		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
					
//		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();	
//		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
//		
//		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
//		String currClusterKnoxFQDN = bdENCmdFactory.getServerURI();
//		System.out.println(" *** bdENCmdFactory.getServerURI() or currClusterKnoxFQDN: " + currClusterKnoxFQDN);
//		
//		String loginUserName = bdENCmdFactory.getUsername();
//		String loginUserADPassWd = bdENCmdFactory.getPassword();
		
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		String knoxTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Knox/";
		
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos			
		String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();		
		System.out.println(" *** Login User Name / AD Password: " + loginUser4AllNodesName + " / " + loginUserADPassWd);
		
//		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
//		String rootUserName = bdENRootCmdFactory.getUsername();
//		//String rootPw = bdENRootCmdFactory.getPassword();			
//		//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
//		//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
//		
//		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
//			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
//		}			
//		System.out.println("*** loginUserName is: " + loginUserName);
//		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
//		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		NumberFormat df = new DecimalFormat("#0.00");
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
	
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);				
		//String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		//System.out.println("\n*** loginUserADPassWd is: " + loginUserADPassWd);
				
						
		ArrayList<String> bdClusterKnoxNodeList = currBdCluster.getCurrentClusterKnoxNodeList();
		int clusterKNNumber = bdClusterKnoxNodeList.size();	
		int clusterKNNumber_Start = 0; //0..1
		System.out.println(" *** clusterKNNumber: " + clusterKNNumber);
		
		String curlExeNode = "EN01";
		BdNode en01BDNode = new BdNode(curlExeNode, bdClusterName);
		ULServerCommandFactory bdENCmdFactory = en01BDNode.getBdENCmdFactory();
		ULServerCommandFactory bdENRootCmdFactory = en01BDNode.getBdENRootCmdFactory();
		System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		
		
		String loginUserName = bdENCmdFactory.getUsername(); 						
		String rootUserName = bdENRootCmdFactory.getUsername();
					
		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
		}			
		System.out.println("*** loginUserName is: " + loginUserName);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
		System.out.println("*** On '" + curlExeNode + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;
		String enServerAppendingTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestAppendingDataFileName;
		
		System.out.println("*** localKnoxTestAppendingDataFileName is: " + localKnoxTestAppendingDataFileName);
		System.out.println("*** enServerAppendingTestDataFileFullPathAndName is: " + enServerAppendingTestDataFileFullPathAndName);
		
		
		//4. Move Test Data and Appending Test Data To EN01 Server Node of the Hadoop cluster
		//String localWinTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
		//String localAppendingTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
		
		
		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestAppendingDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseJsonDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseXmlDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		if (exitVal1 == 0 ){
			System.out.println("\n*** Done - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal2 == 0 ){
			System.out.println("\n*** Done - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		if (exitVal3 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal4 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		
		
		//#5.1 & #5.2 & #5.3 are test scenario types for WebHDFS testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[1]. WebHDFS \n", true);		
		
		//5.1 Login to EN01 and Perform Data Writing/Reading to WebHDFS file 
		//    via cURL cmds and active WebHDFS HTTP URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.1]. WebHDFS via Active Name Node (HDFS REST) Service \n", true);
		int activeNNNumber_Start = 0;
		int clusterActiveNNNumber = 1;
		for (int i = activeNNNumber_Start; i < clusterActiveNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			//(1) Get active WebHDFS HTTP URL:		
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();		
			
			//(2) Generate WebHDFS cmds:
			//Sample: curl -i -v --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebHDFSCmd_L = "curl -k --negotiate -u : --location-trusted ";
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_activenn" + (i +1) + ".txt";
			
			
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCms = curlKerberizedWebHDFSCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebHDFSCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + appendWritingToExistingFileCmd_R;
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName + "; \n");
			//sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			//sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	    
		    
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingFileFullCmd + ";\n");
		    sb.append(createAndWriteNewFileFullCmd + ";\n");
		    sb.append(appendWritingToExistingFileFullCmd + ";\n");    
		    
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHdfsScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHdfsScriptFile_Curl_activeNN" + (i +1) + ".sh";			
			prepareFile (knoxWebHdfsScriptFullFilePathAndName,  "Script File For Testing Knox WebHDFS on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHdfsTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHdfsScriptFullFilePathAndName, webHdfsTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHdfsScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHdfsScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(webHDFSFilePathAndName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				boolean foundWrittenStr = false;
				boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("105,Jim,Kirk,45500,M,Florida")) {
						foundWrittenStr = true;				
					}		
					if (line.contains("Total Message Number in Current HDFS File Is: 11")) {
						foundAppendedStr = true;
						break;
					}								
				}//end while
				br.close();
				
				
				if (foundWrittenStr == true  && foundAppendedStr==true){
					currTestScenarioSuccessStatus = true;
					hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				} 		
				
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebHDFS HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebHDFS HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 5.1
		
		
		
		//5.2 Login to EN01 and Perform Data Writing/Reading to WebHDFS file 
		//    via cURL cmds and Knox HTTPS URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.2]. WebHDFS via Knox Gateway Services \n", true);
				
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1;
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing WebHDFS Through Knox Node: " + tempKnENName);
			
			String currKnoxNodeName = "";
			String currKnoxClusterName = "";
			if (tempKnENName.contains("_")){
				String[] tempKnENNameSplit  = tempKnENName.split("_");
				currKnoxNodeName = tempKnENNameSplit[0];
				String simpleKnoxClusterName = tempKnENNameSplit[1];
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
					currKnoxClusterName = "BDDev3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
					currKnoxClusterName = "BDDev1";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
					currKnoxClusterName = "BDTest3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
					currKnoxClusterName = "BDTest2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
					currKnoxClusterName = "BDProd3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
					currKnoxClusterName = "BDProd2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
					currKnoxClusterName = "BDSdbx";
				}				
			} else {
				currKnoxNodeName = tempKnENName;
				currKnoxClusterName = bdClusterName;
			}
			
			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
			
			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
			
			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
						
			//(1) Get Current Hadoop Cluster's Knox HTTPS URL:			
			String currClusterKnoxWebHDFSHttpsURL = "https://" + currKnoxNodeFQDN + ":8442/gateway/" + bdKnoxClusterIdName + "/webhdfs";
			
			//(2) Generate WebHDFS cmds:
			//Sample: curl -i -k -u m041785:xxxxx -L "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebHDFSCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";					
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_knox" + (i +1) + ".txt";
						
					
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCmd = curlKerberizedWebHDFSCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebHDFSCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + appendWritingToExistingFileCmd_R;
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName + "; \n");
			//sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			//sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	    
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
				    
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 120; \n");
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 777 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    	    
		    sb.append(deleteExistingFileFullCmd + ";\n");
		    sb.append(createAndWriteNewFileFullCmd + ";\n");
		    sb.append(appendWritingToExistingFileFullCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHdfsScriptFullFilePathAndName2 = scriptFilesFoder + "dcTestKnox_WebHdfsScriptFile_Curl_Knox" + (i +1) + ".sh";			
			prepareFile (knoxWebHdfsScriptFullFilePathAndName2,  "Script File For Testing Knox WebHDFS on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHdfsTestingCmds2 = sb.toString();
			writeDataToAFile(knoxWebHdfsScriptFullFilePathAndName2, webHdfsTestingCmds2, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHdfsScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHdfsScriptFullFilePathAndName2, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
				
			//Read and find the appended data from the appended file
			boolean currTestScenarioSuccessStatus2 = false;
			Path filePath2 = new Path(webHDFSFilePathAndName);
			if (currHadoopFS.exists(filePath2)) {
			hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				FileStatus[] status2 = currHadoopFS.listStatus(filePath2);				
				BufferedReader br2 = new BufferedReader(new InputStreamReader(currHadoopFS.open(status2[0].getPath())));
				boolean foundWrittenStr2 = false;
				boolean foundAppendedStr2 = false;
				String line2 = "";
				while ((line2 = br2.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line2.contains("105,Jim,Kirk,45500,M,Florida")) {
						foundWrittenStr2 = true;				
					}		
					if (line2.contains("Total Message Number in Current HDFS File Is: 11")) {
						foundAppendedStr2 = true;
						break;
					}								
				}//end while
				br2.close();
				
				
				if (foundWrittenStr2 == true  && foundAppendedStr2==true){
					currTestScenarioSuccessStatus2 = true;
					hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				} 		
				
	        }//end outer if		
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);		
						
			String testRecordInfo2 = "";
			if (currTestScenarioSuccessStatus2) {
				successTestScenarioNum++;			
				testRecordInfo2 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebHDFSHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo2 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebHDFSHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo2, true);
			prevTime = currTime;					
		}//end 5.2		
					
				
		
		//5.3 Login to EN01 and Perform Data Writing/Reading to WebHDFS file 
		//    via cURL cmds and Cluster F5 Balancer URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.3]. WebHDFS via F5 Balancer Service \n", true);
		int f5BalancerNumber_Start = 0;
		int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			
			//(1) Get F5 Balancer HTTPs URL:		
			//String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			String clusterF5HttpsURL = currClusterF5ConnStr;
			
			//(2) Generate WebHDFS cmds:		
			//curl -i -k -u wa00336:bnhgui89 -X GET -L https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1/webhdfs/v1/user/m041785/test/Solr/solr_curl_query_result1.txt?op=LISTSTATUS
			String curlKerberizedWebHDFSCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";				
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_f5balancer" + (i +1) + ".txt";
						
					
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCmd = curlKerberizedWebHDFSCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebHDFSCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebHDFSCmd_L + appendWritingToExistingFileCmd_R;
					
				
		    
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingFileFullCmd + ";\n");
		    sb.append(createAndWriteNewFileFullCmd + ";\n");
		    sb.append(appendWritingToExistingFileFullCmd + ";\n");    
		    
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHdfsScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHdfsScriptFile_Curl_F5Balancer" + (i +1) + ".sh";				
			prepareFile (knoxWebHdfsScriptFullFilePathAndName,  "Script File For Testing Knox WebHDFS on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHdfsTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHdfsScriptFullFilePathAndName, webHdfsTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHdfsScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHdfsScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(webHDFSFilePathAndName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				boolean foundWrittenStr = false;
				boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("105,Jim,Kirk,45500,M,Florida")) {
						foundWrittenStr = true;				
					}		
					if (line.contains("Total Message Number in Current HDFS File Is: 11")) {
						foundAppendedStr = true;
						break;
					}								
				}//end while
				br.close();
				
				
				if (foundWrittenStr == true  && foundAppendedStr==true){
					currTestScenarioSuccessStatus = true;
					hdfsFilePathAndNameList.add(webHDFSFilePathAndName);
				} 		
				
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
			
		}// end 5.3
		
		

		//#6.1 & #6.2 & #6.3 & #6.4 are test scenario types for WebHBase testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[2]. WebHBase  \n", true);			
		
		//6.1 Login to EN01 and Perform Data Writing/Reading to WebHBase table 
		//    via cURL cmds and active WebHBase HTTP URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[2.1]. WebHBase via Stargate HBase (HBase REST) Service \n", true);
		int stargateHbaseService_Start = 0; //0..1..2
		int stargateHBaseRestServiceNumber = 3; //1..2..3		
		for (int i = stargateHbaseService_Start; i < stargateHBaseRestServiceNumber; i++){ 
			totalTestScenarioNumber++;
			
			//(1) Get active WebHBase HTTP URL:		
			String activeWebHbaseHttpURL = "";
			if (i==0){
				activeWebHbaseHttpURL = currBdCluster.getBdHdfs1stNnIPAddressAndPort().replace(":8020", ":8084").replace("hdfs", "http");
			}
			if (i==1){
				activeWebHbaseHttpURL = currBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace(":8020", ":8084").replace("hdfs", "http");
			}
			if (i==2){
				activeWebHbaseHttpURL = currBdCluster.getBdHdfs3rdNnIPAddressAndPort().replace(":8020", ":8084").replace("hdfs", "http");
			}
			
			if (!activeWebHbaseHttpURL.endsWith("/")){
				activeWebHbaseHttpURL += "/";
			}			
			 
			//(2) Transform TestData into Base64 Encoded Form from input file into one a new file:
			String uncodedLocalWebHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
			String encodedLocalWebHBaseTestDataFilePathAndName = uncodedLocalWebHBaseTestDataFilePathAndName.replace(".txt", "_base64encoded.txt");
			
			System.out.println(" *** uncodedLocalWebHBaseTestDataFilePathAndName: " + uncodedLocalWebHBaseTestDataFilePathAndName);
			System.out.println(" *** encodedLocalWebHBaseTestDataFilePathAndName: " + encodedLocalWebHBaseTestDataFilePathAndName);
			
			prepareFile (encodedLocalWebHBaseTestDataFilePathAndName,  "WebHBase Test Data in Base64 Encoded Form");
			transFormTestDataIntoBase64EncodedFormat (uncodedLocalWebHBaseTestDataFilePathAndName, 
					 encodedLocalWebHBaseTestDataFilePathAndName);
			
			//(3) Generate WebHbase cmds:
			//Sample: curl -i --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/exists";;
			String curlKerberizedWebHBaseCmd_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			//String webHBaseTableName = "employee_webhbase1";	
			String webHBaseTableName = "employee_webhbase" + (i+1);	
			String webHBaseTableURL = activeWebHbaseHttpURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			//String webHBaseTestResultFileName = "employee_webhbase_curl_result.txt";
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result.txt";
			
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd_R = " --location-trusted -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + deleteExistingHBaseTableCmd_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd: \n" + deleteExistingHBaseTableFullCmd);
					
			String createNewHBaseTableCmd_R = " --location-trusted -X  POST -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + createNewHBaseTableCmd_R;
			//System.out.println(" *** createNewHBaseTableFullCmd: \n" + createNewHBaseTableFullCmd);
			
			//(3)a Create WebHBase commands for single cell insertion for the first row (rowkey == 101) of test data 
			// using application/octet-stream method (no base64 encoding)
			
			String row1TestData = getTargetLineDataLineOfAFile (uncodedLocalWebHBaseTestDataFilePathAndName,  1);
			//System.out.println(" *** row1TestData: \n	" + row1TestData);
			String [] row1Split = row1TestData.split(",");
			String row1Key = row1Split[0].trim();
			String row1FirstName = row1Split[1].trim();
			String row1LastName = row1Split[2].trim();
			String row1Salary = row1Split[3].trim();
			String row1Gender = row1Split[4].trim();
			String row1Address = row1Split[5].trim();
			
			//System.out.println(" *** row1TestData: \n	" + row1Key + "," + row1FirstName + ","
			//				+ row1LastName + "," + row1Salary + "," + row1Gender + "," + row1Address);
			//Example: curl -s --negotiate -u : -H "Content-Type: application/octet-stream" \
			//         -X PUT http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName -d 'Joe'
			String insertRow1CellCmd_M =   " --location-trusted -X  PUT -H \"Content-Type: application/octet-stream\""
					+ " -L " + webHBaseTableURL + "/" + row1Key;
			String insertRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:firstName"
					+ " -d '" + row1FirstName + "'";
			String insertRow1LasttNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:lastName"
					+ " -d '" + row1LastName + "'";
			String insertRow1SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:salary"
					+ " -d '" + row1Salary + "'";
			String insertRow1GenderCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:gender"
					+ " -d '" + row1Gender + "'";
			String insertRow1AddressCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:address"
					+ " -d '" + row1Address + "'";
			
			//PUT==POST for WebHBase REST API
			insertRow1FirstNameCellFullCmd = insertRow1FirstNameCellFullCmd.replace("-X PUT", "-X POST");
			insertRow1LasttNameCellFullCmd = insertRow1LasttNameCellFullCmd.replace("-X PUT", "-X POST");
			//System.out.println(" *-* insertRow1FirstNameCellFullCmd: \n	" + insertRow1FirstNameCellFullCmd);
			//System.out.println(" *-* insertRow1AddressCellFullCmd: \n	" + insertRow1AddressCellFullCmd);
			
			//(3)b Create WebHBase commands for single row insertion for the 2nd and 3rd row (rowkey == 102 or 103) of test data 
					// using application/json and  text/xml methods respectively (required base64 encoding)
			//(3)b.1
			String row2TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  2);
			//System.out.println(" *** row2TestData: \n	" + row2TestData);
			
			String [] row2Split = row2TestData.split(",");
			String row2Key = row2Split[0].trim();
			String row2FirstName = row2Split[1].trim();
			String row2LastName = row2Split[2].trim();
			String row2Salary = row2Split[3].trim();
			String row2Gender = row2Split[4].trim();
			String row2Address = row2Split[5].trim();
			//System.out.println(" *-* row2TestData: \n	" + row2Key + "," + row2FirstName + ","
			//				+ row2LastName + "," + row2Salary + "," + row2Gender + "," + row2Address);
			
			String insertRow2Cmd_M =   " --location-trusted -X  POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row2Key);
			
			String insertRow2Cmd_R = " -d '{\"Row\": ["
									+ "{\"key\":\"" + row2Key + "\","
									+ "\"Cell\": ["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row2FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row2LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row2Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row2Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row2Address + "\"}"
									+ " ]} ]}'";
			String insertRow2FullCmd = curlKerberizedWebHBaseCmd_L + insertRow2Cmd_M + insertRow2Cmd_R;
			//System.out.println(" *-* insertRow2FullCmd: \n	" + insertRow2FullCmd);
			
			
			//(3)b.2
			String row3TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  3);
			//System.out.println(" *** row3TestData: \n	" + row3TestData);
			
			String [] row3Split = row3TestData.split(",");
			String row3Key = row3Split[0].trim();
			String row3FirstName = row3Split[1].trim();
			String row3LastName = row3Split[2].trim();
			String row3Salary = row3Split[3].trim();
			String row3Gender = row3Split[4].trim();
			String row3Address = row3Split[5].trim();
			//System.out.println(" *-* row3TestData: \n	" + row3Key + "," + row3FirstName + ","
			//				+ row3LastName + "," + row3Salary + "," + row3Gender + "," + row3Address);
			
			String insertRow3Cmd_M =   " --location-trusted -X  POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row3Key);
			
			String insertRow3Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row3Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row3FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row3LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row3Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row3Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row3Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow3FullCmd = curlKerberizedWebHBaseCmd_L + insertRow3Cmd_M + insertRow3Cmd_R;
			//System.out.println(" *-* insertRow3FullCmd: \n	" + insertRow3FullCmd);
			
			
			//(3)c Create WebHBase commands for multiple-row insertion for the 4th & 5th row, and 6th row (rowkey == 104 & 105 or 106) of test data 
			// using application/json and  text/xml methods respectively (required base64 encoding)
			
			//(3)c.1
			String row4TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  4);
			String row5TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  5);
			//System.out.println(" *** row4TestData: \n	" + row4TestData);
			//System.out.println(" *** row5TestData: \n	" + row5TestData);
			
			String [] row4Split = row4TestData.split(",");
			String row4Key = row4Split[0].trim();
			String row4FirstName = row4Split[1].trim();
			String row4LastName = row4Split[2].trim();
			String row4Salary = row4Split[3].trim();
			String row4Gender = row4Split[4].trim();
			String row4Address = row4Split[5].trim();
			//System.out.println(" *-* row4TestData: \n	" + row4Key + "," + row4FirstName + ","
			//				+ row4LastName + "," + row4Salary + "," + row4Gender + "," + row4Address);
			
			String [] row5Split = row5TestData.split(",");
			String row5Key = row5Split[0].trim();
			String row5FirstName = row5Split[1].trim();
			String row5LastName = row5Split[2].trim();
			String row5Salary = row5Split[3].trim();
			String row5Gender = row5Split[4].trim();
			String row5Address = row5Split[5].trim();
			//System.out.println(" *-* row5TestData: \n	" + row5Key + "," + row5FirstName + ","
			//				+ row5LastName + "," + row5Salary + "," + row5Gender + "," + row5Address);
			
			String insertRows4N5Cmd_M =   " --location-trusted -X  POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRows4N5Cmd_R = " -d '{\"Row\":["
									+ "{\"key\":\"" + row4Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row4FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row4LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row4Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row4Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row4Address + "\"}"
									+ " ]},"
									+ "{\"key\":\"" + row5Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row5FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row5LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row5Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row5Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row5Address + "\"}"
									+ " ]} ]}'";
			String insertRows4N5FullCmd = curlKerberizedWebHBaseCmd_L + insertRows4N5Cmd_M + insertRows4N5Cmd_R;
			//System.out.println(" *-* insertRows4N5FullCmd: \n	" + insertRows4N5FullCmd);
					
					
			//(3)c.2
			String row6TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  6);
			System.out.println(" *** row6TestData: \n	" + row6TestData);
			
			String [] row6Split = row6TestData.split(",");
			String row6Key = row6Split[0].trim();
			String row6FirstName = row6Split[1].trim();
			String row6LastName = row6Split[2].trim();
			String row6Salary = row6Split[3].trim();
			String row6Gender = row6Split[4].trim();
			String row6Address = row6Split[5].trim();
			//System.out.println(" *-* row6TestData: \n	" + row6Key + "," + row6FirstName + ","
			//				+ row6LastName + "," + row6Salary + "," + row6Gender + "," + row6Address);
			
			String insertRow6Cmd_M =  " --location-trusted -X  POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRow6Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row6Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row6FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row6LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row6Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row6Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row6Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow6FullCmd = curlKerberizedWebHBaseCmd_L + insertRow6Cmd_M + insertRow6Cmd_R;
			System.out.println(" *-* insertRow6FullCmd: \n	" + insertRow6FullCmd);
			
			
			//(3)d Create WebHBase commands for querying HBase Data 
			//Note: curlKerberizedWebHBaseCmd_L == "curl -s --negotiate -u : "; 
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			//curl -s --negotiate -u : --location-trusted -X  GET -H "Accept: application/octet-stream" -L http://hdpr05mn01.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}'
			//curl -s --negotiate -u : --location-trusted -X  GET -H "Accept: application/octet-stream" -L http://hdpr05mn01.mayo.edu:8084/employee_webhbase1/102/cfs:lastName | awk '{print $0""}'

			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			String queryHBaseTableCellCmd_M =  " --location-trusted -X  GET -H \"Accept: application/octet-stream\" -L " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/101/cfs:firstName | awk '{print $0\"\"}' > " + tempLocalWebHBaseTestResultPathAndName;
					
			String getRow2LastNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M		
					+ "/102/cfs:lastName | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow3SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/103/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow4SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/104/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow5GenderCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/105/cfs:gender | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow6AddressCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/106/cfs:address | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd = "rm -f " + localWebHBaseTestResultPathAndName;
			
			//(4)
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd + ";\n");
			sb.append(createNewHBaseTableFullCmd + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd + ";\n");		
			sb.append(insertRow1LasttNameCellFullCmd + ";\n");
			sb.append(insertRow1SalaryCellFullCmd + ";\n");
			sb.append(insertRow1GenderCellFullCmd + ";\n");
			sb.append(insertRow1AddressCellFullCmd + ";\n");		
			sb.append(insertRow2FullCmd + ";\n");
			sb.append(insertRow3FullCmd + ";\n");
			sb.append(insertRows4N5FullCmd + ";\n");
			sb.append(insertRow6FullCmd + ";\n");
		
			sb.append(getRow1FirstNameCellFullCmd + ";\n");
			sb.append(getRow2LastNameCellFullCmd + ";\n");
			sb.append(getRow3SalaryCellFullCmd + ";\n");		
			sb.append(getRow4SalaryCellFullCmd + ";\n");
			sb.append(getRow5GenderCellFullCmd + ";\n");
			sb.append(getRow6AddressCellFullCmd + ";\n");		
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd + ";\n"); 
			
			//sb.append("hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + webHBaseTestResultHdfsPathAndName + "; \n");
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//Up to 5/4/2016, BDsdbx Shared Knox01 with BDDev1 that was managed by BDDev1 Ambari
			//if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
			//		||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
			//		|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//	String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
			//	System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
			//	copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//}
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHBaseScriptFile_Curl_StargateHBase" + (i+1) + ".sh";			
			prepareFile (knoxWebHBaseScriptFullFilePathAndName,  "Script File For Testing WebHBase on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(5)
			boolean currTestScenarioSuccessStatus5 = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("Joe,Smith,55000,120000,M,Virginia")) {
						currTestScenarioSuccessStatus5 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
									
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
						+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHBase System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
	        			+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHBase System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }			
			
			System.out.println("\n*** currTestScenarioSuccessStatus5: " + currTestScenarioSuccessStatus5 );
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 6.1
		
		
		
		//6.2 Login to EN01 and Perform Data Writing/Reading to WebHBase table 
		//    via cURL cmds and Knox HTTPS URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[2.2]. WebHBase via Knox Gateway Services \n", true);
				
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1; //4...2;
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing WebHBase Through Knox Node: " + tempKnENName);
			
			String currKnoxNodeName = "";
			String currKnoxClusterName = "";
			if (tempKnENName.contains("_")){
				String[] tempKnENNameSplit  = tempKnENName.split("_");
				currKnoxNodeName = tempKnENNameSplit[0];
				String simpleKnoxClusterName = tempKnENNameSplit[1];
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
					currKnoxClusterName = "BDDev3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
					currKnoxClusterName = "BDDev1";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
					currKnoxClusterName = "BDTest3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
					currKnoxClusterName = "BDTest2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
					currKnoxClusterName = "BDProd3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
					currKnoxClusterName = "BDProd2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
					currKnoxClusterName = "BDSdbx";
				}				
			} else {
				currKnoxNodeName = tempKnENName;
				currKnoxClusterName = bdClusterName;
			}
			
			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
			
			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
			
			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
						
			//(1) Get Current Hadoop Cluster's Knox HTTPS URL:
			//Ex: https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPDEV3/hbase/v1/?op=LISTSTATUS
			String currClusterKnoxWebHBaseHttpsURL = "https://" + currKnoxNodeFQDN + ":8442/gateway/" + bdKnoxClusterIdName + "/hbase";
			
			//(2) Transform TestData into Base64 Encoded Form from input file into one a new file
			String uncodedLocalWebHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
			String encodedLocalWebHBaseTestDataFilePathAndName = uncodedLocalWebHBaseTestDataFilePathAndName.replace(".txt", "_base64encoded.txt");
			
			System.out.println(" *** uncodedLocalWebHBaseTestDataFilePathAndName: " + uncodedLocalWebHBaseTestDataFilePathAndName);
			System.out.println(" *** encodedLocalWebHBaseTestDataFilePathAndName: " + encodedLocalWebHBaseTestDataFilePathAndName);
			
			prepareFile (encodedLocalWebHBaseTestDataFilePathAndName,  "WebHBase Test Data in Base64 Encoded Form");
			transFormTestDataIntoBase64EncodedFormat (uncodedLocalWebHBaseTestDataFilePathAndName, 
					 encodedLocalWebHBaseTestDataFilePathAndName);
			
			//(3) Generate WebHbase cmds:
			//Sample: curl -i --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/exists";;
			String curlKerberizedWebHBaseCmd_L = "curl -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted ";
			String webHBaseTableName = "employee_knox_webhbase" + (i+1);		
			String webHBaseTableURL = currClusterKnoxWebHBaseHttpsURL + "/" + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result_knox.txt";
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd_R = " -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + deleteExistingHBaseTableCmd_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd: \n" + deleteExistingHBaseTableFullCmd);
					
			String createNewHBaseTableCmd_R = " -X POST -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + createNewHBaseTableCmd_R;
			//System.out.println(" *** createNewHBaseTableFullCmd: \n" + createNewHBaseTableFullCmd);
			
			//(3)a Create WebHBase commands for single cell insertion for the first row (rowkey == 101) of test data 
			// using application/octet-stream method (no base64 encoding)
			
			String row1TestData = getTargetLineDataLineOfAFile (uncodedLocalWebHBaseTestDataFilePathAndName,  1);
			//System.out.println(" *** row1TestData: \n	" + row1TestData);
			String [] row1Split = row1TestData.split(",");
			String row1Key = row1Split[0].trim();
			String row1FirstName = row1Split[1].trim();
			String row1LastName = row1Split[2].trim();
			String row1Salary = row1Split[3].trim();
			String row1Gender = row1Split[4].trim();
			String row1Address = row1Split[5].trim();
			
			//System.out.println(" *** row1TestData: \n	" + row1Key + "," + row1FirstName + ","
			//				+ row1LastName + "," + row1Salary + "," + row1Gender + "," + row1Address);
			//Example: curl -s --negotiate -u : -H "Content-Type: application/octet-stream" \
			//         -X PUT http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName -d 'Joe'
			String insertRow1CellCmd_M =  " -X PUT -H \"Content-Type: application/octet-stream\""
					+ " -L " + webHBaseTableURL + "/" + row1Key;
			String insertRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:firstName"
					+ " -d '" + row1FirstName + "'";
			String insertRow1LasttNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:lastName"
					+ " -d '" + row1LastName + "'";
			String insertRow1SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:salary"
					+ " -d '" + row1Salary + "'";
			String insertRow1GenderCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:gender"
					+ " -d '" + row1Gender + "'";
			String insertRow1AddressCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:address"
					+ " -d '" + row1Address + "'";
			
			//PUT==POST for WebHBase REST API
			insertRow1FirstNameCellFullCmd = insertRow1FirstNameCellFullCmd.replace("-X PUT", "-X POST");
			insertRow1LasttNameCellFullCmd = insertRow1LasttNameCellFullCmd.replace("-X PUT", "-X POST");
			//System.out.println(" *-* insertRow1FirstNameCellFullCmd: \n	" + insertRow1FirstNameCellFullCmd);
			//System.out.println(" *-* insertRow1AddressCellFullCmd: \n	" + insertRow1AddressCellFullCmd);
			
			//(3)b Create WebHBase commands for single row insertion for the 2nd and 3rd row (rowkey == 102 or 103) of test data 
					// using application/json and  text/xml methods respectively (required base64 encoding)
			//(3)b.1
			String row2TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  2);
			//System.out.println(" *** row2TestData: \n	" + row2TestData);
			
			String [] row2Split = row2TestData.split(",");
			String row2Key = row2Split[0].trim();
			String row2FirstName = row2Split[1].trim();
			String row2LastName = row2Split[2].trim();
			String row2Salary = row2Split[3].trim();
			String row2Gender = row2Split[4].trim();
			String row2Address = row2Split[5].trim();
			//System.out.println(" *-* row2TestData: \n	" + row2Key + "," + row2FirstName + ","
			//				+ row2LastName + "," + row2Salary + "," + row2Gender + "," + row2Address);
			
			String insertRow2Cmd_M =  " -X POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row2Key);
			
			String insertRow2Cmd_R = " -d '{\"Row\": ["
									+ "{\"key\":\"" + row2Key + "\","
									+ "\"Cell\": ["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row2FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row2LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row2Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row2Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row2Address + "\"}"
									+ " ]} ]}'";
			String insertRow2FullCmd = curlKerberizedWebHBaseCmd_L + insertRow2Cmd_M + insertRow2Cmd_R;
			//System.out.println(" *-* insertRow2FullCmd: \n	" + insertRow2FullCmd);
			
			
			//(3)b.2
			String row3TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  3);
			//System.out.println(" *** row3TestData: \n	" + row3TestData);
			
			String [] row3Split = row3TestData.split(",");
			String row3Key = row3Split[0].trim();
			String row3FirstName = row3Split[1].trim();
			String row3LastName = row3Split[2].trim();
			String row3Salary = row3Split[3].trim();
			String row3Gender = row3Split[4].trim();
			String row3Address = row3Split[5].trim();
			//System.out.println(" *-* row3TestData: \n	" + row3Key + "," + row3FirstName + ","
			//				+ row3LastName + "," + row3Salary + "," + row3Gender + "," + row3Address);
			
			String insertRow3Cmd_M =  " -X POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row3Key);
			
			String insertRow3Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row3Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row3FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row3LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row3Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row3Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row3Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow3FullCmd = curlKerberizedWebHBaseCmd_L + insertRow3Cmd_M + insertRow3Cmd_R;
			//System.out.println(" *-* insertRow3FullCmd: \n	" + insertRow3FullCmd);
			
			
			//(3)c Create WebHBase commands for multiple-row insertion for the 4th & 5th row, and 6th row (rowkey == 104 & 105 or 106) of test data 
			// using application/json and  text/xml methods respectively (required base64 encoding)
			
			//(3)c.1
			String row4TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  4);
			String row5TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  5);
			//System.out.println(" *** row4TestData: \n	" + row4TestData);
			//System.out.println(" *** row5TestData: \n	" + row5TestData);
			
			String [] row4Split = row4TestData.split(",");
			String row4Key = row4Split[0].trim();
			String row4FirstName = row4Split[1].trim();
			String row4LastName = row4Split[2].trim();
			String row4Salary = row4Split[3].trim();
			String row4Gender = row4Split[4].trim();
			String row4Address = row4Split[5].trim();
			//System.out.println(" *-* row4TestData: \n	" + row4Key + "," + row4FirstName + ","
			//				+ row4LastName + "," + row4Salary + "," + row4Gender + "," + row4Address);
			
			String [] row5Split = row5TestData.split(",");
			String row5Key = row5Split[0].trim();
			String row5FirstName = row5Split[1].trim();
			String row5LastName = row5Split[2].trim();
			String row5Salary = row5Split[3].trim();
			String row5Gender = row5Split[4].trim();
			String row5Address = row5Split[5].trim();
			//System.out.println(" *-* row5TestData: \n	" + row5Key + "," + row5FirstName + ","
			//				+ row5LastName + "," + row5Salary + "," + row5Gender + "," + row5Address);
			
			String insertRows4N5Cmd_M =  " -X POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRows4N5Cmd_R = " -d '{\"Row\":["
									+ "{\"key\":\"" + row4Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row4FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row4LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row4Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row4Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row4Address + "\"}"
									+ " ]},"
									+ "{\"key\":\"" + row5Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row5FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row5LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row5Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row5Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row5Address + "\"}"
									+ " ]} ]}'";
			String insertRows4N5FullCmd = curlKerberizedWebHBaseCmd_L + insertRows4N5Cmd_M + insertRows4N5Cmd_R;
			//System.out.println(" *-* insertRows4N5FullCmd: \n	" + insertRows4N5FullCmd);
					
					
			//(3)c.2
			String row6TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  6);
			System.out.println(" *** row6TestData: \n	" + row6TestData);
			
			String [] row6Split = row6TestData.split(",");
			String row6Key = row6Split[0].trim();
			String row6FirstName = row6Split[1].trim();
			String row6LastName = row6Split[2].trim();
			String row6Salary = row6Split[3].trim();
			String row6Gender = row6Split[4].trim();
			String row6Address = row6Split[5].trim();
			//System.out.println(" *-* row6TestData: \n	" + row6Key + "," + row6FirstName + ","
			//				+ row6LastName + "," + row6Salary + "," + row6Gender + "," + row6Address);
			
			String insertRow6Cmd_M =  " -X POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRow6Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row6Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row6FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row6LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row6Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row6Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row6Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow6FullCmd = curlKerberizedWebHBaseCmd_L + insertRow6Cmd_M + insertRow6Cmd_R;
			System.out.println(" *-* insertRow6FullCmd: \n	" + insertRow6FullCmd);
			
			
			//(3)d Create WebHBase commands for querying HBase Data 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result_knox.txt", "_result_knox_temp.txt"); //_result.txt --> _result_knox.txt
			String queryHBaseTableCellCmd_M =  " -X GET -H \"Accept: application/octet-stream\" -L " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/101/cfs:firstName | awk '{print $0\"\"}' > " + tempLocalWebHBaseTestResultPathAndName;
					
			String getRow2LastNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M		
					+ "/102/cfs:lastName | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow3SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/103/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow4SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/104/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow5GenderCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/105/cfs:gender | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow6AddressCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/106/cfs:address | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd = "rm -f " + localWebHBaseTestResultPathAndName;
			
			//(4)
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd + ";\n");
			sb.append(createNewHBaseTableFullCmd + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd + ";\n");		
			sb.append(insertRow1LasttNameCellFullCmd + ";\n");
			sb.append(insertRow1SalaryCellFullCmd + ";\n");
			sb.append(insertRow1GenderCellFullCmd + ";\n");
			sb.append(insertRow1AddressCellFullCmd + ";\n");		
			sb.append(insertRow2FullCmd + ";\n");
			sb.append(insertRow3FullCmd + ";\n");
			sb.append(insertRows4N5FullCmd + ";\n");
			sb.append(insertRow6FullCmd + ";\n");
		
			sb.append(getRow1FirstNameCellFullCmd + ";\n");
			sb.append(getRow2LastNameCellFullCmd + ";\n");
			sb.append(getRow3SalaryCellFullCmd + ";\n");		
			sb.append(getRow4SalaryCellFullCmd + ";\n");
			sb.append(getRow5GenderCellFullCmd + ";\n");
			sb.append(getRow6AddressCellFullCmd + ";\n");		
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd + ";\n"); 
			
			//String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + webHBaseTestResultHdfsPathAndName;
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//Up to 5/4/2016, BDsdbx Shared Knox01 with BDDev1 that was managed by BDDev1 Ambari
			//if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
			//		||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
			//		|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//	String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
			//	System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
			//	copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//}
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxSecureWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_Secure_WebHBaseScriptFile_Curl_Knox" + (i+1) + ".sh";			
			prepareFile (knoxSecureWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxSecureWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxSecureWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(5)
			boolean currTestScenarioSuccessStatus5 = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("Joe,Smith,55000,120000,M,Virginia")) {
						currTestScenarioSuccessStatus5 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
						+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
	        			+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
		   writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);			    
		   prevTime = currTime;					
		}//end 6.2		
					
		
		//6.3 Login to EN01 and Perform Data Writing/Reading to WebHBase table 
		//    via cURL cmds and F5 Balancer HTTPs URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[2.3]. WebHBase via F5 Balancer Service \n", true);
		//int f5BalancerNumber_Start = 0;
		//int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			
			//(1) Get F5 Balancer HTTPs URL:		
			//String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			String clusterF5HttpsURL = currClusterF5ConnStr + "/hbase/";
			 
			//(2) Transform TestData into Base64 Encoded Form from input file into one a new file:
			String uncodedLocalWebHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
			String encodedLocalWebHBaseTestDataFilePathAndName = uncodedLocalWebHBaseTestDataFilePathAndName.replace(".txt", "_base64encoded.txt");
			
			System.out.println(" *** uncodedLocalWebHBaseTestDataFilePathAndName: " + uncodedLocalWebHBaseTestDataFilePathAndName);
			System.out.println(" *** encodedLocalWebHBaseTestDataFilePathAndName: " + encodedLocalWebHBaseTestDataFilePathAndName);
			
			prepareFile (encodedLocalWebHBaseTestDataFilePathAndName,  "WebHBase Test Data in Base64 Encoded Form");
			transFormTestDataIntoBase64EncodedFormat (uncodedLocalWebHBaseTestDataFilePathAndName, 
					 encodedLocalWebHBaseTestDataFilePathAndName);
			
			//(3) Generate WebHbase cmds:
			//Sample: curl -i --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/exists";;
			//String curlKerberizedWebHBaseCmd_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			String curlKerberizedWebHBaseCmd_L = "curl -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted ";
			
			//String webHBaseTableName = "employee_webhbase1";	
			String webHBaseTableName = "employee_webhbase_f5balancer" + (i+1);	
			String webHBaseTableURL = clusterF5HttpsURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			//String webHBaseTestResultFileName = "employee_webhbase_curl_result.txt";
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result_f5balancer.txt";
			
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd_R = " --location-trusted -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + deleteExistingHBaseTableCmd_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd: \n" + deleteExistingHBaseTableFullCmd);
					
			String createNewHBaseTableCmd_R = " --location-trusted -X  POST -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + createNewHBaseTableCmd_R;
			//System.out.println(" *** createNewHBaseTableFullCmd: \n" + createNewHBaseTableFullCmd);
			
			//(3)a Create WebHBase commands for single cell insertion for the first row (rowkey == 101) of test data 
			// using application/octet-stream method (no base64 encoding)
			
			String row1TestData = getTargetLineDataLineOfAFile (uncodedLocalWebHBaseTestDataFilePathAndName,  1);
			//System.out.println(" *** row1TestData: \n	" + row1TestData);
			String [] row1Split = row1TestData.split(",");
			String row1Key = row1Split[0].trim();
			String row1FirstName = row1Split[1].trim();
			String row1LastName = row1Split[2].trim();
			String row1Salary = row1Split[3].trim();
			String row1Gender = row1Split[4].trim();
			String row1Address = row1Split[5].trim();
			
			//System.out.println(" *** row1TestData: \n	" + row1Key + "," + row1FirstName + ","
			//				+ row1LastName + "," + row1Salary + "," + row1Gender + "," + row1Address);
			//Example: curl -s --negotiate -u : -H "Content-Type: application/octet-stream" \
			//         -X PUT http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName -d 'Joe'
			String insertRow1CellCmd_M =   " --location-trusted -X  PUT -H \"Content-Type: application/octet-stream\""
					+ " -L " + webHBaseTableURL + "/" + row1Key;
			String insertRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:firstName"
					+ " -d '" + row1FirstName + "'";
			String insertRow1LasttNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:lastName"
					+ " -d '" + row1LastName + "'";
			String insertRow1SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:salary"
					+ " -d '" + row1Salary + "'";
			String insertRow1GenderCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:gender"
					+ " -d '" + row1Gender + "'";
			String insertRow1AddressCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:address"
					+ " -d '" + row1Address + "'";
			
			//PUT==POST for WebHBase REST API
			insertRow1FirstNameCellFullCmd = insertRow1FirstNameCellFullCmd.replace("-X PUT", "-X POST");
			insertRow1LasttNameCellFullCmd = insertRow1LasttNameCellFullCmd.replace("-X PUT", "-X POST");
			//System.out.println(" *-* insertRow1FirstNameCellFullCmd: \n	" + insertRow1FirstNameCellFullCmd);
			//System.out.println(" *-* insertRow1AddressCellFullCmd: \n	" + insertRow1AddressCellFullCmd);
			
			//(3)b Create WebHBase commands for single row insertion for the 2nd and 3rd row (rowkey == 102 or 103) of test data 
					// using application/json and  text/xml methods respectively (required base64 encoding)
			//(3)b.1
			String row2TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  2);
			//System.out.println(" *** row2TestData: \n	" + row2TestData);
			
			String [] row2Split = row2TestData.split(",");
			String row2Key = row2Split[0].trim();
			String row2FirstName = row2Split[1].trim();
			String row2LastName = row2Split[2].trim();
			String row2Salary = row2Split[3].trim();
			String row2Gender = row2Split[4].trim();
			String row2Address = row2Split[5].trim();
			//System.out.println(" *-* row2TestData: \n	" + row2Key + "," + row2FirstName + ","
			//				+ row2LastName + "," + row2Salary + "," + row2Gender + "," + row2Address);
			
			String insertRow2Cmd_M =   " --location-trusted -X  POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row2Key);
			
			String insertRow2Cmd_R = " -d '{\"Row\": ["
									+ "{\"key\":\"" + row2Key + "\","
									+ "\"Cell\": ["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row2FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row2LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row2Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row2Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row2Address + "\"}"
									+ " ]} ]}'";
			String insertRow2FullCmd = curlKerberizedWebHBaseCmd_L + insertRow2Cmd_M + insertRow2Cmd_R;
			//System.out.println(" *-* insertRow2FullCmd: \n	" + insertRow2FullCmd);
			
			
			//(3)b.2
			String row3TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  3);
			//System.out.println(" *** row3TestData: \n	" + row3TestData);
			
			String [] row3Split = row3TestData.split(",");
			String row3Key = row3Split[0].trim();
			String row3FirstName = row3Split[1].trim();
			String row3LastName = row3Split[2].trim();
			String row3Salary = row3Split[3].trim();
			String row3Gender = row3Split[4].trim();
			String row3Address = row3Split[5].trim();
			//System.out.println(" *-* row3TestData: \n	" + row3Key + "," + row3FirstName + ","
			//				+ row3LastName + "," + row3Salary + "," + row3Gender + "," + row3Address);
			
			String insertRow3Cmd_M =   " --location-trusted -X  POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row3Key);
			
			String insertRow3Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row3Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row3FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row3LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row3Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row3Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row3Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow3FullCmd = curlKerberizedWebHBaseCmd_L + insertRow3Cmd_M + insertRow3Cmd_R;
			//System.out.println(" *-* insertRow3FullCmd: \n	" + insertRow3FullCmd);
			
			
			//(3)c Create WebHBase commands for multiple-row insertion for the 4th & 5th row, and 6th row (rowkey == 104 & 105 or 106) of test data 
			// using application/json and  text/xml methods respectively (required base64 encoding)
			
			//(3)c.1
			String row4TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  4);
			String row5TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  5);
			//System.out.println(" *** row4TestData: \n	" + row4TestData);
			//System.out.println(" *** row5TestData: \n	" + row5TestData);
			
			String [] row4Split = row4TestData.split(",");
			String row4Key = row4Split[0].trim();
			String row4FirstName = row4Split[1].trim();
			String row4LastName = row4Split[2].trim();
			String row4Salary = row4Split[3].trim();
			String row4Gender = row4Split[4].trim();
			String row4Address = row4Split[5].trim();
			//System.out.println(" *-* row4TestData: \n	" + row4Key + "," + row4FirstName + ","
			//				+ row4LastName + "," + row4Salary + "," + row4Gender + "," + row4Address);
			
			String [] row5Split = row5TestData.split(",");
			String row5Key = row5Split[0].trim();
			String row5FirstName = row5Split[1].trim();
			String row5LastName = row5Split[2].trim();
			String row5Salary = row5Split[3].trim();
			String row5Gender = row5Split[4].trim();
			String row5Address = row5Split[5].trim();
			//System.out.println(" *-* row5TestData: \n	" + row5Key + "," + row5FirstName + ","
			//				+ row5LastName + "," + row5Salary + "," + row5Gender + "," + row5Address);
			
			String insertRows4N5Cmd_M =   " --location-trusted -X  POST -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRows4N5Cmd_R = " -d '{\"Row\":["
									+ "{\"key\":\"" + row4Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row4FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row4LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row4Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row4Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row4Address + "\"}"
									+ " ]},"
									+ "{\"key\":\"" + row5Key + "\",\"Cell\":["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row5FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row5LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row5Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row5Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row5Address + "\"}"
									+ " ]} ]}'";
			String insertRows4N5FullCmd = curlKerberizedWebHBaseCmd_L + insertRows4N5Cmd_M + insertRows4N5Cmd_R;
			//System.out.println(" *-* insertRows4N5FullCmd: \n	" + insertRows4N5FullCmd);
					
					
			//(3)c.2
			String row6TestData = getTargetLineDataLineOfAFile (encodedLocalWebHBaseTestDataFilePathAndName,  6);
			System.out.println(" *** row6TestData: \n	" + row6TestData);
			
			String [] row6Split = row6TestData.split(",");
			String row6Key = row6Split[0].trim();
			String row6FirstName = row6Split[1].trim();
			String row6LastName = row6Split[2].trim();
			String row6Salary = row6Split[3].trim();
			String row6Gender = row6Split[4].trim();
			String row6Address = row6Split[5].trim();
			//System.out.println(" *-* row6TestData: \n	" + row6Key + "," + row6FirstName + ","
			//				+ row6LastName + "," + row6Salary + "," + row6Gender + "," + row6Address);
			
			String insertRow6Cmd_M =  " --location-trusted -X  POST -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/false-row-key";
			
			String insertRow6Cmd_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row6Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row6FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row6LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row6Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row6Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row6Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow6FullCmd = curlKerberizedWebHBaseCmd_L + insertRow6Cmd_M + insertRow6Cmd_R;
			System.out.println(" *-* insertRow6FullCmd: \n	" + insertRow6FullCmd);
			
			
			//(3)d Create WebHBase commands for querying HBase Data 
			//Note: curlKerberizedWebHBaseCmd_L == "curl -s --negotiate -u : "; 
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			//curl -s --negotiate -u : --location-trusted -X  GET -H "Accept: application/octet-stream" -L http://hdpr05mn01.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}'
			//curl -s --negotiate -u : --location-trusted -X  GET -H "Accept: application/octet-stream" -L http://hdpr05mn01.mayo.edu:8084/employee_webhbase1/102/cfs:lastName | awk '{print $0""}'

			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result_f5balancer.txt", "_result_f5balancer_temp.txt");
			String queryHBaseTableCellCmd_M =  " --location-trusted -X  GET -H \"Accept: application/octet-stream\" -L " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/101/cfs:firstName | awk '{print $0\"\"}' > " + tempLocalWebHBaseTestResultPathAndName;
					
			String getRow2LastNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M		
					+ "/102/cfs:lastName | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow3SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/103/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow4SalaryCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/104/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow5GenderCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/105/cfs:gender | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow6AddressCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/106/cfs:address | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd = "rm -f " + localWebHBaseTestResultPathAndName;
			
			//(4)
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd + ";\n");
			sb.append(createNewHBaseTableFullCmd + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd + ";\n");		
			sb.append(insertRow1LasttNameCellFullCmd + ";\n");
			sb.append(insertRow1SalaryCellFullCmd + ";\n");
			sb.append(insertRow1GenderCellFullCmd + ";\n");
			sb.append(insertRow1AddressCellFullCmd + ";\n");		
			sb.append(insertRow2FullCmd + ";\n");
			sb.append(insertRow3FullCmd + ";\n");
			sb.append(insertRows4N5FullCmd + ";\n");
			sb.append(insertRow6FullCmd + ";\n");
		
			sb.append(getRow1FirstNameCellFullCmd + ";\n");
			sb.append(getRow2LastNameCellFullCmd + ";\n");
			sb.append(getRow3SalaryCellFullCmd + ";\n");		
			sb.append(getRow4SalaryCellFullCmd + ";\n");
			sb.append(getRow5GenderCellFullCmd + ";\n");
			sb.append(getRow6AddressCellFullCmd + ";\n");		
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd + ";\n"); 
			
			//sb.append("hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + webHBaseTestResultHdfsPathAndName + "; \n");
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//Up to 5/4/2016, BDsdbx Shared Knox01 with BDDev1 that was managed by BDDev1 Ambari
			//if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
			//		||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
			//		|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//	String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
			//	System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
			//	copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//}
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHBaseScriptFile_Curl_F5Balancer" + (i+1) + ".sh";			
			prepareFile (knoxWebHBaseScriptFullFilePathAndName,  "Script File For Testing WebHBase on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(5)
			boolean currTestScenarioSuccessStatus5 = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("Joe,Smith,55000,120000,M,Virginia")) {
						currTestScenarioSuccessStatus5 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
									
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
						+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHBase System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
	        			+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHBase System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }			
			
			System.out.println("\n*** currTestScenarioSuccessStatus5: " + currTestScenarioSuccessStatus5 );
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 6.3
		

		//6.4 Login to  EN01 and Perform Large Data Writing/Reading to WebHBase table 
		//    via cURL cmds and F5 Balancer HTTPs URL
		
		String fileName = localKnoxLargeWebHbaseJsonDataFileName;
		String[] fileNameSplit = fileName.split("_");
		int fileNameSplitLength = fileNameSplit.length;
		String fileSizeInKB = fileNameSplit[fileNameSplitLength-1].replace(".txt", "").toLowerCase().replace("kb", "");
		System.out.println(" *** fileSizeInKB: " + fileSizeInKB);
		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[2.4]. WebHBase Large-Size (" + fileSizeInKB + " KB) Data Insert Per Cell via F5 Balancer Service \n", true);
		//int f5BalancerNumber_Start = 0;
		//int clusterF5BalancerNNNumber = 1;
		f5BalancerNumber_Start = 0;
		clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			
			//(1) Get F5 Balancer HTTPs URL:		
			//String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			String clusterF5HttpsURL = currClusterF5ConnStr + "/hbase/";
			 
					
			//(2) Generate WebHbase cmds:
			String enServerWebHBaseLargeTestJsonDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseJsonDataFileName;
			String enServerWebHBaseLargeTestXmlDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseXmlDataFileName;
			
			//Sample: curl -i --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/exists";;
			//String curlKerberizedWebHBaseCmd_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			String curlKerberizedWebHBaseCmd_L = "curl -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted ";
			
			String webHBaseTableName = "largeInsert_webhbase_f5balancer" + (i+1);	
			String webHBaseTableURL = clusterF5HttpsURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
					
			//String webHBaseTestResultFileName = "employee_webhbase_curl_result.txt";
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_largeinsert_result_f5balancer.txt";
					
			
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd_R = "  -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + deleteExistingHBaseTableCmd_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd: \n" + deleteExistingHBaseTableFullCmd);
			
			
			String createNewHBaseTableCmd_R = "  -X  POST -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd = curlKerberizedWebHBaseCmd_L + createNewHBaseTableCmd_R;
			//System.out.println(" *** createNewHBaseTableFullCmd: \n" + createNewHBaseTableFullCmd);
			
			//(2)a Create WebHBase commands for single cell insertion, querying and deletion for the first row (rowkey == 101)/JSON
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_largeinsert_result_f5balancer.txt", "_largeinsert_result_f5balancer_tmp.txt");
			String insertRow1CellCmd_M =  "  -X  POST -H \"Content-Type: application/json\""
					+ " -L " + webHBaseTableURL + "/101";
			
			String insertRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellCmd_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestJsonDataFilePathAndName;
						
			String queryHBaseTableCellCmd_M =  "  -X  GET -H \"Accept: application/octet-stream\" -L " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 > " + tempLocalWebHBaseTestResultPathAndName;
			
			//curl -s --negotiate -u : -X DELETE http://hdpr03mn02.mayo.edu:8084/largeInsert_webhbase1/101/cfs:firstName 		
			String deleteRow1FirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + "  -X DELETE " + webHBaseTableURL + "/101/cfs:firstName";	
						
			
			//(2)b Create WebHBase commands for single cell insertion and querying for the first row (rowkey == 101)/XML		 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			
			String insertRow1CellSecondCmd_M =  "  -X  POST -H \"Accept: text/xml\"  -H \"Content-Type: text/xml\""
					+ " -L " + webHBaseTableURL + "/101";			
			
			String insertRow1FirstNameCellSecondFullCmd = curlKerberizedWebHBaseCmd_L 
					+ insertRow1CellSecondCmd_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestXmlDataFilePathAndName;
			
			String getRow1SecondFirstNameCellFullCmd = curlKerberizedWebHBaseCmd_L  + queryHBaseTableCellCmd_M		
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 >> " + tempLocalWebHBaseTestResultPathAndName;
				
			//(2)c Actions of post WebHbase Ops 
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd = "rm -f " + localWebHBaseTestResultPathAndName;
			
			
			
			//(3)
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd + ";\n");
			sb.append(createNewHBaseTableFullCmd + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd + ";\n");	
			sb.append(getRow1FirstNameCellFullCmd + ";\n");
			
			sb.append(deleteRow1FirstNameCellFullCmd + ";\n");
			
			sb.append(insertRow1FirstNameCellSecondFullCmd + ";\n");			
			sb.append(getRow1SecondFirstNameCellFullCmd + ";\n");
					
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd + ";\n"); 
			
			//sb.append("hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + webHBaseTestResultHdfsPathAndName + "; \n");
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//Up to 5/4/2016, BDsdbx Shared Knox01 with BDDev1 that was managed by BDDev1 Ambari
			//if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
			//		||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
			//		|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//	String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
			//	System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
			//	copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localWebHBaseTestResultPathAndName + " " + activeNN_addr_port + webHBaseTestResultHdfsPathAndName;
			//}
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHBaseLargeDataCellInsertScriptFile_Curl_F5Balancer" + (i+1) + ".sh";			
			prepareFile (knoxWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(4) Validate result data
			boolean currTestScenarioSuccessStatus = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("1441978062896,1441978062896")) {
						currTestScenarioSuccessStatus = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
						+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
	        			+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);				
			prevTime = currTime;			
		}//end 6.4
		



		//#7.1 & #7.2 & #7.3 are test scenario types for WebHCat testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[3]. WebHCat (Hive Rest Service) \n", true);		
		
		//7.1 Login to EN01 and Perform Data Writing/Reading to Hive Table  via WebHCat 
		//    via cURL cmds and active WebHCat HTTP URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[3.1]. WebHCat - Templeton Rest Service \n", true);
		int webhcatSvrNumber_Start = 0;
		int clusterWebhcatSvrNumber = 2; //1...2
		for (int i = webhcatSvrNumber_Start; i < clusterWebhcatSvrNumber; i++){ 
			totalTestScenarioNumber++;
			
			//(1) Get WebHCat HTTP URL (& active WebHDFS HTTP URL - for WebHDFS to check testing result):
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			
			//http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			String activeWebHcatHttpURL = "";
			if (i==0){
				activeWebHcatHttpURL = currBdCluster.getBdHdfs1stNnIPAddressAndPort().replace(":8020", ":50111").replace("hdfs", "http");
			}
			if (i==1){
				activeWebHcatHttpURL = currBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace(":8020", ":50111").replace("hdfs", "http");
			}
			//if (i==2){
			//	activeWebHcatHttpURL = currBdCluster.getBdHdfs3rdNnIPAddressAndPort().replace(":8020", ":50111").replace("hdfs", "http");
			//}
			
			if (!activeWebHcatHttpURL.endsWith("/")){
				activeWebHcatHttpURL += "/";
			}					
			activeWebHcatHttpURL += "templeton/v1/";
			
			//(2) Generate WebHCat cmds:
			//Sample: curl -i -v --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			//curl -i -k --negotiate -u : --location-trusted -X GET -L http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			// ...http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive
			String curlKerberizedWebHCatCmd_L = "curl -k --negotiate -u : --location-trusted ";
			String webHcatTableName = "employee_webhcat_templeton" +(i+1);
			String hdfsWebHcatTestDataFilePathAndName = knoxTestFolderName + webHcatTableName + "_TableData.txt";
			
			String webHcatTestResultFileName = webHcatTableName + "_curl_result.txt";
			String localWebHCatTestResultPathAndName = enServerScriptFileDirectory + webHcatTestResultFileName;
			String hdfsWebHcatTestResultFileName = knoxTestFolderName + webHcatTestResultFileName;
			
			//curl -k --negotiate -u : --location-trusted -X DELETE -L http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			String deleteExistingHcatTableCmd_R = " -X DELETE -L " + activeWebHcatHttpURL + "ddl/database/default/table/" + webHcatTableName;
			String deleteExistingHcatTableFullCmd = curlKerberizedWebHCatCmd_L + deleteExistingHcatTableCmd_R;
					
			//curl -i -k --negotiate -u : --location-trusted -X PUT -HContent-type:application/json -d  \
			//'{ "comment":"Knox-Gated Hive JDBC Testing Hive Table", "columns": [{ "name": "employeeid", "type": "int" },{ "name": "firstName", "type": "string" }, { "name": "lastName", "type": "string" }, { "name": "salary", "type": "int" }, { "name": "gender", "type": "string" }, { "name": "state", "type": "string" }], "format": { "storedAs": "textfile", "rowFormat": {"fieldsTerminatedBy": "," }} }' \
			//-L  http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat && sleep 15;
			
			String createWebHcatTableCmd_R = " -X PUT -HContent-type:application/json -d  \\\n"
					+ "'{ \"comment\":\"Hive-WebHCat Testing Hive Table\", \"columns\": [{ \"name\": \"employeeid\", \"type\": \"int\" },"
					+ "{ \"name\": \"firstName\", \"type\": \"string\" }, { \"name\": \"lastName\", \"type\": \"string\" }, "
					+ "{ \"name\": \"salary\", \"type\": \"int\" }, { \"name\": \"gender\", \"type\": \"string\" },"
					+ " { \"name\": \"state\", \"type\": \"string\" }], \"format\": { \"storedAs\": \"textfile\", \"rowFormat\": {\"fieldsTerminatedBy\": \",\" }} }' \\\n"
					+ " -L " + activeWebHcatHttpURL + "ddl/database/default/table/" + webHcatTableName + " && sleep 15";
			String createWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + createWebHcatTableCmd_R;
					
			String webhcatTableCreationOutputDir = knoxTestFolderName + webHcatTableName + "_creation.output";
			//curl -i -k --negotiate -u : --location-trusted -X POST -d execute="load data inpath '/user/wa00336/test/Knox/employee_knox_webhcat.txt' overwrite into table default.employee_webhcat;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_creation.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 60;
			String loadDataIntoWebHcatTableCmd_R = " -X POST -d execute=\"load data inpath '" + hdfsWebHcatTestDataFilePathAndName + "' overwrite into table default." + webHcatTableName + ";\" \\\n"
					+ "-d statusdir=\"" + webhcatTableCreationOutputDir + "\" \\\n"
					+ " -L " + activeWebHcatHttpURL + "hive && sleep 60";	 //sleep 60				
			String loadDataIntoWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + loadDataIntoWebHcatTableCmd_R;
			   
			
			String webhcatTableQueryOutputDir = knoxTestFolderName + webHcatTableName + "_query.output";
			//curl -i -k --negotiate -u : --location-trusted -X POST -d execute="select * from default.employee_webhcat where employeeid%3D104;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_query.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 45;
			//curl -k -u wa00336:bnhgui89 --location-trusted  -X GET -L $hadoopClusterF5ConnStr/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_query.output/stdout?op=OPEN > $localWebHCatTestResultPathAndName;
			String queryWebHcatTableCmd_R = " -X POST -d execute=\"select * from default." + webHcatTableName + " where employeeid%3D104;\" \\\n"
					+ "-d statusdir=\"" + webhcatTableQueryOutputDir + "\" \\\n"
					+ " -L " + activeWebHcatHttpURL + "hive && sleep 60";		//sleep 40...45..50..60			
			String queryWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + queryWebHcatTableCmd_R;
			
			String queryResultWebHDFSFilePathAndName = webhcatTableQueryOutputDir + "/stdout";
			String getQueryResultCmd_R = " -X GET -L " + activeWebHdfsHttpURL + "/v1" + queryResultWebHDFSFilePathAndName + "?op=OPEN > " + localWebHCatTestResultPathAndName;
			String getQueryResultFullCmd = curlKerberizedWebHCatCmd_L + getQueryResultCmd_R;			
			String getQueryResultFullCmd_iso = "hdfs dfs -cat " + queryResultWebHDFSFilePathAndName + " > " + localWebHCatTestResultPathAndName;
			
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_webhcat.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestDataFilePathAndName + "; \n");	
				    	    
		    sb.append(deleteExistingHcatTableFullCmd + ";\n");
		    sb.append(createWebHcatTableFullCmd + ";\n");		    
		    sb.append(loadDataIntoWebHcatTableFullCmd + ";\n"); 
		    sb.append(queryWebHcatTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");		   
		    sb.append("rm -f " + localWebHCatTestResultPathAndName + ";\n");
		    sb.append(getQueryResultFullCmd_iso + ";\n");		    
		      
		    
		    sb.append("hadoop fs -copyFromLocal " + localWebHCatTestResultPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
		    //sb.append("rm -f " + localWebHCatTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHcatScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHcatScriptFile_Curl_templeton" + (i +1) + ".sh";			
			prepareFile (knoxWebHcatScriptFullFilePathAndName,  "Script File For Testing Knox WebHCat on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHcatTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHcatScriptFullFilePathAndName, webHcatTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHcatScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHcatScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsWebHcatTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsWebHcatTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//                 104     Brian   Williams        120000  M       NewYork
					if (line.contains("104	Brian	Williams	120000	M	NewYork")) {//Not 104,Brian,Williams,120000,M,NewYork
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				
				
	        }//end outer if	
			
			System.out.println("*** hdfsWebHcatTestResultFileName is: " + hdfsWebHcatTestResultFileName);
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHCat via Templeton Rest Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via WebHCat or Templeton HTTP URL - " + activeWebHcatHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHCat via Templeton Rest Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via WebHCat or Templeton HTTP URL - " + activeWebHcatHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 7.1
		
		
		
		//7.2 Login to EN01 and Perform Data Writing/Reading to Hive Table  via WebHCat 
		//    via cURL cmds and Knox HTTPS URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[3.2]. WebHCat via Knox Gateway Services \n", true);
				
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1;
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing WebHCat Through Knox Node: " + tempKnENName);
			
			String currKnoxNodeName = "";
			String currKnoxClusterName = "";
			if (tempKnENName.contains("_")){
				String[] tempKnENNameSplit  = tempKnENName.split("_");
				currKnoxNodeName = tempKnENNameSplit[0];
				String simpleKnoxClusterName = tempKnENNameSplit[1];
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
					currKnoxClusterName = "BDDev3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
					currKnoxClusterName = "BDDev1";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
					currKnoxClusterName = "BDTest3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
					currKnoxClusterName = "BDTest2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
					currKnoxClusterName = "BDProd3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
					currKnoxClusterName = "BDProd2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
					currKnoxClusterName = "BDSdbx";
				}				
			} else {
				currKnoxNodeName = tempKnENName;
				currKnoxClusterName = bdClusterName;
			}
			
			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
			
			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
			
			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
						
			//(1) Get Current Hadoop Cluster's Knox HTTPS URL (& active WebHDFS HTTP URL - for WebHDFS to check testing result):		
			String currClusterKnoxWebHCatHttpsURL = "https://" + currKnoxNodeFQDN + ":8442/gateway/" + bdKnoxClusterIdName + "/templeton/v1/";
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			
			//(2) Generate WebHCat cmds:
			//Sample: curl -i -k -u wa00336:xxxxx --location-trusted -X GET -L http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			// ...http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive
			String curlKerberizedWebHCatCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";					
			String webHcatTableName = "employee_webhcat_knox" +(i+1);
			String hdfsWebHcatTestDataFilePathAndName = knoxTestFolderName + webHcatTableName + "_TableData.txt";

			String webHcatTestResultFileName = webHcatTableName + "_curl_result.txt";
			String localWebHCatTestResultPathAndName = enServerScriptFileDirectory + webHcatTestResultFileName;
			String hdfsWebHcatTestResultFileName = knoxTestFolderName + webHcatTestResultFileName;
			
			//curl -k -u wa00336:xxxxx --location-trusted -X DELETE -L http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			String deleteExistingHcatTableCmd_R = " -X DELETE -L " + currClusterKnoxWebHCatHttpsURL + "ddl/database/default/table/" + webHcatTableName;
			String deleteExistingHcatTableFullCmd = curlKerberizedWebHCatCmd_L + deleteExistingHcatTableCmd_R;

			//curl -i -k -u wa00336:xxxxx --location-trusted -X PUT -HContent-type:application/json -d  \
			//'{ "comment":"Knox-Gated Hive JDBC Testing Hive Table", "columns": [{ "name": "employeeid", "type": "int" },{ "name": "firstName", "type": "string" }, { "name": "lastName", "type": "string" }, { "name": "salary", "type": "int" }, { "name": "gender", "type": "string" }, { "name": "state", "type": "string" }], "format": { "storedAs": "textfile", "rowFormat": {"fieldsTerminatedBy": "," }} }' \
			//-L  http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat && sleep 15;

			String createWebHcatTableCmd_R = " -X PUT -HContent-type:application/json -d  \\\n"
					+ "'{ \"comment\":\"Hive-WebHCat Testing Hive Table\", \"columns\": [{ \"name\": \"employeeid\", \"type\": \"int\" },"
					+ "{ \"name\": \"firstName\", \"type\": \"string\" }, { \"name\": \"lastName\", \"type\": \"string\" }, "
					+ "{ \"name\": \"salary\", \"type\": \"int\" }, { \"name\": \"gender\", \"type\": \"string\" },"
					+ " { \"name\": \"state\", \"type\": \"string\" }], \"format\": { \"storedAs\": \"textfile\", \"rowFormat\": {\"fieldsTerminatedBy\": \",\" }} }' \\\n"
					+ " -L " + currClusterKnoxWebHCatHttpsURL + "ddl/database/default/table/" + webHcatTableName + " && sleep 15";
			String createWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + createWebHcatTableCmd_R;

			String webhcatTableCreationOutputDir = knoxTestFolderName + webHcatTableName + "_creation.output";
			//curl -i -k -u wa00336:xxxxx --location-trusted -X POST -d execute="load data inpath '/user/wa00336/test/Knox/employee_knox_webhcat.txt' overwrite into table default.employee_webhcat;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_creation.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 60;
			String loadDataIntoWebHcatTableCmd_R = " -X POST -d execute=\"load data inpath '" + hdfsWebHcatTestDataFilePathAndName + "' overwrite into table default." + webHcatTableName + ";\" \\\n"
					+ "-d statusdir=\"" + webhcatTableCreationOutputDir + "\" \\\n"
					+ " -L " + currClusterKnoxWebHCatHttpsURL + "hive && sleep 60";	 //sleep 60				
			String loadDataIntoWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + loadDataIntoWebHcatTableCmd_R;


			String webhcatTableQueryOutputDir = knoxTestFolderName + webHcatTableName + "_query.output";
			//curl -i -k -u wa00336:xxxxx --location-trusted -X POST -d execute="select * from default.employee_webhcat where employeeid%3D104;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_query.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 45;
			//curl -k -u wa00336:bnhgui89 --location-trusted  -X GET -L $hadoopClusterF5ConnStr/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_query.output/stdout?op=OPEN > $localWebHCatTestResultPathAndName;
			String queryWebHcatTableCmd_R = " -X POST -d execute=\"select * from default." + webHcatTableName + " where employeeid%3D104;\" \\\n"
					+ "-d statusdir=\"" + webhcatTableQueryOutputDir + "\" \\\n"
					+ " -L " + currClusterKnoxWebHCatHttpsURL + "hive && sleep 60";		//sleep 40..45..50..60		
			String queryWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + queryWebHcatTableCmd_R;

			String queryResultWebHDFSFilePathAndName = webhcatTableQueryOutputDir + "/stdout";
			//curl -i -k --negotiate -u :  --location-trusted -X GET -L http://hdpr05mn01.mayo.edu:50070/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_knox1_query.output/stdout?op=OPEN 
			//curl -k -u wa00336:bnhgui89  --location-trusted -X GET -L https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPDEV3/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_knox1_query.output/stdout?op=OPEN 
			//String getQueryResultCmd_R = " -X GET -L " + activeWebHdfsHttpURL + "/v1" + queryResultWebHDFSFilePathAndName + "?op=OPEN > " + localWebHCatTestResultPathAndName;
			//String getQueryResultFullCmd = curlKerberizedWebHCatCmd_L.replace(" -u " + loginUserName + ":" + loginUserADPassWd + " ", " --negotiate -u : ") + getQueryResultCmd_R;			
			String getQueryResultCmd_R = " -X GET -L " + currClusterKnoxWebHCatHttpsURL.replace("/templeton/v1/", "/webhdfs/v1/") + queryResultWebHDFSFilePathAndName + "?op=OPEN > " + localWebHCatTestResultPathAndName;
			String getQueryResultFullCmd = curlKerberizedWebHCatCmd_L.replace(" -i -k ", " -k ") + getQueryResultCmd_R;			
			String getQueryResultFullCmd_iso = "hdfs dfs -cat " + queryResultWebHDFSFilePathAndName + " > " + localWebHCatTestResultPathAndName;
			
			
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	

			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos

			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_webhcat.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
			
			sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestDataFilePathAndName + "; \n");	

		    sb.append(deleteExistingHcatTableFullCmd + ";\n");
		    sb.append(createWebHcatTableFullCmd + ";\n");		    
		    sb.append(loadDataIntoWebHcatTableFullCmd + ";\n"); 
		    sb.append(queryWebHcatTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");		   
		    sb.append("rm -f " + localWebHCatTestResultPathAndName + ";\n");
		    sb.append(getQueryResultFullCmd_iso + ";\n");


		    sb.append("hadoop fs -copyFromLocal " + localWebHCatTestResultPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
		    //sb.append("rm -f " + localWebHCatTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
			
		    
		    String knoxWebHcatScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHcatScriptFile_Curl_knox" + (i +1) + ".sh";			
			prepareFile (knoxWebHcatScriptFullFilePathAndName,  "Script File For Testing Knox WebHCat on '" + bdClusterName + "' Entry Node - " + curlExeNode);

			String webHcatTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHcatScriptFullFilePathAndName, webHcatTestingCmds, false);		
			sb.setLength(0);

			//Desktop.getDesktop().open(new File(knoxWebHcatScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHcatScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);

			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsWebHcatTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsWebHcatTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//                 104     Brian   Williams        120000  M       NewYork
					if (line.contains("104	Brian	Williams	120000	M	NewYork")) {//Not 104,Brian,Williams,120000,M,NewYork
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				

			}//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);		
						
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHCat via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebHCatHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHCat via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebHCatHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;					
		}//end 7.2		
					
				
		
		//7.3 Login to EN01 and Perform Data Writing/Reading to Hive Table  via WebHCat 
		//    via cURL cmds and Cluster F5 Balancer URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[3.3]. WebHCat via F5 Balancer Service \n", true);
		//int f5BalancerNumber_Start = 0;
		//int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			
			//(1) Get F5 Balancer HTTPs URL (& active WebHDFS HTTP URL - for WebHDFS to check testing result):		
			//String activeWebHcatHttpURL = currBdCluster.getActiveWebHcatHttpAddress();
			String clusterF5HttpsURL = "";
			if (currClusterF5ConnStr.endsWith("/")) {
				clusterF5HttpsURL = currClusterF5ConnStr + "templeton/v1/";
			} else {
				clusterF5HttpsURL = currClusterF5ConnStr + "/templeton/v1/";
			}
			
			System.out.println(" *** clusterF5HttpsURL for WebHCat: " + clusterF5HttpsURL);
			
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			
			
			//(2) Generate WebHCat cmds:		
			//For WebHDFS: curl -i -k -u wa00336:bnhgui89 -X GET -L https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1/webhdfs/v1/user/m041785/test/Solr/solr_curl_query_result1.txt?op=LISTSTATUS
			//Sample: curl -i -k -u wa00336:xxxxx --location-trusted -X GET -L https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1/templeton/v1/ddl/database/default/table/employee_webhcat;
			// ...https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1/templeton/v1/hive						
			String curlKerberizedWebHCatCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";					
			String webHcatTableName = "employee_webhcat_f5balancer" +(i+1);
			String hdfsWebHcatTestDataFilePathAndName = knoxTestFolderName + webHcatTableName + "_TableData.txt";

			String webHcatTestResultFileName = webHcatTableName + "_curl_result.txt";
			String localWebHCatTestResultPathAndName = enServerScriptFileDirectory + webHcatTestResultFileName;
			String hdfsWebHcatTestResultFileName = knoxTestFolderName + webHcatTestResultFileName;
			
			
					
			//curl -k -u wa00336:xxxxx --location-trusted -X DELETE -L http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat;
			String deleteExistingHcatTableCmd_R = " -X DELETE -L " + clusterF5HttpsURL + "ddl/database/default/table/" + webHcatTableName;
			String deleteExistingHcatTableFullCmd = curlKerberizedWebHCatCmd_L + deleteExistingHcatTableCmd_R;

			//curl -i -k -u wa00336:xxxxx --location-trusted -X PUT -HContent-type:application/json -d  \
			//'{ "comment":"Knox-Gated Hive JDBC Testing Hive Table", "columns": [{ "name": "employeeid", "type": "int" },{ "name": "firstName", "type": "string" }, { "name": "lastName", "type": "string" }, { "name": "salary", "type": "int" }, { "name": "gender", "type": "string" }, { "name": "state", "type": "string" }], "format": { "storedAs": "textfile", "rowFormat": {"fieldsTerminatedBy": "," }} }' \
			//-L  http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_webhcat && sleep 15;

			String createWebHcatTableCmd_R = " -X PUT -HContent-type:application/json -d  \\\n"
					+ "'{ \"comment\":\"Hive-WebHCat Testing Hive Table\", \"columns\": [{ \"name\": \"employeeid\", \"type\": \"int\" },"
					+ "{ \"name\": \"firstName\", \"type\": \"string\" }, { \"name\": \"lastName\", \"type\": \"string\" }, "
					+ "{ \"name\": \"salary\", \"type\": \"int\" }, { \"name\": \"gender\", \"type\": \"string\" },"
					+ " { \"name\": \"state\", \"type\": \"string\" }], \"format\": { \"storedAs\": \"textfile\", \"rowFormat\": {\"fieldsTerminatedBy\": \",\" }} }' \\\n"
					+ " -L " + clusterF5HttpsURL + "ddl/database/default/table/" + webHcatTableName + " && sleep 15";
			String createWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + createWebHcatTableCmd_R;

			String webhcatTableCreationOutputDir = knoxTestFolderName + webHcatTableName + "_creation.output";
			//curl -i -k -u wa00336:xxxxx --location-trusted -X POST -d execute="load data inpath '/user/wa00336/test/Knox/employee_knox_webhcat.txt' overwrite into table default.employee_webhcat;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_creation.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 60;
			String loadDataIntoWebHcatTableCmd_R = " -X POST -d execute=\"load data inpath '" + hdfsWebHcatTestDataFilePathAndName + "' overwrite into table default." + webHcatTableName + ";\" \\\n"
					+ "-d statusdir=\"" + webhcatTableCreationOutputDir + "\" \\\n"
					+ " -L " + clusterF5HttpsURL + "hive && sleep 60";	 //sleep 60	...50			
			String loadDataIntoWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + loadDataIntoWebHcatTableCmd_R;


			String webhcatTableQueryOutputDir = knoxTestFolderName + webHcatTableName + "_query.output";
			//curl -i -k -u wa00336:xxxxx --location-trusted -X POST -d execute="select * from default.employee_webhcat where employeeid%3D104;" \
			//-d statusdir="/user/wa00336/test/Knox/employee_webhcat_query.output" \
			//-L http://hdpr05mn01.mayo.edu:50111/templeton/v1/hive && sleep 45;
			//curl -k -u wa00336:bnhgui89 --location-trusted  -X GET -L $hadoopClusterF5ConnStr/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_query.output/stdout?op=OPEN > $localWebHCatTestResultPathAndName;
			String queryWebHcatTableCmd_R = " -X POST -d execute=\"select * from default." + webHcatTableName + " where employeeid%3D104;\" \\\n"
					+ "-d statusdir=\"" + webhcatTableQueryOutputDir + "\" \\\n"
					+ " -L " + clusterF5HttpsURL + "hive && sleep 60";		//sleep 40..45..50..60	
			String queryWebHcatTableFullCmd = curlKerberizedWebHCatCmd_L + queryWebHcatTableCmd_R;

			String queryResultWebHDFSFilePathAndName = webhcatTableQueryOutputDir + "/stdout";
			//curl -i -k --negotiate -u :  --location-trusted -X GET -L http://hdpr05mn01.mayo.edu:50070/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_knox1_query.output/stdout?op=OPEN 
			//curl -k -u wa00336:bnhgui89  --location-trusted -X GET -L https://hdpr05en01.mayo.edu:8442/gateway/MAYOHADOOPDEV3/webhdfs/v1/user/wa00336/test/Knox/employee_webhcat_knox1_query.output/stdout?op=OPEN 
			//String getQueryResultCmd_R = " -X GET -L " + activeWebHdfsHttpURL + "/v1" + queryResultWebHDFSFilePathAndName + "?op=OPEN > " + localWebHCatTestResultPathAndName;
			//String getQueryResultFullCmd = curlKerberizedWebHCatCmd_L.replace(" -u " + loginUserName + ":" + loginUserADPassWd + " ", " --negotiate -u : ") + getQueryResultCmd_R;			
			String getQueryResultCmd_R = " -X GET -L " + clusterF5HttpsURL.replace("/templeton/v1/", "/webhdfs/v1/") + queryResultWebHDFSFilePathAndName + "?op=OPEN > " + localWebHCatTestResultPathAndName;
			String getQueryResultFullCmd = curlKerberizedWebHCatCmd_L.replace(" -i -k ", " -k ") + getQueryResultCmd_R;			
			String getQueryResultFullCmd_iso = "hdfs dfs -cat " + queryResultWebHDFSFilePathAndName + " > " + localWebHCatTestResultPathAndName;


			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	

			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos

			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_webhcat.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
			sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");			
			sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestDataFilePathAndName + "; \n");	

		    sb.append(deleteExistingHcatTableFullCmd + ";\n");
		    sb.append(createWebHcatTableFullCmd + ";\n");		    
		    sb.append(loadDataIntoWebHcatTableFullCmd + ";\n"); 
		    sb.append(queryWebHcatTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");
		    sb.append("rm -f " + localWebHCatTestResultPathAndName + ";\n");
		    sb.append(getQueryResultFullCmd_iso + ";\n");


		    sb.append("hadoop fs -copyFromLocal " + localWebHCatTestResultPathAndName + " " + activeNN_addr_port + hdfsWebHcatTestResultFileName + "; \n");
		    //sb.append("rm -f " + localWebHCatTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
			
		    
		    String knoxWebHcatScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHcatScriptFile_Curl_F5Balancer" + (i +1) + ".sh";				
			prepareFile (knoxWebHcatScriptFullFilePathAndName,  "Script File For Testing Knox WebHCat on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String webHcatTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHcatScriptFullFilePathAndName, webHcatTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHcatScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHcatScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsWebHcatTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsWebHcatTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//                 104     Brian   Williams        120000  M       NewYork
					if (line.contains("104	Brian	Williams	120000	M	NewYork")) {//Not 104,Brian,Williams,120000,M,NewYork
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				

			}//end outer if	

			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHCat via F5 Balancer Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHCat via F5 Balancer Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHCat System:  '" + hdfsWebHcatTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
			
		}// end 7.3
		
		

//		//#8.1 & #8.2 & #8.3 are test scenario types for HiveJDBC testing
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[4]. HiveJDBC \n", true);		
//		
//		//8.1 Login to EN01 and Perform Data Writing/Reading to Hive Table  via HiveJDBC 
//		//    via cURL cmds and active HiveJDBC HTTP URL	
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[4.1]. HiveJDBC - Beeline via HiveServer2 Services \n", true);
//		int hivejdbcSvrNumber_Start = 0;
//		int clusterHivejdbcSvrNumber = 2; //1...2
//		for (int i = hivejdbcSvrNumber_Start; i < clusterHivejdbcSvrNumber; i++){ 
//			totalTestScenarioNumber++;
//			
//			//(1) Get HiveJDBC URL & Connection String:
//			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
//			
//			//http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_hivejdbc;
//			String hiveServer2JDBCConnStr = "";
//			if (i==0){
//				hiveServer2JDBCConnStr = currBdCluster.getBdHdfs1stNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
//			}
//			if (i==1){
//				hiveServer2JDBCConnStr = currBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
//			}
//			//if (i==2){
//			//	hiveServer2JDBCConnStr = currBdCluster.getBdHdfs3rdNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
//			//}
//			
//			if (!hiveServer2JDBCConnStr.endsWith("/")){
//				hiveServer2JDBCConnStr += "/";
//			}	
//			String hiveKrbSvcPrincipal = currBdCluster.getHiveSvcPrincipalName();
//			hiveServer2JDBCConnStr += ";transportMode=http;httpPath=cliservice;principal=" + hiveKrbSvcPrincipal;
//			//hiveServer2JDBCConnStr += ";transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG";
//			String beelineHiveServer2JDBCConnStr = "beeline -u '" + hiveServer2JDBCConnStr + "'  -e ";
//			System.out.println(" *** Current Hadoop cluster's beelineHiveServer2JDBCConnStr: " + beelineHiveServer2JDBCConnStr);
//			
//			
//			//(2) Generate HiveJDBC cmds:
//			//beeline -u 'jdbc:hive2://hdpr03mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(*) from default.employee1;";
//			//beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "drop table if exists default.employee1;";
//			String jdbcHiveTableName = "default.employee_hivejdbc" +(i+1);
//			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
//			
//			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
//			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
//			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
//			
//			String dropHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
//			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
//					+ "employeeId Int, \n"
//					+ "fistName String, \n"
//					+ "lastName String, \n"
//					+ "salary Int, \n"
//					+ "gender String, \n"
//					+ " address String \n"
//					+ ")Row format delimited fields terminated by ',' \n"
//					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
//						
//			String createHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
//			String loadDataToHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
//			
//			String getQueryResultCmd = beelineHiveServer2JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
//			
//			//sb.append("sudo su - " + loginUserName + ";\n");		
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			// 
//			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
//			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
//			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
//			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
//				    	    
//		    sb.append(dropHiveTableCmd + ";\n");
//		    sb.append(createHiveTableCmd + ";\n");		    
//		    sb.append(loadDataToHiveTableCmd + ";\n"); 
//		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
//		    //sb.append(getQueryResultFullCmd + ";\n");
//		    sb.append(getQueryResultCmd + ";\n");
//		    
//		    
//		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
//		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
//		    sb.append("kdestroy;\n");
//		    
//		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_templeton" + (i +1) + ".sh";			
//			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
//			
//			String hivejdbcTestingCmds = sb.toString();
//			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
//					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
//						
//			boolean currTestScenarioSuccessStatus = false;
//			Path filePath = new Path(hdfsHiveTestResultFileName);
//			if (currHadoopFS.exists(filePath)) {
//				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
//				FileStatus[] status = currHadoopFS.listStatus(filePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				//boolean foundWrittenStr = false;
//				//boolean foundAppendedStr = false;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					//*** line: +------+--+
//					//*** line: | _c0  |
//					//*** line: +------+--+
//					//*** line: | 6    |
//					
//					if (line.contains("6")) {
//						currTestScenarioSuccessStatus = true;
//						break;
//					}													
//				}//end while
//				br.close();				
//				
//	        }//end outer if	
//			
//			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
//			
//			String testRecordInfo = "";
//			if (currTestScenarioSuccessStatus) {
//				successTestScenarioNum++;			
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  -- (1) HiveJDBC - Beeline via HiveServer2 Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String - " + hiveServer2JDBCConnStr
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        } else {
//	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	        			+ "\n  -- (1) HiveJDBC - Beeline via HiveServer2 Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String - " + hiveServer2JDBCConnStr
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        }
//			
//			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
//			prevTime = currTime;			
//		}//end 8.1		
//		
//		
//		//8.2 Login to EN01 or Remotely On This Machine and Perform Data Writing/Reading to Hive Table  via HiveJDBC 
//		//    via Knox Gateway URLs and Cluster F5 Balancer URL(s)		
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[4.2]. HiveJDBC via Knox Gateway Services \n", true);
//		
//		//8.2a - By beeline 
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(4.2a)]. HiveJDBC - By Beeline via Knox Gateway Services \n", true);
//		
//		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
//		//clusterKNNumber_Start = 0; //0..1..2..	
//		//clusterKNNumber = 1; //1..2..4
//		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
//			totalTestScenarioNumber++;
//			
//			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
//			System.out.println("\n--- (" + (i+1) + ") Testing HiveJDBC Through Knox Node: " + tempKnENName);
//			
//			String currKnoxNodeName = "";
//			String currKnoxClusterName = "";
//			if (tempKnENName.contains("_")){
//				String[] tempKnENNameSplit  = tempKnENName.split("_");
//				currKnoxNodeName = tempKnENNameSplit[0];
//				String simpleKnoxClusterName = tempKnENNameSplit[1];
//				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
//					currKnoxClusterName = "BDDev3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
//					currKnoxClusterName = "BDDev1";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
//					currKnoxClusterName = "BDTest3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
//					currKnoxClusterName = "BDTest2";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
//					currKnoxClusterName = "BDProd3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
//					currKnoxClusterName = "BDProd2";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
//					currKnoxClusterName = "BDSdbx";
//				}				
//			} else {
//				currKnoxNodeName = tempKnENName;
//				currKnoxClusterName = bdClusterName;
//			}
//			
//			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
//			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
//			
//			//(1) Get Current Knox Hive JDBC Connection Factory			
//			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
//			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
//			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
//			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
//			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
//			
//			int knoxGateWayPortNum = 8442;
//			System.out.println(" *** knoxGateWayPortNum: " + knoxGateWayPortNum);
//			
//			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
//			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
//				
//			String bdKnoxHiveContextPath = "gateway/" + bdKnoxClusterIdName + "/hive";					
//			String bdKnoxOrF5HiveContextPath = bdKnoxHiveContextPath;
//			
//			
//			String hiveDatabaseName = "default";
//			
//			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
//					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
//			
//			String hiveKnoxOrF5ConnectionURL_ori = aHiveViaKnoxConnFactory.getUrl();
//			String hiveKnoxOrF5ConnectionURL_view = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=.*", "").replaceFirst(";password=.*", "");
//			System.out.println(" *** hiveKnoxOrF5ConnectionURL_view: " + hiveKnoxOrF5ConnectionURL_view);
//			
//			String hiveKnoxOrF5ConnectionURL4Beeline = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=", " ").replaceFirst(";password=", " ");
//			System.out.println(" *** hiveKnoxOrF5ConnectionURL4Beeline: " + hiveKnoxOrF5ConnectionURL4Beeline);
//			//jdbc:hive2://hdpr07en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89			
//			
//			String beelineKnoxOrF5JDBCConnStr = "beeline -u '" + hiveKnoxOrF5ConnectionURL4Beeline + "'  -e ";
//			System.out.println(" *** Current Hadoop cluster's beelineKnoxOrF5JDBCConnStr: " + beelineKnoxOrF5JDBCConnStr);
//
//
//			//(2) Generate HiveJDBC cmds:
//			//## Prod2:
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr02mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr01kx03.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://bigdataknox.mayo.edu/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			//## Dev3:	
//			// beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
//			// beeline -u 'jdbc:hive2://hdpr05en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			// beeline -u 'jdbc:hive2://bigdata.mayo.edu/default;ssl=true;transportMode=http;httpPath=hdp/DEV3/knox/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//		
//			String jdbcHiveTableName = "default.employee_hivejdbc_beeline_knox" +(i+1);
//			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
//			
//			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
//			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
//			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
//			
//			String dropHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
//			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
//					+ "employeeId Int, \n"
//					+ "fistName String, \n"
//					+ "lastName String, \n"
//					+ "salary Int, \n"
//					+ "gender String, \n"
//					+ " address String \n"
//					+ ")Row format delimited fields terminated by ',' \n"
//					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
//						
//			String createHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
//			String loadDataToHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
//			
//			String getQueryResultCmd = beelineKnoxOrF5JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
//			
//			//sb.append("sudo su - " + loginUserName + ";\n");		
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			// 
//			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
//			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
//			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
//			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
//				    	    
//		    sb.append(dropHiveTableCmd + ";\n");
//		    sb.append(createHiveTableCmd + ";\n");		    
//		    sb.append(loadDataToHiveTableCmd + ";\n"); 
//		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
//		    //sb.append(getQueryResultFullCmd + ";\n");
//		    sb.append(getQueryResultCmd + ";\n");
//		    
//		    
//		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
//		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
//		    sb.append("kdestroy;\n");
//		    
//		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_HiveJDBC_Beeline" + (i +1) + ".sh";			
//			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
//			
//			String hivejdbcTestingCmds = sb.toString();
//			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
//					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
//						
//			boolean currTestScenarioSuccessStatus = false;
//			Path filePath = new Path(hdfsHiveTestResultFileName);
//			if (currHadoopFS.exists(filePath)) {
//				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
//				FileStatus[] status = currHadoopFS.listStatus(filePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				//boolean foundWrittenStr = false;
//				//boolean foundAppendedStr = false;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					//*** line: +------+--+
//					//*** line: | _c0  |
//					//*** line: +------+--+
//					//*** line: | 6    |
//					
//					if (line.contains("6")) {
//						currTestScenarioSuccessStatus = true;
//						break;
//					}													
//				}//end while
//				br.close();				
//				
//	        }//end outer if	
//			
//			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
//			
//			String testRecordInfo = "";
//			if (currTestScenarioSuccessStatus) {
//				successTestScenarioNum++;			
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  -- (1) HiveJDBC - Beeline via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        } else {
//	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	        			+ "\n  -- (1) HiveJDBC - Beeline via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        }
//			
//			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
//			prevTime = currTime;	
//		}//end 8.2a
//		
//		
//		//8.2b -- - Remote HiveJDBC via Knox Gateway Services
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(4.2b)]. HiveJDBC - Remotely via Knox Gateway Services \n", true);
//		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
//		//clusterKNNumber_Start = 0; //0..1..2..	
//		//clusterKNNumber = 1; //1..2..4
//		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
//			totalTestScenarioNumber++;
//			
//			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
//			System.out.println("\n--- (" + (i+1) + ") Testing HiveJDBC Through Knox Node: " + tempKnENName);
//			
//			String currKnoxNodeName = "";
//			String currKnoxClusterName = "";
//			if (tempKnENName.contains("_")){
//				String[] tempKnENNameSplit  = tempKnENName.split("_");
//				currKnoxNodeName = tempKnENNameSplit[0];
//				String simpleKnoxClusterName = tempKnENNameSplit[1];
//				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
//					currKnoxClusterName = "BDDev3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
//					currKnoxClusterName = "BDDev1";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
//					currKnoxClusterName = "BDTest3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
//					currKnoxClusterName = "BDTest2";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
//					currKnoxClusterName = "BDProd3";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
//					currKnoxClusterName = "BDProd2";
//				}
//				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
//					currKnoxClusterName = "BDSdbx";
//				}				
//			} else {
//				currKnoxNodeName = tempKnENName;
//				currKnoxClusterName = bdClusterName;
//			}
//			
//			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
//			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
//			
//			//(1) Get Current Knox Hive JDBC Connection Factory			
//			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
//			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
//			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
//			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
//			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
//			
//			int knoxGateWayPortNum = 8442;
//			System.out.println(" *** knoxGateWayPortNum: " + knoxGateWayPortNum);
//			
//			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
//			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
//				
//			String bdKnoxHiveContextPath = "gateway/" + bdKnoxClusterIdName + "/hive";					
//			String bdKnoxOrF5HiveContextPath = bdKnoxHiveContextPath;
//			
//			
//			String hiveDatabaseName = "default";
//			
//			
//			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
//					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
//			
//			//(2) Move Hive Testing Data into HDFS
//			final String jdbcHiveTableName = "employee_hivejdbc_knox" +(i+1);
//			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
//			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
//			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
//			//hdfsHiveDefaultTestDataFilePathAndName = hdfsHiveTestDataFilePathAndName;						
//			
//			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
//	  		hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
//	  		
//	  		Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
//	  		if (currHadoopFS.exists(outputPath)) {
//	  			currHadoopFS.delete(outputPath, false);
//	  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
//	          }
//	  		
//	  		String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;	  		
//	  		moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);
//			
//	  		
//			//(3) Hive Table Operations via Hive JDBC through Knox:			
//
//			String hivejdbcTestResultFileName = jdbcHiveTableName + "_knox_curl_result.txt";
//			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
//			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
//				
//			 
//			Thread thread = new Thread(new Runnable() {
//		    public void run() {
//		    	int totalTestCaseNumber = 0;
//			  	double successTestCaseNum = 0L;
//			  			  	
//			  	StringBuilder sb = new StringBuilder();
//		    	
//			  	try {		    		
//		  	  		Connection conn = aHiveViaKnoxConnFactory.getConnection();     	
//		  	  		Statement stmt = conn.createStatement();
//		  	  					
//		  			//1).drop an existing Hive table
//		  			totalTestCaseNumber++;
//		  			String dropTblSqlStr = "drop table " + jdbcHiveTableName;
//		  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
//		  			if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Dropping Hive table - " + jdbcHiveTableName + "\n");			
//		  			} else {
//		  				sb.append("    -*- 'Failed' - Dropping Hive table - " + jdbcHiveTableName + "\n");
//		  			}
//		  			
//		  			//2). create a new Hive-managed table
//		  			totalTestCaseNumber++;
//		  		    String createTblSqlStr = "create table " + jdbcHiveTableName 
//		  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
//		  		      		+ "Row format delimited fields terminated by ',' ";
//		  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
//		  		    if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Creating Hive table - " + jdbcHiveTableName + "\n");	
//		  		    } else {
//		  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + jdbcHiveTableName + "\n");
//		  			}   
//		  		    
//		  		    //3). load the Hive-managed table by overwriting  
//		  		    totalTestCaseNumber++;
//		  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName ;
//		  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
//		  		  	if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
//		  		  	} else {
//		  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
//		  			}    
//		  	      		    	  	
//		  		    //4).HQuery (row-counting) the above-generated Hive-managed table
//		  	  		totalTestCaseNumber++;
//		  		    String queryTblSqlStr = "select count(1) from " + jdbcHiveTableName;  //count(*)...count(1)...*
//		  		    boolean countingQueryStatus = false;
//		  	  		ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
//		  	  		String line = "";
//		  	  		if (rs != null){
//		  	  			while (rs.next()) {
//		  	  	  			line = rs.getString(1);
//		  	  				System.out.println(line);
//		  	  				if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
//		  	  					successTestCaseNum ++;
//		  	  					countingQueryStatus = true;
//		  	  				    tableRowCount = 6;
//		  	  					break;
//		  	  				}
//		  	  	  		}
//		  	  			rs.close();
//		  	  		}  		  		
//		  	  		if (countingQueryStatus == true){
//		  	  			sb.append("    *** Success - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
//		  	  		} else {
//		  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
//		  	  		}
//		  	  		stmt.close();
//		  	  	    conn.close();
//		  	  	    
//		  	  	    	
//		  	  		sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
//		  	  		sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
//		  	  		currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
//		  			
//		  	  	    NumberFormat df = new DecimalFormat("#0.00");
//		  			sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
//		  	  		
//		  			currScenarioDetailedTestingRecordInfo = sb.toString();		  			
//		  			sb.setLength(0);
//		  	  	} catch (SQLException e) {		
//		  			e.printStackTrace();
//		  		} catch (ClassNotFoundException e) {		
//		  			e.printStackTrace();
//		  		}//end try
//		    	}
//			});
//				
//			thread.start();
//			try {
//				thread.join(20000);
//			} catch (InterruptedException e) {			
//				e.printStackTrace();
//			}
//			if (thread.isAlive()) {
//			    //thread.stop();//.stop();
//			    thread.interrupt();
//			}
//			Thread.sleep(1*5*1000);	
//			
//			
//			System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
//			System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
//			
//	  		String testRecordInfo = "";	  		
//	  		if (currScenarioSuccessRate == 1){
//	  			successTestScenarioNum++;
//	  			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//	  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + jdbcHiveTableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
//	  		} else if (currScenarioSuccessRate == 0){
//	  			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//	  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + jdbcHiveTableName + "'\n";	 	 
//	  		} else {
//	  			successTestScenarioNum += currScenarioSuccessRate;
//	  			testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//	  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
//	  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
//	  		}
//	  		sb.setLength(0);
//	  		//writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);			
//			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
//			prevTime = currTime;					
//		}//end 8.2b		
//		//end 8.2			
//				
//		
//		//8.3 Login to EN01 or Remotely On This Machine and Perform Data Writing/Reading to Hive Table via HiveJDBC 
//		//    via Knox Gateway URLs and Cluster F5 Balancer URL(s)	
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[4.3]. HiveJDBC via F5 Balancer(s) \n", true);
//		//int f5BalancerNumber_Start = 0;
//		//int clusterF5BalancerNNNumber = 1;
//		
//		//8.3a - By beeline 
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(4.3a)]. HiveJDBC - By Beeline via F5 Balancer(s) \n", true);
//		//int f5BalancerNumber_Start = 0;
//		//int clusterF5BalancerNNNumber = 1;
//		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
//			totalTestScenarioNumber++;
//			
//			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
//			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
//			//https://bigdata.mayo.edu/hdp/DEV3/knox
//			
//			
//			//(1) Get Current F5 Hive JDBC Connection Factory			
//			currClusterF5ConnStr = currClusterF5ConnStr.replace("https://", "");
//			String currF5FQDN = currClusterF5ConnStr;
//			String bdF5HiveContextPath = "";
//			if (currClusterF5ConnStr.contains("/")){
//				String[] f5ConnStrSplit = currClusterF5ConnStr.split("/");
//				currF5FQDN  = f5ConnStrSplit[0];
//				for (int j = 1; j < f5ConnStrSplit.length; j++ ){
//					int maxIndexNum = f5ConnStrSplit.length - 1;
//					if (j <= maxIndexNum - 1 ){
//						bdF5HiveContextPath +=  f5ConnStrSplit[j] + "/";
//					} else {
//						bdF5HiveContextPath +=  f5ConnStrSplit[j] ;
//					}					
//				}
//			}
//			bdF5HiveContextPath += "/hive";
//			
//			System.out.println(" *** currF5FQDN: " + currF5FQDN);
//			System.out.println(" *** bdF5HiveContextPath: " + bdF5HiveContextPath);
//
//			int f5BalancerPortNum = 443;
//			System.out.println(" *** f5BalancerPortNum: " + f5BalancerPortNum);
//
//			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
//			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);							
//			String bdKnoxOrF5HiveContextPath = bdF5HiveContextPath;
//
//
//			String hiveDatabaseName = "default";
//			
//			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currF5FQDN, f5BalancerPortNum,
//					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
//			
//						
//			String hiveKnoxOrF5ConnectionURL_ori = aHiveViaKnoxConnFactory.getUrl();
//			String hiveKnoxOrF5ConnectionURL_view = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=.*", "").replaceFirst(";password=.*", "");
//			System.out.println(" *** hiveKnoxOrF5ConnectionURL_view: " + hiveKnoxOrF5ConnectionURL_view);
//			
//			String hiveKnoxOrF5ConnectionURL4Beeline = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=", " ").replaceFirst(";password=", " ");
//			System.out.println(" *** hiveKnoxOrF5ConnectionURL4Beeline: " + hiveKnoxOrF5ConnectionURL4Beeline);
//			//jdbc:hive2://hdpr07en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89			
//			
//			String beelineKnoxOrF5JDBCConnStr = "beeline -u '" + hiveKnoxOrF5ConnectionURL4Beeline + "'  -e ";
//			System.out.println(" *** Current Hadoop cluster's beelineKnoxOrF5JDBCConnStr: " + beelineKnoxOrF5JDBCConnStr);
//
//
//			//(2) Generate HiveJDBC cmds:
//			//## Prod2:
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr02mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr01kx03.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://bigdataknox.mayo.edu/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			//## Dev3:	
//			// beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
//			// beeline -u 'jdbc:hive2://hdpr05en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//			// beeline -u 'jdbc:hive2://bigdata.mayo.edu/default;ssl=true;transportMode=http;httpPath=hdp/DEV3/knox/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
//
//			String jdbcHiveTableName = "default.employee_hivejdbc_beeline_f5balancer" +(i+1);
//			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
//			
//			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
//			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
//			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
//			
//			String dropHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
//			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
//					+ "employeeId Int, \n"
//					+ "fistName String, \n"
//					+ "lastName String, \n"
//					+ "salary Int, \n"
//					+ "gender String, \n"
//					+ " address String \n"
//					+ ")Row format delimited fields terminated by ',' \n"
//					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
//						
//			String createHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
//			String loadDataToHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
//			
//			String getQueryResultCmd = beelineKnoxOrF5JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
//			
//			//sb.append("sudo su - " + loginUserName + ";\n");		
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			// 
//			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
//			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
//			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
//			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
//				    	    
//		    sb.append(dropHiveTableCmd + ";\n");
//		    sb.append(createHiveTableCmd + ";\n");		    
//		    sb.append(loadDataToHiveTableCmd + ";\n"); 
//		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
//		    //sb.append(getQueryResultFullCmd + ";\n");
//		    sb.append(getQueryResultCmd + ";\n");
//		    
//		    
//		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
//		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
//		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
//		    sb.append("kdestroy;\n");
//		    
//		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_HiveJDBC_F5Balancer" + (i +1) + ".sh";			
//			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
//			
//			String hivejdbcTestingCmds = sb.toString();
//			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
//					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
//						
//			boolean currTestScenarioSuccessStatus = false;
//			Path filePath = new Path(hdfsHiveTestResultFileName);
//			if (currHadoopFS.exists(filePath)) {
//				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
//				FileStatus[] status = currHadoopFS.listStatus(filePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				//boolean foundWrittenStr = false;
//				//boolean foundAppendedStr = false;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					//*** line: +------+--+
//					//*** line: | _c0  |
//					//*** line: +------+--+
//					//*** line: | 6    |
//					
//					if (line.contains("6")) {
//						currTestScenarioSuccessStatus = true;
//						break;
//					}													
//				}//end while
//				br.close();				
//				
//	        }//end outer if	
//			
//			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
//			
//			String testRecordInfo = "";
//			if (currTestScenarioSuccessStatus) {
//				successTestScenarioNum++;			
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  -- (1) HiveJDBC - Beeline via F5 Balancer  - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        } else {
//	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//	        			+ "\n  -- (1) HiveJDBC - Beeline via F5 Balancer  - Hive Table Deleting, Creating, Data-Loading, and Querying "
//						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
//						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
//	        }
//			
//			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
//			prevTime = currTime;	
//		}//end 8.3a
//		
//		//8.3b - Remote HiveJDBC via F5 Balancer
//		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(4.3b)]. HiveJDBC - Remotely via F5 Balancer(s) \n", true);		
//		//int f5BalancerNumber_Start = 0;
//		//int clusterF5BalancerNNNumber = 1;
//		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
//			totalTestScenarioNumber++;
//			
//			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
//			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
//			//https://bigdata.mayo.edu/hdp/DEV3/knox
//			
//			
//			//(1) Get Current F5 Hive JDBC Connection Factory			
//			currClusterF5ConnStr = currClusterF5ConnStr.replace("https://", "");
//			String currF5FQDN = currClusterF5ConnStr;
//			String bdF5HiveContextPath = "";
//			if (currClusterF5ConnStr.contains("/")){
//				String[] f5ConnStrSplit = currClusterF5ConnStr.split("/");
//				currF5FQDN  = f5ConnStrSplit[0];
//				for (int j = 1; j < f5ConnStrSplit.length; j++ ){
//					int maxIndexNum = f5ConnStrSplit.length - 1;
//					if (j <= maxIndexNum - 1 ){
//						bdF5HiveContextPath +=  f5ConnStrSplit[j] + "/";
//					} else {
//						bdF5HiveContextPath +=  f5ConnStrSplit[j] ;
//					}					
//				}
//			}
//			bdF5HiveContextPath += "/hive";
//			
//			System.out.println(" *** currF5FQDN: " + currF5FQDN);
//			System.out.println(" *** bdF5HiveContextPath: " + bdF5HiveContextPath);
//
//			int f5BalancerPortNum = 443;
//			System.out.println(" *** f5BalancerPortNum: " + f5BalancerPortNum);
//
//			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
//			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);							
//			String bdKnoxOrF5HiveContextPath = bdF5HiveContextPath;
//
//
//			String hiveDatabaseName = "default";
//			
//			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currF5FQDN, f5BalancerPortNum,
//					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
//
//			//(2) Move Hive Testing Data into HDFS
//			final String jdbcHiveTableName = "employee_hivejdbc_f5balancer" +(i+1);
//			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
//			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
//			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
//			//hdfsHiveDefaultTestDataFilePathAndName = hdfsHiveTestDataFilePathAndName;						
//
//			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
//			hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
//
//			Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
//			if (currHadoopFS.exists(outputPath)) {
//				currHadoopFS.delete(outputPath, false);
//				System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
//			}
//
//			String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;	  		
//			moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);
//
//
//			//(3) Hive Table Operations via Hive JDBC through F5:			
//
//			String hivejdbcTestResultFileName = jdbcHiveTableName + "_knox_curl_result.txt";
//			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
//			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
//
//
//			Thread thread = new Thread(new Runnable() {
//			public void run() {
//			int totalTestCaseNumber = 0;
//			double successTestCaseNum = 0L;
//
//			StringBuilder sb = new StringBuilder();
//
//			try {		    		
//				Connection conn = aHiveViaKnoxConnFactory.getConnection();     	
//				Statement stmt = conn.createStatement();
//
//				//1).drop an existing Hive table
//				totalTestCaseNumber++;
//				String dropTblSqlStr = "drop table " + jdbcHiveTableName;
//				int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
//				if (exitValue == 0){
//					successTestCaseNum ++;
//					sb.append("    *** Success - Dropping Hive table - " + jdbcHiveTableName + "\n");			
//				} else {
//					sb.append("    -*- 'Failed' - Dropping Hive table - " + jdbcHiveTableName + "\n");
//				}
//
//				//2). create a new Hive-managed table
//				totalTestCaseNumber++;
//			    String createTblSqlStr = "create table " + jdbcHiveTableName 
//					+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
//					+ "Row format delimited fields terminated by ',' ";
//			    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
//			    if (exitValue == 0){
//					successTestCaseNum ++;
//					sb.append("    *** Success - Creating Hive table - " + jdbcHiveTableName + "\n");	
//			    } else {
//				sb.append("    -*- 'Failed' - Creating Hive table - " + jdbcHiveTableName + "\n");
//				}   
//
//			    //3). load the Hive-managed table by overwriting  
//			    totalTestCaseNumber++;
//				String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName ;
//				runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
//				if (exitValue == 0){
//					successTestCaseNum ++;
//					sb.append("    *** Success - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
//				} else {
//					sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
//				}    
//
//			    //4).HQuery (row-counting) the above-generated Hive-managed table
//				totalTestCaseNumber++;
//			    String queryTblSqlStr = "select count(1) from " + jdbcHiveTableName;  //count(*)...count(1)...*
//			    boolean countingQueryStatus = false;
//				ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
//				String line = "";
//				if (rs != null){
//					while (rs.next()) {
//						line = rs.getString(1);
//						System.out.println(line);
//						if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
//							successTestCaseNum ++;
//							countingQueryStatus = true;
//						    tableRowCount = 6;
//							break;
//						}
//					}
//					rs.close();
//				}  		  		
//				if (countingQueryStatus == true){
//					sb.append("    *** Success - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
//				} else {
//					sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
//				}
//				stmt.close();
//			    conn.close();
//
//
//				sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
//				sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
//				currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
//
//			    NumberFormat df = new DecimalFormat("#0.00");
//				sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
//
//				currScenarioDetailedTestingRecordInfo = sb.toString();		  			
//				sb.setLength(0);
//				} catch (SQLException e) {		
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {		
//					e.printStackTrace();
//				}//end try
//			}
//			});
//
//			thread.start();
//			try {
//				thread.join(20000);
//			} catch (InterruptedException e) {			
//				e.printStackTrace();
//			}
//			if (thread.isAlive()) {
//				//thread.stop();//.stop();
//			    thread.interrupt();
//			}
//			Thread.sleep(1*5*1000);	
//	
//	
//			System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
//			System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
//	
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
//	
//			String testRecordInfo = "";	  		
//			if (currScenarioSuccessRate == 1){
//				successTestScenarioNum++;
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//					+ "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + jdbcHiveTableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
//			} else if (currScenarioSuccessRate == 0){
//				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//					+ "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + jdbcHiveTableName + "'\n";	 	 
//			} else {
//				successTestScenarioNum += currScenarioSuccessRate;
//				testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
//						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
//						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//					+ "\n  --(2) Current Scenario Test-Case Results Detail: "
//					+ "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
//			}
//			sb.setLength(0);
//			//writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);			
//			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
//			prevTime = currTime;					
//		
//		}//end 8.3b
//		//end 8.3
	
		
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();		
		String timeUsed_end = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing WebHDFS, WebHBase, and WebHCat + via Knox Gateway Services and F5 Balancer.... on '" + bdClusterName + "'";
		currNotingMsg += "\n***** Done - Present Testing Generated HDFS Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed_end; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total WebHDFS, WebHBase, and WebHCat + Knox/F5 Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   WebHDFS, WebHBase, and WebHCat + Knox/F5 Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   WebHDFS, WebHBase, and WebHCat + Knox/F5 Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestKnox_RecFilePathAndName));
	
	}//end run()
	
	
//	private static int appendFileDataToExistingKnoxFile (String existingHdfsFilePathAndName, String appendingDataFile, FileSystem currHadoopFS) throws IOException {
//		int existVal = 10000;
//		FSDataOutputStream fsDataOutStream = currHadoopFS.append(new Path(existingHdfsFilePathAndName));			
////		//PrintWriter bw = new PrintWriter(fsDataOutStream);	
////		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
////		
////		//String srcFilePathAndName2 = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
////		System.out.println("\n*** appendingDataFile: " + appendingDataFile);
////		FileReader aFileReader = new FileReader(appendingDataFile);
////		BufferedReader br = new BufferedReader(aFileReader);
////		String line = "";
////		
////		while ((line = br.readLine()) != null) {
////			try {
////				//bw.append(line);
////				existVal = 0;
////				break;
////			} catch (Exception e) {
////				break;
////				//e.printStackTrace();
////			} //end try
////				
////		}
////		br.close();
////		aFileReader.close();
////						
////		bw.close();
//		fsDataOutStream.close();
//		return existVal;
//	}//end appendFileDataToExistingKnoxFile
	
	
	private static String getTargetLineDataLineOfAFile (String filePathAndName, int targetLineNumber){
		String targetLineData = "";
		try {
			FileReader aFileReader = new FileReader(filePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
						
			String line = "";
			int lineNum = 0;
			while ((line = br.readLine()) != null) {
				lineNum++;
				if (lineNum == targetLineNumber){
					targetLineData = line;
				}							
			}//end while
			br.close();
			aFileReader.close();			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		return targetLineData;		
	}//getTargetLineDataLineOfAFile
	
	private static int runAHiveQuery_NoResultSet(Statement stmt,
			String hiveQueryStr) {
		int exitValue = 1000;
		try {
			System.out.println("\n*** Running: " + hiveQueryStr + ": ");
			stmt.execute(hiveQueryStr);
			exitValue = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} // end try
		//System.out.println("\n*-* exitValue: " + exitValue);
		return exitValue;
	}// end runAHiveQuery_NoResultSet

	private static ResultSet runAHiveQuery_YesResultSet(Statement stmt,
			String hiveQueryStr) {
		ResultSet rs = null;
		try {
			System.out.println("\n*** Running: " + hiveQueryStr + ": ");
			rs = stmt.executeQuery(hiveQueryStr);
		} catch (SQLException e) {
			// e.printStackTrace();
			// Thread.interrupted();
			return null;
		}
		return rs;
	}// end runAHiveQuery
	
	private static void transFormTestDataIntoBase64EncodedFormat (String uncodedLocalWebHBaseTestDataFilePathAndName, 
			String encodedLocalWebHBaseTestDataFilePathAndName){
		try {
			File tempFile = new File(encodedLocalWebHBaseTestDataFilePathAndName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);
			
			FileReader aFileReader = new FileReader(uncodedLocalWebHBaseTestDataFilePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
						
			String line = "";			
			while ((line = br.readLine()) != null) {
				if (line.contains(",")){
					String [] lineSplit = line.split(",");
					String encodedLine = "";
					for (int i = 0; i < lineSplit.length; i++ ){
						String tempEncodedColumn =  Base64Str.getEncodedString(lineSplit[i].trim());
						if (i==0){
							encodedLine += tempEncodedColumn;
						} else {
							encodedLine += "," +tempEncodedColumn;
						}						
					}
					output.println(encodedLine);
				}							
			}//end while
			br.close();
			aFileReader.close();
			
			output.close();
			outStream.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Transform TestData Into Base64 Encoded Format for WebHBase Testing!!!\n");
		
	}//transFormTestDataIntoBase64EncodedFormat
	
	private static void moveWindowsLocalHiveTestDataToHDFS (String localWinSrcHiveTestDataFilePathAndName, 
							String hdfsHiveTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
		//System.out.println("\n*** localWinSrcHiveTestDataFilePathAndName: " + localWinSrcHiveTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/Hive/dcHiveTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsHiveTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing Hive file: " + hdfsHiveTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsHiveTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));			
			
			FileReader aFileReader = new FileReader(localWinSrcHiveTestDataFilePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
						
			String line = "";		
			while ((line = br.readLine()) != null) {
				System.out.println("*** line is: " + line);
				bw.write(line + "\n");	
			}
			br.close();
			aFileReader.close();		
			bw.close();
			
			//hdfsFilePathAndNameList.add(hdfsTestDataFilePathAndName);
				
	        //OutputStream os = currHadoopFS.create(outputPath, true);
	        //Configuration conf = currHadoopFS.getConf(); 
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {
			System.out.println("*** Error occurs in moveWindowsLocalHiveTestDataToHDFS(): ");
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalHiveTestDataToHDFS

	private static void writeDataToAFile (String recordingFile, String recordInfo, boolean AppendingStatus) throws IOException{
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


	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
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
	
	private static void prepareFolder(String aNewFolderPathAndName, String purposeInfo){
		File aFolderFile = new File (aNewFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + purposeInfo + ": \n" + aNewFolderPathAndName); 
		}		
	}//end prepareFolder
	
}//end class
