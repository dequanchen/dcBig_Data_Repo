package dcModelClasses;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 10/13/2014; 07/29/2015; 7/31/2015; 9/10-25/2015
 */

public class HdfsFileFolder {
	private String bdClusterName = ""; //BDInt..BDProd..BDDev..BDSbx
	private String hdfsFolderPathAndName = "";
	private String fileNameSelector = "";
	
	
	private ArrayList<String> allHdfsFileList = new ArrayList<String> ();
	private ArrayList<String> selectedAllHdfsFileList = new ArrayList<String> ();
	
	public HdfsFileFolder() {
		
	}
		
	public HdfsFileFolder(String aBDclusterName, String aHdfsFolderPathAndName, String aFileNameSelector) {
		this.bdClusterName = aBDclusterName;
		this.hdfsFolderPathAndName = aHdfsFolderPathAndName;
		this.fileNameSelector = aFileNameSelector;
		
		if (isHdfsFileOrFolderExisting(this.bdClusterName, this.hdfsFolderPathAndName )){			
			this.allHdfsFileList = this.getHdfsFilesInAFolder (this.hdfsFolderPathAndName, "");		
			this.selectedAllHdfsFileList = this.getHdfsFilesInAFolder (this.hdfsFolderPathAndName, this.fileNameSelector);		
		}		
	}//end alternative1 constructor
	
	
	public HdfsFileFolder(String aBDclusterName, String aHdfsFolderPathAndName, String aPrimaryFileNameSelector, String aSecondaryFileNameSelector, String aFileNameExcluder) {
		this.bdClusterName = aBDclusterName;
		this.hdfsFolderPathAndName = aHdfsFolderPathAndName;
		this.fileNameSelector = aPrimaryFileNameSelector;
		
		if (isHdfsFileOrFolderExisting(this.bdClusterName, this.hdfsFolderPathAndName )){			
			this.allHdfsFileList = this.getHdfsFilesInAFolder (this.hdfsFolderPathAndName, "");	
			ArrayList<String> primarySelectedHdfsFileList = this.getHdfsFilesInAFolder (this.hdfsFolderPathAndName, this.fileNameSelector);
			
			//get the secondarySelectedHdfsFileList from primarySelectedHdfsFileList
			ArrayList<String> secondarySelectedHdfsFileList = new ArrayList<String>();
			if (aSecondaryFileNameSelector.isEmpty()){
				secondarySelectedHdfsFileList = primarySelectedHdfsFileList;
			} else {
				for (String tempFile: primarySelectedHdfsFileList){
					if (tempFile.contains(aSecondaryFileNameSelector)){
						secondarySelectedHdfsFileList.add(tempFile);
					}
				}
			}
			
			//get this.selectedAllHdfsFileList from secondarySelectedHdfsFileList
			if (aFileNameExcluder.isEmpty()){
				this.selectedAllHdfsFileList = secondarySelectedHdfsFileList;
			} else {
				for (String tempFile: secondarySelectedHdfsFileList){
					if (!tempFile.contains(aFileNameExcluder)){
						this.selectedAllHdfsFileList.add(tempFile);
					}
				}
			}							
		}		
	}//end alternative2 constructor



////////////////////////////////////////////////////////////////////	
	public  static ArrayList<String> getMaximumTargetLevelAllSubFolderListInAHdfsFolder (String aBDclusterName, String hdfsParentFolderPath, int targetDirLevel ) {
		if (!hdfsParentFolderPath.endsWith("/")){
			hdfsParentFolderPath += "/";
		}
		
		ArrayList<String> allLevelFolderList = new ArrayList<String> ();
		allLevelFolderList = getAllTargetDirLevelSubFolderListInAHdfsFolder (aBDclusterName, hdfsParentFolderPath, allLevelFolderList, 0, targetDirLevel);
		
		ArrayList<String> allMaximumTargetLevelFolderList = new ArrayList<String> ();
		
		if (!allLevelFolderList.isEmpty()){
			int maxSubDirLevel = -1;
			for (String tempFolderName : allLevelFolderList ){
				String temp = tempFolderName.replaceAll(hdfsParentFolderPath, "");
				int subDirNumber = temp.toCharArray().length - temp.replaceAll("/", "").toCharArray().length + 1;
				if (subDirNumber > maxSubDirLevel){
					maxSubDirLevel = subDirNumber;
				}
			}//end for
			
			System.out.println("\n*** Final maxSubDirLevel: [(" + maxSubDirLevel + ")] for the parent HDFS folder - " + hdfsParentFolderPath);
			targetDirLevel = maxSubDirLevel;
			
			for (String tempFolderName : allLevelFolderList ){			
				String temp = tempFolderName.replaceAll(hdfsParentFolderPath, "");
				//System.out.println("\n*** temp: " + temp);
				
				int subDirNumber = temp.toCharArray().length - temp.replaceAll("/", "").toCharArray().length + 1;
				//System.out.println("*** subDirNumber: " + subDirNumber);
				
				if (subDirNumber == targetDirLevel ){
					allMaximumTargetLevelFolderList.add(tempFolderName);
				} else {
					
					if (temp.contains("/")){
						String[] tempSplit = temp.split("/");
						int tempDirLevelNumber = tempSplit.length;
						//System.out.println("*** tempDirLevelNumber: " + tempDirLevelNumber);
						if (tempDirLevelNumber == targetDirLevel){
							allMaximumTargetLevelFolderList.add(tempFolderName);
						}
					} 
				}		
			}//end for			
		}//end outer if	
		allLevelFolderList.clear();
		
		return allMaximumTargetLevelFolderList;		
	}//end getTargetLevelAllSubFolderListInAHdfsFolder
	
