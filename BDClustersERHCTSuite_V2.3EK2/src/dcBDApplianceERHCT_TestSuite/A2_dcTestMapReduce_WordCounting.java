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
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 06/29/2014; 7/5/2016; 5/10/2017
*/ 

@SuppressWarnings("unused")
public class A2_dcTestMapReduce_WordCounting {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localMapReduceTestDataFileName = "";
	private static String mapReduceTestFolderName = "";
	private static String localMapReduceJarFileName = "";
	private static String internalKinitCmdStr = "";
		
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	
	// /usr/lib/hadoop/bin/hadoop (<=TDH2.1.11) ==> /usr/bin/hadoop or /usr/hdp/2.3.4.0-3485/hadoop (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 6+1 parameters for MapReduce-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localMapReduceTestDataFileName = args[4];
		mapReduceTestFolderName = args[5];
		localMapReduceJarFileName = args[6];
		
		internalKinitCmdStr = args[9];		
		
		
		if (!mapReduceTestFolderName.endsWith("/")){
			mapReduceTestFolderName += "/";
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
		String mapReduceScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "MapReduce\\";
	    prepareFolder(mapReduceScriptFilesFoder, "Local MapReduce Testing Script Files");
	    
		String dcTestMapReduce_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestMapReduce_FileWordCounting_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestMapReduce_RecFilePathAndName,  "Records of Testing MapReduce on '" + bdClusterName + "' Cluster");
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Internal MapReduce - Mapper and Reducer Representative Scenario Testing "
	    		+ "\n-- 		  Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestMapReduce_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//System.out.println("\n--- hdfsNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsNnIPAddressAndPort);
				
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();	
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		mapReduceTestFolderName = "/user/" + loginUser4AllNodesName + "/test/MapReduce/";//Modify mapReduceTestFolderName from "/data/test/MapReduce/"
			
		String mapReduceTestInputFolder = mapReduceTestFolderName + "input/";
		String mapReduceTestOutputFolder = mapReduceTestFolderName + "output/";
		
		System.out.println(" *** mapReduceTestInputFolder: " + mapReduceTestInputFolder);
		System.out.println(" *** mapReduceTestOutputFolder: " + mapReduceTestOutputFolder);
		
		
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
				
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		//4. Loop through bdClusterEntryNodeList to internally run MapReduce Mapper and Reducer jobs - word counting		 		
		//clusterENNumber_Start = 0; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 1; //1..2..3..4..5..6
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing MapReduce Mapper and Reducer on Entry Node: " + tempENName);
			
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
						
						
						
			//(1) Move MapReduce test data file and jar file into Linux local folder for testing
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localMapReduceTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);			
			
			int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localMapReduceJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving MapReduce Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving MapReduce Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
			
			if (exitVal2 == 0 ){
				System.out.println("\n*** Done - Moving MapReduce Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving MapReduce Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
						
			
			
			//(2) Testing MapReduce Function Using Word-counting Java Program
			//String localWinSrcMapReduceTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localMapReduceTestDataFileName;
			String enLocalMapReduceTestDataFilePathAndName = enServerScriptFileDirectory + localMapReduceTestDataFileName;			
			String hdfsMapReduceTestDataFilePathAndName = mapReduceTestInputFolder + "dcMapReduceTestData_employee.txt"; //"/data/test/MapReduce/dcMapReduceTestData_employee.txt"; 	
			
			// /usr/lib/hadoop/bin/hadoop jar StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar mapreduce.WordCount /user/m041785/test/MapReduce/input /user/m041785/test/MapReduce/output;
			String enServerMapReduceJarFilePathAndName = enServerScriptFileDirectory + localMapReduceJarFileName;			
			String runMapReduceWordCountJarCmd = "/usr/bin/hadoop jar " + enServerMapReduceJarFilePathAndName +  " mapreduce.WordCount " + mapReduceTestInputFolder + " " + mapReduceTestOutputFolder ;
			
						
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -rm -r -skipTrash " + mapReduceTestInputFolder + "; \n");
			sb.append("hadoop fs -rm -r -skipTrash " + mapReduceTestOutputFolder + "; \n");
			
			sb.append("hadoop fs -mkdir -p " + mapReduceTestInputFolder + "; \n");
			//sb.append("hadoop fs -mkdir -p " + mapReduceTestOutputFolder + "; \n");			
			//sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + mapReduceTestFolderName + "; \n");			
		    //sb.append("hadoop fs -chown hdfs:bduser " + mapReduceTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 755 " + mapReduceTestFolderName + "; \n");
		    
		    sb.append("hadoop fs -copyFromLocal " + enLocalMapReduceTestDataFilePathAndName + " " + hdfsMapReduceTestDataFilePathAndName + "; \n");
		    		    
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
		    
		    sb.append(runMapReduceWordCountJarCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + mapReduceTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localMapReduceWordCountTestingScriptFilePathAndName = mapReduceScriptFilesFoder + "dcTestMapReduce_WordCountingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localMapReduceWordCountTestingScriptFilePathAndName,  "Script File For Testing MapReduce Word Counting on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String mapReduceWordCountingTestingCmds = sb.toString();
			writeDataToAFile(localMapReduceWordCountTestingScriptFilePathAndName, mapReduceWordCountingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localMapReduceWordCountTestingScriptFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localMapReduceWordCountTestingScriptFilePathAndName, 
						mapReduceScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			String hdfsMapReduceOutPutFilePathAndName = mapReduceTestOutputFolder + "part-r-00000";						
			Path mapReduceOutPutHdfsFilePath = new Path(hdfsMapReduceOutPutFilePathAndName);
			
			boolean currTestScenarioSuccessStatus = false;
			if (currHadoopFS.exists(mapReduceOutPutHdfsFilePath)) {
				System.out.println("\n***  Existing file : " + mapReduceOutPutHdfsFilePath);
				hdfsFilePathAndNameList.add(hdfsMapReduceOutPutFilePathAndName);
				
				FileStatus[] status = currHadoopFS.listStatus(mapReduceOutPutHdfsFilePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				int totalWordCount = 0;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					line = line.replaceAll("\\s", "===");
					//System.out.println(" --1--  line: " + line);
					
					if (line.contains("===")) {						
						String[] lineSplit = line.split("===");
						totalWordCount += Integer.valueOf(lineSplit[1].trim());	
					}		
												
				}//end while
				br.close();
				System.out.println(" *** totalWordCount == " + totalWordCount);	
				
				if (totalWordCount == 36){
					currTestScenarioSuccessStatus = true;					
				} 		
				
	        }//end outer if	
			System.out.println(" *** currTestScenarioSuccessStatus: " + currTestScenarioSuccessStatus);	
			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
				
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus == true){
				successTestScenarioNum++;			
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally MapReduce (Mapper and Reducer) Word Counting "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present MapReduce Output HDFS Folder:  '" + mapReduceTestOutputFolder + "'"
				        + "\n  --(3) MapReduce Testing Total Time Used: " + timeUsed + "\n"; 	
			} else {
				testRecordInfo = "-*-*- 'Failed' - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally MapReduce (Mapper and Reducer) Word Counting "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Present MapReduce Output HDFS Folder:  '" + mapReduceTestOutputFolder + "'"
				        + "\n  --(3) MapReduce Testing Total Time Used: " + timeUsed + "\n";	
			}
			writeDataToAFile(dcTestMapReduce_RecFilePathAndName, testRecordInfo, true);
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
		currNotingMsg += "\n***** Done - Testing MapReduce (Mapper & Reducer) Internal Word-Counting on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present MapReduce Testing Generated Total Critical " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total MapReduce Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   MapReduce Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   MapReduce Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestMapReduce_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestMapReduce_RecFilePathAndName));
	
	}//end run()
	
	
	private static void moveWindowsLocalMapReduceTestDataToHDFS (String localWinSrcMapReduceTestDataFilePathAndName, 
							String hdfsMapReduceTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcMapReduceTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localMapReduceTestDataFileName;
		//System.out.println("\n*** localWinSrcMapReduceTestDataFilePathAndName: " + localWinSrcMapReduceTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/MapReduce/dcMapReduceTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsMapReduceTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing MapReduce file: " + hdfsMapReduceTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsMapReduceTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
						
			FileReader aFileReader = new FileReader(localWinSrcMapReduceTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localMapReduceTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {				
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalMapReduceTestDataToHDFS

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
