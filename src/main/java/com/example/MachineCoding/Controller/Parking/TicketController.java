package com.example.MachineCoding.Controller.Parking;

import com.example.MachineCoding.Service.Parking.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("park")
    public ResponseEntity<?> parkVehicle(@RequestBody Map<String, Object> request) throws Exception {
        String vehicleNumber = (String) request.get("vehicleNumber");
        String vehicleType = (String) request.get("vehicleType");
        Long parkingLotId = ((Number) request.get("parkingLotId")).longValue();
        return ticketService.parkVehicle(parkingLotId, vehicleNumber, vehicleType);
    }

    @PostMapping("unpark")
    public ResponseEntity<?> unparkVehicle(@RequestBody Map<String, Object> request) throws Exception {
        String ticketNumber = (String) request.get("ticketNumber");
        return ticketService.unparkVehicle(ticketNumber);
    }
}
