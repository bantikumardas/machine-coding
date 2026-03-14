package com.example.MachineCoding.Models.Parking;

import jakarta.persistence.*;

@Entity
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot;
    private int floorNumber;

    public Floor(ParkingLot parkingLot, int floorNumber) {
        this.parkingLot = parkingLot;
        this.floorNumber = floorNumber;
    }

    public Floor() {
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Floor(Long id, ParkingLot parkingLotId, int floorNumber, int totalSlots, int availableSlots) {
        this.id = id;
        this.parkingLot = parkingLotId;
        this.floorNumber = floorNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParkingLot getParkingLotId() {
        return parkingLot;
    }

    public void setParkingLotId(ParkingLot parkingLotId) {
        this.parkingLot = parkingLotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }


    @Override
    public String toString() {
        return "Floor{" +
                "id=" + id +
                ", parkingLotId=" + parkingLot +
                ", floorNumber=" + floorNumber +
                '}';
    }
}
