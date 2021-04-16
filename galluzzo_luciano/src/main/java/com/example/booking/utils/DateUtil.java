package com.example.booking.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateUtil {

    public static LocalDate convertToDate(String date){
        String[] dateFromString = date.split("/");
        int day = Integer.parseInt(dateFromString[0]);
        int month = Integer.parseInt(dateFromString[1]);
        int year = Integer.parseInt(dateFromString[2]);
        return LocalDate.of(year, month, day);
    }

    public static boolean validateDate(String strDate){
        /*boolean res;
        if(date.length() != 10)
            return false;
        if(date.charAt(2) != '/' || date.charAt(5) != '/' || date.charAt(8) != '/')
            return false;
        String[] dateSplit = date.split("/");*/

        String dateFormat = "dd/MM/uuuu";

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern(dateFormat)
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate date = LocalDate.parse(strDate, dateTimeFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
