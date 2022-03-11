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
import org.apache.hadoop.security.UserGroupInformation;
//import org.apache.hadoop.hdfs.server.namenode1.ha*; 


public class FileCount1 {

	 @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(final String[] args) throws IOException, FileNotFoundException, InterruptedException{
		 
		 System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5.ini");
		 System.setProperty("sun.security.krb5.debug", "true");

		 UserGroupInformation ugi = UserGroupInformation
	        		.loginUserFromKeytabAndReturnUGI("hdfs/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
	    					"C:\\Windows\\hdfs_test_BDDev.keytab"); 
	     
	     ugi.doAs( new PrivilegedExceptionAction() {
	            public Void run() throws Exception {
	            	String user = "hdfs";
	            	String bdHdfsVNnIPAddressAndPort = "hdfs://hdp003-nn:8020"; //"hdfs://hdp003-nn:8020"..."hdfs://hdpr03mn02:8020";
	    			String hdfsURL = bdHdfsVNnIPAddressAndPort + "?user=" + user;	
	    			//URI uriToHdfs = URI.create(hdfsURL);
	    			
	    			Configuration conf = new Configuration();
	            	conf.set("fs.defaultFS", hdfsURL);
	    			conf.set("dfs.support.append", "true");
	    	        conf.set("dfs.client.use.datanode.hostname", "true");
	    	        conf.set("hadoop.security.authentication", "kerberos");
	    	        
//	    	        BdCluster currBdCluster = new BdCluster("BDDev");
//	    			String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
//	    			conf.set("dfs.zookeeper.quorum", zookeeperQuorum);
//	    			System.out.println("\n*** 'dfs.zookeeper.quorum': " + zookeeperQuorum);
//	    			
//	    			HAUtil.setAllowStandbyReads(conf, true);; //.getNamenodeNameServiceId(conf);
//		    	    boolean temp = HAUtil.shouldAllowStandbyReads(conf) ;
//	    			System.out.println("temp: " + temp);
//	    			
//	    			NameNode mn01 = new NameNode(conf);
//	    			temp = mn01.isStandbyState();
//	    			System.out.println("NameNode.isStandbyState: " + temp);
//	    	        
//	    	        conf.set("dfs.nameservices","MAYOHADOOPDEV1");
//	    	        conf.set("dfs.ha.namenodes.MAYOHADOOPDEV1", "hdpr03mn01,hdpr03mn02");
//	    	        conf.set("dfs.client.failover.proxy.provider.MAYOHADOOPDEV1","org.apache.hadoop.hdfs.server.namenode1.ha.ConfiguredFailoverProxyProvider");
//
//	    	        conf.set("dfs.namenode.rpc-address.MAYOHADOOPDEV1.nn1","hdpr03mn01:8020");
//	    	        conf.set("dfs.namenode.rpc-address.MAYOHADOOPDEV1.nn2","hdpr03mn02:8020");
//	    			
//	    			Configuration otherNode = HAUtil.getConfForOtherNode(conf);
//	    			String  otherNNId = HAUtil.getNameNodeId(otherNode, "nn1");
//	    			InetSocketAddress  otherIpcAddr = NameNode.getServiceAddress(otherNode, true);
//	    			  Preconditions.checkArgument(otherIpcAddr.getPort() != 0 &&
//	    			      !otherIpcAddr.getAddress().isAnyLocalAddress(),
//	    			      "Could not determine valid IPC address for other NameNode (%s)" +
//	    			      ", got: %s", otherNNId, otherIpcAddr);
//	
//	    			  otherHttpAddr = DFSUtil.getInfoServer(null, otherNode, false);
//	    			  otherHttpAddr = DFSUtil.substituteForWildcardAddress(otherHttpAddr,
//	    			      otherIpcAddr.getHostName());
//	    			  
//	    			
//	    	       InetSocketAddress namenode1Addr = new InetSocketAddress("hdpr03mn02.mayo.edu",8020);
//	    	       System.out.println("namenode11Addr: " + namenode1Addr.toString());
//	    	       DFSClient client = new DFSClient(namenode1Addr, conf);
//	    	       ClientProtocol namenode1 = client.getNamenode();	    	       
//	    	       System.out.println("client: " + client.toString());
//	    	       System.out.println("namenode1: " + namenode1.toString());
//	    	       System.out.println("client.getCanonicalServiceName(): " + client.getCanonicalServiceName());
//	    	       System.out.println("client.getClientName(): " + client.getClientName());
//	    	       System.out.println("client.getClientContext(): " + client.getClientContext().toString());
//	    	       //System.out.println("namenode1.get..: " + client.);
//	    	       
//	    	       String canSrvName = client.getCanonicalServiceName();
//	    	       System.out.println("canSrvName: " + canSrvName);
//	    	       
//	    	       String clientName = client.getClientName();
//	    	       System.out.println("clientName: " + clientName);
//
//	    	       ClientContext clientCxt = client.getClientContext();
//	    	       System.out.println("clientCxt: " + clientCxt.toString());
//	    	       
//	    	       Map<String, Map<String, InetSocketAddress>> map = DFSUtil.getHaNnRpcAddresses(conf);	    	       
//	    	       System.out.println("map: " + map.toString());
//	    	       
//	    	       Map<String, InetSocketAddress> addressesInNN = map.get("MAYOHADOOPDEV1");
//	    	       System.out.println("addressesInNN: " + addressesInNN.toString());      
	    	    	      
//	    	       Configuration activeConf = HAUtil.getConfForOtherNode(conf);	    	       
//	    	       InetSocketAddress nn1SerAddr = NameNode.getServiceAddress(activeConf, true);
//	    	       System.out.println("nn1SerAddr: " + nn1SerAddr.toString());

	    	       //HAUtil.
	    	       //System.out.println("namenode1: " + namenode1.getStats());
	    	    
//	    	      Map<String, Map<String, InetSocketAddress>> temp1 =  DFSUtil.getHaNnRpcAddresses(conf); //.getNamenodeNameServiceId(conf);
//	    	       System.out.println("temp1: " + temp1.toString());
//	    	       
//	    	       
//	    	       
//	    	       String nnId = HAUtil.getNameNodeId(conf, nsId);
//	    	       System.out.println("nnId: " + nnId);
//	    	       
//	    	       boolean haStatus = HAUtil.isHAEnabled(conf,nsId );
//	    	       System.out.println("haStatus: " + haStatus);
//	    	       String bdClusterName = "BDDev";
	    	       String nn1ServiceState = "";
	    	       try {
//	    				ConfigureULWResources currConfigureBDResource  = new ConfigureULWResources();
//	    				ULServerCommandFactory bdENCommFactory = null; //new ULServerCommandFactory(currConfigureBDResource.getBdDevEN01ConParameters()); //getBdIntEN02ConParameters..getBdProdEN02ConParameters...getBdIntEN01ConParameters()
//	    				
//	    				if (bdClusterName.equalsIgnoreCase("BDProd")
//	    								|| bdClusterName.equalsIgnoreCase("BDPrd")){						
//	    					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdProdMN01ConParameters()); 
//	    				}
//	    				if (bdClusterName.equalsIgnoreCase("BDInt")){
//	    					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdIntMN01ConParameters()); 
//	    				}
//	    				if (bdClusterName.equalsIgnoreCase("BDDev")) {					
//	    					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdDevMN01ConParameters()); 
//	    				}
//	    				if (bdClusterName.equalsIgnoreCase("BDSbx")
//	    						|| bdClusterName.equalsIgnoreCase("BDSdbx")){
//	    					bdENCommFactory = new ULServerCommandFactory(currConfigureBDResource.getBdSdbxMN01ConParameters()); 
//	    				}
	    				
	    				//System.out.println(" *** bdENCommFactory.getServerURI(): " + bdENCommFactory.getServerURI());
	    			   //String nn1StatusQueryURI = "http://" + bdENCommFactory.getServerURI() + ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
	    	    	   String nn1StatusQueryURI = "http://hdpr03mn01.mayo.edu:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
	    			   System.out.println("\n *** nn1StatusQueryURI: " + nn1StatusQueryURI);
	    	    	  	    				
	    				URL aWebSiteURL = new URL(nn1StatusQueryURI);	     
	    		        URLConnection myConn = aWebSiteURL.openConnection();
	    		        BufferedReader bReader = new BufferedReader(new InputStreamReader(myConn.getInputStream()));
	    		        String inputLine;
	    		       
	    		        while ((inputLine = bReader.readLine()) != null){	        	
	    		            if (inputLine.contains("\"State\" :") ){
	    		            	//System.out.println(inputLine.trim());
	    		            	inputLine = inputLine.replace("\"State\" :", "");
	    		            	nn1ServiceState = inputLine.replace("\"State\" :", "").replace("\"", "").replace(",", "").trim();
	    		            }
	    		        }
	    		        bReader.close();
	    		        System.out.println("\n*** nn1ServiceState: " + nn1ServiceState );
	    	        } catch (Exception e) {
	    	            e.printStackTrace();
	    	        }//end try
	    	        	
	    	        //String nn1ServiceState = HdfsUtil.getNameNode1ServiceState("BDDev");
	    	        String krbNnPrincipal = "";	
	    	        if (nn1ServiceState.equalsIgnoreCase("active")){
	    	        	 krbNnPrincipal = "nn/hdpr03mn01@MAYOHADOOPDEV1.COM";	
	    	        } else {
	    	        	krbNnPrincipal = "nn/hdpr03mn02@MAYOHADOOPDEV1.COM";
	    	        }
	    	        conf.set("dfs.namenode.kerberos.principal", krbNnPrincipal);
	    	        UserGroupInformation.setConfiguration(conf); 
	    	        
	    	        
	    			
	    			//http://www.programcreek.com/java-api-examples/index.php?api=org.apache.hadoop.hdfs.HAUtil
	    				
	    			
//	    			conf.set("dfs.namenode.kerberos.principal.MAYOHADOOPDEV1.nn1", "nn/hdpr03mn01@MAYOHADOOPDEV1.COM");
//	    			conf.set("dfs.namenode.kerberos.principal.MAYOHADOOPDEV1.nn2", "nn/hdpr03mn02@MAYOHADOOPDEV1.COM");
	    			//dfs.namenode.kerberos.principal.mycluster.nn1
	    			//dfs.namenode.kerberos.principal.mycluster.nn2
	    			
	    			//String krbJnPrincipal = "jn/hdpr03mn02@MAYOHADOOPDEV1.COM";
	    			//conf.set("dfs.journalnode.kerberos.principal", krbJnPrincipal);
	    			
	    			//conf.set("dfs.namenode.keytab.file", "/etc/security/keytabs/nn.service.keytab");
	    			//conf.set("dfs.secondary.namenode.kerberos.principal", "nn/hdpr03mn02@MAYOHADOOPDEV1.COM");
	    			
	    			//conf.set("dfs.namenode.kerberos.principal", "nn/hdpr03mn01@MAYOHADOOPDEV1.COM");
	    			//conf.set("dfs.namenode.kerberos.principal", "nn/hdpr03mn02@MAYOHADOOPDEV1.COM");
	    			
//	    	        FileSystem currHadoopFS = FileSystem.get(conf);
//	    	        
//	    	        InetSocketAddress temp2 = HAUtil.getAddressOfActive(currHadoopFS);
//	    	        System.out.println("temp2: " + temp2.toString());
	    	        
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
