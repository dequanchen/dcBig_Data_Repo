package dcHWSandBoxClusterERHCT_TestSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileSystem;

import dcModelClasses.DayClock;
import dcModelClasses.HdfsUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 06/29/2014 
*/ 


public class A2_Sdbx_dcTestMapReduce_WordCounting {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localMapReduceTestDataFileName = "";
	private static String mapReduceTestedFilesFolder = "";
	private static String localMapReduceJarFileName = "";
		
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	

	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for Storm-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localMapReduceTestDataFileName = args[4];
		mapReduceTestedFilesFolder = args[5];
		localMapReduceJarFileName = args[6];
		
						
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
		String dcTestStorm_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestStorm_UsingFlumeAndElasticSearch_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestStorm_RecFilePathAndName,  "Records of Testing Storm on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster Storm Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated Storm Internal Storing File Into HDFS Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get Cluster name node and entry node information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
		
//		//The following code is just for code-testing
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
		if (clusterENNumber > 2){
			clusterENNumber = clusterENNumber-1;
		}
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();	
		
		//4. Loop through bdClusterEntryNodeList to test MapReduce functions from each entry node
		clusterENNumber = 1;
		for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Storm-Storing Data Into HDFS File on Entry Node: " + tempENName);
			
			//(1) Move test data file, and the MapReduce Testing Jar file into Entry node /home/hdfs/ folder
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			
			String enServerFileDirectory = enServerScriptFileDirectory;
			int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localMapReduceTestDataFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
			
