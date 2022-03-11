package Kerberos;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcBDDev3_ReadToRewriteHDFS_AvroFile {
    //https://community.hortonworks.com/questions/14661/read-a-avro-file-stored-in-hdfs.html
    //https://stackoverflow.com/questions/47151008/need-to-merge-avro-files-using-java-application

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
        
        String currSrcHdfsFilePathAndName = "/data/hdf_prod_user/avro_test/rofhdf904a_9684710015859367_2_60"; //2.8 KB HDFS Avro File
        
        System.out.println("\n*** In the HDFS file: " + currSrcHdfsFilePathAndName);        
        Path avroFilePath = new Path(currSrcHdfsFilePathAndName);
        
        try {
            //String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
            //System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
            //FileStatus[] status = currHadoopFS.listStatus(avroFilePath);              
            //BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
            //BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(status[0].getPath()));
            
            String currDestHdfsFilePathAndName = currSrcHdfsFilePathAndName + "_rw.avro";              
            Path destOutPath = new Path(currDestHdfsFilePathAndName); 
            FSDataOutputStream fsDataOutStream = currHadoopFS.create(destOutPath, true);    
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
      
           
            BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(avroFilePath));
            DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
            Schema schema = reader.getSchema();
            System.out.println("\n*1* Read and write the original schema of the src avro file to the new avro file in HDFS: \n\t" + schema.toString());
            bw.write(schema + "\n");  
            
            System.out.println("\n*2* The Data of the avro file:\n\t" );
            long rowCount = 0;
            for (GenericRecord datum : reader) {
                rowCount++;
                System.out.println("(" + rowCount + ") Read and write the original src avro file data: \n\t " + datum);
                bw.write(datum + "\n");  
                //if (rowCount >= 5) {
                //  break;
                //}
            }

            reader.close();
            bw.close();
            fsDataOutStream.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }//end try      
    }//end main
}//end class
