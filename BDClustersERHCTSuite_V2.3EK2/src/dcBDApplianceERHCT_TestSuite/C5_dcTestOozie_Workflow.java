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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.io.Files;

import dcModelClasses.DayClock;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 06/29/2014; 7/5~6/2016
*/ 

@SuppressWarnings("unused")
public class C5_dcTestOozie_Workflow {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localOozieTestDataFileName = "";
	private static String oozieTestFolderName = "";
	private static String localOozieJarFileName = "";
	private static String internalKinitCmdStr = "";
	
	private static String localOozieMRActionConfigFolderName = "";
	private static String localOozieJavaActionConfigFolderName = "";
	private static String localOozieShellActionConfigFolderName = "";
	
	private static String localOozieJobPropertiesFileName = "";
	private static String localOozieWorkflowFileName = "";	
	private static String localOozieShellActionScriptFileName = "";
	
	private static String localOozieKnoxConfigFolderName = "";
	private static String localOozieWorkflowConfigXmlFileName = "";
	
		
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	
	// /usr/lib/hadoop/bin/hadoop (<=TDH2.1.11) ==> /usr/bin/hadoop or /usr/hdp/2.3.4.0-3485/hadoop (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 12){
			System.out.println("\n*** 11 + 1 parameters for Oozie-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localOozieTestDataFileName = args[4];
		oozieTestFolderName = args[5];
		localOozieJarFileName = args[6];
			
		localOozieMRActionConfigFolderName = args[7];
		localOozieJavaActionConfigFolderName = args[8];
		
		internalKinitCmdStr = args[9];		    
		localOozieJobPropertiesFileName = args[10];	
		localOozieWorkflowFileName = args[11];
		
		localOozieShellActionConfigFolderName = args[12];
		localOozieShellActionScriptFileName = args[13];
		
		localOozieKnoxConfigFolderName = args[14];
		localOozieWorkflowConfigXmlFileName = args[15];
		
		if (!localOozieKnoxConfigFolderName.endsWith("/")){
			localOozieKnoxConfigFolderName += "/";
		}
		
		if (!localOozieShellActionConfigFolderName.endsWith("/")){
			localOozieShellActionConfigFolderName += "/";
		}
		if (!localOozieJavaActionConfigFolderName.endsWith("/")){
			localOozieJavaActionConfigFolderName += "/";
		}
		if (!localOozieMRActionConfigFolderName.endsWith("/")){
			localOozieMRActionConfigFolderName += "/";
		}
				
		
		if (!oozieTestFolderName.endsWith("/")){
			oozieTestFolderName += "/";
		}		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
							
		run();
	}//end main
	
	public static void run() throws Exception {
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String oozieScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Oozie\\";
	    prepareFolder(oozieScriptFilesFoder, "Local Oozie Testing Script Files");
	    
		String dcTestOozie_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestOozie_WorkflowActions_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestOozie_RecFilePathAndName,  "Records of Testing Oozie on '" + bdClusterName + "' Cluster");
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Internal Ooze - Work Flow Actions Representative Scenario Testing "
	    		+ "\n-- 		  Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
				
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();	
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		oozieTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Oozie/";//Modify oozieTestFolderName from "/data/test/Oozie/"
			
		String oozieMRActionInputFolder = oozieTestFolderName + "mapreduce/input/";
		String oozieMRActionOutputFolder = oozieTestFolderName + "mapreduce/output/";
		String oozieMRActionLibFolder = oozieTestFolderName + "mapreduce/lib/";
		
		System.out.println(" *** oozieMRActionInputFolder: " + oozieMRActionInputFolder);
		System.out.println(" *** oozieMRActionOutputFolder: " + oozieMRActionOutputFolder);
		System.out.println(" *** oozieMRActionLibFolder: " + oozieMRActionLibFolder);
		
		String oozieJavaActionInputFolder = oozieTestFolderName + "java/input/";
		String oozieJavaActionOutputFolder = oozieTestFolderName + "java/output/";
		String oozieJavaActionLibFolder = oozieTestFolderName + "java/lib/";
		
		System.out.println(" *** oozieJavaActionInputFolder: " + oozieJavaActionInputFolder);
		System.out.println(" *** oozieJavaActionOutputFolder: " + oozieJavaActionOutputFolder);
		System.out.println(" *** oozieJavaActionLibFolder: " + oozieJavaActionLibFolder);
		
		String oozieShellActionInputFolder = oozieTestFolderName + "shell/input/";
		String oozieShellActionOutputFolder = oozieTestFolderName + "shell/output/";
		String oozieShellActionLibFolder = oozieTestFolderName + "shell/lib/";
		
		System.out.println(" *** oozieShellActionInputFolder: " + oozieShellActionInputFolder);
		System.out.println(" *** oozieShellActionOutputFolder: " + oozieShellActionOutputFolder);
		System.out.println(" *** oozieShellActionLibFolder: " + oozieShellActionLibFolder);
				
		String jobTrackerStr = currBdCluster.getBdClusterActiveRMIPAddressAndPort();
		System.out.println("\n*** jobTrackerStr: " + jobTrackerStr);
		
		System.out.println("\n*** loginUser4AllNodesName: " + loginUser4AllNodesName);
		
		//http://hdpr03mn01.mayo.edu:50070/webhdfs .replace(":8020", ":50070") + "/webhdfs"; 
		String oozieServerURLStr = currBdCluster.getActiveWebHdfsHttpAddress().replace(":50070", ":11000").replace("/webhdfs", "/oozie");
		System.out.println("\n*** oozieServerURLStr: " + oozieServerURLStr);
		
		String localOozieMRJobPropertiesFileFullPathAndName= localOozieMRActionConfigFolderName + localOozieJobPropertiesFileName;
		String srcFileFullPathAndName = localOozieMRJobPropertiesFileFullPathAndName;
		
		String localOozieJavaJobPropertiesFileFullPathAndName= localOozieJavaActionConfigFolderName + localOozieJobPropertiesFileName;
		String srcFileFullPathAndName2 = localOozieJavaJobPropertiesFileFullPathAndName;
		
		String localOozieShellJobPropertiesFileFullPathAndName= localOozieShellActionConfigFolderName + localOozieJobPropertiesFileName;
		String srcFileFullPathAndName3 = localOozieShellJobPropertiesFileFullPathAndName;
		
