package com.example.MachineCoding.Models.SplitWise;

import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double spendingAmount;
    private String description;
    private String category;
    @ManyToOne
    @JoinColumn(name = "paid_by_id")
    private User paidBy;
    private String status;//active, settled, cancelled, Initiated
    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Participant> participants;
    private Timestamp transactionDate;

    @PreUpdate
    protected void transaction() {
        transactionDate = new Timestamp(System.currentTimeMillis());
    }
    @PrePersist
    protected void onCreate() {
        transactionDate = new Timestamp(System.currentTimeMillis());
    }
}
