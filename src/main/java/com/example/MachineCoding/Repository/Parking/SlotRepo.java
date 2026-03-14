package com.example.MachineCoding.Repository.Parking;

import com.example.MachineCoding.Models.Parking.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepo extends JpaRepository<Slot, Long> {
    @Query("SELECT s FROM Slot s " +
            "WHERE s.isOccupied = :isOccupied " +
            "AND s.floor.parkingLot.id = :parkingLotId " +
            "AND s.vehicleType = :vehicleType")
    List<Slot> findByIsOccupiedAndParkingLot(@Param("parkingLotId") Long parkingLotId,@Param("isOccupied") boolean isOccupied,@Param("vehicleType") String vehicleType);
}
