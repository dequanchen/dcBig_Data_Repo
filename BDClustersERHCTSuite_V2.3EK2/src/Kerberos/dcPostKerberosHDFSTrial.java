package Kerberos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import dcModelClasses.HdfsUtil;

public class dcPostKerberosHDFSTrial {

	public static void main(String[] args) {
		//-Dsun.security.krb5.debug=true 
		//-Dsun.security.spnego.debug=true 
		//-Djavax.net.debug=all
		//-Djava.net.preferIPv4Stack=true
		
//		String userName = "xxx@xx.xxx";
//		char[] password = "xxxxx".toCharArray();
//		final String dir= "/USER/xxx/DIR1";
//		System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("kerberos_sample.conf").toExternalForm());
//		System.setProperty("java.security.krb5.realm", "xxxxxx");
//		System.setProperty("java.security.krb5.kdc", "xxxxxx");
//		System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
//		System.setProperty("HADOOP_USER_NAME", "xxx");
//		String username = "hdfs@hdpr02mn02.mayo.edu";
//		char[] password = "hdfs4int".toCharArray();
//		try {
//			LoginContext lc = new LoginContext("primaryLoginContext", new UserNamePasswordCallbackHandler(username, password));
//			lc.login();
//			UserGroupInformation ugi = UserGroupInformation.getLoginUser();
//			ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
//			final Configuration conf = new Configuration();
//			conf.set("fs.default.name", "xxxxxxxx");
//			conf.set("hadoop.security.authentication", "kerberos");
//			conf.set("dfs.namenode.kerberos.principal", "nn/xxx@xxxx");
//			ugi.setConfiguration(conf);
//			ugi.doAs(new PrivilegedExceptionAction<Void>() {
//			public Void run() throws Exception {
//			FileSystem fs = FileSystem.get(conf);
//			Path path = new Path(dir);
//			if (fs.exists(path)) {
//			System.out.println("Dir " + dir + " already exists!");
//			return null;
//		}
//		fs.mkdirs(path);
//		fs.close();
//		return null;
//		}
//		});
//		} catch (Exception le) {
//		le.printStackTrace();
//		}
		
		
		try {
			//String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
			//System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
			//FileSystem currHadoopFS = getHadoopFileSystem (uriToHdfs, conf, user);
			//System.setProperty("java.security.krb5.conf", "C:\\Windows\\krb5.ini"); //"C:\\Windows\\krb5.ini...hdpr03mn02.mayo.edu:/etc/krb5.conf");
			//System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
			//System.setProperty("java.security.krb5.realm", "MAYOHADOOPDEV1.COM");
			//System.setProperty("java.security.krb5.kdc", "hdpr03mn02.mayo.edu:88");
			//System.setProperty("java.security.auth.login.config", "C:\\dev\\ecliplse-workspaces\\BigData\\mayo-bigdata-api\\src\\main\\resources\\hbase_client_jaas.conf");

			//System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
			//System.setProperty("sun.security.krb5.debug", "true");
			
			String user = "hdfs"; //hdfs..hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM
			String password = "";
			
			String bdHdfsVNnIPAddressAndPort = "hdfs://hdp003-nn:8020" ;//"hdfs://hdpr03mn02.mayo.edu:8020";	
			//String bdHdfsVNnIPAddressAndPort = "hdfs://hdp02-nn:8020";
			String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;
			URI uriToHdfs = URI.create(hdfsURL);
			
			Configuration conf = new Configuration();
			
			//conf.set("fs.defaultFS", "hdfs://1.2.3.4:8020/user/hbase");
			conf.set("fs.defaultFS", hdfsURL);
			conf.set("dfs.support.append", "true");
	        conf.set("dfs.client.use.datanode.hostname", "true");
			
			conf.set("hadoop.security.authentication", "kerberos");
			//conf.set("hdfs.security.authentication", "kerberos");
	        //String krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";	
			//conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal); //"nn/hdpr03mn02@MAYOHADOOPDEV1.COM...krbtgt/MAYOHADOOPDEV1.COM@MAYOHADOOPDEV1.COM");
			//conf.set("dfs.datanode.kerberos.principal", "dn/_HOST@MAYOHADOOPDEV1.COM");	
				
	        String nn1ServiceState = HdfsUtil.getNameNode1ServiceStateByWeb("BDDev");
	        String krbNnPrincipal = "";	
	        if (nn1ServiceState.equalsIgnoreCase("active")){
	        	 krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";	
	        } else {
	        	krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";
	        }
	        //krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";
			conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal); 
			
			//conf.set("dfs.namenode.kerberos.principal", "nn/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM");	
			//conf.set("dfs.datanode.kerberos.principal", "dn/_HOST@MAYOHADOOPTEST1.COM");	
			
			//UserGroupInformation ugi = UserGroupInformation.getLoginUser();
			//ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
			
			
//			<property>
//		      <name>dfs.namenode.kerberos.principal</name>
//		      <value>nn/_HOST@MAYOHADOOPTEST1.COM</value>
//		    </property>
//			
//			 <property>
//		      <name>dfs.datanode.kerberos.principal</name>
//		      <value>dn/_HOST@MAYOHADOOPTEST1.COM</value>
//		    </property>
		    
		    
			UserGroupInformation.setConfiguration(conf);
			//UserGroupInformation.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
			//UserGroupInformation.loginUserFromKeytab("example_user@IBM.COM", "/path/to/example_user.keytab");
//			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM", //@MAYOHADOOPTEST1.COM
//						"C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\hdfs_extern.keytab");
			
//			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM", //@MAYOHADOOPTEST1.COM
//					"C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Dev\\hdfs_test.keytab");
//			
			
			
//			UserGroupInformation.loginUserFromKeytab("nn/hdpr02mn02@MAYOHADOOPTEST1.COM", //@MAYOHADOOPTEST1.COM
//					"C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\nn.service.keytab");
//			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
//					"C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Dev\\hdfs_test.keytab");   //hdfs@MAYOHADOOPDEV1.COM..hdfs@MAYOHADOOPTEST1.COM
			
//			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
//					"C:\\Windows\\hdfs_test.keytab"); 
//			
//			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
//					"../BDApplianceERHCTSuite_2.1k/src/dcModelClasses/ApplianceEntryNodes/hdfs_test_BDDev.keytab");
			
			String keytabFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("hdfs_test_BDDev.keytab").getPath();
			UserGroupInformation.loginUserFromKeytab("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
					keytabFilePathAndName);
			
			FileSystem currHadoopFS = FileSystem.get(conf);
			
			//FileSystem currHadoopFS = FileSystem.get(conf);	
			
			String currSrcHdfsFilePathAndName = "/concV.properties";
			System.out.println("\nIn the HDFS file: " + currSrcHdfsFilePathAndName);
			
			//InputStreamReader inSReader = new InputStreamReader(currHadoopFS.open(new Path(currSrcHdfsFilePathAndName)));
			
			FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
			BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()){
					System.out.println("*** line: " + line );
				}
															
			}//end while
			br.close(); 
			
			
