package com.example.MachineCoding.Models.Parking;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int slotNumber;
    private boolean isOccupied;
    private String vehicleType;
    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    public Slot(Long id, int slotNumber, boolean isOccupied, String vehicleType, Floor floor) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.isOccupied = isOccupied;
        this.vehicleType = vehicleType;
        this.floor = floor;
    }
    public Slot(){}

    public Slot(int slotNumber, boolean isOccupied, String vehicleType, Floor floor) {
        this.slotNumber = slotNumber;
        this.isOccupied = isOccupied;
        this.vehicleType = vehicleType;
        this.floor = floor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "slot{" +
                "id=" + id +
                ", slotNumber=" + slotNumber +
                ", isOccupied=" + isOccupied +
                ", vehicleType='" + vehicleType + '\'' +
                ", floor=" + floor +
                '}';
    }
}
