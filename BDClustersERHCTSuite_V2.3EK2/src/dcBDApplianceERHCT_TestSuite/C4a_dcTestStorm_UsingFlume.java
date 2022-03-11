package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.DayClock;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/13-18/2014; 3/14/2016; 5/2/2016 
*/ 

@SuppressWarnings("unused")
public class C4a_dcTestStorm_UsingFlume {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localStormTestDataFileName = "";
	private static String stormTestFolderName = "";
	private static String localStormFlumeConfigFileName = "";
	private static String localStormJarFileName = "";	
	private static String esClusterName = "";	
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String esClusterUserName = "";
	private static String esClusterPassWord = "";	
	
	// /usr/bin/flume-ng (<=TDH2.1.11) ==> /usr/bin/flume-ng or /usr/hdp/2.3.4.0-3485/flume/bin/flume-ng (TDH2.3.4)
	// /usr/bin/storm (<=TDH2.1.11) ==> /usr/bin/storm or /usr/hdp/2.3.4.0-3485/storm/bin/storm (TDH2.3.4)
	// /usr/lib/ (<=TDH2.1.11) ==> /usr/hdp/2.3.4.0-3485/ (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for Storm-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localStormTestDataFileName = args[4];
		stormTestFolderName = args[5];
		localStormFlumeConfigFileName = args[6];
		localStormJarFileName = args[7]; 
		esClusterName = args[8]; 
		internalKinitCmdStr = args[9];
				
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos		
		String loginUser4AllNodesName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		System.out.println("*** loginUser4AllNodesName is: " + loginUser4AllNodesName);		
		String loginUserMC_AD_Pw = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		System.out.println("\n*** loginUserMC_AD_Pw is: " + loginUserMC_AD_Pw);
		
		stormTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Storm/";//Modify esTestFolderName from "/data/test/Storm/"
		
