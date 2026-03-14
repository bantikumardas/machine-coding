package com.example.MachineCoding.Models.Loan;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Installment {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
    private Double amount;
    private Boolean isPaid;
    private Double principleAmount;
    private Double interestAmount;
    private Long dueDate;
    private Long paymentDate;
    private Integer installmentNumber;
    private String paymentMethod;
    private String transactionId;

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

}
