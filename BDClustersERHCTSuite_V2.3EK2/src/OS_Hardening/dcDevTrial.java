package OS_Hardening;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcDevTrial {
	private static String bdClusterName = "BDDev"; //BDDev..BDSdbx..BDInt...BDProd2...BDProd...BDInt2
	private static String solrInstalledNodeName = "DN02";

	public static void main(String[] args) throws Exception {
		BdCluster currBdCluster = new BdCluster(bdClusterName);		
		System.out.println(" *** bdClusterName: " + bdClusterName);
		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		
		String hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName = "/user/m041785/test/Solr/solr_curl_query_result1.txt";
		
		Path filePath = new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName);
		boolean solrStatusHdfsFileExistingStatus = false;
		if (currHadoopFS.exists(filePath)) {			
			solrStatusHdfsFileExistingStatus = true;				
		}
		System.out.println("\n*** Exisiting status for " + hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName + ": " + solrStatusHdfsFileExistingStatus);
		
		String targetFoundString = "90000,125000,Texas,120000,45500,250000,110000,140000,7,3,113642.85714285714,";
		boolean solrSearchingSuccessStatus = false;			
		if (solrStatusHdfsFileExistingStatus){ 
			try {							
				FileStatus[] status = currHadoopFS.listStatus(new Path(hdfsSolrTestCollectionSearchingStatusFileNameFilePathAndName));				
				BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
				String line = "";				
				while ((line = br.readLine()) != null) {
					System.out.println("*** line: " + line );
					if (line.contains(targetFoundString)) {												
						solrSearchingSuccessStatus = true;					
					}						
				}//end while
				br.close();				
							
			} catch (IOException e) {				
				e.printStackTrace();				
			}//end try   
		}//end if			
		
		System.out.println("*** solrSearchingSuccessStatus: " + solrSearchingSuccessStatus );
		
//		String solrInstalledNodeServiceUpStatus = HdfsUtil.getSolrInstalledNodeServiceStateByWeb(bdClusterName, solrInstalledNodeName, "secured"); //secured..unsecured
//		System.out.println("\n *** solrInstalledNodeServiceUpStatus: " + solrInstalledNodeServiceUpStatus);
		
//		BdNode tempSolrBDNode = new BdNode(solrInstalledNodeName, bdClusterName);
//		ULServerCommandFactory bdSolrNodeCmdFactory = tempSolrBDNode.getBdENCmdFactory();			
//		String solrRestServiceIpAddressAndPort_unSecure = "http://" + bdSolrNodeCmdFactory.getServerURI()+ ":8983/solr";
//		System.out.println(" *** solrRestServiceIpAddressAndPort_unSecure: " + solrRestServiceIpAddressAndPort_unSecure);
//		
//		String solrInstalledNodeStatusQueryURI = "https://" + bdSolrNodeCmdFactory.getServerURI() + ":8983/solr/admin/collections?action=LIST&indent=true";
//		System.out.println("\n *** solrInstalledNodeStatusQueryURI: " + solrInstalledNodeStatusQueryURI);
//		
//		String querySolrNodeServiceState = "down";
//		try {
//			URL aWebSiteURL = new URL(solrInstalledNodeStatusQueryURI);	     
//	        URLConnection myConn = aWebSiteURL.openConnection();
//	        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
//	        String inputLine;	        
//	        while ((inputLine = bReader.readLine()) != null){	        	
//	            if (inputLine.contains("<int name=\"status\">") ){
//	            	//System.out.println(inputLine);
//	            	//inputLine = inputLine.replace("<int name=\"status\">", "").replace("</int>", "");
//	            	if (inputLine.contains("<int name=\"status\">0</int>")){
//	            		//System.out.println(inputLine.trim());
//	            		querySolrNodeServiceState = "up";
//	            	};
//	            }
//	        }
//	        bReader.close();
//	       
//		} catch (Exception e) {				
//			//e.printStackTrace();
//		}//end try
//		
//		System.out.println("\n *** querySolrNodeServiceState: " + querySolrNodeServiceState);
		   
		
//		ArrayList<String> currentClusterSolrInstalledNodeList = currBdCluster.getCurrentClusterSolrInstalledNodeList();	
//		
//		int nodeCount = 0;
//		for (String solrInstalledNodeName : currentClusterSolrInstalledNodeList){
//			nodeCount++;
//			System.out.println("***(" + nodeCount + ") solrInstalledNodeName: " + solrInstalledNodeName);
//			BdNode aBDNode = new BdNode(solrInstalledNodeName, bdClusterName);
//			//System.out.println(" *** aBDNode.getBdClusterName(): " + aBDNode.getBdClusterName());		
//			
//			ULServerCommandFactory bdENCmdFactory = aBDNode.getBdENCmdFactory();
//			//ULServerCommandFactory bdENRootCmdFactory = aBDNode.getBdENRootCmdFactory();
//			System.out.println(" *** bdENCmdFactory.getBdClusterName(): " + bdENCmdFactory.getBdClusterName());
//			System.out.println(" *** bdENCmdFactory.getServerURI(): " + bdENCmdFactory.getServerURI());
//			
//		}
//		
//		String activeNNAddr_port = currBdCluster.getBdHdfsVNnIPAddressAndPort();
//		System.out.println(" *** activeNNAddr_port: " + activeNNAddr_port);
		
//		String fileName = "dcPayload_large_json_10876kB.txt";
//		String[] fileNameSplit = fileName.split("_");
//		int fileNameSplitLength = fileNameSplit.length;
//		String fileSizeInKB = fileNameSplit[fileNameSplitLength-1].replace(".txt", "").toLowerCase().replace("kb", "");
//		System.out.println(" *** fileSizeInKB: " + fileSizeInKB);

	}

}
