package com.example.MachineCoding.Repository.SplitWise;

import com.example.MachineCoding.Models.SplitWise.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
}
