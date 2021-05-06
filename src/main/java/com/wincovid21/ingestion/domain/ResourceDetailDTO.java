package com.wincovid21.ingestion.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceDetailDTO {

    private String name;
    private String phone1;
    private String phone2; // Optional
    private List<Long> resourceTypeIds;
    private Long cityId;
    private Long stateId;
    private String additionalComment; // Optional
    private String address; // Optional
}
