package dcModelClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 9/22/2014 - 11/7/2014; 11/30/2014; 12/31/2014
 */

public class HdfsUtil {	

	public HdfsUtil(){		
	}//end default constructor	
	

////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	public static String getSolrInstalledNodeServiceStateByWeb (String bdClusterName, String solrInstalledNodeName, String httpProtocol){
		String querySolrNodeServiceState = "down";
		BdNode querySolrBDNode = new BdNode(solrInstalledNodeName, bdClusterName);
		ULServerCommandFactory bdSolrNodeCmdFactory = querySolrBDNode.getBdENCmdFactory();	
		
		String solrRestServiceIpAddressAndPort = "";
		if (httpProtocol.equalsIgnoreCase("secured")){
			solrRestServiceIpAddressAndPort = "https://" + bdSolrNodeCmdFactory.getServerURI()+ ":8983/solr";
		} else {
			solrRestServiceIpAddressAndPort = "http://" + bdSolrNodeCmdFactory.getServerURI()+ ":8983/solr";
		}
				
		String solrInstalledNodeStatusQueryURI = solrRestServiceIpAddressAndPort + "/admin/collections?action=LIST&indent=true";
		System.out.println("\n *** solrInstalledNodeStatusQueryURI: " + solrInstalledNodeStatusQueryURI);		
	
		try {
			ConfigureTestUserADAuthenticator krbTestUserAuthenticator = new ConfigureTestUserADAuthenticator();
			Authenticator.setDefault(krbTestUserAuthenticator);
			
			URL aWebSiteURL = new URL(solrInstalledNodeStatusQueryURI);	     
	        URLConnection myConn = aWebSiteURL.openConnection();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
	        String inputLine;	        
	        while ((inputLine = bReader.readLine()) != null){	        	
	            if (inputLine.contains("<int name=\"status\">") ){
	            	//System.out.println(inputLine);
	            	//inputLine = inputLine.replace("<int name=\"status\">", "").replace("</int>", "");
	            	if (inputLine.contains("<int name=\"status\">0</int>")){
	            		//System.out.println(inputLine.trim());
	            		querySolrNodeServiceState = "up";
	            	};
	            }
	        }
	        bReader.close();
	       
		} catch (Exception e) {				
			//e.printStackTrace();
		}//end try
		
		System.out.println("\n *** querySolrNodeServiceState: " + querySolrNodeServiceState);
		return querySolrNodeServiceState;	
	}//end getNameNode1ServiceState
	
	public static String getResourceManager1ServiceStateByWeb (String bdClusterName){
		//String tempLocalWorkignFileAndPathName = "C:\\BD\\BDTest2\\HDFS\\tempResourceManager1State.txt";
		//prepareFile (tempLocalWorkignFileAndPathName,  "Local File For Resource Manager 1 Service Status of BD Cluster - " + bdClusterName);
		/*
		 * BD - Dev1, Prod2, Test2 and Sdbx Yarn Resource Managers are installed on MN01 and MN02 So MN01 used ad rm1
		 * BD - Dev3, Prod3 and test3 Yarn Resource Managers are installed on MN03 and MN02 So MN03 used ad rm1
		 */
		
		String rm1ServiceState = "standby";
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN03ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN03ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN03ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN03ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							//|| bdClusterName.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN03ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			ConfigureTestUserADAuthenticator krbTestUserAuthenticator = new ConfigureTestUserADAuthenticator();
			Authenticator.setDefault(krbTestUserAuthenticator);
			
			//System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			//"http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus"
			String rm1StatusQueryURI = "http://" + bdENCommFactory.getServerURI() + ":8088/ws/v1/cluster/info";
			System.out.println("\n *** rm1StatusQueryURI: " + rm1StatusQueryURI);
			
			URL aWebSiteURL = new URL(rm1StatusQueryURI);	     
	        URLConnection myConn = aWebSiteURL.openConnection();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
	        String inputLine;	        
	        while ((inputLine = bReader.readLine()) != null){	        	
	        	if (inputLine.contains("\"haState\":\"") ){
	            	//System.out.println(inputLine.trim());
	            	String[] lineSplit = inputLine.trim().split("\"haState\":\"");
	            	String tgtStr = lineSplit[1].trim();
	            	if (tgtStr.contains("\",\"")){
	            		String[] tgtStrSplit = tgtStr.split("\",\"");
	            		rm1ServiceState = tgtStrSplit[0].toLowerCase();
	            	}             	
	            }
	        }
	        bReader.close();	        
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
		
		System.out.println("\n*** rm1ServiceState: " + rm1ServiceState );
		return rm1ServiceState;	
	}//end getNameNode1ServiceState
	
	public static String getNameNode1ServiceStateByWeb (String bdClusterName){
		//String tempLocalWorkignFileAndPathName = "C:\\BD\\BDTest2\\HDFS\\tempNameNode1State.txt";
		//prepareFile (tempLocalWorkignFileAndPathName,  "Local File For Name Node 1 Service Status of BD Cluster - " + bdClusterName);
			
		String nn1ServiceState = "standby";
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							//|| bdClusterName.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN01ConParameters()); 
			}
			
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			ConfigureTestUserADAuthenticator krbTestUserAuthenticator = new ConfigureTestUserADAuthenticator();
			Authenticator.setDefault(krbTestUserAuthenticator);
			
			//System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
			//"http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus"
			String nn1StatusQueryURI = "http://" + bdENCommFactory.getServerURI() + ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
			System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
			
			URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
	        URLConnection myConn = aWebSiteURL.openConnection();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
	        String inputLine;	        
	        while ((inputLine = bReader.readLine()) != null){	        	
	            if (inputLine.contains("\"State\" :") ){
	            	//System.out.println(inputLine.trim());
	            	inputLine = inputLine.replace("\"State\" :", "");
	            	nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
	            }
	        }
	        bReader.close();	        
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
		
		System.out.println("\n*** nn1ServiceState: " + nn1ServiceState );
		return nn1ServiceState;	
	}//end getNameNode1ServiceState
	
	public static String getNameNode1ServiceState (String bdClusterName, String rootPw){
		String tempLocalWorkignFileAndPathName = "C:\\BD\\BDTest2\\HDFS\\tempNameNode1State.txt";
		prepareFile (tempLocalWorkignFileAndPathName,  "Local File For Name Node 1 Service Status of BD Cluster - " + bdClusterName);
			
		String nn1ServiceState = "";
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							//|| bdClusterName.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN01ConParameters()); 
			}			
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			String hdfsNN1StateFindingFullCmdStr = "echo '" + rootPw + "' |  su - root -c 'su hdfs -c \"hdfs haadmin -getServiceState nn1\"'";
			//For active ResourceManger:
			//String yarnRM1StateFindingFullCmdStr = "echo '" + rootPw + "' |  su - root -c 'su yarn -c \"yarn rmadmin -getServiceState rm1\"'";
			String currNameNodeStateFinding_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsNN1StateFindingFullCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			System.out.println("\n -*- currNameNodeStateFinding_PlinkSingleFullCmd: \n" + currNameNodeStateFinding_PlinkSingleFullCmd);
			
			
			int exitVal = executePuttyCommandOnLocalMachine(currNameNodeStateFinding_PlinkSingleFullCmd);				
			
			if (exitVal == 0){	
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";						
				while ( (line = br.readLine()) != null) {
					if (line.contains("active")){
						nn1ServiceState = "active";
					}
					if (line.contains("standby")){
						nn1ServiceState = "standby";
					}
					System.out.println("\n*** Found Name Node 1 State - \'" + nn1ServiceState + "\'" );
		        }//end while
				br.close();
				aFileReader.close();
				
			} else {
				System.out.println("\n---Failed - Found Name Node 1 State from MN01... Try to find it from EN 01" );
				if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); 
				}
				if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN01ConParameters()); 
				}
				
				if (bdClusterName.equalsIgnoreCase("BDProd2")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN01ConParameters()); 
				}
				if (bdClusterName.equalsIgnoreCase("BDProd3")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN01ConParameters()); 
				}
				
				if (bdClusterName.equalsIgnoreCase("BDTest2")
								//|| bdClusterName.equalsIgnoreCase("BDPrd")
								){						
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN01ConParameters()); 
				}
				if (bdClusterName.equalsIgnoreCase("BDTest3")
								){						
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN01ConParameters()); 
				}
				
				
				if (bdClusterName.equalsIgnoreCase("BDSbx")
						|| bdClusterName.equalsIgnoreCase("BDSdbx")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN01ConParameters()); 
				}
				
				String hdfsNN1StateFindingFullCmdStr2 =  "echo '" + rootPw + "' |  su - root -c 'su hdfs -c \"hdfs haadmin -getServiceState nn1\"'";
				//For active ResourceManger:
				//String yarnRM1StateFindingFullCmdStr2 = "echo '" + rootPw + "' |  su - root -c 'su yarn -c \"yarn rmadmin -getServiceState rm1\"'";
				String currNameNodeStateFinding_PlinkSingleFullCmd2 = bdENCommFactory.getPlinkSingleCommandString(hdfsNN1StateFindingFullCmdStr2, " 1>" + tempLocalWorkignFileAndPathName);
				System.out.println("\n -*- currNameNodeStateFinding_PlinkSingleFullCmd2: \n" + currNameNodeStateFinding_PlinkSingleFullCmd2);
				
				int exitVal2 = executePuttyCommandOnLocalMachine(currNameNodeStateFinding_PlinkSingleFullCmd2);				
				
				if (exitVal2 == 0){	
					FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
					BufferedReader br = new BufferedReader(aFileReader);
					
					String line = "";						
					while ( (line = br.readLine()) != null) {
						if (line.contains("active")){
							nn1ServiceState = "active";
						}
						if (line.contains("standby")){
							nn1ServiceState = "standby";
						}
						System.out.println("\n*** Found Name Node 1 State - \'" + nn1ServiceState + "\'" );
			        }//end while
					br.close();
					aFileReader.close();					
				} else {
					System.out.println("\n---Failed - Found Name Node 1 State" );
				}//end if
			}//end if		
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
		
		return nn1ServiceState;	
	}//end getNameNode1ServiceState
