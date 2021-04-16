package com.example.booking.repositories;

import com.example.booking.dtos.FlightDTO;

import java.io.IOException;
import java.util.List;

public interface FlightRepository {

    List<FlightDTO> getAll() throws IOException;
}
