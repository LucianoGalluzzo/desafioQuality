package com.example.booking.controllers;

import com.example.booking.config.*;
import com.example.booking.dtos.ErrorDTO;
import com.example.booking.dtos.HotelBookingResponseDTO;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.dtos.HotelPayloadDTO;
import com.example.booking.services.HotelService;
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
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService){
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelDTO>> getHotels(@RequestParam Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, MissingFiledsInSearchHotelException, EmptySearchException, WrongIntervalDateException {
        return new ResponseEntity<List<HotelDTO>>(hotelService.getHotels(params), HttpStatus.OK);
    }

    @PostMapping("/booking")
    public ResponseEntity<HotelBookingResponseDTO> book(@RequestBody HotelPayloadDTO payload) throws DateFormatException, InvalidRoomAmountException, InvalidRoomException, InexistentDestinationException, BookingErrorException, InvalidEmailException, InvalidPaymentMethodException, IOException, WrongIntervalDateException, InexistentHotelErrorException {
        return new ResponseEntity<HotelBookingResponseDTO>(hotelService.booking(payload), HttpStatus.OK);
    }

    @ExceptionHandler(value={BookingErrorException.class})
    public ResponseEntity<ErrorDTO> bookingErrorException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Booking error", e.getMessage());
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

    @ExceptionHandler(value={InexistentHotelErrorException.class})
    public ResponseEntity<ErrorDTO> inexistentHotelErrorException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Inexistent hotel", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidEmailException.class})
    public ResponseEntity<ErrorDTO> invalidEmailException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid email", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidPaymentMethodException.class})
    public ResponseEntity<ErrorDTO> invalidPaymentMethodException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid Payment Method", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidRoomAmountException.class})
    public ResponseEntity<ErrorDTO> invalidRoomAmountException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid Room Amount", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InvalidRoomException.class})
    public ResponseEntity<ErrorDTO> invalidRoomException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Invalid Room", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={MissingFiledsInSearchHotelException.class})
    public ResponseEntity<ErrorDTO> missingFiledsInSearchException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Missing fields", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={WrongIntervalDateException.class})
    public ResponseEntity<ErrorDTO> wrongIntervalDateException(Exception e){
        ErrorDTO errorDTO = new ErrorDTO("Wrong interval", e.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }



}
