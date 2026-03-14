package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanProductRepo extends JpaRepository<LoanProduct, Long> {
}
