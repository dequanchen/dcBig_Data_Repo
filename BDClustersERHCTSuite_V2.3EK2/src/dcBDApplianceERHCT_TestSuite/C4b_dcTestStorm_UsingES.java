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
* Date: 12/13-18/2014; 3/14/2016; 7/8/2016 
*/ 

@SuppressWarnings("unused")
public class C4b_dcTestStorm_UsingES {
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
			//esClusterUserName = "es_admin";
			//esClusterPassWord = "admin4int";
			//https://hdpr01en01.mayo.edu:8200/_plugin/head/
			esClusterUserName = loginUser4AllNodesName;
			esClusterPassWord = loginUserMC_AD_Pw;
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
								
		//4. Deleted - Storm-Flume-HDFS
	
		
		//5. Loop through bdClusterEntryNodeList to delete and create an ElasticSearch index, create
		//     a ElasticSearch document(s) and refresh the ES Index and querying the index	
		//First modify clusterENNumber for elasticsearch
		for (int i = 0; i < bdClusterEntryNodeList.size(); i++ ){
			String tempNodeName = bdClusterEntryNodeList.get(i).toUpperCase();
			if (tempNodeName.contains("MN")
					|| tempNodeName.contains("KX")
					){
				clusterENNumber--;
			}
		}
		
		
		writeDataToAFile(dcTestStorm_RecFilePathAndName, "\n[2]. Storm-ElasticSearch \n", true);	
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6	
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing ElasticSearch-Storing Data Into HDFS File on Entry Node: " + tempENName);
			
			
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
			
			//String localDownloadedStormKeytabFileName = obtainLocalStormKeytabFileName (bdClusterName);
			//stormInternalKeyTabFilePathAndName = enServerLoginUserKeytabsDirectory + localDownloadedStormKeytabFileName;
			
			//(1) Get Elastic Search Host Web IP Address and Port String
			//BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			//ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			String enIpAddressStr = bdENCmdFactory.getServerURI();
			enIpAddressStr = enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01");
			System.out.println(" *** enIpAddressStr: " + enIpAddressStr);
			String esHostName = enIpAddressStr;
			System.out.println(" *** esHostName: " + esHostName);
			
