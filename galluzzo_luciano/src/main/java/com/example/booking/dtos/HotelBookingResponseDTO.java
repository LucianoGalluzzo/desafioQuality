package com.example.booking.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelBookingResponseDTO {

    private String userName;
    private double amount;
    private double interest;
    private double total;
    private BookingDTO booking;
    private StatusDTO statusCode;
}
