package com.example.booking.repositories;

import com.example.booking.dtos.FlightDTO;
import com.example.booking.dtos.HotelDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FlightRepositoryImplTest {

    private FlightRepository flightRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        flightRepository = new FlightRepositoryImpl("src/test/resources/testFlights.csv");

    }

    @Test
    void getAllTest() throws IOException {
        List<FlightDTO> mockHotels = objectMapper.readValue(
                new File("src/test/resources/testFlights.json"),
                new TypeReference<>() {
                });

        Assertions.assertEquals(mockHotels, flightRepository.getAll());
    }
}
