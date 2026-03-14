package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepo extends JpaRepository<Installment ,Long> {
}
