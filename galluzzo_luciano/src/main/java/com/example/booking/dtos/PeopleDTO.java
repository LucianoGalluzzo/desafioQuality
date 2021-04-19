package com.example.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeopleDTO {

    private String dni;
    private String name;
    private String lastName;
    private String birthDate;
    private String mail;
}