///////////////////////////////////////////////////////////////////////////////////////////////////////	
public static int runSingleCommand_OnBDCluster (String bdClusterName, String bdNodeName, String singleFullCmdStr){
		int exitVal = -1000;
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN02ConParameters()); 
				}
				
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN02ConParameters()); 
				}
			}
			//
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN03ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN04")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN04ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN05")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN05ConParameters()); 
				}
				
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN03ConParameters()); 
				}
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							|| bdClusterName.equalsIgnoreCase("BDPrd")){
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN03ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN04")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN04ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN05")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN05ConParameters()); 
				}
				//if (bdNodeName.equalsIgnoreCase("EN06")){
				//	bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN06ConParameters()); 
				//}
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN02ConParameters()); 
				}
			}
			//
			if (bdClusterName.equalsIgnoreCase("BDTest3")) {					
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN03ConParameters()); 
				}
							
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN03ConParameters()); 
				}
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN03ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN04")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN04ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN05")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN05ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN06")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN06ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN07")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN07ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN08")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN08ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN09")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN09ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN10")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN10ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN11")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN11ConParameters()); 
				}
				
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN02ConParameters()); 
				}
			}
			//
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN03ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN04")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN04ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN05")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN05ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN06")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN06ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN07")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN07ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN08")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN08ConParameters()); 
				}
				
				
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN03ConParameters()); 
				}
			}
			
			
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				if (bdNodeName.equalsIgnoreCase("EN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN02ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN03")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN03ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("EN04")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN04ConParameters()); 
				}
				//if (bdNodeName.equalsIgnoreCase("EN05")){
				//	bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN05ConParameters()); 
				//}
				//if (bdNodeName.equalsIgnoreCase("EN06")){
				//	bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN06ConParameters()); 
				//}
				if (bdNodeName.equalsIgnoreCase("MN01")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
				}
				if (bdNodeName.equalsIgnoreCase("MN02")){
					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN02ConParameters()); 
				}
			}
			
			String currPlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(singleFullCmdStr, "");
			System.out.println("\n -*- currPlinkSingleFullCmd: \n" + currPlinkSingleFullCmd);
			
			exitVal = executePuttyCommandOnLocalMachine(currPlinkSingleFullCmd);
						
			if (exitVal == 0){			
				System.out.println("\n***Succeeded - Executing the command (using plink) - " + singleFullCmdStr);	
			} else {
				System.out.println("\n---Failed - Executing the command (using plink) - " + singleFullCmdStr);	
			}//end if
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try

	   return exitVal;
   }//end runSingleCommand_OnBDCluster

	public static void intraTDHVersion2CopyHdfsFolderOrFile (String srcClusterName, String srcFolderOrFileFullPathAndName, 
			String destClusterName, String destFolderOrFileFullPathAndName, String tdhVersionClusterName_4Copying){
		//working hadoop cmd for hdfs: 
		//   hadoop distcp -p hftp://hdpr02mn01.mayo.edu:50070/data/amalgahistory/CurrentTable_Dump/part-m-00000a hdfs://hdpr04mn02.mayo.edu/data/amalgahistory/CurrentTable_Dump/

		String srcNnURLAddressPort = "";
		if (srcClusterName.equalsIgnoreCase("BDTest2")
				|| srcClusterName.equalsIgnoreCase("BDPrd")){
			//srcNnURLAddressPort = "hdfs://hdpr01mn01.mayo.edu:8020";
			srcNnURLAddressPort = "hdfs://hdp001-nn:8020"; //hdfs://hdp001-nn:8020...hdfs://hdpr01mn01.mayo.edu
		}
		if (srcClusterName.equalsIgnoreCase("BDProd2")){
			//srcNnURLAddressPort = "hdfs://hdpr02mn01.mayo.edu:8020";
			srcNnURLAddressPort = "hdfs://hdp002-nn:8020"; //hdfs://hdp002-nn:8020...hdfs://hdpr02mn01.mayo.edu
		}
		if (srcClusterName.equalsIgnoreCase("BDDev1")){
			//srcNnURLAddressPort = "hdfs://hdpr03mn01.mayo.edu:8020";
			srcNnURLAddressPort = "hdfs://hdp003-nn:8020"; //hdfs://hdp003-nn:8020... hdfs://hdpr03mn01.mayo.edu
		}
		if (srcClusterName.equalsIgnoreCase("BDSbx")
				|| srcClusterName.equalsIgnoreCase("BDSdbx")){
			//srcNnURLAddressPort = "hdfs://hdpr04mn02.mayo.edu:8020";
			srcNnURLAddressPort = "hdfs://hdp004-nn:8020"; //hdfs://hdp004-nn:8020...hdfs://hdpr04mn02.mayo.edu
		}
		
		if (!srcFolderOrFileFullPathAndName.startsWith("/")){
			srcFolderOrFileFullPathAndName = "/" + srcFolderOrFileFullPathAndName; 
		}
		String hdfsCopySrcPart = srcNnURLAddressPort + srcFolderOrFileFullPathAndName;
			
		
		String destNnIpAddress = "";
		if (destClusterName.equalsIgnoreCase("BDTest2")
				|| destClusterName.equalsIgnoreCase("BDPrd")){
			//destNnIpAddress = "hdfs://hdpr01mn01.mayo.edu:8020";
			destNnIpAddress = "hdfs://hdp001-nn:8020";//hdfs://hdp001-nn:8020...hdfs://hdpr01mn01.mayo.edu
		}
		if (destClusterName.equalsIgnoreCase("BDProd2")){
			//destNnIpAddress = "hdfs://hdpr02mn01.mayo.edu:8020";
			destNnIpAddress = "hdfs://hdp002-nn:8020"; //hdfs://hdp002-nn:8020...hdfs://hdpr02mn01.mayo.edu
		}
		if (destClusterName.equalsIgnoreCase("BDDev1")){
			//destNnIpAddress = "hdfs://hdpr03mn01.mayo.edu:8020";
			destNnIpAddress = "hdfs://hdp003-nn:8020"; //hdfs://hdp003-nn:8020...hdfs://hdpr03mn01.mayo.edu
		}
		if (destClusterName.equalsIgnoreCase("BDSbx")
				|| destClusterName.equalsIgnoreCase("BDSdbx")){
			//destNnIpAddress = "hdfs://hdpr04mn02.mayo.edu:8020";
			destNnIpAddress = "hdfs://hdp004-nn:8020";  //hdfs://hdp004-nn:8020...hdfs://hdpr04mn02.mayo.edu
		}
		
		if (!destFolderOrFileFullPathAndName.startsWith("/")){
			destFolderOrFileFullPathAndName = "/" + destFolderOrFileFullPathAndName; 
		}
		String hdfsCopyDestPart = destNnIpAddress + destFolderOrFileFullPathAndName;
		
		String hdfsCopyFullCmdStr = "su hdfs -c 'hadoop distcp -p " + hdfsCopySrcPart + " " + hdfsCopyDestPart + "'"; 
				
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1MN01ConParameters()); 
			}
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3MN03ConParameters()); 
			}
			
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN01ConParameters()); 
			}
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3MN03ConParameters()); 
			}
			
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDTest2")
							//|| tdhVersionClusterName_4Copying.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN01ConParameters()); 
			}
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3MN03ConParameters()); 
			}
			
			
			if (tdhVersionClusterName_4Copying.equalsIgnoreCase("BDSbx")
					|| tdhVersionClusterName_4Copying.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			String currHdfsFolderOrFileCopy_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsCopyFullCmdStr, "");
			System.out.println("\n -*- currHdfsFolderOrFileCopy_PlinkSingleFullCmd: \n" + currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
						
			if (exitVal == 0){			
				System.out.println("\n***Succeeded - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			} else {
				System.out.println("\n---Failed - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			}//end if
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
	
	}//end interVersionCopyHdfsFolderOrFile
	
	public static void interTDHVersionCopyHdfsFolderOrFile (String srcClusterName, String srcFolderOrFileFullPathAndName, 
			String destClusterName, String destFolderOrFileFullPathAndName, String tdhVersionHighClusterName){
		//working hadoop cmd for hdfs: 
		//   hadoop distcp -p hftp://hdpr02mn01.mayo.edu:50070/data/amalgahistory/CurrentTable_Dump/part-m-00000a hdfs://hdpr04mn02.mayo.edu/data/amalgahistory/CurrentTable_Dump/

		String srcNnURLAddressPort = "";
		if (srcClusterName.equalsIgnoreCase("BDDev1")){
			srcNnURLAddressPort = "hftp://hdpr03mn01.mayo.edu:50070";
		}
		if (srcClusterName.equalsIgnoreCase("BDDev3")){
			srcNnURLAddressPort = "hftp://hdpr05mn01.mayo.edu:50070";
		}
		
		if (srcClusterName.equalsIgnoreCase("BDTest2")
				//|| srcClusterName.equalsIgnoreCase("BDPrd")
				){
			srcNnURLAddressPort = "hftp://hdpr01mn01.mayo.edu:50070";
		}
		if (srcClusterName.equalsIgnoreCase("BDTest3")){
			srcNnURLAddressPort = "hftp://hdpr06mn01.mayo.edu:50070";
		}
		
		if (srcClusterName.equalsIgnoreCase("BDProd2")){
			srcNnURLAddressPort = "hftp://hdpr02mn01.mayo.edu:50070";
		}
		if (srcClusterName.equalsIgnoreCase("BDProd3")){
			srcNnURLAddressPort = "hftp://hdpr07mn01.mayo.edu:50070";
		}
		
		
		
		if (srcClusterName.equalsIgnoreCase("BDSbx")
				|| srcClusterName.equalsIgnoreCase("BDSdbx")){
			srcNnURLAddressPort = "hftp://hdpr04mn02.mayo.edu:50070";
		}
		
		if (!srcFolderOrFileFullPathAndName.startsWith("/")){
			srcFolderOrFileFullPathAndName = "/" + srcFolderOrFileFullPathAndName; 
		}
		String hdfsCopySrcPart = srcNnURLAddressPort + srcFolderOrFileFullPathAndName;
			
		
//		String destNnIpAddress = "";
//		if (destClusterName.equalsIgnoreCase("BDTest2")
//				|| destClusterName.equalsIgnoreCase("BDPrd")){
//			//destNnIpAddress = "hdfs://hdpr01mn01.mayo.edu:8020";
//			destNnIpAddress = "hdfs://hdpr01mn01.mayo.edu";
//		}
//		if (destClusterName.equalsIgnoreCase("BDProd2")){
//			//destNnIpAddress = "hdfs://hdpr02mn01.mayo.edu:8020";
//			destNnIpAddress = "hdfs://hdpr02mn01.mayo.edu";
//		}
//		if (destClusterName.equalsIgnoreCase("BDDev1")){
//			//destNnIpAddress = "hdfs://hdpr03mn01.mayo.edu:8020";
//			destNnIpAddress = "hdfs://hdpr03mn01.mayo.edu";
//		}
//		if (destClusterName.equalsIgnoreCase("BDSbx")
//				|| destClusterName.equalsIgnoreCase("BDSdbx")){
//			//destNnIpAddress = "hdfs://hdpr04mn02.mayo.edu:8020";
//			destNnIpAddress = "hdfs://hdpr04mn02.mayo.edu";
//		}
		
		String destNnURLAddressPort = "";
		if (destClusterName.equalsIgnoreCase("BDDev1")){
			destNnURLAddressPort = "hftp://hdpr03mn01.mayo.edu:50070";
		}
		if (destClusterName.equalsIgnoreCase("BDDev3")){
			destNnURLAddressPort = "hftp://hdpr05mn01.mayo.edu:50070";
		}
		
		if (destClusterName.equalsIgnoreCase("BDTest2")
				//|| destClusterName.equalsIgnoreCase("BDPrd")
				){
			destNnURLAddressPort = "hftp://hdpr01mn01.mayo.edu:50070";
		}
		if (destClusterName.equalsIgnoreCase("BDTest3")){
			destNnURLAddressPort = "hftp://hdpr06mn01.mayo.edu:50070";
		}
		
		if (destClusterName.equalsIgnoreCase("BDProd2")){
			destNnURLAddressPort = "hftp://hdpr02mn01.mayo.edu:50070";
		}
		if (destClusterName.equalsIgnoreCase("BDProd3")){
			destNnURLAddressPort = "hftp://hdpr07mn01.mayo.edu:50070";
		}
		
		
		
		if (destClusterName.equalsIgnoreCase("BDSbx")
				|| destClusterName.equalsIgnoreCase("BDSdbx")){
			destNnURLAddressPort = "hftp://hdpr04mn02.mayo.edu:50070";
		}
		
		if (!destFolderOrFileFullPathAndName.startsWith("/")){
			destFolderOrFileFullPathAndName = "/" + destFolderOrFileFullPathAndName; 
		}
		String hdfsCopyDestPart = destNnURLAddressPort + destFolderOrFileFullPathAndName;
		
		String hdfsCopyFullCmdStr = "su hdfs -c 'hadoop distcp -p " + hdfsCopySrcPart + " " + hdfsCopyDestPart + "'"; 
				
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			if (tdhVersionHighClusterName.equalsIgnoreCase("BDTest2")
							|| tdhVersionHighClusterName.equalsIgnoreCase("BDPrd")){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2MN02ConParameters()); 
			}
			if (tdhVersionHighClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2MN02ConParameters()); 
			}
			if (tdhVersionHighClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); 
			}
			if (tdhVersionHighClusterName.equalsIgnoreCase("BDSbx")
					|| tdhVersionHighClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
			}
			
			String currHdfsFolderOrFileCopy_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsCopyFullCmdStr, "");
			System.out.println("\n -*- currHdfsFolderOrFileCopy_PlinkSingleFullCmd: \n" + currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
						
			if (exitVal == 0){			
				System.out.println("\n***Succeeded - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			} else {
				System.out.println("\n---Failed - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			}//end if
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
	
	}//end interVersionCopyHdfsFolderOrFile
	
///////////////////////////////////////////////////////////////////////////////////////////////////////	
	public static void moveHdfsFolderOrFile (String srcClusterName, String srcFolderOrFileFullPathAndName, 
			String destClusterName, String destFolderFullPathAndName){
		//working hadoop cmd for hdfs: 
		//   hadoop fs -mv hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27
		//   hadoop fs -mv /data/amalgahistory/ArchiveTable_Dump /data/amalgahistory/ArchiveTable_Dump_1
		String srcNnIpAddress = "";
		if (srcClusterName.equalsIgnoreCase("BDTest2")
				|| srcClusterName.equalsIgnoreCase("BDPrd")){
			srcNnIpAddress = "hdfs://hdp001-nn:8020";
		}
		if (srcClusterName.equalsIgnoreCase("BDPInt")){
			srcNnIpAddress = "hdfs://hdp002-nn:8020";
		}		
		if (srcClusterName.equalsIgnoreCase("BDDev1")){
			srcNnIpAddress = "hdfs://hdp003-nn:8020";
		}
		if (srcClusterName.equalsIgnoreCase("BDSbx")
				|| srcClusterName.equalsIgnoreCase("BDSdbx")){
			srcNnIpAddress = "hdfs://hdp004-nn:8020";
		}
		
		if (!srcFolderOrFileFullPathAndName.startsWith("/")){
			srcFolderOrFileFullPathAndName = "/" + srcFolderOrFileFullPathAndName; 
		}
		String hdfsCopySrcPart = srcNnIpAddress + srcFolderOrFileFullPathAndName;
			
		
		String destNnIpAddress = "";
		if (destClusterName.equalsIgnoreCase("BDTest2")
				|| destClusterName.equalsIgnoreCase("BDPrd")){
			destNnIpAddress = "hdfs://hdp001-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDPInt")){
			destNnIpAddress = "hdfs://hdp002-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDDev1")){
			destNnIpAddress = "hdfs://hdp003-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDSbx")
				|| destClusterName.equalsIgnoreCase("BDSdbx")){
			destNnIpAddress = "hdfs://hdp004-nn:8020";
		}
		
		if (!destFolderFullPathAndName.startsWith("/")){
			destFolderFullPathAndName = "/" + destFolderFullPathAndName; 
		}
		String hdfsCopyDestPart = destNnIpAddress + destFolderFullPathAndName;
		
		String hdfsCopyFullCmdStr = "su hdfs -c 'hadoop fs -mv " + hdfsCopySrcPart + " " + hdfsCopyDestPart + "'"; 
				
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN01ConParameters());
			
			String currHdfsFolderOrFileCopy_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsCopyFullCmdStr, "");
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
						
			if (exitVal == 0){			
				System.out.println("\n***Succeeded - Moving HDFS Folder/File To Detination Folder - " + destFolderFullPathAndName);	
			} else {
				System.out.println("\n---Failed - Moving HDFS Folder/File To Detination Folder - " + destFolderFullPathAndName);	
			}//end if
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
	
	}//end moveHdfsFolderOrFile
	
	public static void copyHdfsFolderOrFile (String srcClusterName, String srcFolderOrFileFullPathAndName, 
			String destClusterName, String destFolderOrFileFullPathAndName){
		//working hadoop cmd for hdfs: 
		//   hadoop fs -cp hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27
		String srcNnIpAddress = "";
		if (srcClusterName.equalsIgnoreCase("BDTest2")
				|| srcClusterName.equalsIgnoreCase("BDPrd")){
			srcNnIpAddress = "hdfs://hdp001-nn:8020";
		}
		if (srcClusterName.equalsIgnoreCase("BDProd2")){
			srcNnIpAddress = "hdfs://hdp002-nn:8020";
		}
		if (srcClusterName.equalsIgnoreCase("BDDev1")){
			srcNnIpAddress = "hdfs://hdp003-nn:8020";
		}
		if (srcClusterName.equalsIgnoreCase("BDSbx")
				|| srcClusterName.equalsIgnoreCase("BDSdbx")){
			srcNnIpAddress = "hdfs://hdp004-nn:8020";
		}
		
		if (!srcFolderOrFileFullPathAndName.startsWith("/")){
			srcFolderOrFileFullPathAndName = "/" + srcFolderOrFileFullPathAndName; 
		}
		String hdfsCopySrcPart = srcNnIpAddress + srcFolderOrFileFullPathAndName;
			
		
		String destNnIpAddress = "";
		if (destClusterName.equalsIgnoreCase("BDTest2")
				|| destClusterName.equalsIgnoreCase("BDPrd")){
			destNnIpAddress = "hdfs://hdp001-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDProd2")){
			destNnIpAddress = "hdfs://hdp002-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDDev1")){
			destNnIpAddress = "hdfs://hdp003-nn:8020";
		}
		if (destClusterName.equalsIgnoreCase("BDSbx")
				|| destClusterName.equalsIgnoreCase("BDSdbx")){
			destNnIpAddress = "hdfs://hdp004-nn:8020";
		}
		
		if (!destFolderOrFileFullPathAndName.startsWith("/")){
			destFolderOrFileFullPathAndName = "/" + destFolderOrFileFullPathAndName; 
		}
		String hdfsCopyDestPart = destNnIpAddress + destFolderOrFileFullPathAndName;
		
		String hdfsCopyFullCmdStr = "su hdfs -c 'hadoop fs -cp " + hdfsCopySrcPart + " " + hdfsCopyDestPart + "'"; 
				
		try {
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); //getBdProd2EN02ConParameters..getBdTest2EN02ConParameters...getBdProd2EN01ConParameters()
			
			String currHdfsFolderOrFileCopy_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsCopyFullCmdStr, "");
			System.out.println("\n -*- currHdfsFolderOrFileCopy_PlinkSingleFullCmd: \n" + currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsFolderOrFileCopy_PlinkSingleFullCmd);
						
			if (exitVal == 0){			
				System.out.println("\n***Succeeded - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			} else {
				System.out.println("\n---Failed - Copying HDFS Folder/File To Detination Folder/File - " + destFolderOrFileFullPathAndName);	
			}//end if
			
		} catch (Exception e) {				
			e.printStackTrace();
		}//end try
	
	}//end copyHdfsFolderOrFile
	
	
	public static ArrayList<String>  obtainHdfsFolder_FileList_4AFileType (String hdfsFileNameIndicator,
											ArrayList<String> currHdfsFolder_AllSubFolderAndFilePathNameList){
		ArrayList<String> currHdfsFolder_FileList_4AFileType = new  ArrayList<String>();		
		
		for (String temp: currHdfsFolder_AllSubFolderAndFilePathNameList){			
			if (temp.contains(hdfsFileNameIndicator)){
				currHdfsFolder_FileList_4AFileType.add(temp);
				//System.out.println(" *** added filePathAndName " + filePathAndName);
			}			
		}//end for
		
		return currHdfsFolder_FileList_4AFileType;
	}//end obtainHdfsFolder_FileList_4AFileType
		
	
	public static void mergeHdfsSrcParentFolder_EachSubFolderFiles_IntoOneDestFile (ArrayList<String> srcSubFolderList, String srcParentFolderName,String destHdfsFolderName, String tempHdfsFileMergingScriptFilePathAndName,String localScriptFilesFolder, String enServerSqoopScriptFileDirectory, ULServerCommandFactory bdENCommFactory){
		
		generateHdfsSrcParentFolder_ChildSubFolderFiles_MergingScriptFile(srcSubFolderList, srcParentFolderName,
				destHdfsFolderName, tempHdfsFileMergingScriptFilePathAndName);
		
		runScriptFile_OnBDCluster (tempHdfsFileMergingScriptFilePathAndName, 
				localScriptFilesFolder, enServerSqoopScriptFileDirectory, bdENCommFactory);
	}//end mergeSingleHdfsSrcFolder_EachSubFolderFiles_IntoOneDestFile
	
	public static void copyHdfsSrcParentFolder_EachSubFolderFile_IntoOneDestFile (ArrayList<String> srcSubFolderList, String srcParentFolderName,String destHdfsFolderName, String tempHdfsFileMergingScriptFilePathAndName,String localScriptFilesFolder, String enServerSqoopScriptFileDirectory, ULServerCommandFactory bdENCommFactory){
		
		generateSingleHdfsSrcFolder_ChildSubFolderSingleFile_CopyingScriptFile(srcSubFolderList, srcParentFolderName,
				destHdfsFolderName, tempHdfsFileMergingScriptFilePathAndName);
		
		runScriptFile_OnBDCluster (tempHdfsFileMergingScriptFilePathAndName, 
				localScriptFilesFolder, enServerSqoopScriptFileDirectory, bdENCommFactory);
	}//end copyHdfsSrcParentFolder_EachSubFolderFile_IntoOneDestFile
	
	
	public static void mergeSingleHdfsSrcFolder_AllFiles_IntoOneDestFile (String srcHdfsFolderName, String destHdfsFolderName, int destFileNumber, String tempHdfsFileMergingScriptFilePathAndName,	String localScriptFilesFolder, String enServerSqoopScriptFileDirectory, ULServerCommandFactory bdENCommFactory){
		
		generateSingleHdfsSrcFolderFiles_MergingScriptFile(srcHdfsFolderName,
				destHdfsFolderName, destFileNumber, tempHdfsFileMergingScriptFilePathAndName);
		
		runScriptFile_OnBDCluster (tempHdfsFileMergingScriptFilePathAndName, 
				localScriptFilesFolder, enServerSqoopScriptFileDirectory, bdENCommFactory);
		
	}//end mergeSingleHdfsSrcFolderFiles_IntoOneDestFile
	
	
	//For mergeSingleHdfsSrcFolderFiles_IntoOneDestFile:
	public static void generateSingleHdfsSrcFolderFiles_MergingScriptFile(String srcHdfsFolderName,
			String destHdfsFolderName, int destFileNumber, String tempHdfsFileMergingScriptFilePathAndName){
		System.out.println(" *-* srcHdfsFolderName is: " + srcHdfsFolderName);
		System.out.println(" *-* destHdfsFolderName is: " + destHdfsFolderName);
		File aFile = new File (tempHdfsFileMergingScriptFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			    			    
			    output.close();
			    outStream.close();
			}//end if
			
			if (!aFile.exists()){
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			   	    
			    output.close();
			    outStream.close();
			} 
			
			FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName, true);
		    PrintWriter output = new PrintWriter (outStream);
		    
		    String partMStr = "part-m-";				
			if (destFileNumber < 10 ){
				partMStr = partMStr + "0000" + destFileNumber;
			}
			if (destFileNumber >= 10 & destFileNumber <100){
				partMStr = partMStr + "000" + destFileNumber;
			}
			if (destFileNumber >= 100 & destFileNumber <1000){
				partMStr = partMStr + "00" + destFileNumber;
			}
			if (destFileNumber >= 1000 & destFileNumber <10000){
				partMStr = partMStr + "0" + destFileNumber;
			}			
			if (destFileNumber >= 10000 ){
				partMStr = partMStr + destFileNumber;
			}
			
			String tempDestHdfs_FilePathAndName = "";
			if (destHdfsFolderName.endsWith("/")){
				tempDestHdfs_FilePathAndName = destHdfsFolderName + partMStr;
			} else {
				tempDestHdfs_FilePathAndName = destHdfsFolderName + "/"+ partMStr;
			}
		   		
			output.println("hadoop fs -rmr -skipTrash " + tempDestHdfs_FilePathAndName + ";");	
			//Working: hadoop fs -cat /data/amalgahistory/opnote/sirs_edel/2014/8/1/* | hadoop fs -put - /data/amalgahistory/opnote/sirs_edel_2/2014/8/1/part-m-00000
			String tempHdfsFolderMergeCmd = "hadoop fs -cat " + srcHdfsFolderName + "/* | hadoop fs -put - " + tempDestHdfs_FilePathAndName;
			output.println(tempHdfsFolderMergeCmd + ";");			
			
		    output.println("hadoop fs -chmod -R 550 " + destHdfsFolderName + ";");
			output.close();
		    outStream.close();			
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
	}//end generateSingleHdfsSrcFolderFiles_MergingScriptFile 
	
	//For mergeSingleHdfsSrcFolder_EachSubFolderFiles_IntoOneDestFile:
	public static void generateHdfsSrcParentFolder_ChildSubFolderFiles_MergingScriptFile(ArrayList<String> tempParentFolder_ChildLevelSubFolderList, String srcParentFolderName,
			String destHdfsFolderName, String tempHdfsFileMergingScriptFilePathAndName){
		System.out.println(" *-* srcParentFolderName is: " + srcParentFolderName);
		System.out.println(" *-* destHdfsFolderName is: " + destHdfsFolderName);
		File aFile = new File (tempHdfsFileMergingScriptFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			    			    
			    output.close();
			    outStream.close();
			}//end if
			
			if (!aFile.exists()){
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			   	    
			    output.close();
			    outStream.close();
			} 
			
			FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName, true);
		    PrintWriter output = new PrintWriter (outStream);
			for (int i = 0; i < tempParentFolder_ChildLevelSubFolderList.size(); i++){
				String tempSrcHdfs_SubFolderPathAndName = tempParentFolder_ChildLevelSubFolderList.get(i);
				String[] tempSplit = tempSrcHdfs_SubFolderPathAndName.split("_");
				int seqNofragNumber = Integer.valueOf(tempSplit[tempSplit.length-1]);
				
				String partMStr = "part-m-";				
				if (seqNofragNumber < 10 ){
					partMStr = partMStr + "0000" + seqNofragNumber;
				}
				if (seqNofragNumber >= 10 & seqNofragNumber <100){
					partMStr = partMStr + "000" + seqNofragNumber;
				}
				if (seqNofragNumber >= 100 & seqNofragNumber <1000){
					partMStr = partMStr + "00" + seqNofragNumber;
				}
				if (seqNofragNumber >= 1000 & seqNofragNumber <10000){
					partMStr = partMStr + "0" + seqNofragNumber;
				}				
				if (seqNofragNumber >= 10000 ){
					partMStr = partMStr + seqNofragNumber;
				}
				
				String tempDestHdfs_FilePathAndName = "";
				if (destHdfsFolderName.endsWith("/")){
					tempDestHdfs_FilePathAndName = destHdfsFolderName + partMStr;
				} else {
					tempDestHdfs_FilePathAndName = destHdfsFolderName + "/"+ partMStr;
				}
				
				//String tempDestHdfs_FolderPathAndName_R = tempSrcHdfs_SubFolderPathAndName.replace(srcParentFolderName, "");				
				//if (destHdfsFolderName.endsWith("/")){
				//	tempDestHdfs_FolderPathAndName_R +=  partMStr;	
				//} else {
				//	tempDestHdfs_FolderPathAndName_R +=  "/"+ partMStr;
				//}				
				//String tempDestHdfs_FilePathAndName = destHdfsFolderName + tempDestHdfs_FolderPathAndName_R;
			
				output.println("hadoop fs -rmr -skipTrash " + tempDestHdfs_FilePathAndName + ";");	
				
				//Working: hadoop fs -cat /data/amalgahistory/opnote/sirs_edel/2014/8/1/* | hadoop fs -put - /data/amalgahistory/opnote/sirs_edel_2/2014/8/1/part-m-00000
				//String tempHdfsFolderMergeCmd = "hadoop fs -cat " + tempSrcHdfs_SubFolderPathAndName + "/* | hadoop fs -put - " + tempDestHdfs_FilePathAndName;
				String tempHdfsFolderMergeCmd = "hadoop fs -cp " + tempSrcHdfs_SubFolderPathAndName + "/part-m-00000 | hadoop fs -put - " + tempDestHdfs_FilePathAndName;
				output.println(tempHdfsFolderMergeCmd + ";");			    					
			}//end for	
			
		    output.println("hadoop fs -chmod -R 550 " + destHdfsFolderName + ";");
			output.close();
		    outStream.close();			
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
	}//end generateSingleHdfsSrcFolder_ChildSubFolderFiles_MergingScriptFile 
	
	public static void generateSingleHdfsSrcFolder_ChildSubFolderSingleFile_CopyingScriptFile(ArrayList<String> tempParentFolder_ChildLevelSubFolderList, String srcParentFolderName,
			String destHdfsFolderName, String tempHdfsFileMergingScriptFilePathAndName){
		System.out.println(" *-* srcParentFolderName is: " + srcParentFolderName);
		System.out.println(" *-* destHdfsFolderName is: " + destHdfsFolderName);
		File aFile = new File (tempHdfsFileMergingScriptFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			    			    
			    output.close();
			    outStream.close();
			}//end if
			
			if (!aFile.exists()){
				aFile.createNewFile();
				FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName);
			    PrintWriter output = new PrintWriter (outStream);
			    output.println("sudo su - hdfs;");		    
			    output.println("hadoop fs -mkdir " + destHdfsFolderName + ";");
			    output.println("hadoop fs -chmod -R 750 " + destHdfsFolderName + ";");			   	    
			    output.close();
			    outStream.close();
			} 
			
			FileWriter outStream = new FileWriter(tempHdfsFileMergingScriptFilePathAndName, true);
		    PrintWriter output = new PrintWriter (outStream);
			for (int i = 0; i < tempParentFolder_ChildLevelSubFolderList.size(); i++){
				String tempSrcHdfs_SubFolderPathAndName = tempParentFolder_ChildLevelSubFolderList.get(i);
				String[] tempSplit = tempSrcHdfs_SubFolderPathAndName.split("_");
				int seqNofragNumber = Integer.valueOf(tempSplit[tempSplit.length-1]);
				
				String partMStr = "part-m-";				
				if (seqNofragNumber < 10 ){
					partMStr = partMStr + "0000" + seqNofragNumber;
				}
				if (seqNofragNumber >= 10 & seqNofragNumber <100){
					partMStr = partMStr + "000" + seqNofragNumber;
				}
				if (seqNofragNumber >= 100 & seqNofragNumber <1000){
					partMStr = partMStr + "00" + seqNofragNumber;
				}
				if (seqNofragNumber >= 1000 & seqNofragNumber <10000){
					partMStr = partMStr + "0" + seqNofragNumber;
				}				
				if (seqNofragNumber >= 10000 ){
					partMStr = partMStr + seqNofragNumber;
				}
				
				String tempDestHdfs_FilePathAndName = "";
				if (destHdfsFolderName.endsWith("/")){
					tempDestHdfs_FilePathAndName = destHdfsFolderName + partMStr;
				} else {
					tempDestHdfs_FilePathAndName = destHdfsFolderName + "/"+ partMStr;
				}
				
				//String tempDestHdfs_FolderPathAndName_R = tempSrcHdfs_SubFolderPathAndName.replace(srcParentFolderName, "");				
				//if (destHdfsFolderName.endsWith("/")){
				//	tempDestHdfs_FolderPathAndName_R +=  partMStr;	
				//} else {
				//	tempDestHdfs_FolderPathAndName_R +=  "/"+ partMStr;
				//}				
				//String tempDestHdfs_FilePathAndName = destHdfsFolderName + tempDestHdfs_FolderPathAndName_R;
			
				output.println("hadoop fs -rmr -skipTrash " + tempDestHdfs_FilePathAndName + ";");	
				
				//Working: hadoop fs -cat /data/amalgahistory/opnote/sirs_edel/2014/8/1/* | hadoop fs -put - /data/amalgahistory/opnote/sirs_edel_2/2014/8/1/part-m-00000
				//String tempHdfsFolderMergeCmd = "hadoop fs -cat " + tempSrcHdfs_SubFolderPathAndName + "/* | hadoop fs -put - " + tempDestHdfs_FilePathAndName;
				String tempHdfsFolderMergeCmd = "hadoop fs -cp " + tempSrcHdfs_SubFolderPathAndName + "/part-m-00000 " + tempDestHdfs_FilePathAndName;
				output.println(tempHdfsFolderMergeCmd + ";");			    					
			}//end for	
			
		    output.println("hadoop fs -chmod -R 550 " + destHdfsFolderName + ";");
			output.close();
		    outStream.close();			
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
	}//end generateSingleHdfsSrcFolder_ChildSubFolderSingleFile_CopyingScriptFile 
	


	public static int runScriptFile_OnBDCluster (String localScriptFileFullPathAndName, 
					String localScriptFilesFolder, String enServerScriptFileDirectory, ULServerCommandFactory bdENCommFactory){
		//1. Preparation
		if (!localScriptFilesFolder.endsWith("\\")){
			localScriptFilesFolder += "\\";
		}
		if (!enServerScriptFileDirectory.endsWith("/")){
			enServerScriptFileDirectory += "/";
		}
		
        //String localHdfsImportScriptFilePathAndName = sqoopImportScriptFilePathAndName;
        System.out.println("--*--localScriptFileFullPathAndName: \n" + localScriptFileFullPathAndName + "\n");
        String scriptFileName = localScriptFileFullPathAndName.replace(localScriptFilesFolder, ""); 
        //enServerScriptFileDirectory = "/home/hdfs/"; 
         
        String enServerScriptFilePathAndName = enServerScriptFileDirectory + scriptFileName;
        System.out.println("--*--enServerScriptFilePathAndName: \n" + enServerScriptFilePathAndName + "\n");
        
        //2. 5 steps to run a script .sh file      
        //2.(1) Copy Script .sh file from windows local to BD Cluster Entry Node server local - '/home/hdfs/' 
                    
        int exitVal = copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster (scriptFileName, 
        		localScriptFilesFolder, enServerScriptFileDirectory, bdENCommFactory);
       
        
        //2.(2) Step #2: Activate script .sh file inside the folder - '/home/hdfs/' on EntryNodeLocal of a BD Cluster
        if (exitVal !=0 ){
			return exitVal;
		} else {
			exitVal =  activateScriptFile_OnEntryNodeLocal_OnBDCluster (enServerScriptFilePathAndName, 
					bdENCommFactory);
		}
      
        
        //2.(3) Step #3: Step 3 - Change ownership of script .sh file from root into hdfs inside the folder - '/home/hdfs/' on EntryNodeLocal of a BD Cluster
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 changeScriptFile_OwnershipTohdfs_OnEntryNodeLocal_OnBDCluster (enServerScriptFilePathAndName, 
						bdENCommFactory);
		}
    	
        
        //2.(4) Step #4: Step 4 - Run script file inside BD Cluster Entry Node folder - '/home/hdfs/' 
        if (exitVal !=0 ){
			return exitVal;
		} else {
			Hdfs tempHdfs = new Hdfs ("BDProd2");
			//Sqoop tempSqoop = new Sqoop ("BDProd2");
			
			try {
				String tempHdfsScriptFile_RunScriptPathAndName = tempHdfs.createRunServerHdfsScriptFileForPutty(scriptFileName); //enServerScriptFilePathAndName..scriptFileName
				String puttyHdfsScriptFileRunFullCmd = bdENCommFactory.getPuttyScriptCommandString(tempHdfsScriptFile_RunScriptPathAndName);
	    		
	    		exitVal = executePuttyCommandOnLocalMachine(puttyHdfsScriptFileRunFullCmd);    	
		    	String exeResultsMsg = "";
		    	if (exitVal != 0){
		    		exeResultsMsg = "--xxx-- Failed In Running Hdfs Script File Step 4 - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
				} else {
					exeResultsMsg = "--***-- Suceessfully Executed Hdfs Script File Step 4 - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
				}	    	
		    	System.out.println(" **4** exeResultsMsg: " + exeResultsMsg);
			} catch (IOException e) {				
				e.printStackTrace();
			}//end try				
		}
        
        //2.(5) Step #4: Step 5 - Remove script file inside BD Cluster Entry Node folder - '/home/hdfs/' 
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 exitVal = removeFile_FromEntryNodeLocal_OnBDCluster (enServerScriptFilePathAndName, 
						bdENCommFactory);				
		}      
        return exitVal;
	}//end runScriptFile_OnBDCluster
	

	public static int changeScriptFile_OwnershipTohdfs_OnEntryNodeLocal_OnBDCluster (String enServerScriptFilePathAndName, 
			ULServerCommandFactory bdENCommFactory){
		//String plinkChangeOToHdfs_ForScriptFileOperationFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("chown hdfs:users " + enServerScriptFilePathAndName, "");			    		
		
		int exitVal = changeLinuxFile_OwnershipTohdfs_OnBDClusterNode (enServerScriptFilePathAndName, "hdfs:users", bdENCommFactory);
		
		return exitVal;
	}//end changeScriptFile_OwnershipTohdfs_OnEntryNodeLocal_OnBDCluster
	
	public static int changeLinuxFile_OwnershipTohdfs_OnBDClusterNode (String enServerScriptFilePathAndName, String newOwnerGroupStr, ULServerCommandFactory bdENCommFactory){
		String plinkChangeOToHdfs_ForScriptFileOperationFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("chown " + newOwnerGroupStr + " " +  enServerScriptFilePathAndName, "");			    		
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkChangeOToHdfs_ForScriptFileOperationFullPscpCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Changing Linux File Ownership To '" + newOwnerGroupStr + "' on Entry Node Local - " + plinkChangeOToHdfs_ForScriptFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Changing Script File Ownership To '" + newOwnerGroupStr + "' on Entry Node Local - " + plinkChangeOToHdfs_ForScriptFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **3** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end changeScriptFile_OwnershipTohdfs_OnEntryNodeLocal_OnBDCluster	
	
	public static int activateScriptFile_OnEntryNodeLocal_OnBDCluster (String enServerScriptFilePathAndName, 
			ULServerCommandFactory bdENCommFactory){
		String plinkActivateScriptFileOperationFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("chmod +x " + enServerScriptFilePathAndName, "");	
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkActivateScriptFileOperationFullPscpCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Activating Script File on Entry Node Local - " + plinkActivateScriptFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Activating Script File on Entry Node Local - " + plinkActivateScriptFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **2** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end activateScriptFile_OnEntryNodeLocal_OnBDCluster
	

	
	
	
	public static int copyFile_FromWindowsLocal_ToHDFS_OnBDCluster (String fileName, String localFilesFolder,
			String enServerFileDirectory, String hdfsFilePathAndName, ULServerCommandFactory bdENCommFactory){
		
		//1. Preparation 
		if (!localFilesFolder.endsWith("\\")){
			localFilesFolder += "\\";
		}
		if (!enServerFileDirectory.endsWith("/")){
			enServerFileDirectory += "/";
		}
		
		String localFileFullPathAndName = localFilesFolder + fileName;
        System.out.println("--*--localFileFullPathAndName: \n" + localFileFullPathAndName + "\n");
        //String fileName = localFileFullPathAndName.replace(localFilesFolder, ""); 
        //enServerFileDirectory = "/data/tmp/"; //"/data/tmp/".."/home/hdfs/"
        
        //2. 3 steps: Copy file from windows local to BD Cluster Entry Node server local To BD Cluster HDFS System 
        //2.(1) Copy file from windows local to BD Cluster Entry Node server local 
        int exitVal = copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster (fileName, 
				 localFilesFolder, enServerFileDirectory, bdENCommFactory);
        
        
        //2.(2) Copy file from BD Cluster Entry Node server local to HDFS system        
        String enServerFilePathAndName = enServerFileDirectory + fileName;
        System.out.println("--*--enServerFilePathAndName: \n" + enServerFilePathAndName + "\n");
        
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 exitVal = copyFile_FromEntryNodeLocal_ToHdfs_OnBDCluster (enServerFilePathAndName, 
		    			hdfsFilePathAndName, bdENCommFactory);
		}       
        
        //2.(3) Remove file from BD Cluster Entry Node server local       
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 exitVal = removeFile_FromEntryNodeLocal_OnBDCluster (enServerFilePathAndName, 
						bdENCommFactory);
		}          
        return exitVal;        
	}//end copyFile_OnWindowsLocal_ToHDFS_OnBDCluster
	
	
	public static int removeFile_FromEntryNodeLocal_OnBDCluster (String enServerFilePathAndName, 
			ULServerCommandFactory bdENCommFactory){
		String plinkRemoveFileOperationFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("rm -f " + enServerFilePathAndName, "");	
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkRemoveFileOperationFullPscpCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Removing File From Entry Node Local - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Removing File From Entry Node Local - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **4 or 5** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end removeFile_FromEntryNodeLocal_OnBDCluster
	
	
	public static int copyFile_FromHdfs_ToEntryNodeLocal_OnBDCluster (String hdfsFilePathAndName, 
			String enServerFilePathAndName, ULServerCommandFactory bdENCommFactory){
		//1. Preparation  
		System.out.println("--*-- Src hdfsFilePathAndName: \n" + hdfsFilePathAndName + "\n");  
        System.out.println("--*-- Dest enServerFilePathAndName: \n" + enServerFilePathAndName + "\n");
             
       
        //2. Copy the file from windows local to BD Cluster Entry Node server local
        String exeResultsMsg = "";
        
        Hdfs tempHdfs = new Hdfs ("BDProd2");	    		
		int exitVal = 10000;
		try {
			String tempHdfsScriptFile_RunScriptPathAndName = tempHdfs.createCopyFile_FromHdfsToLocal_ScriptFileForPutty(hdfsFilePathAndName, enServerFilePathAndName);
			String puttyHdfsScriptFileRunFullCmd = bdENCommFactory.getPuttyScriptCommandString(tempHdfsScriptFile_RunScriptPathAndName);
    		
    		exitVal = executePuttyCommandOnLocalMachine(puttyHdfsScriptFileRunFullCmd);    	
	    	
	    	if (exitVal != 0){
	    		exeResultsMsg = "--xxx-- Failed In Running Script Copying File from Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			} else {
				exeResultsMsg = "--***-- Suceessfully Executed  Running Script Copying File from Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			}	    	
	    	System.out.println("\n **3** exeResultsMsg: " + exeResultsMsg);
		} catch (IOException e) {				
			e.printStackTrace();
		}//end try      
    	
    	return exitVal;
	}//end copyFile_FromHdfs_ToEntryNodeLocal_OnBDCluster
	
	
	
	public static int copyFile_FromEntryNodeLocal_ToHdfs_OnBDCluster (String enServerFilePathAndName, 
			String hdfsFilePathAndName, ULServerCommandFactory bdENCommFactory){
		//1. Preparation       
        System.out.println("--*--Src enServerFilePathAndName: \n" + enServerFilePathAndName + "\n");
        System.out.println("--*--Dest hdfsFilePathAndName: \n" + hdfsFilePathAndName + "\n");        
       
        //2. Remove pre-existing HDFS file - hdfsFilePathAndName if it does exist on HDFS
        Hdfs tempHdfs = new Hdfs ("BDProd2");	
        int exitVal = 10000;
        
        exitVal = removeFile_FromHDFS_OnBDCluster (hdfsFilePathAndName, bdENCommFactory);         
        //Alternative method:
        //try {
        //	String tempHdfsRemoveScriptFile_RunScriptPathAndName = tempHdfs.createRemoveFile_FromHdfs_ScriptFileForPutty(hdfsFilePathAndName);
     	//	String puttyHdfsRemoveScriptFileRunFullCmd = bdENCommFactory.getPuttyScriptCommandString(tempHdfsRemoveScriptFile_RunScriptPathAndName);
     	//	exitVal = executePuttyCommandOnLocalMachine(puttyHdfsRemoveScriptFileRunFullCmd);  
        //} catch (IOException e) {				
		//	e.printStackTrace();
		//}//end try        
              
       
        
        //3. Copy the file from BD Cluster Entry Node server local to HDFS
        String exeResultsMsg = ""; 
        try {
			String tempHdfsScriptFile_RunScriptPathAndName = tempHdfs.createCopyFile_FromLocalToHdfs_ScriptFileForPutty(enServerFilePathAndName, hdfsFilePathAndName);
			String puttyHdfsScriptFileRunFullCmd = bdENCommFactory.getPuttyScriptCommandString(tempHdfsScriptFile_RunScriptPathAndName);
    		
    		exitVal = executePuttyCommandOnLocalMachine(puttyHdfsScriptFileRunFullCmd);    	
	    	
	    	if (exitVal != 0){
	    		exeResultsMsg = "--xxx-- Failed In Running Script for Copying File To Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			} else {
				exeResultsMsg = "--***-- Suceessfully Executed Script for Copying File To Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			}	    	
	    	System.out.println("\n **2** exeResultsMsg: " + exeResultsMsg);
		} catch (IOException e) {				
			e.printStackTrace();
		}//end try    
        
    	
    	return exitVal;
	}//end copyFile_OnEntryNodeLocal_ToHdfs_OnBDCluster
	
		
	private static int removeFile_FromHDFS_OnBDCluster (String hdfsFilePathAndName, 
			ULServerCommandFactory bdENCommFactory){
		String plinkRemoveFileOperationFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("su hdfs -c \'hadoop fs -rm -skipTrash " + hdfsFilePathAndName + "\'", "");	
		//working cmd per se: su hdfs -c "hadoop fs -rm -skipTrash /data/test/dcUatDataFile_No1"
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkRemoveFileOperationFullPscpCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Removing Existing File From HDFS - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Removing Existing File From HDFS - " + plinkRemoveFileOperationFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **pre-2** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end removeFile_FromHDFS_OnBDCluster
	

	public static int copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster (String fileName, 
					String localFilesFolder, String enServerFileDirectory, ULServerCommandFactory bdENCommFactory){
		//1. Preparation 
		if (!localFilesFolder.endsWith("\\")){
			localFilesFolder += "\\";
		}
		if (!enServerFileDirectory.endsWith("/")){
			enServerFileDirectory += "/";
		}
		
		String localFileFullPathAndName  = localFilesFolder + fileName;
        System.out.println("--*1a--localFileFullPathAndName: \n" + localFileFullPathAndName + "\n");
        //String fileName = localFileFullPathAndName.replace(localFilesFolder, ""); 
        //enServerFileDirectory = "/data/tmp/"; //"/data/tmp/".."/home/hdfs/"
        
        //2. Copy the file from windows local to BD Cluster Entry Node server local
        String enServerFilePathAndName = enServerFileDirectory + fileName;
        System.out.println("--*1b--enServerFilePathAndName: \n" + enServerFilePathAndName + "\n");
        
        //2.(1)
        int exitVal = safelyCreateAFolder_OnEntryNodeLocal_OnBDCluster (enServerFileDirectory, bdENCommFactory);
        System.out.println("--*1c--exitVal of safelyCreateAFolder_OnEntryNodeLocal_OnBDCluster: " + exitVal + "\n");
        
        if (exitVal !=0 ){
			return exitVal;
		} else {
			 //2.(2) execute copying of the local data file from local windows to destination BD cluster entry node server local folder 
			String tempFileCopyFullPscpCmd = bdENCommFactory.getPscpFileCopyToServerCommandString(localFileFullPathAndName, enServerFilePathAndName);
	        //System.out.println("--- tempFileCopyFullPscpCmd is: \n" + tempFileCopyFullPscpCmd);
			
			exitVal = executePuttyCommandOnLocalMachine(tempFileCopyFullPscpCmd);    	
	    	String exeResultsMsg = "";
	    	if (exitVal != 0){
	    		exeResultsMsg = "--xxx-- Failed In Running Script for Copying File To BD Cluster - " + tempFileCopyFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			} else {
				exeResultsMsg = "--***-- Suceessfully Executed Script for Copying File To BD Cluster - " + tempFileCopyFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			}	    	
	    	System.out.println("\n **1** exeResultsMsg: " + exeResultsMsg);
		}    	
    	return exitVal;
	}//end copyFile_OnWindowsLocal_ToEntryNodeLocal_OnBDCluster
	

		
//	public static int appendingStringDataToFile_OnEntryNodeLocal_OnBDCluster (String dataStr4AppendingToSrvFile,  String enServerFilePathAndName,
//			ULServerCommandFactory bdENCommFactory){
//		String plinkAppendStringDataToAFileFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("echo \"" + dataStr4AppendingToSrvFile + "\" >> " +  enServerFilePathAndName, "");			    		
//		
//		int exitVal = executePuttyCommandOnLocalMachine(plinkAppendStringDataToAFileFullPscpCmd);
//		String exeResultsMsg = "";
//		if (exitVal != 0){
//			exeResultsMsg = "\n--xxx-- Failed In Appending a String Data to the target file on Entry Node Local - " + plinkAppendStringDataToAFileFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
//		} else {
//			exeResultsMsg = "\n--***-- Suceessfully Executed Appending a String Data to the target file on Entry Node Local " + plinkAppendStringDataToAFileFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
//		}		 	
//    	System.out.println(" **pre-1** exeResultsMsg: " + exeResultsMsg);
//		
//		return exitVal;
//	}//end appendingStringDataToFile_OnEntryNodeLocal_OnBDCluster
	
	

	public static ArrayList<String> findLinesInAFile_OnEntryNodeLocal_OnBDCluster (String enServerFilePathAndName, String searchStringPattern, String grepStr,
			ULServerCommandFactory bdENCommFactory, String tempLocalWorkignFileAndPathName){
		//grep "javax.jdo" /etc/hive/conf.dist/hive-site.xml
		//grep "javax.jdo" /etc/hive/conf.dist/hive-site.xml | grep Password

		String findLineListCmdStr = "grep " + searchStringPattern + " " + enServerFilePathAndName;		
		if (!grepStr.isEmpty()){
			findLineListCmdStr += " | " + grepStr; 
		}
					
		String findFile_LineList_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(findLineListCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
		
		int exitVal = executePuttyCommandOnLocalMachine(findFile_LineList_PlinkSingleFullCmd);
			
		ArrayList<String> foundLinesList = new ArrayList<String>();
		if (exitVal == 0){			
			try {
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";						
				while ( (line = br.readLine()) != null) {
					foundLinesList.add(line);           
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n*** Succeeded - Finding lines in the entry node file - \'" + enServerFilePathAndName + "\'" );
			} catch (FileNotFoundException e) {
				System.out.println("\n---Failed - Finding lines in the entry node file - \'" + enServerFilePathAndName + "\'" );
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("\n---Failed - Finding lines in the entry node file - \'" + enServerFilePathAndName + "\'" );
				e.printStackTrace();
			}				
		} else {
			System.out.println("\n---Failed - Finding lines in the entry node file - \'" + enServerFilePathAndName + "\'" );	
		}//end if	
		
		return foundLinesList;
	}//end findLinesInAFile_OnEntryNodeLocal_OnBDCluster

	public static ArrayList<String> findFileList_OnEntryNodeLocal_OnBDCluster (String enServerFileDirectory, String fileNamePattern, String grepStr,
			ULServerCommandFactory bdENCommFactory, String tempLocalWorkignFileAndPathName){
		
		//find /usr/lib/hbase -name "hbase-0*" | grep -v tests;// /usr/lib/hbase/hbase-0.94.6.1.3.2.0-110-security.jar
		//find /usr/lib/hbase -name "guava-1*" | grep -v tests;// /usr/lib/hbase/lib/guava-11.0.2.jar 
		//find /usr/lib/ -name "hive-hbase-handler-0*" ; // /usr/lib/hive/lib/hive-hbase-handler-0.11.0.1.3.2.0-110.jar
		//find /usr/lib/ -name "zookeeper-*" | grep "zookeeper/zookeeper"; // /usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-110.jar
		//find /usr/lib/ -name "zookeeper-*" | grep "usr/lib/zookeeper"; // /usr/lib/zookeeper/zookeeper-3.4.5.1.3.2.0-110.jar		
		
		String findFileListCmdStr = "find " + enServerFileDirectory + " -name '" + fileNamePattern + "'";		
		if (!grepStr.isEmpty()){
			findFileListCmdStr += " | " + grepStr; 
		}
		
		
					
		String findParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(findFileListCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
		
		System.out.println(" *** executing findParentFolder_FileListInfo_PlinkSingleFullCmd: \n" + findParentFolder_FileListInfo_PlinkSingleFullCmd); 
		int exitVal = executePuttyCommandOnLocalMachine(findParentFolder_FileListInfo_PlinkSingleFullCmd);
			
//		if (exitVal != 0){
//			String tempEnRecordFilePathAndName = "/home/hdfs/foundTemp.txt";
//			findParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(findFileListCmdStr, " 1>" + tempEnRecordFilePathAndName);
//			
//			exitVal = executePuttyCommandOnLocalMachine(findParentFolder_FileListInfo_PlinkSingleFullCmd);
//			if (exitVal == 0){
//				copyToWinLocal_File_OnEntryNodeLocal_OnBDCluster (tempEnRecordFilePathAndName, 
//						tempLocalWorkignFileAndPathName, bdENCommFactory);
//			}			
//		}
		
		ArrayList<String> foundFilePathAndNameList = new ArrayList<String>();
		if (exitVal == 0){			
			try {
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";						
				while ( (line = br.readLine()) != null) {
					foundFilePathAndNameList.add(line);           
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n*** Succeeded - Finding files in the entry node folder - \'" + enServerFileDirectory + "\'" );
			} catch (FileNotFoundException e) {
				System.out.println("\n---Failed - Finding files in the entry node folder - \'" + enServerFileDirectory + "\'" );
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("\n--2b--Failed - Finding files in the entry node folder - \'" + enServerFileDirectory + "\'" );
				e.printStackTrace();
			}				
		} else {
			System.out.println("\n--1--Failed - Finding files in the entry node folder - \'" + enServerFileDirectory + "\'" );		
		}//end if	
		
		return foundFilePathAndNameList;
	}//end findFileList_OnEntryNodeLocal_OnBDCluster
	
	public static void copyFile_ToWindowsLocal_AndDeleteFile_FromEntryNodeLocal_OnBDCluster (String enFilePathAndName, 
			String localTargetFilePathAndName, ULServerCommandFactory bdENCommFactory){	
		prepareFile (localTargetFilePathAndName,  "Local Target File");
		
		String tempEnFileCopyToWinLocalFullPscpCmd = bdENCommFactory.getPscpFileCopyToLocalCommandString(enFilePathAndName, localTargetFilePathAndName);
		
		int exitVal = executePuttyCommandOnLocalMachine(tempEnFileCopyToWinLocalFullPscpCmd);			
		if (exitVal != 0){					
			//Removing original file on the entry node 
			String rmEnServerFileCmdStr = "rm " + enFilePathAndName; 		
			System.out.println("**** rmEnServerFileCmdStr: " + rmEnServerFileCmdStr);
			
			String rmEnServerFile_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(rmEnServerFileCmdStr, "");
			System.out.println("**** rmEnServerFile_PlinkSingleFullCmd: \n" + rmEnServerFile_PlinkSingleFullCmd);
			int exitValue = executePuttyCommandOnLocalMachine(rmEnServerFile_PlinkSingleFullCmd);
			if (exitValue == 0){
				System.out.println("--*-- Suceessful - Copying to local and deleting file - '" + enFilePathAndName + "' from " + bdENCommFactory.getServerURI() );
				
			} else {
				System.out.println("--*-- Copying to local and deleting file - '" + enFilePathAndName + "' from " + bdENCommFactory.getServerURI() );
			}			
		}	
	
	}//end copyFile_ToWindowsLocal_AndDeleteFile_FromEntryNodeLocal_OnBDCluster
	
	public static void copyFile_ToWindowsLocal_FromEntryNodeLocal_OnBDCluster (String enFilePathAndName, 
			String localTargetFilePathAndName, ULServerCommandFactory bdENCommFactory){	
		prepareFile (localTargetFilePathAndName,  "Local Target File");
		
		String tempEnFileCopyToWinLocalFullPscpCmd = bdENCommFactory.getPscpFileCopyToLocalCommandString(enFilePathAndName, localTargetFilePathAndName);
		
		int exitVal = executePuttyCommandOnLocalMachine(tempEnFileCopyToWinLocalFullPscpCmd);			
		if (exitVal == 0){
			System.out.println("--*-- Suceessful - Copying to local the file - '" + enFilePathAndName + "' from " + bdENCommFactory.getServerURI() );
			
		} else {
			System.out.println("--*-- Copying to local the file - '" + enFilePathAndName + "' from " + bdENCommFactory.getServerURI() );
		}	
	
	}//end copyFile_ToWindowsLocal_FromEntryNodeLocal_OnBDCluster	

	
	public static int safelyCreateAFolder_OnEntryNodeLocal_OnBDCluster (String enServerFileDirectory, 
			ULServerCommandFactory bdENCommFactory){
		String plinkSafelyCreateAFolderFullPscpCmd = bdENCommFactory.getPlinkSingleCommandString("mkdir -p " + enServerFileDirectory, "");	
		
		int exitVal = executePuttyCommandOnLocalMachine(plinkSafelyCreateAFolderFullPscpCmd);    	
    	String exeResultsMsg = "";
    	if (exitVal != 0){
    		exeResultsMsg = "--xxx-- Failed In Running Plink Cmd for Safely Creating a Folder on Entry Node Local - " + plinkSafelyCreateAFolderFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		} else {
			exeResultsMsg = "--***-- Suceessfully Executed Running Plink Cmd for Safely Creating a Folder on Entry Node Local - " + plinkSafelyCreateAFolderFullPscpCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
		}	    	
    	System.out.println(" **pre-1** exeResultsMsg: " + exeResultsMsg);
		
		return exitVal;
	}//end safelyCreateAFolder_OnEntryNodeLocal_OnBDCluster
	
////////////////////////////////////////////////////////////////////////////////////////////////////////
//Specific purpose methods:
	
	public static void downloadHdfsFileMessagesCountsFile_FromServer (String enServerHdfsFileCountsFileName, 
				String localHdfsFileMsgCountRecordFilePathAndName, ULServerCommandFactory bdENCommFactory){				
		
		prepareFile (localHdfsFileMsgCountRecordFilePathAndName,  "HDFS File Message Counting Result");
		
		
		//String currHdfsFileMessageNumberViewCmdStr = "cat " + enServerHdfsFileCountsFileName; //					
		//System.out.println("**** currHdfsFileMessageNumberViewCmdStr: " + currHdfsFileMessageNumberViewCmdStr);			
		//String currHDFS_FileMessageCounting_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(currHdfsFileMessageNumberViewCmdStr, " 1>" + hdfsFileMsgCountRecordFilePathAndName);
		//System.out.println("**** currHDFS_FileMessageCounting_PlinkSingleFullCmd: \n" + currHDFS_FileMessageCounting_PlinkSingleFullCmd);
		copyFile_ToWindowsLocal_FromEntryNodeLocal_OnBDCluster (enServerHdfsFileCountsFileName, 
				localHdfsFileMsgCountRecordFilePathAndName, bdENCommFactory);
		
//		String tempHDFSMsgCountingResults_FileCopyFullPscpCmd = bdENCommFactory.getPscpFileCopyToLocalCommandString(enServerHdfsFileCountsFileName, localHdfsFileMsgCountRecordFilePathAndName);
//		
//		int exitVal = executePuttyCommandOnLocalMachine(tempHDFSMsgCountingResults_FileCopyFullPscpCmd);			
//		if (exitVal == 0){
//			System.out.println("--*-- Suceessful - Downloaded Message Re-Counts From Server" );
//			
//		} else {
//			System.out.println("--*-- Failed - Downloading Message Re-Counts From Server" );
//		}			
	}//end downloadHdfsFileMessagesCountsFile_FromServer	
	

	public static void downloadDelete_HdfsFileMessagesCountsFile_FromServer (String enServerHdfsFileCountsFileName, 
				String localHdfsFileMsgCountRecordFilePathAndName, ULServerCommandFactory bdENCommFactory){				
		
		prepareFile (localHdfsFileMsgCountRecordFilePathAndName,  "HDFS File Message Counting Result");
		
		
		//String currHdfsFileMessageNumberViewCmdStr = "cat " + enServerHdfsFileCountsFileName; //					
		//System.out.println("**** currHdfsFileMessageNumberViewCmdStr: " + currHdfsFileMessageNumberViewCmdStr);			
		//String currHDFS_FileMessageCounting_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(currHdfsFileMessageNumberViewCmdStr, " 1>" + hdfsFileMsgCountRecordFilePathAndName);
		//System.out.println("**** currHDFS_FileMessageCounting_PlinkSingleFullCmd: \n" + currHDFS_FileMessageCounting_PlinkSingleFullCmd);
		
		copyFile_ToWindowsLocal_AndDeleteFile_FromEntryNodeLocal_OnBDCluster (enServerHdfsFileCountsFileName, 
				localHdfsFileMsgCountRecordFilePathAndName, bdENCommFactory);
		
//		String tempHDFSMsgCountingResults_FileCopyFullPscpCmd = bdENCommFactory.getPscpFileCopyToLocalCommandString(enServerHdfsFileCountsFileName, localHdfsFileMsgCountRecordFilePathAndName);
//		
//		int exitVal = executePuttyCommandOnLocalMachine(tempHDFSMsgCountingResults_FileCopyFullPscpCmd);			
//		if (exitVal != 0){					
//			//(2) Removing temp counting files on the server 
//			String rmEnServerTempHdfsCountRecordFileCmdStr = "rm " + enServerHdfsFileCountsFileName; 		
//			System.out.println("**** rmEnServerTempHdfsCountRecordFileCmdStr: " + rmEnServerTempHdfsCountRecordFileCmdStr);
//			
//			String rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(rmEnServerTempHdfsCountRecordFileCmdStr, "");
//			System.out.println("**** rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd: \n" + rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd);
//			int exitValue = executePuttyCommandOnLocalMachine(rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd);
//			if (exitValue == 0){
//				System.out.println("--*-- Suceessful - Downloaded and deleted Message Re-Counts From Server" );
//				
//			} else {
//				System.out.println("--*-- Failed - Downloading and deleting Message Re-Counts From Server" );
//			}			
//		}		
	}//end downloadDelete_HdfsFileMessagesCountsFile_FromServer	
	
	
	
	public static int downloadMessagesCountsInSingleFolder_AllHdfsFiles_FromServer (String hdfsFileMsgCountRecordFilePathAndName,
							String enServerHdfsFileCountsFileName, ULServerCommandFactory bdENCommFactory){				
		int totalHdfsFolder_MsgCount = 0;
		try {
			
			File recFile = new File(hdfsFileMsgCountRecordFilePathAndName);
			if(!recFile.exists()){
				recFile.createNewFile(); 			
			}
			
			//String currHdfsFileMessageNumberViewCmdStr = "cat " + enServerHdfsFileCountsFileName; //					
			//System.out.println("**** currHdfsFileMessageNumberViewCmdStr: " + currHdfsFileMessageNumberViewCmdStr);			
			//String currHDFS_FileMessageCounting_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(currHdfsFileMessageNumberViewCmdStr, " 1>" + hdfsFileMsgCountRecordFilePathAndName);
			//System.out.println("**** currHDFS_FileMessageCounting_PlinkSingleFullCmd: \n" + currHDFS_FileMessageCounting_PlinkSingleFullCmd);
			
			String tempHDFSMsgCountingResults_FileCopyFullPscpCmd = bdENCommFactory.getPscpFileCopyToLocalCommandString(enServerHdfsFileCountsFileName, hdfsFileMsgCountRecordFilePathAndName);
			
			int exitVal = executePuttyCommandOnLocalMachine(tempHDFSMsgCountingResults_FileCopyFullPscpCmd);			
			if (exitVal == 0){					
				FileReader bFileReader = new FileReader(hdfsFileMsgCountRecordFilePathAndName);
				BufferedReader br = new BufferedReader(bFileReader);
				
				String line = "";
				
				int countFileNum = 0;
				while ( (line = br.readLine()) != null) {
					System.out.println("*** line is: " + line);
					countFileNum++;
					
					String[] lineSplit = line.split(",");
					int currFileMsgCount = Integer.valueOf(lineSplit[1]);
					System.out.println("(" +countFileNum+ ") currFileMsgCount: " + currFileMsgCount);
					
					totalHdfsFolder_MsgCount += currFileMsgCount;						
		        }//end while
				br.close();
				bFileReader.close();
				
				
				FileWriter outStream_hdfsmc = new FileWriter(hdfsFileMsgCountRecordFilePathAndName, true);		    
			    PrintWriter output_hdfsmc = new PrintWriter (outStream_hdfsmc);
			    output_hdfsmc.println("\n--*-- Total HDFS File Number: " + countFileNum);
				System.out.println("--*-- Total HDFS File Number: " + countFileNum);
				output_hdfsmc.println("\n--*-- Final HDFS Folder Message Count is: " + totalHdfsFolder_MsgCount);
				System.out.println("--*-- Final HDFS Folder Message Count is: " + totalHdfsFolder_MsgCount);
				output_hdfsmc.close();
				outStream_hdfsmc.close();
			}			
			
			//(2) Removing temp counting files on the server 
			String rmEnServerTempHdfsCountRecordFileCmdStr = "rm " + enServerHdfsFileCountsFileName; 		
			System.out.println("**** rmEnServerTempHdfsCountRecordFileCmdStr: " + rmEnServerTempHdfsCountRecordFileCmdStr);
			
			String rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(rmEnServerTempHdfsCountRecordFileCmdStr, "");
			System.out.println("**** rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd: \n" + rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd);
			int exitValue = executePuttyCommandOnLocalMachine(rmEnServerTempHdfsCountRecordFile_PlinkSingleFullCmd);
			if (exitValue == 0){
				System.out.println("--*-- Suceessful - Get Messages Counts In SingleFolder_AllHdfsFiles From Server" );
				
			} else {
				System.out.println("--*-- Failed - Get Messages Counts In SingleFolder_AllHdfsFiles From Server" );
			}			
		} catch (IOException e1) {				
			e1.printStackTrace();
		}//end try
		
		return totalHdfsFolder_MsgCount;
	}//end downloadMessagesCountsInSingleFolder_AllHdfsFiles_FromServer
	
	
	public static void generateMessageCountingScriptFile_Grep (ArrayList<String> currParentFolderHdfsFileList, 
			String msgIndicator, String tempLocalCountingScriptFileAndPathName, String enServerHdfsFileCountsFileName) {
		try {
			File shFile = new File(tempLocalCountingScriptFileAndPathName);
			if(!shFile.exists()){
				shFile.createNewFile(); 
				FileWriter outStream_hdfsmc = new FileWriter(tempLocalCountingScriptFileAndPathName);		    
			    PrintWriter output_hdfsmc = new PrintWriter (outStream_hdfsmc);
				output_hdfsmc.println("sudo su - hdfs;");			
				output_hdfsmc.close();
				outStream_hdfsmc.close();
			}
			
			if(shFile.exists()){
				shFile.delete(); 
				
				shFile.createNewFile(); 
				FileWriter outStream_hdfsmc = new FileWriter(tempLocalCountingScriptFileAndPathName);		    
			    PrintWriter output_hdfsmc = new PrintWriter (outStream_hdfsmc);
				output_hdfsmc.println("sudo su - hdfs;");			
				output_hdfsmc.close();
				outStream_hdfsmc.close();
			}
			FileWriter outStream_hdfsmc = new FileWriter(tempLocalCountingScriptFileAndPathName, true);		    
		    PrintWriter output_hdfsmc = new PrintWriter (outStream_hdfsmc);
			
			for (int i = 0; i < currParentFolderHdfsFileList.size(); i++){				
				//(a)
				String hdfsTgtFilePathAndName = currParentFolderHdfsFileList.get(i);				
				//System.out.println("\n*** (" + (i+1) + ")  For hdfsTgtFilePathAndName - " + hdfsTgtFilePathAndName + ": ");
				
				String msgCountingCmd_L = "(echo -ne '" + hdfsTgtFilePathAndName + ",'; ";
				String msgCountingCmd_R = "hadoop fs -cat " + hdfsTgtFilePathAndName + " | grep -c \"" + msgIndicator +  "\") >> " + enServerHdfsFileCountsFileName + ";";
				String msgCountingFullCmd = msgCountingCmd_L + msgCountingCmd_R;
				output_hdfsmc.println(msgCountingFullCmd);
				//System.out.println(msgCountingFullCmd);									
			}//end for
			//output_hdfsmc.println("exit;");			
			output_hdfsmc.close();
			outStream_hdfsmc.close();
			
		} catch (IOException e) {				
			e.printStackTrace();
		}//end try		
	}//end generateMessageCountingScriptFile_Grep
	

	public static void generateMessageCountingScriptFile (ArrayList<String> currFolderHdfsFileList, 
			 String serverRunMsgCountingScriptCmdCore, String tableIndicator, String localCountingScriptFileAndPathName) {
		try {
			prepareFile_DeleteOld (localCountingScriptFileAndPathName, "HDFS File Message Counting");
			
			FileWriter outStream_hdfsmc = new FileWriter(localCountingScriptFileAndPathName);		    
			PrintWriter output_hdfsmc = new PrintWriter (outStream_hdfsmc);
			output_hdfsmc.println("sudo su - hdfs;");			
			output_hdfsmc.close();
			outStream_hdfsmc.close();
			
			outStream_hdfsmc = new FileWriter(localCountingScriptFileAndPathName, true);		    
		    output_hdfsmc = new PrintWriter (outStream_hdfsmc);
		   
			for (int i = 0; i < currFolderHdfsFileList.size(); i++){				
				//(a)
				String hdfsTgtFilePathAndName = currFolderHdfsFileList.get(i);				
				System.out.println("\n*** (" + (i+1) + ")  For hdfsTgtFilePathAndName - " + hdfsTgtFilePathAndName + ": ");
				
				//Example:
				// serverRunMsgCountingScriptCmdCore = "./RunDumpFileOrFolderMessageCount.sh";
				// tableIndicator = "archive";
				// hdfsTgtFilePathAndName = "/data/amalgahistory/ArchiveTable_Dump/part-m-00000";
				// msgCountingFullCmd = "./RunDumpFileOrFolderMessageCount.sh archive /data/amalgahistory/ArchiveTable_Dump/"
				
				String msgCountingFullCmd = serverRunMsgCountingScriptCmdCore + " " + tableIndicator + " " + hdfsTgtFilePathAndName + ";";
				
				
				output_hdfsmc.println(msgCountingFullCmd);
				System.out.println(msgCountingFullCmd);									
			}//end for
			//output_hdfsmc.println("exit;");			
			output_hdfsmc.close();
			outStream_hdfsmc.close();
			
		} catch (IOException e) {				
			e.printStackTrace();
		}//end try		
	}//end generateMessageCountingScriptFile

	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static ArrayList<String>  obtainHdfsFolder_ChildLevelSubFolderList (String hdfsFolderPathAndName,
											ArrayList<String> currHdfsFolder_AllSubFolderAndFilePathNameList){
		ArrayList<String> currHdfsFolder_ChildLevelSubFolderList = new  ArrayList<String>();
		
		if (!hdfsFolderPathAndName.endsWith("/")){
			hdfsFolderPathAndName = hdfsFolderPathAndName + "/";
		}
		for (String temp: currHdfsFolder_AllSubFolderAndFilePathNameList){
			temp = temp.replace(hdfsFolderPathAndName, "");
			if (temp.contains("/")){
				String [] tempSplit = temp.split("/");
				String childSubFolderPathAndName = hdfsFolderPathAndName + tempSplit[0];
				currHdfsFolder_ChildLevelSubFolderList.add(childSubFolderPathAndName);
				//System.out.println(" *** added childSubFolderPathAndName " + childSubFolderPathAndName);
			}			
		}//end for
		currHdfsFolder_ChildLevelSubFolderList = getSortedDuplicatesFreeArrayList(currHdfsFolder_ChildLevelSubFolderList);
		return currHdfsFolder_ChildLevelSubFolderList;
	}//end obtainHdfsFolder_ChildLevelSubFolderList
	
	
	public static long  obtainHdfsFolderOrFileSizeInBytes(String hdfsFolderPathAndName, 
							String tempLocalWorkignFileAndPathName, String bdClusterName){		
		long currFolderSize = 0;
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
			
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null;
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							//|| bdClusterName.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN01ConParameters()); 
			}
			
			
						
			String hdfsFileListingCmdStr = "su hdfs -c 'hadoop fs -dus " + hdfsFolderPathAndName + "'"; 
						
			String currHdfsParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
							
				while ( (line = br.readLine()) != null) {
					line = line.replaceAll("\\s+", "===");
					String [] lineSplit = line.split("===");
					
					currFolderSize = Long.valueOf(lineSplit[1]);	            
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n*** Succeeded - Getting Size In Bytes - \'" + currFolderSize + "\' for HDFS folder - " + hdfsFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Getting Size In Bytes for HDFS folder - " + hdfsFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		//System.out.println("\n --*-*-- Current HDFS Folder Size Is: " + currFolderSize);
	
		return currFolderSize;
	}//end obtainHdfsFolderOrFileSizeInBytes
	
	public static ArrayList<String>  obtainHdfsFolder_AllSubFolderAndFileList(String hdfsFolderPathAndName, 
											String tempLocalWorkignFileAndPathName, String bdClusterName){
		ArrayList<String> currHdfsFolder_AllSubFolderAndFilePathNameList = new  ArrayList<String>();
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
			
			ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
			ULServerCommandFactory bdENCommFactory = null;
			
			if (bdClusterName.equalsIgnoreCase("BDDev1")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev1EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDDev3")) {					
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDev3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDProd2")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd2EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDProd3")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProd3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDTest2")
							//|| bdClusterName.equalsIgnoreCase("BDPrd")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest2EN01ConParameters()); 
			}
			if (bdClusterName.equalsIgnoreCase("BDTest3")
							){						
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdTest3EN01ConParameters()); 
			}
			
			if (bdClusterName.equalsIgnoreCase("BDSbx")
					|| bdClusterName.equalsIgnoreCase("BDSdbx")){
				bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxEN01ConParameters()); 
			}
			
			
			String hdfsFileListingCmdStr = "su hdfs -c 'hadoop fs -lsr " + hdfsFolderPathAndName + "'"; 
						
			String currHdfsParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(hdfsFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currHdfsParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;
					line = line.replaceAll(" /", " ===/");
					//System.out.println(" *-* line is: " + line);
					
					if (line.contains("===") ){
						String [] lineSplit = line.split("===");						
						for (int i = 0; i < lineSplit.length; i++){
							String tempField = lineSplit[i];							
							if (tempField.contains(hdfsFolderPathAndName)){
								currHdfsFolder_AllSubFolderAndFilePathNameList.add(tempField);
								//System.out.println(" *** added tempSubFolder_FileFullPathAndName: " + tempField);					
								
							}								
						}//end for 							
					} //end outer if			            
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Listing HDFS Sub-Folder & Files - " + hdfsFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Listing HDFS Sub-Folder & Files - " + hdfsFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		//System.out.println("\n --*-*-- currHdfsFolder_AllSubFolderAndFilePathNameList size : " + currHdfsFolder_AllSubFolderAndFilePathNameList.size());
		//currHdfsParentFolder_SubFolderPathNameList = getSortedDuplicatesFreeArrayList(currHdfsParentFolder_SubFolderPathNameList);
		return currHdfsFolder_AllSubFolderAndFilePathNameList;
	}//end getHdfsFolder_AllSubFolderAndFileList
	
////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int copyFolder_FromEntryNodeLocal_ToHdfs_OnBDCluster(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, String hdfsParentFolderName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_AllLevels_SymLink_FilePathNameList 
				= obtainLinuxFolder_AllLevels_SymLink_FilePathNameList(linuxFolderPathAndName,tempLocalWorkignFileAndPathName, bdENCommFactory);
		
				
		if (!hdfsParentFolderName.endsWith("/")){
			hdfsParentFolderName += "/";
		}
		
		Hdfs tempHdfs = new Hdfs ("BDProd2");	         
        
        //Copy the file from BD Cluster Entry Node server local to HDFS
		int exitVal = 1000;
        String exeResultsMsg = ""; 
        try {
			String tempHdfsScriptFile_RunScriptPathAndName = tempHdfs.createCopyFileList_FromLocalToHdfs_ScriptFileForPutty(currLinuxFolder_AllLevels_SymLink_FilePathNameList, hdfsParentFolderName);				
					
			String puttyHdfsScriptFileRunFullCmd = bdENCommFactory.getPuttyScriptCommandString(tempHdfsScriptFile_RunScriptPathAndName);
    		
    		exitVal = executePuttyCommandOnLocalMachine(puttyHdfsScriptFileRunFullCmd);    	
	    	
	    	if (exitVal != 0){
	    		exeResultsMsg = "--xxx-- Failed In Running Script for Copying File To Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			} else {
				exeResultsMsg = "--***-- Suceessfully Executed Script for Copying File To Hdfs - " + puttyHdfsScriptFileRunFullCmd + " on the Server:  " + bdENCommFactory.getServerURI() + " \n";
			}	    	
	    	System.out.println("\n **2** exeResultsMsg: " + exeResultsMsg);
		} catch (IOException e) {				
			e.printStackTrace();
		}//end try    
        
    			
		//for (String tempLinuxFilePathAndName: currLinuxFolder_AllLevelsFilePathNameList){
		//	String tempLinuxFilePathAndName1 = "";
		//	if (tempLinuxFilePathAndName.startsWith("/")){
		//		tempLinuxFilePathAndName1 = tempLinuxFilePathAndName.replaceFirst("/", "");
		//	}
		//	String tempHdfsFilePathAndName = hdfsParentFolderName + tempLinuxFilePathAndName1;
		//	exitVal = copyFile_FromEntryNodeLocal_ToHdfs_OnBDCluster (tempLinuxFilePathAndName, tempHdfsFilePathAndName, bdENCommFactory);
		//	if (exitVal != 0){
		//		break;
		//	}
		//}//end for
		
		return exitVal;	
	}//end copyFolder_FromEntryNodeLocal_ToHdfs_OnBDCluster
	
	public static ArrayList<String>  obtainLinuxFolder_AllLevels_SymLink_FilePathNameList(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		
		ArrayList<String> currLinuxFolder_AllLevelsSymLink_FilePathNameList = obtainLinuxFolder_AllLevelsFilePathNameList (linuxFolderPathAndName, tempLocalWorkignFileAndPathName, bdENCommFactory); 
		
		ArrayList<String> tempList = obtainLinuxFolder_AllLevelsSymLinkList (linuxFolderPathAndName, tempLocalWorkignFileAndPathName, bdENCommFactory); 
		
		currLinuxFolder_AllLevelsSymLink_FilePathNameList.addAll(tempList);
		
		return currLinuxFolder_AllLevelsSymLink_FilePathNameList;
		
	}//end obtainLinuxFolder_AllLevels_SymLinkAndFile_PathNameList
	
	
	public static ArrayList<String>  obtainLinuxFolder_AllLevelsFilePathNameList(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_AllLevelsFilePathNameList = new  ArrayList<String>();
		
		if (!linuxFolderPathAndName.endsWith("/")){
			linuxFolderPathAndName += "/";
		}
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
			
									
			//String linuxFolderAndFileListingCmdStr = "ls -p " + linuxFolderPathAndName; //with possible symbolic links
			String linuxFolderAndFileListingCmdStr = "find " + linuxFolderPathAndName + " ! -type l -type f"; //with no symbolic links
						
			String currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(linuxFolderAndFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;
					//if (!line.endsWith("/")){
					//	String tempFilePathAndName = linuxFolderPathAndName + line;					
					//	currLinuxFolder_AllLevelsFilePathNameList.add(tempFilePathAndName);
					//	System.out.println("*-* added tempFilePathAndName is: " + tempFilePathAndName);
					//}
														
					currLinuxFolder_AllLevelsFilePathNameList.add(line);
					//System.out.println("*-* added tempFilePathAndName is: " + line);
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Recursively Listing Linux All-Level Files of " + linuxFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Recursively Listing Linux All-Level Files of " + linuxFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		return currLinuxFolder_AllLevelsFilePathNameList;
	}//end obtainLinuxFolder_AllLevelsFilePathNameList
	
	public static ArrayList<String>  obtainLinuxFolder_AllLevelsSymLinkList(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_AllLevelsSymLinkList = new  ArrayList<String>();
		
		if (!linuxFolderPathAndName.endsWith("/")){
			linuxFolderPathAndName += "/";
		}
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
						
			String linuxFolderAndFileListingCmdStr = "find " + linuxFolderPathAndName + " -type l"; //get all symbolic links
						
			String currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(linuxFolderAndFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;
															
					currLinuxFolder_AllLevelsSymLinkList.add(line);
					//System.out.println("*-* added tempSymLink: " + line);
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Recursively Listing Linux All-Level SymLinks of " + linuxFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Recursively Listing Linux All-Level SymLinks of " + linuxFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		return currLinuxFolder_AllLevelsSymLinkList;
	}//end obtainLinuxFolder_AllLevelsSymLinkList
	
	
	public static ArrayList<String>  obtainLinuxFolder_ChildlevelSymLinkList(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_ChildlevelSymLinkList = new  ArrayList<String>();
		
		if (!linuxFolderPathAndName.endsWith("/")){
			linuxFolderPathAndName += "/";
		}
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}
									
			
			String linuxFolderAndFileListingCmdStr = "find " + linuxFolderPathAndName + " -maxdepth 1 -type l"; //with no symbolic links
						
			String currLinuxParentFolder_SymLinkListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(linuxFolderAndFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currLinuxParentFolder_SymLinkListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;																			
					currLinuxFolder_ChildlevelSymLinkList.add(line);
					//System.out.println("*-* added tempSymLink: " + line);
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Listing Linux Child-Level SymLinks of " + linuxFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Listing Linux Child-Level SymLinks of " + linuxFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		return currLinuxFolder_ChildlevelSymLinkList;
	}//end obtainLinuxFolder_ChildlevelSymLinkList
	
	
	public static ArrayList<String>  obtainLinuxFolder_ChildlevelFilePathNameList(String linuxFolderPathAndName, 
			String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_ChildlevelFilePathNameList = new  ArrayList<String>();
		
		if (!linuxFolderPathAndName.endsWith("/")){
			linuxFolderPathAndName += "/";
		}
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
			
									
			//String linuxFolderAndFileListingCmdStr = "ls -p " + linuxFolderPathAndName; //with possible symbolic links
			String linuxFolderAndFileListingCmdStr = "find " + linuxFolderPathAndName + " -maxdepth 1 ! -type l -type f"; //with no symbolic links
						
			String currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(linuxFolderAndFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;
					//if (!line.endsWith("/")){
					//	String tempFilePathAndName = linuxFolderPathAndName + line;					
					//	currLinuxFolder_ChildlevelFilePathNameList.add(tempFilePathAndName);
					//	System.out.println("*-* added tempFilePathAndName: " + tempFilePathAndName);
					//}
														
					currLinuxFolder_ChildlevelFilePathNameList.add(line);
					//System.out.println("*-* added tempFilePathAndName: " + line);
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Listing Linux Child-Level Files of " + linuxFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Listing Linux Child-Level Files of " + linuxFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		return currLinuxFolder_ChildlevelFilePathNameList;
	}//end obtainLinuxFolder_ChildlevelFilePathNameList
	
	
	public static ArrayList<String>  obtainLinuxFolder_SubFolderList(String linuxFolderPathAndName, 
											String tempLocalWorkignFileAndPathName, ULServerCommandFactory bdENCommFactory){
		ArrayList<String> currLinuxFolder_SubFolderList = new  ArrayList<String>();
		
		if (!linuxFolderPathAndName.endsWith("/")){
			linuxFolderPathAndName += "/";
		}
		
		try {			
			File aFile = new File(tempLocalWorkignFileAndPathName);
			if (!aFile.exists()){
				aFile.createNewFile();
			}		
			
									
			//String linuxFolderAndFileListingCmdStr = "ls -p " + linuxFolderPathAndName; //with possible symbolic links
			String linuxFolderAndFileListingCmdStr = "find " + linuxFolderPathAndName + " -maxdepth 1 ! -type l -type d"; //with no symbolic links
			
			String currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd = bdENCommFactory.getPlinkSingleCommandString(linuxFolderAndFileListingCmdStr, " 1>" + tempLocalWorkignFileAndPathName);
			
			int exitVal = executePuttyCommandOnLocalMachine(currLinuxParentFolder_FileListInfo_PlinkSingleFullCmd);
						
			if (exitVal == 0){					
				FileReader aFileReader = new FileReader(tempLocalWorkignFileAndPathName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int lineCount = 0;				
				while ( (line = br.readLine()) != null) {
					//lineCount++;
					if (!line.equalsIgnoreCase(linuxFolderPathAndName)){
						//String tempFolderOFileName = linuxFolderPathAndName + line;
						String tempFolderName = line;
						currLinuxFolder_SubFolderList.add(tempFolderName);
						//System.out.println("*-* added tempFolderName: " + tempFolderName);
					}	            
		        }//end while
				br.close();
				aFileReader.close();
				System.out.println("\n***Succeeded - Listing Linux Child-Level Sub-Folder of " + linuxFolderPathAndName);	
			} else {
				System.out.println("\n---Failed - Listing Linux Child-Level Sub-Folder of " + linuxFolderPathAndName);	
			}//end if
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		return currLinuxFolder_SubFolderList;
	}//end obtainLinuxFolder_SubFolderList
	
////////////////////////////////////////////////////////////////////////////////////////////////////////	
//shared methods:
	public static ArrayList<String> getSortedDuplicatesFreeArrayList (ArrayList<String> currArrayList){
		//Remove duplicates in currArrayList
	    HashSet<String> currArrayList_HashedSet = new HashSet<String>(currArrayList);
	    currArrayList.clear();
	    currArrayList.addAll(currArrayList_HashedSet);	    
	    //Sort the duplicates-removed srcCombinedTabList
		Collections.sort(currArrayList);
		return currArrayList;		
	}//end getSortedDuplicatesFreeArrayList	
	

	private static void prepareFile_DeleteOld (String localFilePathAndName, String fileNoticeInfo){
		File aFile = new File (localFilePathAndName);
		try {
			if (aFile.exists()){
				aFile.delete();
				System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
				aFile.createNewFile();
				System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
			}
			
			if (!aFile.exists()){
				aFile.createNewFile();
				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
			}				
		} catch (IOException e) {				
			e.printStackTrace();
		}			
	}//end prepareFile_DeleteOld

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
	
	@SuppressWarnings("unused")
	private static void prepareFolder(String localFolderPathAndName, String folderNoticeInfo){
		File aFolderFile = new File (localFolderPathAndName);		
		if (!aFolderFile.exists()){
			aFolderFile.mkdirs();			
			System.out.println("\n .. Created folder for " + folderNoticeInfo +": \n" + localFolderPathAndName); 
		}		
	}//end prepareFolder
	

	
	private static int executePuttyCommandOnLocalMachine (String fullPuttyCmd) {
		Runtime rt = Runtime.getRuntime();
		int exitVal = 10000;
		
		try {
			Process proc = rt.exec(fullPuttyCmd);
			
			//String currProcessIdNServer = ManagementFactory.getRuntimeMXBean().getName();
			//String currProcessId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0]; 
			//System.out.println(" --- Current Java Program Process Id & JVM Host is: " + currProcessIdNServer);

			exitVal = proc.waitFor();
			System.out.println("\n--- current fullPuttyCmd exitVal is: " + exitVal);			
		} catch (IOException e1) {			
			e1.printStackTrace();
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}
		return exitVal;
	}//end executePuttyCommandOnLocalMachine
	

}//end class

