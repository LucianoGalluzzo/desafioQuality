package com.example.booking.config;

public class FlightReservationErrorException extends Exception {

    public FlightReservationErrorException(String cod, String destination, String dateFrom, String dateTo, String origin, String seatType) {
        super("There isn´t a flight with number '" + cod + "' available from '" + origin
                + "' to '" + destination + " from " + dateFrom + " to "
                + dateTo + "' and seat type '" + seatType + "'");
    }
}
