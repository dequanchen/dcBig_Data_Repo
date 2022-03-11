package Kerberos;

import java.io.BufferedInputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import dcModelClasses.ApplianceEntryNodes.BdCluster;


public class dcBDDev3_ReadToRewriteHDFS_AvroFile_2 {
    //https://community.hortonworks.com/questions/14661/read-a-avro-file-stored-in-hdfs.html
    //https://stackoverflow.com/questions/47151008/need-to-merge-avro-files-using-java-application
    //https://stackoverflow.com/questions/21977704/how-to-avro-binary-encode-the-json-string-using-apache-avro

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
        Path avroFile_SrcInputPath = new Path(currSrcHdfsFilePathAndName);
        
        try {
            //String krbConfFilePathAndName = dcPostKerberosHDFSTrial.class.getResource("krb5.conf").getPath();
            //System.setProperty("java.security.krb5.conf", krbConfFilePathAndName);
            //FileStatus[] status = currHadoopFS.listStatus(avroFile_SrcInputPath);              
            //BufferedReader br = new BufferedReader(new InputStreamReader(currHadoopFS.open(status[0].getPath())));
            //BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(status[0].getPath()));
            
            //1. Preparation - Deleting existing destination (target) avro file
            String currDestHdfsFilePathAndName = currSrcHdfsFilePathAndName + "_rw.avro";              
            Path avroFile_DestOutPath = new Path(currDestHdfsFilePathAndName);
            if (currHadoopFS.exists(avroFile_DestOutPath)) {
              currHadoopFS.delete(avroFile_DestOutPath, true);
              System.out.println("\n*** deleting existing HDFS destination Avro file: " + avroFile_DestOutPath.getParent() + "/" + avroFile_DestOutPath.getName());
            }
            
            
            //FSDataOutputStream fsDataOutStream = currHadoopFS.create(avroFile_DestOutPath, true);    
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));           
            //DataFileWriter<GenericRecord> writer = new DataFileWriter<GenericRecord>(new GenericDatumWriter<>());
           
            //2. Read the source avro file
            BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(avroFile_SrcInputPath));
            DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
            Schema schema1 = reader.getSchema();
            System.out.println("\n*1* The src avro file schema: \n\t" + schema1.toString());
            //bw.write(schema + "\n");  
            
//            System.out.println("\n*2* The src avro file data:\n\t" );
//            long rowCount = 0;
//            for (GenericRecord datum : reader) {
//                rowCount++;
//                System.out.println("(" + rowCount + ") Read and write the original src avro file data: \n\t " + datum);
//                //bw.write(datum + "\n");  
//                //if (rowCount >= 5) {
//                //  break;
//                //}
//            }
            reader.close();
            //bw.close();
            //fsDataOutStream.close();
            
            //3. Create the target or destination avro file fro reading the source avro file
            //BufferedInputStream inStream2 = new BufferedInputStream(currHadoopFS.open(avroFile_SrcInputPath));
            //DataFileStream<GenericRecord> reader2 = new DataFileStream <GenericRecord>(inStream2, new GenericDatumReader<GenericRecord>());            
            //// SeekableInput seekableInput = new AvroFSInput(dataInputStream, 5);    
            //// DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
            //// DataFileReader<GenericRecord> fileReader = new DataFileReader<GenericRecord>(seekableInput, datumReader);
            
            //Schema schema2 = reader.getSchema();            
            //DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema2);            
            //DataFileWriter<GenericRecord> writer = new DataFileWriter<GenericRecord>(datumWriter);
            //BufferedOutputStream outputStream = new BufferedOutputStream(new BufferedOutputStream(currHadoopFS.create(avroFile_DestOutPath)));            
            //String inputCodec = reader.getMetaString(DataFileConstants.CODEC);
            //if (inputCodec == null) {
            //  inputCodec = DataFileConstants.NULL_CODEC;
            //}
            //writer.setCodec(CodecFactory.fromString(inputCodec));
            //writer.create(schema2, outputStream);
            //writer.appendAllFrom(reader2, false);
            //reader2.close();            
            //writer.close();
            //outputStream.close();
            
//            Schema schema2 = new Schema.Parser().parse(schema1.toString());
//            DatumReader<GenericRecord> reader2 = new GenericDatumReader<GenericRecord>(schema2);
//            
//            InputStream  input = new ByteArrayInputStream(json.getBytes());
//            ByteArrayOutputStream  output = new ByteArrayOutputStream();
//            DataInputStream din = new DataInputStream(input);
//            DataFileWriter<GenericRecord> writer = new DataFileWriter<GenericRecord>(new GenericDatumWriter<GenericRecord>());
//            writer.create(schema2, output);
//            Decoder decoder = DecoderFactory.get().jsonDecoder(schema2, din);
//            GenericRecord datum;
//            while (true) {
//                try {
//                    datum = reader2.read(null, decoder);
//                } catch (EOFException eofe) {
//                    break;
//                }
//                writer.append(datum);
//            }
//            writer.flush();
//            //return output.toByteArray();
//
//            din.close();            
//            input.close();
//            writer.close();
            
            //4. Read the destination (target) avro file
            BufferedInputStream inStream3 = new BufferedInputStream(currHadoopFS.open(avroFile_DestOutPath));
            DataFileStream<GenericRecord> reader3 = new DataFileStream <GenericRecord>(inStream3, new GenericDatumReader<GenericRecord>());
            Schema schema3 = reader.getSchema();
            System.out.println("\n*1* The dest avro file schema: \n\t" + schema3.toString());            
            
            System.out.println("\n*2* The dest avro file data:\n\t" );
            long rowCount2 = 0;
            for (GenericRecord datum : reader3) {
                rowCount2++;
                System.out.println("(" + rowCount2 + ") Read and write the original src avro file data: \n\t " + datum);                
            }
            reader3.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }//end try      
    }//end main
}//end class
