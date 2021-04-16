package com.example.booking.config;

public class DateFormatException extends Exception{

    public DateFormatException(){
        super("Date format must be: 'DD/MM/YYYY'");
    }
}
