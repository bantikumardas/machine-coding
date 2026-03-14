package com.example.MachineCoding.Service.Loan;

import com.example.MachineCoding.DTO.LoanDTO;
import com.example.MachineCoding.Models.Loan.*;
import com.example.MachineCoding.Repository.Loan.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    @Autowired
    private LoginRepo loginRepo;
    @Autowired
    private LoanRepo loanRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LoanProductRepo loanProductRepo;
    @Autowired
    private BankAccountRepo bankAccountRepo;
    @Autowired
    private InstallmentRepo installmentRepo;
    public ResponseEntity<?> createLoan(LoanDTO loanReq, String token){
        if(loginRepo.findByToken(token).isEmpty() || loginRepo.findByToken(token).get().getTokenExpiryTime()<System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        ROLE role=loginRepo.findByToken(token).get().getRole();
        if(role!=ROLE.AGENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only agent can create loan");
        }

        User borrower=userRepo.findById(loanReq.getBorrowerId()).orElse(null);
        if(borrower==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid borrower id");
        }
        User agent=userRepo.findById(loanReq.getAgentId()).orElse(null);
        if(agent==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid agent id");
        }
        LoanProduct loanProduct=loanProductRepo.findById(loanReq.getLoanProductId()).orElse(null);
        if(loanProduct==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid loan product id");
        }
        BankAccount disbursalAccount=bankAccountRepo.findById(loanReq.getDisbursalAccountId()).orElse(null);
        if(disbursalAccount==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid disbursal account id");
        }

        //create loan
        Loan loan=new Loan();
        loan.setBorrower(borrower);
        loan.setAgent(agent);
        loan.setBorrowerImageUrl(loanReq.getBorrowerImageUrl());
        loan.setLoanProduct(loanProduct);
        loan.setInterestRate(loanProduct.getDefInterestRate());
        loan.setTenureInMonths(loanProduct.getDefTenureInMonths());
        loan.setLoanAmount(loanReq.getLoanAmount());
        loan.setStartDate(loanReq.getStartDate());
        loan.setEndDate(System.currentTimeMillis()+loan.getTenureInMonths()*30L*24*60*60*1000);
        loan.setIsClosed(false);
        loan.setStatus(LOAN_STATUS.BEING);
        loan.setDisbursalAccount(disbursalAccount);
        loan.setLoanPurpose(loanReq.getLoanPurpose());
        loan.setMoratoriumInDays(loanReq.getMoratoriumInDays());
        Loan createdLoan=loanRepo.save(loan);

        List<Installment> installments=new ArrayList<>();
        int numberOfInstallments=loanProduct.getDefNumberOfInstallments();
        double totalRepayableAmount=0;
        double totalInterestAmount=0;
        double emiAmount=0;
        for(int i=0;i<numberOfInstallments;i++) {
            Installment installment=new Installment();
            installment.setLoan(createdLoan);
            double totalAmount=createdLoan.getLoanAmount();
            double amountWithoutInterest=totalAmount/numberOfInstallments;
            double amt=((createdLoan.getInterestRate()+100)/100);
            double year=(createdLoan.getTenureInMonths()*1.0)/12;
            double interestAmount=(totalAmount*Math.pow(amt,year))/numberOfInstallments;
            installment.setAmount(amountWithoutInterest+interestAmount);
            emiAmount=amountWithoutInterest+interestAmount;
            installment.setIsPaid(false);
            installment.setPrincipleAmount(amountWithoutInterest);
            installment.setInterestAmount(interestAmount);
            Long dueDate=createdLoan.getStartDate()+((i+1)*30L*24*60*60*1000);
            installment.setDueDate(dueDate);
            installment.setInstallmentNumber(i+1);
            totalRepayableAmount+=amountWithoutInterest+interestAmount;
            totalInterestAmount+=interestAmount;
            installmentRepo.save(installment);
        }
        createdLoan.setEmiAmount(emiAmount);
        createdLoan.setTotalRepayableAmount(totalRepayableAmount);
        createdLoan.setTotalInterestAmount(totalInterestAmount);
        Loan SavedLoan=loanRepo.save(createdLoan);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanReq);
    }
}
