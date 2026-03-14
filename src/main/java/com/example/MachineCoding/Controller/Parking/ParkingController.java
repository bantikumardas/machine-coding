package com.example.MachineCoding.Controller.Parking;


import com.example.MachineCoding.Service.Parking.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("parking")
public class ParkingController{

    @Autowired
    private ParkingService parkingService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createParkingLot(@RequestBody Map<String, Object> request){
        return parkingService.addParking(request);
    }

    @PostMapping(path = "/addSlot", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSlot(Map<String, Object> request){
        Long floorId = (Long) request.get("floorId");
        String slotType = (String) request.get("slotType");
        int slotNumber = (Integer) request.get("slotNumber");
        return parkingService.addSlot(floorId, slotType, slotNumber);
    }

    @PostMapping(path = "/addMultipleSlots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMultipleSlots(@RequestBody SlotRequestDTO request){
        Long floorId =  request.getFloorId();
        if(request.getSlotNumbers().size()!=request.getSlotTypes().size()){
            return ResponseEntity.badRequest().body("Slot numbers and slot types size should be same");
        }
        List<String> slotTypes = request.getSlotTypes();
        List<Integer> slotNumbers = request.getSlotNumbers();

        return parkingService.addMultipleSlots(floorId, slotTypes, slotNumbers);
    }

}

class SlotRequestDTO{
    private Long floorId;
    private List<String> slotTypes;
    private List<Integer> slotNumbers;

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }


    public List<String> getSlotTypes() {
        return slotTypes;
    }

    public void setSlotTypes(List<String> slotTypes) {
        this.slotTypes = slotTypes;
    }

    public List<Integer> getSlotNumbers() {
        return slotNumbers;
    }

    public void setSlotNumbers(List<Integer> slotNumbers) {
        this.slotNumbers = slotNumbers;
    }
}
