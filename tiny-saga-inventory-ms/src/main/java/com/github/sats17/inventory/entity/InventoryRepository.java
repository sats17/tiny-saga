package com.github.sats17.inventory.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, String> {
	
	@Modifying
    @Query("UPDATE Inventory p SET p.productQuantity = CASE WHEN p.productQuantity >= :newQuantity THEN :newQuantity ELSE p.productQuantity END WHERE p.productId = :productId")
    int updateProductQuantity(@Param("productId") String productId, @Param("newQuantity") int quantity);
}

