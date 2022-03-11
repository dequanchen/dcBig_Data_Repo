package Kerberos;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;




public class dcBDDev3_ReadHDFS_AvroFile_3 {
     //https://community.hortonworks.com/questions/14661/read-a-avro-file-stored-in-hdfs.html
     //https://issues.apache.org/jira/secure/attachment/12676349/AVRO-1596.patch

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        //String bdClusterName = "BDDev3"; //BDProd..BDInt..BDSbx..BDDev..BDDev3...BDTest3...BDProd3          
        
        //BdCluster currBdCluster = new BdCluster(bdClusterName);
        //System.out.println("currBdCluster.getBdHdfsNnIPAddressAndPort(): " + currBdCluster.getBdHdfs1stNnIPAddressAndPort());
        
        //FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
        //Configuration configuration = currBdCluster.getConfiguration();
        
        String localAvroFilePathAndName = "C:\\Users\\M041785\\Desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\ProblemIdAndFixing\\HDFS_Issues\\LargeAvroFile_Reading_InvalidSync\\merged_5956176936901551.avro";
         
        System.out.println("\n*** In the Local Avro file: " + localAvroFilePathAndName);
        
        File avroFile = new File(localAvroFilePathAndName);
        
        try {                    
            BufferedInputStream inStream = new BufferedInputStream( new FileInputStream(avroFile));
            DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
            Schema schema = reader.getSchema();
            System.out.println("\n*1* The schema of the local corrupted avro file:\n\t" + schema.toString());
            reader.close();
            inStream.close();
            
            
            System.out.println("\n*2* The Data of the local corrupted avro file:\n\t" );
            DataFileReader dataFileReader = new DataFileReader<Utf8>(avroFile, new GenericDatumReader<Utf8>(schema));
            
           long avroFile_length = avroFile.length();
            System.out.println("avroFile.length() is: " + avroFile_length); 
            long skipLen = avroFile.length() / 10;
            System.out.println("skipLen is: " + skipLen); 
            
            long lastSeekPos = 0;
            long seekPos = skipLen;
            long rowCount = 0;
            while (seekPos < avroFile_length) {  //< avroFile_length           
              seekPos += skipLen;             
              System.out.println("seekPos is: " + seekPos); 
              
              dataFileReader.sync(seekPos);
              lastSeekPos = seekPos;
             
              // read all the elements in the current segment (seekPos up to lastSeekPos)
//              while (dataFileReader.hasNext()
//                      && !dataFileReader.pastSync(lastSeekPos)) {
//                GenericRecord datum = (GenericRecord) dataFileReader.next();    
//                rowCount++;
//                System.out.println("(" + rowCount + ") " + datum);   
//                 
//              }              
            }
            
           System.out.println("lastSeekPos is: " + lastSeekPos);
           dataFileReader.pastSync(lastSeekPos);
           while (dataFileReader.hasNext()) {
                GenericRecord datum = (GenericRecord) dataFileReader.next();    
                rowCount++;
                System.out.println("(" + rowCount + ") " + datum); 
                System.out.println("Final seekPos is: " + lastSeekPos); 
               
           }         
            
           System.out.println("lastSeekPos is: " + lastSeekPos);
           
           
//            long rowCount = 0;
//            
//            long prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync); 
//            String schema2 = dataFileReader.getSchema().toString();
//            System.out.println("The avro file's schema2 is: " + schema2); 
//            
//            
//            dataFileReader.sync(prevSync);
//            dataFileReader.pastSync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);  
//            
            
            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            dataFileReader.next();
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            dataFileReader.next();
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            dataFileReader.next();
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
//            
//            dataFileReader.sync(prevSync);
//            dataFileReader.next();
//            prevSync = dataFileReader.previousSync();
//            System.out.println("The avro file's prevSync is: " + prevSync);
            
            
//            dataFileReader.sync(prevSync);
//            dataFileReader.next();
//            prevSync = dataFileReader.previousSync();
//            System.out.println("dataFileReader.tell(): " + dataFileReader.tell());
            
            
//            //dataFileReader.sync(prevSync);
//            rowCount = prevSync;
//            while (dataFileReader.hasNext()) {
//              rowCount++;
//              String dataRecord = dataFileReader.next().toString();
//              System.out.println("(" + rowCount + ") " + dataRecord); 
//              
//            }
//            dataFileReader.close();
//
//            System.out.println("The avro file's last prevSync is: " + prevSync);
//            System.out.println("Final_rowCount - Final_prevSync = " + (rowCount - prevSync));
//            
            
            //The following final reading finished with: (52559)... "Invalid Sync"... 
//            (52559) {"contractName": "AAU", "contractSource": "IMA.EDT.CNOTES.001", "payloadType": "HL7", "myVelocity": "RT", "messageIngestedts": "1512544085207", "payloadSize": "8187", "payloadEncoding": null, "payload": "MSH|^~`$|TXT|RCH|MR|RCH|20140310095024||MDM^T02|CNI12014031009502387|D|2.3^^0.91|||AL|AL|\rPID|||2922248^^RCH||Frankfurt^Donald ^Lee|\rPV1|||302^||||||||||||||||||||||||||||||||||||RMH|\rCON||DOCCNSNT-ROC^^PHDICT||||||||F|B|||||||||||||^^^^^^^^1053062&&MCR|I||||\rTXA||NOTE^9999^DOC02899|AP|201403100837||201403100935|201403100935||15185508^Ahle^Heidi^J|15185508^Ahle^Heidi^J^^^^^^^^^^^|15185508^Ahle^Heidi^J|384675809^RCH|^RCH|||||0|AV|AC||15185508^Ahle^Heidi^J^^^^^^^^^^^201403100935|\rOBX|1|FT|||<?xml version=\"1.0\"?><levelone><clinical_document_header><id EX=\"384675809\" RT=\"2.16.840.1.113883.3.2.3.1\" \/><set_id EX=\"384675809\" RT=\"2.16.840.1.113883.3.2.1.2\" \/><version_nbr V=\"1\" \/><document_type_cd V=\"9999\" S=\"2.16.840.1.113883.3.2.1.3\" DN=\"Airway Management\" \/><origination_dttm V=\"20140310T093500\" \/><confidentiality_cd V=\"0\" S=\"2.16.840.1.113883.3.2.1.4\" \/><patient_encounter><practice_setting_cd V=\"302\" S=\"2.16.840.1.113883.3.2.1.7\" DN=\"Anesthesiology\" \/><encounter_tmr V=\"20140310T083700\" \/><service_location><id EX=\"RMH\" RT=\"2.16.840.1.113883.3.2.1.8\" \/><\/service_location><\/patient_encounter><legal_authenticator><legal_authenticator.type_cd V=\"SPV\" \/><participation_tmr V=\"\" \/><signature_cd V=\"S\" \/><person><id EX=\"15185508\" RT=\"2.16.840.1.113883.3.2.3.9\" \/><person_name><nm><GIV V=\"Heidi\" \/><MID V=\"J\" \/><FAM V=\"Ahle\" \/><PFX V=\"\" \/><SFX V=\"RN, CRNA\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><telecom V=\"127-01408\" USE=\"PG\" \/><\/person><\/legal_authenticator><originator><originator.type_cd V=\"AUT\" \/><participation_tmr V=\"20140310T093500\" \/><person><id EX=\"15185508\" RT=\"2.16.840.1.113883.3.2.3.2\" \/><person_name><nm><GIV V=\"Heidi\" \/><MID V=\"Jane\" \/><FAM V=\"Ahle\" \/><PFX V=\"\" \/><SFX V=\"RN, CRNA\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><telecom V=\"127-01408\" USE=\"PG\" \/><\/person><\/originator><originating_organization><originating_organization.type_cd V=\"CST\" \/><organization><id EX=\"MCR\" RT=\"2.16.840.1.113883.3.2.1.10\" \/><organization.nm V=\"Mayo Clinic, Rochester\" \/><\/organization><\/originating_organization><transcriptionist><transcriptionist.type_cd V=\"ENT\" \/><participation_tmr V=\"20140310T093500\" \/><person><id EX=\"15185508\" RT=\"2.16.840.1.113883.3.2.1.9\" \/><person_name><nm><GIV V=\"Heidi\" \/><MID V=\"Jane\" \/><FAM V=\"Ahle\" \/><PFX V=\"\" \/><SFX V=\"RN, CRNA\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><\/person><\/transcriptionist><provider><provider.type_cd V=\"PRF\" \/><function_cd V=\"PERFORMER\" DN=\"Performing Physician\" \/><participation_tmr V=\"\" \/><person><id EX=\"\" RT=\"2.16.840.1.113883.3.2.1.9\" \/><person_name><nm><GIV V=\"\" \/><MID V=\"\" \/><FAM V=\"\" \/><PFX V=\"\" \/><SFX V=\"\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><telecom V=\"\" USE=\"PG\" \/><\/person><\/provider><provider><provider.type_cd V=\"CON\" \/><function_cd DN=\"Supervising Consultant\" V=\"SUPERVISOR\" \/><participation_tmr V=\"\" \/><person><id EX=\"\" RT=\"2.16.840.1.113883.3.2.1.9\" \/><person_name><nm><GIV V=\"\" \/><MID V=\"\" \/><FAM V=\"\" \/><PFX V=\"\" \/><SFX V=\"\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><telecom V=\"\" USE=\"PG\" \/><\/person><\/provider><service_actor><service_actor.type_cd V=\"OENT\" DN=\"original transcriber\" \/><participation_tmr V=\"20140310T093500\" \/><person><id EX=\"15185508\" RT=\"2.16.840.1.113883.3.2.1.9\" \/><person_name><nm><GIV V=\"Heidi\" \/><MID V=\"Jane\" \/><FAM V=\"Ahle\" \/><PFX V=\"\" \/><SFX V=\"RN, CRNA\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><\/person><\/service_actor><patient><patient.type_cd V=\"PATSBJ\" \/><person><id EX=\"1053062\" RT=\"2.16.840.1.113883.3.2.2\" \/><person_name><nm><GIV V=\"Donald\" \/><MID V=\"Lee\" \/><FAM V=\"Frankfurt\" \/><PFX V=\"Mr.\" \/><SFX V=\"\" \/><\/nm><person_name.type_cd V=\"L\" \/><\/person_name><addr><LIT V=\"132 Tilden Street\" \/><CTY V=\"Fairmont\" \/><STA V=\"MN\" \/><ZIP V=\"56031\" \/><CNT V=\"US\" \/><\/addr><\/person><is_known_by><id EX=\"2-922-248\" RT=\"2.16.840.1.113883.3.2.2.2\" \/><is_known_to><id EX=\"MCR\" RT=\"2.16.840.1.113883.3.2.1.10\" \/><\/is_known_to><\/is_known_by><birth_dttm V=\"19360404\" \/><administrative_gender_cd V=\"M\" S=\"2.16.840.1.113883.3.2.2.2\" \/><\/patient><status_cd V=\"Fnl\" DN=\"Final\" S=\"2.16.840.1.113883.3.2.1.16\" \/><source_data><document_id EX=\"384675809\" RT=\"2.16.840.1.113883.3.2.3.9\" \/><source_id EX=\"CHARTPLUS\" RT=\"2.16.840.1.113883.3.2.1.16\" \/><\/source_data><document_class_cd V=\"PROC\" DN=\"PROCEDURE NOTE\" S=\"2.16.840.1.113883.3.2.1.17\" SV=\"1.0\" \/><cn1_admin_data><cn1_status_cd V=\"Fnl\" S=\"2.16.840.1.113883.3.2.3.1\" \/><cn1_service_cd V=\"ANESTH\" DN=\"Anesthesiology\" S=\"2.16.840.1.113883.3.2.3.2\" \/><cn1_event_cd V=\"AM\" DN=\"Airway Management\" S=\"2.16.840.1.113883.3.2.3.3\" \/><cn1_history_section_cd V=\"HEN\" S=\"2.16.840.1.113883.3.2.3.4\" \/><cn1_sheet_nbr V=\"1\" \/><cn1_provider_login V=\"M070053\" \/><cn1_signable_allergy_document V=\"Y\" \/><cn1_discharge_date V=\"\" \/><cn1_transfer_service_cd V=\"\" \/><cn1_final_sum_flag V=\"\" \/><cn1_sign_type_cd V=\"\" \/><cn1_summary_version V=\"0\" \/><cn1_save_action_cd V=\"\" \/><cn1_is_inpatient_service V=\"true\" \/><signatureId V=\"15185508\" \/><signatureDate V=\"20140310T093512\" \/><signature2Id V=\"\" \/><signature2Date V=\"\" \/><transcriberId V=\"15185508\" \/><transcriptionDate V=\"20140310T093512\" \/><revisionDate V=\"\" \/><revisionById V=\"\" \/><letterCreated V=\"0\" \/><post_signature_revision V=\"\" \/><obsolete V=\"0\" \/><idxServiceAbbr V=\"DOC02899\" \/><isDocSignable V=\"\" \/><hasChangedSinceOpen V=\"\" \/><\/cn1_admin_data><\/clinical_document_header><body><section><caption><caption_cd V=\"20100\" DN=\"Revision History\" S=\"2.16.840.1.113883.3.1.21\" SV=\"1.0\" \/>Revision History<\/caption><\/section><section><caption><caption_cd V=\"20142\" DN=\"SP\" S=\"2.16.840.1.113883.3.1.21\" SV=\"1.0\" \/>Supervisor Present Statement<\/caption><\/section><section><caption><caption_cd V=\"20143\" DN=\"PPI\" S=\"2.16.840.1.113883.3.1.21\" SV=\"1.0\" \/>Preprocedure Information<\/caption><paragraph><content>Airway Management<\/content><\/paragraph><paragraph><content>Location: RMH OR 54(54)<\/content><\/paragraph><paragraph><content>Medical Team<\/content><\/paragraph><paragraph><content>      Burkle, Christopher                          Anesthesiologist<\/content><\/paragraph><paragraph><content>      Ahle, Heidi J.                               Anesthetist<\/content><\/paragraph><paragraph><content>Preop Assessment of Probable Difficulty:           Questionable\/Suspicious Difficult Airway<\/content><\/paragraph><paragraph><content>Level of Consciousness:                            Anesthetized<\/content><\/paragraph><\/section><section><caption><caption_cd V=\"20144\" DN=\"PI\" S=\"2.16.840.1.113883.3.1.21\" SV=\"1.0\" \/>Procedure Information<\/caption><paragraph><content>Mask Ventilation:                                  Easy mask<\/content><\/paragraph><paragraph><content>Final Airway Management Technique:                 Video Laryngoscope (e.g. GlideScope)<\/content><\/paragraph><paragraph><content>Number of Attempts for Final Technique:            1<\/content><\/paragraph><paragraph><content>Airway Difficulty of Direct Laryngoscopy (DL):     We did not attempt DL<\/content><\/paragraph><paragraph><content>Tube Type:                                         Oral ETT<\/content><\/paragraph><paragraph><content>Tube Size:                                         7.5<\/content><\/paragraph><paragraph><content>Cuffed:                                            Yes<\/content><\/paragraph><paragraph><content>Leak Test Performed:                               No<\/content><\/paragraph><paragraph><content>Endotracheal Tube Distance at Teeth\/Gum:           22<\/content><\/paragraph><paragraph><content>Previous Attempt: Direct Laryngoscopy:             No<\/content><\/paragraph><paragraph><content>Other Previous Techniques Attempted:               None<\/content><\/paragraph><paragraph><content>Airway Confirmation:                               Bilateral Breath Sounds<\/content><\/paragraph><paragraph><content>Airway Confirmation:                               Positive ETCO2<\/content><\/paragraph><paragraph><content>Airway Confirmation:                               Good chest rise<\/content><\/paragraph><paragraph><content>Observations: <\/content><\/paragraph><paragraph><content>Atraumatic Intubation<\/content><\/paragraph><paragraph><content>Observations: <\/content><\/paragraph><paragraph><content>No complications<\/content><\/paragraph><\/section><\/body><\/levelone>|\r\n"}
//            org.apache.avro.AvroRuntimeException: java.io.IOException: Invalid sync!
//                at org.apache.avro.file.DataFileStream.hasNext(DataFileStream.java:210)
//                at Kerberos.dcBDDev3_ReadHDFS_AvroFile_3.main(dcBDDev3_ReadHDFS_AvroFile_3.java:45)
//            Caused by: java.io.IOException: Invalid sync!
//                at org.apache.avro.file.DataFileStream.nextRawBlock(DataFileStream.java:293)
//                at org.apache.avro.file.DataFileStream.hasNext(DataFileStream.java:198)
//                ... 1 more
            
//            BufferedInputStream inStream = new BufferedInputStream(currHadoopFS.open(avroFilePath));
//            DataFileStream<GenericRecord> reader = new DataFileStream <GenericRecord>(inStream, new GenericDatumReader<GenericRecord>());
//            Schema schema = reader.getSchema();
//            System.out.println("\n*1* The schema of the avro file:\n\t" + schema.toString());
//            
//            System.out.println("\n*2* The Data of the avro file:\n\t" );
//            long rowCount = 0;
//            for (GenericRecord datum : reader) {
//                rowCount++;
//                System.out.println("(" + rowCount + ") " + datum);
// 
//            }
//
//            reader.close();
//            System.out.println("\n --- Done - Retrieving all the data of the avro file!!!" );
            
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

