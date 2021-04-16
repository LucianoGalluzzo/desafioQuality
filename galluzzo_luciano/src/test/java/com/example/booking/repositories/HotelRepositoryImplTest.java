package com.example.booking.repositories;

import com.example.booking.config.EmptySearchException;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.utils.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class HotelRepositoryImplTest {

    private HotelRepository hotelRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    List<HotelDTO> mockHotels;

    @BeforeEach
    void setUp() throws IOException {
        hotelRepository = new HotelRepositoryImpl("src/test/resources/testHotels.csv");
        mockHotels = objectMapper.readValue(
                new File("src/test/resources/testHotels.json"),
                new TypeReference<>() {
                });
    }

    @Test
    void getAllTest() throws IOException {

        Assertions.assertEquals(mockHotels, hotelRepository.getAll());
    }

    @Test
    void getHotelsFilteredTest() throws IOException, EmptySearchException {
        LocalDate dateFrom = LocalDate.of(2021, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 03, 02);
        String destination = "Buenos Aires";
        List<HotelDTO> mockFilteredList = mockHotels.stream().filter(hotelDTO -> hotelDTO.getDestination().equalsIgnoreCase(destination)
                && !hotelDTO.isBooked() && !dateFrom.isBefore(DateUtil.convertToDate(hotelDTO.getDateFrom()))
                && !dateTo.isAfter(DateUtil.convertToDate(hotelDTO.getDateTo()))).
                collect(Collectors.toList());

        Assertions.assertEquals(mockFilteredList, hotelRepository.getHotelsFiltered(dateFrom, dateTo, destination));
    }

    @Test
    void getHotelsNoResultsTest(){
        LocalDate dateFrom = LocalDate.of(2020, 02, 10);
        LocalDate dateTo = LocalDate.of(2021, 03, 02);
        String destination = "Buenos Aires";

        Exception exception = Assertions.assertThrows(EmptySearchException.class, () -> {
            hotelRepository.getHotelsFiltered(dateFrom, dateTo, destination);
        });

        String expectedMessage = "No hotels available for that destination between that dates";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }


}