			int exitVal2 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localMapReduceJarFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
			
						
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving MapReduce Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving MapReduce Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving MapReduce Testing Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving MapReduce Testing Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
						
			
			//(2) Testing MapReduce function by the application to count the words in the HDFS file	with test data		
//			String enServerMapReduceTestDataFilePathAndName = enServerFileDirectory + localMapReduceTestDataFileName;
//			String enServerStormJarFilePathAndName = enServerFileDirectory + localMapReduceJarFileName;
//			
//					
//			String stormFlumeStoringHdfsFileStatusFileName =  "stormFlumeStoringHdfsFileStatus_" + (i+1) + ".txt";			
//			String enLocalStormFlumeStoringHdfsFileStatusFilePathAndName = enServerScriptFileDirectory + stormFlumeStoringHdfsFileStatusFileName;
//			//String stormFlumeStoringHdfsFileStatusRetrieveCmd = "grep Renaming nohup.out > " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName;
//			
//			String flumeNgInitiateStr = "/usr/lib/flume/bin/flume-ng agent ";						
//			String stormFlumeStoringDataIntoHDFSFileCmd =  flumeNgInitiateStr 					
//					+ "--conf-file " + enServerStorm_FlumeConfigFilePathAndName + " -name fileToHdfs -Dstorm.root.logger=DEBUG,console > " 
//					+ enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + " 2>&1 & sleep 10";
//			///home/hdfs/dcStormFlumeConf.txt
//			
//			String flumeHostName = bdENCommFactory.getServerURI();			
//			String stormInitiateStr = "/usr/lib/storm/bin/storm ";	
//			String stormTopologyStartCmd =  stormInitiateStr + " jar "
//							+  enServerStormJarFilePathAndName + " topology.TestFlumeTopology "
//							+ flumeHostName + " " + enServerMapReduceTestDataFilePathAndName + " & sleep 35";
//			String stormTopologyStopCmd =  stormInitiateStr + "kill testFlumeTopology"; 
//			
//			//storm  jar StormTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar topology.TestFlumeTopology 
//			//flumeHostName = "hdpr03en01.mayo.edu"; //inputDataFilePathAndName = "C:\\BD\\BD_UAT\\dcFSETestData.txt";
//			//storm kill testFlumeTopology
//			
//			String killStormFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
//						
//			String hdfsStormFlumeStoringHdfsFileStatusFilePathAndName = stormTestedFilesFolder + stormFlumeStoringHdfsFileStatusFileName;
//			hdfsFilePathAndNameList.add(hdfsStormFlumeStoringHdfsFileStatusFilePathAndName);
//			
//			sb.append("chown hdfs:users -R /home/hdfs;\n");				
//			sb.append("sudo su - hdfs;\n");
//			//sb.append("rm -f -R /home/hdfs/flume/drain/checkpoint;\n");
//			//sb.append("rm -f -R /home/hdfs/flume/drain/data;\n");			;
//			sb.append("rm -f -R /home/hdfs/flume/;\n");
//			sb.append("mkdir -p /home/hdfs/flume/drain/checkpoint;\n");
//			sb.append("mkdir -p /home/hdfs/flume/drain/data;\n");
//			sb.append(stormFlumeStoringDataIntoHDFSFileCmd + ";\n");
//			sb.append("hadoop fs -rm -r -skipTrash " + stormTestedFilesFolder + "dcStormFlumeTestData*tmp; \n");			
//		    sb.append("hadoop fs -mkdir -p " + stormTestedFilesFolder + "; \n");		   
//		    sb.append("hadoop fs -chmod -R 750 " + stormTestedFilesFolder + "; \n");		    
//		    sb.append(stormTopologyStartCmd + ";\n");		    
//		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
//		    sb.append("hadoop fs -copyFromLocal " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + " " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
//		   	sb.append("hadoop fs -chmod -R 550 " + stormTestedFilesFolder + "; \n");	   	
//		    sb.append("rm -f " + enLocalStormFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
//		    sb.append("rm -f " + enServerMapReduceTestDataFilePathAndName + "; \n");		    
//		    sb.append("rm -f " + enServerStormJarFilePathAndName + "; \n");	
//		    sb.append("rm -f " + enServerStorm_FlumeConfigFilePathAndName + "; \n");
//		    sb.append(stormTopologyStopCmd + ";\n");
//		    sb.append(killStormFlumeNgCmd + ";\n");
//		    sb.append(stormTopologyStopCmd + ";\n");
//		    
//			
//		    String stormScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Storm\\";
//		    prepareFolder(stormScriptFilesFoder, "Local Storm Testing Script Files");
//		    
//		    String stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName = stormScriptFilesFoder + "dcTestStorm_FlumeStoringDataIntoHdfsFileScriptFile_No"+ (i+1) + ".sh";			
//			prepareFile (stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName,  "Script File For Testing Storm Flume-Storing Daat Into HDFS File on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
//			
//			String stormStoringHdfsFileCmds = sb.toString();
//			writeDataToAFile(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName, stormStoringHdfsFileCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName));			
//			HdfsUtil.runScriptFile_OnBDCluster(stormStoringDataIntoHdfsFileTestScriptFullFilePathAndName, 
//					stormScriptFilesFoder, enServerScriptFileDirectory, bdENCommFactory);
//					
//				
//			System.out.println("\n*** hdfsStormFlumeStoringHdfsFileStatusFilePathAndName: " + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName );
//			boolean stormStoringSuccess = false;
//			String stormStoreHdfsFilePathAndName = "";
//			try {							
//				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsStormFlumeStoringHdfsFileStatusFilePathAndName));				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				String line = "";				
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					if (line.contains("Creating /data/test/Storm/dcStormFlumeTestData.") && line.contains(".tmp")) {
//						String[] lineSplit = line.split(" Creating ");
//						stormStoreHdfsFilePathAndName = lineSplit[1].trim();
//						hdfsFilePathAndNameList.add(stormStoreHdfsFilePathAndName);
//						System.out.println("*** add: " + stormStoreHdfsFilePathAndName );
//						
//						stormStoringSuccess = true;
//						break;
//					}								
//				}//end while
//				br.close();				
//			} catch (IOException e) {				
//				e.printStackTrace();				
//			}//end try   
//			
//			tempClock = new DayClock();				
//			tempTime = tempClock.getCurrentDateTime();	
//			
//			String testRecordInfo = "";			
//			if (stormStoringSuccess == true){
//				successTestScenarioNum++;
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Internally Storm-Using-Flume To Store Data of a Test-Data File On Entry Node into a HDFS File on '" + bdClusterName + "' Cluster From Entry Node - '" 
//						+ tempENName + "' at the time - " + tempTime
//				        + "\n  --(2) Present Storm-Using-Flume Testing-Generated Storing-Status HDFS File:  '" + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "'"
//				        + "\n  --(3) Present Storm-Using-Flume Testing-Generated Stored-Data HDFS File:  '" + stormStoreHdfsFilePathAndName + "'\n";	 
//			} else {
//				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Internally Storm-Using-Flume To Store Data of a Test-Data File On Entry Node into a HDFS File on '" + bdClusterName + "' Cluster From Entry Node - '" 
//						+ tempENName + "' at the time - " + tempTime
//						+ "\n  --(2) Present Storm-Using-Flume Testing-Generated Storing-Status HDFS File:  '" + hdfsStormFlumeStoringHdfsFileStatusFilePathAndName + "'"
//				        + "\n  --(3) Present Storm-Using-Flume Testing-Generated Stored-Data HDFS File:  'None'\n";	 
//			}
//			writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecordInfo, true);	
//			if (i < clusterENNumber-1){
//				Thread.sleep(35000);
//			}
		}//end for
		
		
		
		


		
//				
//		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
//		NumberFormat df = new DecimalFormat("#0.00"); 
//		String currUATPassedRate = df.format(testSuccessRate);
//		
//	    //Notice message on the console
//		DayClock endClock = new DayClock();				
//		String endTime = endClock.getCurrentDateTime();			
//		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
//		
//		String currNotingMsg = "\n\n===========================================================";
//		currNotingMsg += "\n***** Done - Testing Internally Storm Using Flume and ElasticSearch on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
//		currNotingMsg += "\n***** Present Storm Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS File(s)!";
//		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
//		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
//		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
//		currNotingMsg += "\n   Total Storm Test Scenario Number: " + totalTestScenarioNumber;
//		currNotingMsg += "\n   Storm Test Succeeded Scenario Number: " + successTestScenarioNum;
//		currNotingMsg += "\n   Storm Test Scenario Success Rate (%): " + currUATPassedRate;
//		currNotingMsg += "\n===========================================================";	    
//		
//		//System.out.println(currNotingMsg);
//		writeDataToAFile(dcTestStorm_RecFilePathAndName, currNotingMsg, true);		
//		Desktop.getDesktop().open(new File(dcTestStorm_RecFilePathAndName));
//	
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

