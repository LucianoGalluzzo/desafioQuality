package com.example.booking.controllers;

import com.example.booking.config.*;
import com.example.booking.dtos.ErrorDTO;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.repositories.HotelRepository;
import com.example.booking.repositories.HotelRepositoryImpl;
import com.example.booking.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<List<HotelDTO>> getHotels(@RequestParam Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, MissingFiledsInSearchException, EmptySearchException, WrongIntervalDateException {
        return new ResponseEntity<List<HotelDTO>>(hotelService.getHotels(params), HttpStatus.OK);
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

    @ExceptionHandler(value={MissingFiledsInSearchException.class})
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

}
