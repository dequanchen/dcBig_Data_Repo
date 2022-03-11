package dcModelClasses;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 08/07/2014 
*/ 

public class Hdfs {
	private String bdClusterName = "";		
	private String hdfsBinFolderPath = "";
	private String hdfsServerFolderPath = "";
	private static String hdfsScriptFilesFolder = "../DC_BDClusterERHCTSuite_V2EK/src/LinuxScriptFiles/Hdfs/"; //"C:\\BD\\BDProd2\\Hdfs\\";

	public Hdfs(String aBdClusterName) {		
		this.bdClusterName = aBdClusterName;
		
		if (this.bdClusterName.equalsIgnoreCase("BDDev1")){
			this.hdfsBinFolderPath = "/usr/lib/hdfs/bin/";
			this.hdfsServerFolderPath = "/home/hdfs"; //Note: No Hdfs Script files yet /home/m041785
		} else {//when this.bdClusterName.equalsIgnoreCase("BDProd2") or ...equalsIgnoreCase("BDTest2")
			this.hdfsBinFolderPath = "/usr/lib/hdfs/bin";
			this.hdfsServerFolderPath = "/home/hdfs";
		}		
		
		File hdfsFolder = new File(hdfsScriptFilesFolder);	        
        if (!hdfsFolder.exists()){
        	hdfsFolder.mkdirs();
        	System.out.println("--*-- Created a folder for hdfs scripts: " + hdfsScriptFilesFolder);
        }
	}//end constructor
	

