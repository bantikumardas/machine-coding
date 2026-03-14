package com.example.MachineCoding.Controller.Loan;

import com.example.MachineCoding.DTO.LoanDTO;
import com.example.MachineCoding.Models.Loan.Loan;
import com.example.MachineCoding.Service.Loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("loan")
public class LoanController{
    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<?> loan(@RequestBody LoanDTO loan, @RequestHeader("token") String token) {
        // Logic to create loan
        return loanService.createLoan(loan, token);


    }
}
