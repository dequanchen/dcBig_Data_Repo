
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
* Date: 5/3/2016; 5/17, 18/2016
*/ 


public class C6_dcTestKafka_TpcDC_MsgPC {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";
	private static String localKafkaTestDataFileName = "";
	private static String localKafkaJarFileName = "";
	private static String kafkaTestFolderName = "";
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	// /usr/lib/sqoop/bin/sqoop (<=TDH2.1.11) ==> /usr/bin/sqoop or /usr/hdp/2.3.4.0-3485/sqoop/bin/sqoop (TDH2.3.4)
    // /usr/lib/kafka/bin/kafka (<=TDH2.1.11) ==> /usr/bin/kafka or /usr/hdp/2.3.4.0-3485/kafka/bin/kafka (TDH2.3.4)

	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for Kafka-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		localKafkaTestDataFileName = args[4];
		kafkaTestFolderName = args[5];
		localKafkaJarFileName = args[6]; 
			
		internalKinitCmdStr = args[9];
		
		if (!kafkaTestFolderName.endsWith("/")){
			kafkaTestFolderName += "/";
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
		String kafkaScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "kafka\\";
		prepareFolder(kafkaScriptFilesFoder, "Local Kafka Testing Script Files");
		
		String dcTestKafka_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestKafka_TopicCreatingLoadingQuerying_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestKafka_RecFilePathAndName,  "Records of Testing Kafka on '" + bdClusterName + "' Cluster");
		
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Kafka Internal and External Topic Deletion and Creation, Message Producing and Consuming Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Kafka, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestKafka_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		
		String activeNN_addr_port = currBdCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println(" *** Current Hadoop cluster's activeNN_addr_port: " + activeNN_addr_port);
		
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int clusterENNumber = bdClusterEntryNodeList.size();
		int clusterENNumber_Start = 0; //0..1..2..3..4..5
		//clusterENNumber = 1; //1..2..3..4..5..6
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENAbstractedCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		String loginUser4AllNodesName = bdENAbstractedCmdFactory.getUsername(); 
		kafkaTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Kafka/";
						
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		//String ambariQaInternalPrincipal = currBdCluster.getAmbariQaInternalPrincipal(); //..."ambari-qa@MAYOHADOOPDEV1.COM";
		//String ambariInternalKeyTabFilePathAndName = currBdCluster.getAmbariInternalKeyTabFilePathAndName(); //... "/etc/security/keytabs/smokeuser.headless.keytab";
		
		DayClock tempClock = new DayClock();				
		String tempTime = tempClock.getCurrentDateTime();
		NumberFormat df = new DecimalFormat("#0.00"); 
		//String localWinSrcKafkaTestDataFilePathAndName = bdClusterUATestResultsParentFolder + localKafkaTestDataFileName;
		//System.out.println("\n*** localWinSrcKafkaTestDataFilePathAndName: " + localWinSrcKafkaTestDataFilePathAndName);
		//String hdfsKafkaTestDataFilePathAndName = kafkaTestFolderName +  "dcKafkaTestData_employee.txt"; //changed from folder "/data/test/Kafka/"...	
		//System.out.println("\n*** hdfsKafkaTestDataFilePathAndName: " + hdfsKafkaTestDataFilePathAndName);
	
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
						
