package dcBDApplianceERHCT_TestSuite;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dcModelClasses.DayClock;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/8/2014; 
*/ 

public class dcBDInt2_Prod1_ERHCT_Driver_Diagnosis {	
	private static int testingTimesSeqNo = 2;
	private static String bdClusterName = "BDInt2"; //BDProd..BDInt2
	private static String bdApplianceUATestResultsParentFolder = "C:\\BD\\BD_UAT\\";	
	
	
	public static void main(String[] args) throws Exception {
		String currBdClusterUATestResultsFolder = bdApplianceUATestResultsParentFolder + bdClusterName + "\\";
		prepareFolder(currBdClusterUATestResultsFolder, "User Acceptance Results for BD '" + bdClusterName + "' Cluster");
		
		//I.
		//I.1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//I.2. Prepare files for testing records
		String dcTestBdCluster_RecFilePathAndName = currBdClusterUATestResultsFolder + "dcTestBigDataComponentFunctions_Records_No" + testingTimesSeqNo + ".txt";
		prepareFile (dcTestBdCluster_RecFilePathAndName,  "Records of Testing BD Component Functions on '" + bdClusterName + "' Cluster");
		
		String [] uatParametersArray = new String [10];
		uatParametersArray[0] = Integer.toString(testingTimesSeqNo);
		uatParametersArray[1] = bdClusterName;
		uatParametersArray[2] = bdApplianceUATestResultsParentFolder;		
		uatParametersArray[3] = currBdClusterUATestResultsFolder;
		

		//II. Actual Testing On Critical Hadoop Data-oriented Components
		//1. Testing HDSF Writing and Reading Internally and Externally
		System.out.println("*** Intializing HDFS Writing and Reading Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHDFSTestDataFileName = "dcHDFSTest_Data.txt";
		String localHDFSTestAppendingDataFileName = "dcHDFSTest_AppendingData.txt";
		
		uatParametersArray[4] = localHDFSTestDataFileName;
		uatParametersArray[5] = localHDFSTestAppendingDataFileName;	
		
		//A1_dcTestHDFS_WritingAndReading.main(uatParametersArray);
		
		//2.Testing MapReduce word-counting
		System.out.println("*** Intializing Testing MapReduce on BD '" + bdClusterName + "' Cluster... \n");
		String localMapReduceTestDataFileName = "dcFSETestData.txt";
		String mapReduceTestFolderName = "/data/test/MapReduce/";		
		String localMapReduceJarFileName = "StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"WordCount-0.0.1-SNAPSHOT-jar-with-dependencies.jar";				
						
		uatParametersArray[4] = localMapReduceTestDataFileName;		
		uatParametersArray[5] = mapReduceTestFolderName;	
		uatParametersArray[6] = localMapReduceJarFileName;
		
		//A2_dcTestMapReduce_WordCounting.main(uatParametersArray);
		
		
		
		//3. Testing Sqoop Importing and Exporting
		System.out.println("*** Intializing Sqoop Importing and Exporting Testing on BD '" + bdClusterName + "' Cluster... \n");
		String sqlServerDBName = "EDT_BigData"; 
		String importTableName = "archiveFile";
		String exportTableName = "sqoopExported"; 	
		String sqoopImportedFolder = "/data/test/Sqoop/ecg/";
		String exportTableName2 = "sqoopExported2";	
		
		uatParametersArray[4] = sqlServerDBName;
		uatParametersArray[5] = importTableName;	
		uatParametersArray[6] = exportTableName;		
		uatParametersArray[7] = sqoopImportedFolder;
		uatParametersArray[8] = exportTableName2;
		
		//A3_dcTestSqoop_ImportingAndExporting.main(uatParametersArray);
		
		//4. Testing Hive Table Creating, Loading, Dropping and Querying 
		System.out.println("*** Intializing Hive Table Creating, Loading and Query Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHiveTestDataFileName = "dcHHPTestData_employee.txt";
		String hiveTestedTablesFolder = "/data/test/Hive/";
				
		uatParametersArray[4] = localHiveTestDataFileName;		
		uatParametersArray[5] = hiveTestedTablesFolder;	
			
		//B1_dcTestHive_LoadingAndQuerying.main(uatParametersArray);	
		
		//5. Testing Pig Relation Creating (By LOADing, FILTERing, and Grouping), and STOREing Data to HDFS 
		System.out.println("*** Intializing Pig Relation Creating and Storing Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localPigTestDataFileName = "dcHHPTestData_employee.txt";
		String pigTestedRelationsFolder = "/data/test/Pig/";
				
		uatParametersArray[4] = localPigTestDataFileName;		
		uatParametersArray[5] = pigTestedRelationsFolder;	
			
		//B2_dcTestPig_LoadingAndStoring.main(uatParametersArray);			
		
		//6. Testing HBase Table Creating, Loading, Disabling/Dropping and Querying 
		System.out.println("*** Intializing HBase Table Creating, Loading and Query Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHBaseTestDataFileName = "dcHHPTestData_employee.txt";
		String hbaseTestedTablesInfoFolder = "/data/test/HBase/";
		//String hbaseVersion = "hbase-0.94";
				
		uatParametersArray[4] = localHBaseTestDataFileName;		
		uatParametersArray[5] = hbaseTestedTablesInfoFolder;
		//uatParametersArray[6] = hbaseVersion;
			
		//B3_dcTestHBase_LoadingAndQuerying.main(uatParametersArray);		
		
		
		
		//7.Testing Flume Storing a File Data to HDFS 
		System.out.println("*** Intializing Testing Flume Storing File In HDFS on BD '" + bdClusterName + "' Cluster... \n");
		String localFlumeTestDataFileName = "dcFSETestData.txt";
		String flumeTestedFilesFolder = "/data/test/Flume/";
		String localFlumeConfigFileName = "dcFlumeConf.txt";
				
		uatParametersArray[4] = localFlumeTestDataFileName;		
		uatParametersArray[5] = flumeTestedFilesFolder;	
		uatParametersArray[6] = localFlumeConfigFileName;	
		
		//C1_dcTestFlume_StoringData.main(uatParametersArray);	 
		
		
		//8.Testing Elastic Indexing and Searching  
		System.out.println("*** Intializing Testing ElasticSearch Indexing and Searching on BD '" + bdClusterName + "' Cluster... \n");
		String esTestedFilesFolder = "/data/test/ElasticSearch/";
						
		uatParametersArray[4] = esTestedFilesFolder;		
			
		//C2_dcTestES_IndexingAndSearching.main(uatParametersArray);	 
		
		//9.Testing Storm Using Flume & ES 
		System.out.println("*** Intializing Testing Storm Using Flume and ElasticSearch on BD '" + bdClusterName + "' Cluster... \n");
		String localStormTestDataFileName = "dcFSETestData.txt";
		String stormTestedFilesFolder = "/data/test/Storm/";
		String localStormFlumeConfigFileName = "dcStormFlumeConf.txt";
		String localStormJarFileName = "StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";
		
		String esClusterName = "";
		if (bdClusterName.equalsIgnoreCase("BDDev")){
			esClusterName = "bddevelasticsearch";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDInt")){
			esClusterName = "bdintelasticsearch";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDProd")
				|| bdClusterName.equalsIgnoreCase("BDPrd")){
			esClusterName = "bdprdelasticsearch";
		}
			
				
		uatParametersArray[4] = localStormTestDataFileName;		
		uatParametersArray[5] = stormTestedFilesFolder;	
		uatParametersArray[6] = localStormFlumeConfigFileName;
		uatParametersArray[7] = localStormJarFileName;
		uatParametersArray[8] = esClusterName;	
		
		//C3_dcTestStorm_UsingFlumeAndES.main(uatParametersArray);
	
		
				
		//10. Generating All-In-One Integrated Report File
		System.out.println("*** Intializing Get Integerated Report for All Testing on BD '" + bdClusterName + "' Cluster... \n");
		//E_dcGet_IntegratedERHCTReport.main(uatParametersArray);
		
		
		
		//III.
		//Notice message on the console and writing to record file		
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing BigData Component Functions on '" + bdClusterName + "' Cluster!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestBdCluster_RecFilePathAndName, currNotingMsg, true);		
		//Desktop.getDesktop().open(new File(dcTestBdCluster_RecFilePathAndName));
		
	}//end main
	
	

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
	

	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder	

}//end class