package com.example.booking.services;

import com.example.booking.repositories.HotelRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.openMocks;

public class HotelServiceImplTest {

    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @BeforeAll
    void setUpFiles(){

    }

    @BeforeEach
    void setUp(){
        hotelService = new HotelServiceImpl(hotelRepository);
        openMocks(this);
    }

    @Test
    void getAllTest(){

    }
}
