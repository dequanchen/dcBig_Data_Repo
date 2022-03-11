
package dcModelClasses;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dcModelClasses.ApplianceEntryNodes.BdNode;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 03/22/2016 
*/ 

public class LoginUser {
	private String bdClusterName = "";	 
	private String loginUserServerTestingFolderPath = "";
	private String loginUserName = "";
	//private String loginUserPassWord = "";
	//private String rootUserName = "";
	//private String rootPassWord = ""; 
	private static String loginUserScriptFilesFolder = "../DC_BDClusterERHCTSuite_V2EK/src/LinuxScriptFiles/LoginUser/"; //"C:\\BD\\BDProd2\\Hdfs\\";
	//private static String loginUserScriptFilesFolder = "C:\\BD\\BD_ERHCT\\PuttyScriptFiles\\LoginUser\\";
	
	public LoginUser(String aBDClusterName) {
		this.bdClusterName = aBDClusterName;
		
		BdNode currClusterAbstractedBDNode = new BdNode("AllNodes", bdClusterName);
		ULServerCommandFactory bdENCmdFactory = currClusterAbstractedBDNode.getBdENCmdFactory();
		//ULServerCommandFactory bdENRootCmdFactory = currClusterAbstractedBDNode.getBdENRootCmdFactory();
		//System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
		
		String loginUserName = bdENCmdFactory.getUsername();
		//String loginUsrPw = bdENCmdFactory.getPassword();
		//String rootUsrName = bdENRootCmdFactory.getUsername();
		//String rootPw = bdENRootCmdFactory.getPassword();
		
		this.loginUserName = loginUserName;
		System.out.println(" --1--loginUserName: " + loginUserName);
				
		this.loginUserServerTestingFolderPath = "/data/home/" + this.loginUserName + "/test/";		
		System.out.println(" --2--loginUserServerTestingFolderPath: " + loginUserServerTestingFolderPath);
		
		File loginUserScriptsWinLocalFolder = new File(loginUserScriptFilesFolder);	        
        if (!loginUserScriptsWinLocalFolder.exists()){
        	loginUserScriptsWinLocalFolder.mkdirs();
        	System.out.println("--*-- Created a folder for Login User scripts: " + loginUserScriptFilesFolder);
        }
	}//end constructor
	
