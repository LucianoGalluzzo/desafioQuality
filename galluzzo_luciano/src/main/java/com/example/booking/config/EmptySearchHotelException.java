package com.example.booking.config;

public class EmptySearchHotelException extends Exception{
    public EmptySearchHotelException(){
        super("No hotels available for that destination between that dates");
    }
}
