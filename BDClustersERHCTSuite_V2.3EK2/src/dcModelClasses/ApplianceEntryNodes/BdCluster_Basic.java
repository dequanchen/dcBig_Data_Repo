package dcModelClasses.ApplianceEntryNodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import dcModelClasses.HdfsUtil;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-07/2014; 1/5/2016 (Kerberos); 1/14/2016; 5/11/2017 
*/ 

@SuppressWarnings("unused")
public class BdCluster_Basic {
	private String bdClusterName = "";
	private String bdClusterIdName = "";
	private String bdHdfsActiveNnIPAddressAndPort = "";
	private String bdHdfs1stNnIPAddressAndPort = "";
	private String bdHdfs2ndNnIPAddressAndPort = "";
	private String bdHdfs3rdNnIPAddressAndPort = "";
	private String nn1ServiceState = "";
	//private String rm1ServiceState = "";
	private String bdClusterActiveRMIPAddressAndPort = "";	
	private FileSystem hadoopFS = null;
	private String hdfsHomeDirectory = "";
	private String zooKeeperQuorum = "";
	private Configuration configuration = new Configuration();
	private ArrayList<String> currentClusterEntryNodeList = new  ArrayList<String>();	
	private String krbConfFilePathAndName = "";
	private String krbNnPrincipal = "";
	private String hdfsExternalPrincipal = ""; 
	private String hdfsInternalPrincipal = ""; 
	private String ambariQaInternalPrincipal = "";
	private String hiveSvcPrincipalName = "";
	private String hiveServer2BeelineConnString1 = "";
	private String hiveServer2BeelineConnString2 = "";
	private String hbaseInternalPrincipal = ""; 
	private String stormInternalPrincipal = ""; 
	private String hdfsExternalKeyTabFilePathAndName = "";
	private String hdfsInternalKeyTabFilePathAndName = "/etc/security/keytabs/hdfs.headless.keytab";
	private String ambariInternalKeyTabFilePathAndName = "/etc/security/keytabs/smokeuser.headless.keytab";
	private String hbaseInternalKeyTabFilePathAndName = "/etc/security/keytabs/hbase.headless.keytab";
	private String stormInternalKeyTabFilePathAndName = "/etc/security/keytabs/storm.headless.keytab";
	
	private String hbaseMasterPrincipal = ""; 
	private String hbaseRegionServerPrincipal = ""; 
	
	private String hbaseExternalPrincipal = ""; 
	private String hbaseExternalKeyTabFilePathAndName = "";
	private ArrayList<String> currentClusterSolrInstalledNodeList = new  ArrayList<String>();	
	private String currentClusterKnoxNodeName = "";
	private String currentClusterKnoxNode2Name = "";
	private ArrayList<String> currentClusterKnoxNodeList = new  ArrayList<String>();	
	private String activeWebHdfsHttpAddress = "";
	private String kafkaBrokerAndPortList = "";
	
	
	public BdCluster_Basic(String aBdClusterName) {
		this.bdClusterName = aBdClusterName;
		
		obtainBdClusterIdName();
		//obtainHdfsVNnIPAddressAndPort();
		obtainHdfsNnIPAddressAndPort();
		obtainHiveServer2BeelineConnectionStrings();
		this.bdHdfs1stNnIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.toLowerCase();
		this.bdHdfs2ndNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort.toLowerCase();
		//this.nn1ServiceState = HdfsUtil.getNameNode1ServiceState(aBdClusterName);
		this.nn1ServiceState = HdfsUtil.getNameNode1ServiceStateByWeb(aBdClusterName);
		if (this.nn1ServiceState.equalsIgnoreCase("active")){
			this.bdHdfsActiveNnIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort;	
			this.activeWebHdfsHttpAddress = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":50070") + "/webhdfs"; 
        } else {
        	this.bdHdfsActiveNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort;
        	this.activeWebHdfsHttpAddress = this.bdHdfs2ndNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":50070") + "/webhdfs"; 
        }
		
