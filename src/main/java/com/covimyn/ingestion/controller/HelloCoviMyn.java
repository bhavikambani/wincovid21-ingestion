package com.covimyn.ingestion.controller;

import com.covimyn.ingestion.domain.FeedbackType;
import com.covimyn.ingestion.entity.UserActionAudit;
import com.covimyn.ingestion.repository.UserActionAuditRepository;
import lombok.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HelloCoviMyn {

    private final UserActionAuditRepository userActionFlagRepository;

    public HelloCoviMyn(@NonNull final UserActionAuditRepository userActionFlagRepository) {
        this.userActionFlagRepository = userActionFlagRepository;
    }

    @GetMapping("/")
    public String sayHello() {
        UserActionAudit userActionFlag = new UserActionAudit();
        userActionFlag.setFeedbackType(FeedbackType.A);
        userActionFlag.setResourceId(123L);
        userActionFlag.setUpdatedOn(new Date());

        userActionFlagRepository.save(userActionFlag);

        return "Hello!";
    }

}
