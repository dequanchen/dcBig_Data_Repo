package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ConfigureDBResources;
import dcModelClasses.DatabaseConnectionFactory;
import dcModelClasses.DayClock;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/10-11/2014; 
*       1/14-15/2016 (Kerberos); 3/1/2016; 3/24/206; 5/10/2017
*/ 

public class A4_dcTestSqoop_ImportingAndExporting {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";	
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String sqoopTestFolderName = "";
	private static String internalKinitCmdStr = "";
	
	private static String sqlServerDBName = ""; 
	private static String importTableName = ""; 
	private static String exportTableName = ""; 	
	private static String sqoopImportedFolder = "";
	private static String exportTableName2 = ""; 
	private static String enServerScriptFileDirectory = "/home/hdfs";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/sqoop/bin/sqoop (<=TDH2.1.11) (<=TDH2.1.11) ==> /usr/bin/sqoop or /usr/hdp/2.3.4.0-3485/sqoop/bin/sqoop (TDH2.3.4)
    //  sb.append("hadoop fs -rm -r -touchz " + hdfsSqoopImportedFolderPathAndName + "; \n"); //For TDH2.3.4 only
	//--username TU05303 --password vbfgrt45 ==> --username TU05303 --password 80uijknm (06/13/2016)
	
	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 9+1 parameters for Sqoop/MapReduce-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		sqlServerDBName = args[4];	
		importTableName = args[5];	
		exportTableName = args[6];	
		sqoopTestFolderName = args[7];
		exportTableName2 = args[8]; 
		internalKinitCmdStr = args[9];
		
		if (!sqoopTestFolderName.endsWith("/")){
			sqoopTestFolderName += "/";
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
		String sqoopScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "sqoop\\";
	    prepareFolder(sqoopScriptFilesFoder, "Local Sqoop Testing Script Files");
	    
	    String dcTestSqoop_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestSqoop_ImportingAndExporting_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestSqoop_RecFilePathAndName,  "Records of Testing Sqoop on '" + bdClusterName + "' Cluster");
				
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Sqoop Importing And Exporting Representative Scenario Testing - Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestSqoop_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
				
		//3. Get cluster FileSystem and other information for testing	      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();		
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();	
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		sqoopTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Sqoop/"; //Modify sqoopImportedFolder from "/data/test/Sqoop/";
		sqoopImportedFolder = sqoopTestFolderName + "ecg/";//Modify sqoopImportedFolder from "/data/test/Sqoop/ecg/"
		
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
		
