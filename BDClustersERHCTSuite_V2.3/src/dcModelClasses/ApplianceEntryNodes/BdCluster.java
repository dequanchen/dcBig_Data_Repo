package dcModelClasses.ApplianceEntryNodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import dcModelClasses.HdfsUtil;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-07/2014 
*/ 

public class BdCluster {
	private String bdClusterName = "";
	private String bdHdfsActiveNnIPAddressAndPort = "";
	private String bdHdfsNnIPAddressAndPort = "";
	private String bdHdfs2ndNnIPAddressAndPort = "";
	private String nn1ServiceState = "";
	private FileSystem hadoopFS = null;
	private String hdfsHomeDirectory = "";
	private String zooKeeperQuorum = "";
	private Configuration configuration = new Configuration();
	private ArrayList<String> currentClusterEntryNodeList = new  ArrayList<String>(); 
	

	public BdCluster(String aBdClusterName) {
		if (aBdClusterName.equalsIgnoreCase("BDProd2")){
			aBdClusterName = "BDInt";
		}
		if (aBdClusterName.equalsIgnoreCase("BDInt2")){
			aBdClusterName = "BDProd";
		}	
		this.bdClusterName = aBdClusterName;
		obtainHdfsVNnIPAddressAndPort();
		obtainHdfsNnIPAddressAndPort();
		this.bdHdfsNnIPAddressAndPort = this.bdHdfsNnIPAddressAndPort.toLowerCase();
		this.bdHdfs2ndNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort.toLowerCase();
		obtainZookeeperQuorum();
		
		String user = "hdfs";
		//ex:String nnIpAddress = "hdfs://hdpr02mn01.mayo.edu:8020"; //8020..50300 //hdfs://hdp002-nn:8020..hdfs://hdpr02mn01.mayo.edu:8020..10.128.216.167
		
		
		
		String hdfsURL = "";
//		if (bdClusterName.equalsIgnoreCase("BDDev")
//				|| bdClusterName.equalsIgnoreCase("Dev")
//				|| bdClusterName.equalsIgnoreCase("MC_BDDev")){
//			hdfsURL = this.bdHdfsNnIPAddressAndPort + "?user=" + user;
//		} else {
//			
////			this.nn1ServiceState = HdfsUtil.getNameNode1ServiceState(aBdClusterName); //hdfs haadmin -getServiceState nn1 - standby //hdfs haadmin -getServiceState nn2 - active
////			
////			if (this.nn1ServiceState.equalsIgnoreCase("active")){
////				//hdfsURL = this.bdHdfsNnIPAddressAndPort + "?user=" + user;
////				System.out.println("--- ative name node is Name Node #1");
////			} else {
////				//hdfsURL = this.bdHdfs2ndNnIPAddressAndPort + "?user=" + user;
////				System.out.println("--- ative name node is Name Node #2");
////			}
//			
//			hdfsURL = this.bdHdfsActiveNnIPAddressAndPort + "?user=" + user;
//		}
		
		hdfsURL = this.bdHdfsActiveNnIPAddressAndPort + "?user=" + user;	//testing virtual IP for HDFS Name Node HA		
		if (!this.bdClusterName.equalsIgnoreCase("HWSdbx") && !this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
			this.nn1ServiceState = HdfsUtil.getNameNode1ServiceStateByWeb(aBdClusterName);
			if (this.nn1ServiceState.equalsIgnoreCase("active")){
				hdfsURL = this.bdHdfsNnIPAddressAndPort + "?user=" + user;	
	        } else {
	    	    hdfsURL = this.bdHdfs2ndNnIPAddressAndPort + "?user=" + user;
	        }		
		}		
		//hdfsURL = this.bdHdfsNnIPAddressAndPort + "?user=" + user;  //Hard code using active name node when virtual IP fails to work, otherwise comment this out
		System.out.println("\n--- The hdfsURL for the Hadoop cluster is: " + hdfsURL );
				
		//String hdfsURL = this.bdHdfsNnIPAddressAndPort + "?user=" + user;	
		//String hdfsURL = this.bdHdfs2ndNnIPAddressAndPort + "?user=" + user;
        URI uriToHdfs = URI.create(hdfsURL);
        
        System.setProperty("hadoop.home.dir", "C:\\winutil\\");//required for Hadoop 2.6.0 and above cluster - running Java code on Windows 
        
        //configuration.set("fs.default.name", hdfsURL); //fs.default.name..fs.defaultFS //For TDH1.3.2
        this.configuration.set("fs.defaultFS", hdfsURL); //fs.default.name..fs.defaultFS    //For TDH2.1
        //configuration.set("mapred.job.tracker", this.bdHdfsNnIPAddressAndPort.replace("8020", "50300"));  //"hdfs://hdpr02mn01.mayo.edu:50300"
        this.configuration.set("dfs.support.append", "true");
        this.configuration.set("dfs.client.use.datanode.hostname", "true");
        
        //this.configuration.set("dfs.datanode.use.datanode.hostname", "true");
        //configuration.set("dfs.ha.automatic-failover.enabled", "true");
        //configuration.set("dfs.client.failover.proxy.provider.MAYOHADOOPTEST1", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        //this.configuration.set("dfs.replication", "1");
        if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
        	this.configuration.set("dfs.replication", "1");
        }
      //The following two lines are required for using windows command line to run maven-built jar(s)
        this.configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); //for HDFS
        this.configuration.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName()); //For both HDFS & WebHDFS
        //configuration.set("dfs.namenode.https-address", "hdpr03mn01.mayo.edu:50470");
        
