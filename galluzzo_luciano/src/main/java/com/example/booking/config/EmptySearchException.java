package com.example.booking.config;

public class EmptySearchException extends Exception{
    public EmptySearchException(){
        super("No hotels available for that destination between that dates");
    }
}
