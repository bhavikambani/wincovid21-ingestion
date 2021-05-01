package com.covimyn.ingestion.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FeedbackType {

    A("A", "Feedback A"),
    B("B", "Feedback B"),
    C("C", "Feedback C");

    private String code;
    private String feedbackText;

}
