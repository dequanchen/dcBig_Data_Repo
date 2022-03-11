package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import dcModelClasses.Base64Str;
import dcModelClasses.DayClock;
import dcModelClasses.HiveViaKnoxConnectionFactory;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/11/2016
*/ 

public class D1o_v1_dcTestKnox_Knox_Hive_JDBC {
	private static int testingTimesSeqNo = 7;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String knoxTestFolderName = "";
	private static String localKnoxTestDataFileName = "";
	private static String localKnoxTestAppendingDataFileName = "";
	private static String localKnoxTrustStorePathAndName = "";
	private static String localKnoxTrustStorePassWd = "";
	private static String internalKinitCmdStr = "";	
	private static String localKnoxLargeWebHbaseJsonDataFileName = "";		
	private static String localKnoxLargeWebHbaseXmlDataFileName = "";	
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String hdfsHiveDefaultTestDataFilePathAndName = "";
	private static int tableRowCount = 0;  
	private static String currScenarioDetailedTestingRecordInfo = "";
	private static double currScenarioSuccessRate = 0L;
			
	private static String enServerScriptFileDirectory = "/home/hdfs";
	
	public static void main(String[] args) throws Exception {
		//System.out.println("\n*** args.length is " + args.length);
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for Knox-ERHCT have not been specified yet!");
			return;
		}		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		knoxTestFolderName = args[4];
		localKnoxTestDataFileName = args[5];
		localKnoxTestAppendingDataFileName = args[6];
		localKnoxTrustStorePathAndName  = args[7];
		localKnoxTrustStorePassWd  = args[8];	
		
		internalKinitCmdStr = args[9];
		localKnoxLargeWebHbaseJsonDataFileName = args[10];		
		localKnoxLargeWebHbaseXmlDataFileName = args[11];
		
		if (!bdClusterUATestResultsParentFolder.endsWith("\\")){
			bdClusterUATestResultsParentFolder += "\\";
		}
		if (!knoxTestFolderName.endsWith("/")){
			knoxTestFolderName += "/";
		}		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
					
