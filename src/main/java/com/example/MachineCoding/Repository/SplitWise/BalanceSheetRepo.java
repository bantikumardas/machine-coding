package com.example.MachineCoding.Repository.SplitWise;

import com.example.MachineCoding.Models.SplitWise.BalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceSheetRepo extends JpaRepository<BalanceSheet, Long> {

    /**
     * One row per (group, debtor, creditor): {@code giver} owes {@code taker} this balance.
     */
    Optional<BalanceSheet> findByUserGroup_IdAndGiver_IdAndTaker_Id(
            Long groupId, Long giverId, Long takerId);

    List<BalanceSheet> findByUserGroup_Id(Long groupId);
}
