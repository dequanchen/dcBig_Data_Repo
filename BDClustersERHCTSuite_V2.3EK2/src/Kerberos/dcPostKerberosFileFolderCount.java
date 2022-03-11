package Kerberos;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import dcModelClasses.HdfsUtil;

public class dcPostKerberosFileFolderCount {

	 @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException, InterruptedException {
		System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5.ini");

		String user = "hdfs";
    	String bdHdfsVNnIPAddressAndPort = "hdfs://hdp003-nn:8020"; //"hdfs://hdp003-nn:8020"..."hdfs://hdpr03mn02:8020";
		String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;	
		//URI uriToHdfs = URI.create(hdfsURL);
		
		final Configuration conf = new Configuration();
    	conf.set("fs.defaultFS", hdfsURL);
		conf.set("dfs.support.append", "true");
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("hadoop.security.authentication", "kerberos"); 	        
		
        
        String nn1ServiceState = HdfsUtil.getNameNode1ServiceStateByWeb("BDDev");
        String krbNnPrincipal = "";	
        if (nn1ServiceState.equalsIgnoreCase("active")){
        	 krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";	
        } else {
        	krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";
        }
        //krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";
		conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal); 
		UserGroupInformation.setConfiguration(conf); 
	    UserGroupInformation ugi = UserGroupInformation
	        		.loginUserFromKeytabAndReturnUGI("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
	    					"C:\\Windows\\hdfs_test_BDDev.keytab"); 
	    ugi.doAs( new PrivilegedExceptionAction() {
	            public Void run() throws Exception {
	    	        String currSrcHdfsFilePathAndName = "/mr-history/tmp";

	                FileSystem currHadoopFS = FileSystem.get(conf);
	                FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
	                //FileStatus[] status = fs.listStatus(new Path("hdfs://hdp002-nn:8020" + path));
	                System.out.println("\n***File/Folder Count: " + status.length);

	                return null;
	            }
	    } );
	    
	}

}
