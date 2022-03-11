package dcModelClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BDProd1HBase {
	private String bdClusterName = "BDProd";	
	private ArrayList<String> currentClusterSrcHbaseValidTableList = new  ArrayList<String>();
	private String hdfsValidTableListSrcStatus = "";
	private String srcClusterHBaseValidTableListFilePathAndName = "";
	private String srcClusterHBaseValidTableInfoFilePathAndName = "";
	private String srcClusterHBaseValidTableCreationCmdsFilePathAndName = "";
	private int currSrcHbaseValidTableNumber = 0;

	public BDProd1HBase(String aHbaseValidTableListSrcStatus) { //BDProd1HBase
		this.hdfsValidTableListSrcStatus = aHbaseValidTableListSrcStatus;
		if (this.hdfsValidTableListSrcStatus.equalsIgnoreCase("Static")){
			this.srcClusterHBaseValidTableListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableList_Static.txt";
			this.srcClusterHBaseValidTableInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableInfo_Static.txt";
			this.srcClusterHBaseValidTableCreationCmdsFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableCreationCmds_Static.txt";
		}
		if (this.hdfsValidTableListSrcStatus.equalsIgnoreCase("Dynamic")){
			this.srcClusterHBaseValidTableListFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableList_Dynamic.txt";
			this.srcClusterHBaseValidTableInfoFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableInfo_Dynamic.txt";
			this.srcClusterHBaseValidTableCreationCmdsFilePathAndName = "C:\\BD\\BDProd1_DataBkp\\"+ this.bdClusterName  + "\\HBase\\" + "dcBDProd1_HBase_ValidTableCreationCmds_Dynamic.txt";
		}
		
		obtainCurrentClusterValidHbaseFolderList(this.srcClusterHBaseValidTableListFilePathAndName);
	}
	
	private void obtainCurrentClusterValidHbaseFolderList (String srcClusterHBaseValidTableListFilePathAndName){
		File aFile = new File (srcClusterHBaseValidTableListFilePathAndName);
		if (!aFile.exists()){
			System.out.println("\n*** File Not Existing - srcClusterHBaseValidTableListFilePathAndName: " + srcClusterHBaseValidTableListFilePathAndName);
			return;
		}
		if (!srcClusterHBaseValidTableListFilePathAndName.isEmpty()){
			//InputStream in = BDProd1HBase.class.getResourceAsStream(srcClusterHBaseValidTableListFilePathAndName);		
			//InputStreamReader inStrReader = new InputStreamReader(in);
			//BufferedReader br = new BufferedReader(inStrReader);
								
			try {
				FileReader aFileReader = new FileReader(srcClusterHBaseValidTableListFilePathAndName);
				BufferedReader br = new BufferedReader(aFileReader);
				
				String line = "";
				int addCount = 0;
				while ( (line = br.readLine()) != null) {
					if (!line.contains("#")){
						if (!line.isEmpty()){
							addCount++;
							this.currentClusterSrcHbaseValidTableList.add(line.trim());	
							//addCount++;
							//System.out.println("*-* (" + addCount + ") added a entry node: " + line);
						}							
					}													            
				}
				this.currSrcHbaseValidTableNumber = addCount;
				br.close();
				//inStrReader.close();
				//in.close();
				aFileReader.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}		
	}//end obtainCurrentClusterValidHbaseFolderList

	public String getBdClusterName() {
		return bdClusterName;
	}

	public void setBdClusterName(String bdClusterName) {
		this.bdClusterName = bdClusterName;
	}

	public ArrayList<String> getCurrentClusterSrcHbaseValidTableList() {
		return currentClusterSrcHbaseValidTableList;
	}

	public void setCurrentClusterSrcHbaseValidTableList(
			ArrayList<String> currentClusterSrcHbaseValidTableList) {
		this.currentClusterSrcHbaseValidTableList = currentClusterSrcHbaseValidTableList;
	}

	public String getHbaseValidTableListSrcStatus() {
		return hdfsValidTableListSrcStatus;
	}

	public void setHbaseValidTableListSrcStatus(String hdfsValidTableListSrcStatus) {
		this.hdfsValidTableListSrcStatus = hdfsValidTableListSrcStatus;
	}

	public String getSrcClusterHBaseValidTableListFilePathAndName() {
		return srcClusterHBaseValidTableListFilePathAndName;
	}

	public void setSrcClusterHBaseValidTableListFilePathAndName(
			String srcClusterHBaseValidTableListFilePathAndName) {
		this.srcClusterHBaseValidTableListFilePathAndName = srcClusterHBaseValidTableListFilePathAndName;
	}

	public String getSrcClusterHBaseValidTableInfoFilePathAndName() {
		return srcClusterHBaseValidTableInfoFilePathAndName;
	}

	public void setSrcClusterHBaseValidTableInfoFilePathAndName(
			String srcClusterHBaseValidTableInfoFilePathAndName) {
		this.srcClusterHBaseValidTableInfoFilePathAndName = srcClusterHBaseValidTableInfoFilePathAndName;
	}
	
	public int getCurrSrcHbaseValidTableNumber() {
		return currSrcHbaseValidTableNumber;
	}

	public void setCurrSrcHbaseValidTableNumber(int currSrcHbaseValidTableNumber) {
		this.currSrcHbaseValidTableNumber = currSrcHbaseValidTableNumber;
	}

	public String getSrcClusterHBaseValidTableCreationCmdsFilePathAndName() {
		return srcClusterHBaseValidTableCreationCmdsFilePathAndName;
	}

	public void setSrcClusterHBaseValidTableCreationCmdsFilePathAndName(
			String srcClusterHBaseValidTableCreationCmdsFilePathAndName) {
		this.srcClusterHBaseValidTableCreationCmdsFilePathAndName = srcClusterHBaseValidTableCreationCmdsFilePathAndName;
	}
		
}//end class
