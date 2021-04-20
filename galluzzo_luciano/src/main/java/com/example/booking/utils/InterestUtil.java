package com.example.booking.utils;

import com.example.booking.config.InvalidPaymentMethodException;
import com.example.booking.dtos.PaymentMethodDTO;

public class InterestUtil {

    public static void validateInterest(String type, int dues) throws InvalidPaymentMethodException {
        //Validate that card type is "DEBIT" or "CREDIT"
        if(!type.equalsIgnoreCase("DEBIT") && !type.equalsIgnoreCase("CREDIT"))
            throw new InvalidPaymentMethodException(type);

        //I consider max dues = 24
        if(type.equalsIgnoreCase("DEBIT") && dues != 1 || (
                type.equalsIgnoreCase("CREDIT") && (dues > 24 || dues < 1)
        ))
            throw new InvalidPaymentMethodException(type, dues);
    }

    public static double calculateInterest(PaymentMethodDTO paymentMethod){
        if(paymentMethod.getType().equalsIgnoreCase("DEBIT"))
            return 0;
        else{
            if(paymentMethod.getDues()<= 3)
                return 0.05;
            if(paymentMethod.getDues()<=6)
                return 0.1;
            if(paymentMethod.getDues()<=12)
                return 0.15;
            else
                return 0.2;
        }
    }
}
