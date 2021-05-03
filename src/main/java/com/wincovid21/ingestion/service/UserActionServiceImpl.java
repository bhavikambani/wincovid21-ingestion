package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.domain.VerificationType;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.service.user.UserAuthService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserActionServiceImpl implements UserActionService {

    private final UserActionAuditRepository userActionAuditRepository;
    private final CacheUtil cacheUtil;
    private final UserAuthService userAuthService;


    public UserActionServiceImpl(@NonNull final UserActionAuditRepository userActionAuditRepository,
                                 @NonNull final CacheUtil cacheUtil,
                                 @NonNull final UserAuthService userAuthService) {
        this.userActionAuditRepository = userActionAuditRepository;
        this.cacheUtil = cacheUtil;
        this.userAuthService = userAuthService;
    }

    @Override
    @Trace
    public void updateStatus(@NonNull UserActionAudit userActionAudit) {
        userActionAuditRepository.save(userActionAudit);
    }

    @Override
    @Trace
    public List<FeedbackType> getFeedbackTypes(final String authToken) {
        List<FeedbackType> feedbackList = cacheUtil.getFeedbackList();
        if (CollectionUtils.isEmpty(feedbackList)) {
            return feedbackList;
        }

        if (StringUtils.hasText(authToken) && userAuthService.isAuthorised(authToken)) {
            return feedbackList;
        } else {
            Set<String> verifiableEnums = Arrays.stream(VerificationType.values()).map(VerificationType::getValue).collect(Collectors.toSet());
            return feedbackList.stream().filter(e -> !(verifiableEnums.contains(e.getFeedbackMessage()))).collect(Collectors.toList());
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
