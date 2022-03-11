package Kerberos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Knox_GatewayShell_Trail {

	public static void main(String[] args) {
		//kinit WA00121 (jdt5cgh);curl i -v --negotiate -u : -L 'http://hdpr03mn02.mayo.edu:50070/webhdfs/v1/data/test/HDFS/dcUatDataFile_No1.txt?op=OPEN'; (Good)

				String nn1ServiceState = "";
		        try {    				
					String nn1StatusQueryURI = "http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
					System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
					
					URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
			        URLConnection myConn = aWebSiteURL.openConnection();
			        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
			        String inputLine;
			       
			        while ((inputLine = bReader.readLine()) != null){	        	
			            if (inputLine.contains("\"State\" :") ){	    		            	
			            	inputLine = inputLine.replace("\"State\" :", "");
			            	nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
			            }
			        }
			        bReader.close();
			        System.out.println("\n*** nn1ServiceState: " + nn1ServiceState );
		        } catch (Exception e) {
		            e.printStackTrace();
		        }//end try
		        
		        String activeWebHdfsRestURL  = "";
		        if (nn1ServiceState.equalsIgnoreCase("active")){
		        	//krbNnPrincipal = "nn/hdpr02mn01@MAYOHADOOPTEST1.COM";	
		        	activeWebHdfsRestURL  = "http://hdpr03mn01.mayo.edu:50070/webhdfs";
		        } else {
		        	//krbNnPrincipal = "nn/hdpr02mn02@MAYOHADOOPTEST1.COM";
		        	activeWebHdfsRestURL  = "http://hdpr03mn02.mayo.edu:50070/webhdfs";
		        }
		        System.out.println("\n*** activeWebHdfsRestURL: " + activeWebHdfsRestURL );
				
		        String gateway = activeWebHdfsRestURL;
		        String username = "WA00121";
		        String password = "jdt5cgh";
		        
//		        try {
//					Hadoop session = Hadoop.login( gateway, username, password );
//					String text = Hdfs.get( session ).from( "/user/guest/example/README" ).now().string;
//					
//				} catch (URISyntaxException e) {			
//					e.printStackTrace();
//				}


	}

}
