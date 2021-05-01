package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
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
        userActionFlag.setResourceId(123L);
        userActionFlag.setUpdatedOn(new Date());

        userActionFlagRepository.save(userActionFlag);

        return "Hello!";
    }

}
