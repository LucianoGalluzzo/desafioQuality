package com.example.booking.repositories;

import com.example.booking.config.EmptySearchFlightException;
import com.example.booking.config.InexistentFlightErrorException;
import com.example.booking.dtos.FlightDTO;
import com.example.booking.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FlightRepositoryImpl implements FlightRepository{

    private String path;
    private final List<FlightDTO> dataBase = new ArrayList<>();
    private boolean dataBaseLoaded = false;

    public FlightRepositoryImpl(@Value("${flight_path:src/main/resources/dbFlights.csv}") String path){
        this.path = path;
    }

    public void loadDataBase() throws IOException {
        if(!dataBaseLoaded){
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            try {
                br = new BufferedReader(new FileReader(path));
                line = br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(cvsSplitBy);
                    dataBase.add(new FlightDTO(data));
                }
                dataBaseLoaded = true;
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("File " + path + " not found");
            } catch (IOException e) {
                throw new IOException("Error reading the following file: " + path);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new IOException("Error closing the following file: " + path);
                    }
                }
            }
        }
    }

    public List<FlightDTO> getAll() throws IOException {
        loadDataBase();
        return dataBase;
    }

    @Override
    public boolean destinationExist(String destination) throws IOException {
        loadDataBase();
        Optional<FlightDTO> flight = dataBase.stream().filter(flightDTO -> flightDTO.getDestination().equalsIgnoreCase(destination)).
                findFirst();
        if(flight.isEmpty())
            return false;
        return true;
    }

    @Override
    public List<FlightDTO> getFlightsFiltered(LocalDate dateFrom, LocalDate dateTo, String origin, String destination) throws IOException, EmptySearchFlightException {
        loadDataBase();
        List<FlightDTO> filteredList = new ArrayList<>();
        filteredList = dataBase.stream().filter(flightDTO -> flightDTO.getDestination().equalsIgnoreCase(destination)
                && flightDTO.getOrigin().equalsIgnoreCase(origin)
                && !dateFrom.isBefore(DateUtil.convertToDate(flightDTO.getDateFrom()))
                && !dateTo.isAfter(DateUtil.convertToDate(flightDTO.getDateTo()))).
                collect(Collectors.toList());
        if(filteredList.isEmpty())
            throw new EmptySearchFlightException();
        return filteredList;
    }

    @Override
    public FlightDTO getFlightByNumberAndRoute(String number, String origin, String destination) throws IOException, InexistentFlightErrorException {
        loadDataBase();
        Optional<FlightDTO> flight = dataBase.stream().filter(flightDTO -> flightDTO.getFlightNumber().equals(number)
                && flightDTO.getDestination().equalsIgnoreCase(destination)
                && flightDTO.getOrigin().equalsIgnoreCase(origin)).findFirst();
        if(flight.isEmpty())
            throw new InexistentFlightErrorException(number,origin, destination);
        return flight.get();
    }

}
