package com.example.MachineCoding.Controller.Loan;

import com.example.MachineCoding.Models.Loan.LoanProduct;
import com.example.MachineCoding.Service.Loan.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("loan-product")
public class LoanProductController {
    @Autowired
    private LoanProductService loanProductService;

    @PostMapping
    public ResponseEntity<?> createLoanProduct(@Valid @RequestBody LoanProduct loanProduct, @RequestHeader("token") String token){
        return loanProductService.createLoanProduct(loanProduct, token);
    }
}
