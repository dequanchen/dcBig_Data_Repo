package OS_Hardening;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.DayClock;
import dcModelClasses.HdfsFileFolder;
import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 6/6/2016
*/ 

public class D0_dcBDProd1_HDFS_ValidFolderInfo_Dynamic {
	private static String srcBdClusterName = "BDProd2"; // //BDProd2..BDProd..BDInt..BDSbx..BDDev
	private static double sizeInTBCutOff = 1.5d;
	private static String bdProd1_HdfsSourceRecordInfoFolder = "C:\\BD\\BDProd1_DataBkp\\" + srcBdClusterName  + "\\HDFS\\";

	public static void main(String[] args) throws Exception {
		DayClock initialClock = new DayClock();				
		String startTime = initialClock.getCurrentDateTime();
		System.out.println("\n*** Start Date and Time : " + startTime);
		
		long sizeInBytesCuttOff = (long) (sizeInTBCutOff*1024*1024*1024*1024);		
		ArrayList<String> hdfsValidFolder4BkpList = new  ArrayList<String>();		
		ArrayList<String> hdfsFolderList = new  ArrayList<String>();
				
        //hdfsFolderList.add("/vinod");       
        //hdfsFolderList.add("/data/amalgahistory");
        //hdfsFolderList.add("/data/concatenated");
        //hdfsFolderList.add("/data/ocean-2016-01-18");
        //hdfsFolderList.add("/data/oceanhistory");
        //String hdfsValidFolderInfoFileFullPathAndName_BDProd1 = bdProd1_HdfsSourceRecordInfoFolder + "dcBDProd1_HDFS_ValidFolderInfo_Static.txt";
        //String hdfsValidFolderListFileFullPathAndName_BDProd1 = bdProd1_HdfsSourceRecordInfoFolder + "dcBDProd1_HDFS_ValidFolderList_Static.txt";
		//BDProd1HDFS bdProd1Hdfs = new BDProd1HDFS("Static");
        //String hdfsValidFolderInfoFileFullPathAndName_BDProd1 = bdProd1Hdfs.getSrcClusterHDFSValidFolderInfoFilePathAndName();
        //String hdfsValidFolderListFileFullPathAndName_BDProd1 = bdProd1Hdfs.getSrcClusterHDFSValidFolderListFilePathAndName();
        
        //hdfsFolderList.add("/user");  
		BdCluster currBdCluster = new BdCluster(srcBdClusterName);		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();	
		
        ArrayList<String> hdfsFolderList_user = getCurrentDirLevel1SimplifiedSubDirList (currHadoopFS, "/user"); //HdfsFileFolder.getCurrentDirLevel1SimplifiedSubDirList(srcBdClusterName, "/user");
        hdfsFolderList.addAll(hdfsFolderList_user);        
        //hdfsFolderList.add("/ocean");
        //String hdfsValidFolderInfoFileFullPathAndName_BDProd1 = bdProd1_HdfsSourceRecordInfoFolder + "dcBDProd1_HDFS_ValidFolderInfo_Dynamic.txt";
        //String hdfsValidFolderListFileFullPathAndName_BDProd1 = bdProd1_HdfsSourceRecordInfoFolder + "dcBDProd1_HDFS_ValidFolderList_Dynamic.txt";
        BDProd1HDFS bdProd1Hdfs = new BDProd1HDFS("Dynamic");
        String hdfsValidFolderInfoFileFullPathAndName_BDProd1 = bdProd1Hdfs.getSrcClusterHDFSValidFolderInfoFilePathAndName();
        String hdfsValidFolderListFileFullPathAndName_BDProd1 = bdProd1Hdfs.getSrcClusterHDFSValidFolderListFilePathAndName();
        
        	       
		
		
		for (String srcHdfsFolder : hdfsFolderList){
			long currFolderSizeInBytes = obtainHdfsFolderSizeInBytes (currHadoopFS, srcHdfsFolder);
			if (currFolderSizeInBytes <= sizeInBytesCuttOff){				
				hdfsValidFolder4BkpList.add(srcHdfsFolder);
				//System.out.println("\n*** added : " + srcHdfsFolder);
			} else {
				hdfsValidFolder4BkpList = obtainValidHdfsFolderList (srcBdClusterName, srcHdfsFolder, sizeInBytesCuttOff, hdfsValidFolder4BkpList);
			}
		}
		
		hdfsValidFolder4BkpList = getSortedDuplicatesFreeArrayList (hdfsValidFolder4BkpList);
		
		
		//String hdfsValidFolderInfoFileFullPathName_BDProd1 = A0_dcBDProd1_Hive_ValidTableInfo_Static.class.getResource(hdfsValidFolderInfoFileName_BDProd1).toString().replace("file:/C", "C");
		System.out.println("\n*** hdfsValidFolderInfoFileFullPathAndName_BDProd1: " + hdfsValidFolderInfoFileFullPathAndName_BDProd1);
		System.out.println("\n*** hdfsValidFolderListFileFullPathAndName_BDProd1: " + hdfsValidFolderListFileFullPathAndName_BDProd1);
		
		
		FileWriter outStream = new FileWriter(hdfsValidFolderInfoFileFullPathAndName_BDProd1);
		PrintWriter output = new PrintWriter (outStream);	
		FileWriter outStream2 = new FileWriter(hdfsValidFolderListFileFullPathAndName_BDProd1);
		PrintWriter output2 = new PrintWriter (outStream2);
		
		System.out.println("\n*** hdfsValidFolder4BkpList.size(): " + hdfsValidFolder4BkpList.size());
		
        int folderCount = 0;
        for (String srcHdfsFolder : hdfsValidFolder4BkpList){			
			
    	    folderCount++;
    	    
			Path hdfsFileOrFolderPath = new Path(srcHdfsFolder);
			long folderNum = currHadoopFS.getContentSummary(hdfsFileOrFolderPath).getDirectoryCount();
			long fileNum = currHadoopFS.getContentSummary(hdfsFileOrFolderPath).getFileCount();
			long sizeInBytes = currHadoopFS.getContentSummary(hdfsFileOrFolderPath).getLength();
			
			String folderRecInfo = srcHdfsFolder + ", " + folderNum + " folders, " + fileNum + " files, " + sizeInBytes + " Bytes";
			//output.println(folderRecInfo);
			//output2.println(srcHdfsFolder);
			//System.out.println("\n*** (" + folderCount + ") " + srcHdfsFolder );
			System.out.println("\n*** (" + folderCount + ") " + folderRecInfo );
			//System.out.println(srcHdfsFolder);
	
		}//end outer for
        output.close();
        output2.close();
	    outStream.close();
	    outStream2.close();
	    
	    DayClock endClock = new DayClock();				
		String endTime = endClock.getCurrentDateTime();			
		String timeUsed = DayClock.calculateTimeUsed(startTime, endTime); 
		String currNotingMsg = "\n\n===========================================================";
		currNotingMsg += "\n***** Done - Obtain Data-Bkp Valid HDFS Folder Info for '" + srcBdClusterName + "' Hadoop Cluster";
		currNotingMsg += "\n   *-*-* Total Time Used: " + timeUsed; 
		currNotingMsg += "\n   ===== Start Time: " + startTime + "=====";
		currNotingMsg += "\n   =====   End Time: " + endTime + "=====\n";		
		currNotingMsg += "\n===========================================================";	    
		
		
		System.out.println(currNotingMsg);		
//		Desktop.getDesktop().open(new File(hdfsValidFolderInfoFileFullPathAndName_BDProd1));
//		Desktop.getDesktop().open(new File(hdfsValidFolderListFileFullPathAndName_BDProd1));
		
 	}//end main
	
