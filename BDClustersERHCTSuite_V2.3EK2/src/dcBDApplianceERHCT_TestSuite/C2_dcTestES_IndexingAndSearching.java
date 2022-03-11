package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
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
* Date: 12/12/2014; 3/14/2016; 3/25/2016; 3/7, 10/2017
*/ 


public class C2_dcTestES_IndexingAndSearching {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";	
	private static String esTestFolderName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String esClusterUserName = "";
	private static String esClusterPassWord = "";
	
	
	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for ElasticSearch-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		esTestFolderName = args[4];
		internalKinitCmdStr = args[9];
				
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		String loginUser4AllNodesName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		System.out.println("*** loginUser4AllNodesName is: " + loginUser4AllNodesName);		
		String loginUserMC_AD_Pw = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		System.out.println("\n*** loginUserMC_AD_Pw is: " + loginUserMC_AD_Pw);
		
		esTestFolderName = "/user/" + loginUser4AllNodesName + "/test/ElasticSearch/";//Modify esTestFolderName from "/data/test/ElasticSearch/"
		
		if (bdClusterName.equalsIgnoreCase("BDDev1")
				|| bdClusterName.equalsIgnoreCase("BDDev3")
				|| bdClusterName.equalsIgnoreCase("BDTest3")
				|| bdClusterName.equalsIgnoreCase("BDProd3")
				){
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
		
		if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDTest2")
				){
			//https://hdpr01en01.mayo.edu:8200/_plugin/head/
			//esClusterUserName = "esearch";
			//esClusterPassWord = "esearchpw";
			esClusterUserName = loginUser4AllNodesName;
			esClusterPassWord = loginUserMC_AD_Pw;
		}
		
