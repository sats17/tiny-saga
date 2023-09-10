package com.github.sats17.wallet.controller;

import java.util.Optional;

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


// http://localhost:8086/swagger-ui/index.html#/
@RestController
@RequestMapping("/v1/api/wallet")
public class WalletController {

	@Autowired
	private WalletRepository walletRepository;

	@GetMapping("/amount")
	public ResponseEntity<Response> getAmount(@RequestParam Long userId) {
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			Response response = new Response(2000, wallet.getAmount(), "Amount fetched successfully.");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			Response response = new Response(4000, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/amount")
	public ResponseEntity<Response> postAmount(@RequestParam Long userId, @RequestParam Double amount) {
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			wallet.setAmount(wallet.getAmount() + amount);
			Wallet updatedWallet = walletRepository.save(wallet);
			Response response = new Response(2000, updatedWallet.getAmount(), "Amount added successfully.");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			Response response = new Response(4000, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/debit")
	public ResponseEntity<Response> debitAmount(@RequestParam Long userId, @RequestParam Double amount) {
		Optional<Wallet> walletOptional = walletRepository.findById(userId);
		if (walletOptional.isPresent()) {
			Wallet wallet = walletOptional.get();
			Double currentBalance = wallet.getAmount();
			if (currentBalance >= amount) {
				wallet.setAmount(currentBalance - amount);
				Wallet updatedWallet = walletRepository.save(wallet);
				Response response = new Response(2000, updatedWallet.getAmount(), "Amount debited successfully.");
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				Response response = new Response(4000, "Insufficient balance in wallet.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} else {
			Response response = new Response(4000, "UserId not present in wallet.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}
