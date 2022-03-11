package OS_Hardening;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

//import java.util.Properties;
//
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;


public class dcPostESecurityKafkaTrial {
	//-Djava -Djava.security.auth.login.config=C:\Users\m041785\desktop\dcBigData_Records\dcBigDataLearning&Work\Appliances\dcERHCT_NewTestingAppns_Dev\Kafka\kafka_client_jaas.conf
	//https://github.com/bkimminich/apache-kafka-book-examples/tree/master/src/test/kafka
	//https://kafka.apache.org/090/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
	//http://stackoverflow.com/questions/35934578/kafka-java-producer-with-kerberos
	//http://henning.kropponline.de/2016/02/21/secure-kafka-java-producer-with-kerberos/

	public static void main(String[] args) throws IOException {
		//C:\Users\m041785\desktop\dcBigData_Records\dcBigDataLearning&Work\Appliances\dcERHCT_NewTestingAppns_Dev\Kafka\		
		System.setProperty("java.security.krb5.conf", 	"C:\\Users\\m041785\\desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\dcERHCT_NewTestingAppns_Dev\\Kafka\\krb5_BDDev_EK.conf"); //krb5.ini
	    System.setProperty("sun.security.krb5.debug", "true");
	    System.setProperty("java.security.auth.login.config",  "C:\\Users\\m041785\\desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\dcERHCT_NewTestingAppns_Dev\\Kafka\\kafka_client_jaas.conf");
	    System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
        
        
//	     Configuration conf = new Configuration();
//		 UserGroupInformation.setConfiguration(conf);
//	     String keytabFilePathAndName = "C:\\Users\\m041785\\desktop\\dcBigData_Records\\dcBigDataLearning&Work\\Appliances\\dcERHCT_NewTestingAppns_Dev\\Kafka\\kafka.service.keytab";
//		 UserGroupInformation.loginUserFromKeytab("kafka/hdpr03en01.mayo.edu@MFAD.MFROOT.ORG",
//					keytabFilePathAndName);
	    
		 Properties props = new Properties();		 
	     //props.put("bootstrap.servers", "hdpr03en01.mayo.edu:6667");	
	     
		 props.put("bootstrap.servers", "hdpr03en01.mayo.edu:6667");	
	     props.put("metadata.broker.list", "hdpr03en01.mayo.edu:6667");
         props.put("serializer.class", "kafka.serializer.StringEncoder");
         props.put("request.required.acks", "1");
         props.put("security.protocol", "SASL_PLAINTEXT");
         props.put("advertised.listeners=", "PLAINTEXTSASL://hdpr03en01.mayo.edu:6667");
         props.put("zookeeperconnect", "hdpr03mn02.mayo.edu:2181,hdpr03mn01.mayo.edu:2181,hdpr03dn01.mayo.edu:2181");
         
	     props.put("group.id", "test");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("session.timeout.ms", "30000");
	     props.put("heartbeat.interval.ms", "3000000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");	     
	     
	     
	     
	     String TOPIC = "testKafka_dequan";
	     
	     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
	     consumer.subscribe(Arrays.asList(TOPIC));
	     while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(100);
	         for (ConsumerRecord<String, String> record : records)
	             System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
	     }
		
		
//		Properties props = new Properties();
//        props.put("bootstrap.servers", "hdpr03en01.mayo.edu:6667");
//      //props.put("metadata.broker.list", "hdpr03en01.mayo.edu:6667");hdpr03en01.mayo.edu:6667
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("security.protocol", "SASL_PLAINTEXT");
//        props.put("zookeeperconnect", "hdpr03mn02.mayo.edu:2181,hdpr03mn01.mayo.edu:2181,hdpr03dn01.mayo.edu:2181");
//        String TOPIC = "test_kalyan";
//  
//        Producer<String, String> producer = new KafkaProducer<>(props);
//		for (int i = 0; i < 100; i++){
//			  producer.send(new ProducerRecord<String, String>(TOPIC, Integer.toString(i), Integer.toString(i)));
//		}
//	     
//		producer.close();

	}//end main

}//end class