		String localOozieJobPropertiesFileName_clusterSpecific = "";
		if (localOozieJobPropertiesFileName.endsWith("job.properties")){
			localOozieJobPropertiesFileName_clusterSpecific = localOozieJobPropertiesFileName.replace("job.properties", "job_" + bdClusterName + ".properties");
		}	
		
		//a. for MR action
		String bdClusterRealmName = currBdCluster.getBdClusterIdName();		
		String nameNodeStr = "hdfs://" + bdClusterRealmName;
		System.out.println("\n*** nameNodeStr: " + nameNodeStr);
				
		localOozieMRJobPropertiesFileFullPathAndName= localOozieMRActionConfigFolderName + localOozieJobPropertiesFileName_clusterSpecific;
		String destFileFullPathAndName = localOozieMRJobPropertiesFileFullPathAndName;
		
		Files.copy(new File (srcFileFullPathAndName), new File (destFileFullPathAndName));
		updateLocalOozieJobPropertiesFile (localOozieMRJobPropertiesFileFullPathAndName, nameNodeStr, jobTrackerStr, loginUser4AllNodesName);
		
		
		//b. for java action
		String nameNodeStr2 = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println("\n*** nameNodeStr2: " + nameNodeStr2);		
		
		localOozieJavaJobPropertiesFileFullPathAndName= localOozieJavaActionConfigFolderName + localOozieJobPropertiesFileName_clusterSpecific;
		String destFileFullPathAndName2 = localOozieJavaJobPropertiesFileFullPathAndName;
		
		Files.copy(new File (srcFileFullPathAndName2), new File (destFileFullPathAndName2));
		updateLocalOozieJobPropertiesFile (localOozieJavaJobPropertiesFileFullPathAndName, nameNodeStr2, jobTrackerStr, loginUser4AllNodesName);
		
		//c. for shell action
		String nameNodeStr3 = nameNodeStr;
		System.out.println("\n*** nameNodeStr3: " + nameNodeStr3);		
		
		localOozieShellJobPropertiesFileFullPathAndName= localOozieShellActionConfigFolderName + localOozieJobPropertiesFileName_clusterSpecific;
		String destFileFullPathAndName3 = localOozieShellJobPropertiesFileFullPathAndName;
		
		Files.copy(new File (srcFileFullPathAndName3), new File (destFileFullPathAndName3));
		updateLocalOozieJobPropertiesFile (localOozieShellJobPropertiesFileFullPathAndName, nameNodeStr3, jobTrackerStr, loginUser4AllNodesName);
		
		
		//d. for Knox-MapReduce action
		String oozieKnoxMRActionInputFolder = oozieTestFolderName + "knox/input/";
		String oozieKnoxMRActionOutputFolder = oozieTestFolderName + "knox/output/";
		String oozieKnoxMRActionLibFolder = oozieTestFolderName + "knox/lib/";
		
		System.out.println(" *** oozieKnoxMRActionInputFolder: " + oozieKnoxMRActionInputFolder);
		System.out.println(" *** oozieKnoxMRActionOutputFolder: " + oozieKnoxMRActionOutputFolder);
		System.out.println(" *** oozieKnoxMRActionLibFolder: " + oozieKnoxMRActionLibFolder);
		
		
		
		
		String localOozieKnoxWorkflowConfigXmlFileFullPathAndName= localOozieKnoxConfigFolderName + localOozieWorkflowConfigXmlFileName;
		String srcFileFullPathAndName4 = localOozieKnoxWorkflowConfigXmlFileFullPathAndName;
		
		String localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific = "";
		if (localOozieWorkflowConfigXmlFileName.endsWith("configuration.xml")){
			localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific = localOozieWorkflowConfigXmlFileName.replace("configuration.xml", "configuration_" + bdClusterName + ".xml");
		}
		
		String nameNodeStr4 = nameNodeStr;
		System.out.println("\n*** nameNodeStr4: " + nameNodeStr4);		
		
		localOozieKnoxWorkflowConfigXmlFileFullPathAndName = localOozieKnoxConfigFolderName + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
		String destFileFullPathAndName4 = localOozieKnoxWorkflowConfigXmlFileFullPathAndName;
		
		Files.copy(new File (srcFileFullPathAndName4), new File (destFileFullPathAndName4));
		updateLocalOozieKnoxWorkflowConfigXmlFile (localOozieKnoxWorkflowConfigXmlFileFullPathAndName, nameNodeStr4, jobTrackerStr, loginUser4AllNodesName);
		
		
		String activeNN_addr_port = nameNodeStr2;
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
					
		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();
		System.out.println(" *** currClusterKnoxNodeName: " + currClusterKnoxNodeName);
		BdNode knoxNode1 = new BdNode(currClusterKnoxNodeName, bdClusterName);
		ULServerCommandFactory knoxNodeCmdFactory = knoxNode1.getBdENCmdFactory();
		String currClusterKnoxFQDN = knoxNodeCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currClusterKnoxFQDN: " + currClusterKnoxFQDN);
		
		
		String bdClusterKnoxIdName = currBdCluster.getBdClusterIdName();
		System.out.println(" *** bdClusterKnoxIdName: " + bdClusterKnoxIdName);
		
		String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		System.out.println("\n *** currClusterKnoxNode2Name: " + currClusterKnoxNode2Name);
		
		
		String loginUserName_Knox = knoxNodeCmdFactory.getUsername();
		//String loginUserADPassWd = bdENCmdFactory.getPassword();
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos			
		String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		String loginCredential_AD = loginUserName_Knox + ":" + loginUserADPassWd;		
		System.out.println(" *** loginCredential_AD: " + loginCredential_AD);
		
		String knoxTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Knox/";
		String knoxOozieMRActionInputFolder = knoxTestFolderName + "oozie/input/";
		String knoxOozieMRActionOutputFolder = knoxTestFolderName + "oozie/output/";
		String knoxOozieMRActionLibFolder = knoxTestFolderName + "oozie/lib/";
		
		System.out.println(" *** knoxOozieMRActionInputFolder: " + knoxOozieMRActionInputFolder);
		System.out.println(" *** knoxOozieMRActionOutputFolder: " + knoxOozieMRActionOutputFolder);
		System.out.println(" *** knoxOozieMRActionLibFolder: " + knoxOozieMRActionLibFolder);
		
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
				
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		
		
