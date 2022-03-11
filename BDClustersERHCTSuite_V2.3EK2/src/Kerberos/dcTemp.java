package Kerberos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

public class dcTemp {

	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, IOException {
  	    String bdClusterName = "BDDev3"; //BDProd2...BDProd..BDInt..BDSbx..BDDev3...BDDev1
        BdCluster currBdCluster = new BdCluster(bdClusterName);
        FileSystem currHadoopFS  = currBdCluster.getHadoopFS(); 
        
        String loginUser4AllNodesName = "wa00336";
        String hbaseTestFolderName = "/user/" + loginUser4AllNodesName + "/test/HBase/";
        int i = 0;
        String hbaseTableName = "employee" + (i+1);
        String hbaseTableName_bdts = "bdts:" + hbaseTableName;
        String hdfsTableRowCountFilePathAndName = hbaseTestFolderName + "hbaseTable_" + hbaseTableName + "_RowCount.txt";
        
        Path filePath = new Path(hdfsTableRowCountFilePathAndName);
        int hbaseTableRowCount = 0;
        if (currHadoopFS.exists(filePath)) {
            FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsTableRowCountFilePathAndName));              
            BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
            String line = "";           
            while ((line = br.readLine()) != null) {                
                if (line.contains("6 row(s)")) {
                    System.out.println("*** Row Counts # in HBase Table - " + hbaseTableName_bdts + " Is: " + 6 );
                    hbaseTableRowCount = 6;
                    break;
                }                               
            }//end while
            br.close();
        } else {
            hbaseTableRowCount = 0;
        }           
        
        System.out.println("*** Actual Row Counts # for HBase Table - " + hbaseTableName_bdts + " Is: " + hbaseTableRowCount );
      
//		String bdClusterName = "BDProd2"; //BDProd2...BDProd..BDInt..BDSbx..BDDev3...BDDev1
//		BdCluster currBdCluster = new BdCluster(bdClusterName);
//		
//		System.out.println("*-* currBdCluster.getCurrentClusterKnoxNodeName(): " + currBdCluster.getCurrentClusterKnoxNodeName());
//		System.out.println("*-* currBdCluster.getCurrentClusterKnoxNode2Name(): " + currBdCluster.getCurrentClusterKnoxNode2Name());
//		
//		ArrayList<String> currentClusterKnoxNodeList = new  ArrayList<String>();
//		currentClusterKnoxNodeList = currBdCluster.getCurrentClusterKnoxNodeList();
//		int count = 0;
//		for (String tempKnoxNodeName: currentClusterKnoxNodeList){
//			count++;
//			System.out.println("(" + count + ") " + tempKnoxNodeName);
//		}
//		
//		System.out.println("");
//		ArrayList<String> currentClusterEntryNodeList = new  ArrayList<String>();
//		currentClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
//		int count2 = 0;
//		for (String tempEntryNodeName: currentClusterEntryNodeList){
//			count2++;
//			System.out.println("(" + count2 + ") " + tempEntryNodeName);
//		}
		
