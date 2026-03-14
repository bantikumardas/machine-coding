package com.example.MachineCoding.Models.Loan;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private ROLE role;
    private String password;
    private GENDER gender;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;




}
