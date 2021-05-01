package com.covimyn.ingestion.service;

import com.covimyn.ingestion.entity.UserActionAudit;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface UserActionService {

    boolean updateStatus(@NonNull final UserActionAudit userActionAudit);
}
