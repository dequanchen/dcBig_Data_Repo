package Kerberos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
//import org.apache.hadoop.hdfs.server.namenode1.ha*; 

import dcModelClasses.HdfsUtil;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 2/29/2016
*/ 

public class FileCountReading_BDDev_EK {
	//http://stackoverflow.com/questions/14399831/how-to-get-hadoop-client-to-user-correct-credentials-in-a-secure-kerberos-clus
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(final String[] args) throws IOException, FileNotFoundException, InterruptedException{
		 	String bdClusterName = "BDDev";
		
		    System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\dcKerberos\\krb5_BDDev_EK.conf"); //krb5.ini...krb5_BDInt.conf..krb5.conf
		    //System.setProperty("sun.security.krb5.debug", "true");
		    //System.setProperty("java.net.preferIPv4Stack", "true");
		    System.setProperty("hadoop.home.dir", "C:\\winutil\\");
		    
		    		
		    String user = "hdfs";
		    String hdfsURL = "";
		    String nn1ServiceState = HdfsUtil.getNameNode1ServiceStateByWeb(bdClusterName);
			if (nn1ServiceState.equalsIgnoreCase("active")){
				hdfsURL = "hdfs://hdpr03mn01.mayo.edu:8020" + "?user=" + user;
				System.out.println("The Active Name Node is: hdpr03mn01.mayo.edu on " + bdClusterName + " Hadoop Cluster");
	        } else {
	        	hdfsURL = "hdfs://hdpr03mn02.mayo.edu:8020" + "?user=" + user;
	        	System.out.println("The Active Name Node is: hdpr03mn02.mayo.edu on " + bdClusterName + " Hadoop Cluster");
	        }
			
									
			final Configuration conf = new Configuration();
        	conf.set("fs.defaultFS", hdfsURL);
			conf.set("dfs.support.append", "true");
	        conf.set("dfs.client.use.datanode.hostname", "true");
	        conf.set("hadoop.security.authentication", "kerberos");	
	       
	               	    	        
	         String krbNnPrincipal = "";	
	         if (nn1ServiceState.equalsIgnoreCase("active")){
	        	  krbNnPrincipal = " nn/hdpr03mn01.mayo.edu@MFAD.MFROOT.ORG";	
	         } else {
	        	 krbNnPrincipal = " nn/hdpr03mn02.mayo.edu@MFAD.MFROOT.ORG";
	         }
	         System.out.println("\n***krbNnPrincipal: " + krbNnPrincipal);
	         conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal);
	         
	         UserGroupInformation.setConfiguration(conf);
	         UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("hdfs-MAYOHADOOPDEV1@MFAD.MFROOT.ORG",
	    					"C:\\Windows\\dcKerberos\\hdfs.headless_BDDev.keytab"); 
	         
	         ugi.doAs( new PrivilegedExceptionAction() {
	              public Void run() throws Exception {
	    	         String currSrcHdfsFilePathAndName = "/data/test/";
	                 FileSystem currHadoopFS = FileSystem.get(conf);	    	         
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

	                 return null;
	              }
	          } );
	 }

}