		//4. Loop through bdClusterEntryNodeList to internally drop existing kafka topic deletion and creation,
		//     message producing and consuming on the kafka topic 	
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//clusterENNumber_Start = 0; //0..1..2..3..4..5
	   // clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestKafka_RecFilePathAndName, "[1]. Kafka Topic CLI Ops & Message Producing & Consuming CLI Ops \n", true);
		int clusterENNumber1 = 1;
		for (int i = clusterENNumber_Start; i < clusterENNumber1; i++){ //clusterENNumber..bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Kafka Command Line Operations on Entry Node: " + tempENName);
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			//String rootPw = bdENRootCmdFactory.getRootPassword();			
			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
			
			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
						 
						
			//(1) Move test data file to the local Linux server folder of the login user - enServerScriptFileDirectory
			//String enServerScriptFileDirectory = "/data/home/m041785/test";	
			//int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localKafkaTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKafkaTestDataFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Kafka Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			} else {
				System.out.println("\n*** Failed - Moving Kafka Test Data File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			}
			
						
			//(2) Generating commands for testing Kafka topic and message operations
			String kafkaTopicName = "testKafka_TS"+ (i+1);	
			String zookerQuorum4Kafka = currBdCluster.getZookeeperQuorum().replaceAll(".edu,", ".edu:2181,");			
			String currNodeRootLoginPassWord = bdENRootCmdFactory.getRootPassword();						
			String currClusterBrokerAndPortList = currBdCluster.getKafkaBrokerAndPortList();
			
			System.out.println(" *** zookerQuorum4Kafka: " + zookerQuorum4Kafka);		
			System.out.println(" *** currClusterBrokerAndPortList: " + currClusterBrokerAndPortList);
			System.out.println(" *** currNodeRootLoginPassWord: " + currNodeRootLoginPassWord);
			System.out.println(" *** kafkaTopicName: " + kafkaTopicName);
			
			String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory + localKafkaTestDataFileName;			
			System.out.println("\n *** enServerTestDataFileFullPathAndName: \n	---" + enServerTestDataFileFullPathAndName);		
				
			String kafkaTestResultFileName = kafkaTopicName + "_result.txt";
			String localKafkaTestResultPathAndName = enServerScriptFileDirectory + kafkaTestResultFileName;
			
			String kafkaTestResultHdfsPathAndName = kafkaTestFolderName + kafkaTestResultFileName;
			
			//(2)a. Topic Deletion and Creation Cmds from Root User			
			String kafkaTopicShellInitiateStr = "echo '" + currNodeRootLoginPassWord + "' | su - root -c  '/usr/hdp/current/kafka-broker/bin/kafka-topics.sh --zookeeper " + zookerQuorum4Kafka + " ";
			
			String deleteTopicCmd = kafkaTopicShellInitiateStr + "--delete --topic " + kafkaTopicName + "'";
			String createTopicCmd = kafkaTopicShellInitiateStr + "--replication-factor 1 --partitions 1 --create --topic " + kafkaTopicName + "'";
			
			//(2)b. Producing messages and send to the topic created
			String kafkaProducerInitiateStr = "./kafka-console-producer.sh --broker-list " + currClusterBrokerAndPortList + " --security-protocol PLAINTEXTSASL ";
			
			String produceByFileSendMsgToTopicCmd = kafkaProducerInitiateStr + "--topic " + kafkaTopicName + " < " + enServerTestDataFileFullPathAndName;
			String produceByCmdInputSendMsgToTopicCmd = "echo '00301--{\"name\":\"Dan\", \"title\":\"Project Manager\"}' | " + kafkaProducerInitiateStr + "--topic " + kafkaTopicName + " --property parse.key=true --property key.separator=--";
			
			//(2)c. Consuming the messages from the topic
			String kafkaConsumer1InitiateStr = "./kafka-console-consumer.sh --zookeeper " + zookerQuorum4Kafka + " --security-protocol PLAINTEXTSASL --max-messages 100 --timeout-ms 1000  --from-beginning --property print.key=true --property key.separator=... ";
			String kafkaConsumer2InitiateStr = "./kafka-run-class.sh kafka.tools.SimpleConsumerShell --broker-list " + currClusterBrokerAndPortList + " --security-protocol PLAINTEXTSASL --max-messages 100 --property print.key=true --property key.separator=... --partition 0 --offset -2  --no-wait-at-logend ";
			
			String tempLocalKafkaTestResultPathAndName = localKafkaTestResultPathAndName.replace("_result.txt", "_result_temp.txt");
			
			//String consumeMsgsAndParseCmd0 = kafkaConsumer1InitiateStr + "--topic " + kafkaTopicName + " | grep 105 | cut -d',' -f1";
			String consumeMsgsAndParseCmd1 = kafkaConsumer1InitiateStr + "--topic " + kafkaTopicName + " | grep 105 | cut -d',' -f1 > " + tempLocalKafkaTestResultPathAndName;
			String consumeMsgsAndParseCmd2 = kafkaConsumer2InitiateStr + "--topic " + kafkaTopicName + " | grep 00301 | cut -d',' -f1 >> " + tempLocalKafkaTestResultPathAndName;
			
			//(2)d Actions of post Kafka Ops 
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + tempLocalKafkaTestResultPathAndName + " | tr '\\n' ',' ; } > " + localKafkaTestResultPathAndName;
			String removeTempLocalKafkaTestResultsFileCmd = "rm -f " + tempLocalKafkaTestResultPathAndName;
			String removeLocalKafkaTestResultsFileCmd = "rm -f " + localKafkaTestResultPathAndName;
			
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");	
			sb.append("cd /usr/hdp/current/kafka-broker/bin;\n");
			//sb.append(consumeMsgsAndParseCmd0 + ";\n");
		    sb.append(deleteTopicCmd + ";\n");
			sb.append(createTopicCmd + ";\n");	
			
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + kafkaTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + kafkaTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + kafkaTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + kafkaTestFolderName + "; \n");
		    		    
		    
			
			sb.append(produceByFileSendMsgToTopicCmd + ";\n");	
			sb.append(produceByCmdInputSendMsgToTopicCmd + ";\n");			
			
			sb.append(consumeMsgsAndParseCmd1 + ";\n");			
			sb.append(consumeMsgsAndParseCmd2 + ";\n");
					
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");
			sb.append(removeTempLocalKafkaTestResultsFileCmd + ";\n"); 
			
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localKafkaTestResultPathAndName + " " + activeNN_addr_port + kafkaTestResultHdfsPathAndName;
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
						
			sb.append(removeLocalKafkaTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + kafkaTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    
		    		    
		    prepareFolder(kafkaScriptFilesFoder, "Local Kafka Testing Script Files");
		    
		    String kafkaTopicMsgsTestScriptFullFilePathAndName = kafkaScriptFilesFoder + "dcTestKafka_TopicMessagesScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (kafkaTopicMsgsTestScriptFullFilePathAndName,  "Script File For Testing Kafka Topic and Messages Producing and Consuming on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String kafkaManagedTestingCmds = sb.toString();
			writeDataToAFile(kafkaTopicMsgsTestScriptFullFilePathAndName, kafkaManagedTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(kafkaTopicMsgsTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(kafkaTopicMsgsTestScriptFullFilePathAndName, 
					kafkaScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(kafkaTestResultHdfsPathAndName);			
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(kafkaTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(new Path(kafkaTestResultHdfsPathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";			
				while ((line = br.readLine()) != null) {				
					if (line.contains("null...105,00301...{\"name\":\"Dan\"")) {
						currTestScenarioSuccessStatus = true;				
					}							
				}//end while
				br.close();				
			} 			
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Kafka Internal Root-User Topic Deleting & Creating, Non-Root User Message "
						+ "\n          Producing (Cmd Input & File) and Consuming (Console-Consumer and SimpleConsumerShell) "
						+ "\n          for the Broker:Port List - " + currClusterBrokerAndPortList
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + kafkaTestResultHdfsPathAndName + "' \n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Kafka Internal Root-User Topic Deleting & Creating, Non-Root User Message "
						+ "\n          Producing (Cmd Input & File) and Consuming (Console-Consumer and SimpleConsumerShell) "
						+ "\n          for the Broker:Port List - " + currClusterBrokerAndPortList
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + kafkaTestResultHdfsPathAndName + "' \n";
			}
			writeDataToAFile(dcTestKafka_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end for 4
		
		
		
		//5. Loop through bdClusterEntryNodeList to internally drop existing kafka topic deletion and creation but 
		//     externally perform message producing and consuming on the kafka topic using Java programs	
		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		//double successTestScenarioNum = 0L;
		//clusterENNumber_Start = 4; //0..1..2..3..4..5
	    // clusterENNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestKafka_RecFilePathAndName, "[2]. Kafka Topic CLI Ops & Message Producing and Consuming by Java Programs \n", true);
		//clusterENNumber = 1;
		//clusterENNumber = bdClusterEntryNodeList.size();
		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //clusterENNumber..bdClusterEntryNodeList.size()..1	
			totalTestScenarioNumber++;
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Kafka Command Line Operations on Entry Node: " + tempENName);
			
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
			
			String loginUserName = bdENCmdFactory.getUsername(); 						
			String rootUserName = bdENRootCmdFactory.getUsername();
			//String rootPw = bdENRootCmdFactory.getRootPassword();			
			//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
			//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
			
			if (!loginUserName.equalsIgnoreCase(rootUserName)){				
				enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
			}			
			System.out.println("*** loginUserName is: " + loginUserName);
			LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory, bdENCmdFactory);
			System.out.println("*** On '" + tempENName + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
						 
						
			//(1) Move test data file to the local Linux server folder of the login user - enServerScriptFileDirectory
			//String enServerScriptFileDirectory = "/data/home/m041785/test";	
			//int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localKafkaJarFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localKafkaJarFileName, 
					bdClusterUATestResultsParentFolder, enServerScriptFileDirectory, bdENCmdFactory);
			
			tempClock = new DayClock();				
			tempTime = tempClock.getCurrentDateTime();
			
			if (exitVal1 == 0 ){
				System.out.println("\n*** Done - Moving Kafka Test Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			} else {
				System.out.println("\n*** Failed - Moving Kafka Test Jar File into '" + enServerScriptFileDirectory + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);
			}
			
						
			//(2) Generating commands for testing Kafka topic and message operations
			String kafkaTopicName = "testKafka_TS_Java"+ (i+1);	
			String zookerQuorum4Kafka = currBdCluster.getZookeeperQuorum().replaceAll(".edu,", ".edu:2181,");			
			String currNodeRootLoginPassWord = bdENRootCmdFactory.getRootPassword();						
			String currClusterBrokerAndPortList = currBdCluster.getKafkaBrokerAndPortList();
			
			System.out.println(" *** zookerQuorum4Kafka: " + zookerQuorum4Kafka);		
			System.out.println(" *** currClusterBrokerAndPortList: " + currClusterBrokerAndPortList);
			System.out.println(" *** currNodeRootLoginPassWord: " + currNodeRootLoginPassWord);
			System.out.println(" *** kafkaTopicName: " + kafkaTopicName);
			
			String enServerTestJarFileFullPathAndName = enServerScriptFileDirectory + localKafkaJarFileName;			
			System.out.println("\n *** enServerTestJarFileFullPathAndName: \n	---" + enServerTestJarFileFullPathAndName);		
				
			String kafkaTestResultFileName = kafkaTopicName + "_java_result.txt";
			String localKafkaTestResultPathAndName = enServerScriptFileDirectory + kafkaTestResultFileName;
			
			String kafkaTestResultHdfsPathAndName = kafkaTestFolderName + kafkaTestResultFileName;
			
			//(2)a. Topic Deletion and Creation Cmds from Root User			
			String kafkaTopicShellInitiateStr = "echo '" + currNodeRootLoginPassWord + "' | su - root -c  '/usr/hdp/current/kafka-broker/bin/kafka-topics.sh --zookeeper " + zookerQuorum4Kafka + " ";
			
			String deleteTopicCmd = kafkaTopicShellInitiateStr + "--delete --topic " + kafkaTopicName + "'";
			String createTopicCmd = kafkaTopicShellInitiateStr + "--replication-factor 1 --partitions 1 --create --topic " + kafkaTopicName + "'";
			
			//(2)b. Producing messages and send to the topic created (Kinit Method)
			String kafkClientJaas_Kinit_FileFullPathAndName = enServerScriptFileDirectory + ".kafka/kafka_client_jaas.conf";
			String kafkClientJaas_Keytab_FileFullPathAndName = enServerScriptFileDirectory + ".kafka/kafka_client_jaas_keytab.conf";
			
			//java -Djava.security.auth.login.config=/data/home/m041785/.kafka/kafka_client_jaas.conf -cp /data/home/m041785/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar kafka.dcProducer 10 testKafka_dequan;
			// or	java -Djava.security.auth.login.config=/data/home/m041785/.kafka/kafka_client_jaas_keytab.conf -cp /data/home/m041785/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar kafka.dcProducer 10 testKafka_dequan;
				
			String kafkaProducerInitiateStr = "java -Djava.security.auth.login.config=" + kafkClientJaas_Kinit_FileFullPathAndName + " -cp " + enServerTestJarFileFullPathAndName + " kafka.dcProducer ";
			String produceSendMsgByJavaToTopicCmd1 = kafkaProducerInitiateStr + "10 " + kafkaTopicName + " " + currClusterBrokerAndPortList;
			String produceSendMsgByJavaToTopicCmd2 = kafkaProducerInitiateStr + "5 " + kafkaTopicName + " " + currClusterBrokerAndPortList;
			
			
			//(2)c. Consuming the messages from the topic (KeyTab Method)
			//java -Djava.security.auth.login.config=/data/home/m041785/.kafka/kafka_client_jaas.conf -cp /data/home/m041785/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar kafka.dcConsumer 10 testKafka_dequan;
			//or java -Djava.security.auth.login.config=/data/home/m041785/.kafka/kafka_client_jaas_keytab.conf -cp /data/home/m041785/StormTest-0.0.2-SNAPSHOT-jar-with-dependencies.jar kafka.dcConsumer 10 testKafka_dequan;
				
			String kafkaConsumerInitiateStr = "java -Djava.security.auth.login.config=" + kafkClientJaas_Keytab_FileFullPathAndName + " -cp " + enServerTestJarFileFullPathAndName + " kafka.dcConsumer ";
			//String tempLocalKafkaTestResultPathAndName = localKafkaTestResultPathAndName.replace("_java_result.txt", "_java_result_temp.txt");
			String consumeMsgByJavaFromTopicCmd = kafkaConsumerInitiateStr + "100 " + kafkaTopicName  + " " + currClusterBrokerAndPortList + " " + localKafkaTestResultPathAndName;
			
			
			//(2)d Actions of post Kafka Ops			
			String removeLocalKafkaTestResultsFileCmd = "rm -f " + localKafkaTestResultPathAndName;
			
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory + ";\n");	
			sb.append("ln -s /data/home/.kafka .kafka;\n");	
			
			sb.append("cd /usr/hdp/current/kafka-broker/bin;\n");
			//sb.append(consumeMsgsAndParseCmd0 + ";\n");
		    sb.append(deleteTopicCmd + ";\n");
			sb.append(createTopicCmd + ";\n");	
			
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos			
			sb.append("hadoop fs -rm -r -skipTrash " + activeNN_addr_port + kafkaTestResultHdfsPathAndName + "; \n");
			sb.append("hadoop fs -mkdir -p " + activeNN_addr_port + kafkaTestFolderName + "; \n");		
			sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + activeNN_addr_port + kafkaTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + activeNN_addr_port + kafkaTestFolderName + "; \n");
		   		    			
			sb.append(produceSendMsgByJavaToTopicCmd1 + ";\n");	
			sb.append(produceSendMsgByJavaToTopicCmd2 + ";\n");			
			
			sb.append(consumeMsgByJavaFromTopicCmd + ";\n");			
						
			String copyLocalQueryResultsToHDFSCmds = "hadoop fs -copyFromLocal " + localKafkaTestResultPathAndName + " " + activeNN_addr_port + kafkaTestResultHdfsPathAndName;
			sb.append(copyLocalQueryResultsToHDFSCmds + "; \n");
						
			sb.append(removeLocalKafkaTestResultsFileCmd + "; \n");
			sb.append("hadoop fs -chmod -R 550 " + activeNN_addr_port + kafkaTestFolderName + "; \n");		    
		    sb.append("kdestroy;\n");
		    		    		    
		    prepareFolder(kafkaScriptFilesFoder, "Local Kafka Testing Script Files");
		    
		    String kafkaTopicMsgsTestScriptFullFilePathAndName = kafkaScriptFilesFoder + "dcTestKafka_TopicMessages_Java_ScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (kafkaTopicMsgsTestScriptFullFilePathAndName,  "Script File For Testing Kafka Topic and Java Messages Producing and Consuming on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
			
			String kafkaManagedTestingCmds = sb.toString();
			writeDataToAFile(kafkaTopicMsgsTestScriptFullFilePathAndName, kafkaManagedTestingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(kafkaTopicMsgsTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(kafkaTopicMsgsTestScriptFullFilePathAndName, 
					kafkaScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			boolean currTestScenarioSuccessStatus = false;
			Path filePath = new Path(kafkaTestResultHdfsPathAndName);			
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(kafkaTestFolderName + kafkaTestResultHdfsPathAndName);
				FileStatus[] status = currHadoopFS.listStatus(new Path(kafkaTestResultHdfsPathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";			
				while ((line = br.readLine()) != null) {				
					if (line.contains("*-* (10) 9: 10,value -")) {
						currTestScenarioSuccessStatus = true;				
					}							
				}//end while
				br.close();				
			} 			
						
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
			
			String testRecordInfo = "";	
			if (currTestScenarioSuccessStatus){
				successTestScenarioNum++;
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Kafka Internal Root-User Topic Deleting & Creating, Non-Root User External "
						+ "\n          Message  Producing and Consuming (Java Programs) (Authentication: Kinit & Keytab) "
						+ "\n          for the Broker:Port List - " + currClusterBrokerAndPortList
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + kafkaTestResultHdfsPathAndName + "' \n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Kafka Internal Root-User Topic Deleting & Creating, Non-Root User External "
						+ "\n          Message  Producing and Consuming (Java Programs) (Authentication: Kinit & Keytab) "
						+ "\n          for the Broker:Port List - " + currClusterBrokerAndPortList
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + tempENName + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
				        + "\n  -- (2) Generated Test Results File on HDFS/WebHDFS System:  '" + kafkaTestResultHdfsPathAndName + "' \n";
			}
			writeDataToAFile(dcTestKafka_RecFilePathAndName, testRecordInfo, true);
			prevTime = currTime;
		}//end for 5


  		

		
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100;		
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Internally (Kafka CLI) Topic Deleting and Creating, Intenally (Kafka CLI) and Externally "
					  +  "\n                    (Java Programs) Message Producing and Consuming on '" + bdClusterName + "' Cluster from " + clusterENNumber + " Entry Node(s)!"; //bdClusterEntryNodeList.size() 
		currNotingMsg += "\n***** Present Kafka Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS Files!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Kafka Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Kafka Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Kafka Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		writeDataToAFile(dcTestKafka_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestKafka_RecFilePathAndName));
	
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
