package Kerberos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
//import org.apache.hadoop.hdfs.server.namenode1.ha*; 

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 02/02/2016 
*/ 

public class FileCount_BDInt_WebHDFS {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(final String[] args) throws IOException, FileNotFoundException, InterruptedException{
		 
		    System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5_BDInt.conf"); //krb5.ini
		    //System.setProperty("sun.security.krb5.debug", "true");
		    //System.setProperty("java.net.preferIPv4Stack", "true");
		    
		    String nn1ServiceState = "";
	        try {    				
				String nn1StatusQueryURI = "http://hdpr02mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
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
	        	    	        
	        //String krbNnPrincipal = "";
	        String webHdfsURL  = "";
	        if (nn1ServiceState.equalsIgnoreCase("active")){
	        	//krbNnPrincipal = "nn/hdpr02mn01@MAYOHADOOPTEST1.COM";	
	        	webHdfsURL  = "webhdfs://hdpr02mn01:50070";
	        } else {
	        	//krbNnPrincipal = "nn/hdpr02mn02@MAYOHADOOPTEST1.COM";
	        	webHdfsURL  = "webhdfs://hdpr02mn01:50070";
	        }
	        //System.out.println("\n***krbNnPrincipal: " + krbNnPrincipal);
	        System.out.println("\n***webHdfsURL: " + webHdfsURL + "\n");
	         
	        final Configuration conf = new Configuration();	       
        	conf.set("fs.defaultFS", webHdfsURL);
        	conf.set("hadoop.security.authentication", "kerberos");
			//conf.set("dfs.support.append", "true");
	        //conf.set("dfs.client.use.datanode.hostname", "true");	        	
	        //conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal);
	         
	         
	        UserGroupInformation.setConfiguration(conf);
	        UserGroupInformation ugi = UserGroupInformation
	        		.loginUserFromKeytabAndReturnUGI("hdfs/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM",
	    					"C:\\Windows\\hdfs_test_BDInt.keytab"); //hdfs_test_BDInt.keytab
	        ugi.doAs( new PrivilegedExceptionAction() {
	              public Void run() throws Exception {
	    	         String currSrcHdfsFilePathAndName = "/data/test/";
	    	         FileSystem  currWebHdfsFS = WebHdfsFileSystem.get(conf);	    	         
	                 FileStatus[] status = currWebHdfsFS.listStatus(new Path(currSrcHdfsFilePathAndName));              
	                 System.out.println("\n**1** File/Folder Count for " + currSrcHdfsFilePathAndName + ": " + status.length);
	                 
	                 currSrcHdfsFilePathAndName = "/user/m041785/employee1/part-r-00000";
	         		 System.out.println("\n\n**2** In the HDFS file - " + currSrcHdfsFilePathAndName + ": ");	         		 
	         		 BufferedReader br = new BufferedReader(new InputStreamReader(currWebHdfsFS.open(new Path(currSrcHdfsFilePathAndName))));
	    			 String line = "";	    			
	    			 while ((line = br.readLine()) != null) {
	    				if (!line.isEmpty()){
	    					System.out.println("  " + line );
	    				}
	    															
	    			 }//end while
	    			 br.close(); 

	                 return null;
	              }
	        } );
	 }

}
