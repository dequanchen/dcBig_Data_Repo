package dcModelClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 9/25,26/2015
 */

public class HdfsFolderParentToYearMonthDay {		
	//private ArrayList<String> allUTDDirYrMonDayOnlyFolderList = new ArrayList<String> ();
	private ArrayList<String> allYearMonthDayStrList = new ArrayList<String> ();
	private ArrayList<Integer> allPreCutOffDeltaDaysList = new ArrayList<Integer> ();
	
	public HdfsFolderParentToYearMonthDay(String aBdClusterName, String aParentHdfsFolder, int cutOffDeltaDayNumber) {		
		if (!aParentHdfsFolder.endsWith("/")){
			aParentHdfsFolder += "/";
		}
		
		ArrayList<String> allUpToDayDirFolderList = new ArrayList<String> ();
		allUpToDayDirFolderList = getAllUpToYearMonthDayDirLevelSubFolderListInAHdfsFolder (aBdClusterName, aParentHdfsFolder, allUpToDayDirFolderList);
		
		//int count1 = 0;
  		//for (String tempYrMonDayLevelFolderName : allUpToDayDirFolderList){
  		//	count1++;	  			
  		//	System.out.println("*** (" + count1 + ") tempYrMonDayLevelFolderName: " + tempYrMonDayLevelFolderName);			
  		//}//end outer for
  		//System.out.println("\n");
					
		if (!allUpToDayDirFolderList.isEmpty()){
			//this.allUTDDirYrMonDayOnlyFolderList = obtainYearMonthDayDirOnlyAllFolderList (allUpToDayDirFolderList);
			//this.allUTDDirYrMonDayOnlyFolderList = getSortedDuplicatesFreeArrayList(this.allUTDDirYrMonDayOnlyFolderList);
			//int count2 = 0;
	  		//for (String tempYrMonDayFolder : this.allUTDDirYrMonDayOnlyFolderList){
	  		//	count2++;	  			
	  		//	System.out.println("*** (" + count2 + ") tempYrMonDayFolder: " + tempYrMonDayFolder);			
	  		//}//end outer for
	  		//System.out.println("\n");
	  		
			
			this.allYearMonthDayStrList = obtainYearMonthDayStringList (allUpToDayDirFolderList);
			this.allYearMonthDayStrList = getSortedDuplicatesFreeArrayList(this.allYearMonthDayStrList);
			//int count3 = 0;
	  		//for (String tempYrMonDay : this.allYearMonthDayStrList){
	  		//	count3++;	  			
	  		//	System.out.println("*** (" + count3 + ") tempYrMonDay: " + tempYrMonDay);			
	  		//}//end outer for
	  		//System.out.println("\n");
	  		
		}//end outer if			
		allUpToDayDirFolderList.clear();
				
  		
  		if (!this.allYearMonthDayStrList.isEmpty()){
  			String todayDateStr = DayClock.getPastNDaysDate4CurrentDay(0);
  			//System.out.println("*** todayDateStr: " + todayDateStr);
  			String todayYrMonDayStr = "/" + todayDateStr.replace("/0", "/") + "/";
  			//System.out.println("*** todayYrMonDayStr: " + todayYrMonDayStr);
  			//System.out.println("*** cutOffDeltaDayNumber: " + cutOffDeltaDayNumber);
  			
  			SimpleDateFormat sdf = new SimpleDateFormat(("/yyyy/MM/dd"));  			
  			try {  	
  				Calendar calEnd = new GregorianCalendar();
  				Date endDate = sdf.parse(todayYrMonDayStr);
  				calEnd.setTime(endDate);
  				
  				for (String startYrMonDay : this.allYearMonthDayStrList){
  					Calendar calStart = new GregorianCalendar();
  					Date startDate = sdf.parse(startYrMonDay);
  					calStart.setTime(startDate);
  					//System.out.println("\n*** startDate: " + startDate);  
  					int deltaDayNumber = (int) ((calEnd.getTime().getTime() - calStart.getTime().getTime())/(1000*60*60*24));
  					if (deltaDayNumber >= cutOffDeltaDayNumber){
  						this.allPreCutOffDeltaDaysList.add(deltaDayNumber);
  					}  					
  	  			}//end for	  				
			} catch (Exception e) {				
				e.printStackTrace();
			} //end try  			
  		}//end if  		
  		
  		//Collections.sort(this.allPreCutOffDeltaDaysList, Collections.reverseOrder());//This is slower
  		Collections.sort(this.allPreCutOffDeltaDaysList);
  		Collections.reverse(this.allPreCutOffDeltaDaysList);
  		
  		//System.out.println("\n");
  		//int count4 = 0;
  		//for (int tempDeltaDayNumber : this.allPreCutOffDeltaDaysList){
  		//	count4++;	  			
  		//	System.out.println("*** (" + count4 + ") tempDeltaDayNumber: " + tempDeltaDayNumber);			
  		//}//end outer for
  		//System.out.println("\n");  		 		
	}//end constructor
	
