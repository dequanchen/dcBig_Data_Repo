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
* Date: 1/8/2014 
*/ 


public class C4_dcTestStorm_UsingHBase {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localStormTestDataFileName = "";
	private static String stormTestedFilesFolder = "";
	private static String hbaseTableName = "";
	private static String localStormJarFileName = "";
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
		localStormTestDataFileName = args[4];
		stormTestedFilesFolder = args[5];
		hbaseTableName = args[6];
		localStormJarFileName = args[7]; 
		 		
		
		if (!stormTestedFilesFolder.endsWith("/")){
			stormTestedFilesFolder += "/";
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
		String dcTestStorm_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestStorm_UsingFlumeAndElasticSearch_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestStorm_RecFilePathAndName,  "Records of Testing Storm on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster Storm Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated Storm Internal Persisting Data Into HBase Representative Scenario Testing "
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
		
				
		//5. Loop through bdClusterEntryNodeList to disable, drop and create an HBase table, 
		//     insert rows of data into the HBase table, followed by querying the HBase table		
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//int clusterENNumber = bdClusterEntryNodeList.size();
		for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Storm-Storing Data Into HBase File on Entry Node: " + tempENName);
			
			//(1) Get Elastic Search Host Web IP Address and Port String
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			String enIpAddressStr = bdENCommFactory.getServerURI();
			System.out.println(" *** enIpAddressStr: " + enIpAddressStr);
						
								
						
			//(2) Move test data file, and Storm jar  file into Entry node /home/hdfs/ folder
			String enServerFileDirectory = enServerScriptFileDirectory;
			int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localStormTestDataFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
			
			int exitVal2 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localStormJarFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Storm Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Storm Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
								
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving StormTest Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving StormTest Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
						
			//(3) Testing HBase Table Disabling,  Dropping, Creating, Loading and Searching		
			String enServerStormTestDataFilePathAndName = enServerFileDirectory + localStormTestDataFileName;			
			String enServerStormJarFilePathAndName = enServerFileDirectory + localStormJarFileName;
			
			String hbaseShellInitiateStr = " | hbase shell";
			String singleColmnFamilyName = "cfa";
			String disableHBaseTableCmd = "echo \"disable '"+ hbaseTableName + "'\"" + hbaseShellInitiateStr;
			String dropHBaseTableCmd = "echo \"drop '"+ hbaseTableName + "'\"" + hbaseShellInitiateStr;		
			String createHBaseTableCmd = "echo \"create '"+ hbaseTableName + "',{NAME => '" + singleColmnFamilyName + "', VERSIONS => 5}\"" + hbaseShellInitiateStr;
			
			String stormInitiateStr = "/usr/lib/storm/bin/storm ";	
//			String stormTopologyStartCmd =  stormInitiateStr + " jar "
//							+  enServerStormJarFilePathAndName + " topology.TestHBaseTopology "
//							+ bdClusterName + " " + hbaseTableName + " " + enServerStormTestDataFilePathAndName + " & sleep 30";
			String stormTopologyStartCmd =  "java -cp "
					+  enServerStormJarFilePathAndName + " topology.TestHBaseTopology "
					+ bdClusterName + " " + hbaseTableName + " " + enServerStormTestDataFilePathAndName + " & sleep 30";
			String stormTopologyStopCmd =  stormInitiateStr + "kill testHBaseTopology"; 
			
			String enLocalTableRowCountFilePathAndName = enServerFileDirectory + "tempTableRowCount.txt";
			String hdfsTableRowCountFilePathAndName = stormTestedFilesFolder + "hbaseTable_" + hbaseTableName + "_RowCount.txt";
			String hbaseTableRowCountQueryCmd = "echo \"count '" + hbaseTableName + "'\" | hbase shell | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
			
			sb.append("chown hdfs:users -R /home/hdfs;\n");
			sb.append("sudo su - hdfs;\n");
		    sb.append("hadoop fs -mkdir -p " + stormTestedFilesFolder + "; \n");		   
		    sb.append("hadoop fs -chmod -R 750 " + stormTestedFilesFolder + "; \n");
		    sb.append(disableHBaseTableCmd + ";\n");	
		    sb.append(dropHBaseTableCmd + ";\n");	    
		    sb.append(createHBaseTableCmd + ";\n");		   
		    sb.append(stormTopologyStartCmd + ";\n");
			sb.append(stormTopologyStopCmd + ";\n");			
			
			sb.append(hbaseTableRowCountQueryCmd + ";\n");			    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");
		   		    		   
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");		     
		    sb.append("hadoop fs -chmod -R 550 " + stormTestedFilesFolder + "; \n");
		    
		    sb.append(stormTopologyStopCmd + ";\n");
		    
			
		    String esScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Storm\\";
		    prepareFolder(esScriptFilesFoder, "Local ElasticSearch Testing Script Files");
		    
		    String esIndexIndexingAndSearchingTestScriptFullFilePathAndName = esScriptFilesFoder + "dcTestStorm_HBasePersistingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (esIndexIndexingAndSearchingTestScriptFullFilePathAndName,  "Script File For Testing Storm-HBase Persisting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String esIndexIndexingAndSearchingCmds = sb.toString();
			writeDataToAFile(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, esIndexIndexingAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(esIndexIndexingAndSearchingTestScriptFullFilePathAndName));			
			HdfsUtil.runScriptFile_OnBDCluster(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, 
					esScriptFilesFoder, enServerScriptFileDirectory, bdENCommFactory);
					
				
			System.out.println("\n*** hdfsTableRowCountFilePathAndName: " + hdfsTableRowCountFilePathAndName );
			
			String targetFoundString = " row(s)";			
			int hbaseTableRowCount = 0;			
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains(targetFoundString)) {												
						hbaseTableRowCount = Integer.valueOf(line.replace(targetFoundString, ""));
						System.out.println("*** hbaseTableRowCount: " + hbaseTableRowCount );
						break;
					}					
				}//end while
				br.close();				
							
			} catch (IOException e) {				
				e.printStackTrace();				
			}//end try   
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			
			String testRecordInfo = "";			
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storm-Using-HBase for Index Disabling/Dropping, Creating, Loading, and Querying on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Storm-Hbase Testing-Generated HDFS File:  '" + hdfsTableRowCountFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storm-Using-HBase for Index Disabling/Dropping, Creating, Loading, and Querying on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Storm-Hbase Testing-Generated HDFS File:  '" + hdfsTableRowCountFilePathAndName + "'\n";
			}
			writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecordInfo, true);
			if (i < clusterENNumber-1){
				Thread.sleep(35000);
			}
			
		}//end for
		
				
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally Storm Using HBase on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
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