		if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
				||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
				|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			esClusterUserName = loginUser4AllNodesName;
			esClusterPassWord = loginUserMC_AD_Pw;
		}
				
		if (!esTestFolderName.endsWith("/")){
			esTestFolderName += "/";
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
		String esScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "ElasticSearch\\";
	    prepareFolder(esScriptFilesFoder, "Local ElasticSearch Testing Script Files");
	    
		String dcTestElasticSearch_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestES_IndexingAndSearching_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestElasticSearch_RecFilePathAndName,  "Records of Testing ElasticSearch on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated ElasticSearch Internal Indexing and Searching Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestElasticSearch_RecFilePathAndName, testRecHeader, false);		
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
//		String maxENName = bdClusterEntryNodeList.get(clusterENNumber-1).toUpperCase();
//		if (clusterENNumber > 2){
//			clusterENNumber = clusterENNumber-1;
//			if (maxENName.equalsIgnoreCase("KX01")){
//				clusterENNumber = clusterENNumber-1;
//			} 			
//		}		
		
		if (clusterENNumber > 2){
			for (String tempNode : bdClusterEntryNodeList){
				if (tempNode.toUpperCase().contains("MN")
						|| tempNode.toUpperCase().contains("KX")
						){
					clusterENNumber = clusterENNumber-1;
				} 			
			}			
		}
		
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5
		
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();	
				
		//4. Loop through bdClusterEntryNodeList to delete and create an ElasticSearch index, create
		//     a ElasticSearch document(s) and refresh the ES Index and querying the index	
		//clusterENNumber_Start = 1; //0..1..2..3..4..5	    
	    if (bdClusterName.equalsIgnoreCase("BDDev1")
	    		|| bdClusterName.equalsIgnoreCase("BDDev3")
	    		|| bdClusterName.equalsIgnoreCase("BDTest3")
	    		|| bdClusterName.equalsIgnoreCase("BDProd3")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
	    	clusterENNumber = 1;
		}
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
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
			
			
			//(1) Get Elastic Search Host Web IP Address and Port String
			//BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			//ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			String enIpAddressStr = bdENCmdFactory.getServerURI();
			System.out.println(" *** enIpAddressStr: " + enIpAddressStr);
			
			//String esHostWebIpAddressAndPort = "http://" + enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01") + ":9200";
			String esHostWebIpAddressAndPort = "";
			if (bdClusterName.equalsIgnoreCase("BDDev1")
					|| bdClusterName.equalsIgnoreCase("Dev")
					|| bdClusterName.equalsIgnoreCase("MC_BDDev1")){
				esHostWebIpAddressAndPort = "https://" + enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01") + ":8202";
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDTest2")
					|| (bdClusterName.equalsIgnoreCase("BDTest2") || bdClusterName.equalsIgnoreCase("BDTest2"))){
				//esHostWebIpAddressAndPort = "https://" + enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01") + ":8202";
				esHostWebIpAddressAndPort = "https://" + enIpAddressStr + ":8202";
			}
			System.out.println(" *** esHostWebIpAddressAndPort: " + esHostWebIpAddressAndPort);
			
			if (bdClusterName.equalsIgnoreCase("BDDev3")){
				esHostWebIpAddressAndPort = "https://bigdata.mayo.edu/es/DEV3";
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")){
				esHostWebIpAddressAndPort = "https://bigdata.mayo.edu/es/TEST3";
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				esHostWebIpAddressAndPort = "https://bigdata.mayo.edu/es/PROD3";
			}
						
			//(2) Testing ElasticSearch Index Deleting, Creating, Loading and Searching				
			String userAuthenStr = " -v --user " + esClusterUserName + ":" + esClusterPassWord + " "; //BDDev1: es_admin:admin4dev
			
			String esTestIndexSearchingStatusFileName =  "esTestIndexSearchStatus_" + (i+1) + ".txt";			
			String enEsTestIndexSearchingStatusFilePathAndName = enServerScriptFileDirectory + esTestIndexSearchingStatusFileName;
			String hdfsESTestIndexSearchingStatusFileNameFilePathAndName = esTestFolderName + esTestIndexSearchingStatusFileName;
			
			
			String deleteEsTestIndexCmd = "curl" + userAuthenStr + "-XDELETE '" + esHostWebIpAddressAndPort + "/estest/'";
			String createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
					+ "    \"settings\" : {\n"
					+ "        \"index\" : {\n"
					+ "            \"number_of_shards\" : 3,\n"
					+ "            \"number_of_replicas\" : 1\n"
					+ "        }\n"
					+ "    }\n"
					+ "}'";
			if (clusterENNumber >= 2) {
				createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
						+ "    \"settings\" : {\n"
						+ "        \"index\" : {\n"
						+ "            \"number_of_shards\" : 3,\n"
						+ "            \"number_of_replicas\" : 2\n"
						+ "        }\n"
						+ "    }\n"
						+ "}'";
				
			}
			//String createEsTestIndexCmd = "curl -XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '\n"
					//+ "index :\n"
					//+ "    number_of_shards : 3\n"
					//+ "    number_of_replicas : 2 \n"
					//+ "'";
			
						
			String loadGreetingDocCmd1 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/1' -d '{ \"title\":\"Hello Sam\" }'";
			String loadGreetingDocCmd2 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/2' -d '{ \"title\":\"Hello Tom\" }'";
			
			String refreshEsTestIndexCmd = "curl" + userAuthenStr + "-XPOST '" + esHostWebIpAddressAndPort + "/estest/_refresh'";
			
			String searchEsTestIndexCmd = "curl" + userAuthenStr + "-XGET '" + esHostWebIpAddressAndPort + "/estest/greeting/_search?pretty=true,q=title:Hello' > " + enEsTestIndexSearchingStatusFilePathAndName+ " 2>&1";
			
			//kinit -kt /etc/security/keytabs/hdfs_test.keytab hdfs/hdpr03mn02.mayo.edu && {
			//	  curl -s --negotiate -u : "http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/user/?op=LISTSTATUS"
			//	  kdestroy
			//}
			
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
						    
		    sb.append("hadoop fs -mkdir -p " + esTestFolderName + "; \n");
		    sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + esTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + esTestFolderName + "; \n");		   
		    
		    sb.append(deleteEsTestIndexCmd + ";\n");	
		    sb.append(createEsTestIndexCmd + ";\n");	
		    sb.append(loadGreetingDocCmd1 + ";\n");	
		    sb.append(loadGreetingDocCmd2 + ";\n");	
		    sb.append(refreshEsTestIndexCmd + ";\n");	
		    sb.append(searchEsTestIndexCmd + ";\n");	
		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enEsTestIndexSearchingStatusFilePathAndName + " " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + esTestFolderName + "; \n");
		    //sb.append(deleteEsTestIndexCmd + ";\n");	
		    sb.append("rm -f " + enEsTestIndexSearchingStatusFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");		    	
			
		    
		    
		    String esIndexIndexingAndSearchingTestScriptFilePathAndName = esScriptFilesFoder + "dcTestES_IndexingAndSearchingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (esIndexIndexingAndSearchingTestScriptFilePathAndName,  "Script File For Testing ElasticSearch Indexing and Searching on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String esIndexIndexingAndSearchingCmds = sb.toString();
			writeDataToAFile(esIndexIndexingAndSearchingTestScriptFilePathAndName, esIndexIndexingAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(esIndexIndexingAndSearchingTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(esIndexIndexingAndSearchingTestScriptFilePathAndName, 
					esScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			Path filePath = new Path(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);
			boolean esStatusHdfsFileExistingStatus = false;
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);	
				esStatusHdfsFileExistingStatus = true;				
			}
			System.out.println("\n*** Exisiting status for " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + ": " + esStatusHdfsFileExistingStatus);
			
			//String targetFoundString1 = "\"_source\":{ \"title\":\"Hello Sam\" }";
			//String targetFoundString2 = "\"_source\":{ \"title\":\"Hello Tom\" }";
			String targetFoundString1 = "\"title\" : \"Hello Tom\""; //for ES v2.4.1
			String targetFoundString2 = "\"title\" : \"Hello Sam\""; //for ES v2.4.1
			boolean esSearchingSuccessStatus = false;
			boolean foundTargetStr1Status = false;
			boolean foundTargetStr2Status = false;
			if (esStatusHdfsFileExistingStatus){
				try {							
					FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsESTestIndexSearchingStatusFileNameFilePathAndName));				
					BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
					String line = "";				
					while ((line = br.readLine()) != null) {
						System.out.println("*** line: " + line );
						if (line.contains(targetFoundString1)) {												
							foundTargetStr1Status = true;					
						}
						if (line.contains(targetFoundString2)) {												
							foundTargetStr2Status = true;					
						}	
					}//end while
					br.close();	
					
					if (foundTargetStr1Status==true && foundTargetStr2Status==true){
						esSearchingSuccessStatus = true;
					}				
				} catch (IOException e) {				
					e.printStackTrace();				
				}//end try   
			}//end if			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
						
			String testRecordInfo = "";			
			if (esSearchingSuccessStatus == true){
				successTestScenarioNum++;				
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Internally ElasticSearch Index Deleting, Creating, Loading, Refreshing and Searching "
						+ "\n           on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  -- (2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'"
				        + "\n  -- (3) ElasticSearch Testing Total Time Used: " + timeUsed + "\n"; 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Internally ElasticSearch Index Deleting, Creating, Loading, Refreshing and Searching"
						+ "\n           on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  -- (2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'"
				        + "\n  -- (3) ElasticSearch Testing Total Time Used: " + timeUsed + "\n"; 
			}
			writeDataToAFile(dcTestElasticSearch_RecFilePathAndName, testRecordInfo, true);	
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
		currNotingMsg += "\n***** Done - Internally Testing ElasticSearch Index Deleting, Creating, Loading, Refreshing and"
					  +  "\n                   Searching on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present ElasticSearch Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS File(s)!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total ElasticSearch Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   ElasticSearch Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   ElasticSearch Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		//System.out.println(currNotingMsg);
		writeDataToAFile(dcTestElasticSearch_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestElasticSearch_RecFilePathAndName));
	
	}//end run()
	
	
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
