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
* Date: 12/11/2014; 3/14/2016 
*/ 


public class C1_dcTestFlume_StoringData {
	private static int testingTimesSeqNo = 3;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localFlumeTestDataFileName = "";
	private static String flumeTestFolderName = "";
	private static String localFlumeConfigFileName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/flume/bin/flume-ng (<=TDH2.1.11) ==>  /usr/bin/flume-ng or /usr/hdp/2.3.4.0-3485/flume/bin/flume-ng (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 7+1 parameters for Flume-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localFlumeTestDataFileName = args[4];
		flumeTestFolderName = args[5];
		localFlumeConfigFileName = args[6];
		internalKinitCmdStr = args[9];
		
		if (!flumeTestFolderName.endsWith("/")){
			flumeTestFolderName += "/";
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
		String flumeScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Flume\\";
		prepareFolder(flumeScriptFilesFoder, "Local Flume Testing Script Files");
		    
		String dcTestFlume_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestFlume_StoringFileIntoHdfs_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestFlume_RecFilePathAndName,  "Records of Testing Flume on '" + bdClusterName + "' Cluster");	
	   
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Flume Internal Storing File Into HDFS Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestFlume_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
				
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();
		//if (clusterENNumber > 2){
		//	clusterENNumber = clusterENNumber-1;
		//}	
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		flumeTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Flume/";//Modify pigTestedRelationsFolder from "/data/test/Flume/"
		
		String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);
		
		
		//Get the local Flume configuration file path and name for Kerberos authenitcation using a principal and a keytab 
		if (localFlumeConfigFileName.endsWith("FlumeConf.txt")){
			localFlumeConfigFileName = localFlumeConfigFileName.replace("FlumeConf.txt", "FlumeConf_krb.txt");
		}		
		String localFlumeConfigFileFullPathAndName= bdClusterUATestResultsParentFolder + localFlumeConfigFileName;
			
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		//4. Loop through bdClusterEntryNodeList to start a flume agent  that reads data from a test-data file 
		//     and store data into a HDFS file
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Flume-Storing Data Into HDFS File\n         on Entry Node: " + tempENName);
			
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
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory + its sub-folder: " + enServerLoginUserKeytabsDirectory);
								
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
			updateLocalFlumeConfFile (localFlumeConfigFileFullPathAndName, hdfsInternalPrincipal, hdfsInternalKeyTabFilePathAndName);
			
			//(1) Move test data file, Flume configuration file, and ambari-qa keytab file into Entry node login user home  folder's test sub-folder (old: /home/hdfs/ folder)			
			//String enServerScriptFileDirectory = enServerScriptFileDirectory;
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localFlumeTestDataFileName, 
						bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localFlumeConfigFileName, 
						bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localDownloadedHdfsKeytabFileName, 
			//		    bdClusterUATestResultsParentFolder + "keytabs/", enServerLoginUserKeytabsDirectory, bdENCmdFactory);
			
