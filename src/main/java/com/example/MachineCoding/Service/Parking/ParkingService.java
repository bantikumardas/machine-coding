package com.example.MachineCoding.Service.Parking;

import com.example.MachineCoding.Models.Parking.Floor;
import com.example.MachineCoding.Models.Parking.ParkingLot;
import com.example.MachineCoding.Models.Parking.Slot;
import com.example.MachineCoding.Repository.Parking.FloorRepo;
import com.example.MachineCoding.Repository.Parking.ParkingRepo;
import com.example.MachineCoding.Repository.Parking.SlotRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParkingService {

    @Autowired
    private ParkingRepo parkingRepo;
    @Autowired
    private FloorRepo floorRepo;
    @Autowired
    private SlotRepo slotRepo;

    public ResponseEntity<?> addParking(Map<String, Object> request) {
        int floors = (Integer) request.get("floors");
        String location = (String) request.get("location");
        String name = (String) request.get("name");
        if(floors <= 0 || location == null || name == null){
            return ResponseEntity.badRequest().body("Invalid input");
        }
        // Logic to create parking lot and save to database
        ParkingLot parkingLot=new ParkingLot(name, location, floors);
        ParkingLot res=parkingRepo.save(parkingLot);
        ParkingResponseDTO responseDTO=new ParkingResponseDTO();
        responseDTO.setId(res.getId());
        responseDTO.setName(res.getName());
        responseDTO.setLocation(res.getLocation());
        responseDTO.setTotalFloors(res.getTotalFloors());
        for(int i=1;i<=floors;i++){
            Floor floor=new Floor(parkingLot, i);
            Floor fRes=floorRepo.save(floor);
            responseDTO.getFloorList().add(fRes);
        }
        return ResponseEntity.ok(responseDTO);
    }

    public ResponseEntity<?> addSlot(Long floorId, String slotType, int slotNumber) {
        if(slotNumber <= 0 || slotType == null){
            return ResponseEntity.badRequest().body("Invalid input");
        }
        Optional<Floor> floor=floorRepo.findById(floorId);
        if(floor.isEmpty()){
            return ResponseEntity.badRequest().body("Floor not found");
        }
        Slot slot=new Slot(slotNumber, false, slotType, floor.get());
        slotRepo.save(slot);
        return ResponseEntity.ok("Slot added successfully");
    }

    public ResponseEntity<?> addMultipleSlots(Long floorId, List<String> slotTypes, List<Integer> slotNumbers){
        if(slotTypes.size() != slotNumbers.size()){
            return ResponseEntity.badRequest().body("Slot numbers and slot types size should be same");
        }
        Optional<Floor> floor=floorRepo.findById(floorId);
        if(floor.isEmpty()){
            return ResponseEntity.badRequest().body("Floor not found");
        }
        for(int i=0;i<slotTypes.size();i++){
            Slot slot=new Slot(slotNumbers.get(i), false, slotTypes.get(i), floor.get());
            slotRepo.save(slot);
        }
        return ResponseEntity.ok("Slots added successfully");
    }
}

class ParkingResponseDTO{
    private Long id;
    private String name;
    private String location;
    private int totalFloors;
    private List<Floor> floorList;

    public ParkingResponseDTO() {
        this.floorList=new ArrayList<>();
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

    public List<Floor> getFloorList() {
        return floorList;
    }

    public void setFloorList(List<Floor> floorList) {
        this.floorList = floorList;
    }
}
