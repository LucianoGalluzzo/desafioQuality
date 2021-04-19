package com.example.booking.repositories;

import com.example.booking.config.EmptySearchHotelException;
import com.example.booking.config.InexistentHotelErrorException;
import com.example.booking.dtos.HotelDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface HotelRepository {

    List<HotelDTO> getAll() throws IOException;
    boolean destinationExist(String destination) throws IOException;
    List<HotelDTO> getHotelsFiltered(LocalDate dateFrom, LocalDate dateTo, String destination) throws IOException, EmptySearchHotelException;
    HotelDTO getHotelByCodAndDestination(String cod, String destination) throws IOException, InexistentHotelErrorException;
    void setReservation(String cod) throws IOException;
    void updateDB() throws IOException;
}
