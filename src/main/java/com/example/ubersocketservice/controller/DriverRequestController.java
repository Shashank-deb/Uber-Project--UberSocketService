package com.example.ubersocketservice.controller;

import com.example.ubersocketservice.dto.RideRequestDTO;
import com.example.ubersocketservice.dto.RideResponseDTO;
import com.example.ubersocketservice.dto.UpdateBookingRequestDTO;
import com.example.ubersocketservice.dto.UpdateBookingResponseDTO;
import com.example.ubersocketservice.producers.KafkaProducerService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController  // CHANGED from @Controller to @RestController
@RequestMapping("/api/v1/socket")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "*"})  // Added class-level CORS
public class DriverRequestController {

    private final SimpMessagingTemplate simpleMessageTemplate;
    private final RestTemplate restTemplate;
    private final KafkaProducerService kafkaProducerService;

    public DriverRequestController(SimpMessagingTemplate simpleMessageTemplate,KafkaProducerService kafkaProducerService) {
        this.simpleMessageTemplate = simpleMessageTemplate;
        restTemplate=new RestTemplate();
        this.kafkaProducerService=kafkaProducerService;
    }

    /**
     * REST endpoint to receive new ride requests from Booking Service
     * Called by: Booking Service via Retrofit
     */
    @PostMapping("/newRide")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDTO rideRequestDTO) {
        System.out.println("===========================================");
        System.out.println("NEW RIDE REQUEST RECEIVED (REST)");
        System.out.println("===========================================");
        System.out.println("Passenger ID: " + rideRequestDTO.getPassengerId());
        System.out.println("Driver IDs: " + rideRequestDTO.getDriverIds());

        // Broadcast to all connected WebSocket clients
        sendDriverNewRideRequest(rideRequestDTO);

        System.out.println("Broadcast completed!");
        System.out.println("===========================================");

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    /**
     * Broadcasts ride request to all drivers via WebSocket
     */
    public void sendDriverNewRideRequest(RideRequestDTO rideRequestDTO) {
        System.out.println("Broadcasting to /topic/rideRequest...");
        simpleMessageTemplate.convertAndSend("/topic/rideRequest", rideRequestDTO);
        System.out.println("Message sent to /topic/rideRequest");
    }

    /**
     * REST endpoint to handle driver's ride acceptance from browser
     * Called by: Frontend JavaScript via fetch()
     */
    @PostMapping("/rideResponse/{driverId}")
    public ResponseEntity<Boolean> handleRideResponse(
            @PathVariable String driverId,
            @RequestBody RideResponseDTO rideResponseDTO) {

        System.out.println("Driver ID: " + driverId);
        System.out.println("Accepted: " + rideResponseDTO.getResponse());
        System.out.println("Booking ID: " + rideResponseDTO.getBookingId());

        // Add the same logic as the WebSocket handler
        if (rideResponseDTO.getResponse() && rideResponseDTO.getBookingId() != null) {
            UpdateBookingRequestDTO requestDTO = UpdateBookingRequestDTO.builder()
                    .driverId(Optional.of(Long.parseLong(driverId)))
                    .status("SCHEDULED")
                    .build();

            try {
                ResponseEntity<UpdateBookingResponseDTO> response = restTemplate.exchange(
                        "http://localhost:2512/api/v1/booking/" + rideResponseDTO.getBookingId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(requestDTO),
                        UpdateBookingResponseDTO.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Booking updated successfully");
                    return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
                }
            } catch (Exception e) {
                System.err.println("Error updating booking: " + e.getMessage());
                e.printStackTrace();
            }
        }
        kafkaProducerService.publishMessage("sample-topic","Hello");
        return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
    }

    @GetMapping
    public Boolean help(){
        kafkaProducerService.publishMessage("sample-topic","Hello");
        return true;
    }
}