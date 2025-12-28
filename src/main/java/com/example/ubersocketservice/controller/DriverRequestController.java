package com.example.ubersocketservice.controller;

import com.example.ubersocketservice.dto.RideRequestDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/socket")
public class DriverRequestController {
    private final SimpMessagingTemplate simpleMessageTemplate;

    public DriverRequestController(SimpMessagingTemplate simpleMessageTemplate) {
        this.simpleMessageTemplate = simpleMessageTemplate;
    }

    @GetMapping("/newRide")
    public void raiseRideRequest(@RequestBody RideRequestDTO rideRequestDTO) {
        sendDriverNewRideRequest(rideRequestDTO);
    }

    public void sendDriverNewRideRequest(RideRequestDTO requestDTO) {
        System.out.println("Executed periodic function: ");
        //TODO: Ideally the request should go to nearby drivers , but for simplicity we send it to everyone
        simpleMessageTemplate.convertAndSend("/topic/rideRequest", requestDTO);
    }
}
