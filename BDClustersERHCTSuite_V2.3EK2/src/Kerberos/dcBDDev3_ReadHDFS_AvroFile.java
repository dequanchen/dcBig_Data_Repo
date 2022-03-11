package Kerberos;

import java.io.BufferedInputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcBDDev3_ReadHDFS_AvroFile {
	//https://community.hortonworks.com/questions/14661/read-a-avro-file-stored-in-hdfs.html

	public static void main(String[] args) {
		String bdClusterName = "BDDev3"; //BDProd..BDInt..BDSbx..BDDev..BDDev3...BDTest3...BDProd3			
		
		BdCluster currBdCluster = new BdCluster(bdClusterName);
		System.out.println("currBdCluster.getBdHdfsNnIPAddressAndPort(): " + currBdCluster.getBdHdfs1stNnIPAddressAndPort());
		
		FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
		//Configuration configuration = currBdCluster.getConfiguration();
		
		//String currSrcHdfsFilePathAndName = "/user/m041785/part-m-00000.avro"; // /user/m041785/employee1/part-r-00000
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/merged_5772782166943601"; //5.1 GB HDFS Avro File
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/merged_6048105381423202";   //14.6 GB HDFS Avro File
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/merged_6045800187231781";   //10.4 GB HDFS Avro File
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/merged_5956176936901551"; //2.0 GB HDFS Avro File
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test4/merged_4037159641331848"; //1.1 GB HDFS Avro File
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test_DC/merged_5772782166943601"; // 5.1 GB (5428259874 Bytes) HDFS Avro File
		
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test_DC/merged_2To1file.avro"; //2.1 GB merged by avro-tools-1.8.2.jar from 2 src files
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test4Merged/rofhdf901a.mayo.edu_12708352682136239_3_0"; //3.1 GB merged by NiFi by Kevin from 3 src files	
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test4Merged/rofhdf901a.mayo.edu_12712713869767990_5_1529960703398"; //5.1 GB merged by NiFi by Kevin from 5 src files
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/aceTest/HL7Test4Merged/rofhdf905a.mayo.edu_12616729749393919_5_1529960701665"; //5.2 GB merged by NiFi by Kevin from 5 src files
		
		//String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/raw/rofhdf904a_12536785320825486_274608_6"; //1.5 GB merged by NiFi by Kevin from 274608 src flow files
        //String currSrcHdfsFilePathAndName = "/data/bd_dos_developers/lake/raw/rofhdf905a_12712512314461519_256237_5"; //1.4 GB merged by NiFi by Kevin from 256237 src flow files

        
		//String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf904a_12527968477146794_100_0"; //a//7.2 GB merged by Nifi by Mike/Dequan from 100 src files
		//String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf904a_rofhdf904a_12527968477146794_100_0_100_0"; //a//7.2 GB merged by Nifi by Mike/Dequan from 100 src files
		//String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12859574464482657_100_0"; //b//7.2 GB merged by Nifi by Mike/Dequan from 100 src files
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12859574464482657_100_0"; //b//7.2 GB merged by Nifi by Mike/Dequan from 100 src files
        
		//String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) (Use First Metadata)    
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) (Keep Only Common Metadata)  
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) (Do Not Merge Uncommon Data)  
		
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12867838101924965_100_0"; //c//7.2 GB merged by Nifi by Mike/Dequan from 100 src files        
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_13047112041543508_50_0"; //m5//1.4 GB from 49+1 avro src files (m2) (Use First Metadata) 
        
        String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_13291044291441422_9_0"; //15.5 KB from 9 small avro src files (Keep Only Common Metadata)  
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_13047112041543508_50_0"; //m5//1.4 GB from 49+1 avro src files (m2) (Use First Metadata)    
        
		System.out.println("\n*** In the HDFS file: " + currSrcHdfsFilePathAndName);
		
		Path avroFilePath = new Path(currSrcHdfsFilePathAndName);	
		
		try {
			//String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
			//System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
			//FileStatus[] status = currHadoopFS.listStatus(avroFilePath);				
			//BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
			//BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(status[0].getPath()));
			
			//Method #1
			BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(avroFilePath));
			DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
			Schema schema = reader.getSchema();
			System.out.println("\n*1* The schema of the avro file:\n\t" + schema.toString());
			
						
			System.out.println("\n*2* The Data of the avro file:\n\t" );
			long rowCount = 0;
			for (GenericRecord datum : reader) {
			    rowCount++;
			    System.out.println("(" + rowCount + ") " + datum);
			    
			    //if (rowCount >= 5) {
			    //  break;
			    //}
			}

			reader.close();
			System.out.println("\n --- Done - Retrieving all the data of the avro file!!!" );
			
//			//Method #2
//			Configuration  config = currBdCluster.getConfiguration();
//			SeekableInput input = new FsInput(avroFilePath, config);
//			DatumReader<GenericRecord> grReader = new GenericDatumReader<GenericRecord>();
//			FileReader<GenericRecord> fileReader = DataFileReader.openReader(input, grReader);
//
//			Schema schema2 = fileReader.getSchema();
//			System.out.println("\n*2* The schema of the avro file:\n\t" +schema2.toString());
//			
//			
//			for (GenericRecord datum : fileReader) {
//			    System.out.println("value = " + datum);
//			}
//
//			fileReader.close(); 
			
			
		} catch (Exception e) {
            e.printStackTrace();
        }//end try		
	}//end main
}//end class
