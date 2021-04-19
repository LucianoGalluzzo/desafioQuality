package com.example.booking.utils;

import com.example.booking.dtos.PeopleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class EmailUtil {

    public static boolean validateEmails(String email, List<PeopleDTO> peopleEmails){
        String regex = "^(.+)@(.+)$";
        boolean match = true;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            for (int i = 0; i < peopleEmails.size(); i++) {
                if(!pattern.matcher(peopleEmails.get(i).getMail()).matches())
                    match = false;
            }
            return match;
        }else
            return false;
    }
}
