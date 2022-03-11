package OS_Hardening;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dcModelClasses.HiveViaKnoxConnectionFactory;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/21-22/2016
*/ 

public class dcTestHiveJDBCByKnox_BDDev1 {

	public static void main(String[] args) {
		String bdClusterName = "BDDev1";
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNode2Name();	//.getCurrentClusterKnoxNodeName();
		
		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
		String bdClusterIdName = currBdCluster.getBdClusterIdName();
		//bdClusterIdName += "_bkp";
		System.out.println(" *** bdClusterIdName: " + bdClusterIdName);
		
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		String currKnoxNodeFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);

		int knoxGateWayPortNum = 8442;
		String hiveDBName = "default";
		
		//String trustStore = "C:\\BD\\BD_UAT\\truststores\\gateway_kx01.jks";//C:/BD/BD_UAT/keytabs/gateway_kx01.jks
		//String trustStore = "C:/BD/BD_ERHCT/truststores/gateway_kx01.jks"; //"C:/BD/BD_UAT/truststores/gateway_kx01.jks";
		//String trustStorePassword = "knox123";
		String user = "wa00336";
	    String password = "bnhjui89";
	      
		HiveViaKnoxConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
				hiveDBName, "","",bdClusterIdName, user, password);
				
		try {
			Connection conn = aHiveViaKnoxConnFactory.getConnection();
			Statement stmt = conn.createStatement();
			
			System.out.println("\n *1* Waiting for Hive JDBC query: select * from employee1 ..." );
			ResultSet resultSet = stmt.executeQuery( "select * from employee1" );
			//ResultSet resultSet = stmt.executeQuery( "select count(*) from employee1" );

			while ( resultSet.next() ) {
				System.out.println( resultSet.getString(1) + ", " + resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getString(4) + ", " + resultSet.getString(5) + ", " + resultSet.getString(6));
				//System.out.println( resultSet.getInt(0));
			}
			resultSet.close();
			
			System.out.println("\n *2* Waiting for Hive JDBC query: select count(1) from employee1 ..." );
			ResultSet resultSet2 = stmt.executeQuery( "select count(1) from employee1" ); //count(*)..count(1)
			while ( resultSet2.next() ) {
				System.out.println( resultSet2.getString(1)); //getString(1)...getLong(1)..getInt(1)
			}
			resultSet2.close();
			
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		}
	  	
	}//end main

}//end class