//		String rm1ServiceStatus = HdfsUtil.getResourceManager1ServiceStateByWeb(aBdClusterName);
//		if (rm1ServiceStatus.equalsIgnoreCase("active")){
//			this.bdClusterActiveRMIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs://", "").replace(":8020", ":8050");	
//			if (this.bdClusterName.equalsIgnoreCase("BDDev3") 
//					|| this.bdClusterName.equalsIgnoreCase("BDTest3")
//					|| this.bdClusterName.equalsIgnoreCase("BDprod3")
//					){
//				this.bdClusterActiveRMIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs://", "").replace("mn01", "mn03").replace(":8020", ":8050");	
//			}
//        } else {
//        	this.bdClusterActiveRMIPAddressAndPort =  this.bdHdfs2ndNnIPAddressAndPort.replace("hdfs://", "").replace(":8020", ":8050");        	 
//        }
			
		
		
	}//end constructor
	
	////////////////////////////////////////////////////////////////////////////

	private void obtainCurrentClusterEntryNodeList (String currClusterEntryNodeListFilePathAndName){
		if (!currClusterEntryNodeListFilePathAndName.isEmpty()){
			InputStream in = BdCluster.class.getResourceAsStream(currClusterEntryNodeListFilePathAndName);		
			InputStreamReader inStrReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inStrReader);
					
			try {				
				String line = "";
				//int addCount = 0;
				while ( (line = br.readLine()) != null) {
					this.currentClusterEntryNodeList.add(line.toUpperCase());
					//addCount++;
					//System.out.println("*-* (" + addCount + ") added a entry node: " + line);									            
				}
				br.close();
				inStrReader.close();
				in.close();			
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}		
	}//end obtainCurrentClusterEntryNodeList
	
	private ArrayList<String> obtainCurrentClusterSolrInstalledNodeList (String currClusterSolrInstalledNodeFilePathAndName){
		ArrayList<String> currClusterSolrInstalledNodeNameList = new ArrayList<String> ();
		if (!currClusterSolrInstalledNodeFilePathAndName.isEmpty()){
			InputStream in = BdCluster.class.getResourceAsStream(currClusterSolrInstalledNodeFilePathAndName);		
			InputStreamReader inStrReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inStrReader);
						
			try {				
				String line = "";				
				while ( (line = br.readLine()) != null) {
					if (!line.contains("#")){
						String [] lineSplit = line.split("=");
						//System.out.println("*-* Line is : " + line);
						if (lineSplit[0].equalsIgnoreCase(this.bdClusterName)){
							currClusterSolrInstalledNodeNameList.add(lineSplit[1].toUpperCase().trim());						
						}
					}					
				}
				br.close();
				inStrReader.close();
				in.close();							
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}	
		
		int solrInstalledNodeNum = currClusterSolrInstalledNodeNameList.size();
		System.out.println("\n*** SolrInstalledNodeNum: " + solrInstalledNodeNum);
		
		return currClusterSolrInstalledNodeNameList;
	}//end obtainCurrentClusterSolrInstalledNodeList
	
	private ArrayList<String> obtainCurrentClusterKnoxNodeList (String currClusterKnoxNodeFilePathAndName){
		ArrayList<String> currClusterKnoxNodeNameList = new ArrayList<String> ();
		if (!currClusterKnoxNodeFilePathAndName.isEmpty()){
			InputStream in = BdCluster.class.getResourceAsStream(currClusterKnoxNodeFilePathAndName);		
			InputStreamReader inStrReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inStrReader);
						
			try {				
				String line = "";				
				while ( (line = br.readLine()) != null) {
					if (!line.contains("#")){
						String [] lineSplit = line.split("=");
						//System.out.println("*-* Line is : " + line);
						if (lineSplit[0].equalsIgnoreCase(this.bdClusterName)){
							currClusterKnoxNodeNameList.add(lineSplit[1].toUpperCase().trim());						
						}
					}					
				}
				br.close();
				inStrReader.close();
				in.close();							
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}	
		
		int knoxNodeNum = currClusterKnoxNodeNameList.size();
		System.out.println("\n*** knoxNodeNum: " + knoxNodeNum);
				
		if (knoxNodeNum >= 1){
			this.currentClusterKnoxNodeName = currClusterKnoxNodeNameList.get(0);
		}				
		if (knoxNodeNum >= 2){
			this.currentClusterKnoxNode2Name = currClusterKnoxNodeNameList.get(1);
		}
		
		return currClusterKnoxNodeNameList;
	}//end obtainCurrentClusterKnoxNodeList
	
