package com.github.sats17.inventory.controller;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/products/consume")
	public List<Inventory> consumeProduct(@RequestParam String productId, @RequestParam int productQuantity) {
		KafkaEventRequest eventRequest = new KafkaEventRequest();
		eventRequest.setProductId(productId);
		eventRequest.setProductQuantity(productQuantity);
		kafkaController.isInventoryAvailable(eventRequest);
		return getAllProducts();
	}
	
	@GetMapping("/products")
	public List<Inventory> getAllProducts() {
		AppUtils.printLog("Data present in transaction DB " + repository.count());
		Iterable<Inventory> transactionIterable = repository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).toList();
	}

}
