package OS_Hardening;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BDProd1HDFS {
	private String bdClusterName = "BDProd";	
	private ArrayList<String> currentClusterSrcHdfsValidFolderList = new  ArrayList<String>();
	private ArrayList<String> currentClusterSrcHdfsValidFolderIndexPairList = new  ArrayList<String>();
	
	private String hdfsValidFolderListSrcStatus = "";
	private String srcClusterHDFSValidFolderListFilePathAndName = "";
	private String srcClusterHDFSValidFolderInfoFilePathAndName = "";
	private String srcClusterHDFSValidFolderIndexPairFilePathAndName = "";
	private int currSrcHdfsValidFolderNumber = 0;

	public BDProd1HDFS(String aHdfsValidFolderListSrcStatus) { //BDProd1HBase
		this.hdfsValidFolderListSrcStatus = aHdfsValidFolderListSrcStatus;
		if (this.hdfsValidFolderListSrcStatus.equalsIgnoreCase("Static")){
			this.srcClusterHDFSValidFolderListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderList_Static.txt";
			this.srcClusterHDFSValidFolderInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderInfo_Static.txt";
			this.srcClusterHDFSValidFolderIndexPairFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_FolderIndexPair_Static.txt";
		}
		if (this.hdfsValidFolderListSrcStatus.equalsIgnoreCase("Static_ocean1")){
			this.srcClusterHDFSValidFolderListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderList_Static_Ocean20160118.txt";
			this.srcClusterHDFSValidFolderInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderInfo_Static_Ocean20160118.txt";
			this.srcClusterHDFSValidFolderIndexPairFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_FolderIndexPair_Static_Ocean20160118.txt";
		}
		
		if (this.hdfsValidFolderListSrcStatus.equalsIgnoreCase("Static_ocean1_har")){
			this.srcClusterHDFSValidFolderListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderList_Static_Ocean20160118_Har.txt";
			this.srcClusterHDFSValidFolderInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderInfo_Static_Ocean20160118_Har.txt";
			this.srcClusterHDFSValidFolderIndexPairFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_FolderIndexPair_Static_Ocean20160118_Har.txt";
		}
		
		if (this.hdfsValidFolderListSrcStatus.equalsIgnoreCase("Dynamic")){
			this.srcClusterHDFSValidFolderListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderList_Dynamic.txt";
			this.srcClusterHDFSValidFolderInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_ValidFolderInfo_Dynamic.txt";
			this.srcClusterHDFSValidFolderIndexPairFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HDFS\\" + "dcBDProd1_HDFS_FolderIndexPair_Dynamic.txt";
			
		}
		
		obtainCurrentClusterValidHdfsFolderList(this.srcClusterHDFSValidFolderListFilePathAndName);
		obtainCurrentFolderIndexPairList(this.srcClusterHDFSValidFolderIndexPairFilePathAndName);
	}
	
	private void obtainCurrentFolderIndexPairList (String srcClusterHDFSValidFolderIndexPairFilePathAndName){
		File aFile = new File (srcClusterHDFSValidFolderIndexPairFilePathAndName);
		if (!aFile.exists()){
			System.out.println("\n*** File Not Existing - srcClusterHDFSValidFolderIndexPairFilePathAndName: " + srcClusterHDFSValidFolderIndexPairFilePathAndName);
			return;
		}
		if (!srcClusterHDFSValidFolderIndexPairFilePathAndName.isEmpty()){
			//InputStream in = BDProd1HBase.class.getResourceAsStream(srcClusterHDFSValidFolderListFilePathAndName);		
			//InputStreamReader inStrReader = new InputStreamReader(in);
			//BufferedReader br = new BufferedReader(inStrReader);
								
			try {
				FileReader aFileReader = new FileReader(srcClusterHDFSValidFolderIndexPairFilePathAndName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				//int addCount = 0;
				while ( (line = br.readLine()) != null) {
					if (!line.contains("#")){
						if (!line.isEmpty()){
							//addCount++;
							this.currentClusterSrcHdfsValidFolderIndexPairList.add(line.trim());	
							//addCount++;
							//System.out.println("*-* (" + addCount + ") added a entry node: " + line);	
						}						
					}													            
				}
				
				br.close();
				//inStrReader.close();
				//in.close();
				aFileReader.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}		
	}//end obtainCurrentFolderIndexPairList
	
	private void obtainCurrentClusterValidHdfsFolderList (String srcClusterHDFSValidFolderListFilePathAndName){
		File aFile = new File (srcClusterHDFSValidFolderListFilePathAndName);
		if (!aFile.exists()){
			System.out.println("\n*** File Not Existing - srcClusterHDFSValidFolderListFilePathAndName: " + srcClusterHDFSValidFolderListFilePathAndName);
			return;
		}
		if (!srcClusterHDFSValidFolderListFilePathAndName.isEmpty()){
			//InputStream in = BDProd1HBase.class.getResourceAsStream(srcClusterHDFSValidFolderListFilePathAndName);		
			//InputStreamReader inStrReader = new InputStreamReader(in);
			//BufferedReader br = new BufferedReader(inStrReader);
								
			try {
				FileReader aFileReader = new FileReader(srcClusterHDFSValidFolderListFilePathAndName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				int addCount = 0;
				while ( (line = br.readLine()) != null) {
					if (!line.contains("#")){
						if (!line.isEmpty()){
							addCount++;
							this.currentClusterSrcHdfsValidFolderList.add(line.trim());	
							//addCount++;
							//System.out.println("*-* (" + addCount + ") added a entry node: " + line);	
						}						
					}													            
				}
				this.currSrcHdfsValidFolderNumber = addCount;
				br.close();
				//inStrReader.close();
				//in.close();
				aFileReader.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}		
	}//end obtainCurrentClusterValidHdfsFolderList

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public ArrayList<String> getCurrentClusterSrcHdfsValidFolderList() {
		return currentClusterSrcHdfsValidFolderList;
	}

	public void setCurrentClusterSrcHdfsValidFolderList(
			ArrayList<String> currentClusterSrcHdfsValidFolderList) {
		this.currentClusterSrcHdfsValidFolderList = currentClusterSrcHdfsValidFolderList;
	}

	public String getHdfsValidFolderListSrcStatus() {
		return hdfsValidFolderListSrcStatus;
	}

	public void setHdfsValidFolderListSrcStatus(String hdfsValidFolderListSrcStatus) {
		this.hdfsValidFolderListSrcStatus = hdfsValidFolderListSrcStatus;
	}

	public String getSrcClusterHDFSValidFolderListFilePathAndName() {
		return srcClusterHDFSValidFolderListFilePathAndName;
	}

	public void setSrcClusterHDFSValidFolderListFilePathAndName(
			String srcClusterHDFSValidFolderListFilePathAndName) {
		this.srcClusterHDFSValidFolderListFilePathAndName = srcClusterHDFSValidFolderListFilePathAndName;
	}

	public String getSrcClusterHDFSValidFolderInfoFilePathAndName() {
		return srcClusterHDFSValidFolderInfoFilePathAndName;
	}

	public void setSrcClusterHDFSValidFolderInfoFilePathAndName(
			String srcClusterHDFSValidFolderInfoFilePathAndName) {
		this.srcClusterHDFSValidFolderInfoFilePathAndName = srcClusterHDFSValidFolderInfoFilePathAndName;
	}
	
	public int getCurrSrcHdfsValidFolderNumber() {
		return currSrcHdfsValidFolderNumber;
	}

	public void setCurrSrcHdfsValidFolderNumber(int currSrcHdfsValidFolderNumber) {
		this.currSrcHdfsValidFolderNumber = currSrcHdfsValidFolderNumber;
	}

	public String getSrcClusterHDFSValidFolderIndexPairFilePathAndName() {
		return srcClusterHDFSValidFolderIndexPairFilePathAndName;
	}

	public void setSrcClusterHDFSValidFolderIndexPairFilePathAndName(
			String srcClusterHDFSValidFolderIndexPairFilePathAndName) {
		this.srcClusterHDFSValidFolderIndexPairFilePathAndName = srcClusterHDFSValidFolderIndexPairFilePathAndName;
	}

	public ArrayList<String> getCurrentClusterSrcHdfsValidFolderIndexPairList() {
		return currentClusterSrcHdfsValidFolderIndexPairList;
	}

	public void setCurrentClusterSrcHdfsValidFolderIndexPairList(
			ArrayList<String> currentClusterSrcHdfsValidFolderIndexPairList) {
		this.currentClusterSrcHdfsValidFolderIndexPairList = currentClusterSrcHdfsValidFolderIndexPairList;
	}	
	
	
}//end class
