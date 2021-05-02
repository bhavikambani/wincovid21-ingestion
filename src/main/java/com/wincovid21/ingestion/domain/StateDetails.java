package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateDetails {

    private Long id;
    private String stateName;
    private String iconName;
}
