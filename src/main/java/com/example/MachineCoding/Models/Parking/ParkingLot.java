package com.example.MachineCoding.Models.Parking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private int totalFloors;

    public ParkingLot(String name, String location, int totalFloors) {
        this.name = name;
        this.location = location;
        this.totalFloors = totalFloors;
    }

    public ParkingLot() {
    }

    public ParkingLot(Long id, String name, String location, int totalFloors) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.totalFloors = totalFloors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    @Override
    public String toString() {
        return "parkingLot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", totalFloors=" + totalFloors +
                '}';
    }
}

