package com.example.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightReservationResponseDTO {

    private String userName;
    private double amount;
    private double interest;
    private double total;
    private FlightReservationDTO flightReservation;
    private StatusDTO statusCode;
}