//			UserGroupInformation ugi = UserGroupInformation.createRemoteUser(user);
//			ugi.doAs(new PrivilegedExceptionAction<Void>() {
//	                public Void run() throws Exception {
//			    	
//					String bdHdfsVNnIPAddressAndPort = "hdfs://hdp002-nn:8020";	
//					String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;
//					URI uriToHdfs = URI.create(hdfsURL);
//					
//					Configuration conf = new Configuration();
//					
//					//conf.set("fs.defaultFS", "hdfs://1.2.3.4:8020/user/hbase");
//					conf.set("fs.defaultFS", hdfsURL);
//					//conf.set("dfs.support.append", "true");
//			        //conf.set("dfs.client.use.datanode.hostname", "true");
//					//conf.set("hadoop.job.ugi", "hbase");	
//					conf.set("hadoop.security.authentication", "Kerberos");
//					
//					FileSystem currHadoopFS = FileSystem.get(conf);	
//					
//					String currSrcHdfsFilePathAndName = "/data/test/Flume/dcFlumeTestData.1449090807684";
//					
//					//InputStreamReader inSReader = new InputStreamReader(currHadoopFS.open(new Path(currSrcHdfsFilePathAndName)));
//					
//					FileStatus[] status = currHadoopFS.listStatus(new Path(currSrcHdfsFilePathAndName));				
//					BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
//					String line = "";
//					
//					while ((line = br.readLine()) != null) {
//						System.out.println("*** line: " + line );											
//					}//end while
//					br.close(); 
//					
//					return null;
//					
//			    }
//			    
//			});
//			
			
      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end main
	
	public static FileSystem getHadoopFileSystem(final URI uri, final Configuration conf,final String user) 
			                                                throws IOException, InterruptedException {
		UserGroupInformation ugi = null;
		if (user == null) {
			ugi=UserGroupInformation.getCurrentUser();
		} else {
			ugi=UserGroupInformation.createRemoteUser(user);
		}		
		
		  
		return ugi.doAs(new PrivilegedExceptionAction<FileSystem>(){
		    public FileSystem run() throws IOException, InterruptedException {
		    	return FileSystem.get(uri, conf, user);		      
		    }
		  }
		);
	}//end getHadoopFileSystem

}//end class


