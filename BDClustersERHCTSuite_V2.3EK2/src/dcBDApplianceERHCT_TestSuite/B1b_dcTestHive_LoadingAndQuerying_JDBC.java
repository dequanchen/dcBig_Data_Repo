package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.DayClock;
import dcModelClasses.HiveConnectionFactory;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/14-17, 19, 24-25/2014; 
*       1/15/2016 (Kerberos); 3/2, 3, 8/2016; 3/14/2016; 3/25/2016
*/ 


public class B1b_dcTestHive_LoadingAndQuerying_JDBC {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localHiveTestDataFileName = "";
	private static String hiveTestFolderName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String hdfsHiveDefaultTestDataFilePathAndName = "";
	private static int tableRowCount = 0;  
	private static String currScenarioDetailedTestingRecordInfo = "";		
	private static double currScenarioSuccessRate = 0L;
	
	// /usr/lib/sqoop/bin/sqoop (<=TDH2.1.11) ==> /usr/bin/sqoop or /usr/hdp/2.3.4.0-3485/sqoop/bin/sqoop (TDH2.3.4)
    // /usr/lib/hive/bin/hive (<=TDH2.1.11) ==> /usr/bin/hive or /usr/hdp/2.3.4.0-3485/hive/bin/hive (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for Hive-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localHiveTestDataFileName = args[4];
		hiveTestFolderName = args[5];
		internalKinitCmdStr = args[9];
		
		if (!hiveTestFolderName.endsWith("/")){
			hiveTestFolderName += "/";
		}
		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
							
