package Kerberos;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import dcModelClasses.ApplianceEntryNodes.BdCluster;

public class dcPostKerberosHBaseTrial {

	public static void main(String[] args) {
		String bdClusterName = "BDDev";
		String externalHbaseTableName = "employee_krb_hbjc";
		boolean externalHBaseTestStatus = false;
		try {
			String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
			System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
			//System.setProperty("sun.security.krb5.debug", "true");
			
			BdCluster currBdCluster = new BdCluster(bdClusterName);
			String zookeeperQuorum = currBdCluster.getZookeeperQuorum();
			
			Configuration configuration = HBaseConfiguration.create();
			configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
			System.out.println("\n*** 'hbase.zookeeper.quorum': " + zookeeperQuorum);
			//conf.set("hbase.master.port", "60000");
			//conf.set("hbase.zookeeper.peerport", "2888");
			//conf.set("hbase.zookeeper.leaderport", "3888");
			//conf.set("zookeeper.znode.parent", "/hbase-unsecure");			
			configuration.set("zookeeper.znode.parent", "/hbase-secure");
			configuration.set("hbase.zookeeper.property.clientPort", "2181");	//2888..2181
			configuration.set("hbase.cluster.distributed", "true");
			
			
								
			//configuration.set("hadoop.security.authentication", "kerberos");						
			//configuration.addResource(new Path("C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Dev\\hbase-site.xml"));
			//configuration.addResource(new Path("C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Dev\\hdfs-site.xml"));
			//configuration.addResource(new Path("C:\\BD\\BDInt\\EN06\\hdfs\\keytabs\\Dev\\core-site.xml"));
			
							
			UserGroupInformation.setConfiguration(configuration);
			String keytabFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("hbase_test_BDDev.keytab").getPath();
			UserGroupInformation.loginUserFromKeytab("hbase/hdpr03mn02.mayo.edu@MAYOHADOOPDEV1.COM",
					keytabFilePathAndName);
			
			
			HBaseAdmin admin = new HBaseAdmin(configuration);			
			if (admin.tableExists(externalHbaseTableName)){
	        	admin.disableTable(externalHbaseTableName);
	    		System.out.println("\n*** disabled HBase table - " + externalHbaseTableName);
	    		admin.deleteTable(externalHbaseTableName);
	    		System.out.println("\n*** deleted HBase table - " + externalHbaseTableName);
	        }
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(externalHbaseTableName));		
			tableDescriptor.addFamily(new HColumnDescriptor("cf1"));
			admin.createTable(tableDescriptor);
			System.out.println("\n*** Created HBase table - " + externalHbaseTableName);
			
			
			HTable table = new HTable(configuration, externalHbaseTableName);					
			Put put = new Put(Bytes.toBytes("101"));					
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("firstName"), Bytes.toBytes("Jim"));					
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("lastName"), Bytes.toBytes("Brown"));		
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("salary"), Bytes.toBytes("160000"));			
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("gender"), Bytes.toBytes("M"));			
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("address"), Bytes.toBytes("\"3212 Wall St., New York, NY10277\""));			
			table.put(put);					
			table.flushCommits();
			admin.flush(externalHbaseTableName);
			admin.close();				
			System.out.println("\n*** Finished - Putting Data into HBase table - " + externalHbaseTableName);
			
			//table = new HTable(conf, externalHbaseTableName);
			Get get = new Get(Bytes.toBytes("101"));					
			get.addFamily(Bytes.toBytes("cf1"));					
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
	            	externalHBaseTestStatus = true;            	
	            	break;
	            }
	            
			}

			System.out.println("\n*** externalHBaseTestStatus: " + externalHBaseTestStatus);
			System.out.println("\n*** Finished - HBase Get Query!!!");
			table.close();		
      
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