	public String createRunServerHdfsScriptFileForPutty (String hdfsScriptFilePathAndName) throws IOException{	
				
        String startHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_HdfsScriptFile_RunScript.txt";			
		FileWriter outStream = new FileWriter(startHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    output.println("sudo su - hdfs <<HERE");
	    output.println("cd " + this.hdfsServerFolderPath + ";"); //example: "cd /home/hdfs;"
	    output.println("echo -ne \"'Current pwd' is: \";");
	    output.println("pwd;");	   
	    output.println("./" + hdfsScriptFilePathAndName + ";");	       
	    output.println("sleep 1;");	   
	    output.println("HERE");
	    
	    output.close();
	    outStream.close();
		
		return startHdfsScriptFilePathAndName;
	}//createRunServerHdfsScriptFileForPutty
	

	public String createCopyFileList_FromLocalToHdfs_ScriptFileForPutty (ArrayList<String> localLinuxFilePathAndNameList, String hdfsParentFolderName) throws IOException{	
				
        String copyFileToHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyLocalFileList_ToHDFS_Script.txt";			
		FileWriter outStream = new FileWriter(copyFileToHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    output.println("sudo su - hdfs <<HERE");

		if (!hdfsParentFolderName.endsWith("/")){
			hdfsParentFolderName += "/";
		}
		
		for (String tempLinuxFilePathAndName: localLinuxFilePathAndNameList){
			String tempLinuxFilePathAndName1 = "";
			if (tempLinuxFilePathAndName.startsWith("/")){
				tempLinuxFilePathAndName1 = tempLinuxFilePathAndName.replaceFirst("/", "");
			}
			String tempHdfsFilePathAndName = hdfsParentFolderName + tempLinuxFilePathAndName1;
			output.println("hadoop fs -rmr -skipTrash " + tempHdfsFilePathAndName + ";");
			
			String copyFileToHdfsFullCmd = "hadoop fs -copyFromLocal " + tempLinuxFilePathAndName + " " + tempHdfsFilePathAndName;
			output.println(copyFileToHdfsFullCmd + ";");
		}//end for
		
	    
	    //copyFileToHdfspFullCmd = "hadoop fs -put " + localFilePathAndName + " " + hdfsFilePathAndName;   
	    	   
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return copyFileToHdfsScriptFilePathAndName;
	}//createCopyFileList_FromLocalToHdfs_ScriptFileForPutty
	

	public String createCopyFile_FromLocalToHdfs_ScriptFileForPutty (String localFilePathAndName, String hdfsFilePathAndName) throws IOException{	
				
        String copyFileToHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyLocalFile_ToHDFS_Script.txt";			
		FileWriter outStream = new FileWriter(copyFileToHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	        
	    String copyFileToHdfsFullCmd = "hadoop fs -copyFromLocal " + localFilePathAndName + " " + hdfsFilePathAndName;
	    //copyFileToHdfspFullCmd = "hadoop fs -put " + localFilePathAndName + " " + hdfsFilePathAndName;
	    
	    output.println("echo \"tdc\" | sudo -S echo && sudo su - root;");
	    output.println("sudo su - hdfs <<HERE");	   
	    output.println(copyFileToHdfsFullCmd + ";");	   
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return copyFileToHdfsScriptFilePathAndName;
	}//createCopyFile_FromLocalToHdfs_ScriptFileForPutty
	
//	public String createRemoveFile_FromHdfs_ScriptFileForPutty (String hdfsFilePathAndName) throws IOException{	
//		
//        String removeFile_FromHdfs_ScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_RemoveFileFromHDFS_Script.txt";			
//		FileWriter outStream = new FileWriter(removeFile_FromHdfs_ScriptFilePathAndName);
//	    PrintWriter output = new PrintWriter (outStream);	
//	        
//	    String removeFileFromHdfsFullCmd = "hadoop fs -rm -skipTrash " + hdfsFilePathAndName;
//	    //hadoop fs -rm -skipTrash /data/test/dcUatDataFile_No1
//	    
//	    output.println("sudo su - hdfs <<HERE");	   
//	    output.println(removeFileFromHdfsFullCmd + ";");	 //disable for data security purpose  
//	    output.println("sleep 1;");
//	    //output.println("exit");	
//	    output.println("HERE");	
//	    
//	    output.close();
//	    outStream.close();
//		
//		return removeFile_FromHdfs_ScriptFilePathAndName;
//	}//createRemoveFile_FromHdfs_ScriptFileForPutty	
	
	

	public String createCopyFile_FromHdfsToLocal_ScriptFileForPutty (String hdfsFilePathAndName, String localFilePathAndName) throws IOException{	
				
        String copyFileFromHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_CopyHdfsFile_ToENLocal_Script.txt";			
		FileWriter outStream = new FileWriter(copyFileFromHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	        
	    String copyFileFromHdfsFullCmd = "hadoop fs -copyToLocal " + hdfsFilePathAndName + " " + localFilePathAndName;
	    //copyFileToHdfspFullCmd = "hadoop fs -get " + localFilePathAndName + " " + hdfsFilePathAndName;
	    
	    output.println("sudo su - hdfs <<HERE");	   
	    output.println(copyFileFromHdfsFullCmd + ";");	   
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return copyFileFromHdfsScriptFilePathAndName;
	}//createCopyFile_FromLocalToHdfs_ScriptFileForPutty
	
	public String createBDProd2BackupFromBDProd_ScriptFileForPutty (String tgtDocType, String yrMonthDay) throws IOException{	
				
        String startHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_" + tgtDocType + "_BDProd2Backup_FromBDProd_Script.txt";			
		FileWriter outStream = new FileWriter(startHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //su hdfs -c "./home/hdfs/BDINTEN01_cnote_HdfsImportToHDFS_Script.sh"
	    //hadoop fs -rmr -skipTrash /data/amalgahistory/cnote/2009/5/1
	    //hadoop fs -cp hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  
	    //              hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27	    
	    
	    String hdfsServerAndFolderCore_BDProd = "hdfs://hdp001-nn:8020/data/amalgahistory/";
	    String hdfsServerAndFolderCore_BDProd2 = "hdfs://hdp002-nn:8020/data/amalgahistory/";	   
	    
	    String hdfsServerAndFolderFull_BDProd  = hdfsServerAndFolderCore_BDProd + tgtDocType;
	    String hdfsServerAndFolderFull_BDProd2 = hdfsServerAndFolderCore_BDProd2 + tgtDocType;
	    
	    if (!yrMonthDay.isEmpty()){	    	
		    hdfsServerAndFolderFull_BDProd = hdfsServerAndFolderCore_BDProd + tgtDocType + "/" + yrMonthDay;
		    hdfsServerAndFolderFull_BDProd2 = hdfsServerAndFolderCore_BDProd2 + tgtDocType + "/" + yrMonthDay;
	    }
	    
	    String hdfsBackupFullCmd_ToBDProd2 = "hadoop fs -cp " + hdfsServerAndFolderFull_BDProd + " " + hdfsServerAndFolderFull_BDProd2;
	    //String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDProd2 + " " + hdfsServerAndFolderFull_BDProd;
	    
	    output.println("sudo su - hdfs <<HERE");	    
	    //output.println("echo -ne \"'Current pwd' is: \";");
	    //output.println("pwd;");	
	    //output.println("sleep 1;");	    
	    ////output.println("hadoop fs -rmr -skipTrash " + hdfsServerAndFolderFull_BDProd2 + ";");	   
	    output.println(hdfsBackupFullCmd_ToBDProd2 + ";");	   
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return startHdfsScriptFilePathAndName;
	}//createStartDrainerTopologyScriptFileForPutty
	
	public String createBDProdBackupFromBDProd2_ScriptFileForPutty (String tgtDocType, String yrMonthDay) throws IOException{	
		
        String startHdfsScriptFilePathAndName = hdfsScriptFilesFolder + this.bdClusterName.toUpperCase() + "_" + tgtDocType + "_BDProd2Backup_FromBDProd_Script.txt";			
		FileWriter outStream = new FileWriter(startHdfsScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //su hdfs -c "./home/hdfs/BDINTEN01_cnote_HdfsImportToHDFS_Script.sh"
	    //hadoop fs -rmr -skipTrash /data/amalgahistory/cnote/2009/5/1
	    //hadoop fs -cp hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  
	    //              hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27	    
	    
	    String hdfsServerAndFolderCore_BDProd = "hdfs://hdp001-nn:8020/data/amalgahistory/";
	    String hdfsServerAndFolderCore_BDProd2 = "hdfs://hdp002-nn:8020/data/amalgahistory/";	   
	    
	    String hdfsServerAndFolderFull_BDProd  = hdfsServerAndFolderCore_BDProd + tgtDocType;
	    String hdfsServerAndFolderFull_BDProd2 = hdfsServerAndFolderCore_BDProd2 + tgtDocType;
	    
	    if (!yrMonthDay.isEmpty()){	    	
		    hdfsServerAndFolderFull_BDProd = hdfsServerAndFolderCore_BDProd + tgtDocType + "/" + yrMonthDay;
		    hdfsServerAndFolderFull_BDProd2 = hdfsServerAndFolderCore_BDProd2 + tgtDocType + "/" + yrMonthDay;
	    }
	    
	    String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDProd2 + " " + hdfsServerAndFolderFull_BDProd;
	    //String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDProd2 + " " + hdfsServerAndFolderFull_BDProd;
	    
	    output.println("sudo su - hdfs <<HERE");	    
	    //output.println("echo -ne \"'Current pwd' is: \";");
	    //output.println("pwd;");	
	    //output.println("sleep 1;");	   
	    ////output.println("hadoop fs -rmr -skipTrash " + hdfsServerAndFolderFull_BDProd + ";");
	    output.println(hdfsBackupFullCmd_ToBDProd + ";");
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return startHdfsScriptFilePathAndName;
	}//createStartDrainerTopologyScriptFileForPutty
	

//////////////////////////////////////////////////////////////////
	//Common setters and getters:
	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getHdfsBinFolderPath() {
		return hdfsBinFolderPath;
	}

	public void setHdfsBinFolderPath(String hdfsBinFolderPath) {
		this.hdfsBinFolderPath = hdfsBinFolderPath;
	}

	
	public String getHdfsServerFolderPath() {
		return hdfsServerFolderPath;
	}


	public void setHdfsServerFolderPath(String hdfsServerFolderPath) {
		this.hdfsServerFolderPath = hdfsServerFolderPath;
	}


	public static String getHdfsScriptFilesFolder() {
		return hdfsScriptFilesFolder;
	}

	public static void setHdfsScriptFilesFolder(String hdfsScriptFilesFolder) {
		Hdfs.hdfsScriptFilesFolder = hdfsScriptFilesFolder;
	}
	

}//end class
