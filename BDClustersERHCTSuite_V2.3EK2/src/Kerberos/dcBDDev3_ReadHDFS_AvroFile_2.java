package Kerberos;

import java.io.BufferedInputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcBDDev3_ReadHDFS_AvroFile_2 {
    //https://community.hortonworks.com/questions/14661/read-a-avro-file-stored-in-hdfs.html

    public static void main(String[] args) {
        String bdClusterName = "BDDev3"; //BDProd..BDInt..BDSbx..BDDev..BDDev3...BDTest3...BDProd3          
        
        BdCluster currBdCluster = new BdCluster(bdClusterName);
        System.out.println("currBdCluster.getBdHdfsNnIPAddressAndPort(): " + currBdCluster.getBdHdfs1stNnIPAddressAndPort());
        
        FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
        //Configuration configuration = currBdCluster.getConfiguration();
        
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12881973003173878_50_0"; //1.4 GB from 49+1 avro src files
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12884943259242555_50_0"; //1.4 GB from 49+1 avro src files        
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12885318351915323_50_0"; //1.4 GB from 49+1 avro src files
        
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) (Use First Metadata)    
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) 
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_merged/rofhdf901a_12953939766187695_50_0"; //1.4 GB from 49+1 avro src files (m2) 
         
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/source/merged_5956176936901551_corrupted_KA"; //2.04 GB KevinAce_Corrupted File 
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/src_json/temp4.json";
        
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_converted/rofhdf901a_13558392993150925_4_0"; //93.3 KB from 3+1+1 src .json files
        
        String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_converted/rofhdf901a_13640573955437080_100_1"; //8.7 GB recursively merged
        //String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/nifi_converted/rofhdf901a_13640510539238076_100_1"; //5.3 GB recursively merged
                
        System.out.println("\n*** In the HDFS file: " + currSrcHdfsFilePathAndName);
        
        Path avroFilePath = new Path(currSrcHdfsFilePathAndName);
        
        try {
            //String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
            //System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
            //FileStatus[] status = currHadoopFS.listStatus(avroFilePath);              
            //BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
            //BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(status[0].getPath()));
            
//            BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(avroFilePath)));
//            String line = "";
//            long rowCount = 0;
//            while ((line = br.readLine()) != null) {
//               rowCount++;
//               System.out.println("(" + rowCount + ") " + line);
//                                     
//            }//end while
          
            //Method #1
            BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(avroFilePath));
            DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
            Schema schema = reader.getSchema();
            System.out.println("\n*1* The schema of the avro file:\n\t" + schema.toString());
            
            
            System.out.println("\n*2* The Data of the avro file:\n\t" );
//            long rowCount = 0;
//            for (GenericRecord datum : reader) {
//                rowCount++;
//                
//                
//                if (rowCount >= 52550) {
//                  System.out.println("(" + rowCount + ") " + datum);
//                } else {
//                  System.out.println("(" + rowCount + ") ");
//                }
//            }
            
            long rowCount = 0;
            while (reader.hasNext()) {
              GenericRecord datum = reader.next();
              rowCount++; 
              //System.out.println("(" + rowCount + ") " + datum);
              
//              if (datum.toString().contains("AAU_modified")) {
//                System.out.println("(" + rowCount + ") " + datum);
//              } else {
//                System.out.println("\t(" + rowCount + ") ");
//              }
              
              double fileSizeInGB = 8.7;              
              long cutOffRowNum = (long) (fileSizeInGB *66666);
              if (rowCount < cutOffRowNum) {
                long modValue = rowCount % 1000;
                if (modValue == 0) {
                  System.out.println("(" + rowCount + ") " + datum);
                } else {
                  System.out.println("(" + rowCount + ") ");
                }
                
              }
              if (rowCount >= cutOffRowNum) {
                long modValue2 = rowCount % 10;
                if (modValue2 == 0) {
                  System.out.println("(" + rowCount + ") " + datum);
                } else {
                  System.out.println("(" + rowCount + ") ");
                }
              }
            }
            
            reader.close();
            System.out.println("\n --- Done - Retrieving all the data of the avro file!!!" );
            
//          //Method #2
//          Configuration  config = currBdCluster.getConfiguration();
//          SeekableInput input = new FsInput(avroFilePath, config);
//          DatumReader<GenericRecord> grReader = new GenericDatumReader<GenericRecord>();
//          FileReader<GenericRecord> fileReader = DataFileReader.openReader(input, grReader);
//
//          Schema schema2 = fileReader.getSchema();
//          System.out.println("\n*2* The schema of the avro file:\n\t" +schema2.toString());
//          
//          
//          for (GenericRecord datum : fileReader) {
//              System.out.println("value = " + datum);
//          }
//
//          fileReader.close(); 
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }//end try      
    }//end main
}//end class