		//4. Loop through bdClusterEntryNodeList to internally run oozie workflow job that manages the execution of a MapReduce program - word counting		
		writeDataToAFile(dcTestOozie_RecFilePathAndName, "[1]. Oozie Workflow MapReduce Action  \n", true);
		//clusterENNumber_Start = 0; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 4; //1..2..3..4..5..6
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Oozie Workflow MapReduce Action Using MapReduce - Mapper & Reducer Programs on Entry Node: " + tempENName);
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			//String rootPw = bdENRootCmdFactory.getPassword();			
			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
			
			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/oozie/mapreduce/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
									
						
			//(1) Move Oozie MapReduce test data file, jar file, job.properties file and workflow.xml into Linux local folder for testing
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJobPropertiesFileName_clusterSpecific, 
					localOozieMRActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieWorkflowFileName, 
					localOozieMRActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);
		
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
				
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving Oozie MapReduce Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie MapReduce Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal4 == 0 ){
				System.out.println("\n*** Done - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			
			//(2) Testing Oozie Function Using Word-counting Java Program
			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
			String hdfsOozieTestDataFilePathAndName = oozieMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
			String hdfsOozieTestJarFilePathAndName = oozieMRActionLibFolder + localOozieJarFileName; 
			
			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
			String hdfsOozieWorkflowFilePathAndName = oozieTestFolderName + "mapreduce/" + localOozieWorkflowFileName; 
			
			String enServerOozieJobPropertiesFileFullPathAndName  = enServerScriptFileDirectory + localOozieJobPropertiesFileName_clusterSpecific;
			String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieJobPropertiesFileFullPathAndName + " -run & sleep 55";
			
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + oozieMRActionInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieMRActionOutputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieMRActionLibFolder + "; \n");
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
			sb.append("hadoop fs -mkdir -p " + oozieMRActionInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + oozieMRActionOutputFolder + "; \n");			
			sb.append("hadoop fs -mkdir -p " + oozieMRActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  oozieTestFolderName + "mapreduce" + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + oozieTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 755 " + oozieTestFolderName + "mapreduce" + "; \n");
		    
		       
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runOozieWrodCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + oozieTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localOozieMRActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_MRActionWordCountingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localOozieMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String oozieWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localOozieMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";	
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieMRActionWordCountTestingScriptFilePathAndName, 
						oozieScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsOozieOutPutFilePathAndName = oozieMRActionOutputFolder + "part-r-00000";						
			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
			
			boolean currTestScenarioSuccessStatus = false;
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
					currTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting MapReduce Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + oozieMRActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			} else {
				testRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting MapReduce Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + oozieMRActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			}
			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end for 4. [1] Oozie Workflow MapReduce Action
		
		
		
		
		//5. Loop through bdClusterEntryNodeList to internally run oozie workflow job that manages the execution of a MapReduce program - word counting		
		writeDataToAFile(dcTestOozie_RecFilePathAndName, "\n[2]. Oozie Workflow Java Action  \n", true);
		//clusterENNumber_Start = 1; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 2; //1..2..3..4..5..6
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Oozie Workflow Java Action Using MapReduce-WordCount Program on Entry Node: " + tempENName);
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			//String rootPw = bdENRootCmdFactory.getPassword();			
			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
			
			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/oozie/java/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
						
						
						
			//(1) Move Oozie MapReduce test data file, jar file, job.properties file and workflow.xml into Linux local folder for testing
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJobPropertiesFileName_clusterSpecific, 
					localOozieJavaActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieWorkflowFileName, 
					localOozieJavaActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);
		
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
				
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Java Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Java Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal4 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Java Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Java Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			
			//(2) Testing Oozie Function Using Word-counting Java Program
			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
			String hdfsOozieTestDataFilePathAndName = oozieJavaActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
			String hdfsOozieTestJarFilePathAndName = oozieJavaActionLibFolder + localOozieJarFileName; 
			
			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
			String hdfsOozieWorkflowFilePathAndName = oozieTestFolderName + "java/" + localOozieWorkflowFileName; 
			
			String enServerOozieJobPropertiesFileFullPathAndName  = enServerScriptFileDirectory + localOozieJobPropertiesFileName_clusterSpecific;
			String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieJobPropertiesFileFullPathAndName + " -run & sleep 50";
			
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + oozieJavaActionInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieJavaActionOutputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieJavaActionLibFolder + "; \n");
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
			sb.append("hadoop fs -mkdir -p " + oozieJavaActionInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + oozieJavaActionOutputFolder + "; \n");			
			sb.append("hadoop fs -mkdir -p " + oozieJavaActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal /etc/hadoop/conf/*.xml " + oozieJavaActionLibFolder + "; \n");
			
	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  oozieTestFolderName + "mapreduce" + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + oozieTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 755 " + oozieTestFolderName + "mapreduce" + "; \n");
		    
		       
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runOozieWrodCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + oozieTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localOozieJavaActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_JavaActionWordCountingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localOozieJavaActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String oozieWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localOozieJavaActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";	
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieJavaActionWordCountTestingScriptFilePathAndName, 
						oozieScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsOozieOutPutFilePathAndName = oozieJavaActionOutputFolder + "part-r-00000";						
			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
			
			boolean currTestScenarioSuccessStatus = false;
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
					currTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting Java Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie Java Action Output HDFS Folder:  '" + oozieJavaActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			} else {
				testRecordInfo = "-*-*- 'Failed' - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting Java Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie Java Action Output HDFS Folder:  '" + oozieJavaActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			}
			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end for 5. [2]. Oozie Workflow Java Action
		
		
		
		//6. Loop through bdClusterEntryNodeList to internally run oozie workflow job that manages the execution of a MapReduce program - word counting		
		writeDataToAFile(dcTestOozie_RecFilePathAndName, "\n[3]. Oozie Workflow Shell Action  \n", true);
		//clusterENNumber_Start = 1; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 2; //1..2..3..4..5..6
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Oozie Workflow Shell Action Using MapReduce-WordCount Program on Entry Node: " + tempENName);
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			//String rootPw = bdENRootCmdFactory.getPassword();			
			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
			
			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/oozie/shell/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
						
						
						
			//(1) Move Oozie MapReduce test data file, jar file, job.properties file and workflow.xml into Linux local folder for testing
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJobPropertiesFileName_clusterSpecific, 
					localOozieShellActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieWorkflowFileName, 
					localOozieShellActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal5 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieShellActionScriptFileName, 
					localOozieShellActionConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);
		
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
				
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Shell Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Shell Action Properties File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal4 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Shell Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Shell Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal5 == 0 ){
				System.out.println("\n*** Done - Moving Oozie Shell Action Shell-Script File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Oozie Shell Action Shell-Script File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			
			//(2) Testing Oozie Function Using Word-counting Java Program
			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
			String hdfsOozieTestDataFilePathAndName = oozieShellActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
			String hdfsOozieTestJarFilePathAndName = oozieShellActionLibFolder + localOozieJarFileName; 
			
			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
			String hdfsOozieWorkflowFilePathAndName = oozieTestFolderName + "shell/" + localOozieWorkflowFileName; 
			
			String enServerOozieJobPropertiesFileFullPathAndName  = enServerScriptFileDirectory + localOozieJobPropertiesFileName_clusterSpecific;
			
			String enServerOozieShellActionScriptFileFullPathAndName  = enServerScriptFileDirectory + localOozieShellActionScriptFileName;
			String hdfsOozieShellActionScriptFilePathAndName = oozieTestFolderName + "shell/" + localOozieShellActionScriptFileName; 
				
			String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieJobPropertiesFileFullPathAndName + " -run & sleep 50";
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + oozieShellActionInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieShellActionOutputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + oozieShellActionLibFolder + "; \n");	
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");			
			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieShellActionScriptFilePathAndName + "; \n");			
			
			sb.append("hadoop fs -mkdir -p " + oozieShellActionInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + oozieShellActionOutputFolder + "; \n");			
			sb.append("hadoop fs -mkdir -p " + oozieShellActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
			sb.append("hadoop fs -copyFromLocal /etc/hadoop/conf/*.xml " + oozieShellActionLibFolder + "; \n");
			
			sb.append("hadoop fs -copyFromLocal " + enServerOozieShellActionScriptFileFullPathAndName + " " + hdfsOozieShellActionScriptFilePathAndName + "; \n");
			
	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  oozieTestFolderName + "mapreduce" + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + oozieTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 755 " + oozieTestFolderName + "mapreduce" + "; \n");
		    
		       
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runOozieWrodCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + oozieTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localOozieShellActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_ShellActionWordCountingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localOozieShellActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String oozieWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localOozieShellActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";	
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieShellActionWordCountTestingScriptFilePathAndName, 
						oozieScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsOozieOutPutFilePathAndName = oozieShellActionOutputFolder + "part-r-00000";						
			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
			
			boolean currTestScenarioSuccessStatus = false;
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
					currTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting Shell Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie Shell Action Output HDFS Folder:  '" + oozieShellActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			} else {
				testRecordInfo = "-*-*- 'Failed' - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Oozie Workflow MapReduce-Word-Counting Shell Action "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Oozie Shell Action Output HDFS Folder:  '" + oozieShellActionOutputFolder + "'"
				        + "\n  --(3) Oozie Testing Total Time Used: " + timeUsed + "\n"; 	
			}
			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end for 6. [3]. Oozie Workflow Shell Action
		
				
//		//7. Loop through bdClusterEntryNodeList to internally run oozie workflow job that manages the execution of a MapReduce program - word counting		
//		writeDataToAFile(dcTestOozie_RecFilePathAndName, "[4]. Knox-Controlled Oozie Workflow MapReduce Action  \n", true);
//		clusterENNumber_Start = 1; //0..1..2..3..4..5..6..7..8..9
//	    clusterENNumber = 2; //1..2..3..4..5..6
//		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
//			totalTestScenarioNumber++;
//			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
//			System.out.println("\n--- (" + (i+1) + ") Testing Knox-Controlled Oozie Workflow MapReduce Action Using MapReduce - Mapper & Reducer Programs on Entry Node: " + tempENName);
//			
//			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
//			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
//			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
//			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
//			
//			String loginUserName = bdENCmdFactory.getUsername(); 						
//			String rootUserName = bdENRootCmdFactory.getUsername();
//			//String rootPw = bdENRootCmdFactory.getPassword();			
//			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
//			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
//			
//			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
//				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/oozie/knox/";				
//			}			
//			System.out.println("*** loginUserName is: " + loginUserName);
//			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
//			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
//									
//						
//			//(1) Move Knox-Oozie MapReduce test data file, jar file, work flow configuration file and workflow.xml into Linux local folder for testing
//			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieTestDataFileName, 
//					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);			
//			
//			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJarFileName, 
//					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
//			
//			int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific, 
//					localOozieKnoxConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);			
//			
//			int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieWorkflowFileName, 
//					localOozieKnoxConfigFolderName, enServerScriptFileDirectory, bdENCmdFactory);
//		
//			DayClock tempClock = new DayClock();				
//			String tempTime = tempClock.getCurrentDateTime();	
//			
//			if (exitVal1 == 0 ){
//				System.out.println("\n*** Done - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//			
//			if (exitVal2 == 0 ){
//				System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//				
//			if (exitVal3 == 0 ){
//				System.out.println("\n*** Done - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//			
//			if (exitVal4 == 0 ){
//				System.out.println("\n*** Done - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//			
//			
//			//(2) Testing Oozie Function Using Word-counting Java Program
//			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
//			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
//			String hdfsOozieTestDataFilePathAndName = oozieKnoxMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
//			
//			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
//			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
//			String hdfsOozieTestJarFilePathAndName = oozieKnoxMRActionLibFolder + localOozieJarFileName; 
//			
//			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
//			String hdfsOozieWorkflowFilePathAndName = oozieTestFolderName + "knox/" + localOozieWorkflowFileName; 
//			
//			String enServerOozieKnoxWorkflowConfigFileFullPathAndName  = enServerScriptFileDirectory + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
//			
//			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn01.mayo.edu:11000/oozie/v1/jobs?action=start'
//			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn02.mayo.edu:11000/oozie/v1/jobs?action=start'
//			String curlOozieRestApiJobSubmissionCmd_L = "curl -i -k -u : --negotiate -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			//activeNN_addr_port: hdfs://hdpr03mn01.mayo.edu:8020 --> http://hdpr03mn02.mayo.edu:11000
//			//String oozieServer1HttpIpAddressAndPort = activeNN_addr_port.replace(":8020", ":11000").replace("hdfs:", "http:");
//			
//			BdNode mn1BDNode = new BdNode("mn01".toUpperCase(), bdClusterName);
//			ULServerCommandFactory mn1CmdFactory = mn1BDNode.getBdENCmdFactory();			
//			System.out.println(" *** mn1CmdFactory.getServerURI(): " + mn1CmdFactory.getServerURI());			
//			String oozieServerHttpHttpURL = "http://" + mn1CmdFactory.getServerURI() + ":11000";
//			oozieServerHttpHttpURL = oozieServerHttpHttpURL.replace("mn01.", "mn02.");
//			String curlRestApiJobSubmissionCmd_R = "-X POST -L '" + oozieServerHttpHttpURL + "/oozie/v1/jobs?action=start'";
//			String runOozieWrodCountJarCmd = curlOozieRestApiJobSubmissionCmd_L + " " + curlRestApiJobSubmissionCmd_R + " & sleep 55";
//			
//			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/oozie/v1/jobs?action=start'
//			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1_bkp/oozie/v1/jobs?action=start'
//			//String curlKnoxOozieJobSubmissionCmd_L = "curl -i -k -u " + loginCredential_AD + " -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			//String curlKnoxOozieJobSubmissionCmd_R = "-X POST -L '" + "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/oozie/v1/jobs?action=start'";
//			//String runOozieWrodCountJarCmd = curlKnoxOozieJobSubmissionCmd_L + " " + curlKnoxOozieJobSubmissionCmd_R + " & sleep 60";
//			
//			//String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieKnoxWorkflowConfigFileFullPathAndName + " -run & sleep 45";
//			
//						
//			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - hdfs;\n");
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			sb.append("hadoop fs -rm -r -skipTrash " + oozieKnoxMRActionInputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + oozieKnoxMRActionOutputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + oozieKnoxMRActionLibFolder + "; \n");
//			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
//			sb.append("hadoop fs -mkdir -p " + oozieKnoxMRActionInputFolder + "; \n");
//			//sb.append("hadoop fs -mkdir -p " + oozieKnoxMRActionOutputFolder + "; \n");			
//			sb.append("hadoop fs -mkdir -p " + oozieKnoxMRActionLibFolder + "; \n");
//			
//			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
//	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  oozieTestFolderName + "knox" + "; \n");			
//		    //sb.append("hadoop fs -chown hdfs:bduser " + oozieTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 755 " + oozieTestFolderName + "knox" + "; \n");
//		    
//		       
//		    //sb.append("sudo su - " + loginUserName + ";\n");
//		    //sb.append("kdestroy;\n");
//		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
//		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
//		    
//		    sb.append(runOozieWrodCountJarCmd + ";\n");
//		    sb.append("hadoop fs -chmod -R 550 " + oozieTestFolderName + "; \n");
//		    sb.append("kdestroy;\n");
//		    
//		   		    
//		    String localOozieKnoxMRActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_KnoxMRActionWordCountingScriptFile_No"+ (i+1) + ".sh";			
//			prepareFile (localOozieKnoxMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
//			
//			String oozieWordCountingTestingCmds = sb.toString();
//			writeDataToAFile(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
//			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";	
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, 
//						oozieScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
//			
//			String hdfsOozieOutPutFilePathAndName = oozieKnoxMRActionOutputFolder + "part-r-00000";						
//			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
//			
//			boolean currTestScenarioSuccessStatus = false;
//			if (currHadoopFS.exists(oozieOutPutHdfsFilePath)) {
//				System.out.println("\n***  Existing file : " + oozieOutPutHdfsFilePath);
//				hdfsFilePathAndNameList.add(hdfsOozieOutPutFilePathAndName);
//				
//				FileStatus[] status = currHadoopFS.listStatus(oozieOutPutHdfsFilePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				int totalWordCount = 0;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					line = line.replaceAll("\\s", "===");
//					//System.out.println(" --1--  line: " + line);
//					
//					if (line.contains("===")) {						
//						String[] lineSplit = line.split("===");
//						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
//					}		
//												
//				}//end while
//				br.close();
//				System.out.println(" *** totalWordCount == " + totalWordCount);	
//				
//				if (totalWordCount == 36){
//					currTestScenarioSuccessStatus = true;					
//				} 		
//				
//	        }//end outer if	
//			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
//			
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
//				
//			
//			String testRecordInfo = "";	
//			if (currTestScenarioSuccessStatus == true){
//				successTestScenarioNum++;			
//				
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + oozieKnoxMRActionOutputFolder + "'\n"; 				        
//			} else {
//				testRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + oozieKnoxMRActionOutputFolder + "'\n"; 
//			}
//			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
//			prevTime = currTime;
//		}//end for 7. [4] Knox-Controlled Oozie Workflow MapReduce Action
		
		
//		//8. Run oozie workflow job that manages the execution of a MapReduce program - word counting by Oozie Server or Knox Gateway Rest API		
//		writeDataToAFile(dcTestOozie_RecFilePathAndName, "[5]. Knox-Controlled Oozie Workflow MapReduce Action  \n", true);
//		
//		String loginUserName = loginUser4AllNodesName;
//		
//		enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/oozie/knox/";			
//		System.out.println("*** loginUserName is: " + loginUserName);
//		System.out.println("*** enServerScriptFileDirectory is: " + enServerScriptFileDirectory);
//		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, knoxNodeCmdFactory);
//		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
//								
//					
//		//(1) Move Knox-Oozie MapReduce test data file, jar file, work flow configuration file and workflow.xml into Linux local folder for testing
//		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieTestDataFileName, 
//				bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, knoxNodeCmdFactory);			
//		
//		int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieJarFileName, 
//				bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, knoxNodeCmdFactory);
//		
//		int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific, 
//				localOozieKnoxConfigFolderName, enServerScriptFileDirectory, knoxNodeCmdFactory);			
//		
//		int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localOozieWorkflowFileName, 
//				localOozieKnoxConfigFolderName, enServerScriptFileDirectory, knoxNodeCmdFactory);
//	
//		DayClock tempClock = new DayClock();				
//		String tempTime = tempClock.getCurrentDateTime();	
//		
//		if (exitVal1 == 0 ){
//			System.out.println("\n*** Done - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		} else {
//			System.out.println("\n*** Failed - Moving Oozie Test Data File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		}
//		
//		if (exitVal2 == 0 ){
//			System.out.println("\n*** Done - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		} else {
//			System.out.println("\n*** Failed - Moving Oozie Jar File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		}
//			
//		if (exitVal3 == 0 ){
//			System.out.println("\n*** Done - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		} else {
//			System.out.println("\n*** Failed - Moving Knox-Oozie MapReduce Action Workflow Configuration File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		}
//		
//		if (exitVal4 == 0 ){
//			System.out.println("\n*** Done - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		} else {
//			System.out.println("\n*** Failed - Moving Oozie MapReduce Action Workflow File into '" + enServerScriptFileDirectory + "' folder on Knox Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//		}
//		
//		//(2) Run Oozie MapReduce Action Using Different Oozie Servers:
//		//8.1 Loop Through the oozie server rest api to run Oozie MapReduce Action
//		int oozieRestServiceNumber = 2; //1..2
//		int oozieService_Start = 0; //0..1
//		
//		//oozieService_Start = 1;
//		//oozieRestServiceNumber = 1; //1 ..2;
//		for (int i = oozieService_Start; i < oozieRestServiceNumber; i++){			
//			//(1) Get active WebHbase HTTP URL:		
//			totalTestScenarioNumber++;
//			String oozieServerHttpHttpURL = "";
//			
//			if (i==0){
//				BdNode mn1BDNode = new BdNode("mn01".toUpperCase(), bdClusterName);
//				ULServerCommandFactory mn1CmdFactory = mn1BDNode.getBdENCmdFactory();			
//				System.out.println(" *** mn1CmdFactory.getServerURI(): " + mn1CmdFactory.getServerURI());
//				
//				oozieServerHttpHttpURL = "http://" + mn1CmdFactory.getServerURI() + ":11000";
//			}
//			if (i==1){
//				BdNode mn2BDNode = new BdNode("mn02".toUpperCase(), bdClusterName);
//				ULServerCommandFactory mn2CmdFactory = mn2BDNode.getBdENCmdFactory();			
//				System.out.println(" *** mn2CmdFactory.getServerURI(): " + mn2CmdFactory.getServerURI());
//				
//				oozieServerHttpHttpURL = "http://" + mn2CmdFactory.getServerURI() + ":11000";
//			}
//			
//			//(2) Testing Oozie Function Using Word-counting Java Program
//			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
//			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
//			String hdfsOozieTestDataFilePathAndName = knoxOozieMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
//			
//			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
//			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
//			String hdfsOozieTestJarFilePathAndName = knoxOozieMRActionLibFolder + localOozieJarFileName; 
//			
//			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
//			String hdfsOozieWorkflowFilePathAndName = knoxTestFolderName + "oozie/" + localOozieWorkflowFileName; 
//			
//			String enServerOozieKnoxWorkflowConfigFileFullPathAndName  = enServerScriptFileDirectory + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
//			
//			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn01.mayo.edu:11000/oozie/v1/jobs?action=start'
//			//curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn02.mayo.edu:11000/oozie/v1/jobs?action=start'
//			String curlOozieRestApiJobSubmissionCmd_L = "curl -i -k -u : --negotiate -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			//activeNN_addr_port: hdfs://hdpr03mn01.mayo.edu:8020 --> http://hdpr03mn02.mayo.edu:11000
//			//String oozieServerHttpHttpURL = activeNN_addr_port.replace(":8020", ":11000").replace("hdfs:", "http:");				
//			
//			String curlRestApiJobSubmissionCmd_R = "-X POST -L '" + oozieServerHttpHttpURL + "/oozie/v1/jobs?action=start'";
//			String runOozieWrodCountJarCmd = curlOozieRestApiJobSubmissionCmd_L + " " + curlRestApiJobSubmissionCmd_R + " & sleep 55";
//			
//			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/oozie/v1/jobs?action=start'
//			//curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1_bkp/oozie/v1/jobs?action=start'
//			//String curlKnoxOozieJobSubmissionCmd_L = "curl -i -k -u " + loginCredential_AD + " -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			//String curlKnoxOozieJobSubmissionCmd_R = "-X POST -L '" + "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "/oozie/v1/jobs?action=start'";
//			//String runOozieWrodCountJarCmd = curlKnoxOozieJobSubmissionCmd_L + " " + curlKnoxOozieJobSubmissionCmd_R + " & sleep 60";
//			
//			//String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieKnoxWorkflowConfigFileFullPathAndName + " -run & sleep 45";
//			
//						
//			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - hdfs;\n");
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionInputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionOutputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionLibFolder + "; \n");
//			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
//			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionInputFolder + "; \n");
//			//sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionOutputFolder + "; \n");			
//			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionLibFolder + "; \n");
//			
//			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
//	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  knoxTestFolderName + "knox" + "; \n");			
//		    //sb.append("hadoop fs -chown hdfs:bduser " + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 755 " + knoxTestFolderName + "knox" + "; \n");
//		    
//		       
//		    //sb.append("sudo su - " + loginUserName + ";\n");
//		    //sb.append("kdestroy;\n");
//		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
//		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
//		    
//		    sb.append(runOozieWrodCountJarCmd + ";\n");
//		    sb.append("hadoop fs -chmod -R 550 " + knoxTestFolderName + "; \n");
//		    sb.append("kdestroy;\n");
//		    
//		   		    
//		    String localOozieKnoxMRActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_MRActionWordCountingScriptFile_Curl_No"+ (i+1) + ".sh";			
//			prepareFile (localOozieKnoxMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Knox Node - " + currClusterKnoxNodeName);
//			
//			String oozieWordCountingTestingCmds = sb.toString();
//			writeDataToAFile(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
//			String enServerScriptFileDirectory_short = "/data/home/" + loginUserName + "/test/";	
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, 
//						oozieScriptFilesFoder, enServerScriptFileDirectory_short, knoxNodeCmdFactory);
//			
//			String hdfsOozieOutPutFilePathAndName = knoxOozieMRActionOutputFolder + "part-r-00000";	
//			
//			System.out.println("*** hdfsOozieOutPutFilePathAndName is: " + hdfsOozieOutPutFilePathAndName);
//			
//			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
//			
//			boolean currTestScenarioSuccessStatus = false;
//			if (currHadoopFS.exists(oozieOutPutHdfsFilePath)) {
//				System.out.println("\n***  Existing file : " + oozieOutPutHdfsFilePath);
//				hdfsFilePathAndNameList.add(hdfsOozieOutPutFilePathAndName);
//				
//				FileStatus[] status = currHadoopFS.listStatus(oozieOutPutHdfsFilePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				int totalWordCount = 0;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					line = line.replaceAll("\\s", "===");
//					//System.out.println(" --1--  line: " + line);
//					
//					if (line.contains("===")) {						
//						String[] lineSplit = line.split("===");
//						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
//					}		
//												
//				}//end while
//				br.close();
//				System.out.println(" *** totalWordCount == " + totalWordCount);	
//				
//				if (totalWordCount == 36){
//					currTestScenarioSuccessStatus = true;					
//				} 		
//				
//	        }//end outer if	
//			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
//			
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
//				
//			
//			String testRecordInfo = "";	
//			if (currTestScenarioSuccessStatus == true){
//				successTestScenarioNum++;			
//				
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder + "'\n"; 				        
//			} else {
//				testRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Oozie Server HTTP URL - " + oozieServerHttpHttpURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder + "'\n"; 
//			}
//			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
//			prevTime = currTime;
//		}//end 8.1
//		
//		
//		//8.2 Loop Through the Knox Gateway Rest API to Run Oozie MapReduce Action Job
//		int knox_oozieRestServiceNumber = 1; //1..2
//		if (currClusterKnoxNode2Name.isEmpty()){
//			knox_oozieRestServiceNumber += 1;
//		}
//		
//		int knox_oozieService_Start = 0; //0..1
//		//oozieService_Start = 1;
//		//knox_oozieRestServiceNumber = 1; //1 ..2;
//		for (int i = knox_oozieService_Start; i < knox_oozieRestServiceNumber; i++){			
//			//(1) Get active WebHbase HTTP URL:		
//			totalTestScenarioNumber++;
//			String oozieKnoxGatewayHttpsURL = "";
//			
//			if (i==0){
//				oozieKnoxGatewayHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName;
//			}
//			if (i==1){
//				oozieKnoxGatewayHttpsURL = "https://" + currClusterKnoxFQDN + ":8442/gateway/" + bdClusterKnoxIdName + "_bkp";
//			}
//			
//			 
//			//(2) Testing Oozie Function Using Word-counting Java Program
//			//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
//			String enLocalOozieTestDataFilePathAndName = enServerScriptFileDirectory + localOozieTestDataFileName;			
//			String hdfsOozieTestDataFilePathAndName = knoxOozieMRActionInputFolder + "dcOozieTestData_employee.txt"; //"/data/test/Oozie/dcOozieTestData_employee.txt"; 	
//			
//			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/Oozie/input /user/m041785/test/Oozie/output;
//			String enServerOozieJarFilePathAndName = enServerScriptFileDirectory + localOozieJarFileName;
//			String hdfsOozieTestJarFilePathAndName = knoxOozieMRActionLibFolder + localOozieJarFileName; 
//			
//			String enServerOozieWorkflowFilePathAndName = enServerScriptFileDirectory + localOozieWorkflowFileName;
//			String hdfsOozieWorkflowFilePathAndName = knoxTestFolderName + "oozie/" + localOozieWorkflowFileName; 
//			
//			String enServerOozieKnoxWorkflowConfigFileFullPathAndName  = enServerScriptFileDirectory + localOozieKnoxWorkflowConfigXmlFileName_clusterSpecific;
//			
//			////curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn01.mayo.edu:11000/oozie/v1/jobs?action=start'
//			////curl -i -k -u : --negotiate -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'http://hdpr03mn02.mayo.edu:11000/oozie/v1/jobs?action=start'
//			//String curlOozieRestApiJobSubmissionCmd_L = "curl -i -k -u : --negotiate -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			////activeNN_addr_port: hdfs://hdpr03mn01.mayo.edu:8020 --> http://hdpr03mn02.mayo.edu:11000
//			//String oozieServerHttpHttpURL = activeNN_addr_port.replace(":8020", ":11000").replace("hdfs:", "http:");				
//			//String curlRestApiJobSubmissionCmd_R = "-X POST -L '" + oozieServerHttpHttpURL + "/oozie/v1/jobs?action=start'";
//			//String runOozieWrodCountJarCmd = curlOozieRestApiJobSubmissionCmd_L + " " + curlRestApiJobSubmissionCmd_R + " & sleep 55";
//			
//			////curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1/oozie/v1/jobs?action=start'
//			////curl -i -k -u m041785:deehoo16 -H Content-Type:application/xml -T /data/home/m041785/test/oozie/knox/workflow-configuration.xml -X POST -L 'https://hdpr01kx01.mayo.edu:8442/gateway/MAYOHADOOPDEV1_bkp/oozie/v1/jobs?action=start'
//			String curlKnoxOozieJobSubmissionCmd_L = "curl -i -k -u " + loginCredential_AD + " -H Content-Type:application/xml -T " + enServerOozieKnoxWorkflowConfigFileFullPathAndName;	
//			String curlKnoxOozieJobSubmissionCmd_R = "-X POST -L '" + oozieKnoxGatewayHttpsURL + "/oozie/v1/jobs?action=start'";
//			String runOozieWrodCountJarCmd = curlKnoxOozieJobSubmissionCmd_L + " " + curlKnoxOozieJobSubmissionCmd_R + " & sleep 65";
//			
//			//String runOozieWrodCountJarCmd = "/usr/bin/oozie job -oozie " + oozieServerURLStr +  " -config " + enServerOozieKnoxWorkflowConfigFileFullPathAndName + " -run & sleep 45";
//			
//						
//			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - hdfs;\n");
//			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
//			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
//			
//			sb.append("cd " + enServerScriptFileDirectory + ";\n");
//			//sb.append("sudo su - " + loginUserName + ";\n");
//			sb.append("kdestroy;\n");
//			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
//			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
//			
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionInputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionOutputFolder + "; \n");
//			sb.append("hadoop fs -rm -r -skipTrash " + knoxOozieMRActionLibFolder + "; \n");
//			sb.append("hadoop fs -rm -skipTrash " + hdfsOozieWorkflowFilePathAndName + "; \n");	
//			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionInputFolder + "; \n");
//			//sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionOutputFolder + "; \n");			
//			sb.append("hadoop fs -mkdir -p " + knoxOozieMRActionLibFolder + "; \n");
//			
//			sb.append("hadoop fs -copyFromLocal " + enLocalOozieTestDataFilePathAndName + " " + hdfsOozieTestDataFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieWorkflowFilePathAndName + " " + hdfsOozieWorkflowFilePathAndName + "; \n");
//			sb.append("hadoop fs -copyFromLocal " + enServerOozieJarFilePathAndName + " " + hdfsOozieTestJarFilePathAndName + "; \n");
//	    	sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " +  knoxTestFolderName + "knox" + "; \n");			
//		    //sb.append("hadoop fs -chown hdfs:bduser " + knoxTestFolderName + "; \n");
//		    sb.append("hadoop fs -chmod -R 755 " + knoxTestFolderName + "knox" + "; \n");
//		    
//		       
//		    //sb.append("sudo su - " + loginUserName + ";\n");
//		    //sb.append("kdestroy;\n");
//		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
//		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
//		    
//		    sb.append(runOozieWrodCountJarCmd + ";\n");
//		    sb.append("hadoop fs -chmod -R 550 " + knoxTestFolderName + "; \n");
//		    sb.append("kdestroy;\n");
//		    
//		   		    
//		    String localOozieKnoxMRActionWordCountTestingScriptFilePathAndName = oozieScriptFilesFoder + "dcTestOozie_Secure_MRActionWordCountingScriptFile_Curl_No"+ (i+1) + ".sh";			
//			prepareFile (localOozieKnoxMRActionWordCountTestingScriptFilePathAndName,  "Script File For Testing Oozie Word Counting on '" + bdClusterName + "' Cluster Knox Node - " + currClusterKnoxNodeName);
//			
//			String oozieWordCountingTestingCmds = sb.toString();
//			writeDataToAFile(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, oozieWordCountingTestingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(localOozieWordCountTestingScriptFilePathAndName));	
//			String enServerScriptFileDirectory_short = "/data/home/" + loginUserName + "/test/";	
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localOozieKnoxMRActionWordCountTestingScriptFilePathAndName, 
//						oozieScriptFilesFoder, enServerScriptFileDirectory_short, knoxNodeCmdFactory);
//			
//			String hdfsOozieOutPutFilePathAndName = knoxOozieMRActionOutputFolder + "part-r-00000";	
//			
//			System.out.println("*** hdfsOozieOutPutFilePathAndName is: " + hdfsOozieOutPutFilePathAndName);
//			
//			Path oozieOutPutHdfsFilePath = new Path(hdfsOozieOutPutFilePathAndName);
//			
//			boolean currTestScenarioSuccessStatus = false;
//			if (currHadoopFS.exists(oozieOutPutHdfsFilePath)) {
//				System.out.println("\n***  Existing file : " + oozieOutPutHdfsFilePath);
//				hdfsFilePathAndNameList.add(hdfsOozieOutPutFilePathAndName);
//				
//				FileStatus[] status = currHadoopFS.listStatus(oozieOutPutHdfsFilePath);				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				int totalWordCount = 0;
//				String line = "";
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					line = line.replaceAll("\\s", "===");
//					//System.out.println(" --1--  line: " + line);
//					
//					if (line.contains("===")) {						
//						String[] lineSplit = line.split("===");
//						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
//					}		
//												
//				}//end while
//				br.close();
//				System.out.println(" *** totalWordCount == " + totalWordCount);	
//				
//				if (totalWordCount == 36){
//					currTestScenarioSuccessStatus = true;					
//				} 		
//				
//	        }//end outer if	
//			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
//			
//			
//			DayClock currClock = new DayClock();				
//			String currTime = currClock.getCurrentDateTime();				
//			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
//				
//			
//			String testRecordInfo = "";	
//			if (currTestScenarioSuccessStatus == true){
//				successTestScenarioNum++;			
//				
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Knox/Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Knox Gateway HTTPS URL - " + oozieKnoxGatewayHttpsURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder + "'\n"; 				        
//			} else {
//				testRecordInfo = "-*-*- 'Failed' -  # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Knox/Oozie Workflow MapReduce-Word-Counting MapReduce Action - Job Submitted"
//						+ "\n          via Knox Gateway HTTPS URL - " + oozieKnoxGatewayHttpsURL
//						+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
//						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
//				        + "\n  --(2) Present Oozie MapReduce Action Output HDFS Folder:  '" + knoxOozieMRActionOutputFolder + "'\n"; 
//			}
//			writeDataToAFile(dcTestOozie_RecFilePathAndName, testRecordInfo, true);
//			prevTime = currTime;
//		}//end 8.2
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Oozie Workflow Actions on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Oozie Testing Generated Total Critical " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Oozie Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Oozie Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Oozie Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestOozie_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestOozie_RecFilePathAndName));
	
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
	
	private static void updateLocalOozieJobPropertiesFile (String localOozieJobPropertiesFileFullPathAndName, String nameNodeStr, String jobTrackerStr, String userName){
		ArrayList<String> newOozieMRJobPropLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localOozieJobPropertiesFileFullPathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("nameNode=")){
					line = "nameNode=" + nameNodeStr;
				}
				if (line.contains("jobTracker=")){
					line = "jobTracker=" + jobTrackerStr;
				}	
				if (line.contains("userName=")){
					line = "userName=" + userName;
				}		
				newOozieMRJobPropLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localOozieJobPropertiesFileFullPathAndName);				
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
		
		System.out.println("\n*** Done - Updating Local Oozie ... Action Job Properties File!!!\n");
		
	}//end updateLocalOozieJobPropertiesFile
	
	private static void moveWindowsLocalOozieTestDataToHDFS (String localWinSrcOozieTestDataFilePathAndName, 
							String hdfsOozieTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcOozieTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
		//System.out.println("\n*** localWinSrcOozieTestDataFilePathAndName: " + localWinSrcOozieTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/Oozie/dcOozieTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsOozieTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing Oozie file: " + hdfsOozieTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsOozieTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
						
			FileReader aFileReader = new FileReader(localWinSrcOozieTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localOozieTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {				
			e.printStackTrace();			
		}//end try 		
	}//end moveWindowsLocalOozieTestDataToHDFS

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
