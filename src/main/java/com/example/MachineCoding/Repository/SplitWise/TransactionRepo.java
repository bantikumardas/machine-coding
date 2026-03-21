package com.example.MachineCoding.Repository.SplitWise;

import com.example.MachineCoding.Models.SplitWise.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findByGroup_IdAndStatus(Long groupId, String status);
}
