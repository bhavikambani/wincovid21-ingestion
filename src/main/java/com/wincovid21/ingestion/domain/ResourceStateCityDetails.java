package com.wincovid21.ingestion.domain;

import com.wincovid21.ingestion.entity.City;
import com.wincovid21.ingestion.entity.State;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResourceStateCityDetails {
    private State state;

    private List<City> city = new ArrayList<>();

    public void addCity(City c) {
        this.city.add(c);
    }

}
