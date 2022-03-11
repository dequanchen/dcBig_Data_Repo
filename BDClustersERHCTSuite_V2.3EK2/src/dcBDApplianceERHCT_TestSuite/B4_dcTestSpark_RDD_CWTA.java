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
* Date: 3/18/2016; 5/10/2017
*/ 


public class B4_dcTestSpark_RDD_CWTA {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localSparkTestDataFileName = "";
	private static String sparkTestFolderName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/sqoop/bin/sqoop (<=TDH2.1.11) ==> /usr/bin/sqoop or /usr/hdp/2.3.4.0-3485/sqoop/bin/sqoop (TDH2.3.4)
    // /usr/lib/spark/bin/spark-shell (<=TDH2.1.11) ==> /usr/bin/spark-shell or /usr/hdp/2.3.4.0-3485/spark/bin/spark (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 6+1 parameters for Spark-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localSparkTestDataFileName = args[4];
		sparkTestFolderName = args[5];
		internalKinitCmdStr = args[9];
		
		if (!sparkTestFolderName.endsWith("/")){
			sparkTestFolderName += "/";
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
		String sparkScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Spark\\";
		prepareFolder(sparkScriptFilesFoder, "Local Spark Testing Script Files");
		
		String dcTestSpark_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestSpark_RDDCTAS_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestSpark_RecFilePathAndName,  "Records of Testing Spark on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Spark RDD Creation, Transformation, Action and Results Saving Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Spark, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestSpark_RecFilePathAndName, testRecHeader, false);		
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
		sparkTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Spark/";//Modify pigTestFolderName from "/data/test/Pig/"
				
		
		String activeHdfsAddressAndPort = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println("\n--- activeHdfsAddressAndPort on '" + bdClusterName + "' Cluster: " + activeHdfsAddressAndPort);
		
		//String localWinSrcSparkTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localSparkTestDataFileName;
		//System.out.println("\n*** localWinSrcSparkTestDataFilePathAndName: " + localWinSrcSparkTestDataFilePathAndName);
				
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);
			