	public String createRunServerScriptFileForPutty (String loginUserScriptFilePathAndName) throws IOException{		
        String startScriptFilePathAndName = loginUserScriptFilesFolder + this.bdClusterName.toUpperCase() + "_RunScriptFile.txt";			
		FileWriter outStream = new FileWriter(startScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //output.println("sudo su - hdfs <<HERE");
	    //output.println("sudo su - " + this.loginUserName + "<<HERE");
	    //output.println("<<HERE");
	    output.println("cd " + this.loginUserServerTestingFolderPath + ";"); //example: "cd /data/home/m041785;"
	    output.println("echo -ne \"'Current pwd' is: \";");
	    output.println("pwd;");	   
	    output.println("./" + loginUserScriptFilePathAndName + ";");	       
	    output.println("sleep 1;");	   //1..30..60..seconds
	    //output.println("HERE");
	    
	    output.close();
	    outStream.close();
		
	    System.out.println(" --3--startScriptFilePathAndName: " + startScriptFilePathAndName);
		return startScriptFilePathAndName;
	}//createRunServerScriptFileForPutty
	
	public String createRunServerScriptFileForPutty (String loginUserScriptFilePathAndName, String enServerScriptFileDirectory) throws IOException{		
		this.loginUserServerTestingFolderPath = enServerScriptFileDirectory;
		String startScriptFilePathAndName = loginUserScriptFilesFolder + this.bdClusterName.toUpperCase() + "_RunScriptFile.txt";			
		FileWriter outStream = new FileWriter(startScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //output.println("sudo su - hdfs <<HERE");
	    //output.println("sudo su - " + this.loginUserName + "<<HERE");
	    //output.println("<<HERE");
	    output.println("cd " + this.loginUserServerTestingFolderPath + ";"); //example: "cd /data/home/m041785;"
	    output.println("echo -ne \"'Current pwd' is: \";");
	    output.println("pwd;");	   
	    output.println("./" + loginUserScriptFilePathAndName + ";");	       
	    output.println("sleep 1;");	   //1..30..60..seconds
	    //output.println("HERE");
	    
	    output.close();
	    outStream.close();
		
	    System.out.println(" --3--startScriptFilePathAndName: " + startScriptFilePathAndName);
		return startScriptFilePathAndName;
	}//createRunServerScriptFileForPutty

		
//	public String createCopyFileList_FromLocalToHdfs_ScriptFileForPutty (ArrayList<String> localLinuxFilePathAndNameList, 
//			String hdfsParentFolderName) throws IOException{				
//        String copyFileToHdfsScriptFilePathAndName = loginUserScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyLocalFileList_ToHDFS_Script.txt";			
//		FileWriter outStream = new FileWriter(copyFileToHdfsScriptFilePathAndName);
//	    PrintWriter output = new PrintWriter (outStream);	
//	    
//	    //output.println("sudo su - hdfs <<HERE");
//	    output.println("sudo su - " + this.loginUserName + "<<HERE");
//
//		if (!hdfsParentFolderName.endsWith("/")){
//			hdfsParentFolderName += "/";
//		}
//		
//		for (String tempLinuxFilePathAndName: localLinuxFilePathAndNameList){
//			String tempLinuxFilePathAndName1 = "";
//			if (tempLinuxFilePathAndName.startsWith("/")){
//				tempLinuxFilePathAndName1 = tempLinuxFilePathAndName.replaceFirst("/", "");
//			}
//			String tempHdfsFilePathAndName = hdfsParentFolderName + tempLinuxFilePathAndName1;
//			output.println("hadoop fs -rmr -skipTrash " + tempHdfsFilePathAndName + ";");
//			
//			String copyFileToHdfsFullCmd = "hadoop fs -copyFromLocal " + tempLinuxFilePathAndName + " " + tempHdfsFilePathAndName;
//			output.println(copyFileToHdfsFullCmd + ";");
//		}//end for
//		
//	    
//	    //copyFileToHdfspFullCmd = "hadoop fs -put " + localFilePathAndName + " " + hdfsFilePathAndName;   
//	    	   
//	    output.println("sleep 1;");
//	    //output.println("exit");	
//	    output.println("HERE");	
//	    
//	    output.close();
//	    outStream.close();
//		
//		return copyFileToHdfsScriptFilePathAndName;
//	}//createCopyFileList_FromLocalToHdfs_ScriptFileForPutty
//	
//
//	public String createCopyFile_FromLocalToHdfs_ScriptFileForPutty (String localFilePathAndName, String hdfsFilePathAndName) throws IOException{	
//				
//        String copyFileToHdfsScriptFilePathAndName = loginUserScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyLocalFile_ToHDFS_Script.txt";			
//		FileWriter outStream = new FileWriter(copyFileToHdfsScriptFilePathAndName);
//	    PrintWriter output = new PrintWriter (outStream);	
//	        
//	    String copyFileToHdfsFullCmd = "hadoop fs -copyFromLocal " + localFilePathAndName + " " + hdfsFilePathAndName;
//	    //copyFileToHdfspFullCmd = "hadoop fs -put " + localFilePathAndName + " " + hdfsFilePathAndName;
//	    
//	    //output.println("echo \"tdc\" | sudo -S echo && sudo su - root;");
//	  //output.println("sudo su - hdfs <<HERE");
//	    output.println("sudo su - " + this.loginUserName + "<<HERE");	   
//	    output.println(copyFileToHdfsFullCmd + ";");	   
//	    output.println("sleep 1;");
//	    //output.println("exit");	
//	    output.println("HERE");	
//	    
//	    output.close();
//	    outStream.close();
//		
//		return copyFileToHdfsScriptFilePathAndName;
//	}//createCopyFile_FromLocalToHdfs_ScriptFileForPutty
//	
//
//	public String createCopyFile_FromHdfsToLocal_ScriptFileForPutty (String hdfsFilePathAndName, String localFilePathAndName) throws IOException{	
//				
//        String copyFileFromHdfsScriptFilePathAndName = loginUserScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyHdfsFile_ToENLocal_Script.txt";			
//		FileWriter outStream = new FileWriter(copyFileFromHdfsScriptFilePathAndName);
//	    PrintWriter output = new PrintWriter (outStream);	
//	        
//	    String copyFileFromHdfsFullCmd = "hadoop fs -copyToLocal " + hdfsFilePathAndName + " " + localFilePathAndName;
//	    //copyFileToHdfspFullCmd = "hadoop fs -get " + localFilePathAndName + " " + hdfsFilePathAndName;
//	    
//	  //output.println("sudo su - hdfs <<HERE");
//	    output.println("sudo su - " + this.loginUserName + "<<HERE");	   
//	    output.println(copyFileFromHdfsFullCmd + ";");	   
//	    output.println("sleep 1;");
//	    //output.println("exit");	
//	    output.println("HERE");	
//	    
//	    output.close();
//	    outStream.close();
//		
//		return copyFileFromHdfsScriptFilePathAndName;
//	}//createCopyFile_FromLocalToHdfs_ScriptFileForPutty
	
		

//////////////////////////////////////////////////////////////////
	//Common setters and getters:
	
	
	public static void setTestUserScriptFilesFolder(String loginUserScriptFilesFolder) {
		LoginUser.loginUserScriptFilesFolder = loginUserScriptFilesFolder;
	}

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getLoginUserServerTestingFolderPath() {
		return loginUserServerTestingFolderPath;
	}

	public void setLoginUserServerTestingFolderPath(
			String loginUserServerTestingFolderPath) {
		this.loginUserServerTestingFolderPath = loginUserServerTestingFolderPath;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public static String getLoginUserScriptFilesFolder() {
		return loginUserScriptFilesFolder;
	}

	public static void setLoginUserScriptFilesFolder(
			String loginUserScriptFilesFolder) {
		LoginUser.loginUserScriptFilesFolder = loginUserScriptFilesFolder;
	}
	
}//end class
