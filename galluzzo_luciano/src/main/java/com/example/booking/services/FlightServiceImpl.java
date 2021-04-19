package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;
import com.example.booking.repositories.FlightRepository;
import com.example.booking.utils.DateUtil;
import com.example.booking.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository){
        this.flightRepository = flightRepository;
    }

    @Override
    public List<FlightDTO> getFlights(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, EmptySearchException, MissingFieldsInSearchFlightException {

        if(params.size() == 0)
            return getAllFlights();
        validateParams(params);
        return flightRepository.getFlightsFiltered(DateUtil.convertToDate(params.get("dateFrom")),
                DateUtil.convertToDate(params.get("dateTo")), params.get("origin"), params.get("destination"));
    }

    @Override
    public FlightReservationResponseDTO booking(FlightPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, InexistentHotelErrorException, BookingErrorException, InexistentFlightErrorException, FlightReservationErrorException {
        validatePayload(payload);
        checkBooking(payload);
        FlightReservationResponseDTO flightReservationResponseDTO = new FlightReservationResponseDTO();
        flightReservationResponseDTO.setFlightReservation(payload.getFlightReservation());
        flightReservationResponseDTO.setUserName(payload.getUserName());
        double price = calculatePrice(payload);
        flightReservationResponseDTO.setAmount(price);
        flightReservationResponseDTO.setInterest(calculateInterest(payload.getFlightReservation().getPaymentMethod()));
        double total = flightReservationResponseDTO.getAmount() * (1 + flightReservationResponseDTO.getInterest());
        flightReservationResponseDTO.setTotal(Math.round(total * 100d) / 100d);
        flightReservationResponseDTO.setStatusCode(new StatusDTO(200, "El proceso termino satisfactoriamente"));
        return flightReservationResponseDTO;
    }

    @Override
    public void validatePayload(FlightPayloadDTO payload) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, InvalidEmailException, InvalidPaymentMethodException {
// datefrom and dateto validation
        if(!DateUtil.validateDate(payload.getFlightReservation().getDateFrom())
                || !DateUtil.validateDate(payload.getFlightReservation().getDateTo()))
            throw new DateFormatException();
        if(!DateUtil.convertToDate(payload.getFlightReservation().getDateFrom()).isBefore(
                DateUtil.convertToDate(payload.getFlightReservation().getDateTo())))
            throw new WrongIntervalDateException();

        //origin and destination validation
        if(!flightRepository.destinationExist(payload.getFlightReservation().getDestination()))
            throw new InexistentDestinationException(payload.getFlightReservation().getDestination());

        if(!flightRepository.destinationExist(payload.getFlightReservation().getOrigin()))
            throw new InexistentDestinationException(payload.getFlightReservation().getOrigin());

        // email validation
        if(!EmailUtil.validateEmails(payload.getUserName(), payload.getFlightReservation().getPeople()))
            throw new InvalidEmailException();

        //payment validation
        interestValidation(payload.getFlightReservation().getPaymentMethod().getType(),
                payload.getFlightReservation().getPaymentMethod().getDues());
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
    public double calculatePrice(FlightPayloadDTO payload) throws IOException, InexistentHotelErrorException, InexistentFlightErrorException {
        FlightDTO flight = flightRepository.getFlightByNumberAndRoute(payload.getFlightReservation().getFlightNumber(),
                payload.getFlightReservation().getOrigin(), payload.getFlightReservation().getDestination());
        LocalDate dateTo = DateUtil.convertToDate(payload.getFlightReservation().getDateTo());
        LocalDate dateFrom = DateUtil.convertToDate(payload.getFlightReservation().getDateFrom());
        int days = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);
        return days * flight.getPrice() * payload.getFlightReservation().getSeats();
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
    public void checkBooking(FlightPayloadDTO payload) throws IOException, InexistentHotelErrorException, FlightReservationErrorException, InexistentFlightErrorException {
        String cod = payload.getFlightReservation().getFlightNumber();
        String destination = payload.getFlightReservation().getDestination();
        String origin = payload.getFlightReservation().getOrigin();
        FlightDTO flight = flightRepository.getFlightByNumberAndRoute(cod, origin, destination);
        LocalDate dateFrom = DateUtil.convertToDate(payload.getFlightReservation().getDateFrom());
        LocalDate dateTo = DateUtil.convertToDate(payload.getFlightReservation().getDateTo());

        if(dateFrom.isBefore(DateUtil.convertToDate(flight.getDateFrom())) ||
                dateTo.isAfter(DateUtil.convertToDate(flight.getDateTo())) ||
                !flight.getOrigin().equalsIgnoreCase(origin))
            throw new FlightReservationErrorException(cod, destination, payload.getFlightReservation().getDateFrom(),
                    payload.getFlightReservation().getDateTo(), origin);
    }

    @Override
    public List<FlightDTO> getAllFlights() throws IOException {
        return flightRepository.getAll();
    }

    @Override
    public void validateParams(Map<String, String> params) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, MissingFieldsInSearchFlightException {

        if(!params.containsKey("dateFrom") || !params.containsKey("dateTo") || !params.containsKey("destination")
            || !params.containsKey("origin"))
            throw new MissingFieldsInSearchFlightException();
        String dateFrom = params.get("dateFrom");
        String dateTo = params.get("dateTo");
        String destination = params.get("destination");
        String origin = params.get("origin");
        if(!DateUtil.validateDate(params.get("dateFrom")) ||
                !DateUtil.validateDate(params.get("dateTo")))
            throw new DateFormatException();
        if(!DateUtil.convertToDate(dateFrom).isBefore(DateUtil.convertToDate(dateTo)))
            throw new WrongIntervalDateException();
        if(!flightRepository.destinationExist(destination))
            throw new InexistentDestinationException(destination);
        if(!flightRepository.destinationExist(origin))
            throw new InexistentDestinationException(origin);

    }
}
