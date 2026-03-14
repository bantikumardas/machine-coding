package com.example.MachineCoding.Repository.Loan;

import com.example.MachineCoding.Models.Loan.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
}
