package com.github.sats17.inventory.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, String> {
	
	@Transactional
	@Modifying(flushAutomatically = true)
//	 @Query("UPDATE Inventory p SET p.productQuantity = CASE " +
//	            "WHEN p.productQuantity >= :newQuantity THEN p.productQuantity - :newQuantity " +
//	            "ELSE p.productQuantity END " +
//	            "WHERE p.productId = :productId")  
	@Query("UPDATE Inventory p SET p.productQuantity = p.productQuantity - :newQuantity "
			+ "where p.productQuantity >= :newQuantity AND "
			+ "p.productId = :productId")
	int updateProductQuantity(@Param("productId") String productId, @Param("newQuantity") int quantity);
}

