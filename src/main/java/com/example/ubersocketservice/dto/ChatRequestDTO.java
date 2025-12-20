package com.example.ubersocketservice.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private String name;
    private String message;
}
