package com.example.ubersocketservice.controller;

import com.example.ubersocketservice.dto.ChatRequestDTO;
import com.example.ubersocketservice.dto.ChatResponseDTO;
import com.example.ubersocketservice.dto.TestRequestDTO;
import com.example.ubersocketservice.dto.TestResponseDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class TestController {

    private final SimpMessagingTemplate simpleMessageTemplate;

    public TestController(SimpMessagingTemplate simpleMessageTemplate) {
        this.simpleMessageTemplate = simpleMessageTemplate;
    }

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public TestResponseDTO pingCheck(TestRequestDTO message) {
        // Log incoming message from client
        System.out.println("Received message: " + message.getData());
        return TestResponseDTO.builder().data("pong").build();
    }

//    @Scheduled(fixedDelay = 2000)
//    public void sendPeriodMessage() {
////        // Create a proper DTO as the payload
////        TestResponseDTO payload = TestResponseDTO.builder()
////                .data("Scheduled Server Update: " + System.currentTimeMillis())
////                .build();
////
////        // FIX: Send the payload DTO, NOT the template object
////        simpleMessageTemplate.convertAndSend("/topic/scheduled", payload);
////
////        System.out.println("Sent periodic message to /topic/scheduled");
//
//        System.out.println("Executed periodic function: ");
//        simpleMessageTemplate.convertAndSend("/topic/scheduled", "Periodic Message sent " + System.currentTimeMillis());
//    }


    @MessageMapping("/chat/{room}")
    @SendTo("/topic/message/{room}")
    public ChatResponseDTO chatMessage(@DestinationVariable String room, ChatRequestDTO request) {
        // Log the received message
        System.out.println("Received chat message from " + request.getName() + ": " + request.getMessage());

        // Create and return the response DTO with the current timestamp
        return ChatResponseDTO
                .builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp(""+System.currentTimeMillis())
                .build();
    }



    @MessageMapping("/privateChat/{room}/{userId}")
//    @SendTo("/topic/privateMessage/{room}/{userId}")
    public void privateChatMessage(@DestinationVariable String room,@DestinationVariable String userId, ChatRequestDTO request) {
        // Log the received message
        System.out.println("Received chat message from " + request.getName() + ": " + request.getMessage());

        // Create and return the response DTO with the current timestamp
        ChatResponseDTO response= ChatResponseDTO.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        simpleMessageTemplate.convertAndSendToUser(userId,"/queue/privateMessage/"+room, response);

    }
}