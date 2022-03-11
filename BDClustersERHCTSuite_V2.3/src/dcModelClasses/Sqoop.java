package dcModelClasses;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 05/31/2014, 06/01/2014 
*/ 

public class Sqoop {
	private String bdClusterName = "";		
	private String sqoopBinFolderPath = "";
	private String hdfsServerFolderPath = "";
	private static String sqoopScriptFilesFolder = "../BDClustersERHCTSuite_V2.3/src/LinuxScriptFiles/Sqoop/"; //"C:\\BD\\BDInt\\Sqoop\\";

	public Sqoop(String aBdClusterName) {
		if (aBdClusterName.equalsIgnoreCase("BDProd2")){
			aBdClusterName = "BDInt";
		}
		if (aBdClusterName.equalsIgnoreCase("BDInt2")){
			aBdClusterName = "BDProd";
		}
		this.bdClusterName = aBdClusterName;
		if (this.bdClusterName.equalsIgnoreCase("BDDEV")){
			this.sqoopBinFolderPath = "/usr/lib/sqoop/bin/";
			this.hdfsServerFolderPath = "/home/m041785"; //Note: No Sqoop Script files yet
		} else {//when this.bdClusterName.equalsIgnoreCase("BDInt") or ...equalsIgnoreCase("BDProd")
			this.sqoopBinFolderPath = "/usr/lib/sqoop/bin";
			this.hdfsServerFolderPath = "/home/hdfs";
		}		
		
		File sqoopFolder = new File(sqoopScriptFilesFolder);	        
        if (!sqoopFolder.exists()){
        	sqoopFolder.mkdirs();
        	System.out.println("--*-- Created a folder for sqoop scripts: " + sqoopScriptFilesFolder);
        }
	}//end constructor
	
