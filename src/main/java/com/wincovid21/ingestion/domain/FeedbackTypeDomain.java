package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeedbackTypeDomain {
    private Long id;
    private String feedbackCode;
    private String feedbackMessage;
}
