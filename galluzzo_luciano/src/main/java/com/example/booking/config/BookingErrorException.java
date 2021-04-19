package com.example.booking.config;

public class BookingErrorException extends Exception{

    public BookingErrorException(String cod, String destination, String dateFrom, String dateTo, String room){
        super("There isn´t a room '" + room + "' available in " + destination + " from " + dateFrom + " to "
        + dateTo + " in hotel '" + cod + "'");
    }

}
