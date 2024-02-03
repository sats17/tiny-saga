package com.github.sats17.payment.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.sats17.payment.config.Enums.PaymentFailReason;
import com.github.sats17.payment.config.Enums.PaymentStatus;
import com.github.sats17.payment.config.Enums.TransactionType;
import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.exception.WalletException;
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

	public Object processPayment(PaymentProcessRequest request) throws WalletException {

		WalletMsResponse response = walletService.debitAmount(walletMsHost + walletMsBasePath, request.getUserId(),
				request.getPrice());
		if (response == null) {
			return new PaymentMsResponse(500, "Server error occured from wallet MS, while debiting amount");
		} else if (response.getStatus() == 20000) {
			AppUtils.printLog("Amount debited successfully for userId " + request.getUserId());
			return new PaymentMsResponse(200, "Payment is succesful");
//			Transaction transaction = buildTransaction(event, "Amount debit is done",
//					PaymentStatus.PAYMENT_DONE, TransactionType.WITHDRAWAL);
//			updateTransaction(transaction);
			//sendPaymentDoneEvent(event);
		} else if (response.getStatus() == 40001) {
//			Transaction transaction = buildTransaction(event,
//					"Payment cannot be proceed, due to insufficient fund", PaymentStatus.PAYMENT_FAILED,
//					TransactionType.WITHDRAWAL);
//			updateTransaction(transaction);
			return new PaymentMsResponse(400, "Payment cannot be proceed, due to insufficient fund");
//			AppUtils.printLog(
//					"Payment cannot be proceed, due to insufficient fund. Repsonse -> " + response.toString());
//			sendPaymentFailEvent(event, PaymentFailReason.INSUFFICIENT_FUND);
		} else {
			return new PaymentMsResponse(500, "Server error occured from wallet MS, while debiting amount");
//			Transaction transaction = buildTransaction(event, "Payment cannot be proceed",
//					PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
//			updateTransaction(transaction);
//			AppUtils.printLog(
//					"Invalid repsonse code from wallet MS, response is not matching as per contract. Repsonse -> "
//							+ response.toString());
//			sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
		}
	}

}
