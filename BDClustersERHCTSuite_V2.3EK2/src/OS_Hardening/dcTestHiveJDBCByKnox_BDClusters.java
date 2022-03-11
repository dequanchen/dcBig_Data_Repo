package OS_Hardening;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dcModelClasses.HiveViaKnoxOrF5ConnectionFactory;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 04/21-22/2016; 6/6/2017
*/ 

public class dcTestHiveJDBCByKnox_BDClusters {
	//https://stackoverflow.com/questions/25157273/connect-from-java-to-hive-using-jdbc

	public static void main(String[] args) {
		String bdClusterName = "BDProd2"; //BDSdbx...BDDev1...BDDev3...BDTest2...BDTest3...BDProd2...BDProd3
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		String currClusterKnoxNodeName = currBdCluster.getCurrentClusterKnoxNodeName().replace("_DEV3", "");	//_DEV3
		BdNode aBDNode = new BdNode(currClusterKnoxNodeName, bdClusterName);
		String bdClusterIdName = currBdCluster.getBdClusterIdName();
		
		System.out.println(" *** currClusterKnoxNodeName: " + currClusterKnoxNodeName);
		System.out.println(" *** bdClusterIdName: " + bdClusterIdName);
		
		ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
		String currKnoxNodeFQDN = bdENCmdFactory.getServerURI();
		if (bdClusterName.equalsIgnoreCase("BDTest3")){
			currKnoxNodeFQDN = currKnoxNodeFQDN.replace("hdpr06", "hdpr05");
		}
		System.out.println(" *** bdENCmdFactory.getServerURI() or currKnoxNodeFQDN: " + currKnoxNodeFQDN);

		int knoxGateWayPortNum = 8442;
		String hiveDBName = "default"; //bdts...default
		
		//String trustStore = "C:\\BD\\BD_UAT\\truststores\\gateway_kx01.jks";//C:/BD/BD_UAT/keytabs/gateway_kx01.jks
		//String trustStore = "C:/BD/BD_ERHCT/truststores/gateway_kx01.jks"; //"C:/BD/BD_UAT/truststores/gateway_kx01.jks";
		//String trustStorePassword = "knox123";
		String user = "wa00336";
	    String password = "bnhgui89";
	    String hiveDatabaseName = "default";
		String hiveTableName = "employee_hivejdbc_knox1";  //employee1...ecg1...
	      
		currKnoxNodeFQDN = "bigdataknox.mayo.edu"; //"hdpr05en01.mayo.edu";//"bigdata.mayo.edu";//"bigdataknox-dev.mayo.edu" ... bigdataknox.mayo.edu;
		knoxGateWayPortNum = 443 ;//8442; //443;//443
		// httpPath_F5 = 'hdp/DEV3/knox/hive'
		//String bdKnoxOrF5HiveContextPath = "hdp/" + bdClusterIdName.replace("MAYOHADOOP", "") + "/knox/hive";
		
		//httpPath_Knox = 'gateway/MAYOHADOOPDEV3/hive'
		String bdKnoxOrF5HiveContextPath = "gateway/" + bdClusterIdName + "/hive";
		
		HiveViaKnoxOrF5ConnectionFactory aHiveViaKnoxConnFactory = new HiveViaKnoxOrF5ConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
				hiveDBName, "","",bdClusterIdName, user, password, bdKnoxOrF5HiveContextPath);
		
