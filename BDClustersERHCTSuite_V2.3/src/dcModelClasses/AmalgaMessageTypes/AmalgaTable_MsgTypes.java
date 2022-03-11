package dcModelClasses.AmalgaMessageTypes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Author: Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 9/29/2014 
 */

public class AmalgaTable_MsgTypes {
	private String msgTypeListFilePathAndName = "";
	private ArrayList<String> currTableMsgTypeList = new  ArrayList<String>(); 

	public AmalgaTable_MsgTypes(String amalgaTableIndicator ) {
		//Note: aMsgTypeListFilePathAndName must be in the same package as class - MC_BDAppliance_EntryNodes  
		//String aMsgTypeListFilePathAndName ="AmalgArchiveTable_MsgTypeList.txt";
		if (amalgaTableIndicator.equalsIgnoreCase("ArchiveTable")
				|| amalgaTableIndicator.equalsIgnoreCase("Archive Table")
				|| amalgaTableIndicator.equalsIgnoreCase("Archive")){
			this.msgTypeListFilePathAndName = "dcAmalgArchiveTable_MsgTypeList.txt";
		}
		
		if (amalgaTableIndicator.equalsIgnoreCase("GapTable")
				||amalgaTableIndicator.equalsIgnoreCase("Gap Table")
				||amalgaTableIndicator.equalsIgnoreCase("Gap")){
			this.msgTypeListFilePathAndName = "dcAmalgGapTable_MsgTypeList.txt";
		}
		if (amalgaTableIndicator.equalsIgnoreCase("CurrentArchive") 
				|| amalgaTableIndicator.equalsIgnoreCase("Current Archive") 
				|| amalgaTableIndicator.equalsIgnoreCase("CurrArchive")){
			this.msgTypeListFilePathAndName = "dcAmalgCurrArchiveTable_MsgTypeList.txt";
		}
		if (amalgaTableIndicator.equalsIgnoreCase("CurrentTable") 
				|| amalgaTableIndicator.equalsIgnoreCase("Current Table") 
				|| amalgaTableIndicator.equalsIgnoreCase("CurrTable")
				|| amalgaTableIndicator.equalsIgnoreCase("Current")){
			this.msgTypeListFilePathAndName = "dcAmalgCurrentTable_MsgTypeList.txt";
		}
		
		if (!this.msgTypeListFilePathAndName.isEmpty())	{
			InputStream in = AmalgaTable_MsgTypes.class.getResourceAsStream(this.msgTypeListFilePathAndName);		
			InputStreamReader inStrReader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inStrReader);
					
			try {			
				
				String line = "";
				//int addCount = 0;
				while ( (line = br.readLine()) != null) {
					this.currTableMsgTypeList.add(line);
					//addCount++;
					//System.out.println("*-* (" + addCount + ") added a MsgType: " + line);									            
				}
				br.close();
				inStrReader.close();
				in.close();			
			} catch (IOException e) {			
				e.printStackTrace();
			}//end while
		}
		
	}//end constructor

	public String getMsgTypeListFilePathAndName() {
		return msgTypeListFilePathAndName;
	}

	public ArrayList<String> getCurrTableMsgTypeList() {
		return currTableMsgTypeList;
	}

	public void setMsgTypeListFilePathAndName(String msgTypeListFilePathAndName) {
		this.msgTypeListFilePathAndName = msgTypeListFilePathAndName;
	}

	public void setCurrTableMsgTypeList(ArrayList<String> currTableMsgTypeList) {
		this.currTableMsgTypeList = currTableMsgTypeList;
	}
	
	
	

}//end class

