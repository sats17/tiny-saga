package com.github.sats17.inventory;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.sats17.inventory.entity.Inventory;
import com.github.sats17.inventory.entity.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	@Autowired
	InventoryRepository inventoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public void ingest_data() throws Exception {
		// insert five users with random amounts
		Random rand = new Random();
		for (int i = 1; i <= 15; i++) {
			Inventory inventory = new Inventory();
			inventory.setProductId(String.valueOf(i));
			inventory.setProductQuantity(i);
			Inventory output = inventoryRepository.save(inventory);
			System.out.println("Inserted inventory record with ID: " + output.getProductId() + " and quantity: "
					+ output.getProductQuantity());

		}
	}

}