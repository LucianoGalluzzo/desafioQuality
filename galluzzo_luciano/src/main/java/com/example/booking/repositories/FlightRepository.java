package com.example.booking.repositories;

import com.example.booking.config.EmptySearchException;
import com.example.booking.config.InexistentFlightErrorException;
import com.example.booking.config.InexistentHotelErrorException;
import com.example.booking.dtos.FlightDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface FlightRepository {

    List<FlightDTO> getAll() throws IOException;
    boolean destinationExist(String destination) throws IOException;
    List<FlightDTO> getFlightsFiltered(LocalDate dateFrom, LocalDate dateTo, String origin, String destination) throws IOException, EmptySearchException;
    FlightDTO getFlightByNumberAndRoute(String number, String origin, String destination) throws IOException, InexistentHotelErrorException, InexistentFlightErrorException;
    }
