package dcHWSandBoxClusterERHCT_TestSuite;

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
* Date: 12/8/2014, 1/1/2015; 2/25/2016
*/ 


public class E_Sdbx_dcGet_IntegratedERHCTReport {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = ""; //BDProd..BDInt..BDDev..BDSdbx..HWSdbx
	//private static String bdClusterUATestResultsParentFolder = "C:\\BD\\BD_UAT\\";		
	private static String bdClusterUATestResultsParentFolder = "";
	//private static String bdClusterUATestResultsFolder = "";
	
	public static void main(String[] args) throws Exception {
		if (args.length < 5){
			System.out.println("\n*** 5 parameters for Phoenix-UAT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		//bdClusterUATestResultsFolder = args[3];	
							
		run();
	}//end main
	
	public static void run() throws Exception {	
		//1.
		String bdClusterUATestResultsFolder = bdClusterUATestResultsParentFolder + bdClusterName + "\\";
		ArrayList<String> uatTestResultsFilePathAndNameList = new  ArrayList<String>();	
		
		//(1) A1-A2: HDFS and Sqoop
		String dcTestHDFS_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHDFS_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestHDFS_RecFilePathAndName);
		
		String dcTestSqoop_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestSqoop_ImportingAndExporting_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestSqoop_RecFilePathAndName);
		
		//(2) B1-B3: Hive, Pig and HBase
		String dcTestHive_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHive_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestHive_RecFilePathAndName);
		
		String dcTestPig_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestPig_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestPig_RecFilePathAndName);
		
		String dcTestHBase_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHBase_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestHBase_RecFilePathAndName);
		
		
		//C1-C3: Flume, ES and Storm
		String dcTestFlume_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestFlume_StoringFileIntoHdfs_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestFlume_RecFilePathAndName);
		
//		String dcTestElasticSearch_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestES_IndexingAndSearching_Records_No" + testingTimesSeqNo + ".sql";
//		uatTestResultsFilePathAndNameList.add(dcTestElasticSearch_RecFilePathAndName);
//		
		String dcTestStorm_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestStorm_UsingFlumeAndElasticSearch_Records_No" + testingTimesSeqNo + ".sql";
		uatTestResultsFilePathAndNameList.add(dcTestStorm_RecFilePathAndName);
		
