package com.example.ubersocketservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String name;
    private String message;
    private String timeStamp;
}
