package dcHWSandBoxClusterERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.DayClock;
import dcModelClasses.HdfsUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/18-19/2014; 2/24/2016; 3/14/2016 
*/ 


public class B2_Sdbx_dcTestPig_LoadingAndStoring {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localPigTestDataFileName = "";
	private static String pigTestedRelationsFolder = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/pig/bin/pig (<=TDH2.1.11) ==> /usr/bin/pig or /usr/hdp/2.3.2.0-2950/pig/bin/pig (HDP/TDH2.3.2) or /usr/hdp/2.3.4.0-3485/pig/bin/pig (HDP/TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for Pig-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localPigTestDataFileName = args[4];
		pigTestedRelationsFolder = args[5];
		
		
		if (!pigTestedRelationsFolder.endsWith("/")){
			pigTestedRelationsFolder += "/";
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
		String dcTestPig_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestPig_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestPig_RecFilePathAndName,  "Records of Testing Pig on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
	    sb.append("-----**********  Records of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster Pig Enterprise-Readiness Certification Testing Results  **********----- \n" );		    
	    sb.append("-----Automated Pig Internal Relation Creating (Loading, Filetering and Grouping), and Storing Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestPig_RecFilePathAndName, testRecHeader, false);		
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
		
				
		
		//4. Loop through bdClusterEntryNodeList to copy and internally drop existing pig table, create
		//     a pig-managed table, load data into the pig table and query data in the pig table (counting rows) 		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();
		for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Pig-Managed and Pig-External Table on Entry Node: " + tempENName);
			
			//(1) Move test data file to HDFS by external HDFS-writing
			String localWinSrcPigTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localPigTestDataFileName;
			//System.out.println("\n*** localWinSrcPigTestDataFilePathAndName: " + localWinSrcPigTestDataFilePathAndName);
			String hdfsPigTestDataFilePathAndName = "/data/test/Pig/dcPigTestData_employee.txt"; 	
			//System.out.println("\n*** hdfsPigTestDataFilePathAndName: " + hdfsPigTestDataFilePathAndName);
			
			moveWindowsLocalPigTestDataToHDFS (localWinSrcPigTestDataFilePathAndName, hdfsPigTestDataFilePathAndName, currHadoopFS);
			
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Moving Pig Table Test Data into HDFS of BigData '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			//(2) Testing Pig-Relation Creating (by loading, filtering and grouping), and relation data storing in HDFS files
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCommFactory = aBDNode.getBdENCommFactory();
			System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
									
			String hdfsPigTgtFileFolderName = pigTestedRelationsFolder + "employee" + (i+1);			
			String hdfsPigTgtFilePathAndName = hdfsPigTgtFileFolderName + "/part-r-00000";
			hdfsFilePathAndNameList.add(hdfsPigTgtFilePathAndName);
			
			String pigRelationSchemaStr = "AS (employeeId:Int, \n"
					+ "firstName:chararray, \n"
					+ "lastName:chararray, \n"
					+ "salary:Int, \n"
					+ "gender:chararray, \n"
					+ "address:chararray) \n";
			
			String pigShellInitiateStr = "/usr/bin/pig -e ";						
			String pigRelationCreationStoringCmd = pigShellInitiateStr + 
					"\"employee_A = LOAD '" + hdfsPigTestDataFilePathAndName + "' USING PigStorage(',') \n"
					+ pigRelationSchemaStr + ";\n" 
					+ "employee_B = FILTER employee_A by salary > 80000;\n"
					+ "employee_C = GROUP employee_B by gender;"
					+ "STORE employee_C INTO '" + hdfsPigTgtFileFolderName + "';\"";
						
			sb.append("sudo su - hdfs;\n");
		    sb.append("hadoop fs -mkdir -p " + pigTestedRelationsFolder + "; \n");
		    //sb.append("hadoop fs -chown hdfs:bduser " + pigTestedRelationsFolder + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + pigTestedRelationsFolder + "; \n");		    
		    sb.append(pigRelationCreationStoringCmd + ";\n");		         
		    sb.append("hadoop fs -chmod -R 550 " + pigTestedRelationsFolder + "; \n");
			
		    String pigScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "pig\\";
		    prepareFolder(pigScriptFilesFoder, "Local Pig Testing Script Files");
		    
		    String pigManagedTableTestScriptFullFilePathAndName = pigScriptFilesFoder + "dcTestPig_RelationCreationAndStoringScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (pigManagedTableTestScriptFullFilePathAndName,  "Script File For Testing Pig Relation Creating and Storing on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String pigManagedTestingCmds = sb.toString();
			writeDataToAFile(pigManagedTableTestScriptFullFilePathAndName, pigManagedTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(pigManagedTableTestScriptFullFilePathAndName));			
			HdfsUtil.runScriptFile_OnBDCluster(pigManagedTableTestScriptFullFilePathAndName, 
					pigScriptFilesFoder, enServerScriptFileDirectory, bdENCommFactory);
					
						
			Path pigGeneratedHdfsFilePath = new Path(hdfsPigTgtFilePathAndName);					
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();	
			
			String testRecordInfo = "";	
			if (currHadoopFS.exists(pigGeneratedHdfsFilePath)){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Pig Relation Creating (By LOADing with schema definition, FILTERing and GROUPing) and \n         Storing in HDFS on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Pig Testing-Generated HDFS File:  '" + hdfsPigTgtFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Pig Relation Creating (By LOADing with schema definition, FILTERing and GROUPing) and \n         Storing in HDFS on '" + bdClusterName + "' Cluster From Entry Node - '" 
						+ tempENName + "' at the time - " + tempTime
				        + "\n  --(2) Present Pig Testing-Generated HDFS File:  '" + hdfsPigTgtFilePathAndName + "'\n";				    	    
			}
			writeDataToAFile(dcTestPig_RecFilePathAndName, testRecordInfo, true);				
		}//end for
		
				
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally Pig Relation Creating (Loading, Filtering & Grouping) and Storing on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Pig Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Pig Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Pig Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Pig Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestPig_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestPig_RecFilePathAndName));
	
	}//end run()
	
	private static void moveWindowsLocalPigTestDataToHDFS (String localWinSrcPigTestDataFilePathAndName, 
							String hdfsPigTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcPigTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localPigTestDataFileName;
		//System.out.println("\n*** localWinSrcPigTestDataFilePathAndName: " + localWinSrcPigTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/Pig/dcPigTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsPigTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing Pig file: " + hdfsPigTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsPigTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
			
			
			FileReader aFileReader = new FileReader(localWinSrcPigTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localPigTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {				
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalPigTestDataToHDFS

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
