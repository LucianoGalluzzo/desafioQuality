package com.example.booking.config;

public class InvalidRoomException extends Exception{

    public InvalidRoomException(String type){
        super("Room type '" + type + "' is not a valid room type");
    }
}
