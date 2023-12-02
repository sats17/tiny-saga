package com.github.sats17.wallet.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.wallet.entity.Response;
import com.github.sats17.wallet.entity.Wallet;
import com.github.sats17.wallet.entity.WalletRepository;
import com.github.sats17.wallet.utils.AppUtils;

// http://localhost:8086/swagger-ui/index.html#/
@RestController
@RequestMapping("/v1/api/wallet")
public class WalletController {

	@Autowired
	private WalletRepository walletRepository;

	@GetMapping("/dev/amount")
	public ResponseEntity<Response> getAmount(@RequestParam String userId) {
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			Response response = new Response(20000, wallet.getAmount(), "Amount fetched successfully.");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			Response response = new Response(40002, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
	
	@GetMapping("/dev/wallets")
	public List<Wallet> getUsers() {
		AppUtils.printLog("Data present in wallet DB " + walletRepository.count());
		Iterable<Wallet> transactionIterable = walletRepository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false)
                .collect(Collectors.toList());
	}

	@PostMapping("/dev/amount")
	public ResponseEntity<Response> postAmount(@RequestParam String userId, @RequestParam Double amount) {
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			wallet.setAmount(wallet.getAmount() + amount);
			Wallet updatedWallet = walletRepository.save(wallet);
			Response response = new Response(20000, updatedWallet.getAmount(), "Amount added successfully.");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			Response response = new Response(40002, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/debit")
	public ResponseEntity<Response> debitAmount(@RequestParam String userId, @RequestParam Double amount) {
		AppUtils.printLog("Request received for user " + userId + " to debit amount " + amount);
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			Double currentBalance = wallet.getAmount();
			if (currentBalance >= amount) {
				wallet.setAmount(currentBalance - amount);
				Wallet updatedWallet = walletRepository.save(wallet);
				Response response = new Response(20000, updatedWallet.getAmount(), "Amount debited successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				Response response = new Response(40001, "Insufficient balance in wallet.");
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
			}
		} else {
			Response response = new Response(40002, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/credit")
	public ResponseEntity<Response> creditAmount(@RequestParam String userId, @RequestParam Double amount) {
		AppUtils.printLog("Request received for user " + userId + " to debit amount " + amount);
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			Double currentBalance = wallet.getAmount();
			// TODO: Fix this transaction atomic.
			wallet.setAmount(currentBalance + amount);
			Wallet updatedWallet = walletRepository.save(wallet);
			Response response = new Response(20000, updatedWallet.getAmount(), "Amount debited successfully.");
			return ResponseEntity.status(HttpStatus.OK).body(response);

		} else {
			Response response = new Response(40002, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}