		try {
			Connection conn = aHiveViaKnoxConnFactory.getConnection();
			Statement stmt = conn.createStatement();
			
			
			
			String hiveQuery1 = "select * from " + hiveDatabaseName + "." + hiveTableName;			
			System.out.println("\n *1* Waiting for Hive JDBC query:  " + hiveQuery1 + "..." );
			ResultSet resultSet = stmt.executeQuery( hiveQuery1 );
			//ResultSet resultSet = stmt.executeQuery( "select * from employee1" );

			while ( resultSet.next() ) {
				System.out.println( resultSet.getString(1) + ", " + resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getString(4) + ", " + resultSet.getString(5) + ", " + resultSet.getString(6));
				//System.out.println( resultSet.getInt(0));
			}
			resultSet.close();
			
			String hiveQuery2 = "select count(1) from " + hiveDatabaseName + "." + hiveTableName;			
			System.out.println("\n *2* Waiting for Hive JDBC query: " + hiveQuery2 + "..." );
			ResultSet resultSet2 = stmt.executeQuery( hiveQuery2 ); //count(*)..count(1)
			//ResultSet resultSet2 = stmt.executeQuery( "select count(*) from employee1" );
			//ResultSet resultSet2 = stmt.executeQuery( "select count(1) from employee1" );
			
			while ( resultSet2.next() ) {
				System.out.println( resultSet2.getString(1)); //getString(1)...getLong(1)..getInt(1)
			}
			resultSet2.close();
//			
//			//3. Listing Databases
//			String hiveQuery3 = "show databases";
//			System.out.println("\n *3* Waiting for Hive JDBC query:  " + hiveQuery3 + "..." );
//			ResultSet resultSet3 = stmt.executeQuery( hiveQuery3 );
//			
//			int hiveDbCount = 0;
//			while (resultSet3.next()) {
//				hiveDbCount++;
//				System.out.println("(" + hiveDbCount + ") " + resultSet3.getString(1));	
//		        //System.out.println(resultSet3.getString(1));
//		    }
//			resultSet3.close();
//
//
//			//4. Hive table creating ddl - e.g., bdts..default..webhcat_db..webhcat_db2
//			hiveDatabaseName = "webhcat_db2"; //bdts..default..webhcat_db..webhcat_db2
//			String hiveQuery4 = "describe database " + hiveDatabaseName;
//			System.out.println("\n *4* Waiting for Hive JDBC query:  " + hiveQuery4 + "..." );
//			ResultSet resultSet4 = stmt.executeQuery( hiveQuery4 );		
//			
//			while (resultSet4.next()) {
//				System.out.println(resultSet4.getString(1) + "\t" + resultSet4.getString(2) + "\t" + resultSet4.getString(3) + "\t" + resultSet4.getString(4) + "\t" + resultSet4.getString(5) + "\t" + resultSet4.getString(6) );
//		    }
//			resultSet4.close();
//			
//			//5. describe table - e.g., bdts.knox_audit
//			String hiveQuery5 = "describe  " + hiveDatabaseName + "." + hiveTableName;	
//			System.out.println("\n *5* Waiting for Hive JDBC query:  " + hiveQuery5 + "..." );
//			ResultSet resultSet5 = stmt.executeQuery( hiveQuery5 );		
//			
//			while (resultSet5.next()) {
//				System.out.println(resultSet5.getString(1) + "\t" + resultSet5.getString(2) + "\t" + resultSet5.getString(3));
//		    }
//			resultSet5.close();
//			
//			//6. Hive table creating ddl - e.g., bdts.knox_audit
//			String hiveQuery6 = "show create table " + hiveDatabaseName + "." + hiveTableName;	//SHOW CREATE TABLE...show create table 
//			System.out.println("\n *6* Waiting for Hive JDBC query:  " + hiveQuery6 + "..." );
//			ResultSet resultSet6 = stmt.executeQuery( hiveQuery6 );		
//			
//			while (resultSet6.next()) {
//				System.out.println(resultSet6.getString(1));
//		    }
//			resultSet6.close();			
//			
//			//*7. List tables of the connected Hive Database
//			String hiveQuery7 = "show tables";
//			System.out.println("\n *7* Waiting for Hive JDBC query:  " + hiveQuery7 + "..." );
//			ResultSet resultSet7 = stmt.executeQuery( hiveQuery7 );
//			
//			int hiveTableCount = 0;
//			while (resultSet7.next()) {
//				hiveTableCount++;
//				System.out.println("(" + hiveTableCount + ") " + resultSet7.getString(1));	
//		        //System.out.println(resultSet7.getString(1));
//		    }
//			resultSet7.close();
//			
//			//8. List tables of any Hive Database
//			hiveDatabaseName = "default"; //bdts..default..webhcat_db..webhcat_db2
//			HiveViaKnoxConnectionFactory bHiveViaKnoxConnFactory = new HiveViaKnoxConnectionFactory(currKnoxNodeFQDN, knoxGateWayPortNum,
//					hiveDatabaseName, "","",bdClusterIdName, user, password);
//			Connection conn_b = bHiveViaKnoxConnFactory.getConnection();
//			Statement stmt_b = conn_b.createStatement();
//			
//			String hiveQuery8 = "show tables";
//			System.out.println("\n *8* Waiting for Hive JDBC query:  " + hiveQuery8 + "..." );
//			ResultSet resultSet8 = stmt_b.executeQuery( hiveQuery8 );
//			
//			int hiveTableCount = 0;
//			while (resultSet8.next()) {
//				hiveTableCount++;
//				System.out.println("(" + hiveTableCount + ") " + resultSet8.getString(1));	
//		        //System.out.println(resultSet8.getString(1));
//		    }
//			resultSet8.close();
//			stmt_b.close();
//			conn_b.close();
//
//			//9. Hive table external or hive-managed status
//			//hiveTableName = "ecg1";  //employee1...ecg1
//			String hiveQuery9 = "show create table " + hiveDatabaseName + "." + hiveTableName;	//SHOW CREATE TABLE...show create table 
//			System.out.println("\n *9* Waiting for Hive JDBC query:  " + hiveQuery9 + "..." );
//			ResultSet resultSet9 = stmt.executeQuery( hiveQuery9 );		
//			
//			boolean externalTableStatus = false;
//			while (resultSet9.next()) {
//				String temp = resultSet9.getString(1);
//				if (temp.contains("CREATE") 
//						&& temp.contains("TABLE")
//						&& temp.contains("EXTERNAL")
//						){
//					externalTableStatus = true;
//					break;
//				}
//				//System.out.println(resultSet9.getString(1));
//		    }			
//			System.out.println("\n *** externalTableStatus: " + externalTableStatus);
//			resultSet9.close();
//			
//			//10. Hive table existing status - method 2
//			hiveTableName = "employee1";  //employee1...ecg1
//			DatabaseMetaData dbm = conn.getMetaData();			
//			ResultSet resultSet10 = dbm.getTables(null, null, hiveTableName, null);		
//			
//			boolean hiveTableStatus2 = false;
//			while (resultSet10.next()) {
//				String temp = resultSet10.getString("TABLE_CAT") 
//					       + ", " + resultSet10.getString("TABLE_SCHEM")
//					       + ", " + resultSet10.getString("TABLE_NAME")
//					       + ", " + resultSet10.getString("TABLE_TYPE")
//					       + ", " + resultSet10.getString("REMARKS");
//				System.out.println("\n *** temp: " + temp);
//				if (temp.contains(hiveTableName) 
//						&& temp.contains("TABLE")) {
//					hiveTableStatus2=true;
//				}
//		    }			
//			System.out.println("\n *** hiveTableStatus2: " + hiveTableStatus2);
//			resultSet10.close();
//			
//			//11. Hive table existing status - method 1
//			String hiveQuery11 = "show tables in " + hiveDatabaseName + " like '" + hiveTableName + "'";
//			System.out.println("\n *11* Waiting for Hive JDBC query:  " + hiveQuery11 + "..." );
//			ResultSet resultSet11 = stmt.executeQuery( hiveQuery11 );
//			
//			boolean hiveTableStatus1 = false;
//			while (resultSet11.next()) {
//				String temp =resultSet11.getString(1);
//				if (temp.equalsIgnoreCase(hiveTableName)){
//					hiveTableStatus1= true;
//					break;
//				}
//				//System.out.println(resultSet11.getString(1));
//		    }
//			System.out.println("\n *** hiveTableStatus1: " + hiveTableStatus1);
//			resultSet11.close();
//			
//			//12. Hive table exporting
//			String hiveTableExportedIntoHDFSPath = "/user/wa00336/hive_table_exporting/" + hiveTableName;
//			Path srcHdfsHiveTableExportingPath = new Path (hiveTableExportedIntoHDFSPath);			
//			FileSystem srcHadoopFS  = currBdCluster.getHadoopFS();
//			if (srcHadoopFS.exists(srcHdfsHiveTableExportingPath)){
//				srcHadoopFS.delete(srcHdfsHiveTableExportingPath, true);
//				System.out.println("\n *12a* deleted the HDFS folder:  " + hiveTableExportedIntoHDFSPath + " for Hive table exporting ..." );
//			}
//			
//			String hiveQuery12 = "export table " + hiveDatabaseName + "." + hiveTableName + " to '" + hiveTableExportedIntoHDFSPath + "'";			
//			stmt.execute( hiveQuery12 );
//			System.out.println("\n *12b* Executed the Hive Table Exporting JDBC query:  " + hiveQuery12 + " !!!" );			
			
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
		} 
//		catch (IOException e) {			
//			e.printStackTrace();
//		}
	  	
	}//end main
}//end class