		if (bdClusterName.equalsIgnoreCase("BDDev1")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			//esClusterUserName = "es_admin";
			//esClusterPassWord = "admin4dev";
			esClusterUserName = loginUser4AllNodesName;
			esClusterPassWord = loginUserMC_AD_Pw;
		}
		
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			esClusterUserName = "es_admin";
			esClusterPassWord = "admin4int";
			//esClusterUserName = loginUser4AllNodesName;
			//esClusterPassWord = loginUserMC_AD_Pw;
			
		}
		if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//esClusterUserName = "esearch";
			//esClusterPassWord = "esearchpw";
			esClusterUserName = "es_admin";
			esClusterPassWord = "admin4int";
		}
		if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
				||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
				|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			esClusterUserName = loginUser4AllNodesName;
			esClusterPassWord = loginUserMC_AD_Pw;
		}
		
		if (!stormTestFolderName.endsWith("/")){
			stormTestFolderName += "/";
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
		String stormScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Storm\\";
		prepareFolder(stormScriptFilesFoder, "Local Storm Testing Script Files");
			    
		String dcTestStorm_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestStorm_UsingFlumeAndElasticSearch_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestStorm_RecFilePathAndName,  "Records of Testing Storm on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Storm Internal Storing File Into HDFS Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		String hdfsNnIPAddressAndPort = currBdCluster.getBdHdfs1stNnIPAddressAndPort(); 
		System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();		
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5
		
		String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String stormInternalPrincipal = currBdCluster.getStormInternalPrincipal();
		//String stormInternalKeyTabFilePathAndName = currBdCluster.getStormInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
			
		//Get the local Flume configuration file path and name for Kerberos authenitcation using a principal and a keytab
		if (localStormFlumeConfigFileName.endsWith("FlumeConf.txt")){
			localStormFlumeConfigFileName = localStormFlumeConfigFileName.replace("FlumeConf.txt", "FlumeConf_krb.txt");
		}		
		String localStormFlumeConfigFileFullPathAndName= bdClusterUATestResultsParentFolder + localStormFlumeConfigFileName;
		
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();	
								
		//4. Loop through bdClusterEntryNodeList to start a flume agent, storm topology that uses the flume agent to store data
		//     a HDFS file
		//clusterENNumber_Start = 6; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6
		//int clusterENNumber_Start_i = 7; 
		writeDataToAFile(dcTestStorm_RecFilePathAndName, "[1]. Storm-Flume-HDFS \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Storm-Storing Data Into HDFS File on Entry Node: " + tempENName);
			
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
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			//LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			//System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);			
			String enServerLoginUserKeytabsDirectory  = enServerScriptFileDirectory + "keytabs/";
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerLoginUserKeytabsDirectory, bdENCmdFactory);
			
			String enServerLoginUserFlumeCheckPointDirectory = enServerScriptFileDirectory + "flume/drain/checkpoint/";
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerLoginUserFlumeCheckPointDirectory, bdENCmdFactory);
			
			String enServerLoginUserFlumeDataDirectory = enServerScriptFileDirectory + "flume/drain/data/";
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerLoginUserFlumeDataDirectory, bdENCmdFactory);
			
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory + its 3 sub-folders: " + enServerLoginUserKeytabsDirectory + " etc");
					
								
			//String localDownloadedHdfsKeytabFileName = obtainLocalHdfsKeytabFileName_Old (bdClusterName);	
			String localDownloadedHdfsKeytabFilePathAndName = currBdCluster.getHdfsExternalKeyTabFilePathAndName();
			System.out.println("\n*-*=*-* this.hdfsExternalKeyTabFilePathAndName: " + localDownloadedHdfsKeytabFilePathAndName);
			String localDownloadedHdfsKeytabFileName = obtainLocalHdfsKeytabFileName_New (localDownloadedHdfsKeytabFilePathAndName);
			String localDownloadedHdfsKeytabFileFolder = localDownloadedHdfsKeytabFilePathAndName.replaceAll(localDownloadedHdfsKeytabFileName, "");
			if (localDownloadedHdfsKeytabFileFolder.startsWith("/")){
				//String [] tempDirSplit = localDownloadedHdfsKeytabFileFolder.split("/");
				localDownloadedHdfsKeytabFileFolder = localDownloadedHdfsKeytabFileFolder.replaceFirst("/", "");
			}
			//System.out.println("\n*-*0*-* localDownloadedHdfsKeytabFileFolder: " + localDownloadedHdfsKeytabFileFolder);
			
			localDownloadedHdfsKeytabFileFolder = localDownloadedHdfsKeytabFileFolder.replace("/", "\\");
			if (!localDownloadedHdfsKeytabFileFolder.endsWith("\\")){
				localDownloadedHdfsKeytabFileFolder += "\\";
			}			
			System.out.println("\n*-*1*-* localDownloadedHdfsKeytabFileName: " + localDownloadedHdfsKeytabFileName);
			System.out.println("\n*-*2*-* localDownloadedHdfsKeytabFileFolder: " + localDownloadedHdfsKeytabFileFolder);
									
			hdfsInternalKeyTabFilePathAndName = enServerLoginUserKeytabsDirectory + localDownloadedHdfsKeytabFileName;
			updateLocalStormFlumeConfFile (localStormFlumeConfigFileFullPathAndName, hdfsInternalPrincipal, hdfsInternalKeyTabFilePathAndName);			
			updateLocalStormFlumeConfFile2 (localStormFlumeConfigFileFullPathAndName, enServerLoginUserFlumeCheckPointDirectory, enServerLoginUserFlumeDataDirectory, stormTestFolderName);
			
			
			//(1) Move test data file, Storm-Flume configuration file, and Storm jar  file into Entry node /home/hdfs/ folder
			//String enServerScriptFileDirectory = enServerScriptFileDirectory;
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localStormTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localStormFlumeConfigFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
			//int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localDownloadedHdfsKeytabFileName, 
			//		    bdClusterUATestResultsParentFolder + "keytabs/", enServerLoginUserKeytabsDirectory, bdENCmdFactory);
			
			//H:\dcJavaWorkSpaces\dcBigDataWorkSpace_Win7\DC_BDClusterERHCTSuite_V2EK\src\dcModelClasses\ApplianceEntryNodes\hdfs.headless_BDDev1.keytab
		    int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localDownloadedHdfsKeytabFileName, 
		    		localDownloadedHdfsKeytabFileFolder, enServerLoginUserKeytabsDirectory, bdENCmdFactory);
			
			int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localStormJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Storm Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Storm Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Storm Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Storm Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving hdfs Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving hdfs Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal4 == 0 ){
				System.out.println("\n*** Done - Moving Storm Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Storm Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
						
			
			//(2) Testing Storm-Flume storing data in a test-data file into HDFS files			
			//String enServerNohupFilePathAndName = enServerScriptFileDirectory + "nohup.out";
			String enServerStormTestDataFilePathAndName = enServerScriptFileDirectory + localStormTestDataFileName;
			String enServerStorm_FlumeConfigFilePathAndName = enServerScriptFileDirectory + localStormFlumeConfigFileName;
			String enServerStormJarFilePathAndName = enServerScriptFileDirectory + localStormJarFileName;
						
			
					
			String stormFlumeStoringHdfsFileStatusFileName =  "stormFlumeStoringHdfsFileStatus_" + (i+1) + ".txt";			
			String enLocalStormFlumeStoringHdfsFileStatusFilePathAndName = enServerScriptFileDirectory + stormFlumeStoringHdfsFileStatusFileName;
			//String stormFlumeStoringHdfsFileStatusRetrieveCmd = "grep Renaming nohup.out > " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName;
			
			String flumeNgInitiateStr = "/usr/bin/flume-ng agent ";						
			String stormFlumeStoringDataIntoHDFSFileCmd =  flumeNgInitiateStr 					
					+ "--conf-file " + enServerStorm_FlumeConfigFilePathAndName + " -name fileToHdfs -Dstorm.root.logger=DEBUG,console > " 
					+ enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + " 2>&1 & sleep 125";
			///home/hdfs/dcStormFlumeConf.txt
			
			String flumeHostName = bdENCmdFactory.getServerURI();			
			String stormInitiateStr = "/usr/bin/storm ";	
			String stormTopologyStartCmd =  stormInitiateStr + " jar "
							+  enServerStormJarFilePathAndName + " topology.TestFlumeTopology "
							+ flumeHostName + " " + enServerStormTestDataFilePathAndName + "";
			String stormTopologyStopCmd =  stormInitiateStr + "kill testFlumeTopology & sleep 15"; 
			
			//storm  jar StormTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar topology.TestFlumeTopology 
			//flumeHostName = "hdpr03en01.mayo.edu"; //inputDataFilePathAndName = "C:\\BD\\BD_ERHCT\\dcFSETestData.txt";
			//storm kill testFlumeTopology
			
			//String killStormFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			String killStormFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f3,4,5,6 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			
			
			String hdfsStormFlumeStoringHdfsFileStatusFilePathAndName = stormTestFolderName + stormFlumeStoringHdfsFileStatusFileName;
			hdfsFilePathAndNameList.add(hdfsStormFlumeStoringHdfsFileStatusFilePathAndName);
			
			String enServerStormConfFolderPathAndName = enServerScriptFileDirectory.replace("/test", "/.storm");
		    String enServerLoginUserFlumeDirectory = enServerLoginUserFlumeDataDirectory.replace("/drain/data/", "/");
			//enServerLoginUserFlumeDataDirectory: /data/home/m041785/test/flume/drain/data .../home/hdfs/flume/drain/data
			
		    //sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
		    //sb.append("cd " + enServerScriptFileDirectory + ";\n");
		    //sb.append("rm -f -R /home/hdfs/flume/;\n");
			//sb.append("mkdir -p /home/hdfs/flume/drain/checkpoint;\n");
			//sb.append("mkdir -p /home/hdfs/flume/drain/data;\n");	
		    sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			sb.append("rm -f -R " + enServerLoginUserFlumeDirectory + ";\n");
			//sb.append("mkdir -p " + enServerLoginUserFlumeDataDirectory + ";\n");
			//sb.append("mkdir -p " + enServerLoginUserFlumeCheckPointDirectory + ";\n");
			
			sb.append("kdestroy;\n");
			sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
						
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + stormTestFolderName + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + stormTestFolderName + "; \n");		   
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + stormTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 777 " + stormTestFolderName + "; \n");
		    sb.append("hadoop fs -rm -r -skipTrash " + stormTestFolderName + "dcStormFlumeTestData*tmp; \n");
			
		    String enServerScriptFileDirectory_parent = "/data/home/" + loginUserName;
		    sb.append("cd " + enServerScriptFileDirectory_parent + ";\n");	
			sb.append("ln -s /data/home/.storm .storm;\n");	
			
		    sb.append("cd " + enServerStormConfFolderPathAndName + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
		    ////sb.append("rm -f " + enServerNohupFilePathAndName + "; \n");
		    sb.append("export JAVA_HOME=/opt/teradata/jvm64/jdk7;\n");
		    sb.append("export PATH=$PATH:$STORM_HOME/bin:$JAVA_HOME/bin; \n");	
		    
		    ////sb.append("sudo su - storm;\n");
			////sb.append("kdestroy;\n");
			////sb.append("kinit  " + stormInternalPrincipal + " -kt " + stormInternalKeyTabFilePathAndName + "; \n");	
		    sb.append(stormTopologyStopCmd + ";\n");
		    //sb.append("sleep 15;\n");
		    sb.append(stormTopologyStartCmd + ";\n");
		    sb.append(stormFlumeStoringDataIntoHDFSFileCmd + ";\n"); 
		    //sb.append("sleep 15;\n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + " " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		    
		    sb.append(stormTopologyStopCmd + ";\n");
		    //sb.append("Thread.sleep(35000);\n");
		    sb.append(killStormFlumeNgCmd + ";\n");
		    
		    sb.append("kdestroy;\n");
		    sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
		 	sb.append("hadoop fs -chmod -R 550 " + stormTestFolderName + "; \n");
		 	
		 	//sb.append("rm -f " + enServerStormTestDataFilePathAndName + "; \n");		    
		    //sb.append("rm -f " + enServerStormJarFilePathAndName + "; \n");	
		    //sb.append("rm -f " + enServerStorm_FlumeConfigFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");
		   	
		    		   
		    String stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName = stormScriptFilesFoder + "dcTestStorm_FlumeStoringDataIntoHdfsFileScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName,  "Script File For Testing Storm Flume-Storing Daat Into HDFS File on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String stormStoringHdfsFileCmds = sb.toString();
			writeDataToAFile(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName, stormStoringHdfsFileCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName, 
					stormScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
					
				
			System.out.println("\n*** hdfsStormFlumeStoringHdfsFileStatusFilePathAndName: " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName );
			boolean stormStoringSuccess = false;
			String stormStoreHdfsFilePathAndName = "";
			String targetFoundString1 = "Renaming " + stormTestFolderName;
			System.out.println("\n*** targetFoundString1: " + targetFoundString1 );			
			String targetFoundString2 = "/dcStormFlumeTestData.";
			System.out.println("\n*** targetFoundString2: " + targetFoundString2 );
			
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsStormFlumeStoringHdfsFileStatusFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains(targetFoundString1) 
							&& line.contains(targetFoundString2)
							&& line.contains(".tmp to")) {
						String[] lineSplit = line.split(" to ");
						stormStoreHdfsFilePathAndName = lineSplit[1].trim();
						hdfsFilePathAndNameList.add(stormStoreHdfsFilePathAndName);
						System.out.println("*** add: " + stormStoreHdfsFilePathAndName );
						
						stormStoringSuccess = true;
						break;
					}								
				}//end while
				br.close();				
			} catch (IOException e) {				
				e.printStackTrace();				
			}//end try   
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";			
			if (stormStoringSuccess == true){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storm-Using-Flume To Store Data of a Test-Data File On Entry Node into a HDFS File"
						+ "\n          on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName  + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed	
				        + "\n  --(2) Present Storm-Using-Flume Testing-Generated Storing-Status HDFS File:  '" + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "'"
				        + "\n  --(3) Present Storm-Using-Flume Testing-Generated Stored-Data HDFS File:  '" + stormStoreHdfsFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storm-Using-Flume To Store Data of a Test-Data File On Entry Node into a HDFS File"
						+ "\n          on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName  + "'" 
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed	
				        + "\n  --(2) Present Storm-Using-Flume Testing-Generated Storing-Status HDFS File:  '" + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "'"
				        + "\n  --(3) Present Storm-Using-Flume Testing-Generated Stored-Data HDFS File:  '" + stormStoreHdfsFilePathAndName + "'\n";	 
			}
			writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecordInfo, true);	
			
			int nodeNum4ContinuousTesting = clusterENNumber - i - 1;
			int sleepSec = 15;	//15..30	   
		    if (nodeNum4ContinuousTesting > 0 ){		    	
				writeDataToAFile(dcTestStorm_RecFilePathAndName, "*** --Storm-Flume--  Program waits for "+ sleepSec + " seconds for Storm topology fully killed ...\n", true);
				Thread.sleep(sleepSec*1000);
		    }
			
		    currClock = new DayClock();				
			currTime = currClock.getCurrentDateTime();
		    prevTime = currTime;
		}//end for
		
			
			
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally Storm Using Flume and ElasticSearch on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Storm Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS File(s)!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Storm Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Storm Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Storm Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		//System.out.println(currNotingMsg);
		writeDataToAFile(dcTestStorm_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestStorm_RecFilePathAndName));
	
	}//end run()
	

	private static void updateLocalStormFlumeConfFile (String localStormFlumeConfFileName, String flumePrincipalName,
			String flumeKeyTabFilePathAndName){
		ArrayList<String> oldFlumeConfLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localStormFlumeConfFileName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("fileToHdfs.sinks.k1.hdfs.kerberosPrincipal = ")){
					line = "fileToHdfs.sinks.k1.hdfs.kerberosPrincipal = " + flumePrincipalName;
				}
				if (line.contains("fileToHdfs.sinks.k1.hdfs.kerberosKeytab = ")){
					line = "fileToHdfs.sinks.k1.hdfs.kerberosKeytab = " + flumeKeyTabFilePathAndName;
				}				
				oldFlumeConfLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localStormFlumeConfFileName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);	
			for (int j = 0; j < oldFlumeConfLineList.size(); j++){
				String tempLine = oldFlumeConfLineList.get(j);
				output.println(tempLine);
				System.out.println(tempLine);;
			}//end for
			output.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Updating Local Flume Configuration File for Kerberose Authentication!!!\n");
		
	}//updateLocalStormFlumeConfFile
	
	private static String obtainLocalHdfsKeytabFileName_New (String localDownloadedHdfsKeytabFilePathAndName){
		String localHdfsKeytabFileName = "";
		localDownloadedHdfsKeytabFilePathAndName = localDownloadedHdfsKeytabFilePathAndName.replace("\\", "/");
		if (localDownloadedHdfsKeytabFilePathAndName.contains("/")){
			String[] tempSplit = localDownloadedHdfsKeytabFilePathAndName.split("/");
			int maxIndex = tempSplit.length - 1;
			localHdfsKeytabFileName = tempSplit [maxIndex];
		}
		
		return localHdfsKeytabFileName;
		
	}//end obtainLocalHdfsKeytabFileName_New
	
	
	private static String obtainLocalHdfsKeytabFileName_Old (String aBdClusterName) {
		if (aBdClusterName.equalsIgnoreCase("BDProd2")){
			aBdClusterName = "BDProd2";
		}
		if (aBdClusterName.equalsIgnoreCase("BDTest2")
				||aBdClusterName.equalsIgnoreCase("BDTest2") ){
			aBdClusterName = "BDTest2";
		}
		
		String localHdfsKeytabFileName = "";
		if (aBdClusterName.equalsIgnoreCase("BDProd2")
				|| aBdClusterName.equalsIgnoreCase("Int")
				|| aBdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//localHdfsKeytabFileName = "BDProd2_hdfs.headless.keytab";	
			localHdfsKeytabFileName = "BDProd2_hdfs.headless.keytab";//Post cluster conversion: BDProd21 or BDTest1 ==> BDProd2		
		}			
		if (aBdClusterName.equalsIgnoreCase("BDTest2") || aBdClusterName.equalsIgnoreCase("BDPrd")
				|| aBdClusterName.equalsIgnoreCase("Prd") || aBdClusterName.equalsIgnoreCase("Prod")
				|| aBdClusterName.equalsIgnoreCase("MC_BDPrd") || aBdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//localHdfsKeytabFileName = "BDProd_hdfs.headless.keytab";
			localHdfsKeytabFileName = "BDTest2_hdfs.headless.keytab"; //Post cluster conversion: BDProd1 ==> BDTest2 or BDTest2
		}
		
		if (aBdClusterName.equalsIgnoreCase("BDDev1")
				|| aBdClusterName.equalsIgnoreCase("Dev")
				|| aBdClusterName.equalsIgnoreCase("MC_BDDev1")){
			localHdfsKeytabFileName = "BDDev1_hdfs.headless.keytab";			
		}						
		if (aBdClusterName.equalsIgnoreCase("BDSbx")|| aBdClusterName.equalsIgnoreCase("BDSdbx")
				||aBdClusterName.equalsIgnoreCase("Sbx")|| aBdClusterName.equalsIgnoreCase("Sdbx")
				|| aBdClusterName.equalsIgnoreCase("MC_BDSbx") || aBdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			localHdfsKeytabFileName = "BDSdbx_hdfs.headless.keytab";			
		}
		
		return localHdfsKeytabFileName;
	}//end obtainLocalHdfsKeytabFileName
		
				
	private static void updateLocalStormFlumeConfFile2 (String localStormFlumeConfFileName, String enServerLoginUserFlumeCheckPointDirectory,
						 	String enServerLoginUserFlumeDataDirectory, String stormTestFolderName){
		ArrayList<String> oldFlumeConfLineList = new ArrayList<String>();
			
		try {
			FileReader aFileReader = new FileReader(localStormFlumeConfFileName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("fileToHdfs.channels.c1.checkpointDir = ")){
					line = "fileToHdfs.channels.c1.checkpointDir = " + enServerLoginUserFlumeCheckPointDirectory;  ///data/home/m041785/test/flume/drain/checkpoint.../home/hdfs/flume/drain/checkpoint
				}
				if (line.contains("fileToHdfs.channels.c1.dataDirs = ")){
					line = "fileToHdfs.channels.c1.dataDirs = " + enServerLoginUserFlumeDataDirectory; // /data/home/m041785/test/flume/drain/data .../home/hdfs/flume/drain/data
				}
				
				if (line.contains("fileToHdfs.sinks.k1.hdfs.path = ")){
					line = "fileToHdfs.sinks.k1.hdfs.path = " + stormTestFolderName; // /user/m041785/test/Storm/ .../data/test/Storm/ 
				}				
				oldFlumeConfLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localStormFlumeConfFileName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);	
			for (int j = 0; j < oldFlumeConfLineList.size(); j++){
				String tempLine = oldFlumeConfLineList.get(j);
				output.println(tempLine);
				System.out.println(tempLine);;
			}//end for
			output.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Updating Local Flume Configuration File for Kerberose Authentication!!!\n");
		
	}//updateLocalStormFlumeConfFile2
	
	
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
