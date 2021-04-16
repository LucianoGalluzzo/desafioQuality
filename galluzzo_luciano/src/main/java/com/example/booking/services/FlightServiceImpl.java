package com.example.booking.services;

import com.example.booking.dtos.FlightDTO;
import com.example.booking.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository){
        this.flightRepository = flightRepository;
    }

    @Override
    public List<FlightDTO> getAllFlights() throws IOException {
        return flightRepository.getAll();
    }
}