//		//5. Loop through bdClusterEntryNodeList to delete and create an ElasticSearch index, create
//		//     a ElasticSearch document(s) and refresh the ES Index, followed by querying the index		
//		//ArrayList<String> hdfsFilePathAndNameList = new ArrayList<String> ();
//		//double successTestScenarioNum = 0L;
//		//clusterENNumber_Start = 0; //0..1..2..3..4..5
//	    //clusterENNumber = 1; //1..2..3..4..5..6
//		for (int i = clusterENNumber_Start; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1..clusterENNumber	
//			totalTestScenarioNumber++;
//			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();			
//			System.out.println("\n--- (" + (i+1) + ") Testing ElasticSearch-Storing Data Into HDFS File on Entry Node: " + tempENName);
//			
//			//(1) Get Elastic Search Host Web IP Address and Port String
//			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
//			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
//			String enIpAddressStr = bdENCmdFactory.getServerURI();
//			System.out.println(" *** enIpAddressStr: " + enIpAddressStr);
//			
//			String esHostName = enIpAddressStr;
//			System.out.println(" *** esHostName: " + esHostName);
//			
//			String esHostName1 = "http://" + esHostName.replaceAll("[e,m]n0[2-9]", "en01");
//			if (bdClusterName.equalsIgnoreCase("BDProd") || bdClusterName.equalsIgnoreCase("BDPrd")
//					|| bdClusterName.equalsIgnoreCase("Prd") || bdClusterName.equalsIgnoreCase("Prod")
//					|| bdClusterName.equalsIgnoreCase("MC_BDPrd") || bdClusterName.equalsIgnoreCase("MC_BDProd")){
//				esHostName1 = esHostName1.replace("en01", "en02");
//			}
//			System.out.println(" *** esHostName1: " + esHostName1);			
//			String esHostWebIpAddressAndPort = esHostName1 + ":9200";
//			System.out.println(" *** esHostWebIpAddressAndPort: " + esHostWebIpAddressAndPort);
//			
//			
//			
//			
//			//(2) Move test data file, and Storm jar  file into Entry node /home/hdfs/ folder
//			String enServerFileDirectory = enServerScriptFileDirectory;
//			int exitVal1 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localStormTestDataFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCmdFactory);
//			
//			int exitVal2 = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster(localStormJarFileName, bdClusterUATestResultsParentFolder, enServerFileDirectory, bdENCmdFactory);
//			
//			tempClock = new DayClock();				
//			tempTime = tempClock.getCurrentDateTime();	
//			
//			if (exitVal1 == 0 ){
//				System.out.println("\n*** Done - Moving Storm Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving Storm Test Data File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//								
//			if (exitVal2 == 0 ){
//				System.out.println("\n*** Done - Moving StormTest Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			} else {
//				System.out.println("\n*** Failed - Moving StormTest Jar File into /home/hdfs/ folder on Entry Node of '" + bdClusterName + "' Cluster at the time - " + tempTime);				
//			}
//			
//						
//			//(3) Testing ElasticSearch Index Deleting, Creating, Loading and Searching				
//			String enServerStormTestDataFilePathAndName = enServerFileDirectory + localStormTestDataFileName;			
//			String enServerStormJarFilePathAndName = enServerFileDirectory + localStormJarFileName;
//			
//			String userAuthenStr = " -v --user " + esClusterUserName + ":" + esClusterPassWord + " "; //BDDev: es_admin:admin4dev
//			
//			String esTestIndexSearchingStatusFileName =  "esTestIndexSearchStatus_" + (i+1) + ".txt";			
//			String enEsTestIndexSearchingStatusFilePathAndName = enServerScriptFileDirectory + esTestIndexSearchingStatusFileName;
//			String hdfsESTestIndexSearchingStatusFileNameFilePathAndName = stormTestFolderName + esTestIndexSearchingStatusFileName;
//			hdfsFilePathAndNameList.add(hdfsESTestIndexSearchingStatusFileNameFilePathAndName);
//			
//			String deleteEsTestIndexCmd = "curl" + userAuthenStr + "-XDELETE '" + esHostWebIpAddressAndPort + "/estest/'";
//			String createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
//					+ "    \"settings\" : {\n"
//					+ "        \"index\" : {\n"
//					+ "            \"number_of_shards\" : 5,\n"
//					+ "            \"number_of_replicas\" : 1\n"
//					+ "        }\n"
//					+ "    }\n"
//					+ "}'";
//			if (clusterENNumber >= 2) {
//				createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '{\n"
//						+ "    \"settings\" : {\n"
//						+ "        \"index\" : {\n"
//						+ "            \"number_of_shards\" : 5,\n"
//						+ "            \"number_of_replicas\" : 2\n"
//						+ "        }\n"
//						+ "    }\n"
//						+ "}'";
//				
//			}
//			//String createEsTestIndexCmd = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/' -d '\n"
//					//+ "index :\n"
//					//+ "    number_of_shards : 3\n"
//					//+ "    number_of_replicas : 2 \n"
//					//+ "'";
//			
//						
//			//String loadGreetingDocCmd1 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/1' -d '{ \"title\":\"Hello Sam\" }'";
//			//String loadGreetingDocCmd2 = "curl" + userAuthenStr + "-XPUT '" + esHostWebIpAddressAndPort + "/estest/greeting/2' -d '{ \"title\":\"Hello Tom\" }'";
//			String stormInitiateStr = "/usr/bin/storm ";	
//			String stormTopologyStartCmd =  stormInitiateStr + " jar "
//							+  enServerStormJarFilePathAndName + " topology.TestESTopology "
//							+ esHostName + " " + esClusterName + " " + enServerStormTestDataFilePathAndName + " & sleep 35";
//			String stormTopologyStopCmd =  stormInitiateStr + "kill testESTopology"; 
//			
//			
//			String refreshEsTestIndexCmd = "curl" + userAuthenStr + "-XPOST '" + esHostWebIpAddressAndPort + "/estest/_refresh'";
//			
//			String searchEsTestIndexCmd = "curl" + userAuthenStr + "-XGET '" + esHostWebIpAddressAndPort + "/estest/employee/_search?pretty=true,q=employeeId:101' > " + enEsTestIndexSearchingStatusFilePathAndName+ " 2>&1";
//			
//			sb.append("sudo su - hdfs;\n");
//		    sb.append("hadoop fs -mkdir -p " + stormTestFolderName + "; \n");		   
//		    sb.append("hadoop fs -chmod -R 750 " + stormTestFolderName + "; \n");		   
//		    sb.append(deleteEsTestIndexCmd + ";\n");	
//		    sb.append(createEsTestIndexCmd + ";\n");		    
//		    
//		    sb.append(stormTopologyStartCmd + ";\n");
//			sb.append(stormTopologyStopCmd + ";\n");
//		    sb.append(refreshEsTestIndexCmd + ";\n");	
//		    sb.append(searchEsTestIndexCmd + ";\n");	
//		    		    
//		    sb.append("hadoop fs -rm -r -skipTrash " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
//		    sb.append("hadoop fs -copyFromLocal " + enEsTestIndexSearchingStatusFilePathAndName + " " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "; \n");
//		   	sb.append("hadoop fs -chmod -R 550 " + stormTestFolderName + "; \n");    		    
//		   
//		    sb.append(deleteEsTestIndexCmd + ";\n");	    
//		    //sb.append("rm -f " + enServerStormTestDataFilePathAndName + "; \n");		    
//		    //sb.append("rm -f " + enServerStormJarFilePathAndName + "; \n");
//		    //sb.append("rm -f " + enEsTestIndexSearchingStatusFilePathAndName + "; \n");
//		    sb.append(stormTopologyStopCmd + ";\n");
//		    
//			
//		    String esScriptFilesFoder = bdClusterUATestResultsParentFolder + "ScriptFiles_" + bdClusterName + "\\" + "Storm\\";
//		    prepareFolder(esScriptFilesFoder, "Local ElasticSearch Testing Script Files");
//		    
//		    String esIndexIndexingAndSearchingTestScriptFullFilePathAndName = esScriptFilesFoder + "dcTestStorm_ESIndexingAndSearchingScriptFile_No"+ (i+1) + ".sh";			
//			prepareFile (esIndexIndexingAndSearchingTestScriptFullFilePathAndName,  "Script File For Testing ElasticSearch Indexing and Searching on '" + bdClusterName + "' Cluster Entry Node - " + tempENName);
//			
//			String esIndexIndexingAndSearchingCmds = sb.toString();
//			writeDataToAFile(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, esIndexIndexingAndSearchingCmds, false);		
//			sb.setLength(0);
//			
//			//Desktop.getDesktop().open(new File(esIndexIndexingAndSearchingTestScriptFullFilePathAndName));			
//			LoginUserUtil.runScriptFileWithinLoginUserHomeFolder_ByLoginUser_OnBDCluster(esIndexIndexingAndSearchingTestScriptFullFilePathAndName, 
//					esScriptFilesFoder, enServerScriptFileDirectory, bdENCmdFactory);
//					
//				
//			System.out.println("\n*** hdfsESTestIndexSearchingStatusFileNameFilePathAndName: " + hdfsESTestIndexSearchingStatusFileNameFilePathAndName );
//			
//			String targetFoundString = "\"_source\":{\"employeeId\":\"10";			
//			boolean esSearchingSuccessStatus = false;			
//			try {							
//				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsESTestIndexSearchingStatusFileNameFilePathAndName));				
//				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//				String line = "";				
//				while ((line = br.readLine()) != null) {
//					System.out.println("*** line: " + line );
//					if (line.contains(targetFoundString)) {												
//						esSearchingSuccessStatus = true;
//						System.out.println("*** found: " + targetFoundString );
//						break;
//					}					
//				}//end while
//				br.close();				
//							
//			} catch (IOException e) {				
//				e.printStackTrace();				
//			}//end try   
//			
//			tempClock = new DayClock();				
//			tempTime = tempClock.getCurrentDateTime();		
//			
//			String testRecordInfo = "";			
//			if (esSearchingSuccessStatus == true){
//				successTestScenarioNum++;
//				testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Internally Storm-Using-ElasticSearch for Index Deleting, Creating, Loading, Refreshing and\n         Searching on '" + bdClusterName + "' Cluster From Entry Node - '" 
//						+ tempENName + "' at the time - " + tempTime
//				        + "\n  --(2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'\n";	 
//			} else {
//				testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//						+ "\n  --(1) Internally Storm-Using-ElasticSearch for Index Deleting, Creating, Loading, Refreshing and\n         Searching on '" + bdClusterName + "' Cluster From Entry Node - '" 
//						+ tempENName + "' at the time - " + tempTime
//				        + "\n  --(2) Present ElasticSearch Testing-Generated HDFS File for ES Search Status:  '" + hdfsESTestIndexSearchingStatusFileNameFilePathAndName + "'\n";   
//			}
//			writeDataToAFile(dcTestStorm_RecFilePathAndName, testRecordInfo, true);
//			if (i < clusterENNumber-1){
//				Thread.sleep(35000);
//			}
//			
//		}//end for
		
		
//		String localFlumeConfigFileName = "C:\\BD\\BD_UAT\\dcFlumeConf_krb.txt";
//		String ambariQaInternalPrincipal = "ambari-qa-MAYOHADOOPDEV1@MFAD.MFROOT.ORG";
//		String ambariInternalKeyTabFilePathAndName = "/etc/security/keytabs/smokeuser.headless.keytab";
//		ArrayList<String> oldFlumeConfLineList = new ArrayList<String>();
//		
//		try {
//			FileReader aFileReader = new FileReader(localFlumeConfigFileName);
//			BufferedReader br = new BufferedReader(aFileReader);
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				if (line.contains("fileToHdfs.sinks.k1.hdfs.kerberosPrincipal = ")){
//					line = "fileToHdfs.sinks.k1.hdfs.kerberosPrincipal = " + ambariQaInternalPrincipal;
//				}
//				if (line.contains("fileToHdfs.sinks.k1.hdfs.kerberosKeytab = ")){
//					line = "fileToHdfs.sinks.k1.hdfs.kerberosKeytab = " + ambariInternalKeyTabFilePathAndName;
//				}				
//				oldFlumeConfLineList.add(line);				
//			}
//			br.close();
//			
//			File tempFile = new File(localFlumeConfigFileName);				
//			FileWriter outStream = new FileWriter(tempFile, false);			
//			PrintWriter output = new PrintWriter (outStream);	
//			for (int j = 0; j < oldFlumeConfLineList.size(); j++){
//				String tempLine = oldFlumeConfLineList.get(j);
//				output.println(tempLine);
//				System.out.println(tempLine);;
//			}//end for
//			output.close();
//		} catch (FileNotFoundException e) {			
//			e.printStackTrace();
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}		
//		
//		  
//		
//		System.out.print("\n*** Done - Updating");

	}

}
