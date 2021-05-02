package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDetails {
    private String cityName;
}
