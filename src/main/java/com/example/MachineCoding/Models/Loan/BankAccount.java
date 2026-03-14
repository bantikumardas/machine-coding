package com.example.MachineCoding.Models.Loan;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private String accountHolderName;

}
