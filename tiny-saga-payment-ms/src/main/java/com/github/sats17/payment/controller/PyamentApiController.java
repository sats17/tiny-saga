package com.github.sats17.payment.controller;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.entity.TransactionRepository;
import com.github.sats17.payment.exception.WalletException;
import com.github.sats17.payment.model.PaymentMsResponse;
import com.github.sats17.payment.model.v2.PaymentProcessRequest;
import com.github.sats17.payment.service.PaymentService;
import com.github.sats17.payment.util.AppUtils;

@RestController
@RequestMapping("/v2/api/payment")
public class PyamentApiController {

	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	PaymentService paymentService;

	@GetMapping("/dev/healthcheck")
	public ResponseEntity<PaymentMsResponse> getHealthCheck() {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		return ResponseEntity.status(HttpStatus.OK).body(new PaymentMsResponse(200, "Payment Server and transaction DB is up and running."));
	}

	@GetMapping("/dev/transactions")
	public List<Transaction> getAllTransactions() {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).toList();
	}

	@GetMapping("/dev/transactions/order")
	public List<Transaction> getAllTransactionsForOrder(@RequestParam String orderId) {
		AppUtils.printLog("Data present in transaction DB " + transactionRepository.count());
		Iterable<Transaction> transactionIterable = transactionRepository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).filter(transaction -> {
			return transaction.getOrderId().equals(orderId);
		}).sorted((t1, t2) -> {
			return t1.getTimestamp().compareTo(t2.getTimestamp());
		}).toList();
	}

	@PostMapping("/order/pay")
	public ResponseEntity<PaymentMsResponse> processPaymentForOrderPay(@org.springframework.web.bind.annotation.RequestBody PaymentProcessRequest request) {
		System.out.println(request.toString());
		try {
			PaymentMsResponse response = paymentService.processPayment(request);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (WalletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
