package com.example.booking.config;

public class MissingFiledsInSearchException extends Exception{

    public MissingFiledsInSearchException(){
        super("Some fields are missing in search. Required: 'dateFrom', 'dateTo' and 'destination'");
    }
}
