package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.domain.ResourceStateCityDetails;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.service.UserActionService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import com.wincovid21.ingestion.util.monit.Profiler;
import com.wincovid21.ingestion.util.monit.ProfilerNames;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class HelloCoviMyn {

    private final UserActionAuditRepository userActionFlagRepository;
    private final UserActionService userActionService;
    private final CacheUtil cacheUtil;

    private final Profiler profiler;

    public HelloCoviMyn(@NonNull final UserActionAuditRepository userActionFlagRepository,
                        @NonNull final Profiler profiler,
                        @NonNull final UserActionService userActionService,
                        @NonNull final CacheUtil cacheUtil) {
        this.userActionFlagRepository = userActionFlagRepository;
        this.profiler = profiler;
        this.userActionService = userActionService;
        this.cacheUtil = cacheUtil;
    }

    @GetMapping("/")
    public IngestionResponse<List<ResourceStateCityDetails>> sayHello() {
        profiler.increment(ProfilerNames.HELLO_TOTAL);
        UserActionAudit userActionFlag = new UserActionAudit();
        userActionFlag.setResourceId(123L);
        userActionFlag.setUpdatedOn(new Date());
        userActionFlag.setFeedbackType("abc");

        userActionFlagRepository.save(userActionFlag);

        List<FeedbackType> feedbackTypes = userActionService.getFeedbackTypes();


        List<Object[]> objects = userActionFlagRepository.fetchDetails();

        List<Response> responses = new ArrayList<>();


        if (!(CollectionUtils.isEmpty(objects))) {
            objects.forEach(a -> responses.add(Response.builder().a(a[0].toString()).b(a[1].toString()).build()));
        }

        IngestionResponse.<List<Response>>builder().httpStatus(HttpStatus.OK).result(responses).build();

        return IngestionResponse.<List<ResourceStateCityDetails>>builder().httpStatus(HttpStatus.OK).result(cacheUtil.getStateCityDetails()).build();
    }


}

@Builder
@Data
class Response {
    String a;
    String b;
}
