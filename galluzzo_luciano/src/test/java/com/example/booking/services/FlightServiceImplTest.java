package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;
import com.example.booking.repositories.FlightRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class FlightServiceImplTest {

    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    private FlightPayloadDTO mockPayload;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        flightService = new FlightServiceImpl(flightRepository);
        mockPayload =
                objectMapper.readValue(new File("src/test/resources/testFlightPayload.json"),
                        new TypeReference<>() {
                        });
    }


    @Test
    void getAllFlights() throws IOException, InexistentDestinationException, DateFormatException, WrongIntervalDateException, EmptySearchFlightException, MissingFieldsInSearchFlightException {
        List<FlightDTO> mockFlights=
                objectMapper.readValue(new File("src/test/resources/testFlights.json"),
                        new TypeReference<>() {
                        });

        when(flightRepository.getAll()).thenReturn(mockFlights);

        List<FlightDTO> responseFlights = flightService.getFlights(new HashMap<>());

        Assertions.assertEquals(mockFlights, responseFlights);
    }

    @Test
    void getFlightsByParamsTest() throws IOException, InexistentDestinationException, DateFormatException, WrongIntervalDateException, EmptySearchFlightException, MissingFieldsInSearchFlightException {
        List<FlightDTO> mockFlights=
                objectMapper.readValue(new File("src/test/resources/testFlightsFiltered.json"),
                        new TypeReference<>() {
                        });

        Map<String, String> mockParams = new HashMap<>();
        mockParams.put("dateFrom", "10/02/2021");
        mockParams.put("dateTo", "15/02/2021");
        mockParams.put("origin", "Buenos Aires");
        mockParams.put("destination", "Puerto Iguazú");

        when(flightRepository.getFlightsFiltered(any(), any(),any(), any())).thenReturn(mockFlights);
        when(flightRepository.destinationExist(any())).thenReturn(true);

        List<FlightDTO> responseFlights = flightService.getFlights(mockParams);

        Assertions.assertEquals(mockFlights, responseFlights);
    }

    @Test
    void validateParamsMissingFieldsTest(){

        Map<String,String> mockParams = new HashMap<>();
        mockParams.put("dateFrom", "20/02/2021");
        Exception exception = Assertions.assertThrows(MissingFieldsInSearchFlightException.class, () -> {
            flightService.validateParams(mockParams);
        });

        String expectedMessage = "Some fields are missing in search. Required: 'dateFrom', 'dateTo', 'origin' and 'destination'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void validateParamsInvalidFormatDateTest(){

        Map<String,String> mockParams = new HashMap<>();

        //Febraury 30th is an invalid date
        mockParams.put("dateFrom", "30/02/2021");
        mockParams.put("dateTo", "20/03/2021");
        mockParams.put("origin", "Buenos Aires");
        mockParams.put("destination", "Puerto Iguazú");

        Exception exception = Assertions.assertThrows(DateFormatException.class, () -> {
            flightService.validateParams(mockParams);
        });

        String expectedMessage = "Date format must be: 'DD/MM/YYYY'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void validateParamsWrongIntervalTest(){

        Map<String,String> mockParams = new HashMap<>();

        mockParams.put("dateFrom", "20/02/2021");
        mockParams.put("dateTo", "20/01/2021");
        mockParams.put("origin", "Buenos Aires");
        mockParams.put("destination", "Puerto Iguazú");

        Exception exception = Assertions.assertThrows(WrongIntervalDateException.class, () -> {
            flightService.validateParams(mockParams);
        });

        String expectedMessage = "dateFrom can´t be equal or greather than dateTo";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void validateParamsInexistentDestinationTest() throws IOException {

        Map<String,String> mockParams = new HashMap<>();

        mockParams.put("dateFrom", "20/02/2021");
        mockParams.put("dateTo", "20/03/2021");
        mockParams.put("origin", "Buenos Aires");
        mockParams.put("destination", "Montevideo");

        when(flightRepository.destinationExist(any())).thenReturn(false);
        Exception exception = Assertions.assertThrows(InexistentDestinationException.class, () -> {
            flightService.validateParams(mockParams);
        });

        String expectedMessage = "Destination: '" + "Montevideo" + "' doesn´t exist in database";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void calculatePriceTest() throws IOException, InexistentFlightErrorException {
        when(flightRepository.getFlightByNumberAndRoute(any(), any(), any())).thenReturn(flightFixture());
        Assertions.assertEquals(65000, flightService.calculatePrice(mockPayload));
    }

    @Test
    void validatePayloadInvalidUserNameEmailTest() throws IOException {
        String mockEmail = "lucianogalluzzomercadolibre.com";
        mockPayload.setUserName(mockEmail);

        when(flightRepository.destinationExist(any())).thenReturn(true);
        Exception exception = Assertions.assertThrows(InvalidEmailException.class, () -> {
            flightService.validatePayload(mockPayload);
        });

        String expectedMessage = "Some emails are invalid. Please enter valid emails";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePayloadInvalidPeopleEmailTest() throws IOException {
        String mockEmail = "lucianogalluzzomercadolibre.com";
        mockPayload.getFlightReservation().getPeople().get(0).setMail(mockEmail);

        when(flightRepository.destinationExist(any())).thenReturn(true);
        Exception exception = Assertions.assertThrows(InvalidEmailException.class, () -> {
            flightService.validatePayload(mockPayload);
        });

        String expectedMessage = "Some emails are invalid. Please enter valid emails";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkReservationNotValidIntervalTest() throws IOException, InexistentFlightErrorException {
        FlightDTO mockFlight = flightFixtureDifferentDates();
        when(flightRepository.getFlightByNumberAndRoute(any(), any(), any())).thenReturn(mockFlight);

        Exception exception = Assertions.assertThrows(FlightReservationErrorException.class, () -> {
            flightService.checkBooking(mockPayload);
        });

        String expectedMessage = "There isn´t a flight with number '" + mockPayload.getFlightReservation().getFlightNumber()
                + "' available from '" + mockPayload.getFlightReservation().getOrigin()
                + "' to '" + mockPayload.getFlightReservation().getDestination() + " from " +
                mockPayload.getFlightReservation().getDateFrom() + " to "
                + mockPayload.getFlightReservation().getDateTo() + "' and seat type '" +
                mockPayload.getFlightReservation().getSeatType() + "'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void flightReservationTest() throws IOException, InexistentFlightErrorException, DateFormatException, InvalidRoomAmountException, InvalidRoomException, InexistentDestinationException, BookingErrorException, InvalidEmailException, InvalidPaymentMethodException, WrongIntervalDateException, FlightReservationErrorException {
        FlightReservationResponseDTO mockResponse =
                objectMapper.readValue(new File("src/test/resources/testFlightResponse.json"),
                        new TypeReference<>() {
                        });

        when(flightRepository.getFlightByNumberAndRoute(any(), any(), any())).thenReturn(flightFixture());
        when(flightRepository.destinationExist(any())).thenReturn(true);

        FlightReservationResponseDTO hotelBookingResponse = flightService.booking(mockPayload);

        Assertions.assertEquals(mockResponse, hotelBookingResponse);

    }

    FlightDTO flightFixture(){
        return new FlightDTO("BAPI-1235", "Buenos Aires", "Puerto Iguazú", "Economy", 6500, "10/02/2021",
                "15/02/2021");
    }

    FlightDTO flightFixtureDifferentDates(){
        return new FlightDTO("BAPI-1235", "Buenos Aires", "Puerto Iguazú", "Economy", 6500, "10/02/2021",
                "12/02/2021");
    }
}
