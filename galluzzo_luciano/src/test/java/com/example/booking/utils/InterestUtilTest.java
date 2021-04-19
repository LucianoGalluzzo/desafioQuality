package com.example.booking.utils;

import com.example.booking.config.InvalidPaymentMethodException;
import com.example.booking.dtos.PaymentMethodDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterestUtilTest {

    @Test
    void validatePaymentMethodInvalidCardTypeTest(){
        String type = "OTHER";
        int dues = 1;

        Exception exception = Assertions.assertThrows(InvalidPaymentMethodException.class, () -> {
            InterestUtil.validateInterest(type, dues);
        });

        String expectedMessage = "'" + type + "' is not a valid payment method";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void validatePaymentMethodInvalidDuesNumberTest(){
        String type = "DEBIT";
        int dues = 3;

        Exception exception = Assertions.assertThrows(InvalidPaymentMethodException.class, () -> {
            InterestUtil.validateInterest(type, dues);
        });

        String expectedMessage = type + " card doesn´t allow " + String.valueOf(dues) + " dues";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void calculateInterest5PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 3);
        Assertions.assertEquals(0.05, InterestUtil.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest10PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 6);
        Assertions.assertEquals(0.1, InterestUtil.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest15PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 9);
        Assertions.assertEquals(0.15, InterestUtil.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest20PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 18);
        Assertions.assertEquals(0.2, InterestUtil.calculateInterest(mockPaymentMethod));
    }
}
