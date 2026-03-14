package com.example.MachineCoding.DTO;

import lombok.Data;

@Data
public class LoanDTO {
        private Long id;
        private Long borrowerId;
        private Long agentId;
        private String borrowerImageUrl;
        private Long loanProductId;
        private Double interestRate;
        private Integer tenureInMonths;
        private Integer loanAmount;
        private Long startDate;
        private Long endDate;
        private Boolean isClosed;
        private Long disbursalAccountId;
        private String loanPurpose;
        private Double emiAmount;
        private Double totalRepayableAmount;
        private Double totalInterestAmount;
        private int moratoriumInDays;


}
