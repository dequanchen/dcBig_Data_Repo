package LinuxScriptFiles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import dcModelClasses.DayClock;


public class dcTemp1 {

	public static void main(String[] args) {
		//6(2) Testing Hive JDBC
//  		totalTestScenarioNumber++;	  		  	   
//  		  	  
//  	    Thread thread = new Thread(new Runnable() {
//		    public void run() {
//		    	int totalTestCaseNumber = 0;
//			  	double successTestCaseNum = 0L;
//			  			  	
//			  	StringBuilder sb = new StringBuilder();
//		    	
//			  	try {		    		
//		  	  		Connection conn = aHiveConnFactory.getConnection();     	
//		  	  		Statement stmt = conn.createStatement();
//		  	  					
//		  			//1).drop an existing Hive table
//		  			totalTestCaseNumber++;
//		  			String dropTblSqlStr = "drop table " + tableName;
//		  			int exitValue = runAHiveQuery_NoResultSet (stmt, dropTblSqlStr);
//		  			if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Dropping Hive table - " + tableName + "\n");			
//		  			} else {
//		  				sb.append("    -*- 'Failed' - Dropping Hive table - " + tableName + "\n");
//		  			}
//		  			
//		  			//2). create a new Hive-managed table
//		  			totalTestCaseNumber++;
//		  		    String createTblSqlStr = "create table " + tableName 
//		  		      		+ "(employeeId Int, fistName String, lastName String, salary Int, gender String,  address String)"
//		  		      		+ "Row format delimited fields terminated by ',' ";
//		  		    exitValue = runAHiveQuery_NoResultSet (stmt, createTblSqlStr);  
//		  		    if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Creating Hive table - " + tableName + "\n");	
//		  		    } else {
//		  		    	sb.append("    -*- 'Failed' - Creating Hive table - " + tableName + "\n");
//		  			}   
//		  		    
//		  		    //3). load the Hive-managed table by overwriting  
//		  		    totalTestCaseNumber++;
//		  		  	String loadTblSqlStr = "load data inpath '" + hdfsHiveDefaultTestDataFilePathAndName + "' overwrite into table " + tableName ;
//		  		  	runAHiveQuery_NoResultSet (stmt, loadTblSqlStr);  
//		  		  	if (exitValue == 0){
//		  				successTestCaseNum ++;
//		  				sb.append("    *** Success - Loading Data Into Hive table - " + tableName + "\n");
//		  		  	} else {
//		  		  		sb.append("    -*- 'Failed' - Loading Data Into Hive table - " + tableName + "\n");
//		  			}    
//		  	      		    	  	
//		  		    //4).HQuery (row-counting) the above-generated Hive-managed table
//		  	  		totalTestCaseNumber++;
//		  		    String queryTblSqlStr = "select count(1) from " + tableName;  //count(*)...count(1)...*
//		  		    boolean countingQueryStatus = false;
//		  	  		ResultSet rs = runAHiveQuery_YesResultSet (stmt, queryTblSqlStr); 
//		  	  		String line = "";
//		  	  		if (rs != null){
//		  	  			while (rs.next()) {
//		  	  	  			line = rs.getString(1);
//		  	  				System.out.println(line);
//		  	  				if (line.contains("6")){ //106..106,Mary,Mac,250000,F,Virginia
//		  	  					successTestCaseNum ++;
//		  	  					countingQueryStatus = true;
//		  	  				    tableRowCount = 6;
//		  	  					break;
//		  	  				}
//		  	  	  		}
//		  	  			rs.close();
//		  	  		}  		  		
//		  	  		if (countingQueryStatus == true){
//		  	  			sb.append("    *** Success - Querying/Counting Hive table - " + tableName + "\n");
//		  	  		} else {
//		  	  			sb.append("    -*- 'Failed' - Querying/Counting Hive table - " + tableName + "\n");
//		  	  		}
//		  	  		stmt.close();
//		  	  	    conn.close();
//		  	  	    
//		  	  	    	
//		  	  		sb.append("\n    ***** totalTestCaseNumber: " + totalTestCaseNumber);
//		  	  		sb.append("\n    ***** successTestCaseNum: " + successTestCaseNum);
//		  	  		currScenarioSuccessRate = successTestCaseNum/totalTestCaseNumber;	  		 
//		  			
//		  	  	    NumberFormat df = new DecimalFormat("#0.00");
//		  			sb.append("\n    ***** Scenario Test Case Success Rate (%): " + df.format(currScenarioSuccessRate *100));	  		
//		  	  		
//		  			currScenarioDetailedTestingRecordInfo = sb.toString();		  			
//		  			sb.setLength(0);
//		  	  	} catch (SQLException e) {		
//		  			e.printStackTrace();
//		  		} catch (ClassNotFoundException e) {		
//		  			e.printStackTrace();
//		  		}//end try
//		    }
//		});
//				
//		thread.start();
//		try {
//			thread.join(20000);
//		} catch (InterruptedException e) {			
//			e.printStackTrace();
//		}
//		if (thread.isAlive()) {
//		    thread.stop();//.stop();
//		}
//		Thread.sleep(1*5*1000);	
//  	  	
//  	  	tempClock = new DayClock();				
//  		tempTime = tempClock.getCurrentDateTime();	
//  		
//  		String testRecordInfo = "";	
//  		if (currScenarioSuccessRate == 1){
//  			successTestScenarioNum++;
//  			testRecordInfo = "*** Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
//  					+ "\n         on BigData '"	+ bdClusterName + "' Cluster at the time - " + tempTime
//  			        + "\n  --(2) Querying generated Hive-Managed Table - '" + hiveDefaultFolderPath + tableName + "' has a Row Count:  '" + tableRowCount + "'\n";	 
//  		} else if (currScenarioSuccessRate == 0){
//  			testRecordInfo = "-*-*- 'Failed'  - # (" + totalTestScenarioNumber + ") Test Scenario:"
//  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
//  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
//  			        + "\n  --(2) Tested Hive-Managed Table: '" + hiveDefaultFolderPath + tableName + "'\n";	 	 
//  		} else {
//  			successTestScenarioNum += currScenarioSuccessRate;
//  			testRecordInfo = "*** " + df.format(currScenarioSuccessRate *100) + "% Test-Case Success - # (" + totalTestScenarioNumber + ") Test Scenario:"
//  					+ "\n  --(1) Externally (By Hive JDBC) Dropping, Creating, Loading (externally-written HDFS file data), and Querying a Hive-Managed Table "
//  					+ "\n         on BigData '" + bdClusterName + "' Cluster at the time - " + tempTime
//  			        + "\n  --(2) Current Scenario Test-Case Results Detail: "
//  			        + "\n    " + currScenarioDetailedTestingRecordInfo + "\n";	 
//  		}
//  		sb.setLength(0);
//  		writeDataToAFile(dcTestHive_RecFilePathAndName, testRecordInfo, true);		

	}

}
