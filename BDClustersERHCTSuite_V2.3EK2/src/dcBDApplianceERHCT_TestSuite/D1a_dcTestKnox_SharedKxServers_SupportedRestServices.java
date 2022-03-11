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

import com.google.common.io.Files;

import dcModelClasses.Base64Str;
import dcModelClasses.DayClock;
import dcModelClasses.HiveViaKnoxConnectionFactory;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/11/2016; 5/4/2016; 6/14/2016; 6/21/2016; 8/11/2016
*/ 

public class D1a_dcTestKnox_SharedKxServers_SupportedRestServices {
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
	
	private static String localKnoxOozieJarFileName = "";
	private static String localKnoxOozieKnoxConfigFolderName = "";	
	private static String localKnoxOozieWorkflowFileName = "";	
	private static String localKnoxOozieWorkflowConfigXmlFileName = "";
	
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
		
		localKnoxOozieJarFileName = args[12];
		localKnoxOozieKnoxConfigFolderName= args[13];
		localKnoxOozieWorkflowFileName = args[14];
		localKnoxOozieWorkflowConfigXmlFileName = args[15];
						
		if (!bdClusterUATestResultsParentFolder.endsWith("\\")){
			bdClusterUATestResultsParentFolder += "\\";
		}
		if (!knoxTestFolderName.endsWith("/")){
			knoxTestFolderName += "/";
		}		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
		
		if (!localKnoxOozieKnoxConfigFolderName.endsWith("\\")){
			localKnoxOozieKnoxConfigFolderName += "\\";
		}
					
