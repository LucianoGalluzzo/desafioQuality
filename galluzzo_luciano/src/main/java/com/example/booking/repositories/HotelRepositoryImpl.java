package com.example.booking.repositories;

import com.example.booking.config.EmptySearchException;
import com.example.booking.dtos.HotelDTO;
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
public class HotelRepositoryImpl implements HotelRepository{

    private String path;
    private final List<HotelDTO> dataBase = new ArrayList<>();
    private boolean dataBaseLoaded = false;

    public HotelRepositoryImpl(@Value("${hotel_path:src/main/resources/dbHotels.csv}") String path){
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
                    dataBase.add(new HotelDTO(data));
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

    public List<HotelDTO> getAll() throws IOException {
        loadDataBase();
        return dataBase;
    }

    @Override
    public boolean destinationExist(String destination) throws IOException {
        loadDataBase();
        Optional<HotelDTO> hotel = dataBase.stream().filter(hotelDTO -> hotelDTO.getDestination().equalsIgnoreCase(destination)).
                findFirst();
        if(hotel.isEmpty())
            return false;
        return true;
    }

    @Override
    public List<HotelDTO> getHotelsFiltered(LocalDate dateFrom, LocalDate dateTo, String destination) throws IOException, EmptySearchException {
        loadDataBase();
        List<HotelDTO> filteredList = new ArrayList<>();
        filteredList = dataBase.stream().filter(hotelDTO -> hotelDTO.getDestination().equalsIgnoreCase(destination)
                && !hotelDTO.isBooked() && !dateFrom.isBefore(DateUtil.convertToDate(hotelDTO.getDateFrom()))
                && !dateTo.isAfter(DateUtil.convertToDate(hotelDTO.getDateTo()))).
                collect(Collectors.toList());
        if(filteredList.isEmpty())
            throw new EmptySearchException();
        return filteredList;
    }


}
