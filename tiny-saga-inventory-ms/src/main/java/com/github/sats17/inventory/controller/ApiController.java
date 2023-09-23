package com.github.sats17.inventory.controller;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.inventory.entity.Inventory;
import com.github.sats17.inventory.entity.InventoryRepository;
import com.github.sats17.inventory.model.KafkaEventRequest;
import com.github.sats17.inventory.utils.AppUtils;

@RestController
@RequestMapping("/v1/api/inventory")
public class ApiController {
	
	@Autowired
	KafkaController kafkaController;
	
	@Autowired
	InventoryRepository repository;
	
	@GetMapping("/test")
	public void test() {
		KafkaEventRequest eventRequest = new KafkaEventRequest();
		eventRequest.setProductId("12");
		eventRequest.setProductQuantity(23);
		kafkaController.isInventoryAvailable(eventRequest);
		System.out.println(repository.findById("2").get().toString());
	}
	
	@GetMapping("/inventory")
	public List<Inventory> getAllProducts() {
		AppUtils.printLog("Data present in transaction DB " + repository.count());
		Iterable<Inventory> transactionIterable = repository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).toList();
	}

}
