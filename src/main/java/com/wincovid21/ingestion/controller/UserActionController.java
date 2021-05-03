package com.wincovid21.ingestion.controller;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.domain.LoginRequest;
import com.wincovid21.ingestion.domain.LoginResponse;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserSession;
import com.wincovid21.ingestion.exception.UnAuthorizedUserException;
import com.wincovid21.ingestion.service.UserActionServiceImpl;
import com.wincovid21.ingestion.service.user.UserAuthService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user-action")
public class UserActionController {

    private final CacheUtil cacheUtil;
    private final UserActionServiceImpl userActionService;
    private final UserAuthService userAuthService;

    public UserActionController(@NonNull final CacheUtil cacheUtil,
                                @NonNull final UserActionServiceImpl userActionService,
                                @NonNull final UserAuthService userAuthService) {
        this.cacheUtil = cacheUtil;
        this.userActionService = userActionService;
        this.userAuthService = userAuthService;
    }

    @PostMapping("/feedback")
    @Trace
    public ResponseEntity<Boolean> userFeedback(@RequestBody UserActionDTO userActionAudit,
                                                final @RequestHeader(value = "covid-at", required = false) String authToken) {
        try {
            log.info("Feedback request # {}, auth @ {}", userActionAudit, authToken);
            userActionService.updateStatus(userActionService.toEntity(userActionAudit), authToken);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        } catch (UnAuthorizedUserException ue) {
            log.error("UnAuthorised user action # {}.", userActionAudit, ue);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (IOException e) {
            log.error("UnAuthorised user action # {}.", userActionAudit, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/login")
    @Trace
    public ResponseEntity<LoginResponse> userFeedback(@RequestBody LoginRequest loginRequest) {

        try {
            final UserSession userSession = userAuthService.login(loginRequest);

            final LoginResponse response = LoginResponse.builder()
                    .message("Login Successful")
                    .token(userSession.getTokenId())
                    .user(loginRequest.getUser())
                    .build();

            return ResponseEntity.ok(response);

        } catch (UnAuthorizedUserException e) {
            log.error("UnAuthorised user login for user # {}.", loginRequest, e);
            LoginResponse response = LoginResponse.builder()
                    .message("Un Authorised Action Performed")
                    .user(loginRequest.getUser())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception t) {
            log.error("Exception while authenticating # {}.", loginRequest, t);
            LoginResponse response = LoginResponse.builder()
                    .message("Internal Server Error")
                    .user(loginRequest.getUser())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/feedback-types")
    @Trace
    public IngestionResponse<List<FeedbackType>> getFeedbackList(final @RequestHeader(value = "covid-at", required = false) String authToken) {
        return IngestionResponse.<List<FeedbackType>>builder().httpStatus(HttpStatus.OK).result(userActionService.getFeedbackTypes(authToken)).build();
    }

    @PutMapping("/inlidate-cache/feedbacklist")
    @Trace
    public void invalidateFeedbackTypeCache() {
        cacheUtil.invalidateFeedbackListCache();
    }
}
