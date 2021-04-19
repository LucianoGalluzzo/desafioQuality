package com.example.booking.config;

public class MissingFiledsInSearchHotelException extends Exception{

    public MissingFiledsInSearchHotelException(){
            super("Some fields are missing in search. Required: 'dateFrom', 'dateTo' and 'destination'");
    }
}
