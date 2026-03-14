package com.example.MachineCoding.Repository.Parking;

import com.example.MachineCoding.Models.Parking.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepo extends JpaRepository<Floor, Long> {
}
