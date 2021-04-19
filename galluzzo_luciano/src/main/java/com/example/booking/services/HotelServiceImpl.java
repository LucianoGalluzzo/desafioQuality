package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;
import com.example.booking.repositories.HotelRepository;
import com.example.booking.utils.DateUtil;
import com.example.booking.utils.EmailUtil;
import com.example.booking.utils.InterestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final Map<String, Integer> validRooms = new HashMap<String, Integer>(){
        {
            put("SINGLE", 1);
            put("DOUBLE", 2);
            put("TRIPLE", 3);
            put("MULTIPLE", 10);
        }
    };

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository){
        this.hotelRepository = hotelRepository;
    }

    /*
    This method receives the params in a Hashmap, is there aren´t any param, calls to a method which return the
    whole hotel list, if there are some param, calls to a method to validate the params and then obtains the
    filtered list
     */
    @Override
    public List<HotelDTO> getHotels(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFieldsInSearchHotelException, EmptySearchHotelException {

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
    public void validateParams(Map<String, String> params) throws DateFormatException, MissingFieldsInSearchHotelException, WrongIntervalDateException, IOException, InexistentDestinationException {

        //Validate that the 3 parameters are indicated in the query
        if(!params.containsKey("dateFrom") || !params.containsKey("dateTo") || !params.containsKey("destination"))
            throw new MissingFieldsInSearchHotelException();
        String dateFrom = params.get("dateFrom");
        String dateTo = params.get("dateTo");
        String destination = params.get("destination");

        //Validate format of both dates
        if(!DateUtil.validateDate(params.get("dateFrom")) ||
            !DateUtil.validateDate(params.get("dateTo")))
            throw new DateFormatException();

        //Validate that dateFrom is previous of dateTo
        if(!DateUtil.convertToDate(dateFrom).isBefore(DateUtil.convertToDate(dateTo)))
            throw new WrongIntervalDateException();

        //Validate that the destination city is valid
        if(!hotelRepository.destinationExist(destination))
            throw new InexistentDestinationException(destination);

    }

    /*
    This method receives a payload with the booking information and return the booking completed
     */
    @Override
    public HotelBookingResponseDTO booking(HotelPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, InexistentHotelErrorException, BookingErrorException {

        //Validate that every fields in payload are corrects
        validatePayload(payload);

        //Check that the room asked are available for the indicated dates and conditions
        checkBooking(payload);

        HotelBookingResponseDTO hotelBookingResponseDTO = new HotelBookingResponseDTO();
        hotelBookingResponseDTO.setBooking(payload.getBooking());
        hotelBookingResponseDTO.setUserName(payload.getUserName());

        //Calculate the price
        double price = calculatePrice(payload);
        hotelBookingResponseDTO.setAmount(price);

        //Calculate the interests
        hotelBookingResponseDTO.setInterest(InterestUtil.calculateInterest(payload.getBooking().getPaymentMethod()));
        double total = hotelBookingResponseDTO.getAmount() * (1 + hotelBookingResponseDTO.getInterest());
        hotelBookingResponseDTO.setTotal(Math.round(total * 100d) / 100d);
        hotelBookingResponseDTO.setStatusCode(new StatusDTO(200, "El proceso termino satisfactoriamente"));

        //Update database indicating that the room are now booked
        hotelRepository.setReservation(payload.getBooking().getHotelCode());

        return hotelBookingResponseDTO;
    }

    @Override
    public void validatePayload(HotelPayloadDTO payload) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, InvalidRoomException, InvalidRoomAmountException, InvalidEmailException, InvalidPaymentMethodException {

        // datefrom and dateto validation
        if(!DateUtil.validateDate(payload.getBooking().getDateFrom())
            || !DateUtil.validateDate(payload.getBooking().getDateTo()))
            throw new DateFormatException();
        if(!DateUtil.convertToDate(payload.getBooking().getDateFrom()).isBefore(
                DateUtil.convertToDate(payload.getBooking().getDateTo())))
            throw new WrongIntervalDateException();

        //destination validation
        if(!hotelRepository.destinationExist(payload.getBooking().getDestination()))
            throw new InexistentDestinationException(payload.getBooking().getDestination());

        //room validation
        roomValidation(payload.getBooking().getRoomType(), payload.getBooking().getPeopleAmount());

        // email validation
        if(!EmailUtil.validateEmails(payload.getUserName(), payload.getBooking().getPeople()))
            throw new InvalidEmailException();

        //payment validation
        InterestUtil.validateInterest(payload.getBooking().getPaymentMethod().getType(),
                payload.getBooking().getPaymentMethod().getDues());
    }

    /*
    This method validates that the room type exist (searching in a map with valid types) and check that the
    people amount is valid depends on room type
     */
    @Override
    public void roomValidation(String type, int amount) throws InvalidRoomException, InvalidRoomAmountException {

        type = type.toUpperCase();
        if(!validRooms.containsKey(type))
            throw new InvalidRoomException(type);
        if(type.equals("MULTIPLE") && (amount < 4 || amount > 10) || validRooms.get(type) != amount)
            throw new InvalidRoomAmountException(type, amount);
    }

    @Override
    public double calculatePrice(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException {
        //Obtain from database, the hotel asked
        HotelDTO hotel = hotelRepository.getHotelByCodAndDestination(payload.getBooking().getHotelCode(),
                payload.getBooking().getDestination());

        LocalDate dateTo = DateUtil.convertToDate(payload.getBooking().getDateTo());
        LocalDate dateFrom = DateUtil.convertToDate(payload.getBooking().getDateFrom());

        //Calculate duration of booking
        int days = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);

        return days * hotel.getPrice();
    }

    @Override
    public void checkBooking(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException, BookingErrorException {

        //Obtain in some variables some important information from payload
        String cod = payload.getBooking().getHotelCode();
        String destination = payload.getBooking().getDestination();
        String room = payload.getBooking().getRoomType();
        LocalDate dateFrom = DateUtil.convertToDate(payload.getBooking().getDateFrom());
        LocalDate dateTo = DateUtil.convertToDate(payload.getBooking().getDateTo());

        //Search in database the hotel asked
        HotelDTO hotel = hotelRepository.getHotelByCodAndDestination(cod, destination);

        //Validate if the room is available for the indicated dates
        if(dateFrom.isBefore(DateUtil.convertToDate(hotel.getDateFrom())) ||
            dateTo.isAfter(DateUtil.convertToDate(hotel.getDateTo())) ||
            !hotel.getRoomType().equalsIgnoreCase(room) || hotel.isBooked())
            throw new BookingErrorException(cod, destination, payload.getBooking().getDateFrom(),
                    payload.getBooking().getDateTo(), room);
    }


}
