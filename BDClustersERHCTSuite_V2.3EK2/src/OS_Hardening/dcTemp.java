package OS_Hardening;

import java.io.UnsupportedEncodingException;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/21-22/2016
*/ 

public class dcTemp {
	//private static String bdClusterName = "BDDev";
	
	public static void main(String[] args) throws UnsupportedEncodingException {
	    System.out.println("\n *** 1. Normal Loop i++ Results: ");
		for (int i =0; i <= 10; i++) {
		  System.out.println("i=" + i);
		}
		int i = 10;
		System.out.println("i=" + (i++));
		
		System.out.println("\n *** 2. Abnormal Loop ++j Results: ");
		for (int j =0; j <= 10; ++j) {
          System.out.println("j=" + j);
        }
		
		int j = 10;
        System.out.println("j=" + (++j));
		
	//jdbc:hive2://hdpr01kx01.mayo.edu:8442/;ssl=true;
	//  sslTrustStore=/root/gateway.jks;
	//  trustStorePassword=ranger;
	//  hive.server2.transport.mode=http;
	//  hive.server2.thrift.http.path=gateway/MAYOHADOOPDEV1/hive
//	
//	Connection connection = null;
//	Statement statement = null;
//	ResultSet resultSet = null;
//	
//	try {
//	  String user = "m041785";
//	 // String password = user + "-password";
//	  String password = "deehoo16";
//	  String gatewayHost = "hdpr01kx01.mayo.edu";
//	  int gatewayPort = 8442;
//	  //String trustStore = "/usr/lib/knox/data/security/keystores/gateway.jks";
//	  //String sslTrustStorePathAndName = "C:\\BD\\BD_UAT\\keytabs\\gateway_BDDev.jks";
//	  //String trustStore = "C:/BD/BD_UAT/keytabs/gateway_devdc.jks"; //gateway_devdc.jks...gateway_dev.jks
//	  //String trustStorePassword = "knox123";
//	  
//	  //Alternative #1:
//	  //String trustStore = "C:/BD/BD_UAT/keytabs/gateway_kx01.jks";
//	  //String trustStore = "C:\\BD\\BD_UAT\\keytabs\\gateway_kx01.jks";//C:/BD/BD_UAT/keytabs/gateway_kx01.jks
//	  //trustStore = URLEncoder.encode(trustStore, "UTF-8"); 
//	  
//	  //Alternative #2:
//	  String trustStore = "C:\\BD\\BD_UAT\\keytabs\\gateway_kx01.jks";//C:/BD/BD_UAT/keytabs/gateway_kx01.jks
//	  trustStore = URLEncoder.encode(trustStore, "UTF-8"); 
//	  
//	  String trustStorePassword = "knox123";
//	  
//	  String contextPath = "gateway/MAYOHADOOPDEV1/hive";
//	//  String connectionString = String.format( "jdbc:hive2://%s:%d/;ssl=true;"
//	//  		+ "sslTrustStore=%s;trustStorePassword=%s?"
//	//  		+ "hive.server2.transport.mode=http;"
//	//  		+ "hive.server2.thrift.http.path=/%s", 
//	//  		gatewayHost, gatewayPort, trustStore, trustStorePassword, contextPath );
//	  
//	  String connectionString = String.format( "jdbc:hive2://%s:%d/;ssl=true;"
//	      		+ "sslTrustStore=%s;trustStorePassword=%s;"
//	      		+ "transportMode=http;"
//	      		+ "httpPath=%s;user=m041785;password=deehoo16", 
//	      		gatewayHost, gatewayPort, trustStore, trustStorePassword, contextPath );
//	
//	 //String connectionString = "jdbc:hive2://hdpr03mn01.mayo.edu:10001/;transportMode=http;httpPath=cliservice;user=hive;password=''";
//	//  String connectionString = "jdbc:hive2://hdpr03mn01.mayo.edu:10001/default;transportMode=http;"
//	//  		+ "httpPath=cliservice;principal=hive/hdpr03mn02.mayo.edu@MFAD.MFROOT.ORG";//hive/hdpr03mn01.mayo.edu@MFAD.MFROOT.ORG ...hive-MAYOHADOOPDEV1@MFAD.MFROOT.ORG
//	  
//	  //connectionString = URLEncoder.encode(connectionString, "UTF-8"); 
//	  System.out.println("\n***connectionString: \n" + connectionString);
//	  
//	  // load Hive JDBC Driver
//	  Class.forName( "org.apache.hive.jdbc.HiveDriver" );
//	
//	  // configure JDBC connection
//	  connection = DriverManager.getConnection(connectionString, user, password );
//	
//	  statement = connection.createStatement();
//	
//	  // disable Hive authorization - it could be ommited if Hive authorization
//	  // was configured properly
//	  //statement.execute( "set hive.security.authorization.enabled=false" );
//	
//	  // create sample table
//	  //statement.execute( "CREATE TABLE logs(column1 string, column2 string, column3 string, column4 string, column5 string, column6 string, column7 string) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' '" );
//	
//	  // load data into Hive from file /tmp/log.txt which is placed on the local file system
//	  //statement.execute( "LOAD DATA LOCAL INPATH '/tmp/log.txt' OVERWRITE INTO TABLE logs" );
//	
//	  resultSet = statement.executeQuery( "select * from employee1" );
//	
//	  while ( resultSet.next() ) {
//	    System.out.println( resultSet.getString(1) + ", " + resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getString(4) + ", " + resultSet.getString(5) + ", " + resultSet.getString(6));
//	  }
//	} catch ( ClassNotFoundException ex ) {
//		ex.printStackTrace();
//		//Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
//	} catch ( SQLException ex ) {
//		ex.printStackTrace();
//	    //Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
//	} finally {
//	  if ( resultSet != null ) {
//	    try {
//	      resultSet.close();
//	    } catch ( SQLException ex ) {
//	    	ex.printStackTrace();	          
//	    	//Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
//	    }
//	  }
//	  if ( statement != null ) {
//	    try {
//	      statement.close();
//	    } catch ( SQLException ex ) {
//	    	ex.printStackTrace();	
//	    	//Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
//	    }
//	  }
//	  if ( connection != null ) {
//	    try {
//	      connection.close();
//	    } catch ( SQLException ex ) {
//	    	ex.printStackTrace();	
//	    	//Logger.getLogger( HiveJDBCSample.class.getName() ).log( Level.SEVERE, null, ex );
//	    }
//	  }
//	}
	}
}
