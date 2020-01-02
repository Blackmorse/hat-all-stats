package com.blackmorse.hattrick;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Loader {
    private static Map<Integer, String> arabNumbers = new HashMap<>();

    static {
        arabNumbers.put(2, "II");
        arabNumbers.put(3, "III");
        arabNumbers.put(4, "IV");
        arabNumbers.put(5, "V");
        arabNumbers.put(6, "VI");
        arabNumbers.put(7, "VII");
        arabNumbers.put(8, "VIII");
        arabNumbers.put(9, "IX");
        arabNumbers.put(10, "X");
        arabNumbers.put(11, "XI");
        arabNumbers.put(12, "XII");
        arabNumbers.put(13, "XIII");
        arabNumbers.put(14, "XIV");
        arabNumbers.put(15, "XV");
    }

    public void load(List<Integer> countryTeamIds) {

    }
}
