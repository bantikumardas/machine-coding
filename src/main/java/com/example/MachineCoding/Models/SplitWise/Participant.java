package com.example.MachineCoding.Models.SplitWise;


import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;
    private Double shareAmount;
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

}
