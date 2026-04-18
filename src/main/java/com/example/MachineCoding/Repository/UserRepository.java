package com.example.MachineCoding.Repository;

import com.example.MachineCoding.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String query, String query1);
}
