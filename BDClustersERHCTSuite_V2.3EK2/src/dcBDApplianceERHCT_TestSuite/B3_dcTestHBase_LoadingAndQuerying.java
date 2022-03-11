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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import dcModelClasses.ConfigureDBResources;
import dcModelClasses.DatabaseConnectionFactory;
import dcModelClasses.DayClock;
import dcModelClasses.HBase;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/20-30/2014, 12/1-4/2014; 2/4/2015; 2/26/2015; 
*       1/6/2016 (Kerberos); 1/15/2016; 2/18/2016; 3/14/2016; 3/25/2016; 5/9-10/2017
*/ 


public class B3_dcTestHBase_LoadingAndQuerying {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localHBaseTestDataFileName = "";
	private static String hbaseTestFolderName = "";
	private static boolean externalHBaseTestStatus = false;
	private static String externalHbaseTableName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/sqoop/bin/sqoop (<=TDH2.1.11) ==> /usr/bin/sqoop or /usr/hdp/2.3.4.0-3485/sqoop/bin/sqoop (TDH2.3.4)
	// /usr/lib/hive/bin/hive (<=TDH2.1.11) ==> /usr/bin/hive or /usr/hdp/2.3.4.0-3485/hive/bin/hive (TDH2.3.4)
	// /usr/lib/pig/bin/pig (<=TDH2.1.11) ==> /usr/bin/pig or /usr/hdp/2.3.4.0-3485/pig/bin/pig (TDH2.3.4)
	// /usr/lib/ (<=TDH2.1.11) ==> /usr/bin/ or  /usr/hdp/2.3.4.0-3485/ (TDH2.3.4)
	//--username TU05303 --password vbfgrt45 ==> --username TU05303 --password 80uijknm (06/13/2016)


	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for HBase-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localHBaseTestDataFileName = args[4];
		hbaseTestFolderName = args[5];
		internalKinitCmdStr = args[9];
		
		if (!hbaseTestFolderName.endsWith("/")){
			hbaseTestFolderName += "/";
		}
		
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
							
