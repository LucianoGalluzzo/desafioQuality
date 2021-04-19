package com.example.booking.config;

public class InexistentHotelErrorException extends Exception{

    public InexistentHotelErrorException (String cod, String destination){
        super("Hotel with code '" + cod + "' and city '" + destination + "' doesn´t exist in database");
    }
}
