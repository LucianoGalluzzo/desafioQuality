package com.example.booking.repositories;

import com.example.booking.config.EmptySearchFlightException;
import com.example.booking.config.InexistentFlightErrorException;
import com.example.booking.dtos.FlightDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class FlightRepositoryImplTest {

    private FlightRepository flightRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        flightRepository = new FlightRepositoryImpl("src/test/resources/testFlights.csv");

    }

    @DisplayName("Get All Flights")
    @Test
    void getAllTest() throws IOException {
        List<FlightDTO> mockFlights = objectMapper.readValue(
                new File("src/test/resources/testFlights.json"),
                new TypeReference<>() {
                });

        Assertions.assertEquals(mockFlights, flightRepository.getAll());
    }

    @DisplayName("Get filtered Flights")
    @Test
    void getFlightsFilteredTest() throws IOException, EmptySearchFlightException {
        List<FlightDTO> mockFlights = objectMapper.readValue(
                new File("src/test/resources/testFlightsFiltered.json"),
                new TypeReference<>() {
                });
        LocalDate dateFrom = LocalDate.of(2021, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 02, 15);
        String origin = "Buenos Aires";
        String destination = "Puerto Iguazú";
        Assertions.assertEquals(mockFlights, flightRepository.getFlightsFiltered(dateFrom, dateTo, origin, destination));
    }

    @DisplayName("Not found flights")
    @Test
    void getFlightsNoResultsTest(){
        LocalDate dateFrom = LocalDate.of(2021, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 02, 15);
        String origin = "Buenos Aires";
        String destination = "Montevideo";

        Exception exception = Assertions.assertThrows(EmptySearchFlightException.class, () -> {
            flightRepository.getFlightsFiltered(dateFrom, dateTo, origin, destination);
        });

        String expectedMessage = "No flights available for that route between that dates";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @DisplayName("Destination exist")
    @Test
    void destinationExistTest() throws IOException {

        Assertions.assertTrue(flightRepository.destinationExist("Buenos Aires"));
    }

    @DisplayName("Destination not exist")
    @Test
    void destinationNoExistTest() throws IOException {

        Assertions.assertFalse(flightRepository.destinationExist("Montevideo"));
    }

    @DisplayName("Find flight by cod")
    @Test
    void getFlightByCodAndRouteTest() throws IOException, InexistentFlightErrorException {
        FlightDTO mockFlight = new FlightDTO("BAPI-1235", "Buenos Aires", "Puerto Iguazú", "Economy", 6500, "10/02/2021",
                "15/02/2021");
        FlightDTO responseFlight = flightRepository.getFlightByNumberAndRoute("BAPI-1235","Buenos Aires", "Puerto Iguazú");
        Assertions.assertEquals(mockFlight, responseFlight);
    }

    @DisplayName("Flight not exist")
    @Test
    void flightNotExistedException(){
        String number = "XXXX-1235";
        String origin = "Buenos Aires";
        String destination = "Montevideo";
        Exception exception = Assertions.assertThrows(InexistentFlightErrorException.class, () -> {
            flightRepository.getFlightByNumberAndRoute(number, origin, destination);
        });

        String expectedMessage = "Flight with code '" + number + "' from '" + origin + "' to '" + destination
                + "' doesn´t exist in database";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
