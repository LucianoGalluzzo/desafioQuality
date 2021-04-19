package com.example.booking.config;

public class InvalidPaymentMethodException extends Exception{

    public InvalidPaymentMethodException(String type){
        super("'" + type + "' is not a valid payment method");
    }
    public InvalidPaymentMethodException(String type, int dues){
        super(type + " card doesn´t allow " + String.valueOf(dues) + " dues");
    }
}