	public String createRunHDFS_MsgCounting_ScriptFileForPutty (String hdfsFilePathAndName, String msgIndicator, String enServerHdfsFileCountFileName) throws IOException{	
		
        String startHdfsCountingScriptFilePathAndName = sqoopScriptFilesFolder + this.bdClusterName.toUpperCase() + "_HdfsFileMsgCountingFile_RunScript.txt";			
		FileWriter outStream = new FileWriter(startHdfsCountingScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    String hdfsFileMsgCountingFullCmd = "hadoop fs -cat " + hdfsFilePathAndName  + "| grep -c \"" + msgIndicator +  "\" >" + enServerHdfsFileCountFileName;
	    
	    output.println("chown hdfs:users " + enServerHdfsFileCountFileName + ";");	
	    output.println("sudo su - hdfs <<HERE");
	    output.println("cd " + this.hdfsServerFolderPath + ";"); //example: "cd /home/hdfs;"
	    output.println("echo -ne \"'Current pwd' is: \";");
	    output.println("pwd;");		    
	    output.println(hdfsFileMsgCountingFullCmd  + ";");	    
	    //output.println("sleep 1;");	   
	    output.println("HERE");
	    
	    output.close();
	    outStream.close();
		
		return startHdfsCountingScriptFilePathAndName;
	}//createRunServerSqoopScriptFileForPutty

	public String createRunServerSqoopScriptFileForPutty (String sqoopScriptFilePathAndName) throws IOException{	
				
        String startSqoopScriptFilePathAndName = sqoopScriptFilesFolder + this.bdClusterName.toUpperCase() + "_SqoopScriptFile_RunScript.txt";			
		FileWriter outStream = new FileWriter(startSqoopScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    output.println("sudo su - hdfs <<HERE");
	    output.println("cd " + this.hdfsServerFolderPath + ";"); //example: "cd /home/hdfs;"
	    output.println("echo -ne \"'Current pwd' is: \";");
	    output.println("pwd;");	   
	    output.println("./" + sqoopScriptFilePathAndName + ";");	       
	    output.println("sleep 1;");	   
	    output.println("HERE");
	    
	    output.close();
	    outStream.close();
		
		return startSqoopScriptFilePathAndName;
	}//createRunServerSqoopScriptFileForPutty
	
	public String createBDIntBackupFromBDProd_ScriptFileForPutty (String tgtDocType, String yrMonthDay) throws IOException{	
				
        String startSqoopScriptFilePathAndName = sqoopScriptFilesFolder + this.bdClusterName.toUpperCase() + "_" + tgtDocType.replace("/", "_") + "_BDIntBackup_FromBDProd_Script.txt";			
		FileWriter outStream = new FileWriter(startSqoopScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //su hdfs -c "./home/hdfs/BDINTEN01_cnote_SqoopImportToHDFS_Script.sh"
	    //hadoop fs -rmr -skipTrash /data/amalgahistory/cnote/2009/5/1
	    //hadoop fs -cp hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  
	    //              hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27	    
	    
	    String hdfsServerAndFolderCore_BDProd = "hdfs://hdp001-nn:8020/data/amalgahistory/";
	    String hdfsServerAndFolderCore_BDInt = "hdfs://hdp002-nn:8020/data/amalgahistory/";	   
	    
	    String hdfsServerAndFolderFull_BDProd  = hdfsServerAndFolderCore_BDProd + tgtDocType;
	    String hdfsServerAndFolderFull_BDInt = hdfsServerAndFolderCore_BDInt + tgtDocType;
	    
	    if (!yrMonthDay.isEmpty()){	    	
		    hdfsServerAndFolderFull_BDProd = hdfsServerAndFolderCore_BDProd + tgtDocType + "/" + yrMonthDay;
		    hdfsServerAndFolderFull_BDInt = hdfsServerAndFolderCore_BDInt + tgtDocType + "/" + yrMonthDay;
	    }
	    
	    String hdfsBackupFullCmd_ToBDInt = "hadoop fs -cp " + hdfsServerAndFolderFull_BDProd + " " + hdfsServerAndFolderFull_BDInt;
	    //String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDInt + " " + hdfsServerAndFolderFull_BDProd;
	    
	    output.println("sudo su - hdfs <<HERE");	    
	    //output.println("echo -ne \"'Current pwd' is: \";");
	    //output.println("pwd;");	
	    //output.println("sleep 1;");	    
	    ////output.println("hadoop fs -rmr -skipTrash " + hdfsServerAndFolderFull_BDInt + ";");	   
	    output.println(hdfsBackupFullCmd_ToBDInt + ";");	
	    output.println("hadoop fs -chmod -R 550 /data/amalgahistory/" + tgtDocType + ";");	    
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return startSqoopScriptFilePathAndName;
	}//createStartDrainerTopologyScriptFileForPutty
	
	public String createBDProdBackupFromBDInt_ScriptFileForPutty (String tgtDocType, String yrMonthDay) throws IOException{	
		
        String startSqoopScriptFilePathAndName = sqoopScriptFilesFolder + this.bdClusterName.toUpperCase() + "_" + tgtDocType.replace("/", "_") + "_BDIntBackup_FromBDProd_Script.txt";			
		FileWriter outStream = new FileWriter(startSqoopScriptFilePathAndName);
	    PrintWriter output = new PrintWriter (outStream);	
	    
	    //su hdfs -c "./home/hdfs/BDINTEN01_cnote_SqoopImportToHDFS_Script.sh"
	    //hadoop fs -rmr -skipTrash /data/amalgahistory/cnote/2009/5/1
	    //hadoop fs -cp hdfs://hdp001-nn:8020/data/amalgahistory/cnote/2009/12/27  
	    //              hdfs://hdp002-nn:8020/data/amalgahistory/cnote/2009/12/27	    
	    
	    String hdfsServerAndFolderCore_BDProd = "hdfs://hdp001-nn:8020/data/amalgahistory/";
	    String hdfsServerAndFolderCore_BDInt = "hdfs://hdp002-nn:8020/data/amalgahistory/";	   
	    
	    String hdfsServerAndFolderFull_BDProd  = hdfsServerAndFolderCore_BDProd + tgtDocType;
	    String hdfsServerAndFolderFull_BDInt = hdfsServerAndFolderCore_BDInt + tgtDocType;
	    
	    if (!yrMonthDay.isEmpty()){	    	
		    hdfsServerAndFolderFull_BDProd = hdfsServerAndFolderCore_BDProd + tgtDocType + "/" + yrMonthDay;
		    hdfsServerAndFolderFull_BDInt = hdfsServerAndFolderCore_BDInt + tgtDocType + "/" + yrMonthDay;
	    }
	    
	    String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDInt + " " + hdfsServerAndFolderFull_BDProd;
	    //String hdfsBackupFullCmd_ToBDProd = "hadoop fs -cp " + hdfsServerAndFolderFull_BDInt + " " + hdfsServerAndFolderFull_BDProd;
	    
	    output.println("sudo su - hdfs <<HERE");	    
	    //output.println("echo -ne \"'Current pwd' is: \";");
	    //output.println("pwd;");	
	    //output.println("sleep 1;");	   
	    ////output.println("hadoop fs -rmr -skipTrash " + hdfsServerAndFolderFull_BDProd + ";");
	    output.println(hdfsBackupFullCmd_ToBDProd + ";");
	    output.println("hadoop fs -chmod -R 550 /data/amalgahistory/" + tgtDocType + ";");	
	    output.println("sleep 1;");
	    //output.println("exit");	
	    output.println("HERE");	
	    
	    output.close();
	    outStream.close();
		
		return startSqoopScriptFilePathAndName;
	}//createStartDrainerTopologyScriptFileForPutty
	

//////////////////////////////////////////////////////////////////
	//Common setters and getters:
	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getSqoopBinFolderPath() {
		return sqoopBinFolderPath;
	}

	public void setSqoopBinFolderPath(String sqoopBinFolderPath) {
		this.sqoopBinFolderPath = sqoopBinFolderPath;
	}

	
	public String getHdfsServerFolderPath() {
		return hdfsServerFolderPath;
	}


	public void setHdfsServerFolderPath(String hdfsServerFolderPath) {
		this.hdfsServerFolderPath = hdfsServerFolderPath;
	}


	public static String getSqoopScriptFilesFolder() {
		return sqoopScriptFilesFolder;
	}

	public static void setSqoopScriptFilesFolder(String sqoopScriptFilesFolder) {
		Sqoop.sqoopScriptFilesFolder = sqoopScriptFilesFolder;
	}
	

}//end class
