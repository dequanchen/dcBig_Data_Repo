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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
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

import dcModelClasses.DayClock;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/20-30/2014, 12/1-4/2014; 2/4/2015; 2/26/2015; 
*       1/6/2016 (Kerberos); 1/15/2016; 2/18/2016; 3/14/2016; 3/25/2016
*/ 

@SuppressWarnings("unused")
public class B3b_dcTestHBase_LoadingAndQuerying_JavaClient {
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

		
		//9. Externally disabling, droppping, loading and querying HBase table		
		//String zookeeperQuorum = "hdpr02mn01.mayo.edu,hdpr02mn02.mayo.edu,hdpr02dn01.mayo.edu";
		//System.out.println("\n*** zookeeperQuorum: " + zookeeperQuorum);		
		//String [] hdfsAppendingTestParameterArray = new String [3];
		//hdfsAppendingTestParameterArray[0] = bdClusterName;		
		//hdfsAppendingTestParameterArray[0] = 				
		// B3a1_dcHBaseTrial_Native1.main(hdfsAppendingTestParameterArray);
		totalTestScenarioNumber++;	
		externalHbaseTableName = "employee_hbjc";
		Thread thread = new Thread(new Runnable() {	
		    @SuppressWarnings("deprecation")
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
					//tableDescriptor.set..
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
		//1) Obtain sqoop command part #1 //EIMSQLPROD\\EDWSQLPROD ...ROPEIM802Q\\PROJDEV
	    String sqoopCmdLeft_5Q = "/usr/bin/sqoop import --connect \"jdbc:sqlserver://ROPEIM802Q.mayo.edu\\\\PROJDEV;database="
	    		+ sqlServerDBName + "\" --username TU05303 --password vbfgrt45";
	    //System.out.println("\n--*1-- sqoopCmdLeft_5Q is : \n" + sqoopCmdLeft_5Q);
	   
	    //2) Obtain sqoop command part #2
	    String sqoopCmdMiddle_5Q = "--table " + importTableName + "";
	    //System.out.println("\n--*2-- sqoopCmdMiddle_5Q is : \n" + sqoopCmdMiddle_5Q);
	    
	    //" --input-fields-terminated-by , --escaped-by \\ --input-enclosed-by '\"'";
	    
	    //3) Obtain sqoop command part #3	  
	    String sqoopCmdRight_5Q = "--m 1 --hbase-table " + hbaseTableName 
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
