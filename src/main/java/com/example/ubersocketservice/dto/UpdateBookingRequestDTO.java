package com.example.ubersocketservice.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequestDTO {
    private String status;
    private Optional<Long> driverId;
}
