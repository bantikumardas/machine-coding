package com.example.MachineCoding.Models.Loan;

import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;
    private String borrowerImageUrl;
    @ManyToOne
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProduct;
    private Double interestRate;
    private Integer tenureInMonths;
    private Integer loanAmount;
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<Installment> installments;
    private Long startDate;
    private Long endDate;
    private Boolean isClosed;
    private LOAN_STATUS status;
    @ManyToOne
    @JoinColumn(name = "disbursal_account_id")
    private BankAccount disbursalAccount;
    private String loanPurpose;
    private Double emiAmount;
    private Double totalRepayableAmount;
    private Double totalInterestAmount;
    private int moratoriumInDays;



}
