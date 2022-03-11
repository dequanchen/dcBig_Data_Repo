package dcModelClasses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/21-22/2016
*/ 

public class HiveViaKnoxConnectionFactory {
	//private String bdClusterName = "";
	private int knoxGatewayPort = 0;	
	private String hiveDBName = "";	
	private String localKnoxGatewayTrustStore = "";
	private String localKnoxGatewayTrustStorePassword = "";	
	private String username;
	private String password;
	
	private String knoxGatewayHost = "";	
	
	private String driver;	
	private String url;

	public HiveViaKnoxConnectionFactory(String currKnoxNodeFQD, int aBDClusterKnoxPort, String aHiveDBName, 
			String aLocalKnoxGatewayTrustStore, String aLocalKnoxGatewayTrustStorePassword, String bdClusterIdName,
			String aUsername, String aPassword) {
		
		//this.bdClusterName = aBdClusterName;
		
		this.knoxGatewayPort = aBDClusterKnoxPort;		
		this.hiveDBName = aHiveDBName;
		this.localKnoxGatewayTrustStore = aLocalKnoxGatewayTrustStore;		
		this.localKnoxGatewayTrustStorePassword = aLocalKnoxGatewayTrustStorePassword;
		this.username = aUsername;
		this.password = aPassword;
		
		this.driver = "org.apache.hive.jdbc.HiveDriver";
		
		//BdCluster currBdCluster = new BdCluster(this.bdClusterName);
		//String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();	
		//BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);		
		//ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();		
		//this.knoxGatewayHost = bdENCmdFactory.getServerURI();
		this.knoxGatewayHost = currKnoxNodeFQD;
		
		//String bdClusterIdName = currBdCluster.getbdClusterIdName();
		// httpPath_F5 = 'hdp/DEV3/knox/hive'
		String bdKnoxHiveContextPath = "hdp/" + bdClusterIdName.replace("MAYOHADOOP", "") + "/knox/hive";
		
		//httpPath_Knox = 'gateway/MAYOHADOOPDEV3/hive'
		//String bdKnoxHiveContextPath = "gateway/" + bdClusterIdName + "/hive";
		
		
		
		try {
			this.localKnoxGatewayTrustStore = URLEncoder.encode(this.localKnoxGatewayTrustStore, "UTF-8");
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}  
		
		
		//for comodo certificate
		this.url =  String.format( "jdbc:hive2://" + this.knoxGatewayHost + ":" + this.knoxGatewayPort + "/" + this.hiveDBName 
				+ ";ssl=true;"	      		
	      		+ "transportMode=http;"	      		
	      		+ "httpPath=" + bdKnoxHiveContextPath + ";"
	      		+ "user="+ this.username + ";password=" + this.password, 
	      		//this.knoxGatewayHost, //this.knoxGatewayPort, 
	      		this.localKnoxGatewayTrustStore, this.localKnoxGatewayTrustStorePassword 
	      		);
	      		
		
		//for self-signed certificate
		//this.url =  String.format( "jdbc:hive2://%s:%d/" + this.hiveDBName 
		//		+ ";ssl=true;"
	    // 		+ "sslTrustStore=%s;trustStorePassword=%s;"
	    //  		+ "transportMode=http;"
	    //  		+ "httpPath=%s;user="+ this.username + ";password=" + this.password, 
	    //  		this.knoxGatewayHost, this.knoxGatewayPort, 
	    //  		this.localKnoxGatewayTrustStore, this.localKnoxGatewayTrustStorePassword, 
	    // 		bdKnoxHiveContextPath);
		
		 		
	}//end constructor
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{		
		Class.forName(this.driver);
		System.out.println("\n *** hiveConnectionURL: " + this.url);
		Connection conn = DriverManager.getConnection(this.url, this.username, this.password);	
		
		return conn;		  
	}//end getConnection

}//end class
