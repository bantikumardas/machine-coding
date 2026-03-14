package com.example.MachineCoding.Models.Loan;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Entity
@Data
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "Default Interest Rate is mandatory")
    private double defInterestRate;
    private double maxInterestRate;
    private double minInterestRate;
    @NotBlank(message = "Default Tenure in Months is mandatory")
    private int defTenureInMonths;
    private int maxTenureInMonths;
    private int minTenureInMonths;
    @NotBlank(message = "Default Loan Amount is mandatory")
    @Min(value = 5000, message = "Default Loan Amount should be at least 5000")
    @Max(value = 500000, message = "Default Loan Amount should not exceed 500000")
    private int defLoanAmount;
    private int maxLoanAmount;
    private int minLoanAmount;
    @NotBlank(message = "Default Processing Fee is mandatory")
    private int processingFee;
    @NotBlank(message = "Default Moratorium in Days is mandatory")
    private int defMoratoriumInDays;
    private int maxMoratoriumInDays;
    private int minMoratoriumInDays;
    private Boolean isActive;
    private Boolean isGuarantorRequired;
    private int maxGuarantorsAllowed;
    private int minGuarantorsRequired;
    @NotBlank(message = "Default Guarantors Required is mandatory")
    private int defGuarantorsRequired;
    private Boolean isInsuranceRequired;
    @NotBlank(message = "Default Number of Installments is mandatory")
    private int defNumberOfInstallments;
    private int maxNumberOfInstallments;
    private int minNumberOfInstallments;
    @NotBlank(message = "Type of Loan is mandatory")
    private TYPE_OF_LOAN typeOfLoan;


}
