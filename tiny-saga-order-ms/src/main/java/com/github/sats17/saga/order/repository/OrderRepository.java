package com.github.sats17.saga.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.sats17.saga.order.model.db.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {

}
