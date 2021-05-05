package com.wincovid21.ingestion.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@Data
@NonNull
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceDetailDTO {

    @NonNull
    private String name;
    @NonNull
    private String phone1;
    @NonNull
    private Long categoryId;
    @NonNull
    private Long resourceTypeId;
    @NonNull
    private Long cityId;
    @NonNull
    private Long stateId;
}
