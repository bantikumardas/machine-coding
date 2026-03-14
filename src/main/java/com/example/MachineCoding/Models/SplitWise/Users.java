package com.example.MachineCoding.Models.SplitWise;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "person")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Length(min = 2, max = 20)
    private String name;
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Column(unique = true, nullable = false)
    private String email;
    private String hashPassword;
    private Timestamp dateCreated;
    private Timestamp dateUpdated;

    @PrePersist
    protected void onCreate() {
        dateCreated = new Timestamp(System.currentTimeMillis());
        dateUpdated = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = new Timestamp(System.currentTimeMillis());
    }


}
