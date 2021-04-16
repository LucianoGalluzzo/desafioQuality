package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.repositories.HotelRepository;
import com.example.booking.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository){
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<HotelDTO> getHotels(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFiledsInSearchException, EmptySearchException {

        if(params.size() == 0)
            return getAllHotels();
        validateParams(params);
        return hotelRepository.getHotelsFiltered(DateUtil.convertToDate(params.get("dateFrom")),
                DateUtil.convertToDate(params.get("dateTo")), params.get("destination"));
    }


    @Override
    public List<HotelDTO> getAllHotels() throws IOException {
        return hotelRepository.getAll();
    }

    @Override
    public void validateParams(Map<String, String> params) throws DateFormatException, MissingFiledsInSearchException, WrongIntervalDateException, IOException, InexistentDestinationException {

        if(!params.containsKey("dateFrom") || !params.containsKey("dateTo") || !params.containsKey("destination"))
            throw new MissingFiledsInSearchException();
        String dateFrom = params.get("dateFrom");
        String dateTo = params.get("dateTo");
        String destination = params.get("destination");
        if(!DateUtil.validateDate(params.get("dateFrom")) ||
            !DateUtil.validateDate(params.get("dateTo")))
            throw new DateFormatException();
        if(!DateUtil.convertToDate(dateFrom).isBefore(DateUtil.convertToDate(dateTo)))
            throw new WrongIntervalDateException();
        if(!hotelRepository.destinationExist(destination))
            throw new InexistentDestinationException(destination);

    }
}
