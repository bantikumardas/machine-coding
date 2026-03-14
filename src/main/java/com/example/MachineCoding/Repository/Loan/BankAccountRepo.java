package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
}
