package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.HotelBookingResponseDTO;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.dtos.HotelPayloadDTO;
import com.example.booking.dtos.PaymentMethodDTO;
import com.example.booking.repositories.HotelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @DisplayName("Get All Hotels")
    @Test
    void getAllHotels() throws IOException, InexistentDestinationException, MissingFieldsInSearchHotelException, DateFormatException, WrongIntervalDateException, EmptySearchHotelException {
        List<HotelDTO> mockHotels=
                objectMapper.readValue(new File("src/test/resources/testHotels.json"),
                        new TypeReference<>() {
                        });

        when(hotelRepository.getAll()).thenReturn(mockHotels);

        List<HotelDTO> responseHotels = hotelService.getHotels(new HashMap<>());

        Assertions.assertEquals(mockHotels, responseHotels);
    }

    @DisplayName("Get filtered hotels")
    @Test
    void getHotelsByParamsTest() throws IOException, InexistentDestinationException, MissingFieldsInSearchHotelException, DateFormatException, WrongIntervalDateException, EmptySearchHotelException {
        List<HotelDTO> mockHotels=
                objectMapper.readValue(new File("src/test/resources/testHotelsFiltered.json"),
                        new TypeReference<>() {
                        });

        Map<String, String> mockParams = new HashMap<>();
        mockParams.put("dateFrom", "10/02/2021");
        mockParams.put("dateTo", "19/03/2021");
        mockParams.put("destination", "Buenos Aires");

        when(hotelRepository.getHotelsFiltered(any(), any(), any())).thenReturn(mockHotels);
        when(hotelRepository.destinationExist(any())).thenReturn(true);

        List<HotelDTO> responseHotels = hotelService.getHotels(mockParams);

        Assertions.assertEquals(mockHotels, responseHotels);
    }

    @DisplayName("Missing params")
    @Test
    void validateParamsMissingFieldsTest(){

        Map<String,String> mockParams = new HashMap<>();
        mockParams.put("dateFrom", "20/02/2021");
        Exception exception = Assertions.assertThrows(MissingFieldsInSearchHotelException.class, () -> {
            hotelService.validateParams(mockParams);
        });

        String expectedMessage = "Some fields are missing in search. Required: 'dateFrom', 'dateTo' and 'destination'";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @DisplayName("Invalid format date")
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

    @DisplayName("Wrong interval date")
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

    @DisplayName("Destination not exist")
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

    @DisplayName("Calculate price")
    @Test
    void calculatePriceTest() throws IOException, InexistentHotelErrorException {
        when(hotelRepository.getHotelByCodAndDestination(any(), any())).thenReturn(hotelFixture());
        Assertions.assertEquals(81900, hotelService.calculatePrice(mockPayload));
    }

    @DisplayName("Invalid room type")
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

    @DisplayName("Invalid people amount for room")
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

    @DisplayName("Invalid email in UserName")
    @Test
    void validatePayloadInvalidUserNameEmailTest() throws IOException {
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

    @DisplayName("Invalid email in People list")
    @Test
    void validatePayloadInvalidPeopleEmailTest() throws IOException {
        String mockEmail = "lucianogalluzzomercadolibre.com";
        mockPayload.getBooking().getPeople().get(0).setMail(mockEmail);

        when(hotelRepository.destinationExist(any())).thenReturn(true);
        Exception exception = Assertions.assertThrows(InvalidEmailException.class, () -> {
            hotelService.validatePayload(mockPayload);
        });

        String expectedMessage = "Some emails are invalid. Please enter valid emails";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @DisplayName("Hotel already booked")
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

    @DisplayName("Book hotel")
    @Test
    void bookingHotelTest() throws IOException, InexistentHotelErrorException, InvalidEmailException, DateFormatException, InvalidRoomAmountException, WrongIntervalDateException, InvalidPaymentMethodException, InexistentDestinationException, InvalidRoomException, BookingErrorException {

        HotelBookingResponseDTO mockResponse =
                objectMapper.readValue(new File("src/test/resources/testHotelResponse.json"),
                new TypeReference<>() {
                });

        when(hotelRepository.getHotelByCodAndDestination(any(), any())).thenReturn(hotelFixtureToTestBooking());
        doNothing().when(hotelRepository).setReservation(any());
        when(hotelRepository.destinationExist(any())).thenReturn(true);

        HotelBookingResponseDTO hotelBookingResponse = hotelService.booking(mockPayload);

        Assertions.assertEquals(mockResponse, hotelBookingResponse);
    }

    HotelDTO hotelFixture(){
        return new HotelDTO("CH-0002", "Cataratas Hotel", "Puerto Iguazú", "Double", 6300, "10/02/2021",
                "20/03/2021", false);
    }

    HotelDTO hotelFixtureToTestBooking(){
        return new HotelDTO("HB-0001", "Hotel Bristol", "Buenos Aires", "Single", 5435, "10/02/2021",
                "19/03/2021", false);
    }

    HotelDTO hotelFixtureAlreadyBooked(){
        return new HotelDTO("HB-0001", "Hotel Bristol", "Buenos Aires", "Double", 5435, "10/02/2021",
                "19/03/2021", true);
    }

}
