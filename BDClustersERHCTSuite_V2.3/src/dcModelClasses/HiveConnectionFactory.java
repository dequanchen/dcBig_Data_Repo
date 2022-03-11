package dcModelClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

public class HiveConnectionFactory {
	private String bdClusterName = "";
	private int hiveEdn = 0;
	private String hiveDBFolderName;	
	private String username;
	private String password;
	
	private String driver;	
	private String url;
	
	public HiveConnectionFactory(String aBdClusterName, int aHiveEdition, String aHiveDBFolderName, 
            String aUsername, String aPassword) {
		this.bdClusterName = aBdClusterName;
		this.hiveEdn = aHiveEdition;
		this.hiveDBFolderName = aHiveDBFolderName;
		this.username = aUsername;
		this.password = aPassword;
		
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		String bdHdfsNnIPAddressAndPort = currBdCluster.getBdHdfsNnIPAddressAndPort();
		
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
			this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName + ";user=hive;password=''";
		} else {
			this.url = "jdbc:" + bdHiveUrlCore + "/" + this.hiveDBFolderName + "," + this.username + "," + this.password ;
		}
		
		//jdbc:hive2://node1:10000/default;user=LDAP_Userid;password=LDAP_Password
		
		//String bdHdfsNnIPAddressAndPort = "hdfs://hdpr02mn01.mayo.edu:8020";
		//<name>hive.metastore.uris</name>
	    //<value>thrift://hdp002-hive:9083</value>
	    //BDInt: 39.15.255.241	hdp002-hive		
	}//end constructor
	
	public String getUrl() {
		return url;
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(this.driver);
		Connection conn = DriverManager.getConnection(url); //) ...+ ",hdfs,hdfs4dev")
		System.out.println("\n***conn url: \n" + url);
		return conn;		  
	}//end getConnection

}//end class
