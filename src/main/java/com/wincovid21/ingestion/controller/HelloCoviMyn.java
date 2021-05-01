package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.service.UserActionService;
import com.wincovid21.ingestion.util.monit.Profiler;
import com.wincovid21.ingestion.util.monit.ProfilerNames;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class HelloCoviMyn {

    private final UserActionAuditRepository userActionFlagRepository;
    private final UserActionService userActionService;

    private final Profiler profiler;

    public HelloCoviMyn(@NonNull final UserActionAuditRepository userActionFlagRepository,
                        @NonNull final Profiler profiler, UserActionService userActionService) {
        this.userActionFlagRepository = userActionFlagRepository;
        this.profiler = profiler;
        this.userActionService = userActionService;
    }

    @GetMapping("/")
    public IngestionResponse<List<FeedbackType>> sayHello() {
        profiler.increment(ProfilerNames.HELLO_TOTAL);
        UserActionAudit userActionFlag = new UserActionAudit();
        userActionFlag.setResourceId(123L);
        userActionFlag.setUpdatedOn(new Date());
        userActionFlag.setFeedbackType("abc");

        userActionFlagRepository.save(userActionFlag);

        List<FeedbackType> feedbackTypes = userActionService.getFeedbackTypes();

        return IngestionResponse.<List<FeedbackType>>builder().httpStatus(HttpStatus.OK).result(feedbackTypes).build();
    }

}
