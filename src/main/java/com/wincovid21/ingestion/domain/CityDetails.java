package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDetails {
    private Long id;
    private String cityName;
    private String iconName;
}
