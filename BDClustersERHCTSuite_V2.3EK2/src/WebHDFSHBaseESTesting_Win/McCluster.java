package WebHDFSHBaseESTesting_Win;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/06-07/2014; 1/5/2016 (Kerberos); 1/14/2016
*/ 

public class McCluster {
	private String bdClusterName = "";
	private String bdClusterIdName = "";
	
	private String bdHdfs1stNnIPAddressAndPort = "";
	private String bdHdfs2ndNnIPAddressAndPort = "";
	private String nn1ServiceState = "";
	private String bdHdfsActiveNnIPAddressAndPort = "";
	private String bdWebHDFSActiveNnIPAddressAndPort = "";
		
	private String firstHBaseMasterIPAddressAndPort = "";
	private String secondHBaseMasterIPAddressAndPort = "";
	
	private String firstKnoxNodeIPAddressAndPort = "";
	private String secondKnoxNodeIPAddressAndPort = "";
	
	private String F5BalancerIPAddressAndPort = "";
	
	
	public McCluster(String aBdClusterName) {
		this.bdClusterName = aBdClusterName;
		obtainKnoxNodesAndF5BalancerIPAddressAndPort();	
		obtainBdClusterIdName();
		
		///....		
		if (aBdClusterName.equalsIgnoreCase("BDProd2")){
			aBdClusterName = "BDInt";
		}
		if (aBdClusterName.equalsIgnoreCase("BDInt2")
				||aBdClusterName.equalsIgnoreCase("BDTest2") ){
			aBdClusterName = "BDProd";
		}
		obtainHdfsNnIPAddressAndPort();		
		//this.bdHdfs1stNnIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.toLowerCase();
		//this.bdHdfs2ndNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort.toLowerCase();
		String nn1ServerURI = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs://", "").replace(":8020", "");
		this.nn1ServiceState = getNameNode1ServiceStateByWeb (nn1ServerURI);
				//HdfsUtil.getNameNode1ServiceStateByWeb(aBdClusterName);
		if (this.nn1ServiceState.equalsIgnoreCase("active")){
			this.bdHdfsActiveNnIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort;	
			this.bdWebHDFSActiveNnIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":50070") + "/webhdfs"; 
        } else {
        	this.bdHdfsActiveNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort;
        	this.bdWebHDFSActiveNnIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":50070") + "/webhdfs"; 
        }
		
		this.firstHBaseMasterIPAddressAndPort = this.bdHdfs1stNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":8084") ;
		this.secondHBaseMasterIPAddressAndPort = this.bdHdfs2ndNnIPAddressAndPort.replace("hdfs", "http").replace(":8020", ":8084");
		
		
	}//end constructor
	
	
	
	private void obtainKnoxNodesAndF5BalancerIPAddressAndPort(){
		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			this.firstKnoxNodeIPAddressAndPort = "https://hdpr01kx01.mayo.edu:8442";			
			this.secondKnoxNodeIPAddressAndPort = "https://hdpr01kx02.mayo.edu:8442";
			this.F5BalancerIPAddressAndPort = "https://bigdataknox-dev.mayo.edu:443";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				|| this.bdClusterName.equalsIgnoreCase("Prod2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			this.firstKnoxNodeIPAddressAndPort = "https://hdpr01kx03.mayo.edu:8442";			
			this.secondKnoxNodeIPAddressAndPort = "https://hdpr01kx04.mayo.edu:8442";
			this.F5BalancerIPAddressAndPort = "https://bigdataknox.mayo.edu:443";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt2") || this.bdClusterName.equalsIgnoreCase("BDTest2")
				|| this.bdClusterName.equalsIgnoreCase("Int2") || this.bdClusterName.equalsIgnoreCase("Test2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt2") || this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.firstKnoxNodeIPAddressAndPort = "https://hdpr01kx03.mayo.edu:8442";			
			this.secondKnoxNodeIPAddressAndPort = "https://hdpr01kx04.mayo.edu:8442";
			this.F5BalancerIPAddressAndPort = "https://bigdataknox.mayo.edu:443";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.firstKnoxNodeIPAddressAndPort = "https://hdpr01kx01.mayo.edu:8442";			
			this.secondKnoxNodeIPAddressAndPort = "https://hdpr01kx02.mayo.edu:8442";
			this.F5BalancerIPAddressAndPort = "https://bigdataknox-dev.mayo.edu:443";
		}
		
	}//end obtainKnoxNodesAndF5BalancerIPAddressAndPort
	
	private void obtainBdClusterIdName(){
		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			this.bdClusterIdName = "MAYOHADOOPDEV1";		
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				|| this.bdClusterName.equalsIgnoreCase("Prod2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			//this.bdClusterIdName = "MAYOHADOOPTEST1";	
			this.bdClusterIdName = "MAYOHADOOPPROD2";	
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt2") || this.bdClusterName.equalsIgnoreCase("BDTest2")
				|| this.bdClusterName.equalsIgnoreCase("Int2") || this.bdClusterName.equalsIgnoreCase("Test2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt2") || this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			//this.bdClusterIdName = "MAYOHADOOPPROD1";	
			this.bdClusterIdName = "MAYOHADOOPTEST2";				
		}
		
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdClusterIdName = "MAYOHADOOPSNB1";		
		}	
	}//end obtainBdClusterIdName
	
	
	
	private void obtainHdfsNnIPAddressAndPort(){
		if (this.bdClusterName.equalsIgnoreCase("BDDev")
				|| this.bdClusterName.equalsIgnoreCase("Dev")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDDev")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr03mn01.mayo.edu:8020";
			//TDH2.1: dfs.namenode.https-address == hdpr03mn01:50470 ---hdfs-site.xml
			//TDH2.1: fs.defaultFS == hdfs://hdpr03mn01:8020        ---core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr03mn02.mayo.edu:8020";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDProd2")
				|| this.bdClusterName.equalsIgnoreCase("Prod2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDProd2")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr02mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp231-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp002-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr02mn02.mayo.edu:8020";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDInt2") || this.bdClusterName.equalsIgnoreCase("BDTest2")
				|| this.bdClusterName.equalsIgnoreCase("Int2") || this.bdClusterName.equalsIgnoreCase("Test2")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDInt2") || this.bdClusterName.equalsIgnoreCase("MC_BDTest2")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr01mn01.mayo.edu:8020";
			//TDH1.3: dfs.https.address == hdp230-2:50470 --hdfs-site.xml
			//TDH1.3: fs.default.name == hdfs://hdp001-nn:8020 --core-site.xml
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr01mn02.mayo.edu:8020";
		}
		
		if (this.bdClusterName.equalsIgnoreCase("BDSbx")|| this.bdClusterName.equalsIgnoreCase("BDSdbx")
				||this.bdClusterName.equalsIgnoreCase("Sbx")|| this.bdClusterName.equalsIgnoreCase("Sdbx")
				|| this.bdClusterName.equalsIgnoreCase("MC_BDSbx") || this.bdClusterName.equalsIgnoreCase("MC_BDSdbx")){
			this.bdHdfs1stNnIPAddressAndPort = "hdfs://hdpr04mn01.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn2
			this.bdHdfs2ndNnIPAddressAndPort = "hdfs://hdpr04mn02.mayo.edu:8020"; //dfs.namenode.rpc-address.MAYOHADOOPSNB1.nn1
		}		
	}//end obtainHdfsNnIPAddressAndPort
	

	private static String getNameNode1ServiceStateByWeb (String nn1ServerURI){	
		String nn1ServiceState = "standby";
		try {
			
			String nn1StatusQueryURI = "http://" + nn1ServerURI + ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
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
	
///////////////////////////////////////////
	
	public String getBdClusterName() {
		return bdClusterName;
	}


	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}


	public String getBdClusterIdName() {
		return bdClusterIdName;
	}


	public void setBdClusterIdName(
			String bdClusterIdName) {
		this.bdClusterIdName = bdClusterIdName;
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


	public String getNn1ServiceState() {
		return nn1ServiceState;
	}


	public void setNn1ServiceState(String nn1ServiceState) {
		this.nn1ServiceState = nn1ServiceState;
	}


	public String getBdHdfsActiveNnIPAddressAndPort() {
		return bdHdfsActiveNnIPAddressAndPort;
	}


	public void setBdHdfsActiveNnIPAddressAndPort(
			String bdHdfsActiveNnIPAddressAndPort) {
		this.bdHdfsActiveNnIPAddressAndPort = bdHdfsActiveNnIPAddressAndPort;
	}


	public String getBdWebHDFSActiveNnIPAddressAndPort() {
		return bdWebHDFSActiveNnIPAddressAndPort;
	}


	public void setBdWebHDFSActiveNnIPAddressAndPort(
			String bdWebHDFSActiveNnIPAddressAndPort) {
		this.bdWebHDFSActiveNnIPAddressAndPort = bdWebHDFSActiveNnIPAddressAndPort;
	}


	public String getFirstHBaseMasterIPAddressAndPort() {
		return firstHBaseMasterIPAddressAndPort;
	}


	public void setFirstHBaseMasterIPAddressAndPort(
			String firstHBaseMasterIPAddressAndPort) {
		this.firstHBaseMasterIPAddressAndPort = firstHBaseMasterIPAddressAndPort;
	}


	public String getSecondHBaseMasterIPAddressAndPort() {
		return secondHBaseMasterIPAddressAndPort;
	}


	public void setSecondHBaseMasterIPAddressAndPort(
			String secondHBaseMasterIPAddressAndPort) {
		this.secondHBaseMasterIPAddressAndPort = secondHBaseMasterIPAddressAndPort;
	}


	public String getFirstKnoxNodeIPAddressAndPort() {
		return firstKnoxNodeIPAddressAndPort;
	}


	public void setFirstKnoxNodeIPAddressAndPort(
			String firstKnoxNodeIPAddressAndPort) {
		this.firstKnoxNodeIPAddressAndPort = firstKnoxNodeIPAddressAndPort;
	}


	public String getSecondKnoxNodeIPAddressAndPort() {
		return secondKnoxNodeIPAddressAndPort;
	}


	public void setSecondKnoxNodeIPAddressAndPort(
			String secondKnoxNodeIPAddressAndPort) {
		this.secondKnoxNodeIPAddressAndPort = secondKnoxNodeIPAddressAndPort;
	}


	public String getF5BalancerIPAddressAndPort() {
		return F5BalancerIPAddressAndPort;
	}


	public void setF5BalancerIPAddressAndPort(String f5BalancerIPAddressAndPort) {
		F5BalancerIPAddressAndPort = f5BalancerIPAddressAndPort;
	}
	


}//end class
