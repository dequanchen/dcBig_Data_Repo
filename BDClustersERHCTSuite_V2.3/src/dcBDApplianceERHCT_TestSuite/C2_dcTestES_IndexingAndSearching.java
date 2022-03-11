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
import dcModelClasses.HdfsUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/12/2014; 3/14/2016 
*/ 


public class C2_dcTestES_IndexingAndSearching {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";	
	private static String esTestedFilesFolder = "";
	
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String esClusterUserName = "";
	private static String esClusterPassWord = "";
	
	
	

	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for ElasticSearch-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		esTestedFilesFolder = args[4];
		
		if (bdClusterName.equalsIgnoreCase("BDDev")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
			esClusterUserName = "es_admin";
			esClusterPassWord = "admin4dev";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDInt")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
			esClusterUserName = "es_admin";
			esClusterPassWord = "admin4int";
			
		}
		if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
			//esClusterUserName = "esearch";
			//esClusterPassWord = "esearchpw";
			esClusterUserName = "es_admin";
			esClusterPassWord = "admin4int";
		}
				
		if (!esTestedFilesFolder.endsWith("/")){
			esTestedFilesFolder += "/";
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
		String dcTestElasticSearch_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestES_IndexingAndSearching_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestElasticSearch_RecFilePathAndName,  "Records of Testing ElasticSearch on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster ElasticSearch Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated ElasticSearch Internal Indexing and Searching Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestElasticSearch_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get Cluster name node and entry node information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		String hdfsNnIPAddressAndPort = currBdCluster.getBdHdfsNnIPAddressAndPort(); 
		System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
		
		//The following code is just for code-testing
//		int countEntryNode = 1;
//		for (String tempENName : bdClusterEntryNodeList){
//			System.out.println("\n--- (" + countEntryNode + ") " + tempENName.toUpperCase());	
//			
//			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
//			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
//			System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());	
//			countEntryNode++;
//		}	
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();
		String maxENName = bdClusterEntryNodeList.get(clusterENNumber-1).toUpperCase();
		if (clusterENNumber > 2){
			clusterENNumber = clusterENNumber-1;
			if (maxENName.equalsIgnoreCase("KX01")){
				clusterENNumber = clusterENNumber-1;
			} 			
		}
		//clusterENNumber = 1; //1..2..3..4..5
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
				
				
		//4. Loop through bdClusterEntryNodeList to delete and create an ElasticSearch index, create
		//     a ElasticSearch document(s) and refresh the ES Index and querying the index			
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing ElasticSearch-Storing Data Into HDFS File on Entry Node: " + tempENName);
			
			//(1) Get Elastic Search Host Web IP Address and Port String
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			String enIpAddressStr = bdENCommFactory.getServerURI();
			System.out.println(" *** enIpAddressStr: " + enIpAddressStr);
			
			String esHostWebIpAddressAndPort = "http://" + enIpAddressStr.replaceAll("[e,m]n0[2-9]", "en01") + ":9200";
			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
				esHostWebIpAddressAndPort = esHostWebIpAddressAndPort.replace("en01", "en02");
			}
			System.out.println(" *** esHostWebIpAddressAndPort: " + esHostWebIpAddressAndPort);
			
						
			//(2) Testing ElasticSearch Index Deleting, Creating, Loading and Searching		
			
			String userAuthenStr = " -v --user " + esClusterUserName + ":" + esClusterPassWord + " "; //BDDev: es_admin:admin4dev
			
			String esTestIndexSearchingStatusFileName =  "esTestIndexSearchStatus_" + (i+1) + ".txt";			
			String enEsTestIndexSearchingStatusFilePathAndName = enServerScriptFileDirectory + esTestIndexSearchingStatusFileName;
			String hdfsESTestIndexSearchingStatusFileNameFilePathAndName = esTestedFilesFolder + esTestIndexSearchingStatusFileName;
			hdfsFilePathAndNameList.add(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);
			
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
			
			sb.append("sudo su - hdfs;\n");
		    sb.append("hadoop fs -mkdir -p " + esTestedFilesFolder + "; \n");		   
		    sb.append("hadoop fs -chmod -R 750 " + esTestedFilesFolder + "; \n");		   
		    sb.append(deleteEsTestIndexCmd + ";\n");	
		    sb.append(createEsTestIndexCmd + ";\n");	
		    sb.append(loadGreetingDocCmd1 + ";\n");	
		    sb.append(loadGreetingDocCmd2 + ";\n");	
		    sb.append(refreshEsTestIndexCmd + ";\n");	
		    sb.append(searchEsTestIndexCmd + ";\n");	
		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enEsTestIndexSearchingStatusFilePathAndName + " " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + esTestedFilesFolder + "; \n");
		    sb.append(deleteEsTestIndexCmd + ";\n");	
		    sb.append("rm -f " + enEsTestIndexSearchingStatusFilePathAndName + "; \n");
		    		    	
			
		    String esScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "ElasticSearch\\";
		    prepareFolder(esScriptFilesFoder, "Local ElasticSearch Testing Script Files");
		    
		    String esIndexIndexingAndSearchingTestScriptFullFilePathAndName = esScriptFilesFoder + "dcTestES_IndexingAndSearchingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (esIndexIndexingAndSearchingTestScriptFullFilePathAndName,  "Script File For Testing ElasticSearch Indexing and Searching on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String esIndexIndexingAndSearchingCmds = sb.toString();
			writeDataToAFile(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, esIndexIndexingAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(esIndexIndexingAndSearchingTestScriptFullFilePathAndName));			
			HdfsUtil.runScriptFile_OnBDCluster(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, 
					esScriptFilesFoder, enServerScriptFileDirectory, bdENCommFactory);
					
				
			System.out.println("\n*** hdfsESTestIndexSearchingStatusFileNameFilePathAndName: " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName );
			
			String targetFoundString1 = "\"_source\":{ \"title\":\"Hello Sam\" }";
			String targetFoundString2 = "\"_source\":{ \"title\":\"Hello Tom\" }";
			boolean esSearchingSuccessStatus = false;
			boolean foundTargetStr1Status = false;
			boolean foundTargetStr2Status = false;
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
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			
			String testRecordInfo = "";			
			if (esSearchingSuccessStatus == true){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally ElasticSearch Index Deleting, Creating, Loading, Refreshing and\n         Searching on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally ElasticSearch Index Deleting, Creating, Loading, Refreshing and\n         Searching on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'\n";	 			    	    
			}
			writeDataToAFile(dcTestElasticSearch_RecFilePathAndName, testRecordInfo, true);				
		}//end for
		
				
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally ElasticSearch Index Deleting, Creating, Loading, Refreshing and\n         Searching on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
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
