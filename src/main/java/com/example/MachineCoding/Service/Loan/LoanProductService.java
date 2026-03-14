package com.example.MachineCoding.Service.Loan;

import com.example.MachineCoding.Models.Loan.LoanProduct;
import com.example.MachineCoding.Models.Loan.ROLE;
import com.example.MachineCoding.Repository.Loan.LoanProductRepo;
import com.example.MachineCoding.Repository.Loan.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LoanProductService {
    @Autowired
    private LoanProductRepo loanProductRepo;
    @Autowired
    private LoginRepo loginRepo;

    public ResponseEntity<?> createLoanProduct(LoanProduct loanProduct, String token) {
        if(loginRepo.findByToken(token).isEmpty() || !loginRepo.findByToken(token).get().getIsActive()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        ROLE role=loginRepo.findByToken(token).get().getRole();
        if(role!=ROLE.ADMIN || role!=ROLE.LENDER || role!=ROLE.PARTNER_ADMIN) {
            return ResponseEntity.status(403).body("You not unauthorized to create loan product");
        }
        LoanProduct createdLoanProduct=loanProductRepo.save(loanProduct);
        return ResponseEntity.ok(createdLoanProduct);
    }
}
