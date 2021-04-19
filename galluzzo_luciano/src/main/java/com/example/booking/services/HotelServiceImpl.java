package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;
import com.example.booking.repositories.HotelRepository;
import com.example.booking.utils.DateUtil;
import com.example.booking.utils.EmailUtil;
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

    @Override
    public List<HotelDTO> getHotels(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFiledsInSearchHotelException, EmptySearchException {

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
    public void validateParams(Map<String, String> params) throws DateFormatException, MissingFiledsInSearchHotelException, WrongIntervalDateException, IOException, InexistentDestinationException {

        if(!params.containsKey("dateFrom") || !params.containsKey("dateTo") || !params.containsKey("destination"))
            throw new MissingFiledsInSearchHotelException();
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

    @Override
    public HotelBookingResponseDTO booking(HotelPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, InexistentHotelErrorException, BookingErrorException {

        validatePayload(payload);
        checkBooking(payload);
        HotelBookingResponseDTO hotelBookingResponseDTO = new HotelBookingResponseDTO();
        hotelBookingResponseDTO.setBooking(payload.getBooking());
        hotelBookingResponseDTO.setUserName(payload.getUserName());
        double price = calculatePrice(payload);
        hotelBookingResponseDTO.setAmount(price);
        hotelBookingResponseDTO.setInterest(calculateInterest(payload.getBooking().getPaymentMethod()));
        double total = hotelBookingResponseDTO.getAmount() * (1 + hotelBookingResponseDTO.getInterest());
        hotelBookingResponseDTO.setTotal(Math.round(total * 100d) / 100d);
        hotelBookingResponseDTO.setStatusCode(new StatusDTO(200, "El proceso termino satisfactoriamente"));
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
        interestValidation(payload.getBooking().getPaymentMethod().getType(),
                payload.getBooking().getPaymentMethod().getDues());
    }

    @Override
    public void roomValidation(String type, int amount) throws InvalidRoomException, InvalidRoomAmountException {

        type = type.toUpperCase();
        if(!validRooms.containsKey(type))
            throw new InvalidRoomException(type);
        if(type.equals("MULTIPLE") && (amount < 4 || amount > 10) || validRooms.get(type) != amount)
            throw new InvalidRoomAmountException(type, amount);
    }

    @Override
    public void interestValidation(String type, int dues) throws InvalidPaymentMethodException {
        if(!type.equalsIgnoreCase("DEBIT") && !type.equalsIgnoreCase("CREDIT"))
            throw new InvalidPaymentMethodException(type);
        //I consider max dues = 24
        if(type.equalsIgnoreCase("DEBIT") && dues != 1 || (
                type.equalsIgnoreCase("CREDIT") && (dues > 24 || dues < 1)
                ))
            throw new InvalidPaymentMethodException(type, dues);
    }

    @Override
    public double calculatePrice(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException {
        HotelDTO hotel = hotelRepository.getHotelByCodAndDestination(payload.getBooking().getHotelCode(),
                payload.getBooking().getDestination());
        LocalDate dateTo = DateUtil.convertToDate(payload.getBooking().getDateTo());
        LocalDate dateFrom = DateUtil.convertToDate(payload.getBooking().getDateFrom());
        int days = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);
        return days * hotel.getPrice();
    }

    @Override
    public double calculateInterest(PaymentMethodDTO paymentMethod) {
        if(paymentMethod.getType().equalsIgnoreCase("DEBIT"))
            return 0;
        else{
            if(paymentMethod.getDues()<= 3)
                return 0.05;
            if(paymentMethod.getDues()<=6)
                return 0.1;
            if(paymentMethod.getDues()<=12)
                return 0.15;
            else
                return 0.2;
        }
    }

    @Override
    public void checkBooking(HotelPayloadDTO payload) throws IOException, InexistentHotelErrorException, BookingErrorException {
        String cod = payload.getBooking().getHotelCode();
        String destination = payload.getBooking().getDestination();
        String room = payload.getBooking().getRoomType();
        HotelDTO hotel = hotelRepository.getHotelByCodAndDestination(cod, destination);
        LocalDate dateFrom = DateUtil.convertToDate(payload.getBooking().getDateFrom());
        LocalDate dateTo = DateUtil.convertToDate(payload.getBooking().getDateTo());

        if(dateFrom.isBefore(DateUtil.convertToDate(hotel.getDateFrom())) ||
            dateTo.isAfter(DateUtil.convertToDate(hotel.getDateTo())) ||
            !hotel.getRoomType().equalsIgnoreCase(room) || hotel.isBooked())
            throw new BookingErrorException(cod, destination, payload.getBooking().getDateFrom(),
                    payload.getBooking().getDateTo(), room);
    }


}
