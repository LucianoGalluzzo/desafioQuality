package com.example.booking.controllers;

import com.example.booking.config.*;
import com.example.booking.dtos.FlightReservationResponseDTO;
import com.example.booking.dtos.FlightDTO;
import com.example.booking.dtos.FlightPayloadDTO;
import com.example.booking.services.FlightService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class FlightControllerTest {

    private FlightController flightController;

    @Mock
    private FlightService flightService;

    List<FlightDTO> mockFlights;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        flightController = new FlightController(flightService);
        mockFlights =
                objectMapper.readValue(new File("src/test/resources/testFlights.json"),
                        new TypeReference<>() {
                        });
    }

    @Test
    void getAllFlightsTest() throws DateFormatException, IOException, WrongIntervalDateException, InexistentDestinationException, EmptySearchFlightException, MissingFieldsInSearchFlightException {
        ResponseEntity<List<FlightDTO>> mockResponse = new ResponseEntity<List<FlightDTO>>(mockFlights, HttpStatus.OK);

        when(flightService.getFlights(any())).thenReturn(mockFlights);

        Assertions.assertEquals(mockResponse, flightController.getFlights(new HashMap<>()));
    }

    @Test
    void reserveFlightTest() throws IOException, DateFormatException, InvalidRoomException, InexistentDestinationException, BookingErrorException, WrongIntervalDateException, InvalidEmailException, InvalidPaymentMethodException, InvalidRoomAmountException, FlightReservationErrorException, InexistentFlightErrorException, InexistentHotelErrorException {
        FlightPayloadDTO mockPayload =
                objectMapper.readValue(new File("src/test/resources/testFlightPayload.json"),
                        new TypeReference<>() {
                        });

        FlightReservationResponseDTO mockBooked =
                objectMapper.readValue(new File("src/test/resources/testFlightResponse.json"),
                        new TypeReference<>() {
                        });

        ResponseEntity<FlightReservationResponseDTO> mockResponse =
                new ResponseEntity<FlightReservationResponseDTO>(mockBooked, HttpStatus.OK);

        when(flightService.booking(any())).thenReturn(mockBooked);


        Assertions.assertEquals(mockResponse, flightController.reserveFlight(mockPayload));
    }

}
