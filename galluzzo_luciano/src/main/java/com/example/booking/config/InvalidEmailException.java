package com.example.booking.config;

public class InvalidEmailException extends Exception{

    public InvalidEmailException(){
        super("Some emails are invalid. Please enter valid emails");
    }
}
