package com.example.booking.config;

public class InexistentFlightErrorException extends Exception{

    public InexistentFlightErrorException(String number, String origin, String destination){
        super("Flight with code '" + number + "' from '" + origin + "' to '" + destination
                + "' doesn´t exist in database");
    }
}
