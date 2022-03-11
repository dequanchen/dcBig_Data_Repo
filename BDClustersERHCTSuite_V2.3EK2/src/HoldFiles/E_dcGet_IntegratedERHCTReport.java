package HoldFiles;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/8/2014, 1/1/2015; 2/17-20/2017
*/ 


public class E_dcGet_IntegratedERHCTReport {
	private static int testingTimesSeqNo = 4;
	private static String bdClusterName = ""; 

	private static String bdClusterERHCTestResultsParentFolder = "";
	
	
	public static void main(String[] args) throws Exception {
		if (args.length < 3){
			System.out.println("\n*** 3 parameters for ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterERHCTestResultsParentFolder = args[2];
		
							
		run();
	}//end main
	
	public static void run() throws Exception {	
		//1.
		String bdClusterERHCTestResultsFolder = bdClusterERHCTestResultsParentFolder + bdClusterName + "\\";
		ArrayList<String> erhctTestResultsFilePathAndNameList = new  ArrayList<String>();	
		
		//(1) A1-A3: HDFS, MapReduce and Sqoop
		String dcTestHDFS_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestHDFS_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestHDFS_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestHDFS_RecFilePathAndName);
		}
		
		String dcTestMapReduce_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestMapReduce_FileWordCounting_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestMapReduce_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestMapReduce_RecFilePathAndName);
		}
		
		String dcTestYarn_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestYarn_PiApproximation_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestYarn_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestYarn_RecFilePathAndName);
		}
		
		
		String dcTestSqoop_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestSqoop_ImportingAndExporting_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestSqoop_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestSqoop_RecFilePathAndName);
		}
				
		//(2) B1-B4: Hive, Pig, HBase and Spark
		String dcTestHive_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestHive_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestHive_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestHive_RecFilePathAndName);
		}		
		
		String dcTestPig_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestPig_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestPig_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestPig_RecFilePathAndName);
		}
		
		
		String dcTestHBase_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestHBase_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestHBase_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestHBase_RecFilePathAndName);
		}
		
		
		String dcTestSpark_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestSpark_RDDCTAS_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestSpark_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestSpark_RecFilePathAndName);
		}
			
		
		//C1-C4: Flume, ES, Storm, Oozie and Kafka  
		String dcTestFlume_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestFlume_StoringFileIntoHdfs_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestFlume_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestFlume_RecFilePathAndName);
		}
		
		String dcTestElasticSearch_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestES_IndexingAndSearching_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestElasticSearch_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestElasticSearch_RecFilePathAndName);
		}
		
		String dcTestSolr_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestSolr_CollectioningAndSearching_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestSolr_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestSolr_RecFilePathAndName);
		}
				
		String dcTestStorm_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestStorm_UsingFlumeAndElasticSearch_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestStorm_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestStorm_RecFilePathAndName);
		}
		
		String dcTestOozie_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestOozie_WorkflowActions_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestOozie_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestOozie_RecFilePathAndName);
		}		
		
		
		String dcTestKafka_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestKafka_TopicCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestKafka_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestKafka_RecFilePathAndName);
		}
		
		//D1-Dn Knox...
		String dcTestKnox_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestKnox_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestKnox_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestKnox_RecFilePathAndName);
		}
		
		
		//D0: Phoenix
		String dcTestPhoenix_RecFilePathAndName = bdClusterERHCTestResultsFolder + "dcTestPhoenix_Records_No" + testingTimesSeqNo + ".sql";
		if (obtainLocalFileExistingStatus(dcTestPhoenix_RecFilePathAndName) == true){
			erhctTestResultsFilePathAndNameList.add(dcTestPhoenix_RecFilePathAndName);
		}
		
		
