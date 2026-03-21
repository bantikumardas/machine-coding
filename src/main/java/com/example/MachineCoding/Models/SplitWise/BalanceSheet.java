package com.example.MachineCoding.Models.SplitWise;

import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Net debt within a group between two members.
 * <p>
 * Semantics: {@code giver} (debtor) owes {@code taker} (creditor) {@code balance} amount.
 * Example: Alice paid for dinner; Bob's share is 20 → giver=Bob, taker=Alice, balance += 20.
 */
@Entity
@Data
@Table(
        name = "balance_sheet",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_balance_group_giver_taker",
                columnNames = {"user_group_id", "giver_id", "taker_id"}
        )
)
public class BalanceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_group_id")
    private UserGroup userGroup;

    /** User who owes money (debtor). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "giver_id")
    private User giver;

    /** User who is owed money (creditor). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "taker_id")
    private User taker;

    /** Amount {@code giver} owes {@code taker} (same currency as transactions). */
    @Column(nullable = false)
    private Double balance;
}
