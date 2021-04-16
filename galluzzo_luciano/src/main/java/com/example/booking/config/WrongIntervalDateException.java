package com.example.booking.config;

public class WrongIntervalDateException extends Exception{

    public WrongIntervalDateException(){
        super("dateFrom can´t be equal or greather than dateTo");
    }
}
