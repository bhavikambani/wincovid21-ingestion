package com.wincovid21.ingestion.domain;

import lombok.Data;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public class StateWiseConfiguredCities {
    private final StateDetails state;
    private final Set<CityDetails> cityDetailsList;

    public StateWiseConfiguredCities(@NonNull final StateDetails s) {
        this.state = s;
        cityDetailsList = Collections.synchronizedSet(new HashSet<>());
    }

    public void addCity(@NonNull final CityDetails cityDetails) {
        cityDetailsList.add(cityDetails);
    }

    public void addCity(@NonNull final Set<CityDetails> cityDetails) {
        cityDetailsList.addAll(cityDetails);
    }

}
