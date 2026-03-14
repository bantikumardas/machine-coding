package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepo extends JpaRepository<Login, Long> {
    Optional<Login> findByToken(String token);
}
