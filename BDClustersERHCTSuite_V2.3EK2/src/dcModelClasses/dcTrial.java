package dcModelClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import dcModelClasses.ApplianceEntryNodes.BdNode;

public class dcTrial {

	public static void main(String[] args) {
		
		
//		ConfigureDBResources edwProjDevDBConfigRes = new ConfigureDBResources ();
//		String[] edwProjDevDBConnParamenters  = edwProjDevDBConfigRes.getEdwProjDevDBConParameters();
//		
//		//{"Microsoft SQL Server", "ROPEIM802Q\\PROJDEV", "TU05303", "80uijknm"};
//		
//		String rdbServerMS = edwProjDevDBConnParamenters[0];
//		String rdbServerInstance = edwProjDevDBConnParamenters[1];
//		String connUsername = edwProjDevDBConnParamenters[2];
//		String connPassword = edwProjDevDBConnParamenters[3];
		
		ConfigureDBResources edwProjDevDBConfigRes = new ConfigureDBResources ();
		DatabaseConnectionFactory dbcon = new DatabaseConnectionFactory(edwProjDevDBConfigRes.getEdwProjDevDBConParameters());
		//String rdbServerMS = dbcon.getDatabaseMS();
		String rdbServerInstance = dbcon.getDatabaseName().replace("\\", ".mayo.edu\\\\");
		String connUsername = dbcon.getUsername();
		String connPassword = dbcon.getPassword();
		
		// ROPEIM802Q\PROJDEV...> ROPEIM802Q.mayo.edu\\\\PROJDEV
			
		//System.err.println("\n--- rdbServerMS is : " + rdbServerMS);
		System.err.println("\n--- rdbServerInstance is : " + rdbServerInstance);
		System.err.println("\n--- connUsername is : " + connUsername);
		System.err.println("\n--- connPassword is : " + connPassword);
		
		//////////////////		
	}
	

	
	
	

}