//		//D1: Phoenix
//		String dcTestPhoenix_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestPhoenix_Records_No" + testingTimesSeqNo + ".sql";
//		uatTestResultsFilePathAndNameList.add(dcTestPhoenix_RecFilePathAndName);
		
		//
		String uatIntegratedReportFilePathAndName = bdClusterUATestResultsFolder + "dcMC_BigDataAppliance_" + bdClusterName + "_ERHCT_IntegratedReport.sql";
		prepareFile_DeleteOld (uatIntegratedReportFilePathAndName, "Integrated Report of dcUAT Against MC-BigData-Appliance");
		
		StringBuilder sb = new StringBuilder();
	    sb.append("Integrated Report of Mayo Clinic Un-Kerberized '"+ bdClusterName +"' Cluster Enterprise-Readiness Hadoop Certification Testing Results\n\n" );		    
	    sb.append("                           Dequan Chen, Ph.D. \n\n"); 
	    
	    if(bdClusterName.equalsIgnoreCase("BDDev")){
	    	sb.append("--*-- BDDev: 2 Edge Nodes/1 secondary MN (MN02), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("BDSdbx")){
	    	sb.append("--*-- BDSdbx: 4 Edge Nodes/1 secondary MN (MN02), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("BDInt")){
	    	sb.append("--*-- BDInt: 6 Edge Nodes/1 secondary MN (MN02), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("BDProd")){
	    	sb.append("--*-- BDProd: 5 Edge Nodes/1 secondary MN (MN02), TDH2.3.4 / SLES 11 (SP3) \n" );
	    }
	    if(bdClusterName.equalsIgnoreCase("HWSdbx")){
	    	sb.append("--*-- HWSdbx: Single Edge Node, HDP2.3.2 / CentOS Release 6.7 \n" );
	    }
	    
	    sb.append("--*-- Testing Times Sequence No: " + testingTimesSeqNo + " " );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    
	    
	    String intgrtdReportHeader = sb.toString();
		writeDataToAFile(uatIntegratedReportFilePathAndName, intgrtdReportHeader, false);	
		sb.setLength(0);
		
		//--=-- Testing Starting Time: 12/23/2014 21:28:34
		//*-*-* Total Time Used: 15 minutes 41 seconds
       
        int uatResultsFileNumber = uatTestResultsFilePathAndNameList.size();
        System.out.println("\n*** uatResultsFileNumber is: " + uatResultsFileNumber);
        
        String uatSuiteStartTime = "--=-- Testing Starting Time: ";
        int totalDays = 0;
        int totalHrs = 0;
        int totalMinutes = 0 ;
        int totalSeconds = 0;
        
		for (int i = 0; i < uatResultsFileNumber; i++){ //1..uatResultsFileNumber
			String tempTestResultFilePathAndName = uatTestResultsFilePathAndNameList.get(i);
			System.out.println("\n--- (" + (i+1) + ") tempTestResultFilePathAndName: " + tempTestResultFilePathAndName);	
			
			String testedHadoopFunction = "";
			if (tempTestResultFilePathAndName.contains("dcTestHDFS_")){
				testedHadoopFunction = "HDFS";
			}
			if (tempTestResultFilePathAndName.contains("dcTestSqoop_")){
				testedHadoopFunction = "Sqoop and MapReduce";
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
			if (tempTestResultFilePathAndName.contains("dcTestFlume_")){
				testedHadoopFunction = "Flume";
			}
			if (tempTestResultFilePathAndName.contains("dcTestES_")){
				testedHadoopFunction = "ElasticSearch";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestStorm_")){
				testedHadoopFunction = "Storm";
			}
			
			if (tempTestResultFilePathAndName.contains("dcTestPhoenix_")){
				testedHadoopFunction = "Phoenix";
			}
			
			
			sb.append((i+1) + ". " + testedHadoopFunction + "\n" );
			if (testedHadoopFunction.equalsIgnoreCase("Sqoop and MapReduce")){
				sb.append("\n");
			}
			
			FileReader uatResultFileReader = new FileReader(tempTestResultFilePathAndName);
			BufferedReader br_uat = new BufferedReader(uatResultFileReader);			
			String line = "";
			int lineNumber = 0;
			while ((line = br_uat.readLine()) != null){
				lineNumber++;
				System.out.println("\n--- line is: " + line);
				if (testedHadoopFunction.equalsIgnoreCase("HDFS") 
						&& line.contains("--=-- Testing Results File - Generated Time:")){
					uatSuiteStartTime += line.replace("--=-- Testing Results File - Generated Time:", "");
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
				//  *-*-* Total Time Used: 1 minutes 54 seconds 
				
//				if (line.contains("===========================================================")){
//					line = line.replace("===========================================================", "======================================================");
//				}
				
				if (lineNumber > 7){
					System.out.println("\n--- Retrieved line: " + line);	
					sb.append(line + " \n" );
				}//end  if
				
			}//end while
			br_uat.close();
			uatResultFileReader.close();
			
			sb.append("\n\n" );
		}//end for
		
		//System.out.println("\n--- intgrtdReportMain: " +  sb.toString());	
		
		
		
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
		
		String intgrtdReportMiddle = "" + uatSuiteStartTime + "\n"
				+ "--*-*-* Total Time Used: " + totalDays + " days " + totalHrs 
				+ " hours " + totalMinutes + " minutes "  + totalSeconds + " seconds for all tested "
				+ uatResultsFileNumber + " Hadoop Functions \n\n"; 
		
		writeDataToAFile(uatIntegratedReportFilePathAndName, intgrtdReportMiddle, true);
		
		String intgrtdReportMain = sb.toString();
		//System.out.println("\n--- intgrtdReportMain: " +  intgrtdReportMain);	
		writeDataToAFile(uatIntegratedReportFilePathAndName, intgrtdReportMain, true);	
		sb.setLength(0);
		
		Desktop.getDesktop().open(new File(uatIntegratedReportFilePathAndName));
		
		System.out.println("\n--- Done - Integrated Report Generating!!!");	

	}//end main
	
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
