package com.wincovid21.ingestion.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResourceStateCityDetails {
    private String state;

    private List<CityDetails> city = new ArrayList<>();

    public void addCity(String c) {
        this.city.add(CityDetails.builder().cityName(c).build());
    }

}