	private  static ArrayList<String> getAllTargetDirLevelSubFolderListInAHdfsFolder (String aBDclusterName, String hdfsParentFolderPath, ArrayList<String> allFolderList, int currDirLevel, int targetDirLevel) {
		try {
			BdCluster currBdCluster = new BdCluster(aBDclusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsParentFolderPath));				
			//allFolderList.clear();
			
			currDirLevel++;
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFolderPathAndName = "";				
				if (status[i].isDirectory()){
					//folderCountTotal++;
					currHdfsFolderPathAndName = status[i].getPath().toString();
					if (currHdfsFolderPathAndName.contains("hdfs://hdp00") && currHdfsFolderPathAndName.contains(":8020")){
						String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");					
						currHdfsFolderPathAndName = folderNameSplit[1];
						//System.out.println("  *** HDFS Sub-Folder:  " + currHdfsFolderPathAndName + ";");
						allFolderList.add(currHdfsFolderPathAndName);
						//System.out.println("\n*** # (" + folderCountTotal + ") Current HDFS Folder: \n ---" + currHdfsFolderPathAndName + ";");
						if (currDirLevel < targetDirLevel ){
							getAllTargetDirLevelSubFolderListInAHdfsFolder (aBDclusterName, currHdfsFolderPathAndName, allFolderList, currDirLevel, targetDirLevel );
						}//end inner if								
					}//end middle if					
				} //end outer if		
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}

		return allFolderList;
	}//end getAllHdfsFoldersInAParentFolder
