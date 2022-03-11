package OS_Hardening;

import java.io.IOException;

import dcModelClasses.HdfsUtil;
import dcModelClasses.ApplianceEntryNodes.BdCluster;

public class dcActiveRMTrial {
	private static String bdClusterName = "BDDev"; //BDDev..BDProd2..BDSdbx

	public static void main(String[] args) throws IOException {
		String rm1ServiceState = HdfsUtil.getResourceManager1ServiceStateByWeb(bdClusterName);		
		System.out.println("\n ***  '" + bdClusterName + "' rm1ServiceState: " + rm1ServiceState);
		
		BdCluster currBDCluster = new BdCluster(bdClusterName);
		String activeRMIPAddressAndPort = currBDCluster.getBdClusterActiveRMIPAddressAndPort();
		System.out.println("\n ***  '" + bdClusterName + "' activeRMIPAddressAndPort: " + activeRMIPAddressAndPort);
		
		String hdfsActiveNNIPAddressAndPort = currBDCluster.getBdHdfsActiveNnIPAddressAndPort();
		System.out.println("\n ***  '" + bdClusterName + "' hdfsActiveNNIPAddressAndPort: " + hdfsActiveNNIPAddressAndPort);
		
//		String rm1StatusQueryURI = "http://hdpr04mn01.mayo.edu:8088/ws/v1/cluster/info";
//		System.out.println("\n *** BDDev rm1StatusQueryURI: " + rm1StatusQueryURI);
//		String rm1ServiceState = "standby";
//		 
//		URL aWebSiteURL = new URL(rm1StatusQueryURI);	     
//        URLConnection myConn = aWebSiteURL.openConnection();
//        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
//        String inputLine;       
//        while ((inputLine = bReader.readLine()) != null){
//        	//System.out.println(inputLine.trim());
//        	
//        	//JSONObject currJsonObject = new JSONObject(inputLine.trim());        	
//        	//String rm1Status = currJsonObject.getString("clusterInfo").trim();
//			//System.out.println("\n*** rm1Status: " + rm1Status );
//        	
//        	
//            if (inputLine.contains("\"haState\":\"") ){
//            	System.out.println(inputLine.trim());
//            	String[] lineSplit = inputLine.trim().split("\"haState\":\"");
//            	String tgtStr = lineSplit[1].trim();
//            	if (tgtStr.contains("\",\"")){
//            		String[] tgtStrSplit = tgtStr.split("\",\"");
//            		rm1ServiceState = tgtStrSplit[0].toLowerCase();
//            	}            	
//            	
//            }
//        }
//        bReader.close();
//        
//        System.out.println("\n *** BDDev rm1ServiceState: " + rm1ServiceState);

	}

}