//	private void obtainCurrentClusterKnoxNodeName (String currClusterKnoxNodeFilePathAndName){
//		if (!currClusterKnoxNodeFilePathAndName.isEmpty()){
//			InputStream in = BdCluster.class.getResourceAsStream(currClusterKnoxNodeFilePathAndName);		
//			InputStreamReader inStrReader = new InputStreamReader(in);
//			BufferedReader br = new BufferedReader(inStrReader);
//			
//			ArrayList<String> currClusterKnoxNodeNameList = new ArrayList<String> ();
//			
//			try {				
//				String line = "";				
//				while ( (line = br.readLine()) != null) {					
//					String [] lineSplit = line.split("=");
//					System.out.println("*-* Line is : " + line);
//					if (lineSplit[0].equalsIgnoreCase(this.bdClusterName)){
//						currClusterKnoxNodeNameList.add(lineSplit[1].toUpperCase().trim());
//						//this.currentClusterKnoxNodeName = lineSplit[1].toUpperCase().trim();
//						//System.out.println("*-* Line is : " + line);
//						//System.out.println("*-* knox node for current Hadoop Cluster: " + this.currentClusterKnoxNodeName);
//						//break;
//					}
//				}
//				br.close();
//				inStrReader.close();
//				in.close();
//				
//				int knoxNodeNum = currClusterKnoxNodeNameList.size();
//				System.out.println("\n*** knoxNodeNum: " + knoxNodeNum);
//				
//				if (knoxNodeNum >= 1){
//					this.currentClusterKnoxNodeName = currClusterKnoxNodeNameList.get(0);
//				}				
//				if (knoxNodeNum >= 2){
//					this.currentClusterKnoxNode2Name = currClusterKnoxNodeNameList.get(1);
//				}
//				
//			} catch (IOException e) {			
//				e.printStackTrace();
//			}//end while
//		}		
//	}//end obtainCurrentClusterKnoxNodeName
	

	////////////////////////////////////////////////////////////////////////////
	private String obtainHbaseKeyTabFileName(){
		String hbaseKeyTabFileName = "";
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			//hbaseKeyTabFileName = "hbase_test_BDDev1.keytab";
			hbaseKeyTabFileName = "hbase.headless_BDDev1.keytab";			
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){			
			hbaseKeyTabFileName = "hbase.headless_BDDev3.keytab";			
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//hbaseKeyTabFileName = "hbase_test_BDInt.keytab"; //with: hbase/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM
			//hbaseKeyTabFileName = "hbase.headless_BDInt.keytab";
			hbaseKeyTabFileName = "hbase.headless_BDProd2.keytab";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			hbaseKeyTabFileName = "hbase.headless_BDProd3.keytab";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//hbaseKeyTabFileName = "hbase_test_BDProd.keytab";
			//hbaseKeyTabFileName = "hbase.headless_BDProd.keytab";
			hbaseKeyTabFileName = "hbase.headless_BDTest2.keytab";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			hbaseKeyTabFileName = "hbase.headless_BDTest3.keytab";
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//hbaseKeyTabFileName = "hbase_test_BDSbx.keytab";
			hbaseKeyTabFileName = "hbase.headless_BDSbx.keytab";
		}
		
		return hbaseKeyTabFileName;		
	}//end obtainHbaseKeyTabFileName
	
	private void obtainKrbNnAndOtherUsersPrincipals(){
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
	        	 this.krbNnPrincipal = "nn/hdpr03mn01.mayo.edu@MFAD.MFROOT.ORG";	
	        } else {
	        	this.krbNnPrincipal = "nn/hdpr03mn02.mayo.edu@MFAD.MFROOT.ORG";
	        }
			//this.krbNnPrincipal = "nn/hdpr03mn02.mayo.edu@MFAD.MFROOT.ORG";
			this.hdfsExternalPrincipal = "hdfs-mayohadoopdev1@MFAD.MFROOT.ORG";
			this.hdfsInternalPrincipal = "hdfs-mayohadoopdev1@MFAD.MFROOT.ORG";
			this.ambariQaInternalPrincipal = "ambari-qa-mayohadoopdev1@MFAD.MFROOT.ORG";
			this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";   //hive/hdpr03mn01.mayo.edu@MFAD.MFROOT.ORG ...hive-mayohadoopdev1@MFAD.MFROOT.ORG
			this.hbaseInternalPrincipal = "hbase-mayohadoopdev1@MFAD.MFROOT.ORG"; 
			this.stormInternalPrincipal = "storm-mayohadoopdev1@MFAD.MFROOT.ORG";
			this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseExternalPrincipal = "hbase-mayohadoopdev1@MFAD.MFROOT.ORG";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
	        	 this.krbNnPrincipal = "nn/hdpr05mn01.mayo.edu@MFAD.MFROOT.ORG";	
	        } else {
	        	this.krbNnPrincipal = "nn/hdpr05mn02.mayo.edu@MFAD.MFROOT.ORG";
	        }
			this.hdfsExternalPrincipal = "hdfs-mayohadoopdev3@MFAD.MFROOT.ORG";
			this.hdfsInternalPrincipal = "hdfs-mayohadoopdev3@MFAD.MFROOT.ORG";
			this.ambariQaInternalPrincipal = "ambari-qa-mayohadoopdev3@MFAD.MFROOT.ORG";
			this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";   //hive/hdpr05mn01.mayo.edu@MFAD.MFROOT.ORG ...hive-mayohadoopdev3@MFAD.MFROOT.ORG
			this.hbaseInternalPrincipal = "hbase-mayohadoopdev3@MFAD.MFROOT.ORG"; 
			this.stormInternalPrincipal = "storm-mayohadoopdev3@MFAD.MFROOT.ORG";
			this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseExternalPrincipal = "hbase-mayohadoopdev3@MFAD.MFROOT.ORG";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//if (this.nn1ServiceState.equalsIgnoreCase("active")){
	        //	 this.krbNnPrincipal = "nn/hdpr02mn01.mayo.edu@MAYOHADOOPTEST1.COM";	
	        //} else {
	        //	this.krbNnPrincipal = "nn/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM";
	        //}
			////this.krbNnPrincipal = "nn/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM";
			//this.hdfsExternalPrincipal = "hdfs/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM";
			//this.hdfsInternalPrincipal = "hdfs-MAYOHADOOPTEST1@MFAD.MFROOT.ORG";
			//this.ambariQaInternalPrincipal = "ambari-qa-MAYOHADOOPTEST1@MFAD.MFROOT.ORG";
			//this.hiveSvcPrincipalName = "hive/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM";
			//this.hbaseInternalPrincipal = "hbase@MAYOHADOOPTEST1.COM";
			//this.stormInternalPrincipal = "storm@MAYOHADOOPTEST1.COM";
			//this.hbaseMasterPrincipal = "hbase/_HOST@MAYOHADOOPTEST1.COM";
			//this.hbaseRegionServerPrincipal = "hbase/_HOST@MAYOHADOOPTEST1.COM";
			//this.hbaseExternalPrincipal = "hbase/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM";
			
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
	        	 this.krbNnPrincipal = "nn/hdpr02mn01.mayo.edu@MFAD.MFROOT.ORG";	
	        } else {
	        	this.krbNnPrincipal = "nn/hdpr02mn02.mayo.edu@MFAD.MFROOT.ORG";	       
	        }
			
			this.hdfsExternalPrincipal = "hdfs-mayohadoopprod2@MFAD.MFROOT.ORG";
			this.hdfsInternalPrincipal = "hdfs-mayohadoopprod2@MFAD.MFROOT.ORG";
			this.ambariQaInternalPrincipal = "ambari-qa-mayohadoopprod2@MFAD.MFROOT.ORG";
			this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG"; //hive/hdpr02mn01.mayo.edu@MFAD.MFROOT.ORG...hive/hdpr02mn02.mayo.edu@MFAD.MFROOT.ORG
			this.hbaseInternalPrincipal = "hbase-mayohadoopprod2@MFAD.MFROOT.ORG";
			this.stormInternalPrincipal = "storm-mayohadoopprod2@MFAD.MFROOT.ORG";
			this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseExternalPrincipal = "hbase-mayohadoopprod2@MFAD.MFROOT.ORG";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
				 this.krbNnPrincipal = "nn/hdpr07mn01.mayo.edu@MFAD.MFROOT.ORG";	
			} else {
				this.krbNnPrincipal = "nn/hdpr07mn02.mayo.edu@MFAD.MFROOT.ORG";
			}
				this.hdfsExternalPrincipal = "hdfs-mayohadoopprod3@MFAD.MFROOT.ORG";
				this.hdfsInternalPrincipal = "hdfs-mayohadoopprod3@MFAD.MFROOT.ORG";
				this.ambariQaInternalPrincipal = "ambari-qa-mayohadoopprod3@MFAD.MFROOT.ORG";
				this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";   //hive/hdpr07mn01.mayo.edu@MFAD.MFROOT.ORG ...hive-mayohadoopprod3@MFAD.MFROOT.ORG
				this.hbaseInternalPrincipal = "hbase-mayohadoopprod3@MFAD.MFROOT.ORG"; 
				this.stormInternalPrincipal = "storm-mayohadoopprod3@MFAD.MFROOT.ORG";
				this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
				this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
				this.hbaseExternalPrincipal = "hbase-mayohadoopprod3@MFAD.MFROOT.ORG";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
				this.krbNnPrincipal = "nn/hdpr01mn01.mayo.edu@MFAD.MFROOT.ORG";	
	        } else {
	        	this.krbNnPrincipal = "nn/hdpr01mn02.mayo.edu@MFAD.MFROOT.ORG";
	        }
			//this.krbNnPrincipal = "nn/hdpr01mn02.mayo.edu@MAYOHADOOPPROD1.COM";
			this.hdfsExternalPrincipal = "hdfs-MAYOHADOOPTEST2@MFAD.MFROOT.ORG";
			this.hdfsInternalPrincipal = "hdfs-MAYOHADOOPTEST2@MFAD.MFROOT.ORG";
			this.ambariQaInternalPrincipal = "ambari-qa-MAYOHADOOPTEST2@MFAD.MFROOT.ORG";
			this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";
			this.hbaseInternalPrincipal = "hbase-MAYOHADOOPTEST2@MFAD.MFROOT.ORG";
			this.stormInternalPrincipal = "storm-MAYOHADOOPTEST2@MFAD.MFROOT.ORG";
			this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseExternalPrincipal = "hbase-MAYOHADOOPTEST2@MFAD.MFROOT.ORG"; //hbase/hdpr01mn02@MAYOHADOOPPROD1.COM
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
				 this.krbNnPrincipal = "nn/hdpr06mn01.mayo.edu@MFAD.MFROOT.ORG";	
			} else {
				this.krbNnPrincipal = "nn/hdpr06mn02.mayo.edu@MFAD.MFROOT.ORG";
			}
				this.hdfsExternalPrincipal = "hdfs-mayohadooptest3@MFAD.MFROOT.ORG";
				this.hdfsInternalPrincipal = "hdfs-mayohadooptest3@MFAD.MFROOT.ORG";
				this.ambariQaInternalPrincipal = "ambari-qa-mayohadooptest3@MFAD.MFROOT.ORG";
				this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";   //hive/hdpr06mn01.mayo.edu@MFAD.MFROOT.ORG ...hive-mayohadooptest3@MFAD.MFROOT.ORG
				this.hbaseInternalPrincipal = "hbase-mayohadooptest3@MFAD.MFROOT.ORG"; 
				this.stormInternalPrincipal = "storm-mayohadooptest3@MFAD.MFROOT.ORG";
				this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
				this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
				this.hbaseExternalPrincipal = "hbase-mayohadooptest3@MFAD.MFROOT.ORG";
		}
				
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
	        	 this.krbNnPrincipal = "nn/hdpr04mn01.mayo.edu@MFAD.MFROOT.ORG";	
	        } else {
	        	this.krbNnPrincipal = "nn/hdpr04mn02.mayo.edu@MFAD.MFROOT.ORG";
	        }
			//this.krbNnPrincipal = "nn/hdpr04mn02.mayo.edu@MFAD.MFROOT.ORG";
			this.hdfsExternalPrincipal = "hdfs-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
			this.hdfsInternalPrincipal = "hdfs-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
			this.ambariQaInternalPrincipal = "ambari-qa-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
			this.hiveSvcPrincipalName = "hive/_HOST@MFAD.MFROOT.ORG";
			this.hbaseInternalPrincipal = "hbase-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
			this.stormInternalPrincipal = "storm-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
			this.hbaseMasterPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseRegionServerPrincipal = "hbase/_HOST@MFAD.MFROOT.ORG";
			this.hbaseExternalPrincipal = "hbase-MAYOHADOOPSNB1@MFAD.MFROOT.ORG";
		}	
		
	}//end obtainKrbNnAndOtherUsersPrincipals
	
	
	private void obtainBdClusterIdName(){
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			this.bdClusterIdName = "MAYOHADOOPDEV1";		
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			this.bdClusterIdName = "MAYOHADOOPDEV3";		
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//this.bdClusterKnoxIdName = "MAYOHADOOPTEST1";	
			this.bdClusterIdName = "MAYOHADOOPPROD2";	
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			this.bdClusterIdName = "MAYOHADOOPPROD3";	
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//this.bdClusterKnoxIdName = "MAYOHADOOPPROD1";	
			this.bdClusterIdName = "MAYOHADOOPTEST2";				
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			this.bdClusterIdName = "MAYOHADOOPTEST3";				
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdClusterIdName = "MAYOHADOOPSNB1";		
		}	
	}//end obtainBdClusterIdName
	
	private void obtainZookeeperQuorum (){
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			//this.zooKeeperQuorum = "hdpr03mn01.mayo.edu,hdpr03dn01.mayo.edu,hdpr03dn02.mayo.edu";	
			this.zooKeeperQuorum = "hdpr03mn01.mayo.edu,hdpr03mn02.mayo.edu,hdpr03dn01.mayo.edu";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			this.zooKeeperQuorum = "hdpr05mn01.mayo.edu,hdpr05mn02.mayo.edu,hdpr05mn03.mayo.edu";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			this.zooKeeperQuorum = "hdpr02mn01.mayo.edu,hdpr02mn02.mayo.edu,hdpr02dn01.mayo.edu";			
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			this.zooKeeperQuorum = "hdpr07mn01.mayo.edu,hdpr07mn02.mayo.edu,hdpr07mn03.mayo.edu";			
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.zooKeeperQuorum = "hdpr01mn01.mayo.edu,hdpr01mn02.mayo.edu,hdpr01dn01.mayo.edu";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			this.zooKeeperQuorum = "hdpr06mn01.mayo.edu,hdpr06mn02.mayo.edu,hdpr06mn03.mayo.edu";
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.zooKeeperQuorum = "hdpr04mn01.mayo.edu,hdpr04mn02.mayo.edu,hdpr04dn01.mayo.edu";
		}	
		
	}//end obtainZookeeperQuorum
	
	private void obtainKafkaBrokerAndPortList (){
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){			
			//this.kafkaBrokerAndPortList = "hdpr03en01.mayo.edu:6667";
			this.kafkaBrokerAndPortList = "hdpr03en02.mayo.edu:6667,hdpr01kx01.mayo.edu:6667,hdpr01kx02.mayo.edu:6667";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){			
			this.kafkaBrokerAndPortList = "hdpr05en03.mayo.edu:6667,hdpr05en04.mayo.edu:6667,hdpr05en05.mayo.edu:6667";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//this.kafkaBrokerAndPortList = "hdpr02en01.mayo.edu:6667";
			this.kafkaBrokerAndPortList = "hdpr02en01.mayo.edu:6667,hdpr01kx03.mayo.edu:6667,hdpr01kx04.mayo.edu:6667";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){			
			this.kafkaBrokerAndPortList = "hdpr07en03.mayo.edu:6667,hdpr07en04.mayo.edu:6667,hdpr07en05.mayo.edu:6667,hdpr07en06.mayo.edu:6667,hdpr07en07.mayo.edu:6667,hdpr07en08.mayo.edu:6667";
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.kafkaBrokerAndPortList = "hdpr01en01.mayo.edu:6667";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			this.kafkaBrokerAndPortList = "hdpr06en01.mayo.edu:6667,hdpr06en02.mayo.edu:6667,hdpr06en03.mayo.edu:6667";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.kafkaBrokerAndPortList = "hdpr04en01.mayo.edu:6667";
		}	
		
	}//end obtainKafkaBrokerAndPortList
	
