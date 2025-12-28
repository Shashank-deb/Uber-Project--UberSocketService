package com.example.ubersocketservice.dto;

import com.example.ubersocketservice.models.ExactLocation;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDTO {
    private Long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
    private List<Long> driverIds;
}