		DayClock tempClock = new DayClock();			
		String tempTime = tempClock.getCurrentDateTime();
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		//4. Loop through bdClusterEntryNodeList to internally create Spark RDD, perform RDD transformation 
		//    and action and Results saving using Scala Spark (Spark-shell)
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestSpark_RecFilePathAndName, "[1]. Spark-Shell (Scala) \n", true);
		
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //clusterENNumber..bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Spark-Shell on Entry Node: " + tempENName);
			
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
						
			//1. Move Test Data To the Entry Node Server or To the HDFS by external HDFS-writing
			//1.1 Alternative #1: Move test data file to HDFS by external HDFS-writing
			//String hdfsSparkTestDataFilePathAndName = "/data/test/Spark/dcSparkTestData_employee.txt"; 	
			////System.out.println("\n*** hdfsSparkTestDataFilePathAndName: " + hdfsSparkTestDataFilePathAndName);
			//
			//moveWindowsLocalSparkTestDataToHDFS (localWinSrcSparkTestDataFilePathAndName, hdfsSparkTestDataFilePathAndName, currHadoopFS);
						
			//tempClock = new DayClock();				
			//tempTime = tempClock.getCurrentDateTime();		
			//System.out.println("\n*** Done - Moving Spark Test Data into HDFS on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			
			//1.2 Alternative #2: Move Test Data To the Entry Node Server
			String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localSparkTestDataFileName;
			int exitVal = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localSparkTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//tempClock = new DayClock();				
			//tempTime = tempClock.getCurrentDateTime();
			
			if (exitVal == 0 ){
				System.out.println("\n*** Done - Moving Spark Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			} else {
				System.out.println("\n*** Failed - Moving Spark Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			}
			
			//2. Generate Spark-Shell Testing Commands .scala file and move it to Linux Server Node folder - enServerScriptFileDirectory
			//WebURL:https://spark.apache.org/docs/1.2.0/programming-guide.html			
			//       http://spark.apache.org/docs/latest/quick-start.html
			String rdd1_name = "empRDDFromLocalFile";
			String rdd2_name = "wordCountsRDD";
			String rddSavedToHdfsFolderPathAndName = sparkTestFolderName + "dcSpark-ShellTestResult_"+ (i+1);
			//String rddSavedToLinuxLocalFolderPathAndName = enServerScriptFileDirectory + "tempSpark_" + (i+1); 
			
			String rddCreationCmd = "val " + rdd1_name + " = sc.textFile(\"file://" + enServerTestDataFileFullPathAndName + "\")";			
			String rddTransformationCmd = "val " + rdd2_name + " = " + rdd1_name + ".flatMap(line => line.split(\",\")).map(word => (word, 1)).reduceByKey(_+_)";
			String rddRepartitionTrnDisplayActionCmd = rdd2_name + ".repartition(1).collect()";
			String rddCoalesceTrnAndSavingActionCmd = rdd2_name +".coalesce(1, true).saveAsTextFile(\"" + activeHdfsAddressAndPort + rddSavedToHdfsFolderPathAndName +  "\")";
			//String rddCoalesceTrnAndSavingActionCmd2 = rdd2_name +".saveAsTextFile(\"file://" + rddSavedToLinuxLocalFolderPathAndName + "\")";
			
			String LocalWindowsSparkShellCmdsFileName = "dcTestSparkShellCommands_" + (i+1) + ".scala";
			String LocalWindowsSparkShellCmdsFilePathAndName = sparkScriptFilesFoder + LocalWindowsSparkShellCmdsFileName;
			String enServerSparkShellCmdsFileName = enServerScriptFileDirectory + LocalWindowsSparkShellCmdsFileName;
			prepareFile (LocalWindowsSparkShellCmdsFilePathAndName,  "Spark-Shell Commands of Testing RDD on '" + bdClusterName + "' Cluster");
			
			//sb.append(sparkShellInitiateCmd + ";\n");	    
		    sb.append(rddCreationCmd + ";\n");		   
		    sb.append(rddTransformationCmd + ";\n");
		    sb.append(rddRepartitionTrnDisplayActionCmd + ";\n");	
		    sb.append(rddCoalesceTrnAndSavingActionCmd + ";\n");
		    //sb.append(rddCoalesceTrnAndSavingActionCmd2 + ";\n");
		    sb.append( "System.exit(0);\n");
		    		    
		    String sparkShellRDDCmds = sb.toString();
			writeDataToAFile(LocalWindowsSparkShellCmdsFilePathAndName, sparkShellRDDCmds, false);		
			sb.setLength(0);
		    
			//Desktop.getDesktop().open(new File(LocalWindowsSparkShellCmdsFilePathAndName));	
			LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(LocalWindowsSparkShellCmdsFileName, 
					sparkScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Copying Spark Shell Testing Commands .scala File to Entry Node - " + tempENName + " of '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			
			//3. Genrate script file to run the Spark shell commands in the .scala file in the en server folder = enServerScriptFileDirectory
			String runSparkShellCmdsScriptCmd = "/usr/bin/spark-shell -i " + enServerSparkShellCmdsFileName;
		    
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -mkdir -p " + sparkTestFolderName + "; \n");	
			////sb.append("hadoop fs -chown -R hdfs:hdfs " + sparkTestFolderName + "; \n");
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + sparkTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + sparkTestFolderName + "; \n");		    
		    sb.append("hadoop fs -rm -r -skipTrash " + rddSavedToHdfsFolderPathAndName + "; \n");
		    
		    //sb.append("kdestroy;\n");
		    ////sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("sudo su - root;\n");		    	    
		    //sb.append("cd " + enServerScriptFileDirectory + ";\n");
		    //sb.append("kdestroy;\n");
		    ////sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos	
		    
		    sb.append(runSparkShellCmdsScriptCmd + "; \n");
		    		    
		    ///sb.append("kdestroy;\n");		    
		    //sb.append("sudo su - hdfs;\n");		    		    
		    //sb.append("kdestroy;\n");
		    sb.append("hadoop fs -chmod -R 550 " + sparkTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		    String sparkShellTestScriptFullFilePathAndName = sparkScriptFilesFoder + "dcTestSparkShell_ScalaScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (sparkShellTestScriptFullFilePathAndName,  "Script File For Testing Spark Shell on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String sparkShellTestingCmds = sb.toString();
			writeDataToAFile(sparkShellTestScriptFullFilePathAndName, sparkShellTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(sparkShellTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(sparkShellTestScriptFullFilePathAndName, 
					sparkScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			String sparkTestResultHdfsFilePathAndName = rddSavedToHdfsFolderPathAndName + "/part-00000";
			System.out.println(" *** sparkTestResultHdfsFilePathAndName: " + sparkTestResultHdfsFilePathAndName);
			
			Path filePath = new Path(sparkTestResultHdfsFilePathAndName);
			if (currHadoopFS.exists(filePath)) {
				System.out.println("\n***  Existing file : " + sparkTestResultHdfsFilePathAndName);
				//hdfsFilePathAndNameList.add(sparkTestResultHdfsFilePathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				boolean foundWordCountStr1 = false;
				boolean foundWordCountStr2 = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains("(M,4)")) {
						foundWordCountStr1 = true;				
					}		
					if (line.contains("Texas,2")) {
						foundWordCountStr2 = true;						
					}								
				}//end while
				br.close();
				
				
				if (foundWordCountStr1 == true  && foundWordCountStr2==true){
					currTestScenarioSuccessStatus = true;
					hdfsFilePathAndNameList.add(sparkTestResultHdfsFilePathAndName);
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
						+ "\n  --(1) Internal Spark RDD Creation (Local File), Transformation, Action and Results Saving (HDFS File)"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed				        	 
				        + "\n  --(2) Spark-Shell Final RDD Saved To HDFS file - '" + sparkTestResultHdfsFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"				
						+ "\n  --(1) Internal Spark RDD Creation (Local File), Transformation, Action and Results Saving (HDFS File)"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed				        	 
				        + "\n  --(2) Spark-Shell Final RDD Target To Save HDFS file - '" + sparkTestResultHdfsFilePathAndName + "'\n";}
			writeDataToAFile(dcTestSpark_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for 4
		
		
		//5. Loop through bdClusterEntryNodeList to internally create Spark RDD, perform RDD transformation 
		//    and action and Results saving using Python Spark (PySpark)
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestSpark_RecFilePathAndName, "\n[2].  PySpark Shell (Python) \n", true);
		
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //clusterENNumber..bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing PySpark Shell on Entry Node: " + tempENName);
			
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
						
			//1. Move Test Data To the Entry Node Server or To the HDFS by external HDFS-writing
			//1.1 Alternative #1: Move test data file to HDFS by external HDFS-writing
			//String hdfsSparkTestDataFilePathAndName = "/data/test/Spark/dcSparkTestData_employee.txt"; 	
			////System.out.println("\n*** hdfsSparkTestDataFilePathAndName: " + hdfsSparkTestDataFilePathAndName);
			//
			//moveWindowsLocalSparkTestDataToHDFS (localWinSrcSparkTestDataFilePathAndName, hdfsSparkTestDataFilePathAndName, currHadoopFS);
						
			//tempClock = new DayClock();				
			//tempTime = tempClock.getCurrentDateTime();		
			//System.out.println("\n*** Done - Moving Spark Test Data into HDFS on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			
			//1.2 Alternative #2: Move Test Data To the Entry Node Server
			String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localSparkTestDataFileName;
			int exitVal = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localSparkTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			//tempClock = new DayClock();				
			//tempTime = tempClock.getCurrentDateTime();
			
			if (exitVal == 0 ){
				System.out.println("\n*** Done - Moving Spark Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			} else {
				System.out.println("\n*** Failed - Moving Spark Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			}
			
			//2. Generate PySpark Shell Testing Commands .py file and move it to Linux Server Node folder - enServerScriptFileDirectory
			//WebURL:https://spark.apache.org/examples.html
			//	https://spark.apache.org/docs/latest/programming-guide.html#linking-with-spark
			//	https://spark.apache.org/docs/0.9.1/python-programming-guide.html
			//	http://www.physics.nyu.edu/pine/pymanual/html/chap3/chap3_arrays.html
			//	http://www.mccarroll.net/blog/pyspark/index.html
			String rdd1_name = "empRDDFromLocalFile";
			String rdd2_name = "wordCountsRDD";
			String rddSavedToHdfsFolderPathAndName = sparkTestFolderName + "dcPySparkShellTestResult_"+ (i+1);
			//String rddSavedToLinuxLocalFolderPathAndName = enServerScriptFileDirectory + "tempPySpark_" + (i+1); 
			
			String rddCreationCmd = rdd1_name + " = sc.textFile(\"file://" + enServerTestDataFileFullPathAndName + "\")";			
			String rddTransformationCmd = rdd2_name + " = " + rdd1_name + ".flatMap(lambda line: line.split(\",\")).map(lambda word: (word, 1)).reduceByKey(lambda x,y:x+y).sortByKey(True)";
			String rddRepartitionTrnDisplayActionCmd = rdd2_name + ".repartition(1).collect()";
			String rddCoalesceTrnAndSavingActionCmd = rdd2_name +".repartition(1).saveAsTextFile(\"" + activeHdfsAddressAndPort + rddSavedToHdfsFolderPathAndName +  "\")";
			//String rddCoalesceTrnAndSavingActionCmd2 = rdd2_name +".saveAsTextFile(\"file://" + rddSavedToLinuxLocalFolderPathAndName + "\")";
			
			String LocalWindowsPySparkShellCmdsFileName = "dcTestPySparkShellCommands_" + (i+1) + ".py";
			String LocalWindowsPySparkShellCmdsFilePathAndName = sparkScriptFilesFoder + LocalWindowsPySparkShellCmdsFileName;
			String enServerPySparkShellCmdsFileName = enServerScriptFileDirectory + LocalWindowsPySparkShellCmdsFileName;
			prepareFile (LocalWindowsPySparkShellCmdsFilePathAndName,  "PySpark Shell Commands of Testing RDD on '" + bdClusterName + "' Cluster");
			
			sb.append("from pyspark import SparkContext;\n");
			sb.append("sc =SparkContext();\n");			
		    sb.append(rddCreationCmd + ";\n");		   
		    sb.append(rddTransformationCmd + ";\n");
		    sb.append(rddRepartitionTrnDisplayActionCmd + ";\n");	
		    sb.append(rddCoalesceTrnAndSavingActionCmd + ";\n");
		    //sb.append(rddCoalesceTrnAndSavingActionCmd2 + ";\n");
		    //sb.append( "System.exit(0);\n");
		    sb.append("quit();\n");			    
		    		    
		    String pySparkShellRDDCmds = sb.toString();
			writeDataToAFile(LocalWindowsPySparkShellCmdsFilePathAndName, pySparkShellRDDCmds, false);		
			sb.setLength(0);
		    
			//Desktop.getDesktop().open(new File(LocalWindowsSparkShellCmdsFilePathAndName));	
			LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(LocalWindowsPySparkShellCmdsFileName, 
					sparkScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Copying PySpark Shell Testing Commands .py File to Entry Node - " + tempENName + " of '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			
			//3. Genrate script file to run the Spark shell commands in the .scala file in the en server folder = enServerScriptFileDirectory
			String runPySparkShellCmdsScriptCmd = "/usr/bin/pyspark " + enServerPySparkShellCmdsFileName;
		    
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			sb.append("hadoop fs -mkdir -p " + sparkTestFolderName + "; \n");	
			////sb.append("hadoop fs -chown -R hdfs:hdfs " + sparkTestFolderName + "; \n");
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + sparkTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + sparkTestFolderName + "; \n");		    
		    sb.append("hadoop fs -rm -r -skipTrash " + rddSavedToHdfsFolderPathAndName + "; \n");
		    
		    //sb.append("kdestroy;\n");
		    ////sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("sudo su - root;\n");		    	    
		    //sb.append("cd " + enServerScriptFileDirectory + ";\n");
		    //sb.append("kdestroy;\n");
		    ////sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos	
		    
		    sb.append(runPySparkShellCmdsScriptCmd + "; \n");
		    		    
		    ///sb.append("kdestroy;\n");		    
		    //sb.append("sudo su - hdfs;\n");		    		    
		    //sb.append("kdestroy;\n");
		    sb.append("hadoop fs -chmod -R 550 " + sparkTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		    String pySparkShellTestScriptFullFilePathAndName = sparkScriptFilesFoder + "dcTestPySparkShell_PythonScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (pySparkShellTestScriptFullFilePathAndName,  "Script File For Testing PySpark Shell on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String pySparkShellTestingCmds = sb.toString();
			writeDataToAFile(pySparkShellTestScriptFullFilePathAndName, pySparkShellTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(sparkShellTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(pySparkShellTestScriptFullFilePathAndName, 
					sparkScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			String sparkTestResultHdfsFilePathAndName = rddSavedToHdfsFolderPathAndName + "/part-00000";
			System.out.println(" *** sparkTestResultHdfsFilePathAndName: " + sparkTestResultHdfsFilePathAndName);
			
			Path filePath = new Path(sparkTestResultHdfsFilePathAndName);
			if (currHadoopFS.exists(filePath)) {
				System.out.println("\n***  Existing file : " + sparkTestResultHdfsFilePathAndName);
				//hdfsFilePathAndNameList.add(sparkTestResultHdfsFilePathAndName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				boolean foundWordCountStr1 = false;
				boolean foundWordCountStr2 = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains("(u'M', 4)")) {
						foundWordCountStr1 = true;				
					}		
					if (line.contains("u'Texas', 2")) {
						foundWordCountStr2 = true;						
					}								
				}//end while
				br.close();
				
				
				if (foundWordCountStr1 == true  && foundWordCountStr2==true){
					currTestScenarioSuccessStatus = true;
					hdfsFilePathAndNameList.add(sparkTestResultHdfsFilePathAndName);
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
						+ "\n  --(1) Internal PySpark RDD Creation (Local File), Transformation, Action and Results Saving (HDFS File)"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed				        	 
				        + "\n  --(2) Spark-Shell Final RDD Saved To HDFS file - '" + sparkTestResultHdfsFilePathAndName + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"				
						+ "\n  --(1) Internal PySpark RDD Creation (Local File), Transformation, Action and Results Saving (HDFS File)"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed				        	 
				        + "\n  --(2) Spark-Shell Final RDD Target To Save HDFS file - '" + sparkTestResultHdfsFilePathAndName + "'\n";}
			writeDataToAFile(dcTestSpark_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for 5
		
		writeDataToAFile(dcTestSpark_RecFilePathAndName, "\n*** More Test Scenarios Will Be Developed In The Future ***", true);	
		
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100;
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally and Externally Spark RDD Creation, Transformation, Action and Results Saving "
					  +  "\n                   on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Spark Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Spark Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Spark Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Spark Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestSpark_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestSpark_RecFilePathAndName));
	
	}//end run()
	
		
	@SuppressWarnings("unused")
	private static void moveWindowsLocalSparkTestDataToHDFS (String localWinSrcSparkTestDataFilePathAndName, 
							String hdfsSparkTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcSparkTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localSparkTestDataFileName;
		//System.out.println("\n*** localWinSrcSparkTestDataFilePathAndName: " + localWinSrcSparkTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/Spark/dcSparkTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsSparkTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing Spark file: " + hdfsSparkTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsSparkTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
			
			
			FileReader aFileReader = new FileReader(localWinSrcSparkTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localSparkTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {
			System.out.println("*** Error occurs in moveWindowsLocalSparkTestDataToHDFS(): ");
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalSparkTestDataToHDFS

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