////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<String> getCurrentDirLevel1SimplifiedSubDirList (String aBDclusterName, String aHdfsFolderPathName){
		ArrayList<String> allHdfsSubFolderList = new ArrayList<String> ();
		try {
			
			BdCluster currBdCluster = new BdCluster(aBDclusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(aHdfsFolderPathName));
			
			//int subFolderCount = 0;
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFolderPathAndName = "";				
				if (status[i].isDirectory()){ //.isDir()){
					//subFolderCount++;
					currHdfsFolderPathAndName = status[i].getPath().toString();
					if (currHdfsFolderPathAndName.contains("hdfs://hdp00") && currHdfsFolderPathAndName.contains(":8020")){
						String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");						
						String simpleSubFolderName = folderNameSplit[1].replace(aHdfsFolderPathName, "");						
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
	
	public static ArrayList<String> getCurrentDirLevel1FullSubDirList (String aBDclusterName, String aHdfsFolderPathName){
		ArrayList<String> allHdfsSubFolderList = new ArrayList<String> ();
		try {
			
			BdCluster currBdCluster = new BdCluster(aBDclusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(aHdfsFolderPathName));
			
			//int subFolderCount = 0;
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFolderPathAndName = "";				
				if (status[i].isDirectory()){ //.isDir()){
					//subFolderCount++;
					currHdfsFolderPathAndName = status[i].getPath().toString();
					allHdfsSubFolderList.add(currHdfsFolderPathAndName);
					//System.out.println("\n*** # (" + subFolderCount + ") HDFS Sub-Folder:  ---" + currHdfsFolderPathAndName + ";");
					//System.out.println("  *** HDFS Sub-Folder:  " + currHdfsFolderPathAndName + ";");					
				} 
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		return allHdfsSubFolderList;
	}//end getCurrentDirLevel1FullSubDirList

//////////	

	public  static ArrayList<String> getSelectedAllHdfsSubFoldersInAParentFolder (String aBDclusterName, String hdfsParentFolderPath, String folderNameSelector, String folderNameEndingSelector) {
		ArrayList<String> allSubFolderList = getAllHdfsSubFoldersInAParentFolder (aBDclusterName, hdfsParentFolderPath);
		
		if (folderNameSelector.equalsIgnoreCase("") || folderNameSelector.equalsIgnoreCase("all")){
			return allSubFolderList;
		} else {
			ArrayList<String> selectedFolderList = new ArrayList<String> ();
			for (String tempFolderName : allSubFolderList ){
				if (tempFolderName.contains(folderNameSelector) && tempFolderName.endsWith(folderNameEndingSelector)){
					selectedFolderList.add(tempFolderName);
				}
			}
			return selectedFolderList;			
		}		
	}//end getSelectedAllHdfsFoldersInAParentFolder
		
	public  static ArrayList<String> getAllHdfsSubFoldersInAParentFolder (String aBDclusterName, String hdfsParentFolderPath){
		ArrayList<String> allSubFolderList = new ArrayList<String> ();
		allSubFolderList = getAllHdfsFoldersInAParentFolder (aBDclusterName, hdfsParentFolderPath, allSubFolderList);
		return allSubFolderList;
	}//end getAllHdfsSubFoldersInAParentFolder
			
	private  static ArrayList<String> getAllHdfsFoldersInAParentFolder (String aBDclusterName, String hdfsParentFolderPath, ArrayList<String> allSubFolderList) {
		try {
			BdCluster currBdCluster = new BdCluster(aBDclusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsParentFolderPath));				
			
			//int folderCountTotal = 0;
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFolderPathAndName = "";				
				if (status[i].isDirectory()){
					//folderCountTotal++;
					currHdfsFolderPathAndName = status[i].getPath().toString();
					if (currHdfsFolderPathAndName.contains("hdfs://hdp00") && currHdfsFolderPathAndName.contains(":8020")){
						String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");					
						currHdfsFolderPathAndName = folderNameSplit[1];
						//System.out.println("  *** HDFS Sub-Folder:  " + currHdfsFolderPathAndName + ";");
						allSubFolderList.add(currHdfsFolderPathAndName);
						//System.out.println("\n*** # (" + folderCountTotal + ") Current HDFS Folder: \n ---" + currHdfsFolderPathAndName + ";");
						getAllHdfsFoldersInAParentFolder (aBDclusterName, currHdfsFolderPathAndName, allSubFolderList);
					}					
				} 
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		return allSubFolderList;
	}//end getAllHdfsFoldersInAParentFolder
	
	
	
/////////////////////////////////////////////////////////////////////////////////////	
	
	public static boolean isHdfsFileOrFolderExisting (String aBDclusterName, String aHdfsFileOrFolderPath){
		boolean hdfsExistingStatus = false;
		BdCluster currBdCluster = new BdCluster(aBDclusterName);
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();		
		
		try {
			Path hdfsfilePath = new Path(aHdfsFileOrFolderPath);
			if (currHadoopFS.exists(hdfsfilePath)){
				hdfsExistingStatus = true;
			}
			currHadoopFS.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		//System.out.println("\n*** Existing status is: " + hdfsExistingStatus + " for " + aHdfsFileOrFolderPath + "!!!");
		
		return hdfsExistingStatus;
	}//end isHdfsFileOrFolderExisting
	
	
	private ArrayList<String> getHdfsFilesInAFolder (String hdfsFileOrFolderPath, String fileNameSelector) {
		ArrayList<String> selectedFileList = new ArrayList<String> ();
		
		if (fileNameSelector.equalsIgnoreCase("") || fileNameSelector.equalsIgnoreCase("all")){
			this.getAllHdfsFilesInAFolder (hdfsFileOrFolderPath, selectedFileList);
		} else {
			this.getSelectedAllHdfsFilesInAFolder (hdfsFileOrFolderPath, selectedFileList,  fileNameSelector);
		}
		
		return selectedFileList;
	}//end getHdfsFilesInAFolder
	
	

	private void getAllHdfsFilesInAFolder (String hdfsFileOrFolderPath, ArrayList<String> allFilesList) {
		//int fileCount = 0;
		try {
			BdCluster currBdCluster = new BdCluster(this.bdClusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();			
			
			//Approach #1: Works for all HDFS versions 
			// It may be slower on performance for HDFS > V2.0 than Approach #2 only if the file number in a folder is very small
			//FileSystem fs = FileSystem.get(new Configuration());
			//FileStatus[] status = fs.listStatus(new Path(hdfsFileOrFolderPath));
			
			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsFileOrFolderPath));				
			//BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			
			for (int i = 0; i < status.length; i++) {//0..status.length								
				String currHdfsFilePathAndName = "";				
				if (!status[i].isDirectory()){ //.isDir()){
					//fileCount++;
					currHdfsFilePathAndName = status[i].getPath().toString().replace("file:/", "");
					allFilesList.add(currHdfsFilePathAndName);
					//System.out.println("\n*** # (" + fileCount + ") Current HDFS File: \n ---" + currHdfsFilePathAndName + ";");
				} else {
					//fs.open(status[i].getPath()))
					String subHdfsFileOrFolderPath = status[i].getPath().toString().replace("file:/", "");
					//System.out.println("\n*2* : subHdfsFileOrFolderPath: " + subHdfsFileOrFolderPath + "");
					getAllHdfsFilesInAFolder(subHdfsFileOrFolderPath,allFilesList);
				}
			}//end for
			
			//Aproach #2 is disabled at present
//			//Approach #2: Works only for all HDFS V2.0 and above
//			RemoteIterator<LocatedFileStatus> fileStatusListIterator 
//						= currHadoopFS.listFiles(new Path(hdfsFileOrFolderPath), true);
//			while(fileStatusListIterator.hasNext()){
//			    LocatedFileStatus fileStatus = fileStatusListIterator.next();
//			    String currHdfsFolderPathAndName = fileStatus.getPath().toString();
//			    allFilesList.add(currHdfsFolderPathAndName);
//			    
//			//    if (currHdfsFolderPathAndName.contains("hdfs://hdp00") && currHdfsFolderPathAndName.contains(":8020")){
//			//		String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");
//			//		String filePathAndName = folderNameSplit[1].trim();	
//			//		//System.out.println("\n*** HDFS filePathAndName: " + filePathAndName);
//			//		allFileList.add(filePathAndName);
//			//    }	 
//			}//end while			
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}//end getHdfsFilesInAFolder
	
	
	private void getSelectedAllHdfsFilesInAFolder (String hdfsFileOrFolderPath, ArrayList<String> allFilesList, String fileNameSelector) {
		//int fileCount = 0;
		try {
			//FileSystem fs = FileSystem.get(new Configuration());
			//FileStatus[] status = fs.listStatus(new Path(hdfsFileOrFolderPath));
			
			BdCluster currBdCluster = new BdCluster(this.bdClusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsFileOrFolderPath));
			
			for (int i = 0; i < status.length; i++) {//0..status.length	
							
				String currHdfsFilePathAndName = "";				
				if (!status[i].isDirectory()){ //.isDir()){
					//fileCount++;
					currHdfsFilePathAndName = status[i].getPath().toString().replace("file:/", "");
					if (currHdfsFilePathAndName.contains(fileNameSelector)){
						allFilesList.add(currHdfsFilePathAndName);
						//System.out.println("\n*** # (" + fileCount + ") Current HDFS File: \n ---" + currHdfsFilePathAndName + ";");
					}					
				} else {
					//fs.open(status[i].getPath()))
					String subHdfsFileOrFolderPath = status[i].getPath().toString().replace("file:/", "");
					//System.out.println("\n*2* : subHdfsFileOrFolderPath: " + subHdfsFileOrFolderPath + "");
					getSelectedAllHdfsFilesInAFolder(subHdfsFileOrFolderPath,allFilesList, fileNameSelector);
				}
			}//end for
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}//end getSelectedAllHdfsFilesInAFolder
	
////////////////////////////////////////////////////////////////////////////////////	
	public static String createHadoopArchiveCmd(String tgtSrcFolderFullPathAndName, String tgtSrcFolderSimpleName, String destArchiveParentFolder){
		//hadoop archive -archiveName radiology.har -p /data/ocean_old_May6_2015/radiology  /data/archive/jsonv1;
		String fullArchiveCmdStr = "hadoop archive -archiveName "
				+ tgtSrcFolderSimpleName + ".har -p " 
				+ tgtSrcFolderFullPathAndName + " " 
				+ destArchiveParentFolder + ";";
		
		return fullArchiveCmdStr;
	}//end createHadoopArchiveCmd
	
////////////////////////////////////////////////////////////////////////////////////
	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public String getHdfsFolderPathAndName() {
		return hdfsFolderPathAndName;
	}
	
	public void setHdfsFolderPathAndName(String hdfsFolderPathAndName) {
		this.hdfsFolderPathAndName = hdfsFolderPathAndName;
	}

	public String getFileNameSelector() {
		return fileNameSelector;
	}

	public void setFileNameSelector(String fileNameSelector) {
		this.fileNameSelector = fileNameSelector;
	}

	public ArrayList<String> getAllHdfsFileList() {
		return allHdfsFileList;
	}

	public void setAllHdfsFileList(ArrayList<String> allHdfsFileList) {
		this.allHdfsFileList = allHdfsFileList;
	}

	public ArrayList<String> getSelectedAllHdfsFileList() {
		return selectedAllHdfsFileList;
	}

	public void setSelectedAllHdfsFileList(ArrayList<String> selectedAllHdfsFileList) {
		this.selectedAllHdfsFileList = selectedAllHdfsFileList;
	}
		
}//end class
