package com.github.sats17.payment.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.entity.TransactionRepository;
import com.github.sats17.payment.util.AppUtils;

@RestController
@RequestMapping("/v1/api/payment")
public class ApiController {

	@Autowired
	TransactionRepository transactionRepository;

	@GetMapping("/healthcheck")
	public String getHealthCheck() {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		return "ok ok health from transaction";
	}

	@GetMapping("/transactions")
	public List<Transaction> getAllTransactions() {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).toList();
	}

	@GetMapping("/transactions/order")
	public List<Transaction> getAllTransactionsForOrder(@RequestParam String orderId) {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).filter(transaction -> {
			return transaction.getOrderId().equals(orderId);
		}).sorted((t1, t2) -> {
			return t1.getTimestamp().compareTo(t2.getTimestamp());
		}).toList();
	}

}
