package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.domain.VerificationType;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.entity.UserSession;
import com.wincovid21.ingestion.exception.UnAuthorizedUserException;
import com.wincovid21.ingestion.repository.FeedbackTypesRepository;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.repository.UserSessionRepository;
import com.wincovid21.ingestion.repository.UserTypeRepository;
import com.wincovid21.ingestion.service.user.UserAuthService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserActionServiceImpl implements UserActionService {

    private final UserActionAuditRepository userActionAuditRepository;
    private final CacheUtil cacheUtil;
    private final UserAuthService userAuthService;
    private final ResourceService resourceService;
    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private FeedbackTypesRepository feedbackTypesRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    public UserActionServiceImpl(@NonNull final UserActionAuditRepository userActionAuditRepository,
                                 @NonNull final CacheUtil cacheUtil,
                                 @NonNull final UserAuthService userAuthService,
                                 @NonNull final ResourceService resourceService) {
        this.userActionAuditRepository = userActionAuditRepository;
        this.cacheUtil = cacheUtil;
        this.userAuthService = userAuthService;
        this.resourceService = resourceService;
    }

    @Override
    @Trace
    public void updateStatus(@NonNull UserActionAudit userActionAudit, String authToken) throws UnAuthorizedUserException, IOException {
        final Set<String> approvalEnums = Arrays.stream(VerificationType.values()).map(VerificationType::getValue).collect(Collectors.toSet());
        log.info("userActionAudit # {}, auth # {}", userActionAudit, authToken);

        userActionAuditRepository.save(userActionAudit);
        log.info("After Update # {}, auth # {}", userActionAudit, authToken);
        if (!CollectionUtils.isEmpty(approvalEnums)) {
            log.info("Enums is not empty");
            if (approvalEnums.contains(userActionAudit.getFeedbackType()))
                if (userAuthService.isAuthorised(authToken)) {
                    resourceService.updateWithVerified(userActionAudit.getResourceId(), userActionAudit.getFeedbackType());
                } else {
                    throw new UnAuthorizedUserException("User is not Authorised to perform this action");
                }
        } else {
            if (cacheUtil.getFeedbackList().stream().map(FeedbackType::getFeedbackMessage).collect(Collectors.toList()).contains(userActionAudit.getFeedbackType())) {
                resourceService.updateWithUnVerified(userActionAudit.getResourceId(), userActionAudit.getFeedbackType());
            } else {
                throw new UnAuthorizedUserException("Invalid Action Performed");
            }
        }
    }

    @Override
    public void updateAndIndexStatus(@NonNull final UserActionDTO userActionAuditDTO,
                                     @NonNull UserActionAudit userActionAudit, String authToken) throws UnAuthorizedUserException, IOException {
        log.info("userActionAudit # {}, auth # {}", userActionAudit, authToken);
        UserActionAudit save = userActionAuditRepository.save(userActionAudit);
        log.info("UserActionAudit # {}, auth # {} saved into DB", save, authToken);
        log.info("Feedback Message # {}", userActionAudit.getFeedbackType());
        Optional<FeedbackType> byFeedbackMessage = feedbackTypesRepository.findByFeedbackMessageAndFeedbackCode(userActionAuditDTO.getFeedbackCode(), userActionAuditDTO.getFeedbackType());

        log.info("byFeedbackMessage" + byFeedbackMessage);

        if (byFeedbackMessage.isPresent()) {
            resourceService.updateES(userActionAudit.getResourceId(), byFeedbackMessage.get());
        }

    }


    @Override
    @Trace
    public List<FeedbackType> getFeedbackTypes(final String authToken) {
        List<FeedbackType> feedbackList = cacheUtil.getFeedbackList();
        if (CollectionUtils.isEmpty(feedbackList)) {
            return feedbackList;
        }
        log.info("Auth token # {}", authToken);

        if (StringUtils.hasText(authToken) && userAuthService.isAuthorised(authToken)) {
            Optional<UserSession> userSessionOptional = userSessionRepository.findByTokenId(authToken);
            log.info("userSessionOptional # {}", userSessionOptional);
            log.info("User Type # {}", userSessionOptional.get().getUser().getUserType().getId());
            return cacheUtil.getUseWiseAllowedFeedback(userSessionOptional.get().getUser().getUserType().getId());
        } else {
            return cacheUtil.getUseWiseAllowedFeedback(userTypeRepository.findByUserType("Visitors").get().getId());
        }
    }

    @Override
    @Trace
    public UserActionAudit toEntity(UserActionDTO userActionDTO) {

        UserActionAudit userActionAudit = new UserActionAudit();
        userActionAudit.setFeedbackType(userActionDTO.getFeedbackType());
        userActionAudit.setResourceId(userActionDTO.getResourceId());
        userActionAudit.setUpdatedOn(new Date());

        return userActionAudit;
    }

}
