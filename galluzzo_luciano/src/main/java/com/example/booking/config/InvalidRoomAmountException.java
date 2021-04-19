package com.example.booking.config;

public class InvalidRoomAmountException extends Exception{

    public InvalidRoomAmountException(String type, int amount){
        super(String.valueOf(amount) + " people are not available to choose a " + type + " room. Valid options: " +
                "SINGLE: 1 person, "  + "DOUBLE: 2 people, " + "TRIPLE: 3 people and " +
                "MULTIPLE: 4-10 people");
    }
}
