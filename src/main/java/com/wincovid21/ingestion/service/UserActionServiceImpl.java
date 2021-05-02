package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserActionServiceImpl implements UserActionService {

    private final UserActionAuditRepository userActionAuditRepository;
    private final CacheUtil cacheUtil;


    public UserActionServiceImpl(@NonNull final UserActionAuditRepository userActionAuditRepository,
                                 @NonNull final CacheUtil cacheUtil) {
        this.userActionAuditRepository = userActionAuditRepository;
        this.cacheUtil = cacheUtil;
    }

    @Override
    @Trace
    public void updateStatus(@NonNull UserActionAudit userActionAudit) {
        userActionAuditRepository.save(userActionAudit);
    }

    @Override
    @Trace
    public List<FeedbackType> getFeedbackTypes() {
        return cacheUtil.getFeedbackList();
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
