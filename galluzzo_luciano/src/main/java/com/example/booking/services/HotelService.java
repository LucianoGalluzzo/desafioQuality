package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.HotelBookingResponseDTO;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.dtos.HotelPayloadDTO;
import com.example.booking.dtos.PaymentMethodDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HotelService {

    List<HotelDTO> getAllHotels() throws IOException;
    List<HotelDTO> getHotels(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFieldsInSearchHotelException, EmptySearchHotelException;
    void validateParams(Map<String, String> params) throws DateFormatException, MissingFieldsInSearchHotelException, WrongIntervalDateException, IOException, InexistentDestinationException;
    HotelBookingResponseDTO booking(HotelPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, InexistentHotelErrorException, BookingErrorException;
    void validatePayload(HotelPayloadDTO payload) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, InvalidRoomException, InvalidRoomAmountException, InvalidEmailException, InvalidPaymentMethodException;
    void roomValidation(String type, int amount) throws InvalidRoomException, InvalidRoomAmountException;
    double calculatePrice(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException;
    void checkBooking(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException, BookingErrorException;
}
