package com.github.sats17.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet")
public class Transaction {
	
    @Id
    private Long transactionId;
    
    private Long userId;

}