/////////////////
		String erhctIntegratedReportFilePathAndName = bdClusterERHCTestResultsFolder + "dcMC_BDCluster_" + bdClusterName + "_ERHCT_IntegratedReport_No" + testingTimesSeqNo + ".sql";
		prepareFile_DeleteOld (erhctIntegratedReportFilePathAndName, "Integrated Report of dcERHCT Against MC-BigData-Appliance");
		
		StringBuilder sb = new StringBuilder();
	    sb.append("Integrated Report of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Hadoop Cluster Enterprise-Readiness Hadoop Certification Testing Results\n\n" );		    
	    sb.append("                           Dequan Chen, Ph.D. \n\n"); 
	    
	    if(bdClusterName.equalsIgnoreCase("BDDev")){
	    	sb.append("--*-- BDDev: 2 Edge Nodes/2 MNs/1 Knox Node (KX01), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("BDSdbx")){
	    	sb.append("--*-- BDSdbx: 4 Edge Nodes/2 MNs + 1 Knox Gateway (KX01), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("BDProd2") 
	    		|| bdClusterName.equalsIgnoreCase("BDInt")){
	    	sb.append("--*-- BDProd2: 6 Edge Nodes/2 MNs/2 Knox Nodes (KX03 & KX04), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    
	    if(bdClusterName.equalsIgnoreCase("BDInt2") 
	    		|| bdClusterName.equalsIgnoreCase("BDTest2")
	    		|| bdClusterName.equalsIgnoreCase("BDProd")){
	    	sb.append("--*-- BDProd: 5 Edge Nodes/2 MNs + 2 Knox Gateways (KX03 & KX04), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    
	    sb.append("--*-- Testing Times Sequence No: " + testingTimesSeqNo + " " );
	    sb.append("\n--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	   
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    
	    String intgrtdReportHeader = sb.toString();
		writeDataToAFile(erhctIntegratedReportFilePathAndName, intgrtdReportHeader, false);	
		sb.setLength(0);
		
	       
        int erhctResultsFileNumber = erhctTestResultsFilePathAndNameList.size();
        System.out.println("\n*** erhctResultsFileNumber is: " + erhctResultsFileNumber);
        
        String erhctSuiteStartTime = "--=-- Testing Starting Time: ";
        int totalDays = 0;
        int totalHrs = 0;
        int totalMinutes = 0 ;
        int totalSeconds = 0;
        
		for (int i = 0; i < erhctResultsFileNumber; i++){ //1..erhctResultsFileNumber
			String tempTestResultFilePathAndName = erhctTestResultsFilePathAndNameList.get(i);
			System.out.println("\n--- (" + (i+1) + ") tempTestResultFilePathAndName: " + tempTestResultFilePathAndName);	
			
			String testedHadoopFunction = "";
			if (tempTestResultFilePathAndName.contains("dcTestHDFS_")){
				testedHadoopFunction = "HDFS";
			}
			if (tempTestResultFilePathAndName.contains("dcTestMapReduce_")){
				testedHadoopFunction = "MapReduce";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestYarn_")){
				testedHadoopFunction = "Yarn";
			}
			
			
			if (tempTestResultFilePathAndName.contains("dcTestSqoop_")){
				testedHadoopFunction = "Sqoop";
			}
			if (tempTestResultFilePathAndName.contains("dcTestHive_")){
				testedHadoopFunction = "Hive";
			}
			if (tempTestResultFilePathAndName.contains("dcTestPig_")){
				testedHadoopFunction = "Pig";
			}
			if (tempTestResultFilePathAndName.contains("dcTestHBase_")){
				testedHadoopFunction = "HBase and Hcatalog";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestSpark_")){
				testedHadoopFunction = "Spark";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestFlume_")){
				testedHadoopFunction = "Flume";
			}
			if (tempTestResultFilePathAndName.contains("dcTestES_")){
				testedHadoopFunction = "ElasticSearch";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestSolr_")){
				testedHadoopFunction = "Solr";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestStorm_")){
				testedHadoopFunction = "Storm";
			}
			if (tempTestResultFilePathAndName.contains("dcTestOozie_")){
				testedHadoopFunction = "Oozie";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestKafka_")){
				testedHadoopFunction = "Kafka";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestKnox_")){
				testedHadoopFunction = "Knox";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestPhoenix_")){
				testedHadoopFunction = "Phoenix";
			}
			
			
			sb.append((i+1) + ". " + testedHadoopFunction + "\n" );
			if (testedHadoopFunction.equalsIgnoreCase("Sqoop")){
				sb.append("\n");
			}
			
			FileReader erhctResultFileReader = new FileReader(tempTestResultFilePathAndName);
			BufferedReader br_erhct = new BufferedReader(erhctResultFileReader);			
			String line = "";
			int lineNumber = 0;
			while ((line = br_erhct.readLine()) != null){
				lineNumber++;
				System.out.println("\n--- line is: " + line);
				if (testedHadoopFunction.equalsIgnoreCase("HDFS") 
						&& line.contains("--=-- Testing Results File - Generated Time:")){
					erhctSuiteStartTime += line.replace("--=-- Testing Results File - Generated Time:", "");
				}
				
				if (line.contains("*-*-* Total Time Used:")){
					String temp = line.replace("*-*-* Total Time Used:", "").trim();
					if (temp.contains("days")){
						String[] tempSplit = temp.split("days");
						totalDays += Integer.valueOf(tempSplit[0].trim());
						temp = tempSplit[1].trim();
					}
					if (temp.contains("hours")){
						String[] tempSplit = temp.split("hours");
						totalHrs += Integer.valueOf(tempSplit[0].trim());
						temp = tempSplit[1].trim();
					}
					
					if (temp.contains("minutes")){
						String[] tempSplit = temp.split("minutes");
						totalMinutes += Integer.valueOf(tempSplit[0].trim());
						temp = tempSplit[1].trim();
					}
					if (temp.contains("seconds")){
						String[] tempSplit = temp.split("seconds");
						totalSeconds += Integer.valueOf(tempSplit[0].trim());
					}		
					
				}
				
				
				if (lineNumber > 8){
					System.out.println("\n--- Retrieved line: " + line);	
					sb.append(line + " \n" );
				}//end  if
				
			}//end while
			br_erhct.close();
			erhctResultFileReader.close();
			
			sb.append("\n\n" );
		}//end for
		
	
		
		
		if (totalSeconds >= 60) {
			int addToMinutes = totalSeconds/60;
			totalSeconds = totalSeconds - 60 * addToMinutes; 
			totalMinutes += addToMinutes;
		}
		if (totalMinutes >= 60) {
			int addTohrs = totalMinutes/60;
			totalMinutes = totalMinutes - 60 * addTohrs; 
			totalHrs += addTohrs;
		}
		if (totalHrs >= 24) {
			int addToDays = totalHrs/24;
			totalHrs = totalHrs - 24 * addToDays; 
			totalDays += addToDays;
		}
		
		String intgrtdReportMiddle = "" + erhctSuiteStartTime + "\n"
				+ "--*-*-* Total Time Used: " + totalDays + " days " + totalHrs 
				+ " hours " + totalMinutes + " minutes "  + totalSeconds + " seconds for all tested "
				+ erhctResultsFileNumber + " Hadoop Functions \n\n"; 
		
		writeDataToAFile(erhctIntegratedReportFilePathAndName, intgrtdReportMiddle, true);
		
		String intgrtdReportMain = sb.toString();
		
		writeDataToAFile(erhctIntegratedReportFilePathAndName, intgrtdReportMain, true);	
		sb.setLength(0);
		
		Desktop.getDesktop().open(new File(erhctIntegratedReportFilePathAndName));
		
		System.out.println("\n--- Done - Integrated Report Generating!!!");	

	}//end main
	
	private static boolean obtainLocalFileExistingStatus (String localFilePathAndName){
		boolean localFileExistingStatus = false;
		File aFile = new File (localFilePathAndName);
		if (aFile.exists()){
			localFileExistingStatus = true;
	    }
		
		System.out.println("\n*** '" + localFileExistingStatus+ "' for the existing status of File - " + localFilePathAndName);
		
		return localFileExistingStatus;
	}//end localFileExistingStatus
	
	//@SuppressWarnings("unused")
	private static void prepareFile_DeleteOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				System.out.println("\n .. Deleted file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile_DeleteOld
	
	
	@SuppressWarnings("unused")
	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			//if (aFile.exists()){
			//	aFile.delete();
			//	System.out.println("\n .. Deleted file for etl_Backloading: \n" + localFilePathAndName);
			//	aFile.createNewFile();
			//	System.out.println("\n .. Created file for etl_Backloading: \n" + localFilePathAndName);
			//}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	

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


}//end class
