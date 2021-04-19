package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FlightService {

    List<FlightDTO> getAllFlights() throws IOException;
    void validateParams(Map<String, String> params) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, MissingFieldsInSearchFlightException;
    List<FlightDTO> getFlights(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, EmptySearchException, MissingFieldsInSearchFlightException;
    FlightReservationResponseDTO booking(FlightPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, InexistentHotelErrorException, BookingErrorException, InexistentFlightErrorException, FlightReservationErrorException;
    void validatePayload(FlightPayloadDTO payload) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, InvalidRoomException, InvalidRoomAmountException, InvalidEmailException, InvalidPaymentMethodException;
    void interestValidation(String type, int dues) throws InvalidPaymentMethodException;
    double calculatePrice(FlightPayloadDTO payload) throws IOException, InexistentHotelErrorException, InexistentFlightErrorException;
    double calculateInterest(PaymentMethodDTO paymentMethod);
    void checkBooking(FlightPayloadDTO payload) throws IOException, InexistentHotelErrorException, BookingErrorException, FlightReservationErrorException, InexistentFlightErrorException;

}
