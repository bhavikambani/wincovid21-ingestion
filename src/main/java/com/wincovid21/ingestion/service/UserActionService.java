package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.UserActionDTO;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserActionService {

    @Trace
    void updateStatus(@NonNull final UserActionAudit userActionAudit);

    @Trace
    List<FeedbackType> getFeedbackTypes(final String authToken);

    @Trace
    UserActionAudit toEntity(UserActionDTO userActionDTO);
}
