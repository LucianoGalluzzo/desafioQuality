package com.example.booking.controllers;

import com.example.booking.config.*;
import com.example.booking.dtos.ErrorDTO;
import com.example.booking.dtos.HotelBookingResponseDTO;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.dtos.HotelPayloadDTO;
import com.example.booking.services.HotelService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class HotelControllerTest {

    private HotelController hotelController;

    @Mock
    private HotelService hotelService;

    List<HotelDTO> mockHotels;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        hotelController = new HotelController(hotelService);
        mockHotels =
                objectMapper.readValue(new File("src/test/resources/testHotels.json"),
                        new TypeReference<>() {
                        });
    }

    @Test
    void getAllHotelsTest() throws DateFormatException, IOException, MissingFieldsInSearchHotelException, EmptySearchHotelException, WrongIntervalDateException, InexistentDestinationException {
        ResponseEntity<List<HotelDTO>> mockResponse = new ResponseEntity<List<HotelDTO>>(mockHotels, HttpStatus.OK);

        when(hotelService.getHotels(any())).thenReturn(mockHotels);

        Assertions.assertEquals(mockResponse, hotelController.getHotels(new HashMap<>()));
    }

    @Test
    void bookingHotelTest() throws IOException, DateFormatException, InvalidRoomException, InexistentDestinationException, BookingErrorException, WrongIntervalDateException, InvalidEmailException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentHotelErrorException {
        HotelPayloadDTO mockPayload =
                objectMapper.readValue(new File("src/test/resources/testHotelPayload.json"),
                        new TypeReference<>() {
                        });

        HotelBookingResponseDTO mockBooked =
                objectMapper.readValue(new File("src/test/resources/testHotelResponse.json"),
                        new TypeReference<>() {
                        });

        ResponseEntity<HotelBookingResponseDTO> mockResponse =
                new ResponseEntity<HotelBookingResponseDTO>(mockBooked, HttpStatus.OK);

        when(hotelService.booking(any())).thenReturn(mockBooked);


        Assertions.assertEquals(mockResponse, hotelController.book(mockPayload));
    }

}
