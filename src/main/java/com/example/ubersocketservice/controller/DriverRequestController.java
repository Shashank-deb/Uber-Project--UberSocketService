package com.example.ubersocketservice.controller;

import com.example.ubersocketservice.dto.RideRequestDTO;
import com.example.ubersocketservice.dto.RideResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/socket")
public class DriverRequestController {
    private final SimpMessagingTemplate simpleMessageTemplate;

    public DriverRequestController(SimpMessagingTemplate simpleMessageTemplate) {
        this.simpleMessageTemplate = simpleMessageTemplate;
    }

    @PostMapping("/newRide")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDTO rideRequestDTO) {
        System.out.println("request for ride received");
        sendDriverNewRideRequest(rideRequestDTO);
        System.out.println("Request completed");
        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.OK);
    }

    public void sendDriverNewRideRequest(RideRequestDTO rideRequestDTO) {
        System.out.println("Executed periodic function: ");
        //TODO: Ideally the request should go to nearby drivers , but for simplicity we send it to everyone
        simpleMessageTemplate.convertAndSend("/topic/rideRequest", rideRequestDTO);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @MessageMapping("/rideResponse/{userId}")
    public void rideResponseHandler(@DestinationVariable String userId, RideResponseDTO rideResponseDTO){
        System.out.println(rideResponseDTO.getResponse()+" "+userId);
    }
}
