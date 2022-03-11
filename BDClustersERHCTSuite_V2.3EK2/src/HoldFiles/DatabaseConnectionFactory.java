package HoldFiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 04/09/2014; 5/7/2014; 2/14/2017 
 */

public class DatabaseConnectionFactory {

	private String databaseMS;
	private String databaseName;	
	private String username;
	private String password;
	
	private String url;
	private String driver;	
	
	//Constructor#1
	public DatabaseConnectionFactory(String aDatabaseMS, String aDatabaseName, 
			                               String aUsername, String aPassword){		
		this.databaseMS = aDatabaseMS.toUpperCase();
		this.databaseName = aDatabaseName.toUpperCase();
		this.username  = aUsername; 
		this.password = aPassword;	
	
		if (this.databaseMS.equalsIgnoreCase("Microsoft SQL Server")){			
						
			if (this.databaseName.equals("ROPEIM802Q\\PROJDEV")){
				this.url = "jdbc:sqlserver://;servername=ROPEIM802Q\\PROJDEV;databaseName=EDT_BigData;user=" + 
				        this.username + ";password=" + this.password + ";";				
			}
						
			this.driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}	
		
		//... for other Database Management System
	}//end constructor#1
		

	//Constructor#2
	public DatabaseConnectionFactory(String[] dbConParamenters){		
		this.databaseMS = dbConParamenters[0].toUpperCase();
		this.databaseName = dbConParamenters[1].toUpperCase();
		this.username  = dbConParamenters[2]; 
		this.password = dbConParamenters[3];
		
		if (this.databaseMS.equalsIgnoreCase("Microsoft SQL Server")){				
						
			if (this.databaseName.equals("ROPEIM802Q\\PROJDEV")){
				this.url = "jdbc:sqlserver://;servername=ROPEIM802Q\\PROJDEV;databaseName=EDT_BigData;user=" + 
				        this.username + ";password=" + this.password + ";";
			}
						
			this.driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}
		
	}//end constructor#2
	
	
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(this.driver);
		if (this.driver.equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerDriver")){
			Connection conn = DriverManager.getConnection(this.url);
			return conn;
		} else {
			Connection conn = DriverManager.getConnection(this.url, 
					this.username, this.password );
			return conn;
		}			    
	}//end getConnection

	
////*************************************************************************************	
	//The followings are getters and setters for all the attributes
	public String getDatabaseMS() {
		return databaseMS;
	}

	public void setDatabaseMS(String databaseMS) {
		this.databaseMS = databaseMS;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}//end class
