//package com.github.sats17.saga.order.configuration;
//
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Properties;
//
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.LongDeserializer;
//import org.apache.kafka.common.serialization.LongSerializer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Uncomment me if you want to make raw connection to kafka client.
// * 
// * <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
//		<dependency>
//			<groupId>org.apache.kafka</groupId>
//			<artifactId>kafka-clients</artifactId>
//			<version>3.2.0</version>
//		</dependency>
// * 
// * @author sats17
// *
// */
//@Configuration
//public class RawKafkaConfiguration {
//
//    private final static String BOOTSTRAP_SERVERS =
//            "localhost:9092";
//    
//    @Bean
//    public Producer<Long, String> kafkaProducer() {
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                                            BOOTSTRAP_SERVERS);
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                                        LongSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                                    StringSerializer.class.getName());
//        return new KafkaProducer<>(props);
//    }
//    
//    @Bean
//    public Consumer<Long, String> kafkaConsumer() {
//    	final Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                                    BOOTSTRAP_SERVERS);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                                    "OrderMS");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                LongDeserializer.class.getName());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class.getName());
//        
//        Consumer<Long, String> consumer =
//                new KafkaConsumer<>(props);
//        List<String> topics = new ArrayList<>();
//        topics.add("order-topic");
//        consumer.subscribe(topics);
//        ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMillis(5));
//        records.forEach(record -> {
//        	System.out.println("Record "+record);
//        });
//        return consumer;
//    }
//	
//}