			//H:\dcJavaWorkSpaces\dcBigDataWorkSpace_Win7\DC_BDClusterERHCTSuite_V2EK\src\dcModelClasses\ApplianceEntryNodes\hdfs.headless_BDDev1.keytab
		    int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localDownloadedHdfsKeytabFileName, 
		    		localDownloadedHdfsKeytabFileFolder, enServerLoginUserKeytabsDirectory, bdENCmdFactory);
					    
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Flume Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Flume Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Flume Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Flume Configuration File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving hdfs Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving hdfs Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			//(2) Testing Flume-storing data in a test-data file into HDFS files			
			//String enServerNohupFilePathAndName = enServerScriptFileDirectory + "nohup.out";
			String enServerFlumeTestDataFilePathAndName = enServerScriptFileDirectory + localFlumeTestDataFileName;
			String enServerFlumeConfigFilePathAndName = enServerScriptFileDirectory + localFlumeConfigFileName;			
			
			updateLocalFlumeConfFile2 (localFlumeConfigFileFullPathAndName, enServerFlumeTestDataFilePathAndName, flumeTestFolderName);
								
			String flumeStoringHdfsFileStatusFileName =  "flumeStoringHdfsFileStatus_" + (i+1) + ".txt";			
			String enLocalFlumeStoringHdfsFileStatusFilePathAndName = enServerScriptFileDirectory + flumeStoringHdfsFileStatusFileName;
			//String flumeStoringHdfsFileStatusRetrieveCmd = "grep Renaming nohup.out > " + enLocalFlumeStoringHdfsFileStatusFilePathAndName;
			
			String FlumeNgInitiateStr = "/usr/bin/flume-ng agent ";						
			String FlumeStoringDataIntoHDFSFileCmd =  FlumeNgInitiateStr 					
					+ "--conf-file " + enServerFlumeConfigFilePathAndName + " -name fileToHdfs -Dflume.root.logger=DEBUG,console > " 
					+ enLocalFlumeStoringHdfsFileStatusFilePathAndName + " 2>&1 & sleep 60";
			
			//String killFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			String killFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f3,4,5,6 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			
			
			String hdfsFlumeStoringHdfsFileStatusFilePathAndName = flumeTestFolderName + flumeStoringHdfsFileStatusFileName;
			System.out.println("\n*** hdfsFlumeStoringHdfsFileStatusFilePathAndName: " + hdfsFlumeStoringHdfsFileStatusFilePathAndName );		
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			
			//sb.append("ssh -t hdpr03en01.mayo.edu -A \"sudo /usr/sbin/ipset\";\n");
			//sb.append("ssh -t hdpr03en01.mayo.edu -A \"echo \"" + rootPw + "\" | sudo -s echo && sudo su - root\";\n");
			//sb.append("echo \"" + rootPw + "\" | sudo -s echo && sudo -i;\n");
			//sb.append("echo \"" + rootPw + "\" | sudo -s echo && sudo bash;\n");
			//sb.append("echo \"" + rootPw + "\" | sudo -S echo && sudo su - root;\n");					
			//sb.append("whoami;\n");
			//sb.append("pwd;\n");
			//sb.append("sleep 15;\n");
			sb.append("kdestroy;\n");
			sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + flumeTestFolderName + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + hdfsFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + flumeTestFolderName + "; \n");	
			//sb.append("hadoop fs -chown -R hdfs:hdfs " + sparkTestFolderName + "; \n");
			//sb.append("hadoop fs -chown -R ambari-qa:bdadmin " + flumeTestFolderName + "; \n");		    
		    sb.append("hadoop fs -chmod -R 777 " + flumeTestFolderName + "; \n");
		    
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			  
		    sb.append("export JAVA_HOME=/opt/teradata/jvm64/jdk7;\n");
		    sb.append("export PATH=$PATH:$STORM_HOME/bin:$JAVA_HOME/bin; \n");		    		    
		    sb.append(FlumeStoringDataIntoHDFSFileCmd + ";\n");	
		   
		    
		    sb.append("hadoop fs -copyFromLocal " + enLocalFlumeStoringHdfsFileStatusFilePathAndName + " " + hdfsFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		   	sb.append(killFlumeNgCmd + ";\n");
		   	
		    //sb.append("rm -f " + enServerNohupFilePathAndName + "; \n");
//		    sb.append("rm -f " + enLocalFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
//		    sb.append("rm -f " + enServerFlumeTestDataFilePathAndName + "; \n");
//		    sb.append("rm -f " + enServerFlumeConfigFilePathAndName + "; \n");
//		    sb.append("hadoop fs -chmod -R 550 " + flumeTestFolderName + "; \n");
//		    sb.append("kdestroy;\n");
		    	
			
		    String localFlumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName = flumeScriptFilesFoder + "dcTestFlume_StoringDataIntoHdfsFileScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localFlumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName,  "Script File For Testing Flume Storing Daat Into HDFS File\n         on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String flumeStoringHdfsFileCmds = sb.toString();
			writeDataToAFile(localFlumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName, flumeStoringHdfsFileCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localFlumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localFlumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName, 
					flumeScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//dcFSETestData.txt
			boolean flumeStoringSuccess = false;
			String flumeStoreHdfsFilePathAndName = "";
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsFlumeStoringHdfsFileStatusFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";
				String tgtTestStr = ".tmp to " + flumeTestFolderName + "dcFlumeTestData.";
				System.out.println("*** tgtTestStr: " + tgtTestStr );
				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains("Renaming ") && line.contains(tgtTestStr)) {//../data/test/Flume/dcFlumeTestData. ...
						String[] lineSplit = line.split(" to ");
						flumeStoreHdfsFilePathAndName = lineSplit[1].trim();
						hdfsFilePathAndNameList.add(flumeStoreHdfsFilePathAndName);
						System.out.println("*** add: " + flumeStoreHdfsFilePathAndName );
						
						flumeStoringSuccess = true;
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
			if (flumeStoringSuccess == true){
				successTestScenarioNum++;
				
				hdfsFilePathAndNameList.add(hdfsFlumeStoringHdfsFileStatusFilePathAndName);
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storing Data of a Test-Data File On Entry Node into a HDFS File"
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Flume Testing-Generated HDFS File:  '" + flumeStoreHdfsFilePathAndName + "'"
				        + "\n  --(3) Flume-Storing Data To HDFS Total Time Used: " + timeUsed + "\n"; 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storing Data of a Test-Data File On Entry Node into a HDFS File"
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present Flume Testing-Generated HDFS File:  '" + flumeStoreHdfsFilePathAndName + "'"
				        + "\n  --(3) Flume-Storing Data To HDFS Total Time Used: " + timeUsed + "\n"; 				    	    
			}
			writeDataToAFile(dcTestFlume_RecFilePathAndName, testRecordInfo, true);	
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
		currNotingMsg += "\n***** Done - Testing Internally Flume Storing Data Into HDFS File(s) on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Flume Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS File(s)!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Flume Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Flume Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Flume Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestFlume_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestFlume_RecFilePathAndName));
	
	}//end run()
	
	private static void updateLocalFlumeConfFile (String localFlumeConfigFileName, String flumePrincipalName,
			String flumeKeyTabFilePathAndName){
		ArrayList<String> oldFlumeConfLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localFlumeConfigFileName);
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
			
			File tempFile = new File(localFlumeConfigFileName);				
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
		
	}//updateLocalFlumeConfFile
	
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
	
	
	@SuppressWarnings("unused")
	private static String obtainLocalHdfsKeytabFileName_Old (String aBdClusterName) {	
				
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
		
				
	private static void updateLocalFlumeConfFile2 (String localFlumeConfigFileName, String enServerFlumeTestDataFilePathAndName,
								String flumeTestFolderName){
		ArrayList<String> oldFlumeConfLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localFlumeConfigFileName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("fileToHdfs.sources.r1.command = cat ")){
					line = "fileToHdfs.sources.r1.command = cat " + enServerFlumeTestDataFilePathAndName;
				}
				if (line.contains("fileToHdfs.sinks.k1.hdfs.path = ")){
					line = "fileToHdfs.sinks.k1.hdfs.path = " + flumeTestFolderName;
				}				
				oldFlumeConfLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localFlumeConfigFileName);				
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
		
	}//updateLocalFlumeConfFile2
	
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
