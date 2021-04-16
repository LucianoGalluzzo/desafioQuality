package com.example.booking.repositories;

import com.example.booking.dtos.FlightDTO;
import com.example.booking.dtos.HotelDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


}
