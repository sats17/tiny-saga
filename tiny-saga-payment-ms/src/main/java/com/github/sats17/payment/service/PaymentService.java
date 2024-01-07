//package com.github.sats17.payment.service;
//
//import org.springframework.stereotype.Service;
//
//import com.github.sats17.grpc.payment.PaymentRequest;
//import com.github.sats17.grpc.payment.PaymentResponse;
//import com.github.sats17.grpc.payment.PaymentServiceGrpc;
//import com.github.sats17.payment.model.v2.PaymentProcessRequest;
//
///**
// * Written this class while doing grpc project.
// */
//@Service
//public class PaymentService extends PaymentServiceGrpc.PaymentServiceImplBase {
//
//	public void performPayment(PaymentProcessRequest request) {
//
//	}
//
//	public void performRefund(PaymentProcessRequest request) {
//
//	}
//
//	@Override
//	public void processPayment(com.github.sats17.grpc.payment.PaymentRequest request,
//			io.grpc.stub.StreamObserver<com.github.sats17.grpc.payment.PaymentResponse> responseObserver) {
//		System.out.println(request.toString());
//		PaymentResponse rp = PaymentResponse.newBuilder().setMessage("From server").build();
//		responseObserver.onNext(rp);
//		responseObserver.onCompleted();
//	}
//
//}
