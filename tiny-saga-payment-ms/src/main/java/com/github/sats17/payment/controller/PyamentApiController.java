package com.github.sats17.payment.controller;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.entity.TransactionRepository;
import com.github.sats17.payment.model.v2.PaymentProcessRequest;
import com.github.sats17.payment.service.PaymentService;
import com.github.sats17.payment.util.AppUtils;


import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RequestMapping("/v2/api/payment")
public class PyamentApiController {

	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	PaymentService paymentService;

	@GetMapping("/dev/healthcheck")
	public String getHealthCheck() {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		return "ok ok health from transaction";
	}

//	@GetMapping("/dev/transactions")
//	public List<Transaction> getAllTransactions() {
//		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
//		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
//		return StreamSupport.stream(transactionIterable.spliterator(), false).toList();
//	}

//	@GetMapping("/dev/transactions/order")
//	public List<Transaction> getAllTransactionsForOrder(@RequestParam String orderId) {
//		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
//		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
//		return StreamSupport.stream(transactionIterable.spliterator(), false).filter(transaction -> {
//			return transaction.getOrderId().equals(orderId);
//		}).sorted((t1, t2) -> {
//			return t1.getTimestamp().compareTo(t2.getTimestamp());
//		}).toList();
//	}

//	@PostMapping("/process")
//	public List<Transaction> updatePayment(@RequestBody PaymentProcessRequest request) {
//		AppUtils.printLog("Request recived for payment process");
//		AppUtils.printLog("Request body => "+request.toString());
//		if(request.getPaymentProcess().equals(PaymentProcess.PAY)) {
//			paymentService.performPayment(request);
//		} else if(request.getPaymentProcess().equals(PaymentProcess.REFUND)) {
//			paymentService.performRefund(request);
//		}
//		
//		return null;
//	}

}