//	private void obtainHdfsVNnIPAddressAndPort(){	//for //TDH2.1.2/11 & TDH1.3.2	only
//		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
//				|| this.bdClusterName.equalsIgnoreCase("Int")
//				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
//			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp002-nn:8020";			
//		}
//		
//		if (this.bdClusterName.equalsIgnoreCase("BDTest2") || this.bdClusterName.equalsIgnoreCase("BDPrd")
//				|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")
//				|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") || this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
//			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp001-nn:8020";		
//		}
//		
//
//		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
//				|| this.bdClusterName.equalsIgnoreCase("Dev")
//				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
//			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp003-nn:8020";		
//		}
//		
//		
//		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
//				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
//				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
//			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp004-nn:8020";		
//		}	
//		
//	}//end obtainHdfsVNnIPAddressAndPort
	
	private void obtainHdfsNnIPAddressAndPort(){		
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr03mn01.mayo.edu:8020";
			//TDH2.1: dfs.namenode.https-address == hdpr03mn01:50470 ---hdfs-site.xml
			//TDH2.1: fs.defaultFS == hdfs://hdpr03mn01:8020        ---core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr03mn02.mayo.edu:8020";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr05mn01.mayo.edu:8020";
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr05mn02.mayo.edu:8020";
			this.bdHdfs3rdNnIPAddressAndPort = "hdfs://hdpr05mn03.mayo.edu:8020";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr02mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp231-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp002-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr02mn02.mayo.edu:8020";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr07mn01.mayo.edu:8020";
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr07mn02.mayo.edu:8020";
			this.bdHdfs3rdNnIPAddressAndPort = "hdfs://hdpr07mn03.mayo.edu:8020";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr01mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp230-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp001-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr01mn02.mayo.edu:8020";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr06mn01.mayo.edu:8020";
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr06mn02.mayo.edu:8020";
			this.bdHdfs3rdNnIPAddressAndPort = "hdfs://hdpr06mn03.mayo.edu:8020";
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr04mn01.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn2
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr04mn02.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn1
		}	
		
	}//end obtainHdfsNnIPAddressAndPort
	
	
	private void obtainHiveServer2BeelineConnectionStrings(){		
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr03mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr03mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr05mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr05mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";				
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr02mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr02mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr07mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr07mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";				
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr01mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr01mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr06mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr06mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'";				
		}		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.hiveServer2BeelineConnString1 = "beeline -u 'jdbc:hive2://hdpr04mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'"; 
			this.hiveServer2BeelineConnString2 = "beeline -u 'jdbc:hive2://hdpr04mn02.mayo.edu:10001/;transportMode=http;httpPath=cliservice;principal=hive/_HOST@MFAD.MFROOT.ORG'"; 
		}	
		
	}//end obtainHiveServer2BeelineConnectionStrings
	
	
	private String obtainEntryNodeListFilePathAndName (){
		String entryNodeListFilePathAndName = "";
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			entryNodeListFilePathAndName = "dcBDDev1_EntryNode_List.txt";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			entryNodeListFilePathAndName = "dcBDDev3_EntryNode_List.txt";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//entryNodeListFilePathAndName = "dcBDProd2_EntryNode_List.txt";
			entryNodeListFilePathAndName = "dcBDProd2_Int1_EntryNode_List.txt";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){
			entryNodeListFilePathAndName = "dcBDProd3_EntryNode_List.txt";			
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//entryNodeListFilePathAndName = "dcBDTest2_EntryNode_List.txt";
			entryNodeListFilePathAndName = "dcBDTest2_Prod1_EntryNode_List.txt";			
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			entryNodeListFilePathAndName = "dcBDTest3_EntryNode_List.txt";						
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			entryNodeListFilePathAndName = "dcBDSDBX_EntryNode_List.txt";
		}
		
		return entryNodeListFilePathAndName;		
	}//end obtainEntryNodeListFilePathAndName
	
	private String obtainHdfsKeyTabFileName(){
		String hdfsKeyTabFileName = "";
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			//hdfsKeyTabFileName = "hdfs_test_BDDev1.keytab";
			hdfsKeyTabFileName = "hdfs.headless_BDDev1.keytab";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){			
			hdfsKeyTabFileName = "hdfs.headless_BDDev3.keytab";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//hdfsKeyTabFileName = "hdfs_test_BDProd2.keytab";			
			hdfsKeyTabFileName = "hdfs.headless_BDProd2.keytab";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){					
			hdfsKeyTabFileName = "hdfs.headless_BDProd3.keytab";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//hdfsKeyTabFileName = "hdfs_test_BDProd.keytab";			
			hdfsKeyTabFileName = "hdfs.headless_BDTest2.keytab";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){
			hdfsKeyTabFileName = "hdfs.headless_BDTest3.keytab";
		}	
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//hdfsKeyTabFileName = "hdfs_test_BDSbx.keytab";
			hdfsKeyTabFileName = "hdfs.headless_BDSbx.keytab";
		}
		
		return hdfsKeyTabFileName;		
	}//end obtainHdfsKeyTabFileName
	
	private String obtainKrbConfFileName(){
		String krbConfFileName = "";
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")
				//|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev1")){
			//krbConfFileName = "krb5_BDDev1.conf";
			krbConfFileName = "krb5_BDDev1_EK.conf";			
		}
		if (this.bdClusterName.equalsIgnoreCase("BDDev3")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev3")){
			krbConfFileName = "krb5__BDDev3_EK.conf";			
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				//|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//krbConfFileName = "krb5_BDProd2.conf";			
			krbConfFileName = "krb5_BDProd2_EK.conf";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDProd3")				
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd3")){					
			krbConfFileName = "krb5_BDProd3_EK.conf";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDTest2") 
				//|| this.bdClusterName.equalsIgnoreCase("BDPrd")|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//krbConfFileName = "krb5_BDProd.conf";					
			krbConfFileName = "krb5_BDTest2_EK.conf";
		}
		if (this.bdClusterName.equalsIgnoreCase("BDTest3") 
				|| this.bdClusterName.equalsIgnoreCase("MC_BDTest3")){							
			krbConfFileName = "krb5_BDTest3_EK.conf";
		}
		
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			//krbConfFileName = "krb5_BDSbx.conf";
			krbConfFileName = "krb5_BDSbx_EK.conf";
		}
		
		return krbConfFileName;		
	}//end obtainKeyConfFileName

