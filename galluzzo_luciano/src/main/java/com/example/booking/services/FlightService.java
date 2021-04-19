package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FlightService {

    List<FlightDTO> getAllFlights() throws IOException;
    void validateParams(Map<String, String> params) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, MissingFieldsInSearchFlightException;
    List<FlightDTO> getFlights(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFieldsInSearchFlightException, EmptySearchFlightException;
    FlightReservationResponseDTO booking(FlightPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, BookingErrorException, InexistentFlightErrorException, FlightReservationErrorException;
    void validatePayload(FlightPayloadDTO payload) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, InvalidRoomException, InvalidRoomAmountException, InvalidEmailException, InvalidPaymentMethodException;
    double calculatePrice(FlightPayloadDTO payload) throws IOException, InexistentFlightErrorException;
    void checkBooking(FlightPayloadDTO payload) throws IOException, BookingErrorException, FlightReservationErrorException, InexistentFlightErrorException;

}
