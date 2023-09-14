package com.gitub.sats17;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import com.github.sats17.payment.controller.KafkaController;


/**
 * Unit test for simple App.
 */
public class AppTest {
	
	
	public static void main(String[] args) throws InterruptedException {
		KafkaController controller = new KafkaController();
		controller.consume("{\"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\"correlationId\":\"8a2e2d59-9d36-4b87-8ae0-2a4eef15b7f6\",\"eventName\":\"ORDER_INITIATED\",\"version\":\"1.0\",\"timestamp\":214134323,\"orderId\":\"12345\",\"userId\":\"1\",\"orderStatus\":\"INITIATED\",\"paymentType\":\"WALLET\",\"productId\":\"123asf-sfa-2a\",\"productQuantity\":2,\"price\":5000}");

	}
	
}