		run();
	}//end main
	
	@SuppressWarnings("deprecation")
	public static void run() throws Exception {
		//1. Preparation:
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String scriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Knox\\";
	    prepareFolder(scriptFilesFoder, "Local Knox Testing Script Files");
	    
		String dcTestKnox_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKnox_WritingAndReading_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKnox_RecFilePathAndName,  "Records of Testing Knox on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Knox-Gateway WebHDFS, WebHcat, WebHBase, Hive JDBC ... Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		
		//3.Get cluster FileSystem and other information for testing	 		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		
		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
					
		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();
		//String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNode2Name();	
		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
		String bdClusterKnoxIdName = currBdCluster.getBdClusterIdName();
		String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		String currClusterKnoxFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currClusterKnoxFQDN: " + currClusterKnoxFQDN);
		
		String loginUserName = bdENCmdFactory.getUsername();
		//String loginUserADPassWd = bdENCmdFactory.getPassword();
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos			
		String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();		
		System.out.println(" *** Login User Name / AD Password: " + loginUserName + " / " + loginUserADPassWd);
		
		String knoxTestFolderName = "/user/" + loginUserName + "/test/Knox/";
		
		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
		String rootUserName = bdENRootCmdFactory.getUsername();
		//String rootPw = bdENRootCmdFactory.getPassword();			
		//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
		//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
		
		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
		}			
		System.out.println("*** loginUserName is: " + loginUserName);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		NumberFormat df = new DecimalFormat("#0.00");
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
	
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);				
		//String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		//System.out.println("\n*** loginUserADPassWd is: " + loginUserADPassWd);
				
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "A. Testing Knox Node - " + currClusterKnoxNodeName+ "\n", true);
		
		
		//4. Move Test Data and Appending Test Data To Knox Server Node
		//String localWinTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
		//String localAppendingTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
		String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;
		String enServerAppendingTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestAppendingDataFileName;
				
		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestAppendingDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseJsonDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseXmlDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		tempClock = new DayClock();				
		tempTime = tempClock.getCurrentDateTime();
		
		if (exitVal1 == 0 ){
			System.out.println("\n*** Done - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal2 == 0 ){
			System.out.println("\n*** Done - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		if (exitVal3 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		if (exitVal4 == 0 ){
			System.out.println("\n*** Done - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		
		//#5...#8 Deleted
		
		
		//#9 Test scenario type for Hive JDBC Via Knox testing
		//Perform Secure Hive JDBC Operation via remote Knox-Hive JDBC Operations 
		//  due to the disabling of regular hive JDBC operations  		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[5]. Hive JDBC via Knox \n", true);
		
		String currKnoxNodeFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
		
		int secureHiveJDBCViaKnoxServiceNumber = 2; //1..2
		int secureHiveJDBCViaKnoxService_Start = 0; //0..1	
		
		//String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		if (!currClusterKnoxNode2Name.isEmpty()){
			secureHiveJDBCViaKnoxServiceNumber = 1;
		}
				
		for (int i = secureHiveJDBCViaKnoxService_Start; i < secureHiveJDBCViaKnoxServiceNumber; i++){
			totalTestScenarioNumber++;
			//(1) Preparation
			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
			final String tableName = "employee_e1";				
			
			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
	  		hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
	  		
	  		Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
	  		if (currHadoopFS.exists(outputPath)) {
	  			currHadoopFS.delete(outputPath, false);
	  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
	          }
	  		
	  		String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
	  		
	  		moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);		
	  						
	  		tempClock = new DayClock();				
	  		tempTime = tempClock.getCurrentDateTime();		
	  		System.out.println("\n*** Done - Generated Hive-Managed Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
	  						+ hdfsHiveDefaultTestDataFilePathAndName + " at the time - " + tempTime); 	

			//(2) Get active WebHbase HTTPS URL:
			tempClock = new DayClock();				
			String tempTime6_start = tempClock.getCurrentDateTime();
			
			if (i==1){				
				bdClusterKnoxIdName += "_bkp";
			}
			
			int currBdClusterGatewayPortNum = 8442;
			String hiveDBName = "default";

			final HiveViaKnoxConnectionFactory aHiveViaKnoxConnFactory = 
					new HiveViaKnoxConnectionFactory(currKnoxNodeFQDN, currBdClusterGatewayPortNum, 
							hiveDBName, localKnoxTrustStorePathAndName, localKnoxTrustStorePassWd,
							bdClusterKnoxIdName, loginUserName, loginUserADPassWd);
			
			String knoxHiveContextPath = "gateway/" + bdClusterKnoxIdName + "/hive";
			
			//(3) Hive JDBC Via Knox Testing
			Thread thread = new Thread(new Runnable() {
			    public void run() {
			    	int totalTestCaseNumber = 0;
				  	double successTestCaseNum = 0L;
				  			  	
				  	StringBuilder sb = new StringBuilder();
			    	
				  	try {		    		
			  	  		Connection conn = aHiveViaKnoxConnFactory.getConnection();     	
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
			String tempTime6_end = tempClock.getCurrentDateTime();		
			String time6Used = DayClock.calculateTimeUsed(tempTime6_start, tempTime6_end); 
			
			//System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
			//System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
			
	  		String testRecordInfo6 = "";	  		
	  		if (currScenarioSuccessRate == 1){
	  			successTestScenarioNum++;
	  			testRecordInfo6 = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + tempTime6_end + " and Time Used: " + time6Used
	  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + tableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
	  		} else if (currScenarioSuccessRate == 0){
	  			testRecordInfo6 = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + tempTime6_end + " and Time Used: " + time6Used
	  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + tableName + "'\n";	 	 
	  		} else {
	  			successTestScenarioNum += currScenarioSuccessRate;
	  			testRecordInfo6 = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Externally Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + knoxHiveContextPath 
	  					+ "\n          on BigData '" + bdClusterName + "' Cluster From Knox Node - '" + currClusterKnoxNodeName + "'"
						+ "\n          at the time - " + tempTime6_end + " and Time Used: " + time6Used
	  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
	  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
	  		}
	  		sb.setLength(0);
	  		writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo6, true);
	  		
		}//end for of 9
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Knox WebHDFS (cURL), WebHCat.... on '" + bdClusterName + "' Cluster from Knox Server node - " + currClusterKnoxNodeName + "!";
		currNotingMsg += "\n***** Done - Present Knox Testing Generated HDFS/WebHDFS Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Knox Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Knox Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Knox Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, currNotingMsg, true);
		
		if (currClusterKnoxNode2Name.isEmpty()){
			Desktop.getDesktop().open(new File(dcTestKnox_RecFilePathAndName));
		}		
			
	}//end run()
	
	
//	private static int appendFileDataToExistingKnoxFile (String existingHdfsFilePathAndName, String appendingDataFile, FileSystem currHadoopFS) throws IOException {
//		int existVal = 10000;
//		FSDataOutputStream fsDataOutStream = currHadoopFS.append(new Path(existingHdfsFilePathAndName));			
////		//PrintWriter bw = new PrintWriter(fsDataOutStream);	
////		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
////		
////		//String srcFilePathAndName2 = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
////		System.out.println("\n*** appendingDataFile: " + appendingDataFile);
////		FileReader aFileReader = new FileReader(appendingDataFile);
////		BufferedReader br = new BufferedReader(aFileReader);
////		String line = "";
////		
////		while ((line = br.readLine()) != null) {
////			try {
////				//bw.append(line);
////				existVal = 0;
////				break;
////			} catch (Exception e) {
////				break;
////				//e.printStackTrace();
////			} //end try
////				
////		}
////		br.close();
////		aFileReader.close();
////						
////		bw.close();
//		fsDataOutStream.close();
//		return existVal;
//	}//end appendFileDataToExistingKnoxFile
	
	private static String getTargetLineDataLineOfAFile (String filePathAndName, int targetLineNumber){
		String targetLineData = "";
		try {
			FileReader aFileReader = new FileReader(filePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
						
			String line = "";
			int lineNum = 0;
			while ((line = br.readLine()) != null) {
				lineNum++;
				if (lineNum == targetLineNumber){
					targetLineData = line;
				}							
			}//end while
			br.close();
			aFileReader.close();			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		return targetLineData;		
	}//getTargetLineDataLineOfAFile
	
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
		//System.out.println("\n*-* exitValue: " + exitValue);
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
	
	private static void transFormTestDataIntoBase64EncodedFormat (String uncodedLocalWebHBaseTestDataFilePathAndName, 
			String encodedLocalWebHBaseTestDataFilePathAndName){
		try {
			File tempFile = new File(encodedLocalWebHBaseTestDataFilePathAndName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);
			
			FileReader aFileReader = new FileReader(uncodedLocalWebHBaseTestDataFilePathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
						
			String line = "";			
			while ((line = br.readLine()) != null) {
				if (line.contains(",")){
					String [] lineSplit = line.split(",");
					String encodedLine = "";
					for (int i = 0; i < lineSplit.length; i++ ){
						String tempEncodedColumn =  Base64Str.getEncodedString(lineSplit[i].trim());
						if (i==0){
							encodedLine += tempEncodedColumn;
						} else {
							encodedLine += "," +tempEncodedColumn;
						}						
					}
					output.println(encodedLine);
				}							
			}//end while
			br.close();
			aFileReader.close();
			
			output.close();
			outStream.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Transform TestData Into Base64 Encoded Format for WebHBase Testing!!!\n");
		
	}//transFormTestDataIntoBase64EncodedFormat
	
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