//        Configuration conf = new Configuration();
//        String hadoopConfPath = "C:\\BD\\BD_UAT\\BDDev\\";  //"C:\\BD\\BD_UAT\\BDDev\\" .."C:\\BD\\BD_UAT\\BDInt\\"
//        conf.addResource(new Path(hadoopConfPath + "core-site.xml"));
//        conf.addResource(new Path(hadoopConfPath + "hdfs-site.xml"));
//        conf.addResource(new Path(hadoopConfPath + "mapred-site.xml"));
    
        try {
			//this.hadoopFS = FileSystem.get(configuration); //This is good for Kerberized Hadoop Clusters
			this.hadoopFS = FileSystem.get(uriToHdfs, configuration, user); //This is good for Non-Kerberized Hadoop Clusters
        	//this.hadoopFS = FileSystem.get(uriToHdfs, configuration);//DistributedFileSystem
			System.out.println("\n*** Connected to HDFS system at: " + this.hadoopFS.getUri().toString());
			this.hdfsHomeDirectory = this.hadoopFS.getHomeDirectory().toString();
			System.out.println("--- Home directory of '" + user + "' is:" + this.hdfsHomeDirectory);
		} catch (IOException | InterruptedException e1) {
		//} catch (IOException e1) {		
			e1.printStackTrace();
		}
		
		String entryNodeListFilePathAndName = this.obtainEntryNodeListFilePathAndName();
		if (!entryNodeListFilePathAndName.isEmpty()){
			InputStream in = BdCluster.class.getResourceAsStream(entryNodeListFilePathAndName);		
			InputStreamReader inStrReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inStrReader);
					
			try {				
				String line = "";
				//int addCount = 0;
				while ( (line = br.readLine()) != null) {
					this.currentClusterEntryNodeList.add(line);
					//addCount++;
					//System.out.println("*-* (" + addCount + ") added a MsgType: " + line);									            
				}
				br.close();
				inStrReader.close();
				in.close();			
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}		
		
	}//end constructor
	
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

	////////////////////////////////////////////////////////////////////////////	
	private void obtainZookeeperQuorum (){
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt")
				|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt")){
			this.zooKeeperQuorum = "hdpr02mn01.mayo.edu,hdpr02mn02.mayo.edu,hdpr02dn01.mayo.edu";			
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd") || this.bdClusterName.equalsIgnoreCase("BDPrd")
				|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") || this.bdClusterName.equalsIgnoreCase("MC_BDProd")){
			this.zooKeeperQuorum = "hdpr01mn01.mayo.edu,hdpr01mn02.mayo.edu,hdpr01dn01.mayo.edu";			
		}
		

		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			//this.zooKeeperQuorum = "hdpr03mn01.mayo.edu,hdpr03dn01.mayo.edu,hdpr03dn02.mayo.edu";	
			this.zooKeeperQuorum = "hdpr03mn01.mayo.edu,hdpr03mn02.mayo.edu,hdpr03dn01.mayo.edu";	
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.zooKeeperQuorum = "hdpr04mn01.mayo.edu,hdpr04mn02.mayo.edu,hdpr04dn01.mayo.edu";
		}	
		
		if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
			this.zooKeeperQuorum = "sandbox.hortonworks.com";
		}	
		
	}//end obtainZookeeperQuorum
	
	private void obtainHdfsVNnIPAddressAndPort(){
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt")
				|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt")){
			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp002-nn:8020";			
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd") || this.bdClusterName.equalsIgnoreCase("BDPrd")
				|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") || this.bdClusterName.equalsIgnoreCase("MC_BDProd")){
			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp001-nn:8020";		
		}
		

		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp003-nn:8020";		
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://hdp004-nn:8020";		
		}
		
		if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
			this.bdHdfsActiveNnIPAddressAndPort = "hdfs://sandbox.hortonworks.com:8020";
		}	
		
	}//end obtainHdfsVNnIPAddressAndPort
	
	private void obtainHdfsNnIPAddressAndPort(){
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt")
				|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt")){
			this.bdHdfsNnIPAddressAndPort = "hdfs://hdpr02mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp231-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp002-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr02mn02.mayo.edu:8020";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd") || this.bdClusterName.equalsIgnoreCase("BDPrd")
				|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") || this.bdClusterName.equalsIgnoreCase("MC_BDProd")){
			this.bdHdfsNnIPAddressAndPort = "hdfs://hdpr01mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp230-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp001-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr01mn02.mayo.edu:8020";
		}
		

		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			this.bdHdfsNnIPAddressAndPort = "hdfs://hdpr03mn01.mayo.edu:8020";
			//TDH2.1: dfs.namenode.https-address == hdpr03mn01:50470 ---hdfs-site.xml
			//TDH2.1: fs.defaultFS == hdfs://hdpr03mn01:8020        ---core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr03mn01.mayo.edu:8020";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdHdfsNnIPAddressAndPort = "hdfs://hdpr04mn01.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn2
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr04mn02.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn1
		}	
		
		if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
			this.bdHdfsNnIPAddressAndPort = "hdfs://sandbox.hortonworks.com:8020";
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://sandbox.hortonworks.com:8020";
		}	
		
	}//end obtainHdfsNnIPAddressAndPort
	
	private String obtainEntryNodeListFilePathAndName (){
		String entryNodeListFilePathAndName = "";
		if (this.bdClusterName.equalsIgnoreCase("BDInt")
				|| this.bdClusterName.equalsIgnoreCase("Int")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt")){
			entryNodeListFilePathAndName = "dcBDInt_EntryNode_List.txt";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd") || this.bdClusterName.equalsIgnoreCase("BDPrd")
				|| this.bdClusterName.equalsIgnoreCase("Prd") || this.bdClusterName.equalsIgnoreCase("Prod")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDPrd") || this.bdClusterName.equalsIgnoreCase("MC_BDProd")){
			entryNodeListFilePathAndName = "dcBDProd_EntryNode_List.txt";
		}
		

		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			entryNodeListFilePathAndName = "dcBDDev_EntryNode_List.txt";
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			entryNodeListFilePathAndName = "dcBDSDBX_EntryNode_List.txt";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("HWSdbx") || this.bdClusterName.equalsIgnoreCase("HW_Sdbx") ){
			entryNodeListFilePathAndName = "dcHWSDBX_EntryNode_List.txt";
		}	
		
		return entryNodeListFilePathAndName;		
	}//end obtainEntryNodeListFilePathAndName

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getBdHdfsNnIPAddressAndPort() {
		return bdHdfsNnIPAddressAndPort;
	}

	public void setBdHdfsNnIPAddressAndPort(String bdHdfsNnIPAddressAndPort) {
		this.bdHdfsNnIPAddressAndPort = bdHdfsNnIPAddressAndPort;
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
	
	

////////////////////////////////////////////////////////////////////////////	
	
}//end class
