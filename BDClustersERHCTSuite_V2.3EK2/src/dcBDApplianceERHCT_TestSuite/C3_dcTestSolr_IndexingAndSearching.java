package dcBDApplianceERHCT_TestSuite;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import com.google.common.io.Files;

import dcModelClasses.DayClock;
import dcModelClasses.HdfsUtil;
import dcModelClasses.LoginUserUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 7/28/2016~8/8/2016
*/ 


public class C3_dcTestSolr_IndexingAndSearching {
	private static int testingTimesSeqNo = 1;
	private static String bdClusterName = "";
	private static String bdClusterUATestResultsParentFolder = "";
	private static String bdClusterUATestResultsFolder = "";	
	private static String localSolrTestDataFileName = "";
	private static String localSolrHadoopJobJarFileName = "";	
	private static String localSolrClientJaas_FileName = "";
	private static String localSolrConfigFolderName = "";
	
	private static String solrTestFolderName = "";
	
	private static String internalKinitCmdStr = "";
	private static String enServerScriptFileDirectory = "/home/hdfs/";
	
	private static int totalTestScenarioNumber = 0;
	private static double testSuccessRate = 0L;
	
	private static String entryNodeName_4CurlRunning = "EN01";
	private static String solrClusterSecurity = "";
		
	
	public static void main(String[] args) throws Exception {
		if (args.length < 10){
			System.out.println("\n*** 5+1 parameters for Solr-ERHCT have not been specified yet!");
			return;
		}
		
		testingTimesSeqNo = Integer.valueOf(args[0]);
		bdClusterName = args[1];
		bdClusterUATestResultsParentFolder = args[2];
		bdClusterUATestResultsFolder = args[3];	
		
		localSolrTestDataFileName = args[4];
		solrTestFolderName = args[5];
		solrClusterSecurity = args[6];
		localSolrHadoopJobJarFileName = args[7];
		localSolrClientJaas_FileName = args[8];
		localSolrConfigFolderName = args[10];
		
		internalKinitCmdStr = args[9];
		
				
		String [] internalKinitCmdStrSplit = internalKinitCmdStr.split("kinit "); //Enterprise-Kerberos
		String loginUser4AllNodesName = internalKinitCmdStrSplit[1].replace(";", "").trim();//Enterprise-Kerberos
		System.out.println("*** loginUser4AllNodesName is: " + loginUser4AllNodesName);		
		String loginUserMC_AD_Pw = internalKinitCmdStrSplit[0].replace("echo", "").replace("\"", "").replace("|", "").trim();
		System.out.println("\n*** loginUserMC_AD_Pw is: " + loginUserMC_AD_Pw);
		
		solrTestFolderName = "/user/" + loginUser4AllNodesName + "/test/Solr/";//Modify solrTestFolderName from "/data/test/Solr/"
						
		if (!solrTestFolderName.endsWith("/")){
			solrTestFolderName += "/";
		}
		if (!localSolrConfigFolderName.endsWith("/")){
			localSolrConfigFolderName += "/";
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
		String solrScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Solr\\";
	    prepareFolder(solrScriptFilesFoder, "Local Solr Testing Script Files");
	    
		String dcTestSolr_RecFilePathAndName = bdClusterUATestResultsFolder + "dcTestSolr_CollectioningAndSearching_Records_No" + testingTimesSeqNo + ".sql";
		prepareFile (dcTestSolr_RecFilePathAndName,  "Records of Testing Solr on '" + bdClusterName + "' Cluster");
						
		StringBuilder sb = new StringBuilder();
		sb.append("--*****  Records of Mayo Clinic Enterprise-Secured '"+ bdClusterName +"' Cluster Enterprise-Readiness Certification Testing Results  *****-- \n" );		    
	    sb.append("-----Automated Solr Internal Collectioning and Searching Representative Scenario Testing "
	    		+ "\n-- 						Using Software Created By: Dequan Chen, Ph.D. \n\n"); 
	    sb.append("--=-- Testing Results File - Generated Time: " + startTime + " \n" );
	    sb.append("--*-- Testing Times Sequence No:  " + testingTimesSeqNo + " \n" );
	    sb.append("--*-- 1 Testing Scenario == 1 Possible Enterprise Use Case for A Hadoop Cluster!\n" );
	    sb.append("--*-- Enterprise-Secured: Hadoop Cluster Is Protected by Kerberos, Active Directory, LDAP, Knox, Ranger, and OS Hardening!!\n\n" );
	    String testRecHeader = sb.toString();
		writeDataToAFile(dcTestSolr_RecFilePathAndName, testRecHeader, false);		
		sb.setLength(0);
		
		//3. Get cluster FileSystem and other information for testing		      
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterSolrInstalledNodeList = currBdCluster.getCurrentClusterSolrInstalledNodeList();
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		String hdfsActiveNnIPAddressAndPort = currBdCluster.getBdHdfsActiveNnIPAddressAndPort(); 
		System.out.println("\n--- hdfsActiveNnIPAddressAndPort on '" + bdClusterName + "' Cluster: " + hdfsActiveNnIPAddressAndPort);
		
		BdNode aBDNode = new BdNode(entryNodeName_4CurlRunning, bdClusterName);
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
		System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		
		String loginUserName = bdENCmdFactory.getUsername(); 						
		String rootUserName = bdENRootCmdFactory.getUsername();
		String rootPw = bdENRootCmdFactory.getRootPassword();			
		//System.out.println(" *** Root User Name / Password: " + rootUserName + " / " + rootPw);
		//String currEnSudoToRootCmd = "echo \"" + rootPw + "\" | sudo -S echo && sudo su - root";
		
		if (!loginUserName.equalsIgnoreCase(rootUserName)){				
			enServerScriptFileDirectory = "/data/home/" + loginUserName + "/test/";				
		}	
		String enServerScriptFileDirectory_solr = enServerScriptFileDirectory + "solr/";
		System.out.println("*** loginUserName is: " + loginUserName);
		LoginUserUtil.safelyCreateAFolderInHomeFolderByLoginUser_OnEntryNodeLocal_OnBDCluster(enServerScriptFileDirectory_solr, bdENCmdFactory);
		System.out.println("*** On '" + entryNodeName_4CurlRunning + "'server, created enServerScriptFileDirectory: " + enServerScriptFileDirectory);
		
		ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
		double successTestScenarioNum = 0L;
		int solrInstalledDNNumber = bdClusterSolrInstalledNodeList.size();		
		int solrInstalledDNNumber_Start = 0; //0..1..2..3..4..5
		
		//solrInstalledDNNumber = 1; //1..2..3..4..5		
		//String hdfsInternalPrincipal = currBdCluster.getHdfsInternalPrincipal();
		//String hdfsInternalKeyTabFilePathAndName = currBdCluster.getHdfsInternalKeyTabFilePathAndName();
		
		//Get the local Solr Jaas configuration file to be cluster-specific for a secured Solr cloud
		if (solrClusterSecurity.equalsIgnoreCase("secured")){
			String bdClusterKnoxIdName = currBdCluster.getBdClusterIdName();
			String srcFileFullPathAndName = localSolrConfigFolderName + localSolrClientJaas_FileName;
			String destFileName = localSolrClientJaas_FileName.replace("_jaas.conf", "_jaas_" + bdClusterName + ".conf");
			String destFileFullPathAndName = localSolrConfigFolderName + destFileName;
			Files.copy(new File (srcFileFullPathAndName), new File (destFileFullPathAndName));
			
			updateLocalSolrJaasConfFile (destFileFullPathAndName, bdClusterKnoxIdName);
			
			localSolrClientJaas_FileName = destFileName;
			System.out.println("*** localSolrClientJaas_FileName: " + localSolrClientJaas_FileName );
		}
		
				
		//Get activeSolrServiceNumber 
		String solrInstalledNodeStatusFileName = "solr_installedNodesServiceStatus.txt";
		String enServerSolrInstalledNodeStatusFilePathAndName = enServerScriptFileDirectory_solr + solrInstalledNodeStatusFileName;
		String hdfsSolrInstalledNodeStatusFilePathAndName = solrTestFolderName + solrInstalledNodeStatusFileName;
								
		String solrServer_TruststoreJks_Pw = "solr123";
		String solrClient_Keytab_FileName = "solr_client_jaas.keytab";			
		int activeSolrServiceNumber = 0;
		if (solrClusterSecurity.equalsIgnoreCase("secured")){
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory_solr + ";\n");				
			sb.append("kdestroy;\n");
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
						    
		    sb.append("hadoop fs -mkdir -p " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + solrTestFolderName + "; \n");
		    
		    for (int i = solrInstalledDNNumber_Start; i < solrInstalledDNNumber; i++){
				String tempSolrInstalledNodeName = bdClusterSolrInstalledNodeList.get(i).toUpperCase();
				BdNode querySolrBDNode = new BdNode(tempSolrInstalledNodeName, bdClusterName);
				ULServerCommandFactory bdSolrNodeCmdFactory = querySolrBDNode.getBdENCmdFactory();	
				
				String solrNodeFQDN = bdSolrNodeCmdFactory.getServerURI();
				//curl -k --negotiate -u : "https://hdpr04dn02.mayo.edu:8983/solr/admin/collections?action=LIST&indent=true" | grep status;
				String querySolrNodeStateFindingCmd = "curl -k --negotiate -u : \"https://" + solrNodeFQDN + ":8983/solr/admin/collections?action=LIST&indent=true\" | grep status";
				if (i ==0){
					querySolrNodeStateFindingCmd += " > " + enServerSolrInstalledNodeStatusFilePathAndName;
				} else {
					querySolrNodeStateFindingCmd += " >> " + enServerSolrInstalledNodeStatusFilePathAndName;
				}
				
				sb.append(querySolrNodeStateFindingCmd + ";\n");
				
				String currSolrNode = tempSolrInstalledNodeName.toLowerCase();
				String currSolrNodeTrustStoreAlias = "solr_" + bdClusterName.toLowerCase() + "_" + currSolrNode;
				String solrServer_TruststoreJks_FileName =  currSolrNodeTrustStoreAlias + ".jks";	//solrServer_TruststoreJks_FileName.replace("dn02.jks", currSolrNode + ".jks");
				String solrServer_TruststoreJks_FileFullPathAndName = enServerScriptFileDirectory_solr + solrServer_TruststoreJks_FileName;
				
				String solrServer_TruststoreCrt_FileFullPathAndName = solrServer_TruststoreJks_FileFullPathAndName.replace(".jks", ".crt");
				
				//openssl s_client -showcerts -connect hdpr04dn02.mayo.edu:8983 </dev/null 2>/dev/null > /data/home/m041785/test/solr/solr_sdbx_dn02.crt;				
				String genSolrNodeTruststoreCrtCmd = "openssl s_client -showcerts "
						+ "-connect "+ solrNodeFQDN 
						+ ":8983 </dev/null 2>/dev/null > "	+ solrServer_TruststoreCrt_FileFullPathAndName;
				sb.append(genSolrNodeTruststoreCrtCmd + ";\n");
				
				//keytool -import -keystore /data/home/m041785/test/keytabs/solr_sdbx_dn02.jks -file /data/home/m041785/test/keytabs/solr_sdbx_dn02.crt -storepass solr123 -alias solr_sdbx_dn02 -noprompt;
				String genSolrNodeTruststoreJksCmd = "keytool -import -keystore " + solrServer_TruststoreJks_FileFullPathAndName
						+ " -file "+ solrServer_TruststoreCrt_FileFullPathAndName
						+ " -storepass "+ solrServer_TruststoreJks_Pw
						+ " -alias "+ currSolrNodeTrustStoreAlias
						+ " -noprompt";
				sb.append(genSolrNodeTruststoreJksCmd + ";\n");
		    }
		    
		    String genSolrClientKeytabFileCmd = "echo '" + rootPw + "' | su - root -c  'scp /etc/security/keytabs/hdfs.headless.keytab " 
		    			+ enServerScriptFileDirectory_solr + solrClient_Keytab_FileName 
		    			+ "; chown " + loginUserName + ":users -R " + enServerScriptFileDirectory_solr 
		    			+ "; chmod 755 -R  "+ enServerScriptFileDirectory_solr
		    			+ "'";
		    sb.append(genSolrClientKeytabFileCmd + ";\n");
		    		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsSolrInstalledNodeStatusFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enServerSolrInstalledNodeStatusFilePathAndName + " " + hdfsSolrInstalledNodeStatusFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + solrTestFolderName + "; \n");		    
		    //sb.append("rm -f " + enServerSolrInstalledNodeStatusFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");		   
			
		    String solrInstalledNodeServiceStatusScriptFilePathAndName = solrScriptFilesFoder + "dcTestSolr_InstalledNodesServiceStatusFindingScriptFile.sh";			
			prepareFile (solrInstalledNodeServiceStatusScriptFilePathAndName,  "Script File For Finding Installed Solr Service Status on '" + bdClusterName + "' Cluster Entry Node - " + entryNodeName_4CurlRunning);
			
			String solrInstalledNodeServiceStatusFindingCmds = sb.toString();
			writeDataToAFile(solrInstalledNodeServiceStatusScriptFilePathAndName, solrInstalledNodeServiceStatusFindingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(solrInstalledNodeServiceStatusScriptFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(solrInstalledNodeServiceStatusScriptFilePathAndName, 
					solrScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			Path filePath = new Path(hdfsSolrInstalledNodeStatusFilePathAndName);
			boolean solrStatusHdfsFileExistingStatus = false;
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsSolrInstalledNodeStatusFilePathAndName);	
				solrStatusHdfsFileExistingStatus = true;				
			}
			System.out.println("\n*** Exisiting status for " + hdfsSolrInstalledNodeStatusFilePathAndName + ": " + hdfsSolrInstalledNodeStatusFilePathAndName);
			
			String targetFoundString = "<int name=\"status\">0</int>";				
			if (solrStatusHdfsFileExistingStatus){ 
				try {							
					FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsSolrInstalledNodeStatusFilePathAndName));				
					BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
					String line = "";				
					while ((line = br.readLine()) != null) {
						System.out.println("*** line: " + line );
						if (line.contains(targetFoundString)) {												
							activeSolrServiceNumber++;					
						}						
					}//end while
					br.close();				
								
				} catch (IOException e) {				
					e.printStackTrace();				
				}//end try   
			}//end if			
			
		} else {
			for (int i = solrInstalledDNNumber_Start; i < solrInstalledDNNumber; i++){
				String tempSolrInstalledNodeName = bdClusterSolrInstalledNodeList.get(i).toUpperCase();		
				String solrInstalledNodeServiceUpStatus = "";
				solrInstalledNodeServiceUpStatus = HdfsUtil.getSolrInstalledNodeServiceStateByWeb(bdClusterName, tempSolrInstalledNodeName, solrClusterSecurity);
				if (solrInstalledNodeServiceUpStatus.equalsIgnoreCase("up")){
					activeSolrServiceNumber++;
				}					
			}
		}		
		
		System.out.println("*** activeSolrServiceNumber: " + activeSolrServiceNumber );
		
		if (activeSolrServiceNumber == 0){			
			String testRecordInfo = "No Solr Services Are Up or Available for Testing, and ERHCT is Now Exiting...";			
			writeDataToAFile(dcTestSolr_RecFilePathAndName, testRecordInfo, true);	
			return;
		}
		
		
		//4. Move Solr Test Data To entryNodeName_4CurlRunning Server Node		
		String enServerTestDataFileFullPathAndName = enServerScriptFileDirectory_solr + localSolrTestDataFileName;
		String enServerSolrHadoopJobJarFileFullPathAndName = enServerScriptFileDirectory_solr + localSolrHadoopJobJarFileName;
				
		int exitVal1 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localSolrTestDataFileName, bdClusterUATestResultsParentFolder, enServerScriptFileDirectory_solr, bdENCmdFactory);
		int exitVal2 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localSolrHadoopJobJarFileName, localSolrConfigFolderName, enServerScriptFileDirectory_solr, bdENCmdFactory);
				
		DayClock prevClock = new DayClock();				
		String prevTime = prevClock.getCurrentDateTime();
		
		if (exitVal1 == 0 ){
			System.out.println("\n*** Done - Moving Solr Test Data File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
		} else {
			System.out.println("\n*** Failed - Moving Solr Test Data File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
		}
		if (exitVal2 == 0 ){
			System.out.println("\n*** Done - Moving Solr Hadoop Job Jar File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
		} else {
			System.out.println("\n*** Failed - Moving Solr Hadoop Job Jar File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
		}
				
		if (solrClusterSecurity.equalsIgnoreCase("secured")){
			prevClock = new DayClock();				
			prevTime = prevClock.getCurrentDateTime();		
			int exitVal3 = LoginUserUtil.copyFile_FromWindowsLocal_ToEntryNodeLoginUserHomeFolder_OnBDCluster(localSolrClientJaas_FileName, localSolrConfigFolderName, enServerScriptFileDirectory_solr, bdENCmdFactory);
			if (exitVal3 == 0 ){
				System.out.println("\n*** Done - Moving Solr Jaas Conf File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
			} else {
				System.out.println("\n*** Failed - Moving Solr Jaas Conf File into '" + enServerScriptFileDirectory_solr + "' folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + prevTime);
			}
			
		}
				
		//5. Loop through bdClusterSolrInstalledNodeList to delete and create an Solr collection, create
		//     a Solr document(s) (from HDFS CSV file) and refresh the Solr collection and querying the collection	
		//solrInstalledDNNumber_Start = 0; //0..1..2..3..4..5
	    //solrInstalledDNNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestSolr_RecFilePathAndName, "[1]. Solr CLI Operations Through Solr Rest Services  \n", true);
		for (int i = solrInstalledDNNumber_Start; i < solrInstalledDNNumber; i++){ //bdClusterSolrInstalledNodeList.size()..1..solrInstalledDNNumber	
			totalTestScenarioNumber++;
			String tempSolrInstalledNodeName = bdClusterSolrInstalledNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Solr CLI Operations Using Solr Rest Service on the Node: " + tempSolrInstalledNodeName);
			
			//(1) Get Solr Host Web IP Address and Port String
			BdNode tempSolrBDNode = new BdNode(tempSolrInstalledNodeName, bdClusterName);
			ULServerCommandFactory bdSolrNodeCmdFactory = tempSolrBDNode.getBdENCmdFactory();			
			String solrRestServiceIpAddressAndPort_unSecure = "http://" + bdSolrNodeCmdFactory.getServerURI()+ ":8983/solr";
			System.out.println(" *** solrRestServiceIpAddressAndPort_unSecure: " + solrRestServiceIpAddressAndPort_unSecure);
			
			String solrRestServiceIpAddressAndPort_secure = "https://" + bdSolrNodeCmdFactory.getServerURI() + ":8983/solr";
			System.out.println(" *** solrRestServiceIpAddressAndPort_secure: " + solrRestServiceIpAddressAndPort_secure);
					
									
			//(2) Testing Solr Collection Deleting, Creating, Loading, Updating and Searching Using Data-Driven-Schema-Config
			String solrRestServiceIpAddressAndPort = solrRestServiceIpAddressAndPort_unSecure;
			if (solrClusterSecurity.equalsIgnoreCase("secured")){
				solrRestServiceIpAddressAndPort = solrRestServiceIpAddressAndPort_secure;
			}
			
			System.out.println(" *** solrRestServiceIpAddressAndPort: " + solrRestServiceIpAddressAndPort);
			
			String solrTestCollectionName = "solr_test";
			
			int activeSolrRestServiceNumber = activeSolrServiceNumber;
			if (activeSolrRestServiceNumber == 0){
				activeSolrRestServiceNumber = 1;
			}						
			int solrCollectionShardsNum = activeSolrRestServiceNumber; //solrInstalledDNNumber;
			int solrCollectionReplicationFactorNum = activeSolrRestServiceNumber;
			
			// /data/home/m041785/test/solr/dcFSETestData.csv
			// or: enServerScriptFileDirectory_solr + "dcFSETestData.csv";
			String enServerSorlTestDataCsvFilePathAndName = enServerTestDataFileFullPathAndName; 
			
			String solrTestCollectionSearchingStatusFileName =  "solr_curl_query_result" + (i+1) + ".txt";			
			String enSolrTestCollectionSearchingStatusFilePathAndName = enServerScriptFileDirectory_solr + solrTestCollectionSearchingStatusFileName;
			String enSolrTestCollectionSearchingStatusFilePathAndName_temp = enSolrTestCollectionSearchingStatusFilePathAndName.replace(".txt", "_temp.txt");
			String hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName = solrTestFolderName + solrTestCollectionSearchingStatusFileName;
			
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/admin/collections?action=DELETE&name=solr_test";			
			String deleteSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort + "/admin/collections?action=DELETE&name=" + solrTestCollectionName + "\"";
								
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/admin/collections?action=CREATE&name=solr_test&numShards=2&replicationFactor=1&collection.configName=data_driven_schema_config";
			String createSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort 
					+ "/admin/collections?action=CREATE&name=" + solrTestCollectionName 
					+ "&numShards=" + solrCollectionShardsNum 
					+ "&replicationFactor=" + solrCollectionReplicationFactorNum 
					+ "&collection.configName=data_driven_schema_config"
					+ "\" && sleep 10";
			if (solrClusterSecurity.equalsIgnoreCase("secured")){
				createSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort 
						+ "/admin/collections?action=CREATE&name=" + solrTestCollectionName 
						+ "&numShards=" + solrCollectionShardsNum 
						+ "&replicationFactor=" + solrCollectionReplicationFactorNum 
						+ "&collection.configName=data_driven_schema_config"
						+ "\" && sleep 10";
			}
					
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update/csv?commit=true" --data-binary @/data/home/m041785/test/solr/dcFSETestData.csv -H 'Content-type:text/csv';
			String loadDocFromCSVFileCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update/csv?commit=true\" --data-binary @" + enServerSorlTestDataCsvFilePathAndName
					+ " -H 'Content-type:text/csv'";
			
			//curl -k --negotiate -u : -X POST -H 'Content-type:application/json' http://hdpr03dn02.mayo.edu:8983/solr/solr_test/schema --data-binary '{ "replace-field":{"name":"salary","type":"long","indexed":true,"stored":true,"multiValued":false}}';
			String alterSchemaCmd = "curl -k --negotiate -u : -X POST -H 'Content-type:application/json' " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/schema --data-binary '{\"replace-field\":{"
					+ "\"name\":\"salary\","
					+ "\"type\":\"double\","
					+ "\"indexed\":true,"
					+ "\"stored\":true,"
					+ "\"multiValued\":false}}'";
			
			String reIndexingPostSchemaChangeCmd = loadDocFromCSVFileCmd;
			
			//curl -k --negotiate -u : http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update?commit=true -d '<add><doc><field name="id">107</field><field name="firstName">Brian</field><field name="lastName">Zhang</field><field name="salary">110000</field><field name="gender">M</field><field name="state">Iowa</field></doc></add>';
			String loadDocInXmlFormatCmd1 = "curl -k --negotiate -u : " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update?commit=true -d '<add><doc>"
					+ "<field name=\"id\">107</field>"
					+ "<field name=\"firstName\">Brian</field>"
					+ "<field name=\"lastName\">Zhang</field>"
					+ "<field name=\"salary\">110000</field>"
					+ "<field name=\"gender\">M</field>"
					+ "<field name=\"state\">Iowa</field>"
					+ "</doc></add>'";
			
			//curl -k --negotiate -u : http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update?commit=true  -d '[{"id":"108","firstName":"Marry","lastName":"Wang","salary":"137000","gender":"M","state":"state"}]';
			String loadDocInJsonFormatCmd2 = "curl -k --negotiate -u : " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update?commit=true -d '[{"
					+ "\"id\":\"108\","
					+ "\"firstName\":\"Marry\","
					+ "\"lastName\":\"Wang\","
					+ "\"salary\":\"137000\","
					+ "\"gender\":\"M\","
					+ "\"state\":\"state\""
					+ "}]'";
			
			//curl -k --negotiate -u : http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update?commit=true  -d '[{"id":"108", "salary":{"set":"140000"}}]';
			String updateDocCmd = "curl -k --negotiate -u : " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update?commit=true -d '[{"
					+ "\"id\":\"108\", "
					+ "\"salary\":{\"set\":\"140000\""
					+ "}}]'";
			
			
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=id:101&indent=true&omitHeader=true" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 > /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			String searchIdBySelectCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=id:101&indent=true&omitHeader=true\" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 > "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;

			//curl -k --negotiate -u : -g http://hdpr03dn02.mayo.edu:8983/solr/solr_test/query -d '{"query":"id:102"}'| grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			String searchIdByQueryCmd = "curl -k --negotiate -u : -g " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/query -d '{\"query\":\"id:102\"}'| grep salary | tr -d ',' | cut -d':' -f2 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;

			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?ids=103&csv.header=false&wt=csv" | cut -d',' -f6 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=104&omitHeader=true&wt=json" | grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=105&omitHeader=true&wt=python" | grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=106&omitHeader=true&wt=xml" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=107&omitHeader=true&wt=php" | grep salary | cut -d'>' -f2 | tr -d ',' >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=108&omitHeader=true&wt=ruby" | grep salary | cut -d'>' -f2 | tr -d ',' >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;

			String searchIdByGet_CsvCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=103&csv.header=false&wt=csv\" | cut -d',' -f6 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_JsonCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=104&csv.header=false&wt=json\" | grep salary | tr -d ',' | cut -d':' -f2 >>"
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_PythonCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=105&csv.header=false&wt=python\" | grep salary | tr -d ',' | cut -d':' -f2 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_XmlCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=106&csv.header=false&wt=xml\" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_PhpCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=107&csv.header=false&wt=php\" | grep salary | cut -d'>' -f2 | tr -d ',' >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_RubyCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=108&csv.header=false&wt=ruby\" | grep salary | cut -d'>' -f2 | tr -d ',' >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;

		
			//curl http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update?commit=true -H "Content-Type: text/xml" --data-binary '<delete><id>108</id></delete>';
			String deleteDocCmd = "curl -k --negotiate -u : " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update?commit=true -H \"Content-Type: text/xml\" --data-binary '<delete>"
					+ "<id>108</id>"
					+ "</delete>'";
			
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=*:*&sort=salary%20asc&omitHeader=true&wt=json&start=100" | cut -d':' -f3 | cut -d',' -f1 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=salary:[*%20TO%20100000]&sort=salary%20asc&omitHeader=true&wt=json&start=100" | cut -d':' -f3 | cut -d',' -f1 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=*:*&json.facet={avg_salary:'avg(salary)'}&indent=true&omitHeader=true&wt=xml" | grep avg_salary | cut -d'>' -f2 | cut -d'<' -f1 >> /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt;
			String searchAllDocCountCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=*:*&sort=salary%20asc&omitHeader=true&wt=json&start=100\" | cut -d':' -f3 | cut -d',' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchRangeDocCountCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=salary:[*%20TO%20100000]&sort=salary%20asc&omitHeader=true&wt=json&start=100\" | cut -d':' -f3 | cut -d',' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
 
			String searchAverageSalaryCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=*:*&json.facet={avg_salary:'avg(salary)'}&indent=true&omitHeader=true&wt=xml\" | grep avg_salary | cut -d'>' -f2 | cut -d'<' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			//{ cat /data/home/m041785/test/solr/solr_curl_query_result1_temp.txt | tr '\n' ',' ; } > /data/home/m041785/test/solr/solr_curl_query_result1.txt;			
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + enSolrTestCollectionSearchingStatusFilePathAndName_temp + " | tr '\\n' ',' ; } > " + enSolrTestCollectionSearchingStatusFilePathAndName;
			String removeTempLocalSolrTestResultsFileCmd = "rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName_temp;			
					
			//String removeLocalSolrTestResultsFileCmd = "rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName;		
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory_solr + ";\n");
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
						    
		    sb.append("hadoop fs -mkdir -p " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + solrTestFolderName + "; \n");		   
		    
			sb.append(deleteSolrTestCollectionCmd + ";\n");	
			sb.append(createSolrTestCollectionCmd + ";\n");	
			sb.append(loadDocFromCSVFileCmd + ";\n");	
			sb.append(alterSchemaCmd + ";\n");	
			sb.append(reIndexingPostSchemaChangeCmd + ";\n");			
			sb.append(loadDocInXmlFormatCmd1 + ";\n");	
			sb.append(loadDocInJsonFormatCmd2 + ";\n");	
			sb.append(updateDocCmd + ";\n");
			
			sb.append(searchIdBySelectCmd + ";\n");	
			sb.append(searchIdByQueryCmd + ";\n");			
			sb.append(searchIdByGet_CsvCmd + ";\n");			
			sb.append(searchIdByGet_JsonCmd + ";\n");
			sb.append(searchIdByGet_PythonCmd + ";\n");	
			sb.append(searchIdByGet_XmlCmd + ";\n");
			sb.append(searchIdByGet_PhpCmd + ";\n");	
			sb.append(searchIdByGet_RubyCmd + ";\n");
			
			sb.append(deleteDocCmd + ";\n");
			
			sb.append(searchAllDocCountCmd + ";\n");	
			sb.append(searchRangeDocCountCmd + ";\n");	
			sb.append(searchAverageSalaryCmd + ";\n");
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");	
			sb.append(removeTempLocalSolrTestResultsFileCmd + ";\n");				
		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enSolrTestCollectionSearchingStatusFilePathAndName + " " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + solrTestFolderName + "; \n");		    
		    sb.append("rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");		    	
			
		    
		    
		    String solrCollectionCollectioningAndSearchingTestScriptFilePathAndName = solrScriptFilesFoder + "dcTestSolr_CollectioningAndSearchingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (solrCollectionCollectioningAndSearchingTestScriptFilePathAndName,  "Script File For Testing Solr Collectioning and Searching on '" + bdClusterName + "' Cluster Solr Installed Node - " + tempSolrInstalledNodeName);
			
			String solrCollectionCollectioningAndSearchingCmds = sb.toString();
			writeDataToAFile(solrCollectionCollectioningAndSearchingTestScriptFilePathAndName, solrCollectionCollectioningAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(solrCollectionCollectioningAndSearchingTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(solrCollectionCollectioningAndSearchingTestScriptFilePathAndName, 
					solrScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			Path filePath = new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName);
			boolean solrStatusHdfsFileExistingStatus = false;
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName);	
				solrStatusHdfsFileExistingStatus = true;				
			}
			System.out.println("\n*** Exisiting status for " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + ": " + solrStatusHdfsFileExistingStatus);
			
			//String targetFoundString = "90000,125000,Texas,120000,45500,250000,110000,140000,7,3,113642.85714285714,";			
			//String targetFoundString = "90000.0,125000.0,Texas,120000.0,45500.0,250000.0,110000.0,140000.0,7,3,113642.85714285714,";
			String targetFoundString = "90000.0,125000.0,Texas,120000.0,45500.0,250000.0,110000.0,140000.0,7,3,113642.85";
			boolean solrSearchingSuccessStatus = false;			
			if (solrStatusHdfsFileExistingStatus){ 
				try {							
					FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName));				
					BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
					String line = "";				
					while ((line = br.readLine()) != null) {
						System.out.println("*** line: " + line );
						if (line.contains(targetFoundString)) {												
							solrSearchingSuccessStatus = true;					
						}						
					}//end while
					br.close();				
								
				} catch (IOException e) {				
					e.printStackTrace();				
				}//end try   
			}//end if			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
						
			String testRecordInfo = "";			
			if (solrSearchingSuccessStatus == true){
				successTestScenarioNum++;				
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Solr Rest API Collection Deleting & Creating, Schema Altering, Document Operations (Indexing/"
						+ "\n          Re-Indexing - From A File {CSV...} or Inline {Json & Xml}, Updating and Deleting), and Searching"
						+ "\n          (Get, Query, Select) (Id, Count, Range, Aggregate...) (Output: csv, json, xml, python, ruby, php)"
						+ "\n          via Solr Rest Service URL - " + solrRestServiceIpAddressAndPort
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + entryNodeName_4CurlRunning + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Solr Cloud Security - '" + solrClusterSecurity + "'; Active Service Number - '" + activeSolrServiceNumber + "'; Installed Nodes Number - '" + solrInstalledDNNumber+"'"
						+ "\n  -- (3) Generated Test Results File in HDFS System:  '" + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "' \n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  -  # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Solr Rest API Collection Deleting & Creating, Schema Altering, Document Operations (Indexing/"
						+ "\n          Re-Indexing - From A File {CSV...} or Inline {Json & Xml}, Updating and Deleting), and Searching"
						+ "\n          (Get, Query, Select) (Id, Count, Range, Aggregate...) (Output: csv, json, xml, python, ruby, php)"
						+ "\n          via Solr Rest Service URL - " + solrRestServiceIpAddressAndPort
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + entryNodeName_4CurlRunning + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Solr Cloud Security - '" + solrClusterSecurity + "'; Active Service Number - '" + activeSolrServiceNumber + "'; Installed Nodes Number - '" + solrInstalledDNNumber+"'"
						+ "\n  -- (3) Generated Test Results File in HDFS System:  '" + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "' \n";
			}
			writeDataToAFile(dcTestSolr_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for for 5.
						
		
		//6. Loop through bdClusterSolrInstalledNodeList to delete and create an Solr collection, create
		//     a Solr document(s) and refresh the Solr collection and querying the collection	
		//solrInstalledDNNumber_Start = 0; //0..1..2..3..4..5
	    //solrInstalledDNNumber = 1; //1..2..3..4..5..6
		writeDataToAFile(dcTestSolr_RecFilePathAndName, "\n[2]. Solr Document Loading From HDFS\n", true);
		for (int i = solrInstalledDNNumber_Start; i < solrInstalledDNNumber; i++){ //bdClusterSolrInstalledNodeList.size()..1..solrInstalledDNNumber	
			totalTestScenarioNumber++;
			String tempSolrInstalledNodeName = bdClusterSolrInstalledNodeList.get(i).toUpperCase();			
			System.out.println("\n--- (" + (i+1) + ") Testing Solr Docuemtns Loading from HDFS Files Partially By Solr Rest Service on the Node: " + tempSolrInstalledNodeName);
			
			//(1) Get Solr Host Web IP Address and Port String
			BdNode tempSolrBDNode = new BdNode(tempSolrInstalledNodeName, bdClusterName);
			ULServerCommandFactory bdSolrNodeCmdFactory = tempSolrBDNode.getBdENCmdFactory();			
			String solrRestServiceIpAddressAndPort_unSecure = "http://" + bdSolrNodeCmdFactory.getServerURI()+ ":8983/solr";
			System.out.println(" *** solrRestServiceIpAddressAndPort_unSecure: " + solrRestServiceIpAddressAndPort_unSecure);
			
			String solrRestServiceIpAddressAndPort_secure = "https://" + bdSolrNodeCmdFactory.getServerURI() + ":8983/solr";
			System.out.println(" *** solrRestServiceIpAddressAndPort_secure: " + solrRestServiceIpAddressAndPort_secure);
					
									
			//(2) Testing Solr Collection Deleting, Creating, Loading, Updating and Searching Using Data-Driven-Schema-Config
			String solrRestServiceIpAddressAndPort = solrRestServiceIpAddressAndPort_unSecure;
			if (solrClusterSecurity.equalsIgnoreCase("secured")){
				solrRestServiceIpAddressAndPort = solrRestServiceIpAddressAndPort_secure;
			}
			
			System.out.println(" *** solrRestServiceIpAddressAndPort: " + solrRestServiceIpAddressAndPort);
			
			String solrTestCollectionName = "solr_test";
			
			int activeSolrRestServiceNumber = activeSolrServiceNumber;
			if (activeSolrRestServiceNumber == 0){
				activeSolrRestServiceNumber = 1;
			}						
			int solrCollectionShardsNum = activeSolrRestServiceNumber; //solrInstalledDNNumber;
			int solrCollectionReplicationFactorNum = activeSolrRestServiceNumber;
			
			// /data/home/m041785/test/solr/dcFSETestData.csv
			// or: enServerScriptFileDirectory_solr + "dcFSETestData.csv";
			String enServerSorlTestDataCsvFilePathAndName = enServerTestDataFileFullPathAndName; 
			
			String solrTestCollectionSearchingStatusFileName =  "solr_hdfsLoad_query_result" + (i+1) + ".txt";			
			String enSolrTestCollectionSearchingStatusFilePathAndName = enServerScriptFileDirectory_solr + solrTestCollectionSearchingStatusFileName;
			String enSolrTestCollectionSearchingStatusFilePathAndName_temp = enSolrTestCollectionSearchingStatusFilePathAndName.replace(".txt", "_temp.txt");
			String hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName = solrTestFolderName + solrTestCollectionSearchingStatusFileName;
			
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/admin/collections?action=DELETE&name=solr_test";			
			String deleteSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort + "/admin/collections?action=DELETE&name=" + solrTestCollectionName + "\"";
								
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/admin/collections?action=CREATE&name=solr_test&numShards=2&replicationFactor=1&collection.configName=data_driven_schema_config";
			String createSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort 
					+ "/admin/collections?action=CREATE&name=" + solrTestCollectionName 
					+ "&numShards=" + solrCollectionShardsNum 
					+ "&replicationFactor=" + solrCollectionReplicationFactorNum 
					//+ "&collection.configName=data_driven_schema_config"
					+ "\" && sleep 10";
			if (solrClusterSecurity.equalsIgnoreCase("secured")){
				createSolrTestCollectionCmd = "curl -k --negotiate -u : \"" + solrRestServiceIpAddressAndPort 
						+ "/admin/collections?action=CREATE&name=" + solrTestCollectionName 
						+ "&numShards=" + solrCollectionShardsNum 
						+ "&replicationFactor=" + solrCollectionReplicationFactorNum 
						+ "&collection.configName=data_driven_schema_config"
						+ "\" && sleep 10";
			}
			
			//curl -k --negotiate -u : -X POST -H 'Content-type:application/json' http://hdpr03dn02.mayo.edu:8983/solr/solr_test/schema --data-binary '{ "replace-field":{"name":"salary","type":"long","indexed":true,"stored":true,"multiValued":false}}';
			String alterSchemaCmd = "curl -k --negotiate -u : -X POST -H 'Content-type:application/json' " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/schema --data-binary '{\"replace-field\":{"
					+ "\"name\":\"salary\","
					+ "\"type\":\"double\","
					+ "\"indexed\":true,"
					+ "\"stored\":true,"
					+ "\"multiValued\":false}}'";
					
			//String hdfsTestDataFileFullPathAndName = solrTestFolderName + "hdfs" + localSolrTestDataFileName;
			String hdfsTestDataFolderPathAndName = solrTestFolderName + "hdfs/"; 
			String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
			
			//curl -k --negotiate -u : "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update/csv?commit=true" --data-binary @/data/home/m041785/test/solr/dcFSETestData.csv -H 'Content-type:text/csv';
			// /usr/bin/hadoop jar /data/home/m041785/test/solr/solr-hadoop-job-2.2.1.jar com.lucidworks.hadoop.ingest.IngestJob -DcsvFieldMapping=0=id,1=firstName,2=lastName,3=salary,4=gender,5=state -DcsvFirstLineComment=true -DidField=id -DcsvDelimiter="," -Dlww.commit.on.close=true -cls com.lucidworks.hadoop.ingest.CSVIngestMapper -c solr_test -i /user/m041785/test/Solr/hdfs/* -of com.lucidworks.hadoop.io.LWMapRedOutputFormat -zk hdpr03mn01.mayo.edu,hdpr03mn02.mayo.edu,hdpr03dn01.mayo.edu/solr
			String loadDocFromCSVFileCmd = "/usr/bin/hadoop jar " + enServerSolrHadoopJobJarFileFullPathAndName					
					+ " com.lucidworks.hadoop.ingest.IngestJob "					
					+ "-DcsvFieldMapping=0=id,1=firstName,2=lastName,3=salary,4=gender,5=state "
					+ "-DcsvFirstLineComment=true -DidField=id -DcsvDelimiter=\",\" -Dlww.commit.on.close=true "
					+ "-cls com.lucidworks.hadoop.ingest.CSVIngestMapper "
					+ "-c " + solrTestCollectionName + " "
					+ "-i " + hdfsTestDataFolderPathAndName + "* "					
					+ "-of com.lucidworks.hadoop.io.LWMapRedOutputFormat "
					+ "-zk " + zookeeperQuorum + "/solr"
					;
			
			
			//String solrServer_TruststoreJks_FileName = "solr_" + bdClusterName.toLowerCase() + "_dn02.jks";			
			//String currSolrNode = tempSolrInstalledNodeName.toLowerCase();
			//solrServer_TruststoreJks_FileName = solrServer_TruststoreJks_FileName.replace("dn02.jks", currSolrNode + ".jks");	
			String currSolrNode = tempSolrInstalledNodeName.toLowerCase();
			String currSolrNodeTrustStoreAlias = "solr_" + bdClusterName.toLowerCase() + "_" + currSolrNode;
			String solrServer_TruststoreJks_FileName =  currSolrNodeTrustStoreAlias + ".jks";	//solrServer_TruststoreJks_FileName.replace("dn02.jks", currSolrNode + ".jks");
			
			
			String enServerSolrClientJaas_FileName = localSolrClientJaas_FileName; //"solr_client_jaas.conf";
			//String enServerSolrJaasConfFileFullPathAndName = enServerScriptFileDirectory_solr + localSolrClientJaas_FileName;
			
			
			// /usr/bin/hadoop jar /data/home/m041785/test/solr/solr-hadoop-job-2.2.1.jar com.lucidworks.hadoop.ingest.IngestJob 
			//  -files solr_bdsdbx_dn02.jks,solr_client_jaas.conf,solr_client_jaas.keytab 
			//  -Dlww.truststore=./solr_bdsdbx_dn02.jks -Dlww.truststore.password=solr123 -Dlww.jaas.file=./solr_client_jaas.conf 
			//  -DcsvFieldMapping=0=id,1=firstName,2=lastName,3=salary,4=gender,5=state -DcsvFirstLineComment=true -DidField=id -DcsvDelimiter="," -Dlww.commit.on.close=true -cls com.lucidworks.hadoop.ingest.CSVIngestMapper -c solr_test -i /user/m041785/test/Solr/hdfs/* -of com.lucidworks.hadoop.io.LWMapRedOutputFormat -zk hdpr04mn01.mayo.edu,hdpr04mn02.mayo.edu,hdpr04dn01.mayo.edu/solr;
			if (solrClusterSecurity.equalsIgnoreCase("secured")){
				loadDocFromCSVFileCmd = "/usr/bin/hadoop jar " + enServerSolrHadoopJobJarFileFullPathAndName					
						+ " com.lucidworks.hadoop.ingest.IngestJob "
						+ "-files " + solrServer_TruststoreJks_FileName + "," + enServerSolrClientJaas_FileName + "," + solrClient_Keytab_FileName + " "
						+ "-Dlww.truststore=./" + solrServer_TruststoreJks_FileName + " "
						+ "-Dlww.truststore.password=" + solrServer_TruststoreJks_Pw + " "
						+ "-Dlww.jaas.file=./" + enServerSolrClientJaas_FileName + " "						
						+ "-DcsvFieldMapping=0=id,1=firstName,2=lastName,3=salary,4=gender,5=state "
						+ "-DcsvFirstLineComment=true -DidField=id -DcsvDelimiter=\",\" -Dlww.commit.on.close=true "
						+ "-cls com.lucidworks.hadoop.ingest.CSVIngestMapper "
						+ "-c " + solrTestCollectionName + " "
						+ "-i " + hdfsTestDataFolderPathAndName + "* "					
						+ "-of com.lucidworks.hadoop.io.LWMapRedOutputFormat "
						+ "-zk " + zookeeperQuorum + "/solr"
						;
			}			
						
			//String reIndexingPostSchemaChangeCmd = loadDocFromCSVFileCmd;						
			
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=id:101&indent=true&omitHeader=true" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 > /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			String searchIdBySelectCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=id:101&indent=true&omitHeader=true\" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 > "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;

			//curl -k --negotiate -u : -g http://hdpr03dn02.mayo.edu:8983/solr/solr_test/query -d '{"query":"id:102"}'| grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			String searchIdByQueryCmd = "curl -k --negotiate -u : -g " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/query -d '{\"query\":\"id:102\"}'| grep salary | tr -d ',' | cut -d':' -f2 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;

			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?ids=103&csv.header=false&wt=csv" | cut -d',' -f6 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=104&omitHeader=true&wt=json" | grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=105&omitHeader=true&wt=python" | grep salary | tr -d ',' | cut -d':' -f2 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/get?id=106&omitHeader=true&wt=xml" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			
			String searchIdByGet_CsvCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=103&csv.header=false&wt=csv\" | cut -d',' -f6 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_JsonCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=104&csv.header=false&wt=json\" | grep salary | tr -d ',' | cut -d':' -f2 >>"
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_PythonCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=105&csv.header=false&wt=python\" | grep salary | tr -d ',' | cut -d':' -f2 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchIdByGet_XmlCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/get?ids=106&csv.header=false&wt=xml\" | grep salary | cut -d'>' -f2 | cut -d'<' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			
		
			//curl http://hdpr03dn02.mayo.edu:8983/solr/solr_test/update?commit=true -H "Content-Type: text/xml" --data-binary '<delete><id>108</id></delete>';
			String deleteDocCmd = "curl -k --negotiate -u : " + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/update?commit=true -H \"Content-Type: text/xml\" --data-binary '<delete>"
					+ "<id>101</id>"
					+ "</delete>'";
			
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=*:*&sort=salary%20asc&omitHeader=true&wt=json&start=100" | cut -d':' -f3 | cut -d',' -f1 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=salary:[*%20TO%20100000]&sort=salary%20asc&omitHeader=true&wt=json&start=100" | cut -d':' -f3 | cut -d',' -f1 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			//curl -k --negotiate -u : -g "http://hdpr03dn02.mayo.edu:8983/solr/solr_test/select?q=*:*&json.facet={avg_salary:'avg(salary)'}&indent=true&omitHeader=true&wt=xml" | grep avg_salary | cut -d'>' -f2 | cut -d'<' -f1 >> /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt;
			String searchAllDocCountCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=*:*&sort=salary%20asc&omitHeader=true&wt=json&start=100\" | cut -d':' -f3 | cut -d',' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			String searchRangeDocCountCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=salary:[*%20TO%20100000]&sort=salary%20asc&omitHeader=true&wt=json&start=100\" | cut -d':' -f3 | cut -d',' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
 
			String searchAverageSalaryCmd = "curl -k --negotiate -u : -g \"" + solrRestServiceIpAddressAndPort 
					+ "/" + solrTestCollectionName
					+ "/select?q=*:*&json.facet={avg_salary:'avg(salary)'}&indent=true&omitHeader=true&wt=xml\" | grep avg_salary | cut -d'>' -f2 | cut -d'<' -f1 >> "
					+ enSolrTestCollectionSearchingStatusFilePathAndName_temp;
			
			//{ cat /data/home/m041785/test/solr/solr_hdfsLoad_query_result1_temp.txt | tr '\n' ',' ; } > /data/home/m041785/test/solr/solr_hdfsLoad_query_result1.txt;			
			String transformLocalTempFileIntoRecordFileCmd = "{ cat " + enSolrTestCollectionSearchingStatusFilePathAndName_temp + " | tr '\\n' ',' ; } > " + enSolrTestCollectionSearchingStatusFilePathAndName;
			String removeTempLocalSolrTestResultsFileCmd = "rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName_temp;			
					
			//String removeLocalSolrTestResultsFileCmd = "rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName;		
			
			
			//sb.append("chown hdfs:hdfs " + enServerScriptFileDirectory + ";\n");
			//sb.append("sudo su - hdfs;\n");
			sb.append("chown -R " + loginUserName + ":users " + enServerScriptFileDirectory + ";\n");
			sb.append("chmod -R 777 " + enServerScriptFileDirectory + "; \n");	
			
			sb.append("cd " + enServerScriptFileDirectory_solr + ";\n");			
			//sb.append("ln -s /data/home/.solr .solr;\n");	
			//sb.append("scp /data/home/.solr/"+ enServerSolrClientJaas_FileName + " .;\n");	
			//sb.append("scp /data/home/.solr/"+ solrClient_Keytab_FileName + " .;\n");	
			//sb.append("scp /data/home/.solr/"+ solrClientJaas_FileName + " .;\n");	
			
			//sb.append("sudo su - " + loginUserName + ";\n");
			sb.append("kdestroy;\n");
			//sb.append("kinit  hdfs@MAYOHADOOPDEV1.COM -kt /etc/security/keytabs/hdfs.headless.keytab; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			//sb.append("kinit  " + hdfsInternalPrincipal + " -kt " + hdfsInternalKeyTabFilePathAndName +"; \n"); //Local Kerberos or Alternative Enterprise Kerberos
			sb.append(internalKinitCmdStr +"; \n"); //Enterprise Kerberos
						    
		    sb.append("hadoop fs -mkdir -p " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -mkdir -p " + hdfsTestDataFolderPathAndName + "; \n");		    
		    sb.append("hadoop fs -chown -R " + loginUserName + ":bdadmin " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -chmod -R 750 " + solrTestFolderName + "; \n");
		    sb.append("hadoop fs -put " + enServerSorlTestDataCsvFilePathAndName + " " + hdfsTestDataFolderPathAndName +  "; \n");
		    
			sb.append(deleteSolrTestCollectionCmd + ";\n");	
			sb.append(createSolrTestCollectionCmd + ";\n");	
			sb.append(alterSchemaCmd + ";\n");
			sb.append(loadDocFromCSVFileCmd + ";\n");
			
			
			sb.append(searchIdBySelectCmd + ";\n");	
			sb.append(searchIdByQueryCmd + ";\n");			
			sb.append(searchIdByGet_CsvCmd + ";\n");			
			sb.append(searchIdByGet_JsonCmd + ";\n");
			sb.append(searchIdByGet_PythonCmd + ";\n");	
			sb.append(searchIdByGet_XmlCmd + ";\n");			
			
			sb.append(deleteDocCmd + ";\n");
			
			sb.append(searchAllDocCountCmd + ";\n");	
			sb.append(searchRangeDocCountCmd + ";\n");	
			sb.append(searchAverageSalaryCmd + ";\n");
			sb.append(transformLocalTempFileIntoRecordFileCmd + ";\n");	
			sb.append(removeTempLocalSolrTestResultsFileCmd + ";\n");				
		    		    
		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "; \n");
		    sb.append("hadoop fs -copyFromLocal " + enSolrTestCollectionSearchingStatusFilePathAndName + " " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "; \n");
		   	sb.append("hadoop fs -chmod -R 550 " + solrTestFolderName + "; \n");		    
		    sb.append("rm -f " + enSolrTestCollectionSearchingStatusFilePathAndName + "; \n");
		    sb.append("kdestroy;\n");		    	
			
		    
		    
		    String solrCollectionCollectioningAndSearchingTestScriptFilePathAndName = solrScriptFilesFoder + "dcTestSolr_HdfsLoad_CollectioningAndSearchingScriptFile_No"+ (i+1) + ".sh";			
			prepareFile (solrCollectionCollectioningAndSearchingTestScriptFilePathAndName,  "Script File For Testing Solr Hdfs-Loading Collectioning and Searching on '" + bdClusterName + "' Cluster Solr Installed Node - " + tempSolrInstalledNodeName);
			
			String solrCollectionCollectioningAndSearchingCmds = sb.toString();
			writeDataToAFile(solrCollectionCollectioningAndSearchingTestScriptFilePathAndName, solrCollectionCollectioningAndSearchingCmds, false);		
			sb.setLength(0);
			
			//Desktop.getDesktop().open(new File(solrCollectionCollectioningAndSearchingTestScriptFullFilePathAndName));			
			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(solrCollectionCollectioningAndSearchingTestScriptFilePathAndName, 
					solrScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
			
			Path filePath = new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName);
			boolean solrStatusHdfsFileExistingStatus = false;
			if (currHadoopFS.exists(filePath)) {
				hdfsFilePathAndNameList.add(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName);	
				solrStatusHdfsFileExistingStatus = true;				
			}
			System.out.println("\n*** Exisiting status for " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + ": " + solrStatusHdfsFileExistingStatus);
			
			String targetFoundString = "90000.0,125000.0,Texas,120000.0,45500.0,250000.0,5,2,119100.0,";
			boolean solrSearchingSuccessStatus = false;			
			if (solrStatusHdfsFileExistingStatus){ 
				try {							
					FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName));				
					BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
					String line = "";				
					while ((line = br.readLine()) != null) {
						System.out.println("*** line: " + line );
						if (line.contains(targetFoundString)) {												
							solrSearchingSuccessStatus = true;					
						}						
					}//end while
					br.close();				
								
				} catch (IOException e) {				
					e.printStackTrace();				
				}//end try   
			}//end if			
			
			DayClock currClock = new DayClock();				
			String currTime = currClock.getCurrentDateTime();				
			String timeUsed = DayClock.calculateTimeUsed(prevTime, currTime);	
						
			String testRecordInfo = "";			
			if (solrSearchingSuccessStatus == true){
				successTestScenarioNum++;				
				
				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Solr Rest API Collection Deleting & Creating, Schema Altering, Document Operations (Indexing"
						+ "\n          - From Files {CSV...} in HDFS}, and Deleting), and Searching"
						+ "\n          (Get, Query, Select) (Id, Count, Range, Aggregate...) (Output: csv, json, xml, python)"
						+ "\n          via Solr Rest Service URL - " + solrRestServiceIpAddressAndPort
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + entryNodeName_4CurlRunning + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Solr Cloud Security - '" + solrClusterSecurity + "'; Active Service Number - '" + activeSolrServiceNumber + "'; Installed Nodes Number - '" + solrInstalledDNNumber+"'"
						+ "\n  -- (3) Generated Test Results File in HDFS System:  '" + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "' \n";
			} else {
				testRecordInfo = "-*-*- 'Failed'  -  # (" + totalTestScenarioNumber + ") Test Scenario:"
						+ "\n  -- (1) Solr Rest API Collection Deleting & Creating, Schema Altering, Document Operations (Indexing"
						+ "\n          - From Files {CSV...} in HDFS}, and Deleting), and Searching"
						+ "\n          (Get, Query, Select) (Id, Count, Range, Aggregate...) (Output: csv, json, xml, python)"
						+ "\n          via Solr Rest Service URL - " + solrRestServiceIpAddressAndPort
						+ "\n          on BigData '" + bdClusterName + "' Cluster From Entry Node - '" + entryNodeName_4CurlRunning + "'"
						+ "\n          at the time - " + currTime + " and Time Used: " + timeUsed
						+ "\n  -- (2) Solr Cloud Security - '" + solrClusterSecurity + "'; Active Service Number - '" + activeSolrServiceNumber + "'; Installed Nodes Number - '" + solrInstalledDNNumber+"'"
						+ "\n  -- (3) Generated Test Results File in HDFS System:  '" + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + "' \n";
			}
			writeDataToAFile(dcTestSolr_RecFilePathAndName, testRecordInfo, true);	
			prevTime = currTime;
		}//end for for 6.
								
				
		testSuccessRate = (successTestScenarioNum / totalTestScenarioNumber) * 100; 
		NumberFormat df = new DecimalFormat("#0.00"); 
		String currUATPassedRate = df.format(testSuccessRate);
		
	    //Notice message on the console
		DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Testing Solr Collection Deleting, Creating, Schema Altering, Documents Operations and Searching";
		currNotingMsg += "\n***** Present Solr Testing Generated Total " + hdfsFilePathAndNameList.size() + " HDFS File(s)!";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";
		currNotingMsg += "\n   Total Solr Test Scenario Number: " + totalTestScenarioNumber;
		currNotingMsg += "\n   Solr Test Succeeded Scenario Number: " + successTestScenarioNum;
		currNotingMsg += "\n   Solr Test Scenario Success Rate (%): " + currUATPassedRate;
		currNotingMsg += "\n===========================================================";	    
		
		//System.out.println(currNotingMsg);
		writeDataToAFile(dcTestSolr_RecFilePathAndName, currNotingMsg, true);		
		Desktop.getDesktop().open(new File(dcTestSolr_RecFilePathAndName));
	
	}//end run()
	

	private static void updateLocalSolrJaasConfFile (String localSolrJaasConfFileFullPathAndName, String bdClusterKnoxIdName){
		//destFileFullPathAndName, bdClusterKnoxIdName
		ArrayList<String> oldSolrJaasConfLineList = new ArrayList<String>();
		
		try {
			FileReader aFileReader = new FileReader(localSolrJaasConfFileFullPathAndName);
			BufferedReader br = new BufferedReader(aFileReader);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("principal=\"hdfs-")
					 && line.contains("@MFAD.MFROOT.ORG\"")	
						){
					line = "\tprincipal=\"hdfs-" + bdClusterKnoxIdName + "@MFAD.MFROOT.ORG\"";
				}
							
				oldSolrJaasConfLineList.add(line);				
			}
			br.close();
			
			File tempFile = new File(localSolrJaasConfFileFullPathAndName);				
			FileWriter outStream = new FileWriter(tempFile, false);			
			PrintWriter output = new PrintWriter (outStream);	
			for (int j = 0; j < oldSolrJaasConfLineList.size(); j++){
				String tempLine = oldSolrJaasConfLineList.get(j);
				output.println(tempLine);
				System.out.println(tempLine);;
			}//end for
			output.close();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
		System.out.println("\n*** Done - Updating Local Solr Jaas Configuration File for Kerberose Authentication!!!\n");
		
	}//updateLocalSolrJaasConfFile
	
	
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
