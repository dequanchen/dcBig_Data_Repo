package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

import dcModelClasses.DayClock;
import dcModelClasses.HdfsUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-10, 22-24/2014 
*/ 


public class A1_dcTestHDFS_WritingAndReading {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localHDFSTestDataFileName = "";
	private static String localHDFSTestAppendingDataFileName = "";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String existingHdfsFilePathAndName = "";
	private static String winLocalAppendingDataFile = "";
	private static String enServerScriptFileDirectory = "/home/hdfs";
	

	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for HDFS-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localHDFSTestDataFileName = args[4];
		localHDFSTestAppendingDataFileName = args[5];
		
			
		run();
	}//end main
	
	@SuppressWarnings("deprecation")
	public static void run() throws Exception {
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare folder or files for testing records
		String scriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "HDFS\\";
	    prepareFolder(scriptFilesFoder, "Local Sqoop Testing Script Files");
	    
		String dcTestHDFS_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHDFS_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestHDFS_RecFilePathAndName,  "Records of Testing HDFS on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster HDFS Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated HDFS Internal (Writing By Copying) and External (Writing, Reading & Appending) Access Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecHeader, false);		
		
		//String dcTestHDFS_SucceededRecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHDFS_WritingAndReading_SucceededRecords_No" + testingTimesSeqNo + ".txt";
		//prepareFile (dcTestHDFS_SucceededRecFilePathAndName,  "Succeeded Records of Testing HDFS on '" + bdClusterName + "' Cluster");
		
		//String dcTestHDFS_'Failed'RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHDFS_WritingAndReading_'Failed'Records_No" + testingTimesSeqNo + ".txt";
		//prepareFile (dcTestHDFS_'Failed'RecFilePathAndName,  "'Failed' Records of Testing HDFS on '" + bdClusterName + "' Cluster");

		//3. Get Cluster name node and entry node information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();		
		//String hdfsNnIPAddressAndPort = currBdCluster.getBdHdfsNnIPAddressAndPort();
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
		
		//4. Loop through bdClusterEntryNodeList to copy and internally writing a HDFS test data file from local windows to local edge node and then to HDFS system
		//clusterENNumber = 1;
		for (int i = 0; i < clusterENNumber; i++){ //clusterENNumber..bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();
			System.out.println("\n--- (" + (i+1) + ") Testing File-Copying and Writing to HDFS on Entry Node: " + tempENName);	
			
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			
			//1.Preparing testing folder on current Linux machine
			sb.append("mkdir -p " + enServerScriptFileDirectory + ";\n");
			sb.append("chown hdfs:hadoop " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 750 " + enServerScriptFileDirectory + "; \n");			
		    sb.append("exit; \n");
		    		   
			String localHdfsHomeFolderPrepFilePathAndName = scriptFilesFoder + "dcHDFSHomeFolderPrepScript"+ (i+1) + ".sh";			
			prepareFile (localHdfsHomeFolderPrepFilePathAndName,  "Script File For HDFS Home Folder Preparation on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String localHdfsHomeFolderPrepCmds = sb.toString();
			writeDataToAFile(localHdfsHomeFolderPrepFilePathAndName, localHdfsHomeFolderPrepCmds, false);		
			sb.setLength(0);
		
			HdfsUtil.runScriptFile_OnBDCluster(localHdfsHomeFolderPrepFilePathAndName, 
					bdClusterUATestResultsFolder, enServerScriptFileDirectory, bdENCommFactory);
			
			//2. Testing internally writing to HDFS
			//String enServerScriptFileDirectory = "/home/hdfs/"; ///data/tmp/../home/hdfs/
			String hdfsFilePathAndName = "/data/test/HDFS/dcUatDataFile_No"+ (i+1) + ".txt";
			hdfsFilePathAndNameList.add(hdfsFilePathAndName);
			
			int testWritingTestDataFile_FromWinLocalToHDFS_exitVal =
					HdfsUtil.copyFile_FromWindowsLocal_ToHDFS_OnBDCluster (localHDFSTestDataFileName, bdClusterUATestResultsParentFolder,
					enServerScriptFileDirectory, hdfsFilePathAndName, bdENCommFactory);
			
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			String testRecordInfo = "";	
			if (testWritingTestDataFile_FromWinLocalToHDFS_exitVal == 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Writing to HDFS File \n         on BigData '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Generated File on HDFS System:  '" + hdfsFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Internally Writing to HDFS File \n         on BigData '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  -- (2) Generated File on HDFS System:  '" + hdfsFilePathAndName + "'\n";	 				    	    
			}
			writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecordInfo, true);				
		}//end for
		
		//5. Testing HDFS external writing and reading
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		String currHdfsFilePathAndName = "/data/test/HDFS/dcUatDataFile_No"+ (clusterENNumber+1) + ".txt";
		//5.(1) External writing
		boolean writingSuccess;
		try {
			totalTestScenarioNumber++;
			
			Path outputPath = new Path(currHdfsFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing HDFS file: " + currHdfsFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(currHdfsFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
			
			String srcFilePathAndName1 = bdClusterUATestResultsParentFolder + localHDFSTestDataFileName;
			System.out.println("\n*** srcFilePathAndName1: " + srcFilePathAndName1);
			FileReader aFileReader1 = new FileReader(srcFilePathAndName1);
			BufferedReader br1 = new BufferedReader(aFileReader1);
			
			String srcFilePathAndName2 = bdClusterUATestResultsParentFolder + localHDFSTestAppendingDataFileName;
			System.out.println("\n*** srcFilePathAndName2: " + srcFilePathAndName2);
			FileReader aFileReader2 = new FileReader(srcFilePathAndName2);
			BufferedReader br2 = new BufferedReader(aFileReader2);
			
			String line = "";		
			while ((line = br1.readLine()) != null) {
				bw.write(line);	
			}
			br1.close();
			aFileReader1.close();
			bw.append("\n\n");
			
			while ((line = br2.readLine()) != null) {
				bw.append(line);	
			}
			br2.close();
			aFileReader2.close();
							
			bw.close();
			fsDataOutStream.close();
			
			hdfsFilePathAndNameList.add(currHdfsFilePathAndName);
				
	        //OutputStream os = currHadoopFS.create(outputPath, true);
	        //Configuration conf = currHadoopFS.getConf(); 
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localHDFSTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);
			writingSuccess = true;
		} catch (IOException e) {				
			e.printStackTrace();
			writingSuccess = false;
		}//end try   
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
		
		String testRecordInfo = "";	
		if (writingSuccess == true){
			successTestScenarioNum++;
			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
					+ "\n  -- (1) Externally Writing to HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
					+ "\n  -- (2) Generated File on HDFS System:  '" + currHdfsFilePathAndName + "'\n";												     
		} else {
			testRecordInfo = "-*-*- 'Failed'  -  # (" + totalTestScenarioNumber + ") Test Scenario:"
					+ "\n  -- (1) Externally Writing to HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
					+ "\n  -- (2) Generated File on HDFS System:  '" + currHdfsFilePathAndName + "'\n";			    	    
		}
		writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecordInfo, true);		
		
		//5.(2) External reading
		boolean readingSuccess;
		//FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//String currHdfsFilePathAndName = "/ocean/cnote/cnote/2009/4/14/hl7_.1415659795883";
		try {
			totalTestScenarioNumber++;			
			FileStatus[] status = currHadoopFS.listStatus(new Path(currHdfsFilePathAndName));				
			BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			String line = "";
			boolean foundAppendedStr = false;
			while ((line = br.readLine()) != null) {
				//System.out.println("*** line: " + line );
				if (line.contains("Total Message Number in Current HDFS File Is: 11")) {
					foundAppendedStr = true;
					break;
				}								
			}//end while
			br.close();
			if (foundAppendedStr==true){
				readingSuccess = true;
			} else {
				readingSuccess = false;
			}			
		} catch (IOException e) {				
			e.printStackTrace();
			readingSuccess = false;
		}//end try   
        
		tempClock = new DayClock();				
		tempTime = tempClock.getCurrentDateTime();
		
		testRecordInfo = "";	
		if (readingSuccess == true){
			successTestScenarioNum++;
			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
					+ "\n  -- (1) Externally Reading HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime	
					+ "\n  -- (2) HDFS File That Has Been Read:  '" + currHdfsFilePathAndName + "'\n";	
		} else {
			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
					+ "\n  -- (1) Externally Reading HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime	
					+ "\n  -- (2) HDFS File That Has Been Read:  '" + currHdfsFilePathAndName + "'\n";			    	    
		}
		writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecordInfo, true);	
		
        
		
		//5.(3) External Appending Writing
		existingHdfsFilePathAndName = "/data/test/HDFS/dcUatDataFile_No"+ (clusterENNumber+1) + ".txt";
		
		boolean appendingSuccess = false;
		try {
			totalTestScenarioNumber++;
			
			Path outputPath = new Path(existingHdfsFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing HDFS file: " + existingHdfsFilePathAndName);
	        }
			
			//a. Generating an existing HDFS file with data externally written to it
			FSDataOutputStream fsDataOutStream1 = currHadoopFS.create(new Path(existingHdfsFilePathAndName), true);			
			//PrintWriter bw1 = new PrintWriter(fsDataoutStream1);	
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fsDataOutStream1));
			
			String srcFilePathAndName1 = bdClusterUATestResultsParentFolder + localHDFSTestDataFileName;
			System.out.println("\n*** srcFilePathAndName1: " + srcFilePathAndName1);
			FileReader aFileReader1 = new FileReader(srcFilePathAndName1);
			BufferedReader br1 = new BufferedReader(aFileReader1);
			
			String line = "";		
			while ((line = br1.readLine()) != null) {
				bw1.write(line);	
			}
			br1.close();
			aFileReader1.close();
			bw1.append("\n\n");
			bw1.close();
			fsDataOutStream1.close();
			System.out.println("\n*-*-* Done - Generating an existing HDFS file - " + existingHdfsFilePathAndName);
			hdfsFilePathAndNameList.add(existingHdfsFilePathAndName);
			
			
			//b. Append data to the above existing HDFS file
			winLocalAppendingDataFile = bdClusterUATestResultsParentFolder + localHDFSTestAppendingDataFileName;
			System.out.println("\n*** winLocalAppendingDataFile: " + winLocalAppendingDataFile);

			//String [] hdfsAppendingTestParameterArray = new String [3];
			//hdfsAppendingTestParameterArray[0] = bdClusterName;
			//hdfsAppendingTestParameterArray[1] = existingHdfsFilePathAndName;
			//hdfsAppendingTestParameterArray[2] = winLocalAppendingDataFile;			
			//A1a1_dcSingleTestHDFS_Appending1.main(hdfsAppendingTestParameterArray);
			//Thread.sleep(1*25*1000);
			
			//--new A1a2_dcSingleTestHDFS_Appending (bdClusterName, existingHdfsFilePathAndName, appendingDataFile).start();			
			//--Thread.sleep(1*20*1000);
			Thread thread = new Thread(new Runnable() {
			    public void run() {
			    	try {
						BdCluster currBdCluster = new BdCluster(bdClusterName);
						FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
						Path existingOutPath = new Path(existingHdfsFilePathAndName);	
						
						FSDataOutputStream fsDataOutStream = currHadoopFS.append(existingOutPath);	
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
					
						FileReader aFileReader = new FileReader(winLocalAppendingDataFile);
						BufferedReader br = new BufferedReader(aFileReader);
						
						String line = "";
						while ((line = br.readLine()) != null) {
							bw.write(line + "\n");				
						}
						br.close();
						aFileReader.close();							
						bw.close();
						fsDataOutStream.close();
						
						Thread.currentThread().interrupt();
						return;
						
					} catch (Exception e) {	
						System.out.println("---*---Caught Exception: " + e.toString());
						e.printStackTrace();		
					} //end try
					
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
			
			//c. Read and find the appended data from the appended file
			FileStatus[] status = currHadoopFS.listStatus(new Path(existingHdfsFilePathAndName));				
			BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			boolean foundAppendedStr = false;
			while ((line = br.readLine()) != null) {
				//System.out.println("*** line: " + line );
				if (line.contains("Total Message Number in Current HDFS File Is: 11")) {
					foundAppendedStr = true;
					break;
				}								
			}//end while
			br.close();
			
			if (foundAppendedStr==true){
				appendingSuccess = true;
			} 			
						
		
			//d. final recording 
			DayClock tempClock_a = new DayClock();				
			tempTime = tempClock_a.getCurrentDateTime();
			
			testRecordInfo = "";	
			if (appendingSuccess == true){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Externally Appending-Writing to an Existing HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime	
						+ "\n  -- (2) HDFS File That Has Been Tried to Append:  '" + existingHdfsFilePathAndName + "'\n";										     
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Externally Appending-Writing to an Existing HDFS File \n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime	
						+ "\n  -- (2) HDFS File That Has Been Tried to Append:  '" + existingHdfsFilePathAndName + "'\n";				    	    
			}
			writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecordInfo, true);				
		} catch (Exception e) {
			appendingSuccess = false;
			return;
			//e.printStackTrace();		
		} //end try
		
		
//		existingHdfsFilePathAndName = "/data/test/HDFS/dcUatDataFile_No"+ (clusterENNumber+1+1) + ".txt";
//		boolean appendingSuccess;
//		try {
//			totalTestScenarioNumber++;
//			
//			Path outputPath = new Path(existingHdfsFilePathAndName);		
//			if (currHadoopFS.exists(outputPath)) {
//				currHadoopFS.delete(outputPath, true);
//				System.out.println("\n*** deleting existing HDFS file: " + existingHdfsFilePathAndName);
//	        }
//			
//			//a. Generating an existing HDFS file with data externally written to it
//			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(existingHdfsFilePathAndName), true);			
//			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
//			
//			String srcFilePathAndName1 = bdClusterUATestResultsParentFolder + localHDFSTestDataFileName;
//			System.out.println("\n*** srcFilePathAndName1: " + srcFilePathAndName1);
//			FileReader aFileReader1 = new FileReader(srcFilePathAndName1);
//			BufferedReader br1 = new BufferedReader(aFileReader1);
//			
//			String line = "";		
//			while ((line = br1.readLine()) != null) {
//				bw.write(line);	
//			}
//			br1.close();
//			aFileReader1.close();
//			bw.append("\n\n");
//			bw.close();
//			fsDataOutStream.close();
//			
//			//b. Append data to the above existing HDFS file
//			String appendingDataFile = bdClusterUATestResultsParentFolder + localHDFSTestAppendingDataFileName;
//			int appendingStatusCode = appendFileDataToExistingHDFSFile (existingHdfsFilePathAndName, appendingDataFile, currHadoopFS);
//			
//			hdfsFilePathAndNameList.add(existingHdfsFilePathAndName);
//				
//	        //OutputStream os = currHadoopFS.create(outputPath, true);
//	        //Configuration conf = currHadoopFS.getConf(); 
//	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localHDFSTestDataFileName;
//	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
//	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
//	        //IOUtils.copyBytes(is, os, conf);
//			if (appendingStatusCode == 0) {
//				appendingSuccess = true;
//			} else {
//				appendingSuccess = false;
//			}			
//		} catch (Exception e) {
//			appendingSuccess = false;
//			return;
//			//e.printStackTrace();		
//		} //end try
//		
//		tempClock = new DayClock();				
//		tempTime = tempClock.getCurrentDateTime();
//		
//		testRecordInfo = "";	
//		if (appendingSuccess == true){
//			successTestScenarioNum++;
//			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:  Externally Appendingly Writing to an Existing BigData '" 
//					+ bdClusterName + "' Cluster HDFS file - '" + existingHdfsFilePathAndName + "' at the time - " + tempTime;						     
//		} else {
//			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario: InternallyAppendingly Writing to an Existing BigData '"
//					+ bdClusterName + "' Cluster HDFS file - '" + existingHdfsFilePathAndName + "' at the time - " + tempTime;			    	    
//		}
//		writeDataToAFile(dcTestHDFS_RecFilePathAndName, testRecordInfo, true);			
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing HDFS Internally Writing, Externally Writing & Reading, and Externally Appending-Writing on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Done - Testing Generated HDFS Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total HDFS Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   HDFS Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   HDFS Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestHDFS_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestHDFS_RecFilePathAndName));
	
	}//end run()
	
	
	private static int appendFileDataToExistingHDFSFile (String existingHdfsFilePathAndName, String appendingDataFile, FileSystem currHadoopFS) throws IOException {
		int existVal = 10000;
		FSDataOutputStream fsDataOutStream = currHadoopFS.append(new Path(existingHdfsFilePathAndName));			
//		//PrintWriter bw = new PrintWriter(fsDataOutStream);	
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
//		
//		//String srcFilePathAndName2 = bdClusterUATestResultsParentFolder + localHDFSTestAppendingDataFileName;
//		System.out.println("\n*** appendingDataFile: " + appendingDataFile);
//		FileReader aFileReader = new FileReader(appendingDataFile);
//		BufferedReader br = new BufferedReader(aFileReader);
//		String line = "";
//		
//		while ((line = br.readLine()) != null) {
//			try {
//				//bw.append(line);
//				existVal = 0;
//				break;
//			} catch (Exception e) {
//				break;
//				//e.printStackTrace();
//			} //end try
//				
//		}
//		br.close();
//		aFileReader.close();
//						
//		bw.close();
		fsDataOutStream.close();
		return existVal;
	}//end appendFileDataToExistingHDFSFile

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
