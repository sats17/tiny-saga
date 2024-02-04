package com.github.sats17.payment.service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.sats17.payment.config.Enums.PaymentFailReason;
import com.github.sats17.payment.config.Enums.PaymentStatus;
import com.github.sats17.payment.config.Enums.TransactionType;
import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.entity.TransactionRepository;
import com.github.sats17.payment.exception.WalletException;
import com.github.sats17.payment.model.KafkaEventRequest;
import com.github.sats17.payment.model.PaymentMsResponse;
import com.github.sats17.payment.model.WalletMsResponse;
import com.github.sats17.payment.model.v2.PaymentProcessRequest;
import com.github.sats17.payment.util.AppUtils;

@Service
public class PaymentService {

	@Value("${walletMs.host}")
	private String walletMsHost;

	@Value("${walletMs.basePath}")
	private String walletMsBasePath;

	@Autowired
	WalletService walletService;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	ExecutorService cachedThreadPool;

	public PaymentMsResponse processPayment(PaymentProcessRequest request) throws WalletException {

		WalletMsResponse response = walletService.debitAmount(walletMsHost + walletMsBasePath, request.getUserId(),
				request.getPrice());
		if (response == null) {
			return new PaymentMsResponse(500, "Server error occured from wallet MS, while debiting amount");
		} else if (response.getStatus() == 20000) {
			AppUtils.printLog("Amount debited successfully for userId " + request.getUserId());
			Transaction transaction = buildTransaction(request, "Amount debit is done", PaymentStatus.PAYMENT_DONE,
					TransactionType.WITHDRAWAL);
			saveTransactionAsync(transaction);
			return new PaymentMsResponse(200, "Payment is succesful");
		} else if (response.getStatus() == 40001) {
			Transaction transaction = buildTransaction(request, "Payment cannot be proceed, due to insufficient fund",
					PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
			saveTransactionAsync(transaction);
			AppUtils.printLog(
					"Payment cannot be proceed, due to insufficient fund. Repsonse -> " + response.toString());
			return new PaymentMsResponse(400, "Payment cannot be proceed, due to insufficient fund");
		} else if (response.getStatus() == 40002) {
			Transaction transaction = buildTransaction(request,
					"Payment cannot be proceed, due to user not found in wallet db.", PaymentStatus.PAYMENT_FAILED,
					TransactionType.WITHDRAWAL);
			saveTransactionAsync(transaction);
			AppUtils.printLog("Payment cannot be proceed, due to user not found. Repsonse -> " + response.toString());
			return new PaymentMsResponse(404, "Payment cannot be process, " + response.getResponseMessage());
		} else {
			Transaction transaction = buildTransaction(request, "Payment cannot be proceed",
					PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
			saveTransactionAsync(transaction);
			AppUtils.printLog(
					"Server error occured from wallet MS, while debiting amount. Repsonse -> " + response.toString());
			return new PaymentMsResponse(500, "Server error occured from wallet MS, while debiting amount");

		}
	}

	private Transaction buildTransaction(PaymentProcessRequest request, String description, PaymentStatus paymentStatus,
			TransactionType transactionType) {
		Transaction transaction = new Transaction();
		transaction.setOrderId(request.getOrderId());
		transaction.setAmount(request.getPrice());
		transaction.setDescription(description);
		transaction.setUserId(request.getUserId());
		transaction.setTimestamp(AppUtils.generateEpochTimestamp());
		transaction.setCurrency("INR");
		transaction.setTransactionId(AppUtils.generateUniqueID());
		transaction.setPaymentStatus(paymentStatus);
		transaction.setTransactionType(transactionType);

		return transaction;
	}

	private void saveTransactionAsync(Transaction transaction) {
		cachedThreadPool.submit(() -> {
			transactionRepository.save(transaction);
		});
	}
}