		//4. Test Sqoop Importing
		//   Loop through bdClusterEntryNodeList to Import data in EDT_BigData.dbo.archiveFile into HDFS system
		String tempSqlQueryWhereClause = "WHERE MsgType = 'ecg' and amalgaMsgDtTmPosted LIKE '%2013/9%'  \n";		
		//clusterENNumber_Start = 19; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 20; //1..2..3..4..5..6
		writeDataToAFile(dcTestSqoop_RecFilePathAndName, "[1]. Sqoop Importing \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();
			System.out.println("\n--- (" + (i+1) + ") Testing Sqoop Importing to HDFS on Entry Node: " + tempENName);	
						
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
						
			String hdfsSqoopImportedFolderPathAndName = sqoopImportedFolder + "dcSqoopImportedECG_Data_No"+ (i+1);
						
			String importColumnListStr = "amalgaMsgDtTmPosted, hl7MsgId, hl7MsgTimestamp, MsgType, AmalgaMsgText";
			if (tempENName.equalsIgnoreCase("MN02")){
				importColumnListStr = "amalgaMsgDtTmPosted, hl7MsgId, hl7MsgTimestamp, MsgType";
			}
			
			String sqoopImportFullCmd = generateECGTestDataSqoopImportFullCmd (tempSqlQueryWhereClause, importColumnListStr, hdfsSqoopImportedFolderPathAndName);
	
			String loginUserHdfsHomeFolderName = "/user/" + loginUserName;
			
			//hadoop fs ... hdfs dfs
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			
			
			//sb.append("hadoop fs -rm -r -skipTrash " + sqoopImportedFolder + "; \n");	
			
			sb.append("hadoop fs -mkdir -p " + sqoopImportedFolder + "; \n");			    
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + sqoopImportedFolder + "; \n");
		    sb.append("hadoop fs -mkdir -p " + sqoopImportedFolder.replace("ecg/", "") + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + sqoopImportedFolder + "; \n");
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsSqoopImportedFolderPathAndName + "; \n");
		    sb.append("hadoop fs -mkdir -p " + loginUserHdfsHomeFolderName + "; \n"); //for TDH2.3.4 staging
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + loginUserHdfsHomeFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + loginUserHdfsHomeFolderName + "; \n");		    
		    
		    //sb.append("hadoop fs -touchz " + hdfsSqoopImportedFolderPathAndName + "; \n"); //For TDH2.3.4 non-Kerberos only
		    //sb.append("kdestroy;\n");		   		    
		    //sb.append("sudo su - " + loginUserName + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
		    
		    sb.append(sqoopImportFullCmd + ";\n");
		    sb.append("hadoop fs -chmod -R 550 " + sqoopImportedFolder + "; \n");
		    sb.append("kdestroy;\n");
			
			String localSqoopImportScriptFilePathAndName = sqoopScriptFilesFoder + "dcTestSqoop_EcgDataImportingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (dcTestSqoop_RecFilePathAndName,  "Script File For Testing Sqoop Import on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String sqoopImportCmds = sb.toString();
			writeDataToAFile(localSqoopImportScriptFilePathAndName, sqoopImportCmds, false);		
			sb.setLength(0);
				
			//Desktop.getDesktop().open(new File(localSqoopImportScriptFilePathAndName));
			
			//LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localSqoopImportScriptFilePathAndName, 
			//		 bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(localSqoopImportScriptFilePathAndName, 
					sqoopScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			Path outputPath = new Path(hdfsSqoopImportedFolderPathAndName + "/part-m-00000");  //"hdfs://MAYOHADOOPTEST1/" +
			String testRecordInfo = "";
			if (currHadoopFS.exists(outputPath)) {
				successTestScenarioNum++;
				
				hdfsFilePathAndNameList.add(hdfsSqoopImportedFolderPathAndName);
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Sqoop Importing RDMSDB Data to HDFS File \n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
						+ "\n  -- (2) Generated File on HDFS System:  '" + hdfsSqoopImportedFolderPathAndName + "/part-m-00000' "
						+ "\n  -- (3) Sqoop-Import Total Time Used: " + timeUsed + "\n"; 					     
	        } else {
	        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Sqoop Importing RDMSDB Data to HDFS File \n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
						+ "\n  -- (2) Generated File on HDFS System:  '" + hdfsSqoopImportedFolderPathAndName + "/part-m-00000' "
						+ "\n  -- (3) Sqoop-Import Total Time Used: " + timeUsed + "\n";				    	    
	        }			
			writeDataToAFile(dcTestSqoop_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for
				
		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//5. Test Sqoop Exporting:
		//   Loop through bdClusterEntryNodeList to Export data into EDT_BigData.dbo.sqoopExported from HDFS system
		//for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber
		//clusterENNumber = 1;
		writeDataToAFile(dcTestSqoop_RecFilePathAndName, "\n[2]. Sqoop Exporting \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();
			System.out.println("\n--- (" + (i+1) + ") Testing Sqoop Exporting from HDFS on Entry Node: " + tempENName);	
						
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
							
			String hdfsSrcFileFolderPathAndName = sqoopImportedFolder + "dcSqoopImportedECG_Data_No"+ (i+1);			
			String sqoopExportToTableName = exportTableName;
			if (tempENName.equalsIgnoreCase("MN02")){
				sqoopExportToTableName = exportTableName2;
			}
			
			//String hdfsSrcFileFolderPathAndName = sqoopImportedFolder + "dcSqoopImportedECG_Data_No6";			
			//String sqoopExportToTableName = exportTableName2;		
			
			String sqoopExportFullCmd = generateECGTestDataSqoopExportFullCmd (hdfsSrcFileFolderPathAndName,sqoopExportToTableName);
			
			//sb.append("sudo su - " + loginUser4AllNodesName + ";\n");
		    sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
		    sb.append(sqoopExportFullCmd + ";\n");
		    sb.append("kdestroy;\n");
			
			
			//sb.append("sudo su - ambari-qa;\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n");
		    //sb.append(sqoopExportFullCmd + ";\n");		    
		    ////sb.append("hadoop fs -chown -R hdfs:hdfs " + hdfsSrcFileFolderPathAndName + "; \n");
		    //sb.append("hadoop fs -chmod -R 550 " + hdfsSrcFileFolderPathAndName + "; \n");
			
			String sqoopExportScriptFilePathAndName = sqoopScriptFilesFoder + "dcTestSqoop_EcgDataExportingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (sqoopExportScriptFilePathAndName,  "Script File For Testing Sqoop Export on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String sqoopExportCmds = sb.toString();
			writeDataToAFile(sqoopExportScriptFilePathAndName, sqoopExportCmds, false);		
			sb.setLength(0);
						
			//Desktop.getDesktop().open(new File(sqoopExportScriptFilePathAndName));
			
			boolean trucateExportStatus = trucateExportTable(sqoopExportToTableName);
			System.out.println(" --*-- trucateExportStatus is:" + trucateExportStatus);
			
			String testRecordInfo = "";
			if (trucateExportStatus) {
				LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(sqoopExportScriptFilePathAndName, 
						sqoopScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
							
				DayClock currClock = new DayClock();				
				String currTime = currClock.getCurrentDateTime();				
				String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);				
				
				int exportTableRowCount = countExportTableRows(sqoopExportToTableName);			
				
				if (exportTableRowCount > 0) {
					successTestScenarioNum++;
					testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
							+ "\n  -- (1) Sqoop Exporting HDFS Data to RDMSDB  \n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
							+ "\n  -- (2) Exported HDFS Data Total Row #:\t" + exportTableRowCount + " In MsSql Server Table - \"" + sqlServerDBName + ".dbo."  + sqoopExportToTableName + "\" "
							+ "\n  -- (3) Sqoop-Export Total Time Used: " + timeUsed + "\n";					
		        } else {
		        	testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
							+ "\n  -- (1) Sqoop Exporting HDFS Data to RDMSDB  \n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
							+ "\n  -- (2) Exported HDFS Data Total Row #:\t" + exportTableRowCount + " In MsSql Server Table - \"" + sqlServerDBName + ".dbo."  + sqoopExportToTableName + "\" "
							+ "\n  -- (3) Sqoop-Export Total Time Used: " + timeUsed + "\n";
		        }
				
				writeDataToAFile(dcTestSqoop_RecFilePathAndName, testRecordInfo, true);	
				prevTime = currTime;
			}  else {
				DayClock currClock = new DayClock();				
				String currTime = currClock.getCurrentDateTime();				
				//String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);						
				
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"						
						+ "\n  -- (1) Sqoop Could Not Export HDFS Data to RDMSDB  \n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "' at the time - " + currTime
						+ "\n  -- (2) Due to the Failure To Truncate the MsSql Server Table for Sqoop Exporting: \t\"" + sqlServerDBName + ".dbo."  + sqoopExportToTableName + "\" \n";
				
				writeDataToAFile(dcTestSqoop_RecFilePathAndName, testRecordInfo, true);
				prevTime = currTime;
			}//end outer-if		
						
		}//end for
		        
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Sqoop Importing To HDFS on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Done - Testing Sqoop Exporting From HDFS on '" + bdClusterName + "' Cluster for " + hdfsFilePathAndNameList.size() + " HDFS Folders!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Sqoop Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Sqoop Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Sqoop Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestSqoop_RecFilePathAndName, currNotingMsg, true);
		
		Desktop.getDesktop().open(new File(dcTestSqoop_RecFilePathAndName));
	
	}//end run()
	
	private static int countExportTableRows (String sqoopExportToTableName) {
		ConfigureDBResources dcAmalgaDBConfigRes = new ConfigureDBResources ();
	    DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(dcAmalgaDBConfigRes.getEdwProjDevDBConParameters());
	   
		int tableRowCount = 0;
		try {
			Connection  conn = dbcon.getConnection();
			Statement stmt = conn.createStatement();
			
			String currExportTableCountSqlQueryStr = "SELECT count(*) \n"
		    		+ "FROM "+ sqlServerDBName + ".dbo." + sqoopExportToTableName + "; \n";
		    		     
		    System.out.println("\n--- currExportTableCountSqlQueryStr is : \n" + currExportTableCountSqlQueryStr);
		    
		    ResultSet rs = stmt.executeQuery(currExportTableCountSqlQueryStr);
		    
		    while ( rs.next( ) ) { 
		    	tableRowCount = rs.getInt(1);		    	
			}//end while
		    
		    rs.close();
			stmt.close( ); 
			conn.close( );			
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
		
		return tableRowCount;
	}//end countExportTableRows
	
	private static boolean trucateExportTable (String sqoopExportToTableName){
		ConfigureDBResources dcAmalgaDBConfigRes = new ConfigureDBResources ();
	    DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(dcAmalgaDBConfigRes.getEdwProjDevDBConParameters());
	   
	    boolean trucateSuccess = false;
		try {
			Connection  conn = dbcon.getConnection();
			Statement stmt = conn.createStatement();
			
			String truncateExportTableSqlQueryStr = "TRUNCATE TABLE "+ sqlServerDBName + ".dbo." + sqoopExportToTableName + "; \n";
		    
		    System.out.println("\n--- truncateExportTableSqlQueryStr is : \n" + truncateExportTableSqlQueryStr);
		    stmt.executeUpdate(truncateExportTableSqlQueryStr);	
		    stmt.close( ); 
			conn.close( );
			
		    int tableRowCount = countExportTableRows (sqoopExportToTableName);
		    if (tableRowCount == 0){
	    		trucateSuccess = true;
	    	}		    		
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
	    
		return trucateSuccess;
	}//end trucateExportTable
	
	private static String generateECGTestDataSqoopExportFullCmd (String hdfsSrcFolderPath, String sqoopExportToTableName) {
		ConfigureDBResources edwProjDevDBConfigRes = new ConfigureDBResources ();
		DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(edwProjDevDBConfigRes.getEdwProjDevDBConParameters());
		//String rdbServerMS = dbcon.getDatabaseMS();
		String rdbServerInstance = dbcon.getDatabaseName().replace("\\", ".mayo.edu\\\\");
		String connUsername = dbcon.getUsername();
		String connPassword = dbcon.getPassword();
		
		//1) Obtain sqoop command part #1 //EIMSQLPROD\\EDWSQLPROD ...ROPEIM802Q\\PROJDEV
	    //String sqoopCmdLeft_5Q = "/usr/bin/sqoop export --connect \"jdbc:sqlserver://ROPEIM802Q.mayo.edu\\\\PROJDEV;database=EDT_BigData\" --username TU05303 --password 80uijknm";
		String sqoopCmdLeft_5Q = "/usr/bin/sqoop export --connect "
	 		    + "\"jdbc:sqlserver://"	+ rdbServerInstance 
	    		+ ";database=" + sqlServerDBName
	    		+ "\" --username " + connUsername 
	    		+ " --password " + connPassword;
		
	
	    //System.out.println("\n--*1-- sqoopCmdLeft_5Q is : \n" + sqoopCmdLeft_5Q);
	   
	    //2) Obtain sqoop command part #2
	    //String sqoopCmdMiddle_5Q = "--table " + sqlServerDBName + ".dbo." + exportTableName;
	    String sqoopCmdMiddle_5Q = "--table " + sqoopExportToTableName;
	    //System.out.println("\n--*2-- sqoopCmdMiddle_5Q is : \n" + sqoopCmdMiddle_5Q);
	    
	    //3) Obtain sqoop command part #3	   
	    String sqoopCmdRight_5Q = "-m 1 --export-dir " + hdfsSrcFolderPath;
	    sqoopCmdRight_5Q += " --input-fields-terminated-by '\\" + "t' --input-escaped-by '\\' --input-enclosed-by '\"' --input-lines-terminated-by '\\" + "n'";
	    //System.out.println("\n--*3-- sqoopCmdRight_5Q is : \n" + sqoopCmdRight_5Q); //--input-lines-terminated-by \n  	       
	   
	    //4) Obtain sqoop full command in single line == parts #1-4 separated by " "
	    String sqoopFullCmd_5Q = sqoopCmdLeft_5Q + " " + sqoopCmdMiddle_5Q + " " + sqoopCmdRight_5Q; 
	    System.out.println("\n--*-- sqoopFullCmd_5Q is : \n" + sqoopFullCmd_5Q);
	    
	    return sqoopFullCmd_5Q;
	}//end generateECGTestDataSqoopExportFullCmd
	
	
	private static String generateECGTestDataSqoopImportFullCmd (String tempSqlQueryWhereClause, String importColumnListStr, String hdfsTgtFolderPath) {		
		ConfigureDBResources edwProjDevDBConfigRes = new ConfigureDBResources ();
		DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(edwProjDevDBConfigRes.getEdwProjDevDBConParameters());
		//String rdbServerMS = dbcon.getDatabaseMS();
		String rdbServerInstance = dbcon.getDatabaseName().replace("\\", ".mayo.edu\\\\");
		String connUsername = dbcon.getUsername();
		String connPassword = dbcon.getPassword();		
		
		//1) Obtain MsSql query String that is to be used in Sqoop command
	    //String sqoopMsSqlQueryStr = "SELECT amalgaMsgDtTmPosted, hl7MsgId, hl7MsgTimestamp, MsgType, AmalgaMsgText \n"
		//							+ "FROM EDT_BigData.dbo.archiveFile WITH (NOLOCK) \n"
		String sqoopMsSqlQueryStr = "SELECT " + importColumnListStr + " \n"		
	    		+ "FROM " + sqlServerDBName + ".dbo." + importTableName + " WITH (NOLOCK) \n"
	    		+ tempSqlQueryWhereClause
	    		+ "and \\$CONDITIONS \n";	
	    
	    System.out.println("\n--- sqoopMsSqlQueryStr is : \n" + sqoopMsSqlQueryStr);		    
	    sqoopMsSqlQueryStr = sqoopMsSqlQueryStr.replaceAll("\n", "");
	    //System.out.println("\n--*-- sqoopMsSqlQueryStr is : \n" + sqoopMsSqlQueryStr);
	    
	    //2) Obtain sqoop command part #1 //EIMSQLPROD\\EDWSQLPROD ...ROPEIM802Q\\PROJDEV
	    //String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect \"jdbc:sqlserver://ROPEIM802Q.mayo.edu\\\\PROJDEV;database=EDT_BigData\" --username TU05303 --password 80uijknm"; //vbfgrt45...80uijknm
	    String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect "
	 		    + "\"jdbc:sqlserver://"	+ rdbServerInstance 
	    		+ ";database=" + sqlServerDBName
	    		+ "\" --username " + connUsername 
	    		+ " --password " + connPassword;
	    
	   	    
	    //System.out.println("\n--*1-- sqoopCmdLeft_5Q is : \n" + sqoopCmdLeft_5Q);
	   
	    //3) Obtain sqoop command part #2
	    String sqoopCmdMiddle_5Q = "--query \"" + sqoopMsSqlQueryStr + "\"";
	    //System.out.println("\n--*2-- sqoopCmdMiddle_5Q is : \n" + sqoopCmdMiddle_5Q);
	    
	    //" --input-fields-terminated-by , --escaped-by \\ --input-enclosed-by '\"'";
	    
	    //4) Obtain sqoop command part #3	   
	    String sqoopCmdRight_5Q = "-m 1 --target-dir " + hdfsTgtFolderPath;
	    sqoopCmdRight_5Q += " --fields-terminated-by '\\" + "t' --escaped-by '\\' --enclosed-by '\"'";
	    //System.out.println("\n--*3-- sqoopCmdRight_5Q is : \n" + sqoopCmdRight_5Q);  	       
	   
	    //5) Obtain sqoop full command in single line == parts #1-4 separated by " "
	    String sqoopFullCmd_5Q = sqoopCmdLeft_5Q + " " + sqoopCmdMiddle_5Q + " " + sqoopCmdRight_5Q; 
	    System.out.println("\n--*-- sqoopFullCmd_5Q is : \n" + sqoopFullCmd_5Q);
	    
	    return sqoopFullCmd_5Q;
	}//end generateTestDataSqoopImportFullCmd

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
