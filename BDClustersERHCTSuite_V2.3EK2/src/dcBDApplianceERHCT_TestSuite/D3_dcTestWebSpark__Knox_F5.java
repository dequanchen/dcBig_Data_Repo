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
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 9/18/2017
*/ 

@SuppressWarnings("unused")
public class D3_dcTestWebSpark__Knox_F5 {
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
	    
		String dcTestKnox_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKnoxF5_WritingAndReading_Records_WebSpark_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKnox_RecFilePathAndName,  "Records of Testing WebSpark/Knox/F5 on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated WebSpark (Livy Spark), Knox-Gateway WebSpark and F5 Balancered WebSpark ... Representative Scenario Testing "
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
		
		
		
		//#5.1 & #5.2 & #5.3 are test scenario types for WebSpark testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[1]. WebSpark \n", true);		
		
		//5.1 Login to EN01 and Perform Data Writing/Reading to WebSpark file 
		//    via cURL cmds and active WebSpark HTTP URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.1]. WebSpark via Active Name Node (HDFS REST) Service \n", true);
		int activeNNNumber_Start = 0;
		int clusterActiveNNNumber = 1;
		for (int i = activeNNNumber_Start; i < clusterActiveNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			//(1) Get active WebSpark HTTP URL:		
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();		
			
			//(2) Generate WebSpark cmds:
			//Sample: curl -i -v --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebSparkCmd_L = "curl -k --negotiate -u : --location-trusted ";
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_activenn" + (i +1) + ".txt";
			
			
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebSparkCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCms = curlKerberizedWebSparkCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebSparkCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + activeWebHdfsHttpURL + "/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebSparkCmd_L + appendWritingToExistingFileCmd_R;
			
			
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
			prepareFile (knoxWebHdfsScriptFullFilePathAndName,  "Script File For Testing Knox WebSpark on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
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
						+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebSpark HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebSpark HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 5.1
		
		
		
		//5.2 Login to EN01 and Perform Data Writing/Reading to WebSpark file 
		//    via cURL cmds and Knox HTTPS URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.2]. WebSpark via Knox Gateway Services \n", true);
				
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1;
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing WebSpark Through Knox Node: " + tempKnENName);
			
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
			String currClusterKnoxWebSparkHttpsURL = "https://" + currKnoxNodeFQDN + ":8442/gateway/" + bdKnoxClusterIdName + "/webhdfs";
			
			//(2) Generate WebSpark cmds:
			//Sample: curl -i -k -u m041785:xxxxx -L "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebSparkCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";					
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_knox" + (i +1) + ".txt";
						
					
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + currClusterKnoxWebSparkHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebSparkCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebSparkHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCmd = curlKerberizedWebSparkCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebSparkHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebSparkCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + currClusterKnoxWebSparkHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebSparkCmd_L + appendWritingToExistingFileCmd_R;
			
			
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
			prepareFile (knoxWebHdfsScriptFullFilePathAndName2,  "Script File For Testing Knox WebSpark on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
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
						+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebSparkHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo2 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebSparkHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo2, true);
			prevTime = currTime;					
		}//end 5.2		
					
				
		
		//5.3 Login to EN01 and Perform Data Writing/Reading to WebSpark file 
		//    via cURL cmds and Cluster F5 Balancer URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1.3]. WebSpark via F5 Balancer Service \n", true);
		int f5BalancerNumber_Start = 0;
		int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			
			//(1) Get F5 Balancer HTTPs URL:		
			//String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			String clusterF5HttpsURL = currClusterF5ConnStr;
			
			//(2) Generate WebSpark cmds:		
			//curl -i -k -u wa00336:bnhgui89 -X GET -L https://bigdataknox-dev.mayo.edu/gateway/MAYOHADOOPDEV1/webhdfs/v1/user/m041785/test/Solr/solr_curl_query_result1.txt?op=LISTSTATUS
			String curlKerberizedWebSparkCmd_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd + " --location-trusted";				
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl_f5balancer" + (i +1) + ".txt";
						
					
			String deleteExistingFileCmd_R = " -X DELETE -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=DELETE\"";
			String deleteExistingFileFullCmd = curlKerberizedWebSparkCmd_L + deleteExistingFileCmd_R;
					
			//String createNewFileCmd_R = " -X PUT -L \"" + currClusterKnoxWebSparkHttpsURL + "/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCmd = curlKerberizedWebSparkCmd_L + createNewFileCmd_R;
			
			String createAndWriteNewFileCmd_R = " -X PUT -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd = curlKerberizedWebSparkCmd_L + createAndWriteNewFileCmd_R;
					
			String appendWritingToExistingFileCmd_R = " -X POST -L \"" + clusterF5HttpsURL + "/webhdfs/v1" + webHDFSFilePathAndName + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd = curlKerberizedWebSparkCmd_L + appendWritingToExistingFileCmd_R;
					
				
		    
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
			prepareFile (knoxWebHdfsScriptFullFilePathAndName,  "Script File For Testing Knox WebSpark on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
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
						+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebSpark File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via F5 Balancer HTTPS URL - " + clusterF5HttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebSpark System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
			
		}// end 5.3
		
		





				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();		
		String timeUsed_end = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing WebSpark (cURL) via Active Name Node, Knox Gateway Services and F5 Balancer.... on '" + bdClusterName + "'";
		currNotingMsg += "\n***** Done - Present Knox Testing Generated HDFS/WebSpark Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed_end; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Knox Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Knox Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Knox Test Scenario Success Rate (%): " + currUATPassedRate;
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
