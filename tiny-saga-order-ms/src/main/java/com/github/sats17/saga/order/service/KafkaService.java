package com.github.sats17.saga.order.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

	@Autowired
	public Producer<Long, String> producer;

	private final static String TOPIC = "order-topic";

	public void publish(String message) {
		long time = System.currentTimeMillis();
		final ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, 123L, message);

		RecordMetadata metadata;
		try {
			metadata = producer.send(record).get(5000, TimeUnit.MILLISECONDS);
			long elapsedTime = System.currentTimeMillis() - time;
			System.out.printf("sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n", record.key(),
					record.value(), metadata.partition(), metadata.offset(), elapsedTime);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
