package dcModelClasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 9/25/2015
 */

public class HdfsFolderParentToYear {
	private int minParentToYearDirLevelDelta = -100;
	private int maxParentToYearDirLevelDelta = -100;
	private ArrayList<String> aboveYearDir1LevelFolderList = new ArrayList<String> ();

	public HdfsFolderParentToYear(String aBdClusterName, String aParentHdfsFolder) {		
		if (!aParentHdfsFolder.endsWith("/")){
			aParentHdfsFolder += "/";
		}
		
		ArrayList<String> allYearLevelFolderList = new ArrayList<String> ();
		allYearLevelFolderList = getAllYearDirLevelSubFolderListInAHdfsFolder (aBdClusterName, aParentHdfsFolder, allYearLevelFolderList);
		
		//int count1 = 0;
  		//for (String tempYearLevelFolderName : allYearLevelFolderList){
  		//	count1++;	  			
  		//	System.out.println("*** (" + count1 + ") tempYearLevelFolderName: " + tempYearLevelFolderName);			
  		//}//end outer for
  		//System.out.println("\n");
		
		ArrayList<Integer> allDirLevelDeltaList = new ArrayList<Integer> ();		
		if (!allYearLevelFolderList.isEmpty()){						
			for (String tempFolderName : allYearLevelFolderList ){
				//System.out.println("\n*** tempFolderName: " + tempFolderName);
				
				if (tempFolderName.matches(".*/\\d{4}")){
					String yearDir1LevelFolderName = tempFolderName.replaceAll("/\\d{4}", "");
					this.aboveYearDir1LevelFolderList.add(yearDir1LevelFolderName);
				}
				
				String temp = tempFolderName.replaceAll(aParentHdfsFolder, "");
				//System.out.println("*** temp: " + temp);
								
				if (!temp.contains("/")){
					if (temp.matches("\\d{4}")){
						allDirLevelDeltaList.add(0);
						//System.out.println("--- adding 0 to allDirLevelDeltaList!!!");
					}
				} else {
					String [] tempSplits = temp.split("/");
					for (int i = 0; i < tempSplits.length; i++ ){
						String tempStr = tempSplits[i];
						if (tempStr.matches("\\d{4}")){
							allDirLevelDeltaList.add(i);
							//System.out.println("--- adding " + i + " to allDirLevelDeltaList!!!");
						}
					}
				}				
			}//end for			
		}//end outer if	
		allYearLevelFolderList.clear();
		System.out.println("\n");
		
		this.aboveYearDir1LevelFolderList = getSortedDuplicatesFreeArrayList(this.aboveYearDir1LevelFolderList);
		allDirLevelDeltaList = getSortedDuplicatesFreeIntegerList(allDirLevelDeltaList);
		
		//int count2 = 0;
  		//for (int tempDirLevelDelta : allDirLevelDeltaList){
  		//	count2++;	  			
  		//	System.out.println("*** (" + count2 + ") tempDirLevelDelta: " + tempDirLevelDelta);			
  		//}//end outer for
  		//System.out.println("\n");
		
		//int count3 = 0;
  		//for (String tempAboveYearDir1LevelFolderName : this.aboveYearDir1LevelFolderList){
  		//	count3++;	  			
  		//	System.out.println("*** (" + count3 + ") tempAboveYearDir1LevelFolderName: " + tempAboveYearDir1LevelFolderName);			
  		//}//end outer for
  		//System.out.println("\n");
  		
  		if (!allDirLevelDeltaList.isEmpty()){
  			this.minParentToYearDirLevelDelta = allDirLevelDeltaList.get(0);
  			this.maxParentToYearDirLevelDelta = allDirLevelDeltaList.get(allDirLevelDeltaList.size() - 1);
  		}
  		 		
  		
  		//System.out.println("\n*** this.minParentToYearDirLevelDelta: " + this.minParentToYearDirLevelDelta);
  		//System.out.println("\n*** this.maxParentToYearDirLevelDelta: " + this.maxParentToYearDirLevelDelta);
	}//end constructor
	
