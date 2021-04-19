package com.example.booking.config;

public class EmptySearchFlightException extends Exception{

    public EmptySearchFlightException(){
        super("No flights available for that route between that dates");

    }
}
