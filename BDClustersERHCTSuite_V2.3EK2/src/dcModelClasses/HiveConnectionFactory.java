package dcModelClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 11/24-25/2014;
*/ 

public class HiveConnectionFactory {
	private String bdClusterName = "";
	private int hiveEdn = 0;
	private String hiveDBFolderName;
	private String hiveSvcPrincipalName = "";	
	private String username;
	private String password;
	
	private String driver;	
	private String url;
	
	//private String krbConfFilePathAndName = "";
	
	public HiveConnectionFactory(String aBdClusterName, int aHiveEdition, String aHiveDBFolderName, 
            String aUsername, String aPassword) {
		this.bdClusterName = aBdClusterName;
		
		this.hiveEdn = aHiveEdition;
		this.hiveDBFolderName = aHiveDBFolderName;
		this.username = aUsername;
		this.password = aPassword;
		
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		String bdHdfsNnIPAddressAndPort = currBdCluster.getBdHdfsActiveNnIPAddressAndPort(); //.getBdHdfsNnIPAddressAndPort();
		this.hiveSvcPrincipalName = currBdCluster.getHiveSvcPrincipalName();		
		//this.krbConfFilePathAndName = currBdCluster.getKrbConfFilePathAndName();
		System.out.println("*** The hiveSvcPrincipalName is: " + this.hiveSvcPrincipalName  + "\n");
		
		String bdHiveUrlCore = "";		
		if (this.hiveEdn == 1){
			bdHiveUrlCore = bdHdfsNnIPAddressAndPort.replace("hdfs", "hive").replace("8020", "10000");
			this.driver = "org.apache.hadoop.hive.jdbc.HiveDriver";  
		}
		if (this.hiveEdn == 2){
			bdHiveUrlCore = bdHdfsNnIPAddressAndPort.replace("hdfs", "hive2").replace("8020", "10000");
			this.driver = "org.apache.hive.jdbc.HiveDriver";  
		}		
		
			
		if (this.password.isEmpty()){
			//this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName;
			this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName + ";principal="  + this.hiveSvcPrincipalName + ";user=hive;password=''";
		} else {
			this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName + ";principal="  + this.hiveSvcPrincipalName + "," + this.username + "," + this.password ;
		}
		
//		For Hive JDBC Through Knox:
//		jdbc:hive2://hdpr01kx01.mayo.edu:8442/;ssl=true;sslTrustStore=/root/gateway.jks;
//		    trustStorePassword=ranger;transportMode=http;httpPath=gateway/MAYOHADOOPDEV1/hive		
//		//String connectionString = String.format( "jdbc:hive2://%s:%d/;ssl=true;sslTrustStore=%s;
//		  trustStorePassword=%s?hive.server2.transport.mode=http;hive.server2.thrift.http.path=/%s", 
//		  gatewayHost, gatewayPort, trustStore, trustStorePassword, contextPath );
//		String bdClusterKnoxIdName = currBdCluster.getBdClusterKnoxIdName();
//		String sslTrustStorePathAndName = "C:\\BD\\BD_ERHCT\\keytabs\\gateway_BDDev1.jks";
//		if (this.password.isEmpty()){
//			//this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName;
//			this.url = "jdbc:hive2://hdpr01kx01.mayo.edu:8442/;ssl=true;sslTrustStore=" + sslTrustStorePathAndName + ";"
//					+ "trustStorePassword=knox123;hive.server2.transport.mode=http;"
//					+ "hive.server2.thrift.http.path=gateway/MAYOHADOOPDEV1/hive;user=hive;password=''";
//		} else {
//			this.url = "jdbc:hive2://hdpr01kx01.mayo.edu:8442/;ssl=true;sslTrustStore=" + sslTrustStorePathAndName + ";"
//					+ "trustStorePassword=knox123;hive.server2.transport.mode=http;"
//					+ "hive.server2.thrift.http.path=gateway/MAYOHADOOPDEV1/hive;" + this.username + "," + this.password ;
//		}
		
		
		//jdbc:hive2://node1:10000/default;user=LDAP_Userid;password=LDAP_Password
		
		//String bdHdfsNnIPAddressAndPort = "hdfs://hdpr02mn01.mayo.edu:8020";
		//<name>hive.metastore.uris</name>
	    //<value>thrift://hdp002-hive:9083</value>
	    //BDProd2: 39.15.255.241	hdp002-hive		
	}//end constructor
	
	public String getUrl() {
		return url;
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException{
		//System.setProperty("java.security.krb5.conf", this.krbConfFilePathAndName);
		Class.forName(this.driver);
		Connection conn = DriverManager.getConnection(url); //) ...+ ",hdfs,hdfs4dev")
		System.out.println("\n***conn url: \n" + url);
		return conn;		  
	}//end getConnection
	
	
}//end class