	private  static ArrayList<String> getAllYearDirLevelSubFolderListInAHdfsFolder (String aBDclusterName, String hdfsParentFolderPath, ArrayList<String> allFolderList) {
		try {
			BdCluster currBdCluster = new BdCluster(aBDclusterName);
			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsParentFolderPath));				
			//allFolderList.clear();
			
			
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
						if (!currHdfsFolderPathAndName.matches(".*/\\d{4}") ){
							getAllYearDirLevelSubFolderListInAHdfsFolder (aBDclusterName, currHdfsFolderPathAndName, allFolderList);
						}//end inner if								
					}//end middle if					
				} //end outer if		
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}

		return allFolderList;
	}//end getAllYearDirLevelSubFolderListInAHdfsFolder
	
//	private  static ArrayList<String> getAllHdfsFoldersInAParentFolder (String aBDclusterName, String hdfsParentFolderPath, ArrayList<String> allSubFolderList) {
//		try {
//			BdCluster currBdCluster = new BdCluster(aBDclusterName);
//			FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
//			FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsParentFolderPath));				
//			
//			//int folderCountTotal = 0;
//			for (int i = 0; i < status.length; i++) {//0..status.length								
//				String currHdfsFolderPathAndName = "";				
//				if (status[i].isDirectory()){
//					//folderCountTotal++;
//					currHdfsFolderPathAndName = status[i].getPath().toString();
//					if (currHdfsFolderPathAndName.contains("hdfs://hdp00") && currHdfsFolderPathAndName.contains(":8020")){
//						String[] folderNameSplit = currHdfsFolderPathAndName.split(":8020");					
//						currHdfsFolderPathAndName = folderNameSplit[1];
//						//System.out.println("  *** HDFS Sub-Folder:  " + currHdfsFolderPathAndName + ";");
//						allSubFolderList.add(currHdfsFolderPathAndName);
//						//System.out.println("\n*** # (" + folderCountTotal + ") Current HDFS Folder: \n ---" + currHdfsFolderPathAndName + ";");
//						getAllHdfsFoldersInAParentFolder (aBDclusterName, currHdfsFolderPathAndName, allSubFolderList);
//					}					
//				} 
//			}//end for
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}
//		
//		return allSubFolderList;
//	}//end getAllHdfsFoldersInAParentFolder
	
	public static ArrayList<String> getSortedDuplicatesFreeArrayList (ArrayList<String> currArrayList){
		//Remove duplicates in currArrayList
	    HashSet<String> currArrayList_HashedSet = new HashSet<String>(currArrayList);
	    currArrayList.clear();
	    currArrayList.addAll(currArrayList_HashedSet);	    
	    //Sort the duplicates-removed currArrayList
		Collections.sort(currArrayList);
		return currArrayList;		
	}//end getSortedDuplicatesFreeArrayList	
	
	public static ArrayList<Integer> getSortedDuplicatesFreeIntegerList (ArrayList<Integer> currArrayList){
		//Remove duplicates in currArrayList
	    HashSet<Integer> currArrayList_HashedSet = new HashSet<Integer>(currArrayList);
	    currArrayList.clear();
	    currArrayList.addAll(currArrayList_HashedSet);	    
	    //Sort the duplicates-removed currArrayList
		Collections.sort(currArrayList);
		return currArrayList;		
	}//end getSortedDuplicatesFreeIntegerList	


	public int getMinParentToYearDirLevelDelta() {
		return minParentToYearDirLevelDelta;
	}

	public void setMinParentToYearDirLevelDelta(int minParentToYearDirLevelDelta) {
		this.minParentToYearDirLevelDelta = minParentToYearDirLevelDelta;
	}

	public int getMaxParentToYearDirLevelDelta() {
		return maxParentToYearDirLevelDelta;
	}

	public void setMaxParentToYearDirLevelDelta(int maxParentToYearDirLevelDelta) {
		this.maxParentToYearDirLevelDelta = maxParentToYearDirLevelDelta;
	}

	public ArrayList<String> getAboveYearDir1LevelFolderList() {
		return aboveYearDir1LevelFolderList;
	}

	public void setAboveYearDir1LevelFolderList(
			ArrayList<String> aboveYearDir1LevelFolderList) {
		this.aboveYearDir1LevelFolderList = aboveYearDir1LevelFolderList;
	}
}//end class
