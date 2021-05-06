package com.wincovid21.ingestion.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@NonNull
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceDetailDTO {

    @NonNull
    private String name;
    @NonNull
    private String phone1;
    @NonNull
    private String phone2; // Optional
    @NonNull
    private List<Long> resourceTypeIds;
    @NonNull
    private Long resourceTypeId;
    @NonNull
    private Long cityId;
    @NonNull
    private Long stateId;
    @NonNull
    private String additionalComment; // Optional
    @NonNull
    private String address; // Optional
}