	public static int getDeltaDayNumberFromAYearMonthDayString (String queryYearMonthDayString){
		int calculatedDeltaDayNumber = -(Integer.MAX_VALUE);
		if (!queryYearMonthDayString.startsWith("/")){
			queryYearMonthDayString = "/" + queryYearMonthDayString;
		}
		
		String todayDateStr = DayClock.getPastNDaysDate4CurrentDay(0);
		//System.out.println("*** todayDateStr: " + todayDateStr);
		String todayYrMonDayStr = "/" + todayDateStr.replace("/0", "/") + "/";
		//System.out.println("*** todayYrMonDayStr: " + todayYrMonDayStr);
		//System.out.println("*** cutOffDeltaDayNumber: " + cutOffDeltaDayNumber);
		
		SimpleDateFormat sdf = new SimpleDateFormat(("/yyyy/MM/dd"));  			
		try {  	
			Calendar calEnd = new GregorianCalendar();
			Date endDate = sdf.parse(todayYrMonDayStr);
			calEnd.setTime(endDate);
			
			Calendar calStart = new GregorianCalendar();
			Date startDate = sdf.parse(queryYearMonthDayString);
			calStart.setTime(startDate);
			//System.out.println("\n*** startDate: " + startDate);  
			calculatedDeltaDayNumber= (int) ((calEnd.getTime().getTime() - calStart.getTime().getTime())/(1000*60*60*24));
		} catch (Exception e) {				
			e.printStackTrace();
		} //end try 
		
		return calculatedDeltaDayNumber;
	}//end getDeltaDayNumberFromAYearMonthDayString
	
	private static ArrayList<String> obtainYearMonthDayStringList (ArrayList<String> allUpToDayDirFolderList){
		ArrayList<String> allYearMonthDayStrList = new ArrayList<String> ();
		
		for (String tempYrMonDayFolderName : allUpToDayDirFolderList ){
			//System.out.println("\n*** tempFolderName: " + tempFolderName);
			
			if (tempYrMonDayFolderName.matches(".*/\\d{4}/\\d{1,2}/\\d{1,2}")){	
				//System.out.println("\n*** tempYrMonDayFolderName: " + tempYrMonDayFolderName);								
				String temp = tempYrMonDayFolderName.replaceAll("[^0-9/]+", "").replaceFirst("/{1,100}", "/");
				//System.out.println("*** temp: " + temp);
				//String temp1 = temp.replaceFirst("/{1,100}", "/");
				//System.out.println("*** temp1: " + temp1);
				
				if (temp.contains("/")) {
					String[] tempSplits = temp.split("/");
					int maxIndex = tempSplits.length-1;
					String yrMonDay = "/" +tempSplits[maxIndex-2] + "/" + tempSplits[maxIndex-1] + "/" + tempSplits[maxIndex];
					allYearMonthDayStrList.add(yrMonDay);						
				}					
			}//end middle if				
		}//end for		
		
		return allYearMonthDayStrList;
	}//end obtainYearMonthDayStringList
	
//	private static ArrayList<String> obtainYearMonthDayDirOnlyAllFolderList (ArrayList<String> allUpToDayDirFolderList){
//		ArrayList<String> allYearMonthDayOnlyFolderList = new ArrayList<String> ();
//		
//		for (String tempYrMonDayFolderName : allUpToDayDirFolderList ){
//			//System.out.println("\n*** tempFolderName: " + tempFolderName);			
//			if (tempYrMonDayFolderName.matches(".*/\\d{4}/\\d{1,2}/\\d{1,2}")){	
//				//System.out.println("\n*** tempYrMonDayFolderName: " + tempYrMonDayFolderName);			
//				allYearMonthDayOnlyFolderList.add(tempYrMonDayFolderName);
//			}//end middle if				
//		}//end for		
//		
//		return allYearMonthDayOnlyFolderList;
//	}//end getYearMonthDayDirOnlyAllFolderList
	
	private  static ArrayList<String> getAllUpToYearMonthDayDirLevelSubFolderListInAHdfsFolder (String aBDclusterName, String hdfsParentFolderPath, ArrayList<String> allFolderList) {
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
						if (!currHdfsFolderPathAndName.matches(".*/\\d{4}/\\d{1,2}/\\d{1,2}") ){
							getAllUpToYearMonthDayDirLevelSubFolderListInAHdfsFolder (aBDclusterName, currHdfsFolderPathAndName, allFolderList);
						}//end inner if								
					}//end middle if					
				} //end outer if		
			}//end for
		} catch (IOException e) {			
			e.printStackTrace();
		}

		return allFolderList;
	}//end getAllUpToYearMonthDayDirLevelSubFolderListInAHdfsFolder
	
	
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

	public ArrayList<String> getAllYearMonthDayStrList() {
		return allYearMonthDayStrList;
	}

	public void setAllYearMonthDayStrList(ArrayList<String> allYearMonthDayStrList) {
		this.allYearMonthDayStrList = allYearMonthDayStrList;
	}

	public ArrayList<Integer> getAllPreCutOffDeltaDaysList() {
		return allPreCutOffDeltaDaysList;
	}

	public void setAllPreCutOffDeltaDaysList(
			ArrayList<Integer> allPreCutOffDeltaDaysList) {
		this.allPreCutOffDeltaDaysList = allPreCutOffDeltaDaysList;
	}

}//end class
