package com.wincovid21.ingestion.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserActionDTO {

    private Long resourceId;

    private String feedbackType;

    private Date updatedOn;

}