		run();
	}//end main
	
	@SuppressWarnings({ "deprecation", "unused" })
	public static void run() throws Exception {
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String hiveScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "hive\\";
		prepareFolder(hiveScriptFilesFoder, "Local Hive Testing Script Files");
		
		String dcTestHive_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHive_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestHive_RecFilePathAndName,  "Records of Testing Hive on '" + bdClusterName + "' Cluster");
		
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Hive Internal and External (JDBC) Table Dropping, Creating, Loading and Querying Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestHive_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		hiveTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Hive/";
						
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
		NumberFormat df = new DecimalFormat("#0.00"); 
		String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
		//System.out.println("\n*** localWinSrcHiveTestDataFilePathAndName: " + localWinSrcHiveTestDataFilePathAndName);
		String hdfsHiveTestDataFilePathAndName = hiveTestFolderName +  "dcHiveTestData_employee.txt"; //changed from folder "/data/test/Hive/"...	
		//System.out.println("\n*** hdfsHiveTestDataFilePathAndName: " + hdfsHiveTestDataFilePathAndName);
	
		
		//The following 5 line are requeired for #6 and #7
		final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
		hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localHiveTestDataFileName; 
		final HiveConnectionFactory aHiveConnFactory = new HiveConnectionFactory (bdClusterName, 2, "default", "hive", ""); //..ambari-qa...hive		
		final String tableName = "employee_e1";	
		String testRecordInfo = "";
	  			  		
  		//6. Externally drop, creating, loading and querying (counting rows) a hive-managed table by Hive JDBC		
  		//6(1)Externally moving data file for loading into HDFS Hive-default folder
  		//String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
  		//System.out.println("\n*** localWinSrcHiveTestDataFilePathAndName: " + localWinSrcHiveTestDataFilePathAndName);
  		//String hiveDefaultFolderPath = "/apps/hive/warehouse/";
  		//String hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localHiveTestDataFileName;
  		//System.out.println("\n*** hdfsHiveTestDataFilePathAndName: " + hdfsHiveTestDataFilePathAndName);
  				
  		System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
  		hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
  		
  		Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
  		if (currHadoopFS.exists(outputPath)) {
  			currHadoopFS.delete(outputPath, false);
  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
          }
  		
  		moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);		
  						
  		tempClock = new DayClock();				
  		tempTime = tempClock.getCurrentDateTime();		
  		System.out.println("\n*** Done - Generated Hive-Managed Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
  						+ hdfsHiveDefaultTestDataFilePathAndName + " at the time - " + tempTime); 	

  		//6(2) Testing Hive JDBC
  		totalTestScenarioNumber++;	  		  	   
  	  
  	    Thread thread = new Thread(new Runnable() {
		    public void run() {
		    	int totalTestCaseNumber = 0;
			  	double successTestCaseNum = 0L;
			  			  	
			  	StringBuilder sb = new StringBuilder();
		    	
			  	try {		    		
		  	  		Connection conn = aHiveConnFactory.getConnection();     	
		  	  		Statement stmt = conn.createStatement();
		  	  					
		  			//1).drop an existing Hive table
		  			totalTestCaseNumber++;
		  			String dropTblSqlStr = "drop table " + tableName;
		  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
		  			if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Dropping Hive table - " + tableName + "\n");			
		  			} else {
		  				sb.append("    -*- 'Failed' - Dropping Hive table - " + tableName + "\n");
		  			}
		  			
		  			//2). create a new Hive-managed table
		  			totalTestCaseNumber++;
		  		    String createTblSqlStr = "create table " + tableName 
		  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
		  		      		+ "Row format delimited fields terminated by ',' ";
		  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
		  		    if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Creating Hive table - " + tableName + "\n");	
		  		    } else {
		  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + tableName + "\n");
		  			}   
		  		    
		  		    //3). load the Hive-managed table by overwriting  
		  		    totalTestCaseNumber++;
		  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + tableName ;
		  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
		  		  	if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Loading Data Into Hive table - " + tableName + "\n");
		  		  	} else {
		  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + tableName + "\n");
		  			}    
		  	      		    	  	
		  		    //4).HQuery (row-counting) the above-generated Hive-managed table
		  	  		totalTestCaseNumber++;
		  		    String queryTblSqlStr = "select count(1) from " + tableName;  //count(*)...count(1)...*
		  		    boolean countingQueryStatus = false;
		  	  		ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
		  	  		String line = "";
		  	  		if (rs != null){
		  	  			while (rs.next()) {
		  	  	  			line = rs.getString(1);
		  	  				System.out.println(line);
		  	  				if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
		  	  					successTestCaseNum ++;
		  	  					countingQueryStatus = true;
		  	  				    tableRowCount = 6;
		  	  					break;
		  	  				}
		  	  	  		}
		  	  			rs.close();
		  	  		}  		  		
		  	  		if (countingQueryStatus == true){
		  	  			sb.append("    *** Success - Querying/Counting Hive table - " + tableName + "\n");
		  	  		} else {
		  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + tableName + "\n");
		  	  		}
		  	  		stmt.close();
		  	  	    conn.close();
		  	  	    
		  	  	    	
		  	  		sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
		  	  		sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
		  	  		currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
		  			
		  	  	    NumberFormat df = new DecimalFormat("#0.00");
		  			sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
		  	  		
		  			currScenarioDetailedTestingRecordInfo = sb.toString();		  			
		  			sb.setLength(0);
		  	  	} catch (SQLException e) {		
		  			e.printStackTrace();
		  		} catch (ClassNotFoundException e) {		
		  			e.printStackTrace();
		  		}//end try
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
  	  	
  	  	tempClock = new DayClock();				
  		tempTime = tempClock.getCurrentDateTime();	
  		
  		testRecordInfo = "";	
  		if (currScenarioSuccessRate == 1){
  			successTestScenarioNum++;
  			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '"	+ bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + tableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
  		} else if (currScenarioSuccessRate == 0){
  			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + tableName + "'\n";	 	 
  		} else {
  			successTestScenarioNum += currScenarioSuccessRate;
  			testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
  		}
  		sb.setLength(0);
  		writeDataToAFile(dcTestHive_RecFilePathAndName, testRecordInfo, true);
  		
  		
  		//7. Externally drop, creating, loading (internally-written HDFS file data) and querying (counting rows) a hive-managed table by Hive JDBC		
  		//7(1) Internally Moving data file for loading into HDFS Hive-default folder
  		//String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
  		//System.out.println("\n*** localWinSrcHiveTestDataFilePathAndName: " + localWinSrcHiveTestDataFilePathAndName);
  		
  		//String hiveDefaultFolderPath = "/apps/hive/warehouse/";
  		//String hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + "dcHiveTestData_employee.txt";  		
  		//System.out.println("\n*** hdfsHiveTestDataFilePathAndName: " + hdfsHiveTestDataFilePathAndName);
  		
  		//7(1)a. Preparation
  		String tempENName = "EN01";
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
		
		//7(1)b. Deleting HDFS default test data file 		
  		hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + "dcHHPTestData_employee2.txt";  			
  		System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
  				
  		Path defaultHiveTestDataFilePath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
  		if (currHadoopFS.exists(defaultHiveTestDataFilePath)) {
  			currHadoopFS.delete(defaultHiveTestDataFilePath, false);
  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
        }
  		
  	    //7(1)c. Moving Test Data file from Windows Local To EN Server Local 
		LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localHiveTestDataFileName, 
				bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
  		  		
  	    //7(1)d. Moving Test Data file from EN Server Local To HDFS hiveDefaultFolderPath
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		String enLinuxLocaHiveDefaultTestDataFilePathAndName =  enServerScriptFileDirectory + localHiveTestDataFileName;
		
		sb.append("sudo su - hdfs;\n");
		//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
		//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
		sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
		sb.append("hadoop fs -copyFromLocal " + enLinuxLocaHiveDefaultTestDataFilePathAndName + " " + hdfsHiveDefaultTestDataFilePathAndName + "; \n");
	    		   
		String enLinuxLocaHiveDefaultTestDataFileMovingScriptFilePathAndName = hiveScriptFilesFoder + "dcDefaultHiveTestDataFileMovingToHdfsScript.sh";			
		prepareFile (enLinuxLocaHiveDefaultTestDataFileMovingScriptFilePathAndName,  "Script File For Default Hive Test Data Moving to HDFS on '" + bdClusterName + "' Cluster Entry Node - EN01");
		
		String internalDefaultHiveTestDataFileMovingCmds = sb.toString();
		writeDataToAFile(enLinuxLocaHiveDefaultTestDataFileMovingScriptFilePathAndName, internalDefaultHiveTestDataFileMovingCmds, false);		
		sb.setLength(0);
	
		LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(enLinuxLocaHiveDefaultTestDataFileMovingScriptFilePathAndName, 
				hiveScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
		
		tempClock = new DayClock();				
  		tempTime = tempClock.getCurrentDateTime();		
  		System.out.println("\n*** Done - Generated Hive-Managed Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
  						+ hdfsHiveDefaultTestDataFilePathAndName + " at the time - " + tempTime); 	

  		//7(2) Testing Hive JDBC
  		totalTestScenarioNumber++;	  		  	   
	  	  
  	    Thread thread2 = new Thread(new Runnable() {
		    public void run() {
		    	int totalTestCaseNumber = 0;
			  	double successTestCaseNum = 0L;
			  			  	
			  	StringBuilder sb = new StringBuilder();
		    	
			  	try {		    		
		  	  		Connection conn = aHiveConnFactory.getConnection();     	
		  	  		Statement stmt = conn.createStatement();
		  	  					
		  			//1).drop an existing Hive table
		  			totalTestCaseNumber++;
		  			String dropTblSqlStr = "drop table " + tableName;
		  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
		  			if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Dropping Hive table - " + tableName + "\n");			
		  			} else {
		  				sb.append("    -*- 'Failed' - Dropping Hive table - " + tableName + "\n");
		  			}
		  			
		  			//2). create a new Hive-managed table
		  			totalTestCaseNumber++;
		  		    String createTblSqlStr = "create table " + tableName 
		  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
		  		      		+ "Row format delimited fields terminated by ',' ";
		  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
		  		    if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Creating Hive table - " + tableName + "\n");	
		  		    } else {
		  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + tableName + "\n");
		  			}   
		  		    
		  		    //3). load the Hive-managed table by overwriting  
		  		    totalTestCaseNumber++;
		  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + tableName ;
		  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
		  		  	if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Loading Data Into Hive table - " + tableName + "\n");
		  		  	} else {
		  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + tableName + "\n");
		  			}    
		  	      		    	  	
		  		    //4).HQuery (row-counting) the above-generated Hive-managed table
		  	  		totalTestCaseNumber++;
		  		    String queryTblSqlStr = "select count(1) from " + tableName;  //count(*)...count(1)...*
		  		    boolean countingQueryStatus = false;
		  	  		ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
		  	  		String line = "";
		  	  		if (rs != null){
		  	  			while (rs.next()) {
		  	  	  			line = rs.getString(1);
		  	  				System.out.println(line);
		  	  				if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
		  	  					successTestCaseNum ++;
		  	  					countingQueryStatus = true;
		  	  				    tableRowCount = 6;
		  	  					break;
		  	  				}
		  	  	  		}
		  	  			rs.close();
		  	  		}  		  		
		  	  		if (countingQueryStatus == true){
		  	  			sb.append("    *** Success - Querying/Counting Hive table - " + tableName + "\n");
		  	  		} else {
		  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + tableName + "\n");
		  	  		}
		  	  		stmt.close();
		  	  	    conn.close();
		  	  	    
		  	  	    	
		  	  		sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
		  	  		sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
		  	  		currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
		  			
		  	  	    NumberFormat df = new DecimalFormat("#0.00");
		  			sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
		  	  		
		  			currScenarioDetailedTestingRecordInfo = sb.toString();		  			
		  			sb.setLength(0);
		  	  	} catch (SQLException e) {		
		  			e.printStackTrace();
		  		} catch (ClassNotFoundException e) {		
		  			e.printStackTrace();
		  		}//end try
		    }
		});
				
  	    thread2.start();
		try {
			thread2.join(20000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		if (thread2.isAlive()) {
			thread2.stop();//.stop();
		}
		Thread.sleep(1*5*1000);			
  		 	  	
  	  	tempClock = new DayClock();				
  		tempTime = tempClock.getCurrentDateTime();	
  		
  		testRecordInfo = "";	
  		if (currScenarioSuccessRate == 1){
  			successTestScenarioNum++;
  			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (internally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + tableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
  		} else if (currScenarioSuccessRate == 0){
  			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (internally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Generated Hive-Managed Table: '" + hiveDefaultFolderPath + tableName + "'\n";	 	 
  		} else {
  			successTestScenarioNum += currScenarioSuccessRate;
  			testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (internally-written HDFS file data), and Querying a Hive-Managed Table "
  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
  		}
  		sb.setLength(0);
  		writeDataToAFile(dcTestHive_RecFilePathAndName, testRecordInfo, true);
		
		
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100;		
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally (Hive Shell & Sqoop) and Externally (JDBC) Dropping, Creating, Loading, and Querying (Counting)"
					  +  "\n                   Hive-Managed or Hive-External Table(s) on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present Hive Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Hive Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Hive Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Hive Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestHive_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestHive_RecFilePathAndName));
	
	}//end run()
	
	private static int runAHiveQuery_NoResultSet(Statement stmt,
			String hiveQueryStr) {
		int exitValue = 1000;
		try {
			System.out.println("\n*** Running: " + hiveQueryStr + ": ");
			stmt.execute(hiveQueryStr);
			exitValue = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} // end try
		return exitValue;
	}// end runAHiveQuery_NoResultSet

	private static ResultSet runAHiveQuery_YesResultSet(Statement stmt,
			String hiveQueryStr) {
		ResultSet rs = null;
		try {
			System.out.println("\n*** Running: " + hiveQueryStr + ": ");
			rs = stmt.executeQuery(hiveQueryStr);
		} catch (SQLException e) {
			// e.printStackTrace();
			// Thread.interrupted();
			return null;
		}
		return rs;
	}// end runAHiveQuery
	
	@SuppressWarnings("unused")
	private static String generateECGTestDataSqoopImportToHiveFullCmd (String sqlServerDBName, 
			String importTableName, String hiveTableName) {
		//1) Obtain sqoop command part #1 //EIMSQLPROD\\EDWSQLPROD ...ROPEIM802Q\\EDWPROJDEV
	    String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect \"jdbc:sqlserver://ROPEIM802Q.mayo.edu\\\\PROJDEV;database="  //EDWPROJDEV:54024
	    		+ sqlServerDBName + "\" --username TU05303 --password vbfgrt45";
	    //System.out.println("\n--*1-- sqoopCmdLeft_5Q is : \n" + sqoopCmdLeft_5Q);
	   
	    //2) Obtain sqoop command part #2
	    String sqoopCmdMiddle_5Q = "--table " + importTableName + "";
	    //System.out.println("\n--*2-- sqoopCmdMiddle_5Q is : \n" + sqoopCmdMiddle_5Q);
	    
	    //" --input-fields-terminated-by , --escaped-by \\ --input-enclosed-by '\"'";
	    
	    //3) Obtain sqoop command part #3	  
	    String sqoopCmdRight_5Q = "--m 1 --hive-import --hive-overwrite --hive-table " 
	    		+ hiveTableName + " --fields-terminated-by ','"; //--m 1..--split-by hl7MsgId 
	    //System.out.println("\n--*3-- sqoopCmdRight_5Q is : \n" + sqoopCmdRight_5Q);  	       
	   
	    //4) Obtain sqoop full command in single line == parts #1-4 separated by " "
	    String sqoopFullCmd_5Q = sqoopCmdLeft_5Q + " " + sqoopCmdMiddle_5Q + " " + sqoopCmdRight_5Q; 
	    System.out.println("\n--*-- sqoopFullCmd_5Q is : \n" + sqoopFullCmd_5Q);
	    
	    return sqoopFullCmd_5Q;
	}//end generateECGTestDataSqoopImportToHiveFullCmd
	
	private static void moveWindowsLocalHiveTestDataToHDFS (String localWinSrcHiveTestDataFilePathAndName, 
							String hdfsHiveTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
		//System.out.println("\n*** localWinSrcHiveTestDataFilePathAndName: " + localWinSrcHiveTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/Hive/dcHiveTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsHiveTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing Hive file: " + hdfsHiveTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsHiveTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));			
			
			FileReader aFileReader = new FileReader(localWinSrcHiveTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localHiveTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {
			System.out.println("*** Error occurs in moveWindowsLocalHiveTestDataToHDFS(): ");
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalHiveTestDataToHDFS

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