			String esHostWebIpAddressAndPort = "http://" + enIpAddressStr + ":9200"; //enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01") + ":9200";
			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDTest2")){
				esHostWebIpAddressAndPort = esHostWebIpAddressAndPort.replace("en01", "en02");
			}
			System.out.println(" *** esHostWebIpAddressAndPort: " + esHostWebIpAddressAndPort);
			
			
			//(2) Move test data file, and Storm jar  file into Entry node folder - enServerScriptFileDirectory			
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localStormTestDataFileName,
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localStormJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localDownloadedStormKeytabFileName, 
			//	    bdClusterUATestResultsParentFolder + "keytabs/", enServerLoginUserKeytabsDirectory, bdENCmdFactory);
		
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Storm Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Storm Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
								
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving StormTest Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving StormTest Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			//if (exitVal3 == 0 ){
			//	System.out.println("\n*** Done - Moving storm Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			//} else {
			//	System.out.println("\n*** Failed - Moving storm Keytab File into '" + enServerLoginUserKeytabsDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			//}
			
			//(3) Testing ElasticSearch Index Deleting, Creating, Loading and Searching				
			String enServerStormTestDataFilePathAndName = enServerScriptFileDirectory + localStormTestDataFileName;			
			String enServerStormJarFilePathAndName = enServerScriptFileDirectory + localStormJarFileName;
			String enServerStormConfFolderPathAndName = enServerScriptFileDirectory.replace("/test", "/.storm");
			
			String userAuthenStr = " -v --user " + esClusterUserName + ":" + esClusterPassWord + " "; //BDDev1: es_admin:admin4dev
			
			String esTestIndexSearchingStatusFileName =  "esTestIndexSearchStatus_" + (i+1) + ".txt";			
			String enEsTestIndexSearchingStatusFilePathAndName = enServerScriptFileDirectory + esTestIndexSearchingStatusFileName;
			String hdfsESTestIndexSearchingStatusFileNameFilePathAndName = stormTestFolderName + esTestIndexSearchingStatusFileName;
			hdfsFilePathAndNameList.add(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);
			
			String deleteEsTestIndexCmd = "curl" + userAuthenStr + "-XDELETE '" + esHostWebIpAddressAndPort + "/estest/'";
			String createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
					+ "    \"settings\" : {\n"
					+ "        \"index\" : {\n"
					+ "            \"number_of_shards\" : 5,\n"
					+ "            \"number_of_replicas\" : 1\n"
					+ "        }\n"
					+ "    }\n"
					+ "}'";
			if (clusterENNumber >= 2) {
				createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
						+ "    \"settings\" : {\n"
						+ "        \"index\" : {\n"
						+ "            \"number_of_shards\" : 5,\n"
						+ "            \"number_of_replicas\" : 2\n"
						+ "        }\n"
						+ "    }\n"
						+ "}'";
				
			}
			//String createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '\n"
					//+ "index :\n"
					//+ "    number_of_shards : 3\n"
					//+ "    number_of_replicas : 2 \n"
					//+ "'";
			
						
			//String loadGreetingDocCmd1 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/1' -d '{ \"title\":\"Hello Sam\" }'";
			//String loadGreetingDocCmd2 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/2' -d '{ \"title\":\"Hello Tom\" }'";
			String stormInitiateStr = "/usr/bin/storm ";	
			String stormTopologyStartCmd =  stormInitiateStr + " jar "
							+  enServerStormJarFilePathAndName + " topology.TestESTopology "
							+ esHostName + " " + esClusterName + " " + enServerStormTestDataFilePathAndName + " " + esClusterUserName + " " + esClusterPassWord  + " & sleep 95";
			String stormTopologyStopCmd =  stormInitiateStr + "kill testESTopology"; 
			
			
			
			String refreshEsTestIndexCmd = "curl" + userAuthenStr + "-XPOST '" + esHostWebIpAddressAndPort + "/estest/_refresh'";
			
			String searchEsTestIndexCmd = "curl" + userAuthenStr + "-XGET '" + esHostWebIpAddressAndPort + "/estest/employee/_search?pretty=true,q=employeeId:101' > " + enEsTestIndexSearchingStatusFilePathAndName+ " 2>&1";
			
			//sb.append("sudo su - hdfs;\n");
			//sb.append("hadoop fs -mkdir -p " + stormTestFolderName + "; \n");		   
		    //sb.append("hadoop fs -chmod -R 750 " + stormTestFolderName + "; \n");			    
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerStormConfFolderPathAndName + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append(deleteEsTestIndexCmd + ";\n");	
		    sb.append(createEsTestIndexCmd + ";\n");			    
			//sb.append(stormTopologyStopCmd + ";\n");
			//sb.append("sleep 30;\n");
			
		    sb.append("hadoop fs -mkdir -p " + stormTestFolderName + "; \n");
		    sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + stormTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + stormTestFolderName + "; \n");	
		    
		    sb.append(stormTopologyStopCmd + ";\n");
		    sb.append("sleep 15;\n");
		   	sb.append(stormTopologyStartCmd + ";\n");
		   	sb.append(refreshEsTestIndexCmd + ";\n");
			//sb.append("sleep 20;\n");			
		    sb.append(searchEsTestIndexCmd + ";\n");	
		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enEsTestIndexSearchingStatusFilePathAndName + " " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + stormTestFolderName + "; \n");    		    
		   
		   	sb.append(stormTopologyStopCmd + ";\n");
		    sb.append(deleteEsTestIndexCmd + ";\n");		
		    
		    //sb.append("rm -f " + enServerStormTestDataFilePathAndName + "; \n");		    
		    //sb.append("rm -f " + enServerStormJarFilePathAndName + "; \n");
		    //sb.append("rm -f " + enEsTestIndexSearchingStatusFilePathAndName + "; \n");
		    //sb.append("rm -f " + enEsTestIndexSearchingStatusFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");					
					        
		    
		    String esIndexIndexingAndSearchingTestScriptFilePathAndName = stormScriptFilesFoder + "dcTestES_IndexingAndSearchingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (esIndexIndexingAndSearchingTestScriptFilePathAndName,  "Script File For Testing ElasticSearch Indexing and Searching on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String esIndexIndexingAndSearchingCmds = sb.toString();
			writeDataToAFile(esIndexIndexingAndSearchingTestScriptFilePathAndName, esIndexIndexingAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(esIndexIndexingAndSearchingTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(esIndexIndexingAndSearchingTestScriptFilePathAndName, 
					stormScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			Path filePath = new Path(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);
			boolean esStatusHdfsFileExistingStatus = false;
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);	
				esStatusHdfsFileExistingStatus = true;				
			}
			System.out.println("\n*** Exisiting status for " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + ": " + esStatusHdfsFileExistingStatus);
			
			
			System.out.println("\n*** hdfsESTestIndexSearchingStatusFileNameFilePathAndName: " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName );
			
			String targetFoundString = "\"_source\":{\"employeeId\":\"10";			
			boolean esSearchingSuccessStatus = false;			
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsESTestIndexSearchingStatusFileNameFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains(targetFoundString)) {												
						esSearchingSuccessStatus = true;
						System.out.println("*** found: " + targetFoundString );
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
			if (esSearchingSuccessStatus == true){
				successTestScenarioNum++;				
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Internally Storm-Using-ElasticSearch for Index Deleting, Creating, Loading, Refreshing and "
						+ "\n           Searching on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + tempTime
				        + "\n  -- (2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'"
				        + "\n  -- (3) ElasticSearch Testing Total Time Used: " + timeUsed + "\n"; 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Internally Storm-Using-ElasticSearch for Index Deleting, Creating, Loading, Refreshing and "
						+ "\n           Searching on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + tempTime
				        + "\n  -- (2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'"
				        + "\n  -- (3) ElasticSearch Testing Total Time Used: " + timeUsed + "\n"; 
			}
			writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecordInfo, true);	
						
			int nodeNum4ContinuousTesting = clusterENNumber - i - 1;
			int sleepSec = 15;		   
		    if (nodeNum4ContinuousTesting > 0 ){				
				writeDataToAFile(dcTestStorm_RecFilePathAndName, "*** --Storm-ES--  Program waits for "+ sleepSec + " seconds for Storm topology fully killed ...\n", true);
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
	
	private static String obtainLocalHdfsKeytabFileName (String bdClusterName) {
		if (bdClusterName.equalsIgnoreCase("BDProd2")){
			bdClusterName = "BDProd2";
		}
		if (bdClusterName.equalsIgnoreCase("BDTest2")
				||bdClusterName.equalsIgnoreCase("BDTest2") ){
			bdClusterName = "BDTest2";
		}
		
		String localHdfsKeytabFileName = "";
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//localHdfsKeytabFileName = "BDProd2_hdfs.headless.keytab";	
			localHdfsKeytabFileName = "BDProd2_hdfs.headless.keytab";			
		}			
		if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			localHdfsKeytabFileName = "BDProd_hdfs.headless.keytab";		
		}
		
		if (bdClusterName.equalsIgnoreCase("BDDev1")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			localHdfsKeytabFileName = "BDDev1_hdfs.headless.keytab";			
		}						
		if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
				||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
				|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
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
