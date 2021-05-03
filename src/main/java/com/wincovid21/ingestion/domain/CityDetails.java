package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDetails implements Comparable<CityDetails> {
    private Long id;
    private String cityName;
    private String iconName;

    @Override
    public int compareTo(CityDetails o) {
        return this.cityName.compareTo(o.cityName);
    }
}
