package dcHWSandBoxClusterERHCT_TestSuite;



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
* Date: 12/11/2014; 2/24-25/2016; 3/14/2016 
*/ 


public class C1_Sdbx_dcTestFlume_StoringData {
	private static int testingTimesSeqNo = 3;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localFlumeTestDataFileName = "";
	private static String flumeTestedFilesFolder = "";
	private static String localFlumeConfigFileName = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/flume/bin/flume-ng  ==> /usr/bin/flume-ng or /usr/hdp/2.3.2.0-2950/flume/bin/flume-ng (HDP/TDH2.3.2) or /usr/hdp/2.3.4.0-3485/flume/bin/flume-ng (HDP/TDH2.3.4)
	// export JAVA_HOME=/opt/teradata/jvm64/jdk7(<=TDH) ==> export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.91.x86_64 (HDP Sdbx)
	//Not Needed: & sleep 60 (TDH) ==> & sleep 60 or 120 (HDP Sdbx)(Line#163)
	
	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for Flume-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localFlumeTestDataFileName = args[4];
		flumeTestedFilesFolder = args[5];
		localFlumeConfigFileName = args[6];
		
		if (!flumeTestedFilesFolder.endsWith("/")){
			flumeTestedFilesFolder += "/";
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
		String dcTestFlume_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestFlume_StoringFileIntoHdfs_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestFlume_RecFilePathAndName,  "Records of Testing Flume on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster Flume Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated Flume Internal Storing File Into HDFS Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestFlume_RecFilePathAndName, testRecHeader, false);		
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
//		if (clusterENNumber > 2){
//			clusterENNumber = clusterENNumber-1;
//		}
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();		
		
		//4. Loop through bdClusterEntryNodeList to start a flume agent  that reads data from a test-data file 
		//     and store data into a HDFS file
		//clusterENNumber = 1;
		for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Flume-Storing Data Into HDFS File\n         on Entry Node: " + tempENName);
			
			//(1) Move test data file and Flume configuration file into Entry node /home/hdfs/ folder
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			
			String enServerFileDirectory = enServerScriptFileDirectory;
			int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localFlumeTestDataFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
			
			int exitVal2 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localFlumeConfigFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCommFactory);
						
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Flume Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Flume Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving Flume Configuration File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Flume Configuration File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			
			//(2) Testing Flume-storing data in a test-data file into HDFS files			
			//String enServerNohupFilePathAndName = enServerFileDirectory + "nohup.out";
			String enServerFlumeTestDataFilePathAndName = enServerFileDirectory + localFlumeTestDataFileName;
			String enServerFlumeConfigFilePathAndName = enServerFileDirectory + localFlumeConfigFileName;
			
			String flumeStoringHdfsFileStatusFileName =  "flumeStoringHdfsFileStatus_" + (i+1) + ".txt";			
			String enLocalFlumeStoringHdfsFileStatusFilePathAndName = enServerScriptFileDirectory + flumeStoringHdfsFileStatusFileName;
			//String flumeStoringHdfsFileStatusRetrieveCmd = "grep Renaming nohup.out > " + enLocalFlumeStoringHdfsFileStatusFilePathAndName;
			
			String FlumeNgInitiateStr = "/usr/bin/flume-ng agent ";						
			String FlumeStoringDataIntoHDFSFileCmd =  FlumeNgInitiateStr 					
					+ "--conf-file " + enServerFlumeConfigFilePathAndName + " -name fileToHdfs -Dflume.root.logger=DEBUG,console > " + enLocalFlumeStoringHdfsFileStatusFilePathAndName + " 2>&1 & sleep 60";
						
			String killFlumeNgCmd = "ps -ef | grep flume | grep logger=DEBUG | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
						
			String hdfsFlumeStoringHdfsFileStatusFilePathAndName = flumeTestedFilesFolder + flumeStoringHdfsFileStatusFileName;
			hdfsFilePathAndNameList.add(hdfsFlumeStoringHdfsFileStatusFilePathAndName);
			
			
			sb.append("sudo su - hdfs;\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("chown -R hdfs:bduser " + enServerScriptFileDirectory + "; \n");
		    sb.append("hadoop fs -mkdir -p " + flumeTestedFilesFolder + "; \n");		   
		    sb.append("hadoop fs -chmod -R 750 " + flumeTestedFilesFolder + "; \n");
		    //sb.append("rm -f " + enServerNohupFilePathAndName + "; \n");
		    //sb.append("export JAVA_HOME=/opt/teradata/jvm64/jdk7;\n"); //TDH
		    sb.append("export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.91.x86_64;\n"); //HDP SandBox
		    sb.append("export PATH=$PATH:$STORM_HOME/bin:$JAVA_HOME/bin; \n");
		    
		    sb.append(FlumeStoringDataIntoHDFSFileCmd + ";\n");		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalFlumeStoringHdfsFileStatusFilePathAndName + " " + hdfsFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + flumeTestedFilesFolder + "; \n");
		    //sb.append("rm -f " + enLocalFlumeStoringHdfsFileStatusFilePathAndName + "; \n");
		    sb.append(killFlumeNgCmd + ";\n");
		    //sb.append("rm -f " + enServerNohupFilePathAndName + "; \n");
		    sb.append("rm -f " + enServerFlumeTestDataFilePathAndName + "; \n");
		    sb.append("rm -f " + enServerFlumeConfigFilePathAndName + "; \n");
		    	
			
		    String flumeScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Flume\\";
		    prepareFolder(flumeScriptFilesFoder, "Local Flume Testing Script Files");
		    
		    String flumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName = flumeScriptFilesFoder + "dcTestFlume_StoringDataIntoHdfsFileScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (flumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName,  "Script File For Testing Flume Storing Daat Into HDFS File\n         on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String flumeStoringHdfsFileCmds = sb.toString();
			writeDataToAFile(flumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName, flumeStoringHdfsFileCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(flumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName));			
			HdfsUtil.runScriptFile_OnBDCluster(flumeStoringDataIntoHdfsFileTestScriptFullFilePathAndName, 
					flumeScriptFilesFoder, enServerScriptFileDirectory, bdENCommFactory);
					
				
			System.out.println("\n*** hdfsFlumeStoringHdfsFileStatusFilePathAndName: " + hdfsFlumeStoringHdfsFileStatusFilePathAndName );
			boolean flumeStoringSuccess = false;
			String flumeStoreHdfsFilePathAndName = "";
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsFlumeStoringHdfsFileStatusFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains("Renaming ") && line.contains(".tmp to /data/test/Flume/dcFlumeTestData.")) {
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
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			String testRecordInfo = "";			
			if (flumeStoringSuccess == true){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storing Data of a Test-Data File On Entry Node into a HDFS File\n         on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Flume Testing-Generated HDFS File:  '" + flumeStoreHdfsFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Storing Data of a Test-Data File On Entry Node into a HDFS File\n         on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Flume Testing-Generated HDFS File:  '" + flumeStoreHdfsFilePathAndName + "'\n";				    	    
			}
			writeDataToAFile(dcTestFlume_RecFilePathAndName, testRecordInfo, true);				
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
