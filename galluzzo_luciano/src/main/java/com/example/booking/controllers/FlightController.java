package com.example.booking.controllers;

import com.example.booking.config.*;
import com.example.booking.dtos.ErrorDTO;
import com.example.booking.dtos.FlightDTO;
import com.example.booking.dtos.FlightPayloadDTO;
import com.example.booking.dtos.FlightReservationResponseDTO;
import com.example.booking.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService){
        this.flightService = flightService;
    }


    @GetMapping("/flights")
    public ResponseEntity<List<FlightDTO>> getFlights(@RequestParam Map<String, String> params) throws IOException, InexistentDestinationException, DateFormatException, EmptySearchException, WrongIntervalDateException, MissingFieldsInSearchFlightException {
        return new ResponseEntity<List<FlightDTO>>(flightService.getFlights(params), HttpStatus.OK);
    }

    @PostMapping("/flight-reservation")
    public ResponseEntity<FlightReservationResponseDTO> reserveFlight(@RequestBody FlightPayloadDTO payload) throws DateFormatException, InvalidRoomAmountException, InvalidRoomException, InexistentDestinationException, FlightReservationErrorException, BookingErrorException, InvalidEmailException, InvalidPaymentMethodException, IOException, WrongIntervalDateException, InexistentFlightErrorException, InexistentHotelErrorException {
        return new ResponseEntity<FlightReservationResponseDTO>(flightService.booking(payload), HttpStatus.OK);
    }

    @ExceptionHandler(value={FlightReservationErrorException.class})
    public ResponseEntity<ErrorDTO> flightReservationErrorException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Reservation Error", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InexistentFlightErrorException.class})
    public ResponseEntity<ErrorDTO> inexistentFlightErrorException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Inexistent Flight", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidPaymentMethodException.class})
    public ResponseEntity<ErrorDTO> invalidPaymentMethodException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid Payment Method", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidEmailException.class})
    public ResponseEntity<ErrorDTO> invalidEmailException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid email", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={MissingFieldsInSearchFlightException.class})
    public ResponseEntity<ErrorDTO> missingFiledsInSearchException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Missing fields", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={WrongIntervalDateException.class})
    public ResponseEntity<ErrorDTO> wrongIntervalDateException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Wrong interval", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={DateFormatException.class})
    public ResponseEntity<ErrorDTO> dateFormatException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid date format", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={EmptySearchException.class})
    public ResponseEntity<ErrorDTO> emptySearchException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("No results", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InexistentDestinationException.class})
    public ResponseEntity<ErrorDTO> inexistentDestinationException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Destination error", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }
}
