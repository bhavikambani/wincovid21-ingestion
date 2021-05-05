package com.wincovid21.ingestion.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActionDTO {

    private Long resourceId;
    private String feedbackMessage;
    private String feedbackCode;
    private Date updatedOn;
    private String token;

}
