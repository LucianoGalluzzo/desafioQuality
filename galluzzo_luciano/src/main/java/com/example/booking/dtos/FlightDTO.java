package com.example.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {

    private String flightNumber;
    private String origin;
    private String destination;
    private String seatType;
    private int price;
    private String dateFrom;
    private String dateTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDTO flightDTO = (FlightDTO) o;
        return Objects.equals(flightNumber, flightDTO.flightNumber);
    }

    public FlightDTO(String[] data){
        this.flightNumber = data[0];
        this.origin = data[1];
        this.destination = data[2];
        this.seatType = data[3];
        this.price = Integer.parseInt(data[4].replace("$", "").replace(".", ""));
        this.dateFrom = data[5];
        this.dateTo = data[6];
    }
}
