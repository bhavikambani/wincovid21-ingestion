package com.covimyn.ingestion.util.validator;

import com.newrelic.api.agent.Trace;

public interface InputValidator<T> {
    @Trace
    ValidationStatus validate(T element);
}