	private static ArrayList<String> getCurrentDirLevel1SimplifiedSubDirList (FileSystem currHadoopFS, String aHdfsFolderPathName){
		ArrayList<String> allHdfsSubFolderList = new ArrayList<String> ();
		try {			
			
			FileStatus[] status = currHadoopFS.listStatus(new Path(aHdfsFolderPathName));
			
			//int subFolderCount = 0;
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFolderPathAndName = "";				
				if (status[i].isDirectory()){ //.isDir()){
					//subFolderCount++;
					currHdfsFolderPathAndName = status[i].getPath().toString();
					//System.out.println("\n*** # (" + subFolderCount + ") HDFS Sub-Folder:  ---" + currHdfsFolderPathAndName + ";");
					if (currHdfsFolderPathAndName.contains("hdfs://") && currHdfsFolderPathAndName.contains(":8020")){
						String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");						
						String simpleSubFolderName = folderNameSplit[1]; //.replace(aHdfsFolderPathName, "");						
						allHdfsSubFolderList.add(simpleSubFolderName);
						//System.out.println("\n*** # (" + subFolderCount + ") HDFS Sub-Folder:  ---" + simpleSubFolderName + ";");
						//System.out.println("  *** HDFS Sub-Folder:  " + simpleSubFolderName + ";");
					}					
				} 
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		return allHdfsSubFolderList;
	}//end getCurrentDirLevel1SimplifiedSubDirList
	
	private static ArrayList<String> obtainValidHdfsFolderList (String currBdClusterName, String currHdfsFolderName, long sizeInBytesCuttOff, ArrayList<String> hdfsValidFolderList) {
		BdCluster currBdCluster = new BdCluster(currBdClusterName);		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		long currFolderSizeInBytes = obtainHdfsFolderSizeInBytes (currHadoopFS, currHdfsFolderName);
		
		if (currFolderSizeInBytes <= sizeInBytesCuttOff) {
			hdfsValidFolderList.add(currHdfsFolderName);
			//System.out.println("\n*** added : " + currHdfsFolderName);
		} else {
			ArrayList<String> tempHdfsFolderList = HdfsFileFolder.getCurrentDirLevel1SimplifiedSubDirList(srcBdClusterName, currHdfsFolderName);
			
			for (String tempHdfsFolderName : tempHdfsFolderList) {
				long tempFolderSizeInBytes = obtainHdfsFolderSizeInBytes (currHadoopFS, tempHdfsFolderName);
				if (tempFolderSizeInBytes  <= sizeInBytesCuttOff) {
					hdfsValidFolderList.add(tempHdfsFolderName);
					//System.out.println("\n*** added : " + tempHdfsFolderName);
				} else {
					System.out.println("\n*** tempHdfsFolderName - " + tempHdfsFolderName + ": " + tempFolderSizeInBytes + "; >> " + sizeInBytesCuttOff + " Bytes!!!");									
					hdfsValidFolderList = obtainValidHdfsFolderList (currBdClusterName,  tempHdfsFolderName, sizeInBytesCuttOff, hdfsValidFolderList);
				}
			}
			
		}
		
		return hdfsValidFolderList;		
	}//end obtainValidHdfsFolderList
	
	private static long obtainHdfsFolderSizeInBytes (FileSystem currHadoopFS, String hdfsFolderName) {
		Path hdfsFileOrFolderPath = new Path(hdfsFolderName);		
		long sizeInBytes = 0L;
		try {
			sizeInBytes = currHadoopFS.getContentSummary(hdfsFileOrFolderPath).getLength();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		//System.out.println("\n*** The Size of HDFS Folder - " + hdfsFolderName + ": " + sizeInBytes + " Bytes");									
		return sizeInBytes;
	}//end obtainHdfsFolderSizeInBytes
	
	public static ArrayList<String> getSortedDuplicatesFreeArrayList (ArrayList<String> currArrayList){
		//Remove duplicates in currArrayList
	    HashSet<String> currArrayList_HashedSet = new HashSet<String>(currArrayList);
	    currArrayList.clear();
	    currArrayList.addAll(currArrayList_HashedSet);	    
	    //Sort the duplicates-removed currArrayList
		Collections.sort(currArrayList);
		return currArrayList;		
	}//end getSortedDuplicatesFreeArrayList	
	
}//end class
