package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepo extends JpaRepository<Loan, Long> {
}
