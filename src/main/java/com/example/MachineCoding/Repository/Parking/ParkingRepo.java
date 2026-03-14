package com.example.MachineCoding.Repository.Parking;

import com.example.MachineCoding.Models.Parking.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRepo extends JpaRepository<ParkingLot, Long> {
}
