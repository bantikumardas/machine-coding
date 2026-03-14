package com.example.MachineCoding.Models.Loan;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
    private Long loginTime;
    private Long logoutTime;
    private Long tokenExpiryTime;
    private ROLE role;
    private Boolean isActive;
}
