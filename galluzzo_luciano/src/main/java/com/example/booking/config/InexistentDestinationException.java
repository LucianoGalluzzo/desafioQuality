package com.example.booking.config;

public class InexistentDestinationException extends Exception{

    public InexistentDestinationException(String destination){
        super("Destination: '" + destination + "' doesn´t exist in database");
    }
}
