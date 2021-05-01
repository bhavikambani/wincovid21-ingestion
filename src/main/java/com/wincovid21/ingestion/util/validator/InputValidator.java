package com.wincovid21.ingestion.util.validator;

import com.newrelic.api.agent.Trace;

public interface InputValidator<T> {
    @Trace
    ValidationStatus validate(T element);
}
