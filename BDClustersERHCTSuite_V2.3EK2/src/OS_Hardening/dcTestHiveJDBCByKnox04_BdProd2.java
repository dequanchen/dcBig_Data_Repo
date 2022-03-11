package OS_Hardening;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dcModelClasses.HiveViaKnoxConnectionFactory_Old;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 7/5/2016
*/ 

public class dcTestHiveJDBCByKnox04_BdProd2 {

	public static void main(String[] args) {
		String bdClusterName = "BDProd2";
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		//String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName();
		String currClusterKnoxNode2Name = currBdCluster.getCurrentClusterKnoxNode2Name();
		System.out.println(" *** currClusterKnoxNode2Name: " + currClusterKnoxNode2Name);
		
		BdNode aBDNode = new BdNode(currClusterKnoxNode2Name, bdClusterName);
		String bdClusterKnoxIdName = currBdCluster.getBdClusterIdName();
		System.out.println(" *** bdClusterKnoxIdName: " + bdClusterKnoxIdName);
		
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		String currKnoxNodeFQDN = bdENCmdFactory.getServerURI();
		System.out.println(" *** bdENCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);

		int knoxGateWayPortNum = 8442;
		String hiveDBName = "default";
		
		//String trustStore = "C:\\BD\\BD_UAT\\truststores\\gateway_kx01.jks";//C:/BD/BD_UAT/keytabs/gateway_kx01.jks
		String trustStore = "C:/BD/BD_UAT/truststores/gateway_kx04.jks";
		String trustStorePassword = "knox123";
		String user = "m041785";
	    String password = "deehoo16";
	      
		HiveViaKnoxConnectionFactory_Old aHiveViaKnoxConnFactory = new HiveViaKnoxConnectionFactory_Old(currKnoxNodeFQDN, knoxGateWayPortNum,
				hiveDBName, trustStore,trustStorePassword,bdClusterKnoxIdName, user, password);
				
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