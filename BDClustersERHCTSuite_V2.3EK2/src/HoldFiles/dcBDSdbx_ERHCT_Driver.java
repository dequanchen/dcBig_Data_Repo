package HoldFiles;

import java.io.File;
import java.io.IOException;

import dcBDApplianceERHCT_TestSuite.A1_dcTestHDFS_WritingAndReading;
import dcBDApplianceERHCT_TestSuite.A2_dcTestMapReduce_WordCounting;
import dcBDApplianceERHCT_TestSuite.A3_dcTestYarn_PiApproximation;
import dcBDApplianceERHCT_TestSuite.A4_dcTestSqoop_ImportingAndExporting;
import dcBDApplianceERHCT_TestSuite.B1_dcTestHive_LoadingAndQuerying;
import dcBDApplianceERHCT_TestSuite.B2_dcTestPig_LoadingAndStoring;
import dcBDApplianceERHCT_TestSuite.B3_dcTestHBase_LoadingAndQuerying;
import dcBDApplianceERHCT_TestSuite.B4_dcTestSpark_RDD_CWTA;
import dcBDApplianceERHCT_TestSuite.C1_dcTestFlume_StoringData;
import dcBDApplianceERHCT_TestSuite.C2_dcTestES_IndexingAndSearching;
import dcBDApplianceERHCT_TestSuite.D1a_dcTestKnox_SharedKxServers_SupportedRestServices;
import dcBDApplianceERHCT_TestSuite.D1b_dcTestKnox_SharedKxServers_SupportedRestServices;
import dcModelClasses.DayClock;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/8/2014, 1/1/2015; 1/5/2016 (Kerberos); 1/14/2016; 2/29/2016; 2/15/2017
*/ 

public class dcBDSdbx_ERHCT_Driver {	
	private static int testingTimesSeqNo = 8;
	private static String bdClusterName = "BDSdbx";
	private static String bdApplianceERHCTestResultsParentFolder = "C:\\BD\\BD_ERHCT\\";	
	
	
	public static void main(String[] args) throws Exception {
		String testUserAccountName = "TestUserName";

		String testUserKDCPW = "TestUserKDCPassWord";
		String internalKinitCmdStr = "";
		internalKinitCmdStr = "echo \"" + testUserKDCPW + "\" | kinit " + testUserAccountName; //  "@MFAD.MFROOT.ORG";
		if (args.length >=4) {
			testingTimesSeqNo = Integer.valueOf(args[0]);
			bdClusterName = args[1];
			String loginUserName = args[2];
			String loginUserPw = args[3];			
			internalKinitCmdStr = "echo \"" + loginUserPw + "\" | kinit " + loginUserName;			
		}		
		
		String currBdClusterERHCTestResultsFolder = bdApplianceERHCTestResultsParentFolder + bdClusterName + "\\";
		prepareFolder(currBdClusterERHCTestResultsFolder, "User Acceptance Results for BD '" + bdClusterName + "' Cluster");
		
		//I.
		//I.1. Get process/thread start time
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();		 
		
		//I.2. Prepare files for testing records
		String dcTestBdCluster_RecFilePathAndName = currBdClusterERHCTestResultsFolder + "dcTestBigDataComponentFunctions_Records_No" + testingTimesSeqNo + ".txt";
		prepareFile (dcTestBdCluster_RecFilePathAndName,  "Records of Testing BD Component Functions on '" + bdClusterName + "' Cluster");
		
		String [] uatParametersArray = new String [20];
		uatParametersArray[0] = Integer.toString(testingTimesSeqNo);
		uatParametersArray[1] = bdClusterName;
		uatParametersArray[2] = bdApplianceERHCTestResultsParentFolder;		
		uatParametersArray[3] = currBdClusterERHCTestResultsFolder;
		
		uatParametersArray[9] = internalKinitCmdStr;
				
		//II. Actual Testing On Critical Hadoop Data-oriented Components
		//1. Testing HDSF Writing and Reading Internally and Externally
		System.out.println("*** Intializing HDFS Writing and Reading Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHDFSTestDataFileName = "dcHDFSTest_Data.txt";
		String localHDFSTestAppendingDataFileName = "dcHDFSTest_AppendingData.txt";
		String hdfsTestFolderName = "/data/test/HDFS/";
		
		uatParametersArray[4] = localHDFSTestDataFileName;
		uatParametersArray[5] = localHDFSTestAppendingDataFileName;	
		uatParametersArray[6] = hdfsTestFolderName;
				
		A1_dcTestHDFS_WritingAndReading.main(uatParametersArray);
		
		
		//2.Testing MapReduce word-counting
		System.out.println("*** Intializing Testing MapReduce on BD '" + bdClusterName + "' Cluster... \n");
		String localMapReduceTestDataFileName = "dcFSETestData.txt";
		String mapReduceTestFolderName = "/data/test/MapReduce/";		
		String localMapReduceJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"WordCount-0.0.1-SNAPSHOT-jar-with-dependencies.jar";				
						
		uatParametersArray[4] = localMapReduceTestDataFileName;		
		uatParametersArray[5] = mapReduceTestFolderName;	
		uatParametersArray[6] = localMapReduceJarFileName;
		
		A2_dcTestMapReduce_WordCounting.main(uatParametersArray);
		
		
		//3.Testing Yarn PiApproximation
		System.out.println("*** Intializing Testing Yarn on BD '" + bdClusterName + "' Cluster... \n");
		String LocalPiApproximationTermNumber = "100000";
		String yarnTestFolderName = "/data/test/Yarn/";		
		String localYarnJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"WordCount-0.0.1-SNAPSHOT-jar-with-dependencies.jar";				
						
		uatParametersArray[4] = LocalPiApproximationTermNumber;		
		uatParametersArray[5] = yarnTestFolderName;	
		uatParametersArray[6] = localYarnJarFileName;
		
		A3_dcTestYarn_PiApproximation.main(uatParametersArray);
						
		
		//4. Testing Sqoop Importing and Exporting
		System.out.println("*** Intializing Sqoop Importing and Exporting Testing on BD '" + bdClusterName + "' Cluster... \n");
		String sqlServerDBName = "EDT_BigData"; 
		String importTableName = "archiveFile";
		String exportTableName = "sqoopExported"; 	
		String sqoopTestFolderName = "/data/test/Sqoop/";
		String exportTableName2 = "sqoopExported2";	
		
		uatParametersArray[4] = sqlServerDBName;
		uatParametersArray[5] = importTableName;	
		uatParametersArray[6] = exportTableName;		
		uatParametersArray[7] = sqoopTestFolderName;
		uatParametersArray[8] = exportTableName2;
		
		A4_dcTestSqoop_ImportingAndExporting.main(uatParametersArray);
		
		//5. Testing Hive Table Creating, Loading, Dropping and Querying 
		System.out.println("*** Intializing Hive Table Creating, Loading and Query Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHiveTestDataFileName = "dcHHPTestData_employee.txt"; //"dcHHPTestData_employee.txt ...dcHiveTestData_employee.txt";
		String hiveTestFolderName = "/data/test/Hive/";
				
		uatParametersArray[4] = localHiveTestDataFileName;		
		uatParametersArray[5] = hiveTestFolderName;	
			
		B1_dcTestHive_LoadingAndQuerying.main(uatParametersArray);	
		
		
		//6. Testing Pig Relation Creating (By LOADing, FILTERing, and Grouping), and STOREing Data to HDFS 
		System.out.println("*** Intializing Pig Relation Creating and Storing Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localPigTestDataFileName = "dcHHPTestData_employee.txt";
		String pigTestFolderName = "/data/test/Pig/";
				
		uatParametersArray[4] = localPigTestDataFileName;		
		uatParametersArray[5] = pigTestFolderName;	
			
		B2_dcTestPig_LoadingAndStoring.main(uatParametersArray);			
		
		//7. Testing HBase Table Creating, Loading, Disabling/Dropping and Querying 
		System.out.println("*** Intializing HBase Table Creating, Loading and Query Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localHBaseTestDataFileName = "dcHHPTestData_employee.txt";
		String hbaseTestFolderName = "/data/test/HBase/";
		
				
		uatParametersArray[4] = localHBaseTestDataFileName;		
		uatParametersArray[5] = hbaseTestFolderName;
		
			
		B3_dcTestHBase_LoadingAndQuerying.main(uatParametersArray);
		
					
		//8. Testing Spark RDD Creation, Transformation, Action and Saving 
		System.out.println("*** Intializing Spark RDD Creation, Transformation, Action and Saving Testing on BD '" + bdClusterName + "' Cluster... \n");
		String localSparkTestDataFileName = "dcHHPTestData_employee.txt";
		String sparkTestFolderName = "/data/test/Spark/";		
				
		uatParametersArray[4] = localSparkTestDataFileName;		
		uatParametersArray[5] = sparkTestFolderName;
		
		B4_dcTestSpark_RDD_CWTA.main(uatParametersArray);
		
		//9.Testing Flume Storing a File Data to HDFS 
		System.out.println("*** Intializing Testing Flume Storing File In HDFS on BD '" + bdClusterName + "' Cluster... \n");
		String localFlumeTestDataFileName = "dcFSETestData.txt";
		String flumeTestFolderName = "/data/test/Flume/";
		String localFlumeConfigFileName = "dcFlumeConf.txt";
				
		uatParametersArray[4] = localFlumeTestDataFileName;		
		uatParametersArray[5] = flumeTestFolderName;	
		uatParametersArray[6] = localFlumeConfigFileName;	
		
		C1_dcTestFlume_StoringData.main(uatParametersArray);	 
				
		//10.Testing ElasticSearch Indexing and Searching  
		System.out.println("*** Intializing Testing ElasticSearch Indexing and Searching on BD '" + bdClusterName + "' Cluster... \n");
		String esTestFolderName = "/data/test/ElasticSearch/";
						
		uatParametersArray[4] = esTestFolderName;
					
		C2_dcTestES_IndexingAndSearching.main(uatParametersArray);	 
		
		
		//11. Testing Solr Indexing and Searching  
		System.out.println("*** Intializing Testing Solr Indexing and Searching on BD '" + bdClusterName + "' Cluster... \n");
		//--disabled	
		
		
		//12.Testing Storm Using Flume & ES 
		System.out.println("*** Intializing Testing Storm Using Flume and ElasticSearch on BD '" + bdClusterName + "' Cluster... \n");
		String localStormTestDataFileName = "dcFSETestData.txt";
		String stormTestFolderName = "/data/test/Storm/";
		String localStormFlumeConfigFileName = "dcStormFlumeConf.txt";
		String localStormJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";
		//String localStormJarFileName = "StormTest-0.0.1-SNAPSHOT.jar";
		
		String esClusterName = "";
		if (bdClusterName.equalsIgnoreCase("BDDev")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
			esClusterName = "bddevelasticsearch";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("BDInt")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
			//esClusterName = "bdintelasticsearch";
			esClusterName = "bdprd2elasticsearch";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDInt2")
				|| bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
			esClusterName = "bdint2elasticsearch";
		}
			
		if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
				||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
				|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			esClusterName = "bdsbxelasticsearch"; //http://hdpr04en01:9200/_plugin/head/
		}
		
				
		uatParametersArray[4] = localStormTestDataFileName;		
		uatParametersArray[5] = stormTestFolderName;	
		uatParametersArray[6] = localStormFlumeConfigFileName;
		uatParametersArray[7] = localStormJarFileName;
		uatParametersArray[8] = esClusterName;	
		
		//C4a_dcTestStorm_UsingFlume.main(uatParametersArray);
		//C3_dcTestStorm_UsingFlumeAndES.main(uatParametersArray);		
	
		
		//13.Testing Oozie
		System.out.println("*** Intializing Testing Oozie on BD '" + bdClusterName + "' Cluster... \n");
		String localOozieTestDataFileName = "dcFSETestData.txt";
		String oozieTestFolderName = "/data/test/Oozie/";		
		String localOozieJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";//"WordCount-0.0.1-SNAPSHOT-jar-with-dependencies.jar";				
		
		String localOozieMRActionConfigFolderName = "C:\\BD\\BD_ERHCT\\oozie\\mapreduce\\";
		String localOozieJavaActionConfigFolderName = "C:\\BD\\BD_ERHCT\\oozie\\java\\";
		String localOozieShellActionConfigFolderName = "C:\\BD\\BD_ERHCT\\oozie\\shell\\";
		String localOozieJobPropertiesFileName = "job.properties";
		String localOozieWorkflowFileName = "workflow.xml";
		String localOozieShellActionScriptFileName = "RunMapReduceProgramByShellScript4Oozie.sh";
		
		uatParametersArray[4] = localOozieTestDataFileName;		
		uatParametersArray[5] = oozieTestFolderName;	
		uatParametersArray[6] = localOozieJarFileName;
		
		uatParametersArray[7] = localOozieMRActionConfigFolderName;	
		uatParametersArray[8] = localOozieJavaActionConfigFolderName;		
		uatParametersArray[10] = localOozieJobPropertiesFileName;	
		uatParametersArray[11] = localOozieWorkflowFileName;
		
		uatParametersArray[12] = localOozieShellActionConfigFolderName;	
		uatParametersArray[13] = localOozieShellActionScriptFileName;
		
		String localOozieKnoxConfigFolderName = "C:\\BD\\BD_ERHCT\\oozie\\knox\\";
		String localOozieWorkflowConfigXmlFileName = "workflow-configuration.xml";		
		uatParametersArray[14] = localOozieKnoxConfigFolderName;	
		uatParametersArray[15] = localOozieWorkflowConfigXmlFileName;
		
		//C4_dcTestOozie_Workflow.main(uatParametersArray);
				
		
		//14.Testing Kafka
		System.out.println("*** Intializing Testing Kafka on BD '" + bdClusterName + "' Cluster... \n");
		String localKafkaTestDataFileName = "dcHHPTestData_employee.txt"; //"dcHHPTestData_employee.txt ...dcHiveTestData_employee.txt";
		String kafkaTestFolderName = "/data/test/kafka/";
		String localKafkaJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";
				
		uatParametersArray[4] = localKafkaTestDataFileName;		
		uatParametersArray[5] = kafkaTestFolderName;
		uatParametersArray[6] = localKafkaJarFileName;	
						
		//C5_dcTestKafka_TpcDC_MsgPC.main(uatParametersArray);		
				
		//15. Testing Knox-LDAP-AD Secured Hadoop Services (WebHDFS, WebHcat, ....)
		String knoxTestFolderName = "/data/test/Knox/";
		String localKnoxTestDataFileName = "dcFSETestData.txt";		
		String localKnoxLargeWebHbaseJsonDataFileName = "dcPayload_large_json_21969kB.txt";
		String localKnoxLargeWebHbaseXmlDataFileName = localKnoxLargeWebHbaseJsonDataFileName.replace("_json_", "_xml_");
				
		String localKnoxTrustStoreFolderName = "C:\\BD\\BD_ERHCT\\truststores\\";
		String localKnoxTrustStorePathAndName = "";
		if (bdClusterName.equalsIgnoreCase("BDDev")
				|| bdClusterName.equalsIgnoreCase("Dev")
				|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
			localKnoxTrustStorePathAndName = localKnoxTrustStoreFolderName + "gateway_kx01.jks";
		}		
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("BDInt")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDInt")){
			localKnoxTrustStorePathAndName = localKnoxTrustStoreFolderName + "gateway_kx03.jks";
		}		
		if (bdClusterName.equalsIgnoreCase("BDInt2")
				|| bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
			localKnoxTrustStorePathAndName = localKnoxTrustStoreFolderName + "gateway_kx03.jks";
		}
		
		if (bdClusterName.equalsIgnoreCase("BDSbx")|| bdClusterName.equalsIgnoreCase("BDSdbx")
				||bdClusterName.equalsIgnoreCase("Sbx")|| bdClusterName.equalsIgnoreCase("Sdbx")
				|| bdClusterName.equalsIgnoreCase("MC_BDSbx") || bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			localKnoxTrustStorePathAndName = localKnoxTrustStoreFolderName + "gateway_kx01.jks";
		}
		String localKnoxTrustStorePassWd = "localKnoxTrustStorePassWord";
		uatParametersArray[4] = knoxTestFolderName;
		uatParametersArray[5] = localKnoxTestDataFileName;
		uatParametersArray[6] = localHDFSTestAppendingDataFileName;	
		uatParametersArray[7] = localKnoxTrustStorePathAndName;	
		uatParametersArray[8] = localKnoxTrustStorePassWd;	
		
		uatParametersArray[10] = localKnoxLargeWebHbaseJsonDataFileName;		
		uatParametersArray[11] = localKnoxLargeWebHbaseXmlDataFileName;	
		
		String localKnoxOozieJarFileName = "DequanTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar";		
		String localKnoxOozieKnoxConfigFolderName = "C:\\BD\\BD_ERHCT\\oozie\\knox\\";
		String localKnoxOozieWorkflowFileName = "workflow.xml";
		String localKnoxOozieWorkflowConfigXmlFileName = "workflow-configuration.xml";
				
		uatParametersArray[12] = localKnoxOozieJarFileName;
		uatParametersArray[13] = localKnoxOozieKnoxConfigFolderName;
		uatParametersArray[14] = localKnoxOozieWorkflowFileName;
		uatParametersArray[15] = localKnoxOozieWorkflowConfigXmlFileName;
				
		D1a_dcTestKnox_SharedKxServers_SupportedRestServices.main(uatParametersArray);
		if (bdClusterName.equalsIgnoreCase("BDProd2")
				|| bdClusterName.equalsIgnoreCase("BDInt")
				|| bdClusterName.equalsIgnoreCase("Int")
				|| bdClusterName.equalsIgnoreCase("MC_BDInt")
			|| bdClusterName.equalsIgnoreCase("BDInt2")
				|| bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
				|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
				|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")
				){
			localKnoxTrustStorePathAndName = localKnoxTrustStoreFolderName + "gateway_kx04.jks";
			uatParametersArray[7] = localKnoxTrustStorePathAndName;	
		}
		D1b_dcTestKnox_SharedKxServers_SupportedRestServices.main(uatParametersArray);
			
				
		
		//16. Generating All-In-One Integrated Report File
		System.out.println("*** Intializing Get Integerated Report for All Testing on BD '" + bdClusterName + "' Cluster... \n");
		//E_dcGet_IntegratedERHCTReport.main(uatParametersArray);
			
		
		//III.
		//Notice message on the console and writing to record file		
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing BigData Component Functions on '" + bdClusterName + "' Cluster!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n===========================================================";	    
		System.out.println(currNotingMsg);		
				
	}//end main
	
	
	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
						
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile
	

	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder	

}//end class
