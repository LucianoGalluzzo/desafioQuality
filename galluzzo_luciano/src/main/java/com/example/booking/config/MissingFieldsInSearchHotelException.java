package com.example.booking.config;

public class MissingFieldsInSearchHotelException extends Exception{

    public MissingFieldsInSearchHotelException(){
            super("Some fields are missing in search. Required: 'dateFrom', 'dateTo' and 'destination'");
    }
}
