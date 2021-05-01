package com.covimyn.ingestion.util.validator;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@ToString
@EqualsAndHashCode
public class ValidationStatus {

    @Setter
    @Getter
    private boolean isValid;

    @Getter
    private final List<String> validationMessage = new ArrayList<>();

    public void addValidationMessage(@NonNull final String message) {
        validationMessage.add(message);
    }
}
