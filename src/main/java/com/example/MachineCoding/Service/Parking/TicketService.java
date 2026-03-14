package com.example.MachineCoding.Service.Parking;

import com.example.MachineCoding.Models.Parking.Floor;
import com.example.MachineCoding.Models.Parking.ParkingLot;
import com.example.MachineCoding.Models.Parking.Slot;
import com.example.MachineCoding.Models.Parking.Ticket;
import com.example.MachineCoding.Repository.Parking.FloorRepo;
import com.example.MachineCoding.Repository.Parking.ParkingRepo;
import com.example.MachineCoding.Repository.Parking.SlotRepo;
import com.example.MachineCoding.Repository.Parking.TicketRepo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private FloorRepo floorRepo;
    @Autowired
    private SlotRepo slotRepo;
    @Autowired
    private ParkingRepo parkingRepo;

    @Transactional
    public ResponseEntity<?> parkVehicle(Long parkingLotId, String vehicleNumber, String vehicleType) throws Exception {
        Optional<ParkingLot> parkingLotOpt = parkingRepo.findById(parkingLotId);
        if (parkingLotOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Parking lot not found");
        }
        Optional<Ticket> existingTicketOpt = ticketRepo.findByVehicleNumberAndParkingLotAndExitTime(vehicleNumber, parkingLotOpt.get(), 0);
        if (existingTicketOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Vehicle already parked in this parking lot");
        }
        //find all slot of type vehicleType in parking lot and sort by floor number and slot number
        List<Slot> slots=slotRepo.findByIsOccupiedAndParkingLot(parkingLotId, false, vehicleType);
        if(slots.isEmpty()){
            return ResponseEntity.ok("Try again later, no slot available for this vehicle type");
        }
        //sort it by floor number and slot number
        List<Slot> availableSlot=slots.stream().sorted((a, b)->{
            if(a.getFloor().getFloorNumber()==b.getFloor().getFloorNumber()){
                return Integer.compare(a.getSlotNumber(), b.getSlotNumber());
            }
            return Integer.compare(a.getFloor().getFloorNumber(), b.getFloor().getFloorNumber());
        }).toList();
        Slot slot=availableSlot.get(0);
        slot.setOccupied(true);
        slotRepo.save(slot);
        Ticket ticket=new Ticket();
        ticket.setVehicleNumber(vehicleNumber);
        ticket.setEntryTime(System.currentTimeMillis());
        ticket.setParkingLot(parkingLotOpt.get());
        ticket.setSlot(slot);
        String ticketNumber=parkingLotId+"-"+slot.getFloor().getFloorNumber()+"-"+slot.getSlotNumber()+"-"+System.currentTimeMillis();
        ticket.setTicketNumber(ticketNumber);
        String path=generatePdfWithQr(ticketNumber);
        System.out.println("PDF generated at: "+path);
        return ResponseEntity.ok(ticketRepo.save(ticket));
    }

    public ResponseEntity<?> unparkVehicle(String ticketNumber) throws Exception {
        Optional<Ticket> ticketOpt = ticketRepo.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty() || ticketOpt.get().getExitTime() != 0) {
            return ResponseEntity.badRequest().body("Invalid ticket number");
        }
        Ticket ticket = ticketOpt.get();
        Slot slot = ticket.getSlot();
        slot.setOccupied(false);
        slotRepo.save(slot);
        ticket.setExitTime(System.currentTimeMillis());
        Double fee=calculateFee(ticket);
        ticket.setFee(fee);
        ticketRepo.save(ticket);
        return ResponseEntity.ok(ticket);
    }

    private Double calculateFee(Ticket ticket) {
        //₹0.8 per minute for bike and ₹1 for car and ₹1.3 the first 30 minutes and if less than 30 minutes then ₹10 for bike and ₹20 for car and ₹30 for Truck
        long duration = Math.abs(System.currentTimeMillis() - ticket.getEntryTime());
        long minutes = duration / (1000 * 60);
        System.out.println("Duration in minutes: " + minutes);
        String vehicleType = ticket.getSlot().getVehicleType();
        if (vehicleType.equalsIgnoreCase("BIKE")) {
            if (minutes <= 30) {
                return 10.0;
            } else {
                return Math.max(0.8 * minutes, 10);
            }
        }
        else if (vehicleType.equalsIgnoreCase("CAR")) {
            if (minutes <= 30) {
                return 20.0;
            } else {
                return Math.max(1.0 * minutes, 20);
            }
        }
        else {
            if (minutes <= 30) {
                return 30.0;
            } else {
                return Math.max(1.3 * minutes, 30);
            }
        }
    }

    public BufferedImage generateQRCode(String text) throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        BufferedImage image = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 250; x++) {
            for (int y = 0; y < 250; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return image;
    }

    public String generatePdfWithQr(String ticketNumber) throws Exception {


        // Sample Data (you can pass these as parameters)
        String vehicleNumber = "Jh-10J-3242";
        String vehicleType = "CAR";
        String entryTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        // 1️⃣ Generate QR
        BufferedImage qrImage = generateQRCode(ticketNumber);
        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", imageBaos);

        // 2️⃣ Define Path
        String folderPath = System.getProperty("user.dir") + "/TempData";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = folderPath + "/" + ticketNumber + ".pdf";

        // 3️⃣ Create PDF
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // ===============================
        // 🔥 1. HEADER
        // ===============================
        Paragraph header = new Paragraph("Parking Ticket")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(header);

        // ===============================
        // 🔥 2. HORIZONTAL LINE
        // ===============================
        SolidLine line = new SolidLine(1f);
        LineSeparator ls = new LineSeparator(line);
        document.add(ls);

        document.add(new Paragraph("\n")); // small spacing

        // ===============================
        //  3. TICKET DETAILS
        // ===============================
        document.add(new Paragraph("1. Vehicle Number: " + vehicleNumber));
        document.add(new Paragraph("2. Vehicle Type: " + vehicleType));
        document.add(new Paragraph("3. Entry Time: " + entryTime));
        document.add(new Paragraph("4. Ticket ID: " + ticketNumber));

        document.add(new Paragraph("\n\n")); // spacing before QR

        // ===============================
        // 4. CENTERED QR
        // ===============================
        Image qrCode = new Image(
                ImageDataFactory.create(imageBaos.toByteArray())
        );

        qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);
        qrCode.setWidth(150);
        qrCode.setHeight(150);

        document.add(qrCode);

        document.close();

        return filePath;
    }

}
