package com.example.booking.repositories;

import com.example.booking.config.EmptySearchHotelException;
import com.example.booking.config.InexistentHotelErrorException;
import com.example.booking.dtos.HotelDTO;
import com.example.booking.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
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
    public List<HotelDTO> getHotelsFiltered(LocalDate dateFrom, LocalDate dateTo, String destination) throws IOException, EmptySearchHotelException {
        loadDataBase();
        List<HotelDTO> filteredList = new ArrayList<>();
        filteredList = dataBase.stream().filter(hotelDTO -> hotelDTO.getDestination().equalsIgnoreCase(destination)
                && !hotelDTO.isBooked() && !dateFrom.isBefore(DateUtil.convertToDate(hotelDTO.getDateFrom()))
                && !dateTo.isAfter(DateUtil.convertToDate(hotelDTO.getDateTo()))).
                collect(Collectors.toList());
        if(filteredList.isEmpty())
            throw new EmptySearchHotelException();
        return filteredList;
    }

    @Override
    public HotelDTO getHotelByCodAndDestination(String cod, String destination) throws IOException, InexistentHotelErrorException {
        loadDataBase();
        Optional<HotelDTO> hotel = dataBase.stream().filter(hotelDTO -> hotelDTO.getCod().equals(cod)
            && hotelDTO.getDestination().equalsIgnoreCase(destination)).findFirst();
        if(hotel.isEmpty())
            throw new InexistentHotelErrorException(cod, destination);
        return hotel.get();
    }

    @Override
    public void setReservation(String cod) throws IOException {
        Optional<HotelDTO> hotel = dataBase.stream().filter(hotelDTO -> hotelDTO.getCod().equals(cod)).findFirst();
        if(hotel.isPresent()){
            HotelDTO hotelDTO = hotel.get();
            dataBase.get(dataBase.indexOf(hotelDTO)).setBooked(true);
        }
        updateDB();

    }

    @Override
    public void updateDB() throws IOException {
        FileWriter writer = new FileWriter(path);
        String separator = ",";
        String collect = "Código Hotel,Nombre,Lugar/Ciudad,Tipo de Habitación,Precio por noche,Disponible Desde,Disponible hasta,Reservado\n";
        String booked = "NO";
        for(HotelDTO hotelDTO: dataBase) {
            if(hotelDTO.isBooked())
                booked = "SI";
            else
                booked = "NO";

            collect += hotelDTO.getCod() + separator + hotelDTO.getName() + separator +
                    hotelDTO.getDestination() + separator + hotelDTO.getRoomType() + separator + "$" +
                    hotelDTO.getPrice() + separator + hotelDTO.getDateFrom() + separator + hotelDTO.getDateTo() +
                    separator + booked + "\n";
        }
        writer.write(collect);
        writer.close();
    }
}
