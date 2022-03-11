package Kerberos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class FileCount2 {

	public static void main(String[] args) {
		System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5.ini");
		String user = "hdfs";
    	String bdHdfsVNnIPAddressAndPort = "hdfs://hdp003-nn:8020"; //"hdfs://hdp003-nn:8020"..."hdfs://hdpr03mn02:8020";
		String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;	
		//URI uriToHdfs = URI.create(hdfsURL);
		
		Configuration conf = new Configuration();
    	conf.set("fs.defaultFS", hdfsURL);
		conf.set("dfs.support.append", "true");
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("hadoop.security.authentication", "kerberos");            
        
        String nn1ServiceState = "";
        try { 
        	UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",	
        										"C:\\Windows\\hdfs_test_BDDev.keytab"); 
			
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
	        
	        String krbNnPrincipal = "";	
	         if (nn1ServiceState.equalsIgnoreCase("active")){
	        	  krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";	
	         } else {
	        	 krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";
	         }
	         System.out.println("\n***krbNnPrincipal: " + krbNnPrincipal);
	         conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal);
	         UserGroupInformation.setConfiguration(conf);  
	           
	   
	         String currSrcHdfsFilePathAndName = "/mr-history/tmp";
	         FileSystem currHadoopFS = FileSystem.get(conf);
	         //FileSystem currHadoopFS = FileSystem.get(uriToHdfs, conf, user);
	         FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
	         //FileStatus[] status = fs.listStatus(new Path("hdfs://hdp002-nn:8020" + path));
	         System.out.println("\n***File/Folder Count: " + status.length);

         } catch (Exception e) {
            e.printStackTrace();
         }//end try
        	    	        
         
	}

}
