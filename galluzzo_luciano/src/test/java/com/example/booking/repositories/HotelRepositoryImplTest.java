package com.example.booking.repositories;

import com.example.booking.config.EmptySearchHotelException;
import com.example.booking.config.InexistentHotelErrorException;
import com.example.booking.dtos.HotelDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class HotelRepositoryImplTest {

    private HotelRepository hotelRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() throws IOException {
        hotelRepository = new HotelRepositoryImpl("src/test/resources/testHotels.csv");
    }

    @DisplayName("Get All Hotels")
    @Test
    void getAllTest() throws IOException {
        List<HotelDTO> mockHotels = objectMapper.readValue(
                new File("src/test/resources/testHotels.json"),
                new TypeReference<>() {
                });
        Assertions.assertEquals(mockHotels, hotelRepository.getAll());
    }

    @DisplayName("Get Filtered Hotels")
    @Test
    void getHotelsFilteredTest() throws IOException, EmptySearchHotelException {
        List<HotelDTO> mockHotels = objectMapper.readValue(
                new File("src/test/resources/testHotelsFiltered.json"),
                new TypeReference<>() {
                });
        LocalDate dateFrom = LocalDate.of(2021, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 03, 02);
        String destination = "Buenos Aires";
        Assertions.assertEquals(mockHotels, hotelRepository.getHotelsFiltered(dateFrom, dateTo, destination));
    }

    @DisplayName("Not found hotels")
    @Test
    void getHotelsNoResultsTest(){
        LocalDate dateFrom = LocalDate.of(2020, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 03, 02);
        String destination = "Buenos Aires";

        Exception exception = Assertions.assertThrows(EmptySearchHotelException.class, () -> {
            hotelRepository.getHotelsFiltered(dateFrom, dateTo, destination);
        });

        String expectedMessage = "No hotels available for that destination between that dates";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @DisplayName("Destination exist")
    @Test
    void destinationExistTest() throws IOException {

        Assertions.assertTrue(hotelRepository.destinationExist("Buenos Aires"));
    }

    @DisplayName("Destination not exist")
    @Test
    void destinationNoExistTest() throws IOException {

        Assertions.assertFalse(hotelRepository.destinationExist("Montevideo"));
    }

    @DisplayName("Find hotel by cod")
    @Test
    void getHotelByCodAndDestinationTest() throws IOException, InexistentHotelErrorException {
        HotelDTO mockHotel = new HotelDTO("CH-0002", "Cataratas Hotel", "Puerto Iguazú", "Doble", 6300, "10/02/2021",
                "20/03/2021", false);
        HotelDTO responseHotel = hotelRepository.getHotelByCodAndDestination("CH-0002", "Puerto Iguazú");
        Assertions.assertEquals(mockHotel, responseHotel);
    }

    @DisplayName("Hotel not exist")
    @Test
    void hotelNotExistedException(){
        String cod = "XXXX";
        String destination = "Montevideo";
        Exception exception = Assertions.assertThrows(InexistentHotelErrorException.class, () -> {
            hotelRepository.getHotelByCodAndDestination(cod, destination);
        });

        String expectedMessage = "Hotel with code '" + cod + "' and city '" + destination + "' doesn´t exist in database";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }


}
