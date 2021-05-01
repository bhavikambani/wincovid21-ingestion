package com.covimyn.ingestion.util.validator;

import com.covimyn.ingestion.entity.UserActionAudit;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserActionAuditValidator implements InputValidator<UserActionAudit> {

    @Override
    @Trace
    public ValidationStatus validate(UserActionAudit element) {
        final ValidationStatus validationStatus = new ValidationStatus();

        if (element == null) {
            validationStatus.setValid(false);
            validationStatus.addValidationMessage("UserActionAudit is null");
        } else {


        }
        return validationStatus;
    }

}
