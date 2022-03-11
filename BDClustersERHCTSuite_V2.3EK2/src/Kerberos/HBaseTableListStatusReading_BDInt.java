package Kerberos;

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

public class HBaseTableListStatusReading_BDInt {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {		
		final String externalHbaseTableName = "employee1";
		
		try {			
			System.setProperty("java.security.krb5.conf", 	"C:\\Windows\\krb5_BDInt.conf"); //krb5.ini...krb5_BDInt.conf..krb5.conf
		    System.setProperty("sun.security.krb5.debug", "true");
		    //System.setProperty("java.net.preferIPv4Stack", "true");
			
		    //String bdClusterName = "BDInt";
			//BdCluster currBdCluster = new BdCluster(bdClusterName);
			//String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
			String zookeeperQuorum= "hdpr02mn01,hdpr02mn02,hdpr02dn01";
			System.out.println("\n*** 'hbase.zookeeper.quorum': " + zookeeperQuorum);
			
			final Configuration conf = HBaseConfiguration.create();			
			conf.set("hbase.zookeeper.quorum", zookeeperQuorum);			
			
			//conf.set("zookeeper.znode.parent", "/hbase-unsecure");			
			conf.set("zookeeper.znode.parent", "/hbase-secure");
			conf.set("hbase.zookeeper.property.clientPort", "2181");	//2888..2181
			conf.set("hbase.cluster.distributed", "true");
			
			
			conf.set("hadoop.security.authentication", "kerberos");
			conf.set("hbase.security.authentication", "kerberos");									
			//conf.addResource(new Path("C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Int\\hbase-site.xml"));
			conf.set("hbase.master.kerberos.principal", "hbase/_HOST@MAYOHADOOPTEST1.COM");
			conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@MAYOHADOOPTEST1.COM");

							
			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation ugi = UserGroupInformation.
					loginUserFromKeytabAndReturnUGI("hbase/hdpr02mn02.mayo.edu@MAYOHADOOPTEST1.COM", 
							"C:\\Windows\\hbase_test_BDInt.keytab");	
			

			 ugi.doAs( new PrivilegedExceptionAction() {
	              @SuppressWarnings("deprecation")
				public Void run() throws Exception {
	            	  
	            	  System.out.println("\n***Logged in Using a Keytab file...");
	            	  
	            	              	  
	            	  HBaseAdmin admin = new HBaseAdmin(conf);
	            	  TableName[] tables  = admin.listTableNames();

	            	  for(TableName table: tables){
	            	      System.out.println(table.toString());
	            	  }
	            	  
	            	 // String externalHbaseTableName = "employee1";
	            	  if (admin.tableExists(externalHbaseTableName)){
	            		  System.out.println("\n*** Exisiting table: " + externalHbaseTableName);
	            	  } else {
	            		  System.out.println("\n*** Non-Exisiting table: " + externalHbaseTableName);	      	    		
	      	          }
	            	 
	            	  admin.close();
	            	  
	            	  HTable table = new HTable(conf, externalHbaseTableName);
	            	  Get get = new Get(Bytes.toBytes("101"));					
	            	  get.addFamily(Bytes.toBytes("cfa"));	
	            	  get.setMaxVersions(3);
	            	  Result result = table.get(get);
	            	  for (KeyValue kv : result.raw()) {
		      				String rowKey = new String(kv.getRow());
		      				String colFamily = new String(kv.getFamily());
		      				String columnName = new String(kv.getQualifier());
		      				String colValue = new String(kv.getValue());
		      				System.out.println("\n--- rowKey " + rowKey); 
		      	            System.out.println("colFamily: " + colFamily); 
		      	            System.out.println("columnName: " + columnName); 
		      	            System.out.println("colValue: " + colValue); 
		      	            if (columnName.equalsIgnoreCase("salary") && colValue.equalsIgnoreCase("160000")){
		      	            	System.out.println("\n***HBase Table Reading Success!!!");           	
		      	            	break;
		      	            }
		      		  }
		      		  table.close();
		      		  
			  		  return null;
	              }
	          } );      
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
