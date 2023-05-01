package com.github.sats17.wallet;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.github.sats17.wallet.entity.Wallet;
import com.github.sats17.wallet.entity.WalletRepository;

@SpringBootApplication
public class WalletServiceApplication {
	
    @Autowired
    private WalletRepository walletRepository;

	public static void main(String[] args) {
		SpringApplication.run(WalletServiceApplication.class, args);
	}
	
	@Bean
    public void ingest_data() throws Exception {
        // insert five users with random amounts
        Random rand = new Random();
        for (int i = 1; i <= 5; i++) {
            Wallet wallet = new Wallet();
            wallet.setUserId((long) i);
            wallet.setAmount((double) rand.nextInt(1000));
            Wallet output = walletRepository.save(wallet);
            System.out.println("Inserted wallet record with ID: " + output.getUserId() + " and amount: " + output.getAmount());

        }
    }

}