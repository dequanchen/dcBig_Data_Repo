package Kerberos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;
//import org.apache.hadoop.hdfs.server.namenode.ha*; 


public class FileCountReading_BDDev {

	 @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(final String[] args) throws 
	 		IOException, FileNotFoundException, InterruptedException{
		 //1. For Non-Kerberized BDDev Cluster
		 String bdClusterName = "BDDev"; //BDProd..BDInt..BDSbx..BDDev		
		 BdCluster srcBdCluster = new BdCluster(bdClusterName);
		 FileSystem currHadoopFS  = srcBdCluster.getHadoopFS();
		 
		 String currSrcHdfsFilePathAndName = "/data/test/";
		 FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));              
         System.out.println("\n**1** File/Folder Count for " + currSrcHdfsFilePathAndName + ": " + status.length);
         
         currSrcHdfsFilePathAndName = "/data/test/Pig/employee1/part-r-00000";
 		 System.out.println("\n\n**2** In the HDFS file - " + currSrcHdfsFilePathAndName + ": ");	         		 
 		 BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(new Path(currSrcHdfsFilePathAndName))));
		 String line = "";	    			
		 while ((line = br.readLine()) != null) {
			if (!line.isEmpty()){
				System.out.println("  " + line );
			}
														
		 }//end while
		 br.close(); 
		 
//		 //2. For Kerberized BDDev Cluster
//		 System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5.ini");
//
//	        UserGroupInformation ugi = UserGroupInformation
//	        		.loginUserFromKeytabAndReturnUGI("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
//	    					"C:\\Windows\\hdfs_test_BDDev.keytab"); 
//	        ugi.doAs( new PrivilegedExceptionAction() {
//	            public Void run() throws Exception {
//	            	String user = "hdfs";
//	            	String bdHdfsVNnIPAddressAndPort = "hdfs://hdp003-nn:8020";
//	    			String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;	    			
//	    			
//	    			Configuration configuration = new Configuration();
//	            	configuration.set("fs.defaultFS", hdfsURL);
//	    			configuration.set("dfs.support.append", "true");
//	    	        configuration.set("dfs.client.use.datanode.hostname", "true");
//
//	    	        configuration.set("hadoop.security.authentication", "kerberos");	    			
//	    	        
//
//	    	        String nn1ServiceState = "standby";
//	    	        String krbNnPrincipal = "";	
//	    	        if (nn1ServiceState.equalsIgnoreCase("active")){
//	    	        	 krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";	
//	    	        } else {
//	    	        	krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";
//	    	        }
//	    			configuration.set("dfs.namenode.kerberos.principal", krbNnPrincipal);
//	    			
//	    	        
//	    	        String currSrcHdfsFilePathAndName = "/mr-history/tmp";
//
//	                FileSystem currHadoopFS = FileSystem.get(configuration);
//	                FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
//	                //FileStatus[] status = fs.listStatus(new Path("hdfs://hdp002-nn:8020" + path));
//	                System.out.println("File Count: " + status.length);
//
//	                return null;
//	            }
//	        } );
	    }//end main
}//end class
