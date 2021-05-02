package com.wincovid21.ingestion.controller;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.service.UserActionServiceImpl;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-action")
public class UserActionController {

    private final CacheUtil cacheUtil;
    private final UserActionServiceImpl userActionService;

    public UserActionController(@NonNull final CacheUtil cacheUtil,
                                @NonNull final UserActionServiceImpl userActionService) {
        this.cacheUtil = cacheUtil;
        this.userActionService = userActionService;
    }

    @PostMapping("/feedback")
    @Trace
    public IngestionResponse<Boolean> userFeedback(@RequestBody UserActionDTO userActionAudit) {
        userActionService.updateStatus(userActionService.toEntity(userActionAudit));
        return IngestionResponse.<Boolean>builder().result(true).httpStatus(HttpStatus.OK).build();
    }

    @GetMapping("/feedback-types")
    @Trace
    public IngestionResponse<List<FeedbackType>> getFeedbackList() {
        return IngestionResponse.<List<FeedbackType>>builder().httpStatus(HttpStatus.OK).result(userActionService.getFeedbackTypes()).build();

    }

    @PutMapping("/inlidate-cache/feedbacklist")
    @Trace
    public void invalidateFeedbackTypeCache() {
        cacheUtil.invalidateFeedbackListCache();
    }
}
