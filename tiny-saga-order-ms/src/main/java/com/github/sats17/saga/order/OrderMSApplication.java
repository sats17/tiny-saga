package com.github.sats17.saga.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.repository.OrderRepository;


@SpringBootApplication
public class OrderMSApplication {
	
	@Autowired
	OrderRepository orderRepository;
	
    public static void main( String[] args )
    {
    	SpringApplication.run(OrderMSApplication.class, args);
    }
    
    @Bean
    public void ingestTempData() {
    	Order order1 = new Order();
    	order1.setOrderId(11111L);
    	order1.setProductId(12312L);
    	orderRepository.save(order1);
    	orderRepository.findAll().forEach(test -> {
    		System.out.println(test);
    	});
    }
    
    
}
