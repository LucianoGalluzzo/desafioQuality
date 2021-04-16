package com.example.booking.controllers;

import com.example.booking.dtos.FlightDTO;
import com.example.booking.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<List<FlightDTO>> getFlights(@RequestParam Map<String, String> params) throws IOException {
        return new ResponseEntity<List<FlightDTO>>(flightService.getAllFlights(), HttpStatus.OK);
    }
}
