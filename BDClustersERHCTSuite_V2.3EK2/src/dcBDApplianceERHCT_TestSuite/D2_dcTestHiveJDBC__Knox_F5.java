package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.Base64Str;
import dcModelClasses.DayClock;
import dcModelClasses.HiveViaKnoxOrF5ConnectionFactory;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 3/11/2016; 5/4/2016; 9/15-18/2017
*/ 

@SuppressWarnings("unused")
public class D2_dcTestHiveJDBC__Knox_F5 {
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
	
	
	//@SuppressWarnings("deprecation")
	public static void run() throws Exception {
		//1. Preparation:
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String scriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Knox\\";
	    prepareFolder(scriptFilesFoder, "Local Knox Testing Script Files");
	    
		String dcTestKnox_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKnoxF5_WritingAndReading_Records_HiveJDBC_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKnox_RecFilePathAndName,  "Records of Testing HiveJDBC/Knox/F5 on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----    Automated Knox/F5-Gated Hive JDBC ... Representative Scenario Testing "
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
		//String bdKnoxClusterIdName = currBdCluster.getBdClusterIdName();
		
		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
					
//		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();	
//		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
//		
//		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
//		String currClusterKnoxFQDN = bdENCmdFactory.getServerURI();
//		System.out.println(" *** bdENCmdFactory.getServerURI() or currClusterKnoxFQDN: " + currClusterKnoxFQDN);
//		
//		String loginUserName = bdENCmdFactory.getUsername();
//		String loginUserADPassWd = bdENCmdFactory.getPassword();
		
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		String knoxTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Knox/";
		
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos			
		String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();		
		System.out.println(" *** Login User Name / AD Password: " + loginUser4AllNodesName + " / " + loginUserADPassWd);
		
//		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
//		String rootUserName = bdENRootCmdFactory.getUsername();
//		//String rootPw = bdENRootCmdFactory.getPassword();			
//		//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
//		//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
//		
//		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
//			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
//		}			
//		System.out.println("*** loginUserName is: " + loginUserName);
//		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
//		System.out.println("*** On '" + currClusterKnoxNodeName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		NumberFormat df = new DecimalFormat("#0.00");
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
	
		//String hdfsInternalPrincipal = currBdCluster.getHcatInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHcatInternalKeyTabFilePathAndName();
		
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);				
		//String loginUserADPassWd = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		//System.out.println("\n*** loginUserADPassWd is: " + loginUserADPassWd);
				
						
		ArrayList<String> bdClusterKnoxNodeList = currBdCluster.getCurrentClusterKnoxNodeList();
		int clusterKNNumber = bdClusterKnoxNodeList.size();	
		int clusterKNNumber_Start = 0; //0..1
		System.out.println(" *** clusterKNNumber: " + clusterKNNumber);
		
		String curlExeNode = "EN01";
		BdNode en01BDNode = new BdNode(curlExeNode, bdClusterName);
		ULServerCommandFactory bdENCmdFactory = en01BDNode.getBdENCmdFactory();
		ULServerCommandFactory bdENRootCmdFactory = en01BDNode.getBdENRootCmdFactory();
		System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		
		
