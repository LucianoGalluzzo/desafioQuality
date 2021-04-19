package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.dtos.HotelPayloadDTO;
import com.example.booking.dtos.PaymentMethodDTO;
import com.example.booking.repositories.HotelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class HotelServiceImplTest {

    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    private HotelPayloadDTO mockPayload;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        openMocks(this);
        hotelService = new HotelServiceImpl(hotelRepository);
        mockPayload =
                objectMapper.readValue(new File("src/test/resources/testHotelPayload.json"),
                        new TypeReference<>() {
                        });
    }

    @Test
    void validateParamsMissingFieldsTest(){

        Map<String,String> mockParams = new HashMap<>();
        mockParams.put("dateFrom", "20/02/2021");
        Exception exception = Assertions.assertThrows(MissingFiledsInSearchHotelException.class, () -> {
            hotelService.validateParams(mockParams);
        });

        String expectedMessage = "Some fields are missing in search. Required: 'dateFrom', 'dateTo' and 'destination'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void validateParamsInvalidFormatDateTest(){

        Map<String,String> mockParams = new HashMap<>();

        //Febraury 30th is an invalid date
        mockParams.put("dateFrom", "30/02/2021");
        mockParams.put("dateTo", "20/03/2021");
        mockParams.put("destination", "Buenos Aires");

        Exception exception = Assertions.assertThrows(DateFormatException.class, () -> {
            hotelService.validateParams(mockParams);
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
        mockParams.put("destination", "Buenos Aires");

        Exception exception = Assertions.assertThrows(WrongIntervalDateException.class, () -> {
            hotelService.validateParams(mockParams);
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
        mockParams.put("destination", "Montevideo");

        when(hotelRepository.destinationExist(any())).thenReturn(false);
        Exception exception = Assertions.assertThrows(InexistentDestinationException.class, () -> {
            hotelService.validateParams(mockParams);
        });

        String expectedMessage = "Destination: '" + "Montevideo" + "' doesn´t exist in database";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void calculateInterest5PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 3);
        Assertions.assertEquals(0.05, hotelService.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest10PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 6);
        Assertions.assertEquals(0.1, hotelService.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest15PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 9);
        Assertions.assertEquals(0.15, hotelService.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculateInterest20PercentTest(){
        PaymentMethodDTO mockPaymentMethod = new PaymentMethodDTO("CREDIT", "1234-1234-1234-1234", 18);
        Assertions.assertEquals(0.2, hotelService.calculateInterest(mockPaymentMethod));
    }

    @Test
    void calculatePriceTest() throws IOException, InexistentHotelErrorException {
        when(hotelRepository.getHotelByCodAndDestination(any(), any())).thenReturn(hotelFixture());
        Assertions.assertEquals(81900, hotelService.calculatePrice(mockPayload));
    }

    @Test
    void validatePaymentMethodInvalidCardTypeTest(){
        String type = "OTHER";
        int dues = 1;

        Exception exception = Assertions.assertThrows(InvalidPaymentMethodException.class, () -> {
            hotelService.interestValidation(type, dues);
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
            hotelService.interestValidation(type, dues);
        });

        String expectedMessage = type + " card doesn´t allow " + String.valueOf(dues) + " dues";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void roomValidationInvalidTypeTest(){
        String type = "KING ROOM";
        Exception exception = Assertions.assertThrows(InvalidRoomException.class, () -> {
            hotelService.roomValidation(type, 2);
        });

        String expectedMessage = "Room type '" + type + "' is not a valid room type";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void roomValidationInvalidPeopleAmountTest(){
        String type = "DOUBLE";
        int amount = 3;
        Exception exception = Assertions.assertThrows(InvalidRoomAmountException.class, () -> {
            hotelService.roomValidation(type, amount);
        });

        String expectedMessage = String.valueOf(amount) + " people are not available to choose a " + type + " room. Valid options: " +
                "SINGLE: 1 person, "  + "DOUBLE: 2 people, " + "TRIPLE: 3 people and " +
                "MULTIPLE: 4-10 people";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePayloadInvalidEmailTest() throws IOException {
        String mockEmail = "lucianogalluzzomercadolibre.com";
        mockPayload.setUserName(mockEmail);

        when(hotelRepository.destinationExist(any())).thenReturn(true);
        Exception exception = Assertions.assertThrows(InvalidEmailException.class, () -> {
            hotelService.validatePayload(mockPayload);
        });

        String expectedMessage = "Some emails are invalid. Please enter valid emails";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkBookingHotelAlreadyBookedTest() throws IOException, InexistentHotelErrorException {
        HotelDTO mockHotel = hotelFixtureAlreadyBooked();
        when(hotelRepository.getHotelByCodAndDestination(any(), any())).thenReturn(mockHotel);

        Exception exception = Assertions.assertThrows(BookingErrorException.class, () -> {
            hotelService.checkBooking(mockPayload);
        });

        String expectedMessage = "There isn´t a room '" + mockPayload.getBooking().getRoomType() + "' available in "
                + mockPayload.getBooking().getDestination() + " from " + mockPayload.getBooking().getDateFrom()
                + " to " + mockPayload.getBooking().getDateTo() + " in hotel '" + mockPayload.getBooking().getHotelCode()
                + "'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    HotelDTO hotelFixture(){
        return new HotelDTO("CH-0002", "Cataratas Hotel", "Puerto Iguazú", "Double", 6300, "10/02/2021",
                "20/03/2021", false);
    }

    HotelDTO hotelFixtureAlreadyBooked(){
        return new HotelDTO("HB-0001", "Hotel Bristol", "Buenos Aires", "Double", 5435, "10/02/2021",
                "19/03/2021", true);
    }

}
