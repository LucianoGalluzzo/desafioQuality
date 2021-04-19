package com.example.booking.config;

public class MissingFieldsInSearchFlightException extends Exception{

    public MissingFieldsInSearchFlightException(){
        super("Some fields are missing in search. Required: 'dateFrom', 'dateTo', 'origin' and 'destination'");
    }
}
