package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.HotelDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HotelService {

    List<HotelDTO> getAllHotels() throws IOException;
    List<HotelDTO> getHotels(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFiledsInSearchException, EmptySearchException;
    void validateParams(Map<String, String> params) throws DateFormatException, MissingFiledsInSearchException, WrongIntervalDateException, IOException, InexistentDestinationException;
}