////////////////////////////////////////////////////////////////////////
	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getBdHdfs1stNnIPAddressAndPort() {
		return bdHdfs1stNnIPAddressAndPort;
	}

	public void setBdHdfs1stNnIPAddressAndPort(String bdHdfs1stNnIPAddressAndPort) {
		this.bdHdfs1stNnIPAddressAndPort = bdHdfs1stNnIPAddressAndPort;
	}

	public String getBdHdfs2ndNnIPAddressAndPort() {
		return bdHdfs2ndNnIPAddressAndPort;
	}

	public void setBdHdfs2ndNnIPAddressAndPort(String bdHdfs2ndNnIPAddressAndPort) {
		this.bdHdfs2ndNnIPAddressAndPort = bdHdfs2ndNnIPAddressAndPort;
	}

	public ArrayList<String> getCurrentClusterEntryNodeList() {
		return currentClusterEntryNodeList;
	}

	public void setCurrentClusterEntryNodeList(
			ArrayList<String> currentClusterEntryNodeList) {
		this.currentClusterEntryNodeList = currentClusterEntryNodeList;
	}

	public FileSystem getHadoopFS() {
		return hadoopFS;
	}

	public void setHadoopFS(FileSystem hadoopFS) {
		this.hadoopFS = hadoopFS;
	}

	public String getHdfsHomeDirectory() {
		return hdfsHomeDirectory;
	}

	public void setHdfsHomeDirectory(String hdfsHomeDirectory) {
		this.hdfsHomeDirectory = hdfsHomeDirectory;
	}

	public String getZookeeperQuorum() {
		return zooKeeperQuorum;
	}

	public void setZookeeperQuorum(String zooKeeperQuorum) {
		this.zooKeeperQuorum = zooKeeperQuorum;
	}

	public String getBdHdfsActiveNnIPAddressAndPort() {
		return bdHdfsActiveNnIPAddressAndPort;
	}

	public void setBdHdfsActiveNnIPAddressAndPort(String bdHdfsActiveNnIPAddressAndPort) {
		this.bdHdfsActiveNnIPAddressAndPort = bdHdfsActiveNnIPAddressAndPort;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String getKrbConfFilePathAndName() {
		return krbConfFilePathAndName;
	}

	public void setKrbConfFilePathAndName(String krbConfFilePathAndName) {
		this.krbConfFilePathAndName = krbConfFilePathAndName;
	}

	public String getKrbNnPrincipal() {
		return krbNnPrincipal;
	}

	public void setKrbNnPrincipal(String krbNnPrincipal) {
		this.krbNnPrincipal = krbNnPrincipal;
	}

	public String getHdfsExternalPrincipal() {
		return hdfsExternalPrincipal;
	}

	public void setHdfsExternalPrincipal(String hdfsExternalPrincipal) {
		this.hdfsExternalPrincipal = hdfsExternalPrincipal;
	}

	public String getHdfsInternalPrincipal() {
		return hdfsInternalPrincipal;
	}

	public void setHdfsInternalPrincipal(String hdfsInternalPrincipal) {
		this.hdfsInternalPrincipal = hdfsInternalPrincipal;
	}

	public String getHdfsExternalKeyTabFilePathAndName() {
		return hdfsExternalKeyTabFilePathAndName;
	}

	public void setHdfsExternalKeyTabFilePathAndName(
			String hdfsExternalKeyTabFilePathAndName) {
		this.hdfsExternalKeyTabFilePathAndName = hdfsExternalKeyTabFilePathAndName;
	}

	public String getHdfsInternalKeyTabFilePathAndName() {
		return hdfsInternalKeyTabFilePathAndName;
	}

	public void setHdfsInternalKeyTabFilePathAndName(
			String hdfsInternalKeyTabFilePathAndName) {
		this.hdfsInternalKeyTabFilePathAndName = hdfsInternalKeyTabFilePathAndName;
	}

	public String getNn1ServiceState() {
		return nn1ServiceState;
	}

	public void setNn1ServiceState(String nn1ServiceState) {
		this.nn1ServiceState = nn1ServiceState;
	}

	public String getZooKeeperQuorum() {
		return zooKeeperQuorum;
	}

	public void setZooKeeperQuorum(String zooKeeperQuorum) {
		this.zooKeeperQuorum = zooKeeperQuorum;
	}

	public String getHbaseExternalPrincipal() {
		return hbaseExternalPrincipal;
	}

	public void setHbaseExternalPrincipal(String hbaseExternalPrincipal) {
		this.hbaseExternalPrincipal = hbaseExternalPrincipal;
	}

	public String getHbaseExternalKeyTabFilePathAndName() {
		return hbaseExternalKeyTabFilePathAndName;
	}

	public void setHbaseExternalKeyTabFilePathAndName(
			String hbaseExternalKeyTabFilePathAndName) {
		this.hbaseExternalKeyTabFilePathAndName = hbaseExternalKeyTabFilePathAndName;
	}

	public String getAmbariQaInternalPrincipal() {
		return ambariQaInternalPrincipal;
	}

	public void setAmbariQaInternalPrincipal(String ambariQaInternalPrincipal) {
		this.ambariQaInternalPrincipal = ambariQaInternalPrincipal;
	}

	public String getAmbariInternalKeyTabFilePathAndName() {
		return ambariInternalKeyTabFilePathAndName;
	}

	public void setAmbariInternalKeyTabFilePathAndName(
			String ambariInternalKeyTabFilePathAndName) {
		this.ambariInternalKeyTabFilePathAndName = ambariInternalKeyTabFilePathAndName;
	}

	public String getHiveSvcPrincipalName() {
		return hiveSvcPrincipalName;
	}

	public void setHiveSvcPrincipalName(String hiveSvcPrincipalName) {
		this.hiveSvcPrincipalName = hiveSvcPrincipalName;
	}

	public String getHbaseInternalPrincipal() {
		return hbaseInternalPrincipal;
	}

	public void setHbaseInternalPrincipal(String hbaseInternalPrincipal) {
		this.hbaseInternalPrincipal = hbaseInternalPrincipal;
	}

	public String getHbaseInternalKeyTabFilePathAndName() {
		return hbaseInternalKeyTabFilePathAndName;
	}

	public void setHbaseInternalKeyTabFilePathAndName(
			String hbaseInternalKeyTabFilePathAndName) {
		this.hbaseInternalKeyTabFilePathAndName = hbaseInternalKeyTabFilePathAndName;
	}

	public String getStormInternalPrincipal() {
		return stormInternalPrincipal;
	}

	public void setStormInternalPrincipal(String stormInternalPrincipal) {
		this.stormInternalPrincipal = stormInternalPrincipal;
	}

	public String getStormInternalKeyTabFilePathAndName() {
		return stormInternalKeyTabFilePathAndName;
	}

	public void setStormInternalKeyTabFilePathAndName(
			String stormInternalKeyTabFilePathAndName) {
		this.stormInternalKeyTabFilePathAndName = stormInternalKeyTabFilePathAndName;
	}

	public String getHbaseMasterPrincipal() {
		return hbaseMasterPrincipal;
	}

	public void setHbaseMasterPrincipal(String hbaseMasterPrincipal) {
		this.hbaseMasterPrincipal = hbaseMasterPrincipal;
	}

	public String getHbaseRegionServerPrincipal() {
		return hbaseRegionServerPrincipal;
	}

	public void setHbaseRegionServerPrincipal(String hbaseRegionServerPrincipal) {
		this.hbaseRegionServerPrincipal = hbaseRegionServerPrincipal;
	}

	
	public String getActiveWebHdfsHttpAddress() {
		return activeWebHdfsHttpAddress;
	}

	public void setActiveWebHdfsHttpAddress(String activeWebHdfsHttpAddress) {
		this.activeWebHdfsHttpAddress = activeWebHdfsHttpAddress;
	}

	public String getBdClusterIdName() {
		return bdClusterIdName;
	}

	public void setBdClusterIdName(String bdClusterKnoxIdName) {
		this.bdClusterIdName = bdClusterKnoxIdName;
	}

	public String getKafkaBrokerAndPortList() {
		return kafkaBrokerAndPortList;
	}

	public void setKafkaBrokerAndPortList(String kafkaBrokerAndPortList) {
		this.kafkaBrokerAndPortList = kafkaBrokerAndPortList;
	}

	public ArrayList<String> getCurrentClusterKnoxNodeList() {
		return currentClusterKnoxNodeList;
	}

	public void setCurrentClusterKnoxNodeList(
			ArrayList<String> currentClusterKnoxNodeList) {
		this.currentClusterKnoxNodeList = currentClusterKnoxNodeList;
	}

	public String getCurrentClusterKnoxNodeName() {
		return currentClusterKnoxNodeName;
	}

	public void setCurrentClusterKnoxNodeName(String currentClusterKnoxNodeName) {
		this.currentClusterKnoxNodeName = currentClusterKnoxNodeName;
	}

	public String getCurrentClusterKnoxNode2Name() {
		return currentClusterKnoxNode2Name;
	}

	public void setCurrentClusterKnoxNode2Name(String currentClusterKnoxNode2Name) {
		this.currentClusterKnoxNode2Name = currentClusterKnoxNode2Name;
	}

	public String getBdClusterActiveRMIPAddressAndPort() {
		return bdClusterActiveRMIPAddressAndPort;
	}

	public void setBdClusterActiveRMIPAddressAndPort(
			String bdClusterActiveRMIPAddressAndPort) {
		this.bdClusterActiveRMIPAddressAndPort = bdClusterActiveRMIPAddressAndPort;
	}

	public ArrayList<String> getCurrentClusterSolrInstalledNodeList() {
		return currentClusterSolrInstalledNodeList;
	}

	public void setCurrentClusterSolrInstalledNodeList(
			ArrayList<String> currentClusterSolrInstalledNodeList) {
		this.currentClusterSolrInstalledNodeList = currentClusterSolrInstalledNodeList;
	}

	public String getBdHdfs3rdNnIPAddressAndPort() {
		return bdHdfs3rdNnIPAddressAndPort;
	}

	public void setBdHdfs3rdNnIPAddressAndPort(String bdHdfs3rdNnIPAddressAndPort) {
		this.bdHdfs3rdNnIPAddressAndPort = bdHdfs3rdNnIPAddressAndPort;
	}

	public String getHiveServer2BeelineConnString1() {
		return hiveServer2BeelineConnString1;
	}

	public void setHiveServer2BeelineConnString1(String hiveServer2BeelineConnString1) {
		this.hiveServer2BeelineConnString1 = hiveServer2BeelineConnString1;
	}

	public String getHiveServer2BeelineConnString2() {
		return hiveServer2BeelineConnString2;
	}

	public void setHiveServer2BeelineConnString2(String hiveServer2BeelineConnString2) {
		this.hiveServer2BeelineConnString2 = hiveServer2BeelineConnString2;
	}
	
	
			
////////////////////////////////////////////////////////////////////////////	
	
}//end class
