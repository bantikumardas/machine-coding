package com.example.MachineCoding.Repository.Parking;

import com.example.MachineCoding.Models.Parking.ParkingLot;
import com.example.MachineCoding.Models.Parking.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepo  extends JpaRepository<Ticket,  Long> {
    Optional<Ticket> findByVehicleNumberAndParkingLotAndExitTime(String vehicleNumber, ParkingLot parkingLot, long exitTime);

    Optional<Ticket> findByTicketNumber(String ticketNumber);
}
