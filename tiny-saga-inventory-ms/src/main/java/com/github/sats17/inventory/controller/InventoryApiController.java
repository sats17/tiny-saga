package com.github.sats17.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.inventory.entity.Inventory;
import com.github.sats17.inventory.entity.InventoryRepository;
import com.github.sats17.inventory.model.InventoryMsResponse;
import com.github.sats17.inventory.model.ReserveProductRequestBody;
import com.github.sats17.inventory.utils.AppUtils;

@RestController
@RequestMapping("/v2/api/inventory")
public class InventoryApiController {

	@Autowired
	InventoryRepository inventoryRepository;

	@PutMapping("/reserve/product/{productId}")
	public ResponseEntity<InventoryMsResponse> reserveInventoryProduct(@PathVariable String productId,
			@RequestBody ReserveProductRequestBody body) {

		Optional<Inventory> inventory = inventoryRepository.findById(productId);
		if (inventory.isEmpty()) {
			AppUtils.printLog("No product found in inventory, Check with administrator. ProductId: " + productId);
			InventoryMsResponse response = new InventoryMsResponse(500,
					"No product found in inventory, Check with administrator. ProductId: " + productId);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} else {
			int rowsAffected = inventoryRepository.updateProductQuantity(productId, body.getQuantity());
			if (rowsAffected <= 0) {
				AppUtils.printLog("Quantity is not sufficient for product " + inventory.get().getProductId()
						+ ". Available quantity is " + inventory.get().getProductQuantity());
				InventoryMsResponse response = new InventoryMsResponse(500,
						"Quantity is not sufficient for product in inventory.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
			AppUtils.printLog("Updated quantity for product with id " + inventory.get().getProductId());
			InventoryMsResponse response = new InventoryMsResponse(200, "Inventory updated succesfully");
			return ResponseEntity.status(HttpStatus.OK).body(response);

		}
	}

}
