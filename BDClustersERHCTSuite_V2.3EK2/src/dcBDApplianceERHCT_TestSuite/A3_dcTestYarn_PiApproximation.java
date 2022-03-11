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
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 8/31/2016; 5/10/2017
*/ 


public class A3_dcTestYarn_PiApproximation {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static int LocalPiApproximationTermNumber = 0;
	private static String yarnTestFolderName = "";
	private static String localYarnJarFileName = "";
	private static String internalKinitCmdStr = "";
		
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	
	// /usr/lib/hadoop/bin/hadoop (<=TDH2.1.11) ==> /usr/bin/hadoop or /usr/hdp/2.3.4.0-3485/hadoop (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 6+1 parameters for Yarn-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];
		
		LocalPiApproximationTermNumber = Integer.valueOf(args[4]);
		yarnTestFolderName = args[5];
		localYarnJarFileName = args[6];
				
		internalKinitCmdStr = args[9];		
		
		
		if (!yarnTestFolderName.endsWith("/")){
			yarnTestFolderName += "/";
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
		String mapReduceScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Yarn\\";
	    prepareFolder(mapReduceScriptFilesFoder, "Local Yarn Testing Script Files");
	    
		String dcTestYarn_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestYarn_PiApproximation_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestYarn_RecFilePathAndName,  "Records of Testing Yarn on '" + bdClusterName + "' Cluster");
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Internal Yarn - Running Java Program In A Jar File Scenario Testing "
	    		+ "\n-- 		  Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestYarn_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();

		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
				
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();	
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		yarnTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Yarn/";//Modify yarnTestFolderName from "/data/test/Yarn/"
			
				
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
		
		//4. Loop through bdClusterEntryNodeList to internally run Yarn Mapper and Reducer jobs - word counting		 		
		//clusterENNumber_Start = 0; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestYarn_RecFilePathAndName, "[1]. Yarn - Run Pi Approximation Java Program  \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Yarn Run Jar  on Entry Node: " + tempENName);
			
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
						
						
						
			//(1) Move Yarn test jar file into Linux local folder for testing
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localYarnJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
			DayClock tempClock = new DayClock();				
			String tempTime = tempClock.getCurrentDateTime();	
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Yarn Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			} else {
				System.out.println("\n*** Failed - Moving Yarn Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
			}
						
			
			
			//(2) Testing Yarn Run Jar Using Pi Approximation Java Program
			String yarnTestResultFileName = "yarnTestResult_No" + (i+1) + ".txt";			
			String enLocalYarnTestResultPathAndName = enServerScriptFileDirectory + yarnTestResultFileName;
			
			// yarn jar /data/home/m041785/test/yarn/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar yarn.dcPiApproximation 100000 | tail -1 > /data/home/m041785/test/yarn/yarnTestResult1.text;
			// hadoop jar /data/home/m041785/test/yarn/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar yarn.dcPiApproximation 2000000 | tail -1 >> /data/home/m041785/test/yarn/yarnTestResult1.text;
			// /usr/bin/yarn jar /data/home/m041785/test/yarn/DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar yarn.dcPiApproximation 100000 | tail -1 > /data/home/m041785/test/yarn/yarnTestResult1.text;
			// /usr/bin/hadoop jar /data/home/m041785/test/yarn/DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar yarn.dcPiApproximation 200000 | tail -1 >> /data/home/m041785/test/yarn/yarnTestResult1.text;
		
			String enServerYarnJarFilePathAndName = enServerScriptFileDirectory + localYarnJarFileName;			
			String yarnRunPiApproximationJarCmd = "/usr/bin/yarn jar " + enServerYarnJarFilePathAndName +  " yarn.dcPiApproximation " + LocalPiApproximationTermNumber + " | tail -1 > " + enLocalYarnTestResultPathAndName;
			
			int newTermNumber = LocalPiApproximationTermNumber*2;
			String hadoopRunPiApproximationJarCmd = "/usr/bin/hadoop jar " + enServerYarnJarFilePathAndName +  " yarn.dcPiApproximation " + newTermNumber + " | tail -1 >> " + enLocalYarnTestResultPathAndName;
			
			
			String yarnTestResultHdfsPathAndName = yarnTestFolderName + yarnTestResultFileName;
			
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
			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + yarnTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + yarnTestFolderName + "; \n");		
			//sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + yarnTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 755 " + activeNN_addr_port + yarnTestFolderName + "; \n");
					    	    
		    
		    sb.append(yarnRunPiApproximationJarCmd + ";\n");
		    sb.append(hadoopRunPiApproximationJarCmd + ";\n");
		    
		    String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + enLocalYarnTestResultPathAndName + " " + activeNN_addr_port + yarnTestResultHdfsPathAndName;
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
			
		    sb.append("hadoop fs -chmod -R 550 " + yarnTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		   		    
		    String localYarnPiApproximationTestingScriptFilePathAndName = mapReduceScriptFilesFoder + "dcTestYarn_PiApproximationingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (localYarnPiApproximationTestingScriptFilePathAndName,  "Script File For Testing Yarn Pi Approximation on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String mapReducePiApproximationingTestingCmds = sb.toString();
			writeDataToAFile(localYarnPiApproximationTestingScriptFilePathAndName, mapReducePiApproximationingTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(localYarnPiApproximationTestingScriptFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localYarnPiApproximationTestingScriptFilePathAndName, 
						mapReduceScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(yarnTestResultHdfsPathAndName);			
			if (currHadoopFS.exists(filePath)) {
				System.out.println("\n***  Existing file : " + yarnTestResultHdfsPathAndName);
				hdfsFilePathAndNameList.add(yarnTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				boolean foundPiApproximationStr1 = false;
				boolean foundPiApproximationStr2 = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains("*** For termNum - 100000:  pi = 3.1416026536897204")) {
						//*** For termNum - 100000:  pi = 3.1416026536897204						
						foundPiApproximationStr1 = true;				
					}		
					if (line.contains("*** For termNum - 200000:  pi = 3.141597653614762")) {
						//*** For termNum - 200000:  pi = 3.141597653614762
						foundPiApproximationStr2 = true;						
					}								
				}//end while
				br.close();
				
				
				if (foundPiApproximationStr1 == true  || foundPiApproximationStr2==true){
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
						+ "\n  --(1) Internally Yarn Run Pi Approximation Java Program in a Jar Two Times "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Java Program-Generated Approximated Pi Values Saved in a HDFS File:  '" + yarnTestResultHdfsPathAndName + "'"
				        + "\n  --(3) Yarn Testing Total Time Used: " + timeUsed + "\n"; 	
			} else {
				testRecordInfo = "-*-*- 'Failed' - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Yarn Run Pi Approximation Java Program in a Jar Two Times "
						+ "\n         on '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
				        + "\n  --(2) Java Program-Generated Approximated Pi Values Saved in a HDFS File:  '" + yarnTestResultHdfsPathAndName + "'"
				        + "\n  --(3) Yarn Testing Total Time Used: " + timeUsed + "\n"; 	
			}
			writeDataToAFile(dcTestYarn_RecFilePathAndName, testRecordInfo, true);
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
		currNotingMsg += "\n***** Done - Testing Yarn-Running Pi Approximation Java Program in A Jar File on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Yarn Testing Generated Total Critical " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Yarn Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Yarn Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Yarn Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestYarn_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestYarn_RecFilePathAndName));
	
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