		String loginUserName = bdENCmdFactory.getUsername(); 						
		String rootUserName = bdENRootCmdFactory.getUsername();
					
		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
		}			
		System.out.println("*** loginUserName is: " + loginUserName);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
		System.out.println("*** On '" + curlExeNode + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestDataFileName;
		//String enServerAppendingTestDataFileFullPathAndName = enServerScriptFileDirectory + localKnoxTestAppendingDataFileName;
		
		System.out.println("*** enServerTestDataFileFullPathAndName is: " + enServerTestDataFileFullPathAndName);
		//System.out.println("*** enServerAppendingTestDataFileFullPathAndName is: " + enServerAppendingTestDataFileFullPathAndName);
		
		
		//4. Move Test Data and Appending Test Data To EN01 Server Node of the Hadoop cluster
		//String localWinTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;
		//String localAppendingTestDataFileFullPathAndName = bdClusterUATestResultsParentFolder + localKnoxTestAppendingDataFileName;
		
		
		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		//int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxTestAppendingDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		//int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseJsonDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		//int exitVal4 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKnoxLargeWebHbaseXmlDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
		
		if (exitVal1 == 0 ){
			System.out.println("\n*** Done - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		} else {
			System.out.println("\n*** Failed - Moving Knox Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		}
		//if (exitVal2 == 0 ){
		//	System.out.println("\n*** Done - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//} else {
		//	System.out.println("\n*** Failed - Moving Knox Appending Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//}
		
		//if (exitVal3 == 0 ){
		//	System.out.println("\n*** Done - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//} else {
		//	System.out.println("\n*** Failed - Moving Knox/WebHabse Large JSON Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//}
		//if (exitVal4 == 0 ){
		//	System.out.println("\n*** Done - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//} else {
		//	System.out.println("\n*** Failed - Moving Knox/WebHabse Large XML Test Data File into '" + enServerScriptFileDirectory + "' folder on the 1st Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
		//}
		
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		
		
		//#8.1 & #8.2 & #8.3 are test scenario types for HiveJDBC testing
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\nHiveJDBC Testing: \n", true);		
		
		//8.1 Login to EN01 and Perform Data Writing/Reading to Hive Table  via HiveJDBC 
		//    via cURL cmds and active HiveJDBC HTTP URL	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[1]. HiveJDBC - Beeline via HiveServer2 Services \n", true);
		int hivejdbcSvrNumber_Start = 0;
		int clusterHivejdbcSvrNumber = 2; //1...2
		for (int i = hivejdbcSvrNumber_Start; i < clusterHivejdbcSvrNumber; i++){ 
			totalTestScenarioNumber++;
			
			//(1) Get HiveJDBC URL & Connection String:
			String activeWebHdfsHttpURL = currBdCluster.getActiveWebHdfsHttpAddress();
			
			//http://hdpr05mn01.mayo.edu:50111/templeton/v1/ddl/database/default/table/employee_hivejdbc;
			String hiveServer2JDBCConnStr = "";
			if (i==0){
				hiveServer2JDBCConnStr = currBdCluster.getBdHdfs1stNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
			}
			if (i==1){
				hiveServer2JDBCConnStr = currBdCluster.getBdHdfs2ndNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
			}
			//if (i==2){
			//	hiveServer2JDBCConnStr = currBdCluster.getBdHdfs3rdNnIPAddressAndPort().replace(":8020", ":10001").replace("hdfs", "jdbc:hive2");
			//}
			
			if (!hiveServer2JDBCConnStr.endsWith("/")){
				hiveServer2JDBCConnStr += "/";
			}	
			String hiveKrbSvcPrincipal = currBdCluster.getHiveSvcPrincipalName();
			hiveServer2JDBCConnStr += ";transportMode=http;httpPath=cliservice;principal=" + hiveKrbSvcPrincipal;
			//hiveServer2JDBCConnStr += ";transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG";
			String beelineHiveServer2JDBCConnStr = "beeline -u '" + hiveServer2JDBCConnStr + "'  -e ";
			System.out.println(" *** Current Hadoop cluster's beelineHiveServer2JDBCConnStr: " + beelineHiveServer2JDBCConnStr);
			
			
			//(2) Generate HiveJDBC cmds:
			//beeline -u 'jdbc:hive2://hdpr03mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(*) from default.employee1;";
			//beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "drop table if exists default.employee1;";
			String jdbcHiveTableName = "default.employee_hivejdbc" +(i+1);
			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
			
			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
			
			String dropHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
					+ "employeeId Int, \n"
					+ "fistName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ " address String \n"
					+ ")Row format delimited fields terminated by ',' \n"
					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
						
			String createHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
			String loadDataToHiveTableCmd = beelineHiveServer2JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
			
			String getQueryResultCmd = beelineHiveServer2JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
			
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
				    	    
		    sb.append(dropHiveTableCmd + ";\n");
		    sb.append(createHiveTableCmd + ";\n");		    
		    sb.append(loadDataToHiveTableCmd + ";\n"); 
		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");
		    sb.append(getQueryResultCmd + ";\n");
		    
		    
		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_templeton" + (i +1) + ".sh";			
			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String hivejdbcTestingCmds = sb.toString();
			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsHiveTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//*** line: +------+--+
					//*** line: | _c0  |
					//*** line: +------+--+
					//*** line: | 6    |
					
					if (line.contains("6")) {
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				
				
	        }//end outer if	
			
			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) HiveJDBC - Beeline via HiveServer2 Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String - " + hiveServer2JDBCConnStr
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) HiveJDBC - Beeline via HiveServer2 Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String - " + hiveServer2JDBCConnStr
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;			
		}//end 8.1		
		
		
		//8.2 Login to EN01 or Remotely On This Machine and Perform Data Writing/Reading to Hive Table  via HiveJDBC 
		//    via Knox Gateway URLs and Cluster F5 Balancer URL(s)		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[2]. HiveJDBC via Knox Gateway Services \n", true);
		
		//8.2a - By beeline 
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(2.1)]. HiveJDBC - By Beeline via Knox Gateway Services \n", true);
		
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1; //1..2..4
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HiveJDBC Through Knox Node: " + tempKnENName);
			
			String currKnoxNodeName = "";
			String currKnoxClusterName = "";
			if (tempKnENName.contains("_")){
				String[] tempKnENNameSplit  = tempKnENName.split("_");
				currKnoxNodeName = tempKnENNameSplit[0];
				String simpleKnoxClusterName = tempKnENNameSplit[1];
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
					currKnoxClusterName = "BDDev3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
					currKnoxClusterName = "BDDev1";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
					currKnoxClusterName = "BDTest3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
					currKnoxClusterName = "BDTest2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
					currKnoxClusterName = "BDProd3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
					currKnoxClusterName = "BDProd2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
					currKnoxClusterName = "BDSdbx";
				}				
			} else {
				currKnoxNodeName = tempKnENName;
				currKnoxClusterName = bdClusterName;
			}
			
			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
			
			//(1) Get Current Knox Hive JDBC Connection Factory			
			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
			
			int knoxGateWayPortNum = 8442;
			System.out.println(" *** knoxGateWayPortNum: " + knoxGateWayPortNum);
			
			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
				
			String bdKnoxHiveContextPath = "gateway/" + bdKnoxClusterIdName + "/hive";					
			String bdKnoxOrF5HiveContextPath = bdKnoxHiveContextPath;
			
			
			String hiveDatabaseName = "default";
			
			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
			
			String hiveKnoxOrF5ConnectionURL_ori = aHiveViaKnoxConnFactory.getUrl();
			String hiveKnoxOrF5ConnectionURL_view = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=.*", "").replaceFirst(";password=.*", "");
			System.out.println(" *** hiveKnoxOrF5ConnectionURL_view: " + hiveKnoxOrF5ConnectionURL_view);
			
			String hiveKnoxOrF5ConnectionURL4Beeline = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=", " ").replaceFirst(";password=", " ");
			System.out.println(" *** hiveKnoxOrF5ConnectionURL4Beeline: " + hiveKnoxOrF5ConnectionURL4Beeline);
			//jdbc:hive2://hdpr07en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89			
			
			String beelineKnoxOrF5JDBCConnStr = "beeline -u '" + hiveKnoxOrF5ConnectionURL4Beeline + "'  -e ";
			System.out.println(" *** Current Hadoop cluster's beelineKnoxOrF5JDBCConnStr: " + beelineKnoxOrF5JDBCConnStr);


			//(2) Generate HiveJDBC cmds:
			//## Prod2:
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr02mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr01kx03.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://bigdataknox.mayo.edu/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			//## Dev3:	
			// beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
			// beeline -u 'jdbc:hive2://hdpr05en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			// beeline -u 'jdbc:hive2://bigdata.mayo.edu/default;ssl=true;transportMode=http;httpPath=hdp/DEV3/knox/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
		
			String jdbcHiveTableName = "default.employee_hivejdbc_beeline_knox" +(i+1);
			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
			
			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
			
			String dropHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
					+ "employeeId Int, \n"
					+ "fistName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ " address String \n"
					+ ")Row format delimited fields terminated by ',' \n"
					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
						
			String createHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
			String loadDataToHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
			
			String getQueryResultCmd = beelineKnoxOrF5JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
			
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
				    	    
		    sb.append(dropHiveTableCmd + ";\n");
		    sb.append(createHiveTableCmd + ";\n");		    
		    sb.append(loadDataToHiveTableCmd + ";\n"); 
		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");
		    sb.append(getQueryResultCmd + ";\n");
		    
		    
		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_HiveJDBC_Beeline" + (i +1) + ".sh";			
			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String hivejdbcTestingCmds = sb.toString();
			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsHiveTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//*** line: +------+--+
					//*** line: | _c0  |
					//*** line: +------+--+
					//*** line: | 6    |
					
					if (line.contains("6")) {
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				
				
	        }//end outer if	
			
			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) HiveJDBC - Beeline via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) HiveJDBC - Beeline via Knox Gateway Service - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;	
		}//end 8.2a
		
		
		//8.2b -- - Remote HiveJDBC via Knox Gateway Services
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(2.2)]. HiveJDBC - Remotely via Knox Gateway Services \n", true);
		//int clusterKNNumber = bdClusterKnoxNodeList.size();	
		//clusterKNNumber_Start = 0; //0..1..2..	
		//clusterKNNumber = 1; //1..2..4
		for (int i = clusterKNNumber_Start; i < clusterKNNumber; i++){ //bdClusterKnoxNodeList.size()..1..clusterKNNumber
			totalTestScenarioNumber++;
			
			String tempKnENName = bdClusterKnoxNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HiveJDBC Through Knox Node: " + tempKnENName);
			
			String currKnoxNodeName = "";
			String currKnoxClusterName = "";
			if (tempKnENName.contains("_")){
				String[] tempKnENNameSplit  = tempKnENName.split("_");
				currKnoxNodeName = tempKnENNameSplit[0];
				String simpleKnoxClusterName = tempKnENNameSplit[1];
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV3")){
					currKnoxClusterName = "BDDev3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("DEV1")){
					currKnoxClusterName = "BDDev1";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("TEST3")){
					currKnoxClusterName = "BDTest3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Test3")){
					currKnoxClusterName = "BDTest2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD3")){
					currKnoxClusterName = "BDProd3";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("PROD2")){
					currKnoxClusterName = "BDProd2";
				}
				if (simpleKnoxClusterName.equalsIgnoreCase("Sdbx")){
					currKnoxClusterName = "BDSdbx";
				}				
			} else {
				currKnoxNodeName = tempKnENName;
				currKnoxClusterName = bdClusterName;
			}
			
			System.out.println(" *** currKnoxNodeName: " + currKnoxNodeName);
			System.out.println(" *** currKnoxClusterName: " + currKnoxClusterName);
			
			//(1) Get Current Knox Hive JDBC Connection Factory			
			BdNode knoxBDNode = new BdNode(currKnoxNodeName, currKnoxClusterName);
			ULServerCommandFactory bdKnoxCmdFactory = knoxBDNode.getBdENCmdFactory();
			//ULServerCommandFactory bdKnoxRootCmdFactory = knoxBDNode.getBdENRootCmdFactory();
			String currKnoxNodeFQDN = bdKnoxCmdFactory.getServerURI();
			System.out.println(" *** bdKnoxCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);
			
			int knoxGateWayPortNum = 8442;
			System.out.println(" *** knoxGateWayPortNum: " + knoxGateWayPortNum);
			
			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);
				
			String bdKnoxHiveContextPath = "gateway/" + bdKnoxClusterIdName + "/hive";					
			String bdKnoxOrF5HiveContextPath = bdKnoxHiveContextPath;
			
			
			String hiveDatabaseName = "default";
			
			
			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
			
			//(2) Move Hive Testing Data into HDFS
			final String jdbcHiveTableName = "employee_hivejdbc_knox" +(i+1);
			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
			//hdfsHiveDefaultTestDataFilePathAndName = hdfsHiveTestDataFilePathAndName;						
			
			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
	  		hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);
	  		
	  		Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
	  		if (currHadoopFS.exists(outputPath)) {
	  			currHadoopFS.delete(outputPath, false);
	  			System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
	          }
	  		
	  		String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;	  		
	  		moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);
			
	  		
			//(3) Hive Table Operations via Hive JDBC through Knox:			

			String hivejdbcTestResultFileName = jdbcHiveTableName + "_knox_curl_result.txt";
			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
				
			 
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
		  			String dropTblSqlStr = "drop table " + jdbcHiveTableName;
		  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
		  			if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Dropping Hive table - " + jdbcHiveTableName + "\n");			
		  			} else {
		  				sb.append("    -*- 'Failed' - Dropping Hive table - " + jdbcHiveTableName + "\n");
		  			}
		  			
		  			//2). create a new Hive-managed table
		  			totalTestCaseNumber++;
		  		    String createTblSqlStr = "create table " + jdbcHiveTableName 
		  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
		  		      		+ "Row format delimited fields terminated by ',' ";
		  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
		  		    if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Creating Hive table - " + jdbcHiveTableName + "\n");	
		  		    } else {
		  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + jdbcHiveTableName + "\n");
		  			}   
		  		    
		  		    //3). load the Hive-managed table by overwriting  
		  		    totalTestCaseNumber++;
		  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName ;
		  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
		  		  	if (exitValue == 0){
		  				successTestCaseNum ++;
		  				sb.append("    *** Success - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
		  		  	} else {
		  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
		  			}    
		  	      		    	  	
		  		    //4).HQuery (row-counting) the above-generated Hive-managed table
		  	  		totalTestCaseNumber++;
		  		    String queryTblSqlStr = "select count(1) from " + jdbcHiveTableName;  //count(*)...count(1)...*
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
		  	  			sb.append("    *** Success - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
		  	  		} else {
		  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
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
			    //thread.stop();//.stop();
			    thread.interrupt();
			}
			Thread.sleep(1*5*1000);	
			
			
			System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
			System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
			
	  		String testRecordInfo = "";	  		
	  		if (currScenarioSuccessRate == 1){
	  			successTestScenarioNum++;
	  			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + jdbcHiveTableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
	  		} else if (currScenarioSuccessRate == 0){
	  			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + jdbcHiveTableName + "'\n";	 	 
	  		} else {
	  			successTestScenarioNum += currScenarioSuccessRate;
	  			testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
	  					+ "\n  --(1) Hive JDBC Via Knox - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
	  					+ "\n          and Querying a Hive-Managed Table via Knox/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
	  					+ "\n          via BigData '" + bdClusterName + "' Cluster Knox Server - '" + currKnoxNodeFQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
	  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
	  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
	  		}
	  		sb.setLength(0);
	  		//writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;					
		}//end 8.2b		
		//end 8.2			
				
		
		//8.3 Login to EN01 or Remotely On This Machine and Perform Data Writing/Reading to Hive Table via HiveJDBC 
		//    via Knox Gateway URLs and Cluster F5 Balancer URL(s)	
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "\n[3]. HiveJDBC via F5 Balancer(s) \n", true);
		int f5BalancerNumber_Start = 0;
		int clusterF5BalancerNNNumber = 1;
		
		//8.3a - By beeline 
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(3.1)]. HiveJDBC - By Beeline via F5 Balancer(s) \n", true);
		//int f5BalancerNumber_Start = 0;
		//int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			//https://bigdata.mayo.edu/hdp/DEV3/knox
			
			
			//(1) Get Current F5 Hive JDBC Connection Factory			
			currClusterF5ConnStr = currClusterF5ConnStr.replace("https://", "");
			String currF5FQDN = currClusterF5ConnStr;
			String bdF5HiveContextPath = "";
			if (currClusterF5ConnStr.contains("/")){
				String[] f5ConnStrSplit = currClusterF5ConnStr.split("/");
				currF5FQDN  = f5ConnStrSplit[0];
				for (int j = 1; j < f5ConnStrSplit.length; j++ ){
					int maxIndexNum = f5ConnStrSplit.length - 1;
					if (j <= maxIndexNum - 1 ){
						bdF5HiveContextPath +=  f5ConnStrSplit[j] + "/";
					} else {
						bdF5HiveContextPath +=  f5ConnStrSplit[j] ;
					}					
				}
			}
			bdF5HiveContextPath += "/hive";
			
			System.out.println(" *** currF5FQDN: " + currF5FQDN);
			System.out.println(" *** bdF5HiveContextPath: " + bdF5HiveContextPath);

			int f5BalancerPortNum = 443;
			System.out.println(" *** f5BalancerPortNum: " + f5BalancerPortNum);

			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);							
			String bdKnoxOrF5HiveContextPath = bdF5HiveContextPath;


			String hiveDatabaseName = "default";
			
			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currF5FQDN, f5BalancerPortNum,
					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);
			
						
			String hiveKnoxOrF5ConnectionURL_ori = aHiveViaKnoxConnFactory.getUrl();
			String hiveKnoxOrF5ConnectionURL_view = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=.*", "").replaceFirst(";password=.*", "");
			System.out.println(" *** hiveKnoxOrF5ConnectionURL_view: " + hiveKnoxOrF5ConnectionURL_view);
			
			String hiveKnoxOrF5ConnectionURL4Beeline = hiveKnoxOrF5ConnectionURL_ori.replaceFirst(";user=", " ").replaceFirst(";password=", " ");
			System.out.println(" *** hiveKnoxOrF5ConnectionURL4Beeline: " + hiveKnoxOrF5ConnectionURL4Beeline);
			//jdbc:hive2://hdpr07en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89			
			
			String beelineKnoxOrF5JDBCConnStr = "beeline -u '" + hiveKnoxOrF5ConnectionURL4Beeline + "'  -e ";
			System.out.println(" *** Current Hadoop cluster's beelineKnoxOrF5JDBCConnStr: " + beelineKnoxOrF5JDBCConnStr);


			//(2) Generate HiveJDBC cmds:
			//## Prod2:
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr02mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://hdpr01kx03.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			// beeline --silent=true --showWarnings=false -u 'jdbc:hive2://bigdataknox.mayo.edu/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPPROD2/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			//## Dev3:	
			// beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG' -e "select count(1) from employee_hivejdbc_knox1";
			// beeline -u 'jdbc:hive2://hdpr05en01.mayo.edu:8442/default;ssl=true;transportMode=http;httpPath=gateway/MAYOHADOOPDEV3/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'
			// beeline -u 'jdbc:hive2://bigdata.mayo.edu/default;ssl=true;transportMode=http;httpPath=hdp/DEV3/knox/hive wa00336 bnhgui89' -e 'select count(1) from employee_hivejdbc_knox1'

			String jdbcHiveTableName = "default.employee_hivejdbc_beeline_f5balancer" +(i+1);
			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
			
			String hivejdbcTestResultFileName = jdbcHiveTableName + "_result.txt";
			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;
			
			String dropHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"drop table " + jdbcHiveTableName + "\"";
			String createHiveTableStr = "create table " + jdbcHiveTableName + "( \n"
					+ "employeeId Int, \n"
					+ "fistName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ " address String \n"
					+ ")Row format delimited fields terminated by ',' \n"
					+ "Location '" + knoxTestFolderName + jdbcHiveTableName + "' ";
						
			String createHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
			String loadDataToHiveTableCmd = beelineKnoxOrF5JDBCConnStr + "\"load data inpath '" + hdfsHiveTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName + "\"";
			
			String getQueryResultCmd = beelineKnoxOrF5JDBCConnStr + "\"select count(*) from " + jdbcHiveTableName + "\" > " + localHiveJDBCTestResultPathAndName;
			
			//sb.append("sudo su - " + loginUserName + ";\n");		
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			// 
			//hdfs dfs -mkdir -p /user/wa00336/test/Knox;
			//hdfs dfs -copyFromLocal /data/home/wa00336/test/dcFSETestData.txt /user/wa00336/test/Knox/employee_knox_hivejdbc.txt;
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + knoxTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + knoxTestFolderName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerTestDataFileFullPathAndName + " " + activeNN_addr_port + hdfsHiveTestDataFilePathAndName + "; \n");	
				    	    
		    sb.append(dropHiveTableCmd + ";\n");
		    sb.append(createHiveTableCmd + ";\n");		    
		    sb.append(loadDataToHiveTableCmd + ";\n"); 
		    //sb.append(queryHiveTableFullCmd + ";\n"); 		    
		    //sb.append(getQueryResultFullCmd + ";\n");
		    sb.append(getQueryResultCmd + ";\n");
		    
		    
		    sb.append("hadoop fs -copyFromLocal " + localHiveJDBCTestResultPathAndName + " " + activeNN_addr_port + hdfsHiveTestResultFileName + "; \n");
		    //sb.append("rm -f " + localHiveJDBCTestResultPathAndName + "; \n");
		    sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + knoxTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    String knoxHiveScriptFullFilePathAndName = scriptFilesFoder + "dcTestKnox_HiveScriptFile_Curl_HiveJDBC_F5Balancer" + (i +1) + ".sh";			
			prepareFile (knoxHiveScriptFullFilePathAndName,  "Script File For Testing Knox HiveJDBC on '" + bdClusterName + "' Entry Node - " + curlExeNode);
			
			String hivejdbcTestingCmds = sb.toString();
			writeDataToAFile(knoxHiveScriptFullFilePathAndName, hivejdbcTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(knoxHiveScriptFullFilePathAndName));		
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(knoxHiveScriptFullFilePathAndName, 
					scriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(hdfsHiveTestResultFileName);
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsHiveTestResultFileName);
				FileStatus[] status = currHadoopFS.listStatus(filePath);				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				//boolean foundWrittenStr = false;
				//boolean foundAppendedStr = false;
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					//*** line: +------+--+
					//*** line: | _c0  |
					//*** line: +------+--+
					//*** line: | 6    |
					
					if (line.contains("6")) {
						currTestScenarioSuccessStatus = true;
						break;
					}													
				}//end while
				br.close();				
				
	        }//end outer if	
			
			System.out.println("*** hdfsHiveTestResultFileName is: " + hdfsHiveTestResultFileName);
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	 
			
			String testRecordInfo = "";
			if (currTestScenarioSuccessStatus) {
				successTestScenarioNum++;			
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) HiveJDBC - Beeline via F5 Balancer  - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
	        			+ "\n  -- (1) HiveJDBC - Beeline via F5 Balancer  - Hive Table Deleting, Creating, Data-Loading, and Querying "
						+ "\n          via HiveJDBC Connection String by Beeline - " + hiveKnoxOrF5ConnectionURL_view
						+ "\n          on BigData '" + bdClusterName  + "' Cluster From Entry Node - '" + curlExeNode + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Generated Testing Results File on HDFS/HiveJDBC:  '" + hdfsHiveTestResultFileName + "' \n";
	        }
			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;	
		}//end 8.3a
		
		//8.3b - Remote HiveJDBC via F5 Balancer
		writeDataToAFile(dcTestKnox_RecFilePathAndName, "[(3.2)]. HiveJDBC - Remotely via F5 Balancer(s) \n", true);		
		//int f5BalancerNumber_Start = 0;
		//int clusterF5BalancerNNNumber = 1;
		for (int i = f5BalancerNumber_Start; i < clusterF5BalancerNNNumber; i++){ 
			totalTestScenarioNumber++;
			
			String currClusterF5ConnStr = bdENCmdFactory.getBdClusterF5ConnStr();
			System.out.println(" *** currClusterF5ConnStr or bdENCmdFactory.getBdClusterF5ConnStr() : " + currClusterF5ConnStr);
			//https://bigdata.mayo.edu/hdp/DEV3/knox
			
			
			//(1) Get Current F5 Hive JDBC Connection Factory			
			currClusterF5ConnStr = currClusterF5ConnStr.replace("https://", "");
			String currF5FQDN = currClusterF5ConnStr;
			String bdF5HiveContextPath = "";
			if (currClusterF5ConnStr.contains("/")){
				String[] f5ConnStrSplit = currClusterF5ConnStr.split("/");
				currF5FQDN  = f5ConnStrSplit[0];
				for (int j = 1; j < f5ConnStrSplit.length; j++ ){
					int maxIndexNum = f5ConnStrSplit.length - 1;
					if (j <= maxIndexNum - 1 ){
						bdF5HiveContextPath +=  f5ConnStrSplit[j] + "/";
					} else {
						bdF5HiveContextPath +=  f5ConnStrSplit[j] ;
					}					
				}
			}
			bdF5HiveContextPath += "/hive";
			
			System.out.println(" *** currF5FQDN: " + currF5FQDN);
			System.out.println(" *** bdF5HiveContextPath: " + bdF5HiveContextPath);

			int f5BalancerPortNum = 443;
			System.out.println(" *** f5BalancerPortNum: " + f5BalancerPortNum);

			String bdKnoxClusterIdName = bdENCmdFactory.getBdClusterIdName();
			System.out.println(" *** bdKnoxClusterIdName: " + bdKnoxClusterIdName);							
			String bdKnoxOrF5HiveContextPath = bdF5HiveContextPath;


			String hiveDatabaseName = "default";
			
			final HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currF5FQDN, f5BalancerPortNum,
					hiveDatabaseName, "","",bdKnoxClusterIdName, loginUserName, loginUserADPassWd, bdKnoxOrF5HiveContextPath);

			//(2) Move Hive Testing Data into HDFS
			final String jdbcHiveTableName = "employee_hivejdbc_f5balancer" +(i+1);
			String hdfsHiveTestDataFilePathAndName = knoxTestFolderName + jdbcHiveTableName + "_TableData.txt";
			final String hiveDefaultFolderPath = "/apps/hive/warehouse/";
			hdfsHiveDefaultTestDataFilePathAndName = hiveDefaultFolderPath + localKnoxTestDataFileName; 
			//hdfsHiveDefaultTestDataFilePathAndName = hdfsHiveTestDataFilePathAndName;						

			System.out.println("\n*** hdfsHiveDefaultTestDataFilePathAndName: " + hdfsHiveDefaultTestDataFilePathAndName);
			hdfsFilePathAndNameList.add(hdfsHiveDefaultTestDataFilePathAndName);

			Path outputPath = new Path(hdfsHiveDefaultTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, false);
				System.out.println("\n*** deleting existing Hive Testa Data HDFS file in default Hive folder: \n	---- " + hdfsHiveDefaultTestDataFilePathAndName);
			}

			String localWinSrcHiveTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKnoxTestDataFileName;	  		
			moveWindowsLocalHiveTestDataToHDFS (localWinSrcHiveTestDataFilePathAndName, hdfsHiveDefaultTestDataFilePathAndName, currHadoopFS);


			//(3) Hive Table Operations via Hive JDBC through F5:			

			String hivejdbcTestResultFileName = jdbcHiveTableName + "_knox_curl_result.txt";
			String localHiveJDBCTestResultPathAndName = enServerScriptFileDirectory + hivejdbcTestResultFileName;
			String hdfsHiveTestResultFileName = knoxTestFolderName + hivejdbcTestResultFileName;


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
				String dropTblSqlStr = "drop table " + jdbcHiveTableName;
				int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
				if (exitValue == 0){
					successTestCaseNum ++;
					sb.append("    *** Success - Dropping Hive table - " + jdbcHiveTableName + "\n");			
				} else {
					sb.append("    -*- 'Failed' - Dropping Hive table - " + jdbcHiveTableName + "\n");
				}

				//2). create a new Hive-managed table
				totalTestCaseNumber++;
			    String createTblSqlStr = "create table " + jdbcHiveTableName 
					+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
					+ "Row format delimited fields terminated by ',' ";
			    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
			    if (exitValue == 0){
					successTestCaseNum ++;
					sb.append("    *** Success - Creating Hive table - " + jdbcHiveTableName + "\n");	
			    } else {
				sb.append("    -*- 'Failed' - Creating Hive table - " + jdbcHiveTableName + "\n");
				}   

			    //3). load the Hive-managed table by overwriting  
			    totalTestCaseNumber++;
				String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + jdbcHiveTableName ;
				runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
				if (exitValue == 0){
					successTestCaseNum ++;
					sb.append("    *** Success - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
				} else {
					sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + jdbcHiveTableName + "\n");
				}    

			    //4).HQuery (row-counting) the above-generated Hive-managed table
				totalTestCaseNumber++;
			    String queryTblSqlStr = "select count(1) from " + jdbcHiveTableName;  //count(*)...count(1)...*
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
					sb.append("    *** Success - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
				} else {
					sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + jdbcHiveTableName + "\n");
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
				//thread.stop();//.stop();
			    thread.interrupt();
			}
			Thread.sleep(1*5*1000);	
	
	
			System.out.println("\n*-* currScenarioDetailedTestingRecordInfo: " + currScenarioDetailedTestingRecordInfo);
			System.out.println("\n*-* currScenarioSuccessRate: " + currScenarioSuccessRate);
	
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
	
			String testRecordInfo = "";	  		
			if (currScenarioSuccessRate == 1){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
					+ "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + jdbcHiveTableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
			} else if (currScenarioSuccessRate == 0){
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
					+ "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + jdbcHiveTableName + "'\n";	 	 
			} else {
				successTestScenarioNum += currScenarioSuccessRate;
				testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Hive JDBC Via F5 Balancer - Remotely Dropping, Creating, Loading (externally -written HDFS file data),"
						+ "\n          and Querying a Hive-Managed Table via F5 Balancer/Hive JDBC httpPath - " + bdKnoxOrF5HiveContextPath 
						+ "\n          via BigData '" + bdClusterName + "' Cluster - F5 Balancer Server - '" + currF5FQDN + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
					+ "\n  --(2) Current Scenario Test-Case Results Detail: "
					+ "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
			}
			sb.setLength(0);
			//writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);			
			writeDataToAFile(dcTestKnox_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;					
		
		}//end 8.3b
		//end 8.3

		
						
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();		
		String timeUsed_end = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing HiveJDBC (cURL) via HiveServer2, Knox Gateway Services and F5 Balancer.... on '" + bdClusterName + "'";
		currNotingMsg += "\n***** Done - Present Knox/F5 Testing Generated HDFS/HiveJDBC Files - Total: '" + hdfsFilePathAndNameList.size() + "'";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed_end; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Knox/F5 Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Knox/F5 Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Knox/F5 Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestKnox_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestKnox_RecFilePathAndName));
	
	}//end run()
	
	
//	private static int appendFileDataToExistingKnoxFile (String existingHcatFilePathAndName, String appendingDataFile, FileSystem currHadoopFS) throws IOException {
//		int existVal = 10000;
//		FSDataOutputStream fsDataOutStream = currHadoopFS.append(new Path(existingHcatFilePathAndName));			
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
