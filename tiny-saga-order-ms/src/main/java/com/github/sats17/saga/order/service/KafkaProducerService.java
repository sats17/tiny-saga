//package com.github.sats17.saga.order.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Service;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//@Service
//public class KafkaProducerService {
//
//	@Autowired
//	public KafkaTemplate<String, String> kafkaTemplate;
//
//	public boolean publish(String topic, String data) {
//		kafkaTemplate.send(topic, data)
//					 .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
//
//						@Override
//						public void onSuccess(SendResult<String, String> result) {
//							System.out.println(result.toString());
//							// TODO Auto-generated method stub
//							System.out.println("inside success");
//			
//						}
//			
//						@Override
//						public void onFailure(Throwable ex) {
//							// TODO Auto-generated method stub
//							System.out.println("Inside failure");
//						}
//
//			});
//		System.out.println("Outside of future");
//		return true;
//	}
//
//}
