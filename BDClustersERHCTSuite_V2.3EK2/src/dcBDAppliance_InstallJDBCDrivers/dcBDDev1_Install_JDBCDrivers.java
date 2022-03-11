
package dcBDAppliance_InstallJDBCDrivers;

import java.io.File;
import java.util.ArrayList;

import dcModelClasses.HdfsUtil;
import dcModelClasses.ULServerCommandFactory;
import dcModelClasses.ApplianceEntryNodes.BdCluster;
import dcModelClasses.ApplianceEntryNodes.BdNode;


/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 12/8/2014; 9/11/2017
*/ 

public class dcBDDev1_Install_JDBCDrivers {
	private static String bdClusterName = "BDDev1";
	private static String jdbcDriverSrcFolder = "\\\\mfad.mfroot.org\\rchdept\\EDW\\Section Documents\\BigDataRelated\\MC_Appliances(Int_Prod)_Installation\\JDBC_Latest_Drivers\\";
	//private static String bdApplianceInstallFolder = "/usr/lib/sqoop/lib/"; //for version <= TDH2.1.11
	//private static String bdApplianceInstallFolder = "/usr/hdp/2.3.4.0-3485/sqoop/lib/"; //for TDH2.3.4
	private static String bdApplianceInstallFolder = "/usr/hdp/2.5.3.0-37/sqoop/lib/"; //for HDP2.5.3

	public static void main(String[] args) {		
		ArrayList<String> jdbcJarFileNameList = getJdbcDriverSrcJarFileList(jdbcDriverSrcFolder);		
		
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		ArrayList<String> bdClusterEntryNodeList = currBdCluster.getCurrentClusterEntryNodeList();
		
		int clusterENNumber = bdClusterEntryNodeList.size();
		for (int i = 0; i < clusterENNumber; i++){ //bdClusterEntryNodeList.size()..1
			String tempENName = bdClusterEntryNodeList.get(i).toUpperCase();
			BdNode aBDNode = new BdNode(tempENName, bdClusterName);
			ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
			System.out.println(" *** bdENRootCmdFactory.getServerURI(): " + bdENRootCmdFactory.getServerURI());
			
			int countJarFile = 1;
			for (String jdbcJarFileName: jdbcJarFileNameList){
				int exitVal = HdfsUtil.copyFile_FromWindowsLocal_ToEntryNodeLocal_OnBDCluster (jdbcJarFileName,jdbcDriverSrcFolder, bdApplianceInstallFolder, bdENRootCmdFactory);
				if (exitVal ==0) {
					System.err.println("*** Success - #(" + countJarFile + ") " + jdbcJarFileName + " has been installed on '"  + bdClusterName + "' entry node - " + tempENName);
				} else {
					System.out.println("*** Failed - #(" + countJarFile + ") " + jdbcJarFileName + " has been installed on '"  + bdClusterName + "' entry node - " + tempENName);
				}
				countJarFile++;
			}
		}//end for
		 

	}//end main
	
	private static ArrayList<String> getJdbcDriverSrcJarFileList (String srcFolderName){
		File jdbcDriverSrcfolder = new File(srcFolderName);
		ArrayList<String> jdbcJarFileNameList = new ArrayList<String>();
		
		File[] listOfFiles = jdbcDriverSrcfolder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File tempFile = listOfFiles[i];
			if (tempFile.isFile() && tempFile.getName().contains(".jar") ) {
				String jdbcJarFileName = tempFile.getName(); 
				jdbcJarFileNameList.add(jdbcJarFileName);
		        //System.out.println("*** jdbcJarFileName: " + jdbcJarFileName);
		    } 
		 }
		
		return jdbcJarFileNameList;		
	}//end getJdbcDriverSrcJarFileList
	

}//end class
