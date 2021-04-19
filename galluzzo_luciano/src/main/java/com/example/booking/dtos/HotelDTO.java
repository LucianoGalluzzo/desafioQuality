package com.example.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private String cod;
    private String name;
    private String destination;
    private String roomType;
    private int price;
    private String dateFrom;
    private String dateTo;
    private boolean booked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelDTO hotelDTO = (HotelDTO) o;
        return Objects.equals(cod, hotelDTO.cod);
    }

    public HotelDTO(String[] data){
        this.cod = data[0];
        this.name = data[1];
        this.destination = data[2];
        this.roomType = data[3];
        this.price = Integer.parseInt(data[4].replace("$", "").replace(".", ""));
        this.dateFrom = data[5];
        this.dateTo = data[6];
        if(data[7].toLowerCase().equals("si"))
            this.booked = true;
        else
            this.booked = false;
    }

    public HotelDTO(String cod){
        this.cod = cod;
    }

}
