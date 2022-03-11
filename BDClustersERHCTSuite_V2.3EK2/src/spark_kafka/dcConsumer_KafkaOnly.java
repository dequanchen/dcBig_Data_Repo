package spark_kafka;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Properties;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import dcModelClasses.DayClock;
import dcModelClasses.ApplianceEntryNodes.BdCluster;

/**
* Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
* Date: 5/13, 18/2016; 12/15/2015; 1/17/2018
*/ 

public class dcConsumer_KafkaOnly {
    //CLI: kinit -k -t "C:\data\home\kafka.service.keytab" kafka/hdpr05en05.mayo.edu@MFAD.MFROOT.ORG
	//Run As: VM arguments: -Djava.security.auth.login.config=C:\data\home\kafka_client_jaas_keytab.conf
	//https://kafka.apache.org/090/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
	//http://kafka.apache.org/documentation.html#consumerconfigs
	//http://www.programcreek.com/java-api-examples/index.php?api=kafka.consumer.ConsumerConfig
	//-Djava.security.auth.login.config=C:\Security\BDDev\kafka_client_jaas_keytab.conf - must
	//-Djava.security.krb5.conf=C:\Security\BDDev\krb5_BDDev_EK.conf - not a must
	//-Djavax.security.auth.useSubjectCredsOnly=false or true - not a must
			
	public static void main(String[] args) throws IOException {
	    String bdClusterName = "BDDev3"; //BDProd..BDInt..BDSbx..BDDev..BDDev3...BDTest3...BDProd3       
	  
		long msToConsume = 10;
		String topic = "testSparkKafka";
		String brokerList = "hdpr05en03.mayo.edu:6667,hdpr05en04.mayo.edu:6667,hdpr05en05.mayo.edu:6667";
		//BDDev:  hdpr03en02.mayo.edu:6667,hdpr01kx01.mayo.edu:6667,hdpr01kx02.mayo.edu:6667...hdpr03en01.mayo.edu:6667
		//BDTest2: hdpr01en01.mayo.edu:6667
		//BDProd2:hdpr02en01.mayo.edu:6667,hdpr01kx03.mayo.edu:6667,hdpr01kx04.mayo.edu:6667...hdpr02en01.mayo.edu:6667
		//BDSdbx: hdpr04en01.mayo.edu:6667		
		
		//String srvLocalTestResultFilePathAndName = "/data/home/m041785/test/" + topic + "_java_result.txt";;
        
        if (args.length >= 1){
        	msToConsume = Long.parseLong(args[0]);        	
        }
        if (args.length >= 2){
        	topic = args[1];        	
        }
        if (args.length >= 3){
        	brokerList = args[2];
        }
        //if (args.length >= 4){
        //	srvLocalTestResultFilePathAndName = args[3];
        //}
             
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList); 
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "500");
        props.put("session.timeout.ms", "30000");
        
        props.put("auto.offset.reset", "earliest"); 
        props.put("connections.max.idle.ms", "2000");    
        
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        //props.put("metadata.broker.list", brokerList);        
        props.put("security.protocol", "PLAINTEXTSASL"); //PLAINTEXTSASL...SASL_PLAINTEXT
        //props.put("-Djava.security.auth.login.config", "/data/home/kafka_client_jaas_keytab.conf");
       //-Djava.security.auth.login.config=C:\data\home/kafka_client_jaas_keytab.conf 
       
        	
        //prepareFile (srvLocalTestResultFilePathAndName,  "Kafka Testing Results File");
	    //FileWriter outStream = new FileWriter(srvLocalTestResultFilePathAndName, true);				
	    //PrintWriter output = new PrintWriter (outStream);
	    
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList(topic));	
	   