		run();
	}//end main
	
	@SuppressWarnings("deprecation")
	public static void run() throws Exception {
		//1. Preparation:
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String scriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Knox\\";
	    prepareFolder(scriptFilesFoder, "Local Knox Testing Script Files");
	    
		String dcTestKnox_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKnox_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKnox_RecFilePathAndName,  "Records of Testing Knox on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Knox-Gateway WebHDFS, WebHcat, WebHBase, Hive JDBC ... Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		
		//3.Get cluster FileSystem and other information for testing
		//3.(1)
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		
		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
					
		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();
		//String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNode2Name();	
		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
		String bdClusterKnoxIdName = currBdCluster.getBdClusterIdName();
		String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		String currClusterKnoxFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currClusterKnoxFQDN: " + currClusterKnoxFQDN);
		
		String loginUserName = bdENCmdFactory.getUsername();
		//String loginUserADPassWd = bdENCmdFactory.getPassword();
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos			
		String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();		
		System.out.println(" *** Login User Name / AD Password: " + loginUserName + " / " + loginUserADPassWd);
		
		String knoxTestFolderName = "/user/" + loginUserName + "/test/Knox/";
		//String knoxTestFolderName = activeNN_addr_port + "/user/" + loginUserName + "/test/Knox/";
		
		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
		String rootUserName = bdENRootCmdFactory.getUsername();
		//String rootPw = bdENRootCmdFactory.getPassword();			
		//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
		//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
		
		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
		}			
		System.out.println("*** loginUserName is: " + loginUserName);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		
		//3.(2) The followings are for Knox-OOzie testing:
		String knoxTestFolderName_Full = activeNN_addr_port + knoxTestFolderName;
		
		String loginCredential_AD = loginUserName + ":" + loginUserADPassWd;		
		System.out.println(" *** loginCredential_AD: " + loginCredential_AD);
		
		String knoxOozieMRActionInputFolder = knoxTestFolderName_Full + "oozie/input/";
		String knoxOozieMRActionOutputFolder = knoxTestFolderName_Full + "oozie/output/";
		String knoxOozieMRActionLibFolder = knoxTestFolderName_Full + "oozie/lib/";
		
		System.out.println(" *** knoxOozieMRActionInputFolder: " + knoxOozieMRActionInputFolder);
		System.out.println(" *** knoxOozieMRActionOutputFolder: " + knoxOozieMRActionOutputFolder);
		System.out.println(" *** knoxOozieMRActionLibFolder: " + knoxOozieMRActionLibFolder);
		String localOozieKnoxWorkflowConfigXmlFileFullPathAndName= localKnoxOozieKnoxConfigFolderName + localKnoxOozieWorkflowConfigXmlFileName;
		String srcFileFullPathAndName = localOozieKnoxWorkflowConfigXmlFileFullPathAndName;
		
		String localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific = "";
		if (localKnoxOozieWorkflowConfigXmlFileName.endsWith("configuration.xml")){
			localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific = localKnoxOozieWorkflowConfigXmlFileName.replace("configuration.xml", "configuration_" + bdClusterName + ".xml");
		}
				
		String nameNodeStr = "hdfs://" + currBdCluster.getBdClusterIdName();
		System.out.println("\n*** nameNodeStr: " + nameNodeStr);	
		String jobTrackerStr = currBdCluster.getBdClusterActiveRMIPAddressAndPort();
		System.out.println("\n*** jobTrackerStr: " + jobTrackerStr);
		
		System.out.println("\n*** srcFileFullPathAndName: " + srcFileFullPathAndName);
		
		
		localOozieKnoxWorkflowConfigXmlFileFullPathAndName = localKnoxOozieKnoxConfigFolderName + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
		String destFileFullPathAndName = localOozieKnoxWorkflowConfigXmlFileFullPathAndName;
		System.out.println("\n*** destFileFullPathAndName: " + destFileFullPathAndName);
		
		Files.copy(new File (srcFileFullPathAndName), new File (destFileFullPathAndName));
		updateLocalOozieKnoxWorkflowConfigXmlFile (localOozieKnoxWorkflowConfigXmlFileFullPathAndName, nameNodeStr, jobTrackerStr, loginUserName);
						
		String enServerScriptFileDirectory_oozieKnox = "/data/home/" + loginUserName + "/test/oozie/knox/";		
		System.out.println("*** enServerScriptFileDirectory_oozieKnox is: " + enServerScriptFileDirectory_oozieKnox);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory_oozieKnox, bdENCmdFactory);
		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory_oozieKnox: " + enServerScriptFileDirectory_oozieKnox);
								
		//3.(3)
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
				
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "A. Testing Knox Node - " + currClusterKnoxNodeName+ "\n", true);
		
		
		//4. Move Test Data and Appending Test Data, Knox-Oozie-related Jar File & Workflow and Configuration Files To Knox Server Node
		//String localWinTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
		//String localAppendingTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
		
		//4.(1) Moving test data and appending test data into Linux local folder for testing	
		String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;
		String enServerAppendingTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestAppendingDataFileName;
				
		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestAppendingDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseJsonDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseXmlDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		tempClock = new DayClock();				
		tempTime = tempClock.getCurrentDateTime();
		
		if (exitVal1 == 0 ){
			System.out.println("\n*** Done - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal2 == 0 ){
			System.out.println("\n*** Done - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		if (exitVal3 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal4 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		//4.(2) Moving Knox-Oozie MapReduce jar file, work flow configuration file and workflow.xml into Linux local folder for testing		
		int exitVal5 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxOozieJarFileName, 
				bdClusterUATestResultsParentFolder, enServerScriptFileDirectory_oozieKnox, bdENCmdFactory);
		
		int exitVal6 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific, 
				localKnoxOozieKnoxConfigFolderName, enServerScriptFileDirectory_oozieKnox, bdENCmdFactory);			
		
		int exitVal7 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxOozieWorkflowFileName, 
				localKnoxOozieKnoxConfigFolderName, enServerScriptFileDirectory_oozieKnox, bdENCmdFactory);
	
		tempClock = new DayClock();				
		tempTime = tempClock.getCurrentDateTime();	
				
		
		if (exitVal5 == 0 ){
			System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		} else {
			System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		}
			
		if (exitVal6 == 0 ){
			System.out.println("\n*** Done - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		} else {
			System.out.println("\n*** Failed - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		}
		
		if (exitVal7 == 0 ){
			System.out.println("\n*** Done - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		} else {
			System.out.println("\n*** Failed - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory_oozieKnox + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
		}
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		
		
		//#5.1 & #5.2 are test scenario types for WebHDFS testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[1]. Knox & WebHDFS \n", true);		
		//5.1 Login to Current Hadoop Cluster Knox Server and Perform Data Writing/Reading to WebHDFS file 
		//    via cURL cmds and active WebHDFS HTTP URL		
		int webHdfsRestServiceNumber = 1; //1
		int webHdfsService_Start = 0; //0..1
		
		for (int i = webHdfsService_Start; i < webHdfsRestServiceNumber; i++){
			totalTestScenarioNumber++;
			
			//tempClock = new DayClock();				
			//String tempTime1_start = tempClock.getCurrentDateTime();
			
			//(1) Get active WebHDFS HTTP URL:			
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			
			//(2) Generate WebHDFS cmds:
			//Sample: curl -i -v --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebHDFSCmd_L = "curl -i -v --negotiate -u : ";
			String webHDFSFilePathAndName = knoxTestFolderName + "employee_webhdfs_curl.txt";
			
			
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
		    
		    String knoxWebHdfsScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHdfsScriptFile_Curl.sh";			
			prepareFile (knoxWebHdfsScriptFullFilePathAndName,  "Script File For Testing Knox WebHDFS on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
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
						+ "\n  -- (1) WebHDFS Rest API File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebHDFS HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHDFS Rest API File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Active WebHDFS HTTP URL - " + activeWebHdfsHttpURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end 5.1
		
		
		
		//5.2 Login to Current Hadoop Cluster Knox Server and Perform Data Writing/Reading to WebHDFS file 
		//    via cURL cmds and Knox HTTPS URL			
		int securedWebHdfsRestServiceNumber = 1; //1
		int securedWebHdfsService_Start = 0; //0..1
		
		if (currClusterKnoxNode2Name.isEmpty()){
			securedWebHdfsRestServiceNumber += 1;
		}
		
		for (int i = securedWebHdfsService_Start; i < securedWebHdfsRestServiceNumber; i++){
			totalTestScenarioNumber++;	
			
			//tempClock = new DayClock();				
			//String tempTime2_start = tempClock.getCurrentDateTime();
			
			//(1) Get Current Hadoop Cluster's Knox HTTPS URL:			
			String currClusterKnoxWebHDFSHttpsURL = "";
			if (i==0){
				currClusterKnoxWebHDFSHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/webhdfs";
			}
			if (i==1){
				currClusterKnoxWebHDFSHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "_bkp" + "/webhdfs";
			}
						
			//(2) Generate WebHDFS cmds:
			//Sample: curl -i -k -u m041785:xxxxx -L "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN"
			String curlKerberizedWebHDFSCmd2_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd;				
			String webHDFSFilePathAndName2 = knoxTestFolderName + "employee_webhdfs_curl2.txt";
						
					
			String deleteExistingFileCmd2_R = " -X DELETE -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName2 + "?op=DELETE\"";
			String deleteExistingFileFullCmd2 = curlKerberizedWebHDFSCmd2_L + deleteExistingFileCmd2_R;
					
			//String createNewFileCmd2_R = " -X PUT -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName2 + "?op=CREATE&overwrite=true\"";
			//String createNewFileFullCmd2 = curlKerberizedWebHDFSCmd2_L + createNewFileCmd2_R;
			
			String createAndWriteNewFileCmd2_R = " -X PUT -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName2 + "?op=CREATE&overwrite=true\"" + " -T " + enServerTestDataFileFullPathAndName;
			String createAndWriteNewFileFullCmd2 = curlKerberizedWebHDFSCmd2_L + createAndWriteNewFileCmd2_R;
					
			String appendWritingToExistingFileCmd2_R = " -X POST -L \"" + currClusterKnoxWebHDFSHttpsURL + "/v1" + webHDFSFilePathAndName2 + "?op=APPEND\"" + " -T " + enServerAppendingTestDataFileFullPathAndName;
			String appendWritingToExistingFileFullCmd2 = curlKerberizedWebHDFSCmd2_L + appendWritingToExistingFileCmd2_R;
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName2 + "; \n");
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
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHDFSFilePathAndName2 + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 777 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    	    
		    sb.append(deleteExistingFileFullCmd2 + ";\n");
		    sb.append(createAndWriteNewFileFullCmd2 + ";\n");
		    sb.append(appendWritingToExistingFileFullCmd2 + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHdfsScriptFullFilePathAndName2 = scriptFilesFoder + "dcTestKnox_WebHdfsScriptFile_Curl2.sh";			
			prepareFile (knoxWebHdfsScriptFullFilePathAndName2,  "Script File For Testing Knox WebHDFS on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
			String webHdfsTestingCmds2 = sb.toString();
			writeDataToAFile(knoxWebHdfsScriptFullFilePathAndName2, webHdfsTestingCmds2, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHdfsScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHdfsScriptFullFilePathAndName2, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
				
			//Read and find the appended data from the appended file
			boolean currTestScenarioSuccessStatus2 = false;
			Path filePath2 = new Path(webHDFSFilePathAndName2);
			if (currHadoopFS.exists(filePath2)) {
			hdfsFilePathAndNameList.add(webHDFSFilePathAndName2);
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
					hdfsFilePathAndNameList.add(webHDFSFilePathAndName2);
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
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName2 + "' \n";
	        } else {
	        	testRecordInfo2 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHDFS File Deleting, Creating & Writing, and Append-Writing "
						+ "\n          via Knox Gateway HTTPS URL - " + currClusterKnoxWebHDFSHttpsURL
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHDFSFilePathAndName2 + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo2, true);
			prevTime = currTime;
		}//end 5.2
		
				
		
		//#6.1 & #6.2 are test scenario types for WebHcat testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[2]. Knox & WebHCat \n", true);
		//6.1 Login to Current Hadoop Cluster Knox Server and Perform HCatalog DDL operation 
		//    via cURL cmds and WebHCat HTTP URL determined by name node status
		
		int webHcatRestServiceNumber = 2; //1..2
		int webHcatService_Start = 0; //0..1
		
		//webHcatService_Start = 1;
		//webHcatRestServiceNumber = 1; //1 ..2;
		for (int i = webHcatService_Start; i < webHcatRestServiceNumber; i++){
			totalTestScenarioNumber++;
			
			//tempClock = new DayClock();				
			//String tempTime3_start = tempClock.getCurrentDateTime();			
			
			//(1) Get WebHbase HTTP URL:				
			String activeWebHcatHttpURL = "";
			
			if (i==0){
				activeWebHcatHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":50111").replace("webhdfs", "templeton");
			}
			if (i==1){
				activeWebHcatHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":50111").replace("webhdfs", "templeton").replace("mn01.", "mn02.");
			}
			
			//(2) Generate WebHcat cmds:
			//Sample: curl -s --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:50111/templeton/v1/ddl/database";
			String curlKerberizedWebHDFSCmd3_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			String webHcatDbName = "webhcat_db";
			String webHcatDbLocation = "hdfs://" + bdClusterKnoxIdName + "/apps/hive/warehouse/" + webHcatDbName;
			String webHcatTableName = "employee_webhcat1";
			String webHcatTestResultFileName = "employee_webhcat_curl_result.txt";
			String localWebHcatTestResultPathAndName = enServerScriptFileDirectory + webHcatTestResultFileName;
			String webHcatTestResultHdfsPathAndName = knoxTestFolderName + webHcatTestResultFileName;
			
			String dropExistingHcatDBCmd_R = " -X DELETE -L \"" + activeWebHcatHttpURL + "/v1/ddl/database/" + webHcatDbName + "\"";
			String dropExistingHcatDBFullCmd = curlKerberizedWebHDFSCmd3_L + dropExistingHcatDBCmd_R;
					
			String createNewHcatDBCmd_R = " -X PUT -L -HContent-type:application/json "
					+ " -d '{ \"comment\":\"Default HCatalog database\", "
					        + "\"location\":\""+ webHcatDbLocation + "\", "
					        + "\"properties\":{\"owner\":\"public\",\"ownerType\":\"ROLE\"}}' "
					+ " -L \"" + activeWebHcatHttpURL + "/v1/ddl/database/" + webHcatDbName + "\"";
			String createNewHcatDBFullCmd = curlKerberizedWebHDFSCmd3_L + createNewHcatDBCmd_R;
			
			
			String dropHcatTableCmd_R = " -X DELETE -L \"" + activeWebHcatHttpURL + "/v1/ddl/database/" + webHcatDbName + "/table/" + webHcatTableName + "\"";
			String dropHcatTableFullCmd = curlKerberizedWebHDFSCmd3_L + dropHcatTableCmd_R;
			
			String createNewHcatTableCmd_R = " -X PUT -L -HContent-type:application/json "
					+ " -d '{ \"comment\":\"WebHcat Testing HCatalog Table\", "
					        + "\"columns\": ["
					            + "{ \"name\": \"employeeId\", \"type\": \"int\" }, "
					            + "{ \"name\": \"firstName\", \"type\": \"string\" }, "
					            + "{ \"name\": \"lastName\", \"type\": \"int\" }, "
					            + "{ \"name\": \"salary\", \"type\": \"int\" }, "
					            + "{ \"name\": \"gender\", \"type\": \"string\" }, "
					            + "{ \"name\": \"address\", \"type\": \"string\" }], "
					        + "\"format\": { \"storedAs\": \"rcfile\" } }' "
					+ " -L \"" + activeWebHcatHttpURL + "/v1/ddl/database/" + webHcatDbName + "/table/" + webHcatTableName + "\"";
			String createNewHcatTableFullCmd = curlKerberizedWebHDFSCmd3_L + createNewHcatTableCmd_R;
					
			String listHcatTableColumnCmd_R = " -L \"" + activeWebHcatHttpURL + "/v1/ddl/database/" + webHcatDbName + "/table/" + webHcatTableName + "/column\"";
			String listHcatTableColumnFullCmd = curlKerberizedWebHDFSCmd3_L + listHcatTableColumnCmd_R;
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHcatTestResultHdfsPathAndName + "; \n");
			//sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			//sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	    
		    //sb.append("exit; \n");	
		    
		    //sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHcatTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    
			sb.append(dropHcatTableFullCmd + ";\n");	
		    sb.append(dropExistingHcatDBFullCmd + ";\n");
		    sb.append(createNewHcatDBFullCmd + ";\n");
		    sb.append(dropHcatTableFullCmd + ";\n");	    
		    sb.append(createNewHcatTableFullCmd + ";\n");	    
		    sb.append(listHcatTableColumnFullCmd + " > " + localWebHcatTestResultPathAndName +";\n"); 
		    
		    sb.append("hadoop fs -copyFromLocal " + localWebHcatTestResultPathAndName + " " + activeNN_addr_port + webHcatTestResultHdfsPathAndName + "; \n");
		   	//sb.append("kdestroy;\n");	    
		    //sb.append("sudo su - hdfs;\n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHcatScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHcatScriptFile_Curl_No"+ (i+1) + ".sh";			
			prepareFile (knoxWebHcatScriptFullFilePathAndName,  "Script File For Testing Knox WebHcat on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
			String webHcatTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHcatScriptFullFilePathAndName, webHcatTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHcatScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHcatScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus3 = false;
			Path filePath3 = new Path(webHcatTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath3)) {
				hdfsFilePathAndNameList.add(webHcatTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath3);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("{\"name\":\"address\",\"type\":\"string\"}")) {
						currTestScenarioSuccessStatus3 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
			
			String testRecordInfo3 = "";
			if (currTestScenarioSuccessStatus3) {
				successTestScenarioNum++;			
				testRecordInfo3 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHcat Rest API Database Dropping & Creating, Table Dropping & Creating, and Column Listing "
						+ "\n          via Active WebHcat HTTP URL - " + activeWebHcatHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHcatTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo3 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHcat Rest API Database Dropping & Creating, Table Dropping & Creating, and Column Listing "
						+ "\n          via Active WebHcat HTTP URL - " + activeWebHcatHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHcatTestResultHdfsPathAndName + "' \n";
	        }			
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo3, true);	
			prevTime = currTime;
		}//end 6.1
		
				
		
		//6.2 Login to Current Hadoop Cluster Knox Server and Perform HCatalog DDL operation 
		//    via cURL cmds and WebHCat Knox HTTPS URL 		
		int knox_WebHcatRestServiceNumber = 1; //1..2
		if (currClusterKnoxNode2Name.isEmpty()){
			knox_WebHcatRestServiceNumber += 1;
		}
		
		int knox_WebHcatService_Start = 0; //0..1		
		for (int i = knox_WebHcatService_Start; i < knox_WebHcatRestServiceNumber; i++){
			totalTestScenarioNumber++;
			
			//tempClock = new DayClock();				
			//String tempTime4_start = tempClock.getCurrentDateTime();		
			
			//(1) Get Knox-Gateway WebHcat HTTPs URL:			
			String currClusterKnoxWebHcatHttpsURL = "";			
			if (i==0){
				currClusterKnoxWebHcatHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/templeton";
			}
			if (i==1){
				currClusterKnoxWebHcatHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "_bkp" + "/templeton";				
			}
			
			//(2) Generate WebHDFS cmds:
			//Sample: curl -i -k -u m041785:xxxxx -L "https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/templeton/v1/ddl/database/webhcat_db/table/employee_webhcat1/column";
			String curlKerberizedWebHDFSCmd4_L = "curl -i -k -u " + loginUserName + ":" + loginUserADPassWd;		
			String webHcatDbName2 = "webhcat_db2";
			String webHcatDbLocation2 = "hdfs://" + bdClusterKnoxIdName + "/apps/hive/warehouse/" + webHcatDbName2;
			String webHcatTableName2 = "employee_webhcat2";
			String webHcatTestResultFileName2 = "employee_webhcat_curl_result2.txt";
			String localWebHcatTestResultPathAndName2 = enServerScriptFileDirectory + webHcatTestResultFileName2;
			String webHcatTestResultHdfsPathAndName2 = knoxTestFolderName + webHcatTestResultFileName2;
			
			String dropExistingHcatDBCmd4_R = " -X DELETE -L \"" + currClusterKnoxWebHcatHttpsURL + "/v1/ddl/database/" + webHcatDbName2 + "\"";
			String dropExistingHcatDBFullCmd_4 = curlKerberizedWebHDFSCmd4_L + dropExistingHcatDBCmd4_R;
					
			String createNewHcatDBCmd4_R = " -X PUT -L -HContent-type:application/json "
					+ " -d '{ \"comment\":\"Default HCatalog database 2\", "
					        + "\"location\":\""+ webHcatDbLocation2 + "\", "
					        + "\"properties\":{\"owner\":\"public\",\"ownerType\":\"ROLE\"}}' "
					+ " -L \"" + currClusterKnoxWebHcatHttpsURL + "/v1/ddl/database/" + webHcatDbName2 + "\"";
			String createNewHcatDBFullCmd_4 = curlKerberizedWebHDFSCmd4_L + createNewHcatDBCmd4_R;
			
			
			String dropHcatTableCmd4_R = " -X DELETE -L \"" + currClusterKnoxWebHcatHttpsURL + "/v1/ddl/database/" + webHcatDbName2 + "/table/" + webHcatTableName2 + "\"";
			String dropHcatTableFullCmd_4 = curlKerberizedWebHDFSCmd4_L + dropHcatTableCmd4_R;
			
			String createNewHcatTableCmd4_R = " -X PUT -L -HContent-type:application/json "
					+ " -d '{ \"comment\":\"WebHcat Testing HCatalog Table\", "
					        + "\"columns\": ["
					            + "{ \"name\": \"employeeId\", \"type\": \"int\" }, "
					            + "{ \"name\": \"firstName\", \"type\": \"string\" }, "
					            + "{ \"name\": \"lastName\", \"type\": \"int\" }, "
					            + "{ \"name\": \"salary\", \"type\": \"int\" }, "
					            + "{ \"name\": \"gender\", \"type\": \"string\" }, "
					            + "{ \"name\": \"address\", \"type\": \"string\" }], "
					        + "\"format\": { \"storedAs\": \"rcfile\" } }' "
					+ " -L \"" + currClusterKnoxWebHcatHttpsURL + "/v1/ddl/database/" + webHcatDbName2 + "/table/" + webHcatTableName2 + "\"";
			String createNewHcatTableFullCmd_4 = curlKerberizedWebHDFSCmd4_L + createNewHcatTableCmd4_R;
					
			String listHcatTableColumnCmd4_R = " -L \"" + currClusterKnoxWebHcatHttpsURL + "/v1/ddl/database/" + webHcatDbName2 + "/table/" + webHcatTableName2 + "/column\"";
			String listHcatTableColumnFullCmd_4 = curlKerberizedWebHDFSCmd4_L + listHcatTableColumnCmd4_R;
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHcatTestResultHdfsPathAndName2 + "; \n");
			//sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			//sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	    
		    	
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
		    //sb.append("sleep 30; \n");
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + webHcatTestResultHdfsPathAndName2 + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":hdfs " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");	    
			
		    sb.append(dropHcatTableFullCmd_4 + ";\n");	 
		    sb.append(dropExistingHcatDBFullCmd_4 + ";\n");
		    sb.append(createNewHcatDBFullCmd_4 + ";\n");
		    sb.append(dropHcatTableFullCmd_4 + ";\n");	    
		    sb.append(createNewHcatTableFullCmd_4 + ";\n");	    
		    sb.append(listHcatTableColumnFullCmd_4 + " > " + localWebHcatTestResultPathAndName2 +";\n"); 
		    
		    sb.append("hadoop fs -copyFromLocal " + localWebHcatTestResultPathAndName2 + " " + activeNN_addr_port + webHcatTestResultHdfsPathAndName2 + "; \n");
		   	//sb.append("kdestroy;\n");	    
		    //sb.append("sudo su - hdfs;\n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxWebHcatScriptFullFilePathAndName2 = scriptFilesFoder + "dcTestKnox_Secure_WebHcatScriptFile_Curl_No"+ (i+1) + ".sh";			
			prepareFile (knoxWebHcatScriptFullFilePathAndName2,  "Script File For Testing Knox WebHcat on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
			String webHcatTestingCmds2 = sb.toString();
			writeDataToAFile(knoxWebHcatScriptFullFilePathAndName2, webHcatTestingCmds2, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHcatScriptFullFilePathAndName2));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHcatScriptFullFilePathAndName2, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus4 = false;
			Path filePath4 = new Path(webHcatTestResultHdfsPathAndName2);
			if (currHadoopFS.exists(filePath4)) {
				hdfsFilePathAndNameList.add(webHcatTestResultHdfsPathAndName2);
				FileStatus[] status = currHadoopFS.listStatus(filePath4);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("{\"name\":\"address\",\"type\":\"string\"}")) {
						currTestScenarioSuccessStatus4 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime); 
			
			String testRecordInfo4 = "";
			if (currTestScenarioSuccessStatus4) {
				successTestScenarioNum++;			
				testRecordInfo4 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHcat Database Dropping & Creating, Table Dropping & Creating, and Column Listing "
						+ "\n          via Knox WebHcat HTTPS URL - " + currClusterKnoxWebHcatHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHcatTestResultHdfsPathAndName2 + "' \n";
	        } else {
	        	testRecordInfo4 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHcat Database Dropping & Creating, Table Dropping & Creating, and Column Listing "
						+ "\n          via Knox WebHcat HTTPS URL - " + currClusterKnoxWebHcatHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/WebHDFS System:  '" + webHcatTestResultHdfsPathAndName2 + "' \n";
	        }
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo4, true);	
			prevTime = currTime;
		}//end 6.2
		
		
		
		//#7.1 & #7.2 are test scenario types for WebHbase testing
		//7.1 Login to Current Hadoop Cluster Knox Server and Perform WebHbase operation 
		//    via cURL cmds and WebHbase HTTP URL(s)
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[3]. Knox & WebHBase (Stargate HBase) \n", true);
		int webHbaseRestServiceNumber = 2; //1..2
		int webHbaseService_Start = 0; //0..1
		
		for (int i = webHbaseService_Start; i < webHbaseRestServiceNumber; i++){
			//(1) Get active WebHcat HTTP URL:
			//tempClock = new DayClock();				
			//String tempTime5_start = tempClock.getCurrentDateTime();
			
			totalTestScenarioNumber++;
			String activeWebHbaseHttpURL = "";
			if (i==0){
				activeWebHbaseHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("/webhdfs", "/").replace("mn02.", "mn01.");
			}
			if (i==1){
				activeWebHbaseHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("/webhdfs", "/").replace("mn01.", "mn02.");
			}
			//currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("mn01.", "mn02.").replace("/webhdfs", "/");
			//System.out.println(" *** activeWebHbaseHttpURL: " + activeWebHbaseHttpURL);
			
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
			String curlKerberizedWebHBaseCmd5_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			//String webHBaseTableName = "employee_webhbase1";	
			String webHBaseTableName = "employee_webhbase" + (i+1);	
			String webHBaseTableURL = activeWebHbaseHttpURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			//String webHBaseTestResultFileName = "employee_webhbase_curl_result.txt";
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result.txt";
			
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd5_R = " -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd5 = curlKerberizedWebHBaseCmd5_L + deleteExistingHBaseTableCmd5_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd5: \n" + deleteExistingHBaseTableFullCmd5);
					
			String createNewHBaseTableCmd5_R = " -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd5 = curlKerberizedWebHBaseCmd5_L + createNewHBaseTableCmd5_R;
			//System.out.println(" *** createNewHBaseTableFullCmd5: \n" + createNewHBaseTableFullCmd5);
			
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
			String insertRow1CellCmd5_M =  " -H \"Content-Type: application/octet-stream\""
					+ " -X PUT " + webHBaseTableURL + "/" + row1Key;
			String insertRow1FirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:firstName"
					+ " -d '" + row1FirstName + "'";
			String insertRow1LasttNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:lastName"
					+ " -d '" + row1LastName + "'";
			String insertRow1SalaryCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:salary"
					+ " -d '" + row1Salary + "'";
			String insertRow1GenderCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:gender"
					+ " -d '" + row1Gender + "'";
			String insertRow1AddressCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:address"
					+ " -d '" + row1Address + "'";
			
			//PUT==POST for WebHBase REST API
			insertRow1FirstNameCellFullCmd5 = insertRow1FirstNameCellFullCmd5.replace("-X PUT", "-X POST");
			insertRow1LasttNameCellFullCmd5 = insertRow1LasttNameCellFullCmd5.replace("-X PUT", "-X POST");
			//System.out.println(" *-* insertRow1FirstNameCellFullCmd5: \n	" + insertRow1FirstNameCellFullCmd5);
			//System.out.println(" *-* insertRow1AddressCellFullCmd5: \n	" + insertRow1AddressCellFullCmd5);
			
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
			
			String insertRow2Cmd5_M =  " -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row2Key);
			
			String insertRow2Cmd5_R = " -d '{\"Row\": ["
									+ "{\"key\":\"" + row2Key + "\","
									+ "\"Cell\": ["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row2FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row2LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row2Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row2Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row2Address + "\"}"
									+ " ]} ]}'";
			String insertRow2FullCmd5 = curlKerberizedWebHBaseCmd5_L + insertRow2Cmd5_M + insertRow2Cmd5_R;
			//System.out.println(" *-* insertRow2FullCmd5: \n	" + insertRow2FullCmd5);
			
			
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
			
			String insertRow3Cmd5_M =  " -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row3Key);
			
			String insertRow3Cmd5_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row3Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row3FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row3LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row3Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row3Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row3Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow3FullCmd5 = curlKerberizedWebHBaseCmd5_L + insertRow3Cmd5_M + insertRow3Cmd5_R;
			//System.out.println(" *-* insertRow3FullCmd5: \n	" + insertRow3FullCmd5);
			
			
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
			
			String insertRows4N5Cmd5_M =  " -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/false-row-key";
			
			String insertRows4N5Cmd5_R = " -d '{\"Row\":["
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
			String insertRows4N5FullCmd5 = curlKerberizedWebHBaseCmd5_L + insertRows4N5Cmd5_M + insertRows4N5Cmd5_R;
			//System.out.println(" *-* insertRows4N5FullCmd5: \n	" + insertRows4N5FullCmd5);
					
					
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
			
			String insertRow6Cmd5_M =  " -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/false-row-key";
			
			String insertRow6Cmd5_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row6Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row6FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row6LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row6Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row6Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row6Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow6FullCmd5 = curlKerberizedWebHBaseCmd5_L + insertRow6Cmd5_M + insertRow6Cmd5_R;
			System.out.println(" *-* insertRow6FullCmd5: \n	" + insertRow6FullCmd5);
			
			
			//(3)d Create WebHBase commands for querying HBase Data 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			String queryHBaseTableCellCmd5_M =  " " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/101/cfs:firstName | awk '{print $0\"\"}' > " + tempLocalWebHBaseTestResultPathAndName;
					
			String getRow2LastNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M		
					+ "/102/cfs:lastName | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow3SalaryCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/103/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow4SalaryCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/104/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow5GenderCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/105/cfs:gender | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow6AddressCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/106/cfs:address | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String transformLocalTempFileIntoRecordFileCmd5 = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd5 = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd5 = "rm -f " + localWebHBaseTestResultPathAndName;
			
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
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd5 + ";\n");
			sb.append(createNewHBaseTableFullCmd5 + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd5 + ";\n");		
			sb.append(insertRow1LasttNameCellFullCmd5 + ";\n");
			sb.append(insertRow1SalaryCellFullCmd5 + ";\n");
			sb.append(insertRow1GenderCellFullCmd5 + ";\n");
			sb.append(insertRow1AddressCellFullCmd5 + ";\n");		
			sb.append(insertRow2FullCmd5 + ";\n");
			sb.append(insertRow3FullCmd5 + ";\n");
			sb.append(insertRows4N5FullCmd5 + ";\n");
			sb.append(insertRow6FullCmd5 + ";\n");
		
			sb.append(getRow1FirstNameCellFullCmd5 + ";\n");
			sb.append(getRow2LastNameCellFullCmd5 + ";\n");
			sb.append(getRow3SalaryCellFullCmd5 + ";\n");		
			sb.append(getRow4SalaryCellFullCmd5 + ";\n");
			sb.append(getRow5GenderCellFullCmd5 + ";\n");
			sb.append(getRow6AddressCellFullCmd5 + ";\n");		
			sb.append(transformLocalTempFileIntoRecordFileCmd5 + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd5 + ";\n"); 
			
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
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd5 + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHBaseScriptFile_Curl" + (i+1) + ".sh";			
			prepareFile (knoxWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
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
			
			String testRecordInfo5 = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo5 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHBase Rest API Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
						+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo5 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHBase Rest API Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
	        			+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo5, true);
			prevTime = currTime;
		}//end for of 7.1
		
		
		//7.2 Login to Current Hadoop Cluster Knox Server and Perform Secure WebHbase operation 
		//    via cURL cmds and WebHbase HTTP URL determined by name node status
		int secureWebHbaseThroughKnoxServiceNumber = 1; //1..2
		int secureWebHbaseThroughKnoxService_Start = 0; //0..1

		//String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		if (currClusterKnoxNode2Name.isEmpty()){
			secureWebHbaseThroughKnoxServiceNumber += 1;
		}
		System.out.println("*-* secureWebHbaseThroughKnoxServiceNumber: " + secureWebHbaseThroughKnoxServiceNumber);
		
		
		for (int i = secureWebHbaseThroughKnoxService_Start; i < secureWebHbaseThroughKnoxServiceNumber; i++){
			//(1) Get active WebHbase HTTPS URL:
			//tempClock = new DayClock();				
			//String tempTime5_start = tempClock.getCurrentDateTime();
			
			//String currClusterKnoxWebHBaseHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/webhdfs";
			
			totalTestScenarioNumber++;
			String currClusterKnoxWebHBaseHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName;
			if (i==0){
				currClusterKnoxWebHBaseHttpsURL += "/hbase/";
			}
			if (i==1){				
				currClusterKnoxWebHBaseHttpsURL += "_bkp/hbase/";
			}
			//currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("mn01.", "mn02.").replace("/webhdfs", "/");
			//System.out.println(" *** currClusterKnoxWebHBaseHttpsURL: " + currClusterKnoxWebHBaseHttpsURL);
			
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
			String curlKerberizedWebHBaseCmd6_L = "curl -k -u " + loginUserName + ":" + loginUserADPassWd;
			String webHBaseTableName = "employee_knox_webhbase" + (i+1);		
			String webHBaseTableURL = currClusterKnoxWebHBaseHttpsURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result.txt";
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd6_R = " -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd6 = curlKerberizedWebHBaseCmd6_L + deleteExistingHBaseTableCmd6_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd6: \n" + deleteExistingHBaseTableFullCmd6);
					
			String createNewHBaseTableCmd6_R = " -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd6 = curlKerberizedWebHBaseCmd6_L + createNewHBaseTableCmd6_R;
			//System.out.println(" *** createNewHBaseTableFullCmd6: \n" + createNewHBaseTableFullCmd6);
			
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
			String insertRow1CellCmd6_M =  " -H \"Content-Type: application/octet-stream\""
					+ " -X PUT " + webHBaseTableURL + "/" + row1Key;
			String insertRow1FirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:firstName"
					+ " -d '" + row1FirstName + "'";
			String insertRow1LasttNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:lastName"
					+ " -d '" + row1LastName + "'";
			String insertRow1SalaryCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:salary"
					+ " -d '" + row1Salary + "'";
			String insertRow1GenderCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:gender"
					+ " -d '" + row1Gender + "'";
			String insertRow1AddressCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:address"
					+ " -d '" + row1Address + "'";
			
			//PUT==POST for WebHBase REST API
			insertRow1FirstNameCellFullCmd6 = insertRow1FirstNameCellFullCmd6.replace("-X PUT", "-X POST");
			insertRow1LasttNameCellFullCmd6 = insertRow1LasttNameCellFullCmd6.replace("-X PUT", "-X POST");
			//System.out.println(" *-* insertRow1FirstNameCellFullCmd6: \n	" + insertRow1FirstNameCellFullCmd5);
			//System.out.println(" *-* insertRow1AddressCellFullCmd6: \n	" + insertRow1AddressCellFullCmd6);
			
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
			
			String insertRow2Cmd6_M =  " -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row2Key);
			
			String insertRow2Cmd6_R = " -d '{\"Row\": ["
									+ "{\"key\":\"" + row2Key + "\","
									+ "\"Cell\": ["
									+ "{\"column\":\"Y2ZzOmZpcnN0TmFtZQ==\",\"$\":\"" + row2FirstName + "\"},"
									+ "{\"column\":\"Y2ZzOmxhc3ROYW1l\",\"$\":\"" + row2LastName + "\"},"
									+ "{\"column\":\"Y2ZzOnNhbGFyeQ==\",\"$\":\"" + row2Salary + "\"},"
									+ "{\"column\":\"Y2ZzOmdlbmRlcg==\",\"$\":\"" + row2Gender + "\"},"
									+ "{\"column\":\"Y2ZzOmFkZHJlc3M=\",\"$\":\"" + row2Address + "\"}"
									+ " ]} ]}'";
			String insertRow2FullCmd6 = curlKerberizedWebHBaseCmd6_L + insertRow2Cmd6_M + insertRow2Cmd6_R;
			//System.out.println(" *-* insertRow2FullCmd6: \n	" + insertRow2FullCmd6);
			
			
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
			
			String insertRow3Cmd6_M =  " -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/" + Base64Str.getDecodedString(row3Key);
			
			String insertRow3Cmd6_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row3Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row3FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row3LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row3Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row3Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row3Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow3FullCmd6 = curlKerberizedWebHBaseCmd6_L + insertRow3Cmd6_M + insertRow3Cmd6_R;
			//System.out.println(" *-* insertRow3FullCmd6: \n	" + insertRow3FullCmd6);
			
			
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
			
			String insertRows4N5Cmd6_M =  " -H \"Accept: application/json\" -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/false-row-key";
			
			String insertRows4N5Cmd6_R = " -d '{\"Row\":["
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
			String insertRows4N5FullCmd6 = curlKerberizedWebHBaseCmd6_L + insertRows4N5Cmd6_M + insertRows4N5Cmd6_R;
			//System.out.println(" *-* insertRows4N5FullCmd6: \n	" + insertRows4N5FullCmd6);
					
					
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
			
			String insertRow6Cmd6_M =  " -H \"Accept: text/xml\" -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/false-row-key";
			
			String insertRow6Cmd6_R = " -d '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CellSet>"
									+ "<Row key=\"" + row6Key + "\">"
									+ "<Cell column=\"Y2ZzOmZpcnN0TmFtZQ==\" >" + row6FirstName + "</Cell>"
									+ "<Cell column=\"Y2ZzOmxhc3ROYW1l\" >" + row6LastName + "</Cell>"
									+ "<Cell column=\"Y2ZzOnNhbGFyeQ==\" >" + row6Salary + "</Cell>"
									+ "<Cell column=\"Y2ZzOmdlbmRlcg==\" >" + row6Gender + "</Cell>"
									+ "<Cell column=\"Y2ZzOmFkZHJlc3M=\" >" + row6Address + "</Cell>"
									+ "</Row></CellSet>'";
			String insertRow6FullCmd6 = curlKerberizedWebHBaseCmd6_L + insertRow6Cmd6_M + insertRow6Cmd6_R;
			System.out.println(" *-* insertRow6FullCmd6: \n	" + insertRow6FullCmd6);
			
			
			//(3)d Create WebHBase commands for querying HBase Data 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			String queryHBaseTableCellCmd6_M =  " " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/101/cfs:firstName | awk '{print $0\"\"}' > " + tempLocalWebHBaseTestResultPathAndName;
					
			String getRow2LastNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M		
					+ "/102/cfs:lastName | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow3SalaryCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/103/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow4SalaryCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/104/cfs:salary | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow5GenderCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/105/cfs:gender | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String getRow6AddressCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/106/cfs:address | awk '{print $0\"\"}' >> " + tempLocalWebHBaseTestResultPathAndName;
			
			String transformLocalTempFileIntoRecordFileCmd6 = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd6 = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd6 = "rm -f " + localWebHBaseTestResultPathAndName;
			
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
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd6 + ";\n");
			sb.append(createNewHBaseTableFullCmd6 + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd6 + ";\n");		
			sb.append(insertRow1LasttNameCellFullCmd6 + ";\n");
			sb.append(insertRow1SalaryCellFullCmd6 + ";\n");
			sb.append(insertRow1GenderCellFullCmd6 + ";\n");
			sb.append(insertRow1AddressCellFullCmd6 + ";\n");		
			sb.append(insertRow2FullCmd6 + ";\n");
			sb.append(insertRow3FullCmd6 + ";\n");
			sb.append(insertRows4N5FullCmd6 + ";\n");
			sb.append(insertRow6FullCmd6 + ";\n");
		
			sb.append(getRow1FirstNameCellFullCmd6 + ";\n");
			sb.append(getRow2LastNameCellFullCmd6 + ";\n");
			sb.append(getRow3SalaryCellFullCmd6 + ";\n");		
			sb.append(getRow4SalaryCellFullCmd6 + ";\n");
			sb.append(getRow5GenderCellFullCmd6 + ";\n");
			sb.append(getRow6AddressCellFullCmd6 + ";\n");		
			sb.append(transformLocalTempFileIntoRecordFileCmd6 + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd6 + ";\n"); 
			
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
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd6 + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxSecureWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_Secure_WebHBaseScriptFile_Curl" + (i+1) + ".sh";			
			prepareFile (knoxSecureWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
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
			
			String testRecordInfo5 = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo5 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
						+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo5 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Cell, Row & Multiple Row) (Octet, Json & Xml), "
	        			+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo5, true);	
			prevTime = currTime;
		}//end for of 7.2
		
		
		
		//#8.1 & #8.2 are test scenario types for WebHbase testing with large JSON and XML data for single HBase cell
		//8.1 Login to Current Hadoop Cluster Knox Server and Perform WebHbase operation 
		//    via cURL cmds and WebHbase HTTP URL(s)
		String fileName = localKnoxLargeWebHbaseJsonDataFileName;
		String[] fileNameSplit = fileName.split("_");
		int fileNameSplitLength = fileNameSplit.length;
		String fileSizeInKB = fileNameSplit[fileNameSplitLength-1].replace(".txt", "").toLowerCase().replace("kb", "");
		System.out.println(" *** fileSizeInKB: " + fileSizeInKB);
				
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[4]. Knox & WebHBase (Stargate HBase) - Large-Size (" + fileSizeInKB + " KB) Data Insert Per Cell \n", true);
		//writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[4]. Knox & WebHBase (Stargate HBase) - Large-Size Data Insert Per Cell \n", true);
		
		int webHbaseRestServiceNumber2 = 2; //1..2
		int webHbaseService_Start2 = 0; //0..1
		
		for (int i = webHbaseService_Start2; i < webHbaseRestServiceNumber2; i++){
			//(1) Get active WebHbase HTTP URL:
			//tempClock = new DayClock();				
			//String tempTime5_start = tempClock.getCurrentDateTime();
			
			totalTestScenarioNumber++;
			String activeWebHbaseHttpURL = "";
			if (i==0){
				activeWebHbaseHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("/webhdfs", "/").replace("mn02.", "mn01.");
			}
			if (i==1){
				activeWebHbaseHttpURL = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("/webhdfs", "/").replace("mn01.", "mn02.");
			}
			//currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("mn01.", "mn02.").replace("/webhdfs", "/");
			//System.out.println(" *** activeWebHbaseHttpURL: " + activeWebHbaseHttpURL);
			
			//(2) Generate WebHbase cmds:
			String enServerWebHBaseLargeTestJsonDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseJsonDataFileName;
			String enServerWebHBaseLargeTestXmlDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseXmlDataFileName;
			
			//Sample: curl -i --negotiate -u :  -L "http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/exists";;
			String curlKerberizedWebHBaseCmd5_L = "curl -s --negotiate -u : "; //-i -v ==> -s
			//String webHBaseTableName = "employee_webhbase1";	
			String webHBaseTableName = "largeInsert_webhbase" + (i+1);	
			String webHBaseTableURL = activeWebHbaseHttpURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			//String webHBaseTestResultFileName = "employee_webhbase_curl_result.txt";
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result.txt";
			
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd5_R = " -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd5 = curlKerberizedWebHBaseCmd5_L + deleteExistingHBaseTableCmd5_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd5: \n" + deleteExistingHBaseTableFullCmd5);
					
			String createNewHBaseTableCmd5_R = " -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd5 = curlKerberizedWebHBaseCmd5_L + createNewHBaseTableCmd5_R;
			//System.out.println(" *** createNewHBaseTableFullCmd5: \n" + createNewHBaseTableFullCmd5);
			
			//(2)a Create WebHBase commands for single cell insertion, querying and deletion for the first row (rowkey == 101)/JSON
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			String insertRow1CellCmd5_M =  " -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/101";
			
			String insertRow1FirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellCmd5_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestJsonDataFilePathAndName;
						
			String queryHBaseTableCellCmd5_M =  " " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 > " + tempLocalWebHBaseTestResultPathAndName;
			
			//curl -s --negotiate -u : -X DELETE http://hdpr03mn02.mayo.edu:8084/largeInsert_webhbase1/101/cfs:firstName 		
			String deleteRow1FirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + " -X DELETE " + webHBaseTableURL + "/101/cfs:firstName";	
						
			
			//(2)b Create WebHBase commands for single cell insertion and querying for the first row (rowkey == 101)/XML		 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			
			String insertRow1CellSecondCmd5_M =  " -H \"Accept: text/xml\"  -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/101";			
			
			String insertRow1FirstNameCellSecondFullCmd5 = curlKerberizedWebHBaseCmd5_L 
					+ insertRow1CellSecondCmd5_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestXmlDataFilePathAndName;
			
			String getRow1SecondFirstNameCellFullCmd5 = curlKerberizedWebHBaseCmd5_L  + queryHBaseTableCellCmd5_M		
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 >> " + tempLocalWebHBaseTestResultPathAndName;
				
			//(2)c Actions of post WebHbase Ops 
			String transformLocalTempFileIntoRecordFileCmd5 = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd5 = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd5 = "rm -f " + localWebHBaseTestResultPathAndName;
			
	
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
				    	    
		    sb.append(deleteExistingHBaseTableFullCmd5 + ";\n");
			sb.append(createNewHBaseTableFullCmd5 + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd5 + ";\n");	
			sb.append(getRow1FirstNameCellFullCmd5 + ";\n");
			
			sb.append(deleteRow1FirstNameCellFullCmd5 + ";\n");
			
			sb.append(insertRow1FirstNameCellSecondFullCmd5 + ";\n");			
			sb.append(getRow1SecondFirstNameCellFullCmd5 + ";\n");
					
			sb.append(transformLocalTempFileIntoRecordFileCmd5 + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd5 + ";\n"); 
			
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
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd5 + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_WebHBaseLargeDataCellInsertScriptFile_Curl" + (i+1) + ".sh";			
			prepareFile (knoxWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(4)
			boolean currTestScenarioSuccessStatus5 = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("1441978062896,1441978062896")) {
						currTestScenarioSuccessStatus5 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo5 = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo5 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) WebHBase Rest API Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
						+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo5 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) WebHBase Rest API Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
	        			+ "\n          and Querying via WebHBase Rest (Stargate HBase) HTTP URL - " + activeWebHbaseHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo5, true);	
			prevTime = currTime;
		}//end for of 8.1
		
		
		//8.2 Login to Current Hadoop Cluster Knox Server and Perform Secure WebHbase operation 
		//    via cURL cmds and WebHbase HTTP URL determined by name node status
		int secureWebHbaseThroughKnoxServiceNumber2 = 1; //1..2
		int secureWebHbaseThroughKnoxService_Start2 = 0; //0..1
		
		//String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		if (currClusterKnoxNode2Name.isEmpty()){
			secureWebHbaseThroughKnoxServiceNumber2 += 1;
		}			
		
		for (int i = secureWebHbaseThroughKnoxService_Start2; i < secureWebHbaseThroughKnoxServiceNumber2; i++){
			//(1) Get active WebHbase HTTPS URL:
			//tempClock = new DayClock();				
			//String tempTime5_start = tempClock.getCurrentDateTime();
			//String currClusterKnoxWebHBaseHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/webhdfs";
			
			totalTestScenarioNumber++;
			String currClusterKnoxWebHBaseHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName;
			if (i==0){
				currClusterKnoxWebHBaseHttpsURL += "/hbase/";
			}
			if (i==1){				
				currClusterKnoxWebHBaseHttpsURL += "_bkp/hbase/";
			}
			//currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":8084").replace("mn01.", "mn02.").replace("/webhdfs", "/");
			//System.out.println(" *** currClusterKnoxWebHBaseHttpsURL: " + currClusterKnoxWebHBaseHttpsURL);
			
			//(2) Generate WebHbase cmds:
			String enServerWebHBaseLargeTestJsonDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseJsonDataFileName;
			String enServerWebHBaseLargeTestXmlDataFilePathAndName = enServerScriptFileDirectory + localKnoxLargeWebHbaseXmlDataFileName;
						
			String curlKerberizedWebHBaseCmd6_L = "curl -k -u " + loginUserName + ":" + loginUserADPassWd;
			String webHBaseTableName = "largeInsert_knox_webhbase" + (i+1); 		
			String webHBaseTableURL = currClusterKnoxWebHBaseHttpsURL + webHBaseTableName;
			String webHBaseTableSchemaURL = webHBaseTableURL + "/schema";
			
			String webHBaseTestResultFileName = webHBaseTableName + "_curl_result.txt";
			String localWebHBaseTestResultPathAndName = enServerScriptFileDirectory + webHBaseTestResultFileName;
			String webHBaseTestResultHdfsPathAndName = knoxTestFolderName + webHBaseTestResultFileName;
			
			String deleteExistingHBaseTableCmd6_R = " -X DELETE " + webHBaseTableSchemaURL;
			String deleteExistingHBaseTableFullCmd6 = curlKerberizedWebHBaseCmd6_L + deleteExistingHBaseTableCmd6_R;		
			//System.out.println(" *** deleteExistingHBaseTableFullCmd6: \n" + deleteExistingHBaseTableFullCmd6);
					
			String createNewHBaseTableCmd6_R = " -H \"Accept: application/json\"  -H \"Content-Type: application/json\""
					+ " " + webHBaseTableSchemaURL
					+ " -d '{\"name\":\"" + webHBaseTableName + "\","
					+ " \"ColumnSchema\":["
					+ " {\"name\":\"cfs\", \"VERSIONS\":\"5\"}]}'"
					;
			String createNewHBaseTableFullCmd6 = curlKerberizedWebHBaseCmd6_L + createNewHBaseTableCmd6_R;
			//System.out.println(" *** createNewHBaseTableFullCmd6: \n" + createNewHBaseTableFullCmd6);
			
			//(2)a Create WebHBase commands for single cell insertion, querying and deletion for the first row (rowkey == 101)/JSON
			String tempLocalWebHBaseTestResultPathAndName = localWebHBaseTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			String insertRow1CellCmd6_M =  " -H \"Content-Type: application/json\""
					+ " -X POST " + webHBaseTableURL + "/101";
			
			String insertRow1FirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellCmd6_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestJsonDataFilePathAndName;
						
			String queryHBaseTableCellCmd6_M =  " " + webHBaseTableURL ;
			
			String getRow1FirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 > " + tempLocalWebHBaseTestResultPathAndName;
			
			//curl -s --negotiate -u : -X DELETE http://hdpr03mn02.mayo.edu:8084/largeInsert_webhbase1/101/cfs:firstName 		
			String deleteRow1FirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + " -X DELETE " + webHBaseTableURL + "/101/cfs:firstName";	
						
			
			//(2)b Create WebHBase commands for single cell insertion and querying for the first row (rowkey == 101)/XML		 		
			//Example: curl -s --negotiate -u : http://hdpr03mn02.mayo.edu:8084/employee_webhbase1/101/cfs:firstName | awk '{print $0""}' > temp1.txt;
			
			String insertRow1CellSecondCmd6_M =  " -H \"Accept: text/xml\"  -H \"Content-Type: text/xml\""
					+ " -X POST " + webHBaseTableURL + "/101";			
			
			String insertRow1FirstNameCellSecondFullCmd6 = curlKerberizedWebHBaseCmd6_L 
					+ insertRow1CellSecondCmd6_M + "/cfs:firstName"
					+ " -d @" + enServerWebHBaseLargeTestXmlDataFilePathAndName;
			
			String getRow1SecondFirstNameCellFullCmd6 = curlKerberizedWebHBaseCmd6_L  + queryHBaseTableCellCmd6_M		
					+ "/101/cfs:firstName | cut -d'=' -f3 | cut -d '\"' -f2 >> " + tempLocalWebHBaseTestResultPathAndName;
				
			
			//(2)c Actions of post WebHbase Ops 			
			String transformLocalTempFileIntoRecordFileCmd6 = "{ cat " + tempLocalWebHBaseTestResultPathAndName + " | tr '\\n' ',' ; } > " + localWebHBaseTestResultPathAndName;
			String removeTempLocalWebHbaseTestResultsFileCmd6 = "rm -f " + tempLocalWebHBaseTestResultPathAndName;
			String removeLocalWebHbaseTestResultsFileCmd6 = "rm -f " + localWebHBaseTestResultPathAndName;
			
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
				    	    
		    //sb.append(deleteExistingHBaseTableFullCmd6 + ";\n");
			//sb.append(createNewHBaseTableFullCmd6 + ";\n");		
			//sb.append(insertRow1FirstNameCellFullCmd6 + ";\n");		
			
			sb.append(deleteExistingHBaseTableFullCmd6 + ";\n");
			sb.append(createNewHBaseTableFullCmd6 + ";\n");		
			sb.append(insertRow1FirstNameCellFullCmd6 + ";\n");	
			sb.append(getRow1FirstNameCellFullCmd6 + ";\n");
			
			sb.append(deleteRow1FirstNameCellFullCmd6 + ";\n");
			
			sb.append(insertRow1FirstNameCellSecondFullCmd6 + ";\n");			
			sb.append(getRow1SecondFirstNameCellFullCmd6 + ";\n");
					
			sb.append(transformLocalTempFileIntoRecordFileCmd6 + ";\n");
			sb.append(removeTempLocalWebHbaseTestResultsFileCmd6 + ";\n");
					 
			
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
			
			sb.append(removeLocalWebHbaseTestResultsFileCmd6 + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    
			String knoxSecureWebHBaseScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_Secure_WebHBaseLargeDataCellInsertScriptFile_Curl" + (i+1) + ".sh";			
			prepareFile (knoxSecureWebHBaseScriptFullFilePathAndName,  "Script File For Testing Knox WebHBase on '" + bdClusterName + "' Knox Node - " + currClusterKnoxNodeName);
			
			String webHBaseTestingCmds = sb.toString();
			writeDataToAFile(knoxSecureWebHBaseScriptFullFilePathAndName, webHBaseTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxWebHBaseScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxSecureWebHBaseScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//(4)
			boolean currTestScenarioSuccessStatus5 = false;
			Path filePath5 = new Path(webHBaseTestResultHdfsPathAndName);
			if (currHadoopFS.exists(filePath5)) {
				hdfsFilePathAndNameList.add(webHBaseTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath5);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));					
				String line = "";
				while ((line = br.readLine()) != null) {
					//System.out.println("*** line: " + line );
					if (line.contains("1441978062896,1441978062896")) {
						currTestScenarioSuccessStatus5 = true;				
					}	
												
				}//end while
				br.close();	
	        }//end outer if	
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo5 = "";
			if (currTestScenarioSuccessStatus5) {
				successTestScenarioNum++;			
				testRecordInfo5 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
						+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        } else {
	        	testRecordInfo5 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) Knox/WebHBase Table Deleting, Creating, Inserting (Single Cell - Large-Size Data in Json & Xml), "
	        			+ "\n          and Querying via Knox/WebHBase Rest (Stargate HBase) HTTPS URL - " + currClusterKnoxWebHBaseHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + webHBaseTestResultHdfsPathAndName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo5, true);
			prevTime = currTime;
		}//end for of 8.2
		
		Thread.sleep(5*1000);
		
		//#9 Test scenario type for Hive JDBC Via Knox testing
		//Perform Secure Hive JDBC Operation via remote Knox-Hive JDBC Operations 
		//  due to the disabling of regular hive JDBC operations  		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[5]. Hive JDBC via Knox \n", true);
		
		String currKnoxNodeFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
		
		int secureHiveJDBCViaKnoxServiceNumber = 2; //1..2
		int secureHiveJDBCViaKnoxService_Start = 0; //0..1	
		
		//String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		if (!currClusterKnoxNode2Name.isEmpty()){
			secureHiveJDBCViaKnoxServiceNumber = 1;
		}
				
		for (int i = secureHiveJDBCViaKnoxService_Start; i < secureHiveJDBCViaKnoxServiceNumber; i++){
			totalTestScenarioNumber++;
			//(1) Preparation
			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
			final String tableName = "employee_e1";				
			
			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
	  		hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
	  		
	  		Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
	  		if (currHadoopFS.exists(outputPath)) {
	  			currHadoopFS.delete(outputPath, false);
	  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
	          }
	  		
	  		String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
	  		
	  		moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);		
	  						
	  		tempClock = new DayClock();				
	  		tempTime = tempClock.getCurrentDateTime();		
	  		System.out.println("\n*** Done - Generated Hive-Managed Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
	  						+ hdfsHiveDefaultTestDataFilePathAndName + " at the time - " + tempTime); 	

			//(2) Get active WebHbase HTTPS URL:
			//tempClock = new DayClock();				
			//String tempTime6_start = tempClock.getCurrentDateTime();
			
	  		String bdClusterKnoxIdName_4Hive = bdClusterKnoxIdName;
	  		
			if (i==1){				
				bdClusterKnoxIdName_4Hive += "_bkp";
			}
			
			int currBdClusterGatewayPortNum = 8442;
			String hiveDBName = "default";

			final HiveViaKnoxConnectionFactory aHiveViaKnoxConnFactory = 
					new HiveViaKnoxConnectionFactory(currKnoxNodeFQDN, currBdClusterGatewayPortNum, 
							hiveDBName, localKnoxTrustStorePathAndName, localKnoxTrustStorePassWd,
							bdClusterKnoxIdName_4Hive, loginUserName, loginUserADPassWd);
			
			String knoxHiveContextPath = "gateway/" + bdClusterKnoxIdName_4Hive + "/hive";
			
			//(3) Hive JDBC Via Knox Testing
			Thread thread = new Thread(new Runnable() {
			    public void run() {
			    	int totalTestCaseNumber = 0;
				  	double successTestCaseNum = 0L;
				  			  	
				  	StringBuilder sb = new StringBuilder();
			    	
				  	try {		    		
			  	  		Connection conn = aHiveViaKnoxConnFactory.getConnection();     	
			  	  		Statement stmt = conn.createStatement();
			  	  					
			  			//1).drop an existing Hive table
			  			totalTestCaseNumber++;
			  			String dropTblSqlStr = "drop table " + tableName;
			  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
			  			if (exitValue == 0){
			  				successTestCaseNum ++;
			  				sb.append("    *** Success - Dropping Hive table - " + tableName + "\n");			
			  			} else {
			  				sb.append("    -*- 'Failed' - Dropping Hive table - " + tableName + "\n");
			  			}
			  			
			  			//2). create a new Hive-managed table
			  			totalTestCaseNumber++;
			  		    String createTblSqlStr = "create table " + tableName 
			  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
			  		      		+ "Row format delimited fields terminated by ',' ";
			  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
			  		    if (exitValue == 0){
			  				successTestCaseNum ++;
			  				sb.append("    *** Success - Creating Hive table - " + tableName + "\n");	
			  		    } else {
			  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + tableName + "\n");
			  			}   
			  		    
			  		    //3). load the Hive-managed table by overwriting  
			  		    totalTestCaseNumber++;
			  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + tableName ;
			  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
			  		  	if (exitValue == 0){
			  				successTestCaseNum ++;
			  				sb.append("    *** Success - Loading Data Into Hive table - " + tableName + "\n");
			  		  	} else {
			  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + tableName + "\n");
			  			}    
			  	      		    	  	
			  		    //4).HQuery (row-counting) the above-generated Hive-managed table
			  	  		totalTestCaseNumber++;
			  		    String queryTblSqlStr = "select count(1) from " + tableName;  //count(*)...count(1)...*
			  		    boolean countingQueryStatus = false;
			  	  		ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
			  	  		String line = "";
			  	  		if (rs != null){
			  	  			while (rs.next()) {
			  	  	  			line = rs.getString(1);
			  	  				System.out.println(line);
			  	  				if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
			  	  					successTestCaseNum ++;
			  	  					countingQueryStatus = true;
			  	  				    tableRowCount = 6;
			  	  					break;
			  	  				}
			  	  	  		}
			  	  			rs.close();
			  	  		}  		  		
			  	  		if (countingQueryStatus == true){
			  	  			sb.append("    *** Success - Querying/Counting Hive table - " + tableName + "\n");
			  	  		} else {
			  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + tableName + "\n");
			  	  		}
			  	  		stmt.close();
			  	  	    conn.close();
			  	  	    
			  	  	    	
			  	  		sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
			  	  		sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
			  	  		currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
			  			
			  	  	    NumberFormat df = new DecimalFormat("#0.00");
			  			sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
			  	  		
			  			currScenarioDetailedTestingRecordInfo = sb.toString();		  			
			  			sb.setLength(0);
			  	  	} catch (SQLException e) {		
			  			e.printStackTrace();
			  		} catch (ClassNotFoundException e) {		
			  			e.printStackTrace();
			  		}//end try
			    }
			});
					
			thread.start();
			try {
				thread.join(20000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
			if (thread.isAlive()) {
			    thread.stop();//.stop();
			}
			Thread.sleep(1*5*1000);	
	  	  	
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime); 
			
			//System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
			//System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
			
	  		String testRecordInfo6 = "";	  		
	  		if (currScenarioSuccessRate == 1){
	  			successTestScenarioNum++;
	  			testRecordInfo6 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
	  					+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + tableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
	  		} else if (currScenarioSuccessRate == 0){
	  			testRecordInfo6 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
	  					+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + tableName + "'\n";	 	 
	  		} else {
	  			successTestScenarioNum += currScenarioSuccessRate;
	  			testRecordInfo6 = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
	  					+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
	  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
	  		}
	  		sb.setLength(0);
	  		writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo6, true);
	  		prevTime = currTime;
		}//end for of 9
		
	

		
		//10. Run oozie workflow job that manages the execution of a MapReduce program - word counting by Oozie Server or Knox Gateway Rest API		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[6]. Knox-Controlled Oozie Workflow MapReduce Action  \n", true);				
		//10.1 Loop Through the oozie server rest api to run Oozie MapReduce Action
		int oozieRestServiceNumber = 2; //1..2
		int oozieService_Start = 0; //0..1
		
		//oozieService_Start = 1;
		//oozieRestServiceNumber = 1; //1 ..2;
		for (int i = oozieService_Start; i < oozieRestServiceNumber; i++){			
			//(1) Get Knox-Gateway Oozie  HTTPs URL:		
			totalTestScenarioNumber++;
			String oozieServerHttpHttpURL = "";
			
			if (i==0){
				BdNode mn1BDNode = new BdNode("mn01".toUpperCase(), bdClusterName);
				ULServerCommandFactory mn1CmdFactory = mn1BDNode.getBdENCmdFactory();			
				System.out.println(" *** mn1CmdFactory.getServerURI(): " + mn1CmdFactory.getServerURI());
				
				oozieServerHttpHttpURL = "http://" + mn1CmdFactory.getServerURI() + ":11000";
			}
			if (i==1){
				BdNode mn2BDNode = new BdNode("mn02".toUpperCase(), bdClusterName);
				ULServerCommandFactory mn2CmdFactory = mn2BDNode.getBdENCmdFactory();			
				System.out.println(" *** mn2CmdFactory.getServerURI(): " + mn2CmdFactory.getServerURI());
				
				oozieServerHttpHttpURL = "http://" + mn2CmdFactory.getServerURI() + ":11000";
			}
			
			//(2) Testing Oozie Function Using Word-counting Java Program
			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;			
			String hdfsOozieTestDataFilePathAndName = knoxOozieMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory_oozieKnox + localKnoxOozieJarFileName;
			String hdfsOozieTestJarFilePathAndName = knoxOozieMRActionLibFolder + localKnoxOozieJarFileName; 
			
			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory_oozieKnox + localKnoxOozieWorkflowFileName;
			String hdfsOozieWorkflowFilePathAndName = knoxTestFolderName_Full + "oozie/" + localKnoxOozieWorkflowFileName; 
			
			String enServerOozieKnoxWorkflowConfigFileFullPathAndName  = enServerScriptFileDirectory_oozieKnox + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
			
			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn01.mayo.edu:11000/oozie/v1/jobs?action=start'
			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn02.mayo.edu:11000/oozie/v1/jobs?action=start'
			String curlOozieRestApiJobSubmissionCmd_L = "curl -i -k -u : --negotiate -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
			//activeNN_addr_port: hdfs://hdpr03mn01.mayo.edu:8020 --> http://hdpr03mn02.mayo.edu:11000
			//String oozieServerHttpHttpURL = activeNN_addr_port.replace(":8020", ":11000").replace("hdfs:", "http:");				
			
			String curlRestApiJobSubmissionCmd_R = "-X POST -L '" + oozieServerHttpHttpURL + "/oozie/v1/jobs?action=start'";
			String runOozieWrodCountJarCmd = curlOozieRestApiJobSubmissionCmd_L + " " + curlRestApiJobSubmissionCmd_R + " & sleep 55";
			
			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/oozie/v1/jobs?action=start'
			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1_bkp/oozie/v1/jobs?action=start'
			//String curlKnoxOozieJobSubmissionCmd_L = "curl -i -k -u " + loginCredential_AD + " -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
			//String curlKnoxOozieJobSubmissionCmd_R = "-X POST -L '" + "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/oozie/v1/jobs?action=start'";
			//String runOozieWrodCountJarCmd = curlKnoxOozieJobSubmissionCmd_L + " " + curlKnoxOozieJobSubmissionCmd_R + " & sleep 60";
			
			//String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieKnoxWorkflowConfigFileFullPathAndName + " -run & sleep 45";
			
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory_oozieKnox + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory_oozieKnox + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory_oozieKnox + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory_oozieKnox + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionOutputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionLibFolder + "; \n");
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionOutputFolder + "; \n");			
			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  knoxTestFolderName_Full + "oozie" + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + knoxTestFolderName_Full + "; \n");
		    sb.append("hadoop fs -chmod -R 755 " + knoxTestFolderName_Full + "oozie" + "; \n");
		    
		       
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runOozieWrodCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + knoxTestFolderName_Full + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localOozieKnoxMRActionWordCountTestingScriptFilePathAndName = scriptFilesFoder + "dcTestOozie_MRActionWordCountingScriptFile_Curl_No"+ (i+1) + ".sh";			
			prepareFile (localOozieKnoxMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Knox Node - " + currClusterKnoxNodeName);
			
			String oozieWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
			//String enServerScriptFileDirectory_short = "/data/home/" + loginUserName + "/test/";	
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, 
						scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsOozieOutPutFilePathAndName = knoxOozieMRActionOutputFolder + "part-r-00000";	
			
			System.out.println("*** hdfsOozieOutPutFilePathAndName is: " + hdfsOozieOutPutFilePathAndName);
			
			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
			
			boolean presentTestScenarioSuccessStatus = false;
			if (currHadoopFS.exists(oozieOutPutHdfsFilePath)) {
				System.out.println("\n***  Existing file : " + oozieOutPutHdfsFilePath);
				hdfsFilePathAndNameList.add(hdfsOozieOutPutFilePathAndName);
				
				FileStatus[] status = currHadoopFS.listStatus(oozieOutPutHdfsFilePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				int totalWordCount = 0;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					line = line.replaceAll("\\s", "===");
					//System.out.println(" --1--  line: " + line);
					
					if (line.contains("===")) {						
						String[] lineSplit = line.split("===");
						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
					}		
												
				}//end while
				br.close();
				System.out.println(" *** totalWordCount == " + totalWordCount);	
				
				if (totalWordCount == 36){
					presentTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** presentTestScenarioSuccessStatus: " + presentTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String presentTestRecordInfo = "";	
			if (presentTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				presentTestRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				       + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder.replace(activeNN_addr_port, "") + "'\n";				        
			} else {
				presentTestRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				       + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder.replace(activeNN_addr_port, "") + "'\n";
			}
			writeDataToAFile(dcTestKnox_RecFilePathAndName, presentTestRecordInfo, true);
			prevTime = currTime;
		}//end 10.1
		
		
		//10.2 Loop Through the Knox Gateway Rest API to Run Oozie MapReduce Action Job
		int knox_oozieRestServiceNumber = 1; //1..2
		if (currClusterKnoxNode2Name.isEmpty()){
			knox_oozieRestServiceNumber += 1;
		}
		
		int knox_oozieService_Start = 0; //0..1
		//oozieService_Start = 1;
		//knox_oozieRestServiceNumber = 1; //1 ..2;
		for (int i = knox_oozieService_Start; i < knox_oozieRestServiceNumber; i++){			
			//(1) Get Oozie Server HTTP URL:	 HTTP URL:		
			totalTestScenarioNumber++;
			String oozieKnoxGatewayHttpsURL = "";
			
			if (i==0){
				oozieKnoxGatewayHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName;
			}
			if (i==1){
				oozieKnoxGatewayHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "_bkp";
			}
			
			 
			//(2) Testing Oozie Function Using Word-counting Java Program
			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;			
			String hdfsOozieTestDataFilePathAndName = knoxOozieMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory_oozieKnox + localKnoxOozieJarFileName;
			String hdfsOozieTestJarFilePathAndName = knoxOozieMRActionLibFolder + localKnoxOozieJarFileName; 
			
			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory_oozieKnox + localKnoxOozieWorkflowFileName;
			String hdfsOozieWorkflowFilePathAndName = knoxTestFolderName_Full + "oozie/" + localKnoxOozieWorkflowFileName; 
			
			String enServerOozieKnoxWorkflowConfigFileFullPathAndName  = enServerScriptFileDirectory_oozieKnox + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
			
			////curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn01.mayo.edu:11000/oozie/v1/jobs?action=start'
			////curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn02.mayo.edu:11000/oozie/v1/jobs?action=start'
			//String curlOozieRestApiJobSubmissionCmd_L = "curl -i -k -u : --negotiate -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
			////activeNN_addr_port: hdfs://hdpr03mn01.mayo.edu:8020 --> http://hdpr03mn02.mayo.edu:11000
			//String oozieServerHttpHttpURL = activeNN_addr_port.replace(":8020", ":11000").replace("hdfs:", "http:");				
			//String curlRestApiJobSubmissionCmd_R = "-X POST -L '" + oozieServerHttpHttpURL + "/oozie/v1/jobs?action=start'";
			//String runOozieWrodCountJarCmd = curlOozieRestApiJobSubmissionCmd_L + " " + curlRestApiJobSubmissionCmd_R + " & sleep 55";
			
			////curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/oozie/v1/jobs?action=start'
			////curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1_bkp/oozie/v1/jobs?action=start'
			String curlKnoxOozieJobSubmissionCmd_L = "curl -i -k -u " + loginCredential_AD + " -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
			String curlKnoxOozieJobSubmissionCmd_R = "-X POST -L '" + oozieKnoxGatewayHttpsURL + "/oozie/v1/jobs?action=start'";
			String runOozieWrodCountJarCmd = curlKnoxOozieJobSubmissionCmd_L + " " + curlKnoxOozieJobSubmissionCmd_R + " & sleep 65";
			
			//String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieKnoxWorkflowConfigFileFullPathAndName + " -run & sleep 45";
			
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory_oozieKnox + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory_oozieKnox + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory_oozieKnox + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory_oozieKnox + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionOutputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionLibFolder + "; \n");
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionOutputFolder + "; \n");			
			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  knoxTestFolderName_Full + "oozie" + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + knoxTestFolderName_Full + "; \n");
		    sb.append("hadoop fs -chmod -R 755 " + knoxTestFolderName_Full + "oozie" + "; \n");
		    
		       
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runOozieWrodCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + knoxTestFolderName_Full + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localOozieKnoxMRActionWordCountTestingScriptFilePathAndName = scriptFilesFoder + "dcTestOozie_Secure_MRActionWordCountingScriptFile_Curl_No"+ (i+1) + ".sh";			
			prepareFile (localOozieKnoxMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Knox Node - " + currClusterKnoxNodeName);
			
			String oozieWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
			//String enServerScriptFileDirectory_short = "/data/home/" + loginUserName + "/test/";	
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, 
						scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsOozieOutPutFilePathAndName = knoxOozieMRActionOutputFolder + "part-r-00000";	
			
			System.out.println("*** hdfsOozieOutPutFilePathAndName is: " + hdfsOozieOutPutFilePathAndName);
			
			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
			
			boolean presentTestScenarioSuccessStatus = false;
			if (currHadoopFS.exists(oozieOutPutHdfsFilePath)) {
				System.out.println("\n***  Existing file : " + oozieOutPutHdfsFilePath);
				hdfsFilePathAndNameList.add(hdfsOozieOutPutFilePathAndName);
				
				FileStatus[] status = currHadoopFS.listStatus(oozieOutPutHdfsFilePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				int totalWordCount = 0;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					line = line.replaceAll("\\s", "===");
					//System.out.println(" --1--  line: " + line);
					
					if (line.contains("===")) {						
						String[] lineSplit = line.split("===");
						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
					}		
												
				}//end while
				br.close();
				System.out.println(" *** totalWordCount == " + totalWordCount);	
				
				if (totalWordCount == 36){
					presentTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** presentTestScenarioSuccessStatus: " + presentTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String presentTestRecordInfo = "";	
			if (presentTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				presentTestRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Knox/Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
						+ "\n          via Knox Gateway HTTPS URL - " + oozieKnoxGatewayHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				       + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder.replace(activeNN_addr_port, "") + "'\n";				        
			} else {
				presentTestRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Knox/Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
						+ "\n          via Knox Gateway HTTPS URL - " + oozieKnoxGatewayHttpsURL
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				       + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder.replace(activeNN_addr_port, "") + "'\n";
			}
			writeDataToAFile(dcTestKnox_RecFilePathAndName, presentTestRecordInfo, true);
			prevTime = currTime;
		}//end 10.2
		
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Knox WebHDFS (cURL), WebHCat.... on '" + bdClusterName + "' Cluster from Knox Server node - " + currClusterKnoxNodeName + "!";
		currNotingMsg += "\n***** Done - Present Knox Testing Generated HDFS/WebHDFS Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Knox Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Knox Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Knox Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, currNotingMsg, true);
		
		if (currClusterKnoxNode2Name.isEmpty()){
			Desktop.getDesktop().open(new File(dcTestKnox_RecFilePathAndName));
		}		
	
	}//end run()
	

	private static void updateLocalOozieKnoxWorkflowConfigXmlFile (String localOozieWorflowXmlFileFullPathAndName, String nameNodeStr, String jobTrackerStr, String userName){
		ArrayList<String> newOozieMRJobPropLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localOozieWorflowXmlFileFullPathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("<value>") && line.contains("hdfs://")){
					line = "\t\t<value>" + nameNodeStr + "</value>";
				}				
				
				if (line.contains("<value>") && line.contains(":8050")){
					line = "\t\t<value>" + jobTrackerStr + "</value>";					
				}	
				if (line.contains("<value>") && line.contains("m041785")){
					line = "\t\t<value>" + userName + "</value>";					
				}		
				newOozieMRJobPropLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localOozieWorflowXmlFileFullPathAndName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);	
			for (int j = 0; j < newOozieMRJobPropLineList.size(); j++){
				String tempLine = newOozieMRJobPropLineList.get(j);
				output.println(tempLine);
				System.out.println(tempLine);;
			}//end for
			output.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Updating Local Oozie ... Action Knox Workflow Configuration XML File!!!\n");
		
	}//end updateLocalOozieKnoxWorkflowConfigXmlFile
	
	
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
