package com.example.booking.services;

import com.example.booking.dtos.FlightDTO;

import java.io.IOException;
import java.util.List;

public interface FlightService {

    List<FlightDTO> getAllFlights() throws IOException;
}