		run();
	}//end main
	
	@SuppressWarnings("deprecation")
	public static void run() throws Exception {
		//1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//2. Prepare files for testing records
		String hbaseScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "hbase\\";
	    prepareFolder(hbaseScriptFilesFoder, "Local HBase Testing Script Files");
	        
		String dcTestHBase_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestHBase_TableCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestHBase_RecFilePathAndName,  "Records of Testing HBase on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated HBase/Hcat Internal and External (Java Client) Table Disabling/Dropping, Creating, Loading and Querying Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecHeader, false);		
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
		String hbaseTestFolderName = "/user/" + loginUser4AllNodesName + "/test/HBase/";
						
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
		//String hbaseInternalPrincipal = currBdCluster.getHbaseInternalPrincipal();
		//String hbaseInternalKeyTabFilePathAndName = currBdCluster.getHbaseInternalKeyTabFilePathAndName();		
		//String loginUserName = "";
		//loginUserName = "ambari-qa"; //Local Kerberos			
		//String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		//loginUserName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		//System.out.println("*** loginUserName is: " + loginUserName);
				
		String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
		String hbaseShellInitiateStr = " | hbase shell";
		String hcatShellInitiateStr = "hcat -e ";
		String pigShellInitiateStr = "/usr/bin/pig -stop_on_failure -useHCatalog -e ";
		
		DayClock tempClock = new DayClock();
		String tempTime = "";
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();

		//4. Loop through bdClusterEntryNodeList to internally disable/drop existing HBase table, create
		//     a HBase table, load data into the HBase table by HBase Shell, and query data in the HBase table (counting rows) 		
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//int clusterENNumber = bdClusterEntryNodeList.size();
		//DayClock tempClock = new DayClock();				
		//clusterENNumber_Start = 2; //0..1..2..3..4..5..6..7..8..9
	    //clusterENNumber = 3; //1..2..3..4..5..6
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "[1]. HBase Table Internal Operation \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HBase Table on Entry Node: " + tempENName);
			
			//(1) Generate HBase Shell Testing Commands .txt File with test data - dcTestHBase_TableDisableDrop_Create_LoadCommands.txt			
		    String LocalWindowsHBaseShellCmdsFileName = "dcTestHBase_TableDisableDrop_Create_LoadCommands_" + (i+1) + ".txt";
			String LocalWindowsHBaseShellCmdsFilePathAndName = hbaseScriptFilesFoder + LocalWindowsHBaseShellCmdsFileName;
			prepareFile (LocalWindowsHBaseShellCmdsFilePathAndName,  "HBase Shell Commands of Testing HBase on '" + bdClusterName + "' Cluster");
				
			String hbaseTableName = "employee" + (i+1);
			generateHBaseShellTestingCommandsFile (LocalWindowsHBaseShellCmdsFilePathAndName, hbaseTableName);
			
			//Desktop.getDesktop().open(new File(LocalWindowsHBaseShellCmdsFilePathAndName));			
						

			//(2) Copy HBase Shell Testing Commands .txt File to Entry Node /home/hdfs/folder
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
			
			
			LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(LocalWindowsHBaseShellCmdsFileName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
						
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Copying HBase Shell Testing Commands .txt File to Entry Node - " + tempENName + " of '" + bdClusterName + "' Cluster at the time - " + tempTime); 	
			
			//(3) Testing HBase Table disabling & dropping, creating, data loading and querying (counting)
			//String enLocalTableRowCountFilePathAndName = "/home/hdfs/tempTableRowCount.txt";
			String enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";
			
			//hbase shell ./dcTestHBase_TableDisableDrop_Create_Load_QueryCommands.txt;
			//hbase shell /home/hdfs/dcTestHBase_TableDisableDrop_Create_Load_QueryCommands.txt;
			String enHBaseShellTestingCmdsFilePathAndName = enServerScriptFileDirectory + LocalWindowsHBaseShellCmdsFileName;
			String runHBaseShellTestingCmdsFileCmd = "hbase shell " + enHBaseShellTestingCmdsFilePathAndName;			
			
			//echo "count 'employee1'" | hbase shell | grep 'row(s)' > /home/hdfs/tempTableRowCount.txt			
			String hbaseTableRowCountQueryCmd = "echo \"count '" + hbaseTableName + "'\" | hbase shell | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
			
			String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + "hbaseTable_" + hbaseTableName + "_RowCount.txt";
			hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 30; \n");
			
		    sb.append("hadoop fs -mkdir -p " + hbaseTestFolderName + "; \n");
		    //sb.append("hadoop fs -chown hdfs:bduser " + hbaseTestFolderName + "; \n");		   
		    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //Local Kerberos
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + hbaseTestFolderName + "; \n");
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("sudo su - ambari-qa;\n");	//Not a must
			//sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
			//sb.append("cd " + enServerScriptFileDirectory + ";\n"); //Not a must
		    //sb.append("kinit  " + hbaseInternalPrincipal + " -kt " + hbaseInternalKeyTabFilePathAndName +"; \n");	//Local Kerberos		
		   	
		    sb.append(runHBaseShellTestingCmdsFileCmd + ";\n");	  	   
		    sb.append(hbaseTableRowCountQueryCmd + ";\n");	
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos	
		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");
		    //sb.append("rm -f " + enHBaseShellTestingCmdsFilePathAndName + "; \n");		    
		    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
					    
		    String hbaseTableTestScriptFullFilePathAndName = hbaseScriptFilesFoder + "dcTestHBase_ManagedTableScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (hbaseTableTestScriptFullFilePathAndName,  "Script File For Testing HBase Managed Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String hbaseManagedTestingCmds = sb.toString();
			writeDataToAFile(hbaseTableTestScriptFullFilePathAndName, hbaseManagedTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(hbaseTableTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(hbaseTableTestScriptFullFilePathAndName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
					
			Path filePath = new Path(hdfsTableRowCountFilePathAndName);
			int hbaseTableRowCount = 0;
			if (currHadoopFS.exists(filePath)) {
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";			
				while ((line = br.readLine()) != null) {				
					if (line.contains("6 row(s)")) {
						System.out.println("*** Row Counts # in HBase Table - " + hbaseTableName + " Is: " + 6 );
						hbaseTableRowCount = 6;
						break;
					}								
				}//end while
				br.close();
			} else {
				hbaseTableRowCount = 0;
			}			
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (By HBase Shell), and Querying a HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying (Counting) generated HBase Table - '" + hbaseTableName + "' has a Row Count:  '" + hbaseTableRowCount + "'\n";	 
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (By HBase Shell), and Querying a HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying (Counting) generated HBase Table - '" + hbaseTableName + "' has a Row Count:  '" + hbaseTableRowCount + "'\n";	 
			}
			writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for
	

		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//5. Loop through bdClusterEntryNodeList to internally disable/drop existing HBase table, create
		//     a HBase table, load data into the HBase table by Sqoop Importing, and query data in the HBase table (counting rows) 		
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//int clusterENNumber = bdClusterEntryNodeList.size();		
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "\n[2]. HBase Table & Sqoop Importing  \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HBaseTable on Entry Node: " + tempENName);
			
			//Testing HBase Table disabling, dropping, creating, data-loading (sqoop-importing) and querying
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
			
			
			String hbaseForSqoopTableName = "ecg" + (i+1);			
			String singleColmnFamilyName = "cf1";			
			//String hbaseShellInitiateStr = " | hbase shell";
			String disableHBaseTableCmd = "echo \"disable '"+ hbaseForSqoopTableName + "'\"" + hbaseShellInitiateStr;
			String dropHBaseTableCmd = "echo \"drop '"+ hbaseForSqoopTableName + "'\"" + hbaseShellInitiateStr;		
			String createHBaseTableCmd = "echo \"create '"+ hbaseForSqoopTableName + "',{NAME => '" + singleColmnFamilyName + "', VERSIONS => 5}\"" + hbaseShellInitiateStr;
			
			String sqlServerDBName = "EDT_BigData";
			String importTableName = "sqoopExported";
			String hbaseTableRowKey = "hl7MsgId";
			String sqoopImportToHBaseForSqoopTableCmd = generateECGTestDataSqoopImportToHBaseFullCmd (sqlServerDBName, 
					importTableName, hbaseForSqoopTableName, singleColmnFamilyName, hbaseTableRowKey);			
			
			String enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";
			String hbaseTableRowCountQueryCmd = "echo \"count '"+ hbaseForSqoopTableName + "'\"" + hbaseShellInitiateStr + " | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
			String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + "hbaseForSqoopTable_" + hbaseForSqoopTableName + "_RowCount.txt";
			hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
			String hbaseHdfsHomeFolder = "/user/hbase";
			
			
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 30; \n");
			
		    sb.append("hadoop fs -mkdir -p " + hbaseHdfsHomeFolder + "; \n");
		    //sb.append("hadoop fs -chown -R hbase:bdadmin " + hbaseHdfsHomeFolder + "; \n");	
		    //sb.append("hadoop fs -chmod -R 750 " + hbaseHdfsHomeFolder + "; \n");
		    
		    sb.append("hadoop fs -mkdir -p " + hbaseTestFolderName + "; \n");
		    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //Local Kerberos
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
		    //sb.append("hadoop fs -chmod -R 750 " + hbaseTestFolderName + "; \n");
		   		    
		    //sb.append("kdestroy;\n");
		    //sb.append("sudo su - ambari-qa;\n"); //Not a must
			//sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
			//sb.append("cd " + enServerScriptFileDirectory + ";\n"); //Not a must
		    //sb.append("kinit  " + hbaseInternalPrincipal + " -kt " + hbaseInternalKeyTabFilePathAndName +"; \n");	//Local Kerberos		
		    
		    sb.append(disableHBaseTableCmd + ";\n");	
		    sb.append(dropHBaseTableCmd + ";\n");	    
		    sb.append(createHBaseTableCmd + ";\n");		   
		    sb.append(sqoopImportToHBaseForSqoopTableCmd + ";\n");					    
		    sb.append(hbaseTableRowCountQueryCmd + ";\n");
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos		    
			
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");		     
		    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		    		    
		    String hbaseForSqoopTableTestScriptFullFilePathAndName = hbaseScriptFilesFoder + "dcTestHBase_SqoopImportHBaseForSqoopTableScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (hbaseForSqoopTableTestScriptFullFilePathAndName,  "Script File For Testing HBase ForSqoop Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String hbaseForSqoopTestingCmds = sb.toString();
			writeDataToAFile(hbaseForSqoopTableTestScriptFullFilePathAndName, hbaseForSqoopTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(hbaseForSqoopTableTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(hbaseForSqoopTableTestScriptFullFilePathAndName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
					
			Path filePath = new Path(hdfsTableRowCountFilePathAndName);
			int hbaseTableRowCount = 0;
			if (currHadoopFS.exists(filePath)) {
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";			
				while ((line = br.readLine()) != null) {				
					if (line.contains("1")) {
						System.out.println("*** Row # in HBase-ForSqoop Table Is: " + 1 );
						hbaseTableRowCount = 1;
						break;
					}								
				}//end while
				br.close();
			} else {
				hbaseTableRowCount = 0;
			}
									
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"						
								+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Sqoop-Importing), and Querying a HBase Table"
								+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
								+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
								+ "\n  --(2) Querying generated HBase for-Sqoop-importing Table - '" + hbaseForSqoopTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Sqoop-Importing), and Querying a HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  --(2) Querying generated HBase for-Sqoop-importing Table - '" + hbaseForSqoopTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			}
			writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for 5.	
		//successTestScenarioNum = 20L;
		//totalTestScenarioNumber = 20;
		
		
		HBase aHBase = new HBase(bdClusterName);	        
		String exportHadoopClassPath = aHBase.getExportHadoopClassPath_ForHBaseStr();
		String setHiveAuxJarsPathStr = aHBase.getSetHiveAuxJarsPath_ForHBaseStr();
		String exportPigClassPath = aHBase.getExportPigClassPath_ForHBaseStr();
		String exportPigOpts = aHBase.getExportPigOpts_ForHBaseStr();
		String exportHcatHome = aHBase.getExportHcatHome_ForHBaseStr();
		String pigRegisterHBaseJarsForHcatStr = aHBase.getPigRegisterHBaseJarsForHcatStr();
		String exportHcatClassPath = exportPigClassPath.replace("PIG_CLASSPATH", "HCAT_CLASSPATH"); //For TDH2.3.4	
		
		System.out.println(" *** ExportHadoopClassPath_ForHBaseStr: " + exportHadoopClassPath);
        System.out.println(" *** SetHiveAuxJarsPath_ForHBaseStr: " + setHiveAuxJarsPathStr);       
        System.out.println(" *** ExportPigClassPath_ForHBaseStr: " + exportPigClassPath);
        System.out.println(" *** ExportPigOpts_ForHBaseStr: " + exportPigOpts);
        System.out.println(" *** exportHcatHome: " + exportHcatHome);
        System.out.println(" *** pigRegisterHBaseJarsForHcatStr: " + pigRegisterHBaseJarsForHcatStr);
        System.out.println(" *** exportHcatClassPath: " + exportHcatClassPath);//For TDH2.3.4
        
		String setHiveAuxJarsPathCmd = hiveShellInitiateStr + "\"" + setHiveAuxJarsPathStr + "\"";
		String hdfsHBaseTestDataFilePathAndName = hbaseTestFolderName +  "dcHBaseTestData_employee.txt"; //changed from /data/test/HBase/dcHBaseTestData_employee.txt....	
		
		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//6. Loop through bdClusterEntryNodeList to internally disable/drop existing HBase table, create
		//     a HBase table, load data into the HBase table by Hive-HBase Integration, and query data in the HBase table (counting rows)
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6	
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "\n[3]. Hive-HBase Integration \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HBaseTable on Entry Node: " + tempENName);
			
			//Methods: First, generate a Hive-external table and store test data in the Hive-external table - (1) & (2)
			//    Second, load Hive-external table data into a Hive-managed HBase Table - (3)
			//(1) Move test data file to HDFS by external HDFS-writing
			String localWinSrcHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
			//System.out.println("\n*** localWinSrcHBaseTestDataFilePathAndName: " + localWinSrcHBaseTestDataFilePathAndName);
						
			//System.out.println("\n*** hdfsHBaseTestDataFilePathAndName: " + hdfsHBaseTestDataFilePathAndName);
			
			moveWindowsLocalHBaseTestDataToHDFS (localWinSrcHBaseTestDataFilePathAndName, hdfsHBaseTestDataFilePathAndName, currHadoopFS);
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Generated Hive-Managed HBase Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
					+ hdfsHBaseTestDataFilePathAndName + " at the time - " + tempTime);
			
			//(2). Create (IF NOT EXISTS) and load test data into a Hive-external table     
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
			
			String hiveExternalTableName = "employee_hs_" + (i+1);
			
			String createHiveTableStr = "create external table IF NOT EXISTS " + hiveExternalTableName + "( \n"
					+ "employeeId Int, \n"
					+ "firstName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ " address String \n"
					+ ")Row format delimited fields terminated by ',' \n"
					+ "Location '" + hbaseTestFolderName + hiveExternalTableName + "' ";
			
			hdfsFilePathAndNameList.add(hbaseTestFolderName + hiveExternalTableName);				
			
			//String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
			//String dropHiveTableCmd = hiveShellInitiateStr + "\" drop table " + hiveExternalTableName + "\"";			
			String createHiveTableCmd = hiveShellInitiateStr + "\"" + createHiveTableStr.replaceAll("\n", "") + "\"";			
			String loadDataToHiveTableCmd = hiveShellInitiateStr + "\"load data inpath '" + hdfsHBaseTestDataFilePathAndName + "' overwrite into table " + hiveExternalTableName + "\"";
			
			String enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";			
						
			String hiveTableRowCountQueryCmd = hiveShellInitiateStr + "\"select count(*) from " + hiveExternalTableName + "\" > " + enLocalTableRowCountFilePathAndName;
			String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + "hiveTable_No" + (i+1) + "_RowCount.txt";
			hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
			
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
		    //sb.append("hadoop fs -mkdir -p " + hbaseTestFolderName + "; \n");
		    //sb.append("hadoop fs -chown hdfs:bduser " + hbaseTestFolderName + "; \n");
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 30; \n");
			
		    //sb.append("hadoop fs -chmod -R 750 " + hbaseTestFolderName + "; \n");		    
		    //sb.append(dropHiveTableCmd + ";\n");		    
		    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //Local Kerberos
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("sudo su - ambari-qa;\n"); //Not a must
			//sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
		    //sb.append("kdestroy;\n");
		    //sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos	
		    
		    sb.append(createHiveTableCmd + ";\n");		   
		    sb.append(loadDataToHiveTableCmd + ";\n");
		    sb.append(hiveTableRowCountQueryCmd + ";\n");
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");		     
		    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    
		    		    
		    String hiveLoadHBaseTableTestScriptFullFilePathAndName = hbaseScriptFilesFoder + "dcTestHive_LoadingDataInto_HBaseTableScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (hiveLoadHBaseTableTestScriptFullFilePathAndName,  "Script File For Testing Hive Loading Data Into HBase Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String hiveLoadHBaseTableTestingCmds = sb.toString();
			writeDataToAFile(hiveLoadHBaseTableTestScriptFullFilePathAndName, hiveLoadHBaseTableTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(hiveManagedTableTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(hiveLoadHBaseTableTestScriptFullFilePathAndName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			
			Path filePath = new Path(hdfsTableRowCountFilePathAndName);
			int hiveTableRowCount = 0;
			if (currHadoopFS.exists(filePath)) {
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";
				//int hiveTableRowCount = 0;
				while ((line = br.readLine()) != null) {				
					if (line.contains("6")) {
						System.out.println("*** Row # in Hive-External Table for Loading into HBase Table Is: " + 6 );
						hiveTableRowCount = 6;
						break;
					}								
				}//end while
				br.close();
			}			
			
			//(3) Loading test data into a Hive-managed HBase Table	from the above Hive-external table using Hive-HBase Integration
			      
			String hiveTableName_forManagingHBaseTable = "employee_hmhb_"+ (i+1);			
			String hiveManagedHBaseTableName = "hive_managed_employee" + (i+1);
			
			int hbaseTableRowCount = 0;		
			if (hiveTableRowCount != 0) {
				//if (hbaseVersion.contains("0.94")){
				//	exportHadoopClassPath = "export HADOOP_CLASSPATH=/etc/hbase/conf:/usr/lib/hbase/hbase-0.94.6.1.3.2.0-111-security.jar:/usr/lib/zookeeper/zookeeper.jar";
				//	setHiveAuxJarsPathStr = "SET hive.aux.jars.path=file:///etc/hbase/conf/hbase-site.xml,file:///usr/lib/hive/lib/hive-hbase-handler-0.11.0.1.3.2.0-110.jar,file:///usr/lib/hbase/hbase-0.94.6.1.3.2.0-110-security.jar,file:///usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-110.jar, file:///usr/lib/hive/lib/guava-11.0.2.jar";
				//}				
				
				String singleColmnFamilyName = "cf1";			
				//String hbaseShellInitiateStr = " | hbase shell";
				String disableHBaseTableCmd = "echo \"disable '"+ hiveManagedHBaseTableName + "'\"" + hbaseShellInitiateStr;
				String dropHiveTable_ForManagingHBaseTableCmd = hiveShellInitiateStr + "\" drop table " + hiveTableName_forManagingHBaseTable + "\"";			
				//String dropHBaseTableCmd = "echo \"drop '"+ hiveManagedHBaseTableName + "'\"" + hbaseShellInitiateStr;	
				
				String hbaseColumnsMappingStr = ":key," + singleColmnFamilyName + ":fistName," 
												+ singleColmnFamilyName + ":lastName," + singleColmnFamilyName + ":salary," 
												+ singleColmnFamilyName + ":gender,"+ singleColmnFamilyName + ":address";			
				String createHiveTable_ForManagingHBaseTableStr = "create table " + hiveTableName_forManagingHBaseTable + "( \n"
						+ "employeeId Int, \n"
						+ "firstName String, \n"
						+ "lastName String, \n"
						+ "salary Int, \n"
						+ "gender String, \n"
						+ "address String \n"
						+ ")Row format delimited fields terminated by ',' \n"
						//+ "Location '" + hbaseTestFolderName + hiveTableName_forManagingHBaseTable + "' \n" //This line is not working
						+ "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' \n"
						+ "WITH SERDEPROPERTIES ('hbase.columns.mapping' = '" + hbaseColumnsMappingStr + "') \n"
						+ "TBLPROPERTIES ('hbase.table.name' = '" + hiveManagedHBaseTableName + "')" ;
					
				hdfsFilePathAndNameList.add(hbaseTestFolderName + hiveTableName_forManagingHBaseTable);			
				createHiveTableCmd = hiveShellInitiateStr + "\"" + createHiveTable_ForManagingHBaseTableStr.replaceAll("\n", "") + "\"";
				
				//String setHiveAuxJarsPathCmd = hiveShellInitiateStr + "\"" + setHiveAuxJarsPathStr + "\"";
				
				String hiveLoadHBaseTableStr = "INSERT OVERWRITE TABLE " + hiveTableName_forManagingHBaseTable +  " SELECT * FROM " + hiveExternalTableName; 
				String hiveLoadHBaseTableCmd =  hiveShellInitiateStr + "\"" + hiveLoadHBaseTableStr + "\"&\n" + "sleep 30";
				
				
				//date +"%T";
				// /usr/bin/hive -e "INSERT OVERWRITE TABLE  emp1 select * from employee_sd"&
				//sleep 75;
				//date +"%T";
				//ps -ef | grep 'hdfs' | grep 'hive' | cut -f6 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
				//ps -ef | grep 'hdfs' | grep 'hive' | cut -f7 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
				//ps -ef | grep 'hdfs' | grep 'hive' | cut -f8 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
				//su - hdfs;
						
				
				enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";
				//String createEnLocalTableRowCountFileFileCmd = "touch " + enLocalTableRowCountFilePathAndName;
								
				//String hbaseTableRowCountQueryCmd = hiveShellInitiateStr + "\"select count(*) from " + hiveTableName_forManagingHBaseTable + "\" > " + enLocalTableRowCountFilePathAndName;
				String hbaseTableRowCountQueryCmd = "echo \"count '"+ hiveManagedHBaseTableName + "'\"" + hbaseShellInitiateStr + " | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
				
				hdfsTableRowCountFilePathAndName = hbaseTestFolderName + "HiveHBase_Integrated_" + hiveManagedHBaseTableName + "_RowCount.txt";
				hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
				
				String killLoadProc = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
				//String killLoadProc1 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f6 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
				//String killLoadProc2 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f7 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
				//String killLoadProc3 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
				String grepHiveProcCmd = "ps -ef | grep 'hdfs' | grep 'hive'| grep -v grep";
					
				
			    //sb.append("hadoop fs -mkdir -p " + hbaseTestFolderName + "; \n");
			    //sb.append("hadoop fs -chown hdfs:bduser " + hbaseTestFolderName + "; \n");			  
				
				//sb.append("sudo su - hdfs;\n");
				//sb.append("cd " + enServerScriptFileDirectory + ";\n");
				//sb.append("kdestroy;\n");
				//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");
				//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
				//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
				
				//sb.append("sudo su - " + loginUserName + ";\n");
				sb.append("cd " + enServerScriptFileDirectory + ";\n");			
				sb.append("kdestroy;\n");
				//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
				//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
				sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
				//sb.append("sleep 30; \n");
				
			    //sb.append("hadoop fs -chmod -R 750 " + hbaseTestFolderName + "; \n");
			    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //Local Kerberos
	    		//sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
	    		
	    		//sb.append("kdestroy;\n");			    
			    ////sb.append("sudo su - ambari-qa;\n");
				//sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
				//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			    //sb.append("kdestroy;\n");
			    ////sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
			    //sb.append("kinit  " + hbaseInternalPrincipal + " -kt " + hbaseInternalKeyTabFilePathAndName +"; \n"); //Enterprise-Kerberos				
			    
	    		sb.append(exportHadoopClassPath + ";\n");
			    sb.append(disableHBaseTableCmd + ";\n");
			    sb.append(dropHiveTable_ForManagingHBaseTableCmd + ";\n");
			    //sb.append(dropHBaseTableCmd + ";\n"); //This is just for "to be on the safe-side"		    
			    sb.append(createHiveTableCmd + ";\n");
			    
			    sb.append(setHiveAuxJarsPathCmd + ";\n");		    
			    sb.append(hiveLoadHBaseTableCmd + ";\n");	 
			   		    				    
			    sb.append(hbaseTableRowCountQueryCmd + ";\n");
			    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
			    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
			    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");	
			    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
			    sb.append(killLoadProc + ";\n");	
			    sb.append(grepHiveProcCmd + ";\n");	
			    sb.append("kdestroy;\n");						   
			    
			    String hiveLoadHBaseTableTestScriptFullFilePathAndName2 = hbaseScriptFilesFoder + "dcTestHive_LoadingDataInto_HBaseTableScriptFile2_No"+ (i+1) + ".sh";			
				prepareFile (hiveLoadHBaseTableTestScriptFullFilePathAndName2,  "Script File #2 For Testing Hive Loading Data Into HBase Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
				
				String hiveLoadHBaseTableTestingCmds2 = sb.toString();
				writeDataToAFile(hiveLoadHBaseTableTestScriptFullFilePathAndName2, hiveLoadHBaseTableTestingCmds2, false);		
				sb.setLength(0);
				
				//Desktop.getDesktop().open(new File(hbaseForSqoopTableTestScriptFullFilePathAndName));			
				LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(hiveLoadHBaseTableTestScriptFullFilePathAndName2, 
						hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
					
				Path filePath2 = new Path(hdfsTableRowCountFilePathAndName);				
				if (currHadoopFS.exists(filePath2)) {
					FileStatus[] status2 = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));	
					System.out.println("\n*** hdfsHiveMangedTableRowCountFilePathAndName: " + hdfsTableRowCountFilePathAndName + "\n" );
					
					BufferedReader br2 = new BufferedReader(new InputStreamReader(currHadoopFS.open(status2[0].getPath())));			
					String line = "";
					while ((line = br2.readLine()) != null) {				
						if (line.contains("6 row(s)")) {
							System.out.println("*** Row # in Hive-Managed HBase Table Is: " + 6 );
							hbaseTableRowCount = 6;
							break;
						}								
					}//end while
					br2.close();
				}				
			}//end outer if for hiveTableRowCount 
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Hive-HBase Integration) and Querying Hive-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Hive-Managed HBase Table - '" + hiveManagedHBaseTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Hive-HBase Integration) and Querying Hive-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Hive-Managed HBase Table - '" + hiveManagedHBaseTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			}
			writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for
		
		
		
		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//7. Loop through bdClusterEntryNodeList to internally disable/drop existing HBase table, create
	    // a HBase table, load data into the HBase table by Hive-Generated-HFile, and query data in the HBase table (counting rows)
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//int clusterENNumber = bdClusterEntryNodeList.size();				
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6	
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "\n[4]. HBase Table & Hive-Generated-HFile \n", true);
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			//(1) Preparation & Move test data file to HDFS by external HDFS-writing
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HBaseTable on Entry Node: " + tempENName);
			
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
												
			
			String localWinSrcHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
			//System.out.println("\n*** localWinSrcHBaseTestDataFilePathAndName: " + localWinSrcHBaseTestDataFilePathAndName);
			//String hdfsHBaseTestDataFilePathAndName = "/data/test/HBase/dcHBaseTestData_employee.txt"; 	
			//System.out.println("\n*** hdfsHBaseTestDataFilePathAndName: " + hdfsHBaseTestDataFilePathAndName);
			
			moveWindowsLocalHBaseTestDataToHDFS (localWinSrcHBaseTestDataFilePathAndName, hdfsHBaseTestDataFilePathAndName, currHadoopFS);
			hdfsFilePathAndNameList.add(hdfsHBaseTestDataFilePathAndName);
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();		
			System.out.println("\n*** Done - Generated Hive-HFile Loading HBase Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
					+ hdfsHBaseTestDataFilePathAndName + " at the time - " + tempTime);
			
			//(2) Loading test data into a HCatalog generated Hive-External table, 
			//    then generate HFiles in "hfile.family.path" through a hive table,
			// and finally load HFiles into a HBase table using completebulkload methods either throug hbase or hadoop...
			
			//String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
	        //String hbaseShellInitiateStr = " | hbase shell";
			//String hcatShellInitiateStr = "hcat -e ";
			//String pigShellInitiateStr = "/usr/bin/pig -stop_on_failure -useHCatalog -e ";
			//String setHiveAuxJarsPathCmd = hiveShellInitiateStr + "\"" + setHiveAuxJarsPathStr + "\"";
			
			String hcatTableName_forHFilesGenerating = "employee_hcat_"+ (i+1);
			String hcatTableName_forHFilesStoring = "employee_hfiles_"+ (i+1);	
			String hbaseTableName_forloadingHFiles = "hive_hfile_employee" + (i+1);
				
			//(a)
			String dropHcatTableName_forHFilesGeneratingCmd = hcatShellInitiateStr + "\"drop table " + hcatTableName_forHFilesGenerating + "\"";			
			String createHcatTable_forHFilesGeneratingStr = "create table " + hcatTableName_forHFilesGenerating + "( \n"
					+ "employeeId Int, \n"
					+ "firstName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ "address String \n"
					+ ") "
					+ "Row format delimited fields terminated by ',' \n"
					+ "STORED AS textfile \n"
					+ "Location '" + hbaseTestFolderName + hcatTableName_forHFilesGenerating + "'" ;
			hdfsFilePathAndNameList.add(hbaseTestFolderName + hcatTableName_forHFilesGenerating );
			String createHcatTable_forHFilesGeneratingCmd = hcatShellInitiateStr + "\"" + createHcatTable_forHFilesGeneratingStr.replaceAll("\n", "") + "\"";
			String loadHcatTable_forHFilesGeneratingStr = "load data inpath '"+ hdfsHBaseTestDataFilePathAndName + "' overwrite into table " + hcatTableName_forHFilesGenerating;
			String loadHcatTable_forHFilesGeneratingCmd = hiveShellInitiateStr + "\"" + loadHcatTable_forHFilesGeneratingStr + "\"";
			
			
			//(b)
			String singleColmnFamilyName = "cf1";
			//##SET hive.execution.engine=mr;
			//##cat -e "create table employee_hfiles_1( employeeId Int, firstName String, lastName String, salary Int, gender String, address String )
			//##Row format delimited fields terminated by ',' 
			////Location Location '/data/test/HBase/employee_hfiles_1' ==>
			//##STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.hbase.HiveHFileOutputFormat' 
			//##TBLPROPERTIES ('hfile.family.path' = '/data/test/HBase/employee_hfiles_1/cf1');"
					
			String dropHcatTableName_forHFilesStoringCmd = hcatShellInitiateStr + "\"drop table " + hcatTableName_forHFilesStoring + "\"";			
			String createHcatTable_forHFilesStoringStr = "create table " + hcatTableName_forHFilesStoring + "( \n"
					+ "employeeId Int, \n"
					+ "firstName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ "address String \n"
					+ ") "
					+ "Row format delimited fields terminated by ',' \n"
					+ "STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.hbase.HiveHFileOutputFormat' \n"
					+ "TBLPROPERTIES ('hfile.family.path' = '" + hbaseTestFolderName + hcatTableName_forHFilesStoring + "/" + singleColmnFamilyName + "');";
					//+ "STORED AS textfile \n"
					//+ "Location '" + hbaseTestFolderName + hcatTableName_forHFilesStoring + "'" ;
			
			hdfsFilePathAndNameList.add(hbaseTestFolderName + hcatTableName_forHFilesStoring );
			String createHcatTable_forHFilesStoringCmd = hcatShellInitiateStr + "\"" + createHcatTable_forHFilesStoringStr.replaceAll("\n", "") + "\"";
			
			
		    String hiveConfForHiveAuxJarsPathStr = setHiveAuxJarsPathStr.replace("SET ", "--hiveconf "); 
		    String hiveConfStr = "--hiveconf hive.mapred.partitioner=org.apache.hadoop.mapred.lib.TotalOrderPartitioner "
		    		+ "--hiveconf hive.execution.engine=mr "
		    		+ "--hiveconf mapred.reduce.tasks=1 "
		    		+ hiveConfForHiveAuxJarsPathStr; //+ "--hiveconf hive.aux.jars.path=...";		    		
		    
		    		
			String hiveGenHFiles_forHFilesStoringStr = "INSERT OVERWRITE TABLE " + hcatTableName_forHFilesStoring + " SELECT * FROM " + hcatTableName_forHFilesGenerating + " CLUSTER BY employeeId";
			String hiveGenHFiles_forHFilesStoringCmd = hiveShellInitiateStr + "\"" + hiveGenHFiles_forHFilesStoringStr + "\" " + hiveConfStr;
			
			
			//(c)		
						
			String disableHBaseTableCmd = "echo \"disable '"+ hbaseTableName_forloadingHFiles + "'\"" + hbaseShellInitiateStr;
			String dropHbaseTableName_forloadingHFilesCmd = "echo \"drop '"+ hbaseTableName_forloadingHFiles + "'\"" + hbaseShellInitiateStr;
			String createHbaseTableName_forloadingHFilesStr = "create '" + hbaseTableName_forloadingHFiles + "',{NAME => '" + singleColmnFamilyName + "', VERSIONS => 5}";
			String createHbaseTableName_forloadingHFilesCmd = "echo \""+ createHbaseTableName_forloadingHFilesStr + "\"" + hbaseShellInitiateStr;
			String loadHFilesToHBaseTableCmd = "hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles " + hbaseTestFolderName + hcatTableName_forHFilesStoring + " " + hbaseTableName_forloadingHFiles;
			//String loadHFilesToHBaseTableCmd = "hadoop jar /usr/lib/hbase/hbase-0.94.6.1.3.2.0-110-security.jar completebulkload " + hbaseTestFolderName + hcatTableName_forHFilesStoring + " " + hbaseTableName_forloadingHFiles;
			
			
			String enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";
			//String createEnLocalTableRowCountFileFileCmd = "touch " + enLocalTableRowCountFilePathAndName;
							
			String hbaseTableRowCountQueryCmd = "echo \"count '"+ hbaseTableName_forloadingHFiles + "'\"" + hbaseShellInitiateStr + " | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
			String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + hbaseTableName_forloadingHFiles + "_RowCount.txt";
			hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
			
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");		
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 30; \n");
			
			//sb.append("hadoop fs -chmod -R 757 " + hbaseTestFolderName + "; \n");		    
		    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //Local Kerberos
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
		    //sb.append(setHiveAuxJarsPathCmd + ";\n");		   
		    //sb.append(exportPigClassPath + ";\n");
		    //sb.append(exportPigOpts + ";\n");	    
		    
		    //sb.append("kdestroy;\n");
		    ////sb.append("sudo su - ambari-qa;\n");
		    //sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
		    //sb.append("cd " + enServerScriptFileDirectory + ";\n");
		    //sb.append("kdestroy;\n");
		    ////sb.append("kinit  " + ambariQaInternalPrincipal + " -kt " + ambariInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos
		    //sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
		    
		    sb.append(exportHadoopClassPath + ";\n");
			sb.append(dropHcatTableName_forHFilesStoringCmd + ";\n");		   
		    sb.append(createHcatTable_forHFilesStoringCmd + ";\n");		    
		    sb.append(dropHcatTableName_forHFilesGeneratingCmd + ";\n");
		    sb.append(createHcatTable_forHFilesGeneratingCmd + ";\n");
		    sb.append(loadHcatTable_forHFilesGeneratingCmd + ";\n");		    
		    ////sb.append(dropHcatTableName_forHFilesStoringCmd + ";\n");		   
		    ////sb.append(createHcatTable_forHFilesStoringCmd + ";\n");	
	    	//sb.append("hive -e \"SET hive.execution.engine=mr;\";\n");	
	    	//sb.append("hive -e \"SET mapred.reduce.tasks=1\";\n");	
	    	//sb.append("hive -e \"SET hive.mapred.partitioner=org.apache.hadoop.mapred.lib.TotalOrderPartitioner\";\n");
		    sb.append(hiveGenHFiles_forHFilesStoringCmd + ";\n");	
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");		
		    //sb.append("hadoop fs -chown -R hbase:hdfs " + hbaseTestFolderName + "; \n");
		    
		    //sb.append("kdestroy;\n");		    
		    //sb.append("kinit  " + hbaseInternalPrincipal + " -kt " + hbaseInternalKeyTabFilePathAndName +"; \n");
		    
		    sb.append(disableHBaseTableCmd + ";\n");
		    sb.append(dropHbaseTableName_forloadingHFilesCmd + ";\n");	
		    sb.append(createHbaseTableName_forloadingHFilesCmd + ";\n");	 
		    sb.append(loadHFilesToHBaseTableCmd + ";\n");		   		    				    
		    sb.append(hbaseTableRowCountQueryCmd + ";\n");
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");	
		    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
		    sb.append("kdestroy;\n");
		    						   
		    		    
		    String hiveHFileHBaseTableTestScriptFullFilePathAndName = hbaseScriptFilesFoder + "dcTestHiveGenHFiles_LoadingDataInto_HBaseTableScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (hiveHFileHBaseTableTestScriptFullFilePathAndName,  "Script File For Testing Hive-Generating-HFiles Loading Into HBase Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String hiveHFilesLoadHBaseTableTestingCmds = sb.toString();
			writeDataToAFile(hiveHFileHBaseTableTestScriptFullFilePathAndName, hiveHFilesLoadHBaseTableTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(hiveHFileHBaseTableTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(hiveHFileHBaseTableTestScriptFullFilePathAndName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			Path filePath = new Path(hdfsTableRowCountFilePathAndName);
			int hbaseTableRowCount = 0;
			if (currHadoopFS.exists(filePath)) {
				FileStatus[] status2 = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));	
				System.out.println("\n*** hdfsHiveMangedTableRowCountFilePathAndName: " + hdfsTableRowCountFilePathAndName + "\n" );
				
				BufferedReader br2 = new BufferedReader(new InputStreamReader(currHadoopFS.open(status2[0].getPath())));			
				String line = "";
				
				while ((line = br2.readLine()) != null) {				
					if (line.contains("6 row(s)")) {
						System.out.println("*** Row # in Hive-HFiles Loaded HBase Table Is: " + 6 );
						hbaseTableRowCount = 6;
						break;
					}								
				}//end while
				br2.close(); 
			} else {
				hbaseTableRowCount = 0;
			}
											
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Hive-Generating-HFiles) and Querying Hive-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Hive-Generated-HFiles-Loaded Hive-Managed HBase Table - '" + hbaseTableName_forloadingHFiles + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Hive-Generating-HFiles) and Querying Hive-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Hive-Generated-HFiles-Loaded Hive-Managed HBase Table - '" + hbaseTableName_forloadingHFiles + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			}
			writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for
			
		
		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//8. Loop through bdClusterEntryNodeList to internally disable/drop existing HBase table, create
	    // a HBase table, load data into the HBase table by Pig through HCatalog, and query data in the HBase table (counting rows)
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//int clusterENNumber = bdClusterEntryNodeList.size();
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "\n[5]. HBase Table & Pig-Through-HCatalog \n", true);
     
        //8(A) Move test data file to HDFS by external HDFS-writing
		String localWinSrcHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
		//System.out.println("\n*** localWinSrcHBaseTestDataFilePathAndName: " + localWinSrcHBaseTestDataFilePathAndName);
		//String hdfsHBaseTestDataFilePathAndName = "/data/test/HBase/dcHBaseTestData_employee.txt"; 	
		//System.out.println("\n*** hdfsHBaseTestDataFilePathAndName: " + hdfsHBaseTestDataFilePathAndName);
		
		moveWindowsLocalHBaseTestDataToHDFS (localWinSrcHBaseTestDataFilePathAndName, hdfsHBaseTestDataFilePathAndName, currHadoopFS);
		hdfsFilePathAndNameList.add(hdfsHBaseTestDataFilePathAndName);			
		
		tempClock = new DayClock();				
		tempTime = tempClock.getCurrentDateTime();		
		System.out.println("\n*** Done - Generated Pig-through-HCatalog Loading HBase Table Test Data in '" + bdClusterName + "' Cluster HDFS: " 
				+ hdfsHBaseTestDataFilePathAndName + " at the time - " + tempTime);
		
		//8(B) Loading test data into a HCatalog-managed HBase Table from a Pig Relation using pig.HCataStorer()
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	    //clusterENNumber = 1; //1..2..3..4..5..6	 
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing HBaseTable on Entry Node: " + tempENName);
			   
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
					
			
			//String hiveShellInitiateStr = "/usr/bin/hive -S -e ";
	        //String hbaseShellInitiateStr = " | hbase shell";
			//String hcatShellInitiateStr = "hcat -e ";
			//String pigShellInitiateStr = "/usr/bin/pig -stop_on_failure -useHCatalog -e ";
			//String setHiveAuxJarsPathCmd = hiveShellInitiateStr + "\"" + setHiveAuxJarsPathStr + "\"";
			
			String hcatTableName_forManagingHBaseTable = "employee_pighcat_"+ (i+1);			
			String hcatManagedHBaseTableName = "pighcat_loaded_employee" + (i+1);
						
			String singleColmnFamilyName = "cf1";			
			String disableHBaseTableCmd = "echo \"disable '"+ hcatManagedHBaseTableName + "'\"" + hbaseShellInitiateStr;
			String dropHcatTable_ForManagingHBaseTableCmd = hcatShellInitiateStr + "\"drop table " + hcatTableName_forManagingHBaseTable + "\"";			
			String dropHBaseTableCmd = "echo \"drop '"+ hcatManagedHBaseTableName + "'\"" + hbaseShellInitiateStr;	
			
			String hbaseColumnsMappingStr = ":key," + singleColmnFamilyName + ":fistName," 
											+ singleColmnFamilyName + ":lastName," + singleColmnFamilyName + ":salary," 
											+ singleColmnFamilyName + ":gender,"+ singleColmnFamilyName + ":address";			
			String createHcatTable_ForManagingHBaseTableStr = "create table " + hcatTableName_forManagingHBaseTable + "( \n"
					+ "employeeId Int, \n"
					+ "firstName String, \n"
					+ "lastName String, \n"
					+ "salary Int, \n"
					+ "gender String, \n"
					+ "address String \n"
					+ ") "
					//+ "Row format delimited fields terminated by ',' \n"                                        //This line is not needed
					//+ "Location '" + hbaseTestFolderName + hcatTableName_forManagingHBaseTable + "' \n" //This line is not working
					//+ "STORED BY 'org.apache.hcatalog.hbase.HBaseStorageHandler' \n" //HBaseHCatStorageHandler.. //This is for TDH1.3.2
					+ "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' \n" //HBaseHCatStorageHandler..//This is for TDH2.1
					+ "WITH SERDEPROPERTIES ('hbase.columns.mapping' = '" + hbaseColumnsMappingStr + "') \n"    //This is for TDH2.1
					+ "TBLPROPERTIES ('hbase.table.name' = '" + hcatManagedHBaseTableName + "')" ; //"', 'hbase.columns.mapping' = '" + hbaseColumnsMappingStr + "', 'hcat.hbase.output.bulkMode' = 'true' )" ;
			
						
			String createHcatTableCmd = hcatShellInitiateStr + "\"" + createHcatTable_ForManagingHBaseTableStr.replaceAll("\n", "") + "\"";
									
			String pigRelationSchemaStr = "AS (employeeid:Int, \n"
					+ "firstname:chararray, \n"
					+ "lastname:chararray, \n"
					+ "salary:Int, \n"
					+ "gender:chararray, \n"
					+ "address:chararray)\n";
	
			String pigLoadHcatAndHBaseTableStr = "employee_A = LOAD '" + hdfsHBaseTestDataFilePathAndName +  "' USING PigStorage(',') " + pigRelationSchemaStr.replaceAll("\n", "") + "; "
					+ "STORE employee_A INTO '" + hcatTableName_forManagingHBaseTable + "' USING org.apache.hive.hcatalog.pig.HCatStorer();";//Old not working: USING org.apache.hcatalog.pig.HCatStorer()
			String pigLoadHcatAndHBaseTableCmd =  pigShellInitiateStr + "\"" + pigRegisterHBaseJarsForHcatStr + pigLoadHcatAndHBaseTableStr + "\"&\n" + "sleep 30";
			
			
			//date +"%T";
			// /usr/bin/hive -e "INSERT OVERWRITE TABLE  emp1 select * from employee_sd"&
			//sleep 75;
			//date +"%T";
			//ps -ef | grep 'hdfs' | grep 'hive' | cut -f6 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
			//ps -ef | grep 'hdfs' | grep 'hive' | cut -f7 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
			//ps -ef | grep 'hdfs' | grep 'hive' | cut -f8 -d " " | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done;
			//su - hdfs;
					
			
			String enLocalTableRowCountFilePathAndName = enServerScriptFileDirectory + "tempTableRowCount.txt";
			//String createEnLocalTableRowCountFileFileCmd = "touch " + enLocalTableRowCountFilePathAndName;
							
			//String hbaseTableRowCountQueryCmd = hcatShellInitiateStr + "\"select count(*) from " + hcatTableName_forManagingHBaseTable + "\" > " + enLocalTableRowCountFilePathAndName;
			String hbaseTableRowCountQueryCmd = "echo \"count '"+ hcatManagedHBaseTableName + "'\"" + hbaseShellInitiateStr + " | grep 'row(s)' > " + enLocalTableRowCountFilePathAndName;
			
			String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + hcatManagedHBaseTableName + "_RowCount.txt";
			hdfsFilePathAndNameList.add(hdfsTableRowCountFilePathAndName);
			
			String killLoadProc = "ps -ef | grep 'hdfs' | grep 'pig' | cut -f6,7,8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			//String killLoadProc1 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f6 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			//String killLoadProc2 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f7 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			//String killLoadProc3 = "ps -ef | grep 'hdfs' | grep 'hive' | cut -f8 -d \" \" | sed -e 's/^/kill\\ -9\\ /g' | while read ln; do $ln; done";
			String grepPigProcCmd = "ps -ef | grep 'hdfs' | grep 'pig'| grep -v grep";
			 
			
			//sb.append("sudo su - hdfs;\n");
			//sb.append("cd " + enServerScriptFileDirectory + ";\n");
			//sb.append("kdestroy;\n");
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n");		
			//sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			//sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");				
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("cd " + enServerScriptFileDirectory + ";\n");			
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
			//sb.append("sleep 30; \n");
			
			//sb.append("hadoop fs -chmod -R 757 " + hbaseTestFolderName + "; \n");		    
		    //sb.append("hadoop fs -chown -R ambari-qa:hdfs " + hbaseTestFolderName + "; \n"); //..hiveManagedTableName
		    //sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + hbaseTestFolderName + "; \n");
		    
		    //sb.append("kdestroy;\n");		    
		    ////sb.append("sudo su - ambari-qa;\n"); //Not a must
		    ////sb.append("sudo su - " + loginUserName + ";\n"); //Not a must
		    ////sb.append("cd " + enServerScriptFileDirectory + ";\n"); //Not a must		    
		    //sb.append("kinit  " + hbaseInternalPrincipal + " -kt " + hbaseInternalKeyTabFilePathAndName +"; \n");
		    
		    sb.append(exportHadoopClassPath + ";\n");
		    sb.append(setHiveAuxJarsPathCmd + ";\n");
		    sb.append(exportPigClassPath + ";\n");
		    sb.append(exportPigOpts + ";\n");
		    sb.append(exportHcatHome + ";\n");
		    sb.append(exportHcatClassPath + ";\n"); //For TDH2.3.4
		    
		    sb.append(disableHBaseTableCmd + ";\n");
		    sb.append(dropHcatTable_ForManagingHBaseTableCmd + ";\n");
		    sb.append(dropHBaseTableCmd + ";\n"); //This is just for "to be on the safe-side"		    
		    
		    sb.append(createHcatTableCmd + ";\n");		    	    
		    sb.append(pigLoadHcatAndHBaseTableCmd + ";\n");			    
		    	   		    				    
		    sb.append(hbaseTableRowCountQueryCmd + ";\n");
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enLocalTableRowCountFilePathAndName + " " + hdfsTableRowCountFilePathAndName + "; \n");
		    sb.append("rm -f " + enLocalTableRowCountFilePathAndName + "; \n");	
		    sb.append("hadoop fs -chmod -R 550 " + hbaseTestFolderName + "; \n");
		    sb.append(killLoadProc + ";\n");	
		    sb.append(grepPigProcCmd + ";\n");
		    sb.append("kdestroy;\n");
		    						   
		  		    
		    String pigLoadHBaseTableTestScriptFullFilePathAndName = hbaseScriptFilesFoder + "dcTestPigHcat_LoadingDataInto_HBaseTableScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (pigLoadHBaseTableTestScriptFullFilePathAndName,  "Script File #3 For Testing Pig-HCatalog Loading Data Into HBase Table on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String pigLoadHBaseTableTestingCmds = sb.toString();
			writeDataToAFile(pigLoadHBaseTableTestScriptFullFilePathAndName, pigLoadHBaseTableTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(pigLoadHBaseTableTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(pigLoadHBaseTableTestScriptFullFilePathAndName, 
					hbaseScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
				
			Path filePath = new Path(hdfsTableRowCountFilePathAndName);
			int hbaseTableRowCount = 0;
			if (currHadoopFS.exists(filePath)) {
				FileStatus[] status2 = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));	
				System.out.println("\n*** hdfsHiveMangedTableRowCountFilePathAndName: " + hdfsTableRowCountFilePathAndName + "\n" );
				
				BufferedReader br2 = new BufferedReader(new InputStreamReader(currHadoopFS.open(status2[0].getPath())));			
				String line = "";
				
				while ((line = br2.readLine()) != null) {				
					if (line.contains("6 row(s)")) {
						System.out.println("*** Row # in Pig-Loaded HCatalog-Managed HBase Table Is: " + 6 );
						hbaseTableRowCount = 6;
						break;
					}								
				}//end while
				br2.close(); 
			} else {
				hbaseTableRowCount = 0;
			}			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (hbaseTableRowCount > 0){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Pig-Through-HCatalog) and Querying Hcatalog-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Pig-Loaded HCatalog-Managed HBase Table - '" + hcatManagedHBaseTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  --(1) Internally Disabling, Dropping, Creating, Loading (Pig-Through-HCatalog) and Querying Hcatalog-Managed HBase Table"
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  --(2) Querying generated Pig-Loaded HCatalog-Managed HBase Table - '" + hcatManagedHBaseTableName + "' gets a Row-Count:  '" + hbaseTableRowCount + "'\n";
			}
			writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for
			
			
		prevClock = new DayClock();				
		prevTime = prevClock.getCurrentDateTime();
		
		//9. Externally disabling, droppping, loading and querying HBase table		
		//String zookeeperQuorum = "hdpr02mn01.mayo.edu,hdpr02mn02.mayo.edu,hdpr02dn01.mayo.edu";
		//System.out.println("\n*** zookeeperQuorum: " + zookeeperQuorum);		
		//String [] hdfsAppendingTestParameterArray = new String [3];
		//hdfsAppendingTestParameterArray[0] = bdClusterName;		
		//hdfsAppendingTestParameterArray[0] = 				
		// B3a1_dcHBaseTrial_Native1.main(hdfsAppendingTestParameterArray);
		writeDataToAFile(dcTestHBase_RecFilePathAndName, "\n[6]. HBase Java Client on HBase Table \n", true);

		totalTestScenarioNumber++;	
		externalHbaseTableName = "employee_hbjc";
		Thread thread = new Thread(new Runnable() {	
		    public void run() {		    	
		    	try {
					BdCluster currBdCluster = new BdCluster(bdClusterName);
					String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
					String hbaseMasterPrincipal = currBdCluster.getHbaseMasterPrincipal(); 
					String hbaseRegionServerPrincipal = currBdCluster.getHbaseRegionServerPrincipal(); 
					
					String krbConfFilePathAndName = currBdCluster.getKrbConfFilePathAndName(); //dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
					//System.setProperty("sun.security.krb5.debug", "true");
					System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
					
					Configuration conf = HBaseConfiguration.create();
					conf.set("hbase.zookeeper.quorum", zookeeperQuorum);					
					//conf.set("hbase.master.port", "60000");
					//conf.set("hbase.zookeeper.peerport", "2888");
					//conf.set("hbase.zookeeper.leaderport", "3888");
					conf.set("zookeeper.znode.parent", "/hbase-secure");
					conf.set("hbase.zookeeper.property.clientPort", "2181");	//2888..2181				
					System.out.println("\n*** 'hbase.zookeeper.quorum': " + zookeeperQuorum);
								
					conf.set("hadoop.security.authentication", "kerberos");
					conf.set("hbase.security.authentication", "kerberos");		
					
					//conf.addResource(new Path("C:\\Windows\\dcKerberos\\hbase-site.xml"));					
					conf.set("hbase.master.kerberos.principal", hbaseMasterPrincipal);
					conf.set("hbase.regionserver.kerberos.principal", hbaseRegionServerPrincipal);
					
					UserGroupInformation.setConfiguration(conf);
					String hbaseExternalPrincipal = currBdCluster.getHbaseExternalPrincipal(); 
					String hbaseExternalKeyTabFilePathAndName = currBdCluster.getHbaseExternalKeyTabFilePathAndName();
					//String hbaseKeytabFilePathAndName = B3_dcTestHBase_LoadingAndQuerying.class.getResource("hbase_test_BDDev1.keytab").getPath();
					//UserGroupInformation.loginUserFromKeytab("hbase/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",hbaseKeytabFilePathAndName);
					UserGroupInformation.loginUserFromKeytab(hbaseExternalPrincipal, hbaseExternalKeyTabFilePathAndName);
							
					HBaseAdmin admin = new HBaseAdmin(conf);
					if (admin.tableExists(externalHbaseTableName)){
			        	admin.disableTable(externalHbaseTableName);
			    		System.out.println("\n*** disabled HBase table - " + externalHbaseTableName);
			    		admin.deleteTable(externalHbaseTableName);
			    		System.out.println("\n*** deleted HBase table - " + externalHbaseTableName);
			        }
					HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(externalHbaseTableName));		
					tableDescriptor.addFamily(new HColumnDescriptor("cf1"));
					admin.createTable(tableDescriptor);
					System.out.println("\n*** Created HBase table - " + externalHbaseTableName);
					
					
					HTable table = new HTable(conf, externalHbaseTableName);					
					Put put = new Put(Bytes.toBytes("101"));					
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes("firstName"), Bytes.toBytes("Jim"));					
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes("lastName"), Bytes.toBytes("Brown"));		
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes("salary"), Bytes.toBytes("160000"));			
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes("gender"), Bytes.toBytes("M"));			
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes("address"), Bytes.toBytes("\"3212 Wall St., New York, NY10277\""));			
					table.put(put);					
					table.flushCommits();
					admin.flush(externalHbaseTableName);
					admin.close();				
					System.out.println("\n*** Finished - Putting Data into HBase table - " + externalHbaseTableName);
					
					//table = new HTable(conf, externalHbaseTableName);
					Get get = new Get(Bytes.toBytes("101"));					
					get.addFamily(Bytes.toBytes("cf1"));					
					get.setMaxVersions(3);					
					Result result = table.get(get);
					for (KeyValue kv : result.raw()) {
						String rowKey = new String(kv.getRow());
						String colFamily = new String(kv.getFamily());
						String columnName = new String(kv.getQualifier());
						String colValue = new String(kv.getValue());
						System.out.println("\n--- rowKey " + rowKey); 
			            System.out.println("colFamily: " + colFamily); 
			            System.out.println("columnName: " + columnName); 
			            System.out.println("colValue: " + colValue); 
			            if (columnName.equalsIgnoreCase("salary") && colValue.equalsIgnoreCase("160000")){
			            	externalHBaseTestStatus = true;            	
			            	break;
			            }
			            
					}

					System.out.println("\n*** externalHBaseTestStatus: " + externalHBaseTestStatus);
					System.out.println("\n*** Finished - HBase Get Query!!!");
					table.close();		
					
		    	} catch (Exception e) {	
					System.out.println("---*---Caught Exception: " + e.toString());
					e.printStackTrace();		
				} //end try
				
		    }//end run
		    
		});
				
		thread.start();
		try {			
			thread.join(50000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
//		if (thread.isAlive()) {
//		    thread.stop();//.stop();
//		}		
	   Thread.sleep(1*15*1000);
		
//		try {
//			//BdCluster currBdCluster = new BdCluster(bdClusterName);
//			String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
//			//String zookeeperQuorum = "hdpr03mn01:2888:3888,hdpr03dn01:2888:3888,hdpr03dn02:2888:3888";
//			
//			Configuration conf = HBaseConfiguration.create();
//			conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
//			System.out.println("\n*** conf.set 'hbase.zookeeper.quorum' - " + zookeeperQuorum);
//			
//						
//			final HBaseAdmin admin = new HBaseAdmin(conf);
//			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(externalHbaseTableName));		
//			tableDescriptor.addFamily(new HColumnDescriptor("cf1")); //.setCompressionType(Algorithm.SNAPPY)
//			
//			admin.disableTable(tableDescriptor.getName());
//		    System.out.println("\n*** disabled HBase table - " + externalHbaseTableName);
//		    admin.deleteTable(tableDescriptor.getName());
//		    System.out.println("\n*** deleted HBase table - " + externalHbaseTableName);			
//			
//			admin.createTable(tableDescriptor);
//			System.out.println("\n*** Created HBase table - " + externalHbaseTableName);
//			admin.close();
//			
//			
//		     //table.addFamily(new HColumnDescriptor(CF_DEFAULT).setCompressionType(Algorithm.SNAPPY));
//			
//			HTable table = new HTable(conf, externalHbaseTableName);					
//			Put put = new Put(Bytes.toBytes("101"));					
//			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("firstName"), Bytes.toBytes("Jim"));					
//			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("lastName"), Bytes.toBytes("Brown"));		
//			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("salary"), Bytes.toBytes("160000"));			
//			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("gender"), Bytes.toBytes("M"));			
//			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("address"), Bytes.toBytes("\"3212 Wall St., New York, NY10277\""));			
//			table.put(put);					
//			table.flushCommits();					
//			System.out.println("\n*** Finished - Putting Data into HBase table - " + externalHbaseTableName);
//			
//			//table = new HTable(conf, externalHbaseTableName);
//			Get get = new Get(Bytes.toBytes("101"));					
//			get.addFamily(Bytes.toBytes("cf1"));					
//			get.setMaxVersions(3);					
//			Result result = table.get(get);
//			if (result.getValue(Bytes.toBytes("cf1"),  Bytes.toBytes("salary")).toString() == "160000"){
//				externalHBaseTestStatus = true;
//				System.out.println("\n*** Finished - Querying HBase table - " + externalHbaseTableName);
//				System.out.println("*** externalHBaseTestStatus == " + externalHBaseTestStatus);
//			}
//			table.close();				
//			
//		} catch (Exception e) {	
//			System.out.println("---*---Caught Exception: " + e.toString());
//			e.printStackTrace();		
//		} //end try
		
	    DayClock currClock = new DayClock();				
		String currTime = currClock.getCurrentDateTime();				
		String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);
		
		String testRecordInfo = "";			
		if (externalHBaseTestStatus == true){
			successTestScenarioNum++;
			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario: "
					+ "\n  -- (1) Externally (HBase Java Client) Disabling, Dropping, Creating, Loading (Putting) "
					+ "\n          and Querying (Getting) HBase Table - '" + externalHbaseTableName + "'"
					+ "\n  -- (2) External HBase Testing Total Time Used: " + timeUsed + "\n"; 			       
		} else {
			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario: "
					+ "\n  -- (1) Externally (HBase Java Client) Disabling, Dropping, Creating, Loading (Putting) "
					+ "\n          and Querying (Getting) HBase Table - '" + externalHbaseTableName + "'"
					+ "\n  -- (2) External HBase Testing Total Time Used: " + timeUsed + "\n";			       
		}
		writeDataToAFile(dcTestHBase_RecFilePathAndName, testRecordInfo, true);		
		

		
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed_end = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally (HBase Shell, Sqoop,...) and Externally (Java Client) Disabling, Dropping, Creating, Loading and Querying"
				      +  "\n                   HBase Table(s) on '" + bdClusterName + "' Cluster from " + bdClusterEntryNodeList.size() + " Entry Node(s)!";
		currNotingMsg += "\n***** Present HBase Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed_end; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total HBase Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   HBase Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   HBase Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestHBase_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestHBase_RecFilePathAndName));
	
	}//end run()
	

	private static void generateHBaseShellTestingCommandsFile (String LocalWindowsHBaseShellCmdsFilePathAndName, 
					String hbaseTableName) throws IOException {
		//String LocalWindowsHBaseShellCmdsFilePathAndName = bdClusterUATestResultsParentFolder + "dcTestHBase_TableDisableDrop_Create_Load_QueryCommands.txt";
		//prepareFile (LocalWindowsHBaseShellCmdsFilePathAndName,  "HBase Shell Commands of Testing HBase on '" + bdClusterName + "' Cluster");
		//String hBaseTableName = "employee" + (i+1);
				
		FileWriter outStream = new FileWriter(LocalWindowsHBaseShellCmdsFilePathAndName);		
	    PrintWriter output = new PrintWriter (outStream);
	    output.println("disable '" + hbaseTableName + "'");
	    output.println("drop '" + hbaseTableName + "'");
	    output.println("create '" + hbaseTableName + "',{NAME => 'cfa', VERSIONS => 5}");   
	    
		String localWinSrcHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
		FileReader aFileReader = new FileReader(localWinSrcHBaseTestDataFilePathAndName);
		BufferedReader br = new BufferedReader(aFileReader);
		String line = "";
		
		while ((line = br.readLine()) != null) {
			System.out.println ("\n*** line is: " + line);
			String[] lineSplit = line.split(",");
			String empId = lineSplit[0];			
			String fName = lineSplit[1];
			String lName = lineSplit[2];
			String salary = lineSplit[3];
			String gender = lineSplit[4];
			String address = lineSplit[5];
			if (lineSplit.length == 7){
				address +=  "," + lineSplit[6];
			}
			if (lineSplit.length == 8){
				address += "," + lineSplit[7];
			}
//			System.out.println (" --- empId: " + empId);
//			System.out.println (" --- fName: " + fName);
//			System.out.println (" --- lName: " + lName);
//			System.out.println (" --- salary: " + salary);
//			System.out.println (" --- gender: " + gender);
//			System.out.println (" --- address: " + address);
			String put1 = "put '" + hbaseTableName + "', '" + empId + "', 'cfa:firstName', '" + fName + "'";
			System.out.println (" -*- put1: " + put1);			
			String put2 = "put '" + hbaseTableName + "', '" + empId + "', 'cfa:lastName', '" + lName + "'";
			System.out.println (" -*- put2: " + put2);			
			String put3 = "put '" + hbaseTableName + "', '" + empId + "', 'cfa:salary', '" + salary + "'";
			System.out.println (" -*- put3: " + put3);
			String put4 = "put '" + hbaseTableName + "', '" + empId + "', 'cfa:gender', '" + gender + "'";
			System.out.println (" -*- put4: " + put4);
			String put5 = "put '" + hbaseTableName + "', '" + empId + "', 'cfa:address', '" + address + "'";
			System.out.println (" -*- put5: " + put5);
			
			output.println (put1);
			output.println (put2);
			output.println (put3);
			output.println (put4);
			output.println (put5);
			
		}
		br.close();
		aFileReader.close();
		
		output.println ("exit");
		output.close();
		outStream.close();		
	}//end generateHBaseShellTestingCommandsFile
	
	
	
	private static String generateECGTestDataSqoopImportToHBaseFullCmd (String sqlServerDBName, 
			String importTableName, String hbaseTableName, String columnFamily, String rowKey) {
		ConfigureDBResources edwProjDevDBConfigRes = new ConfigureDBResources ();
		DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(edwProjDevDBConfigRes.getEdwProjDevDBConParameters());
		//String rdbServerMS = dbcon.getDatabaseMS();
		String rdbServerInstance = dbcon.getDatabaseName().replace("\\", ".mayo.edu\\\\");		
		String connUsername = dbcon.getUsername();
		String connPassword = dbcon.getPassword();
		
		//1) Obtain sqoop command part #1 //EIMSQLPROD\\EDWSQLPROD ...ROPEIM802Q\\PROJDEV
	    //String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect \"jdbc:sqlserver://ROPEIM802Q.mayo.edu\\\\PROJDEV;database="
	    //		+ sqlServerDBName + "\" --username TU05303 --password 80uijknm";
		String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect "
	 		    + "\"jdbc:sqlserver://"	+ rdbServerInstance 
	    		+ ";database=" + sqlServerDBName
	    		+ "\" --username " + connUsername 
	    		+ " --password " + connPassword;
				  
	    
	    //System.out.println("\n--*1-- sqoopCmdLeft_5Q is : \n" + sqoopCmdLeft_5Q);
	   
	    //2) Obtain sqoop command part #2
	    String sqoopCmdMiddle_5Q = "--table " + importTableName + "";
	    //System.out.println("\n--*2-- sqoopCmdMiddle_5Q is : \n" + sqoopCmdMiddle_5Q);
	    
	    //" --input-fields-terminated-by , --escaped-by \\ --input-enclosed-by '\"'";
	    
	    //3) Obtain sqoop command part #3	  
	    String sqoopCmdRight_5Q = "-m 1 --hbase-table " + hbaseTableName 
	    		+ " --column-family " + columnFamily 
	    		+ " --hbase-row-key " + rowKey; //--m 1..--split-by hl7MsgId 
	    //System.out.println("\n--*3-- sqoopCmdRight_5Q is : \n" + sqoopCmdRight_5Q);  	       
	   
	    //4) Obtain sqoop full command in single line == parts #1-4 separated by " "
	    String sqoopFullCmd_5Q = sqoopCmdLeft_5Q + " " + sqoopCmdMiddle_5Q + " " + sqoopCmdRight_5Q; 
	    System.out.println("\n--*-- sqoopFullCmd_5Q is : \n" + sqoopFullCmd_5Q);
	    
	    return sqoopFullCmd_5Q;
	}//end generateECGTestDataSqoopImportToHBaseFullCmd
	
	private static void moveWindowsLocalHBaseTestDataToHDFS (String localWinSrcHBaseTestDataFilePathAndName, 
							String hdfsHBaseTestDataFilePathAndName, FileSystem currHadoopFS ){
		//String localWinSrcHBaseTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
		//System.out.println("\n*** localWinSrcHBaseTestDataFilePathAndName: " + localWinSrcHBaseTestDataFilePathAndName);
		//String hdfsTestDataFilePathAndName = "/data/test/HBase/dcHBaseTestData_employee.txt"; 		
		try {						
			Path outputPath = new Path(hdfsHBaseTestDataFilePathAndName);		
			if (currHadoopFS.exists(outputPath)) {
				currHadoopFS.delete(outputPath, true);
				System.out.println("\n*** deleting existing HBase file: " + hdfsHBaseTestDataFilePathAndName);
	        }
			
			FSDataOutputStream fsDataOutStream = currHadoopFS.create(new Path(hdfsHBaseTestDataFilePathAndName), true);			
			//PrintWriter bw = new PrintWriter(fsDataoutStream);	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
			
			
			FileReader aFileReader = new FileReader(localWinSrcHBaseTestDataFilePathAndName);
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
	        //String srcFilePathAndName = bdClusterUATestResultsParentFolder + localHBaseTestDataFileName;
	        //InputStream is = new BufferedInputStream(new FileInputStream(srcFilePathAndName));
	        //System.out.println("\n*** srcFilePathAndName: " + srcFilePathAndName);
	        //IOUtils.copyBytes(is, os, conf);			
		} catch (IOException e) {				
			e.printStackTrace();			
		}//end try 		
	}//moveWindowsLocalHBaseTestDataToHDFS

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
