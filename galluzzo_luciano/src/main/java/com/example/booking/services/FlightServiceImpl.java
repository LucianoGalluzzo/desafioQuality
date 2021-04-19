package com.example.booking.services;

import com.example.booking.config.*;
import com.example.booking.dtos.*;
import com.example.booking.repositories.FlightRepository;
import com.example.booking.utils.DateUtil;
import com.example.booking.utils.EmailUtil;
import com.example.booking.utils.InterestUtil;
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

    /*
    This method receives the params in a Hashmap, is there aren´t any param, calls to a method which return the
    whole flight list, if there are some param, calls to a method to validate the params and then obtains the
    filtered list
     */
    @Override
    public List<FlightDTO> getFlights(Map<String, String> params) throws IOException, DateFormatException, InexistentDestinationException, WrongIntervalDateException, MissingFieldsInSearchFlightException, EmptySearchFlightException {

        if(params.size() == 0)
            return getAllFlights();
        validateParams(params);
        return flightRepository.getFlightsFiltered(DateUtil.convertToDate(params.get("dateFrom")),
                DateUtil.convertToDate(params.get("dateTo")), params.get("origin"), params.get("destination"));
    }

    @Override
    public List<FlightDTO> getAllFlights() throws IOException {
        return flightRepository.getAll();
    }

    @Override
    public void validateParams(Map<String, String> params) throws DateFormatException, WrongIntervalDateException, IOException, InexistentDestinationException, MissingFieldsInSearchFlightException {

        //Validate that the 4 parameters are indicated in the query
        if(!params.containsKey("dateFrom") || !params.containsKey("dateTo") || !params.containsKey("destination")
                || !params.containsKey("origin"))
            throw new MissingFieldsInSearchFlightException();
        String dateFrom = params.get("dateFrom");
        String dateTo = params.get("dateTo");
        String destination = params.get("destination");
        String origin = params.get("origin");

        //Validate format of both dates
        if(!DateUtil.validateDate(params.get("dateFrom")) ||
                !DateUtil.validateDate(params.get("dateTo")))
            throw new DateFormatException();

        //Validate that dateFrom is previous of dateTo
        if(!DateUtil.convertToDate(dateFrom).isBefore(DateUtil.convertToDate(dateTo)))
            throw new WrongIntervalDateException();

        //Validate that the origin and destination city are valid
        if(!flightRepository.destinationExist(destination))
            throw new InexistentDestinationException(destination);
        if(!flightRepository.destinationExist(origin))
            throw new InexistentDestinationException(origin);

    }

    /*
    This method receives a payload with the reservation information and return the reservation completed
     */
    @Override
    public FlightReservationResponseDTO booking(FlightPayloadDTO payload) throws InvalidRoomException, DateFormatException, InvalidEmailException, WrongIntervalDateException, InvalidPaymentMethodException, InvalidRoomAmountException, InexistentDestinationException, IOException, BookingErrorException, InexistentFlightErrorException, FlightReservationErrorException {
        //Validate that every fields in payload are corrects
        validatePayload(payload);

        //Check that the flight asked are available for the indicated dates and conditions
        checkBooking(payload);

        FlightReservationResponseDTO flightReservationResponseDTO = new FlightReservationResponseDTO();
        flightReservationResponseDTO.setFlightReservation(payload.getFlightReservation());
        flightReservationResponseDTO.setUserName(payload.getUserName());

        //Calculate the price
        double price = calculatePrice(payload);
        flightReservationResponseDTO.setAmount(price);

        //Calculate the interests
        flightReservationResponseDTO.setInterest(InterestUtil.calculateInterest(payload.getFlightReservation().getPaymentMethod()));
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
        InterestUtil.validateInterest(payload.getFlightReservation().getPaymentMethod().getType(),
                payload.getFlightReservation().getPaymentMethod().getDues());
    }

    @Override
    public double calculatePrice(FlightPayloadDTO payload) throws IOException, InexistentFlightErrorException {
        //Obtain from database, the flight asked
        FlightDTO flight = flightRepository.getFlightByNumberAndRoute(payload.getFlightReservation().getFlightNumber(),
                payload.getFlightReservation().getOrigin(), payload.getFlightReservation().getDestination());

        return flight.getPrice() * payload.getFlightReservation().getSeats();
    }

    @Override
    public void checkBooking(FlightPayloadDTO payload) throws IOException, FlightReservationErrorException, InexistentFlightErrorException {

        //Obtain in some variables some important information from payload
        String cod = payload.getFlightReservation().getFlightNumber();
        String destination = payload.getFlightReservation().getDestination();
        String origin = payload.getFlightReservation().getOrigin();
        LocalDate dateFrom = DateUtil.convertToDate(payload.getFlightReservation().getDateFrom());
        LocalDate dateTo = DateUtil.convertToDate(payload.getFlightReservation().getDateTo());
        String seatType = payload.getFlightReservation().getSeatType();

        //Search in database the flight asked
        FlightDTO flight = flightRepository.getFlightByNumberAndRoute(cod, origin, destination);

        //Validate if the flight is available for the indicated dates
        if(dateFrom.isBefore(DateUtil.convertToDate(flight.getDateFrom())) ||
                dateTo.isAfter(DateUtil.convertToDate(flight.getDateTo())) ||
                !flight.getOrigin().equalsIgnoreCase(origin)
                || !flight.getSeatType().equalsIgnoreCase(seatType))
            throw new FlightReservationErrorException(cod, destination, payload.getFlightReservation().getDateFrom(),
                    payload.getFlightReservation().getDateTo(), origin, seatType);
    }

}