//	    ArrayList <TopicPartition> topicPartitionList = new ArrayList<TopicPartition>();
//	    TopicPartition tp0 = new TopicPartition(topic, 0);
//	    TopicPartition tp1 = new TopicPartition(topic, 1);
//	    TopicPartition tp2 = new TopicPartition(topic, 2);	    
//	    topicPartitionList.add(tp0);
//	    topicPartitionList.add(tp1);
//	    topicPartitionList.add(tp2);	    
//	    consumer.assign(topicPartitionList);
//	    consumer.seekToBeginning(tp0);
		
	    StringBuilder sb = new StringBuilder();
	    String kafkaConsumingRec = "";
	    int processedRecordCount = 0;
		try {	
			 while (true) {				 				 
		         ConsumerRecords<String, String> records = consumer.poll(msToConsume);
		         int consumedRecCount = records.count();
		         System.out.print("\n\n*-1-* Total Consumed Message # - " + consumedRecCount + " from the Kafka topic - " + topic  + "\n\n");
		         
		         String recordInfo = "";
		         for (ConsumerRecord<String, String> record : records) {
		             processedRecordCount++;   
		        	 recordInfo = "	*-* (" + processedRecordCount + ") " + record.offset() + ": " +  record.key() + "," + record.value();
		        	 System.out.println(recordInfo);
		        	 sb.append(recordInfo + " \n" );
		        	 //output.println(recordInfo);
		        	 
			       	 //System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
		        	 if (processedRecordCount == consumedRecCount){	
		        		 recordInfo = "\n\n*-2-* Done - Consuming '" + processedRecordCount + "' messages from the Kafka topic - " + topic  + "\n\n"; 
		        		 System.out.println(recordInfo);
		        		 
			        	 //output.println(recordInfo);
		    	    	 consumer.close();
		    	    	 //output.close();
		    	 	     //outStream.close();	
		    	     }		        	 
		        	     	     
		         }		         
			 }	 
			
	    } finally {
	    	//consumer.close();
	    	//output.close();
	 	    //outStream.close();
	        String recordInfoSummary = "\n*** Done - Consuming '"+ msToConsume + "' ms or " + processedRecordCount + " of messages from the Kafka topic - " + topic + "\n";
		    System.out.print(recordInfoSummary  + "\n");	   
		    sb.append(recordInfoSummary + " \n" );
		    
		    kafkaConsumingRec = sb.toString();  
            sb.setLength(0);
            System.out.print(kafkaConsumingRec  + "\n");   
            
                           
            BdCluster currBdCluster = new BdCluster(bdClusterName);
            System.out.println("currBdCluster.getBdHdfsNnIPAddressAndPort(): " + currBdCluster.getBdHdfs1stNnIPAddressAndPort());            
            FileSystem currHadoopFS  = currBdCluster.getHadoopFS();
            
            String kafkaTestingResultsHdfsFilePathAndName = "/user/m041785/dcKafkaConsumingTestingResults.txt";
            Path outputPath = new Path(kafkaTestingResultsHdfsFilePathAndName);
            if (currHadoopFS.exists(outputPath)) {
                currHadoopFS.delete(outputPath, true);
                System.out.println("\n*** deleting existing Kafka Consuming Testing HDFS file: " + kafkaTestingResultsHdfsFilePathAndName);
            }
            
            FSDataOutputStream fsDataOutStream = currHadoopFS.create(outputPath, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutStream));
            
            bw.write(kafkaConsumingRec);
            DayClock currClock = new DayClock();                
            String currTime = currClock.getCurrentDateTime();   
            
            bw.append("Finished time is " + currTime + "n\n");
            bw.close();
            fsDataOutStream.close();
            
            currHadoopFS.close();
            
			System.exit(0);     
	    }//end try
	   	    
		
	    
	    
	    
	    
//		try {	
//			 while (true) {
//				 int recordCount = 1;				 
//		         ConsumerRecords<String, String> records = consumer.poll(msToConsume);
//		        		         	         
//		         for (ConsumerRecord<String, String> record : records) { 
//		        	 System.out.print("\n	*-* (" + recordCount + ") " + record.offset() + ": " +  record.key() + "," + record.value());
//			       	 //System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
//		       	     recordCount++;
//		         }		         
//			 }			 
//	     } finally {
//	    	 consumer.close();
//	 	     System.out.print("\n\n*** Done - Consuming the target number of messages from the Kafka topic - " + topic  + "\n\n");	    
//	 	    
//	 		 System.exit(0);     
//	     }//end try
		   
	    
	}//end main
	
//	private static void prepareFile (String localFilePathAndName, String fileNoticeInfo){
//		File aFile = new File (localFilePathAndName);
//		try {
//			//if (aFile.exists()){
//			//	aFile.delete();
//			//	System.out.println("\n .. Deleted file for dump_Backloading: \n" + localFilePathAndName);
//			//	aFile.createNewFile();
//			//	System.out.println("\n .. Created file for dump_Backloading: \n" + localFilePathAndName);
//			//}
//			
//			if (!aFile.exists()){
//				aFile.createNewFile();
//				System.out.println("\n .. Created file for " + fileNoticeInfo + ": \n" + localFilePathAndName);
//			}				
//		} catch (IOException e) {				
//			e.printStackTrace();
//		}			
//	}//end prepareFile
	
}//end class
