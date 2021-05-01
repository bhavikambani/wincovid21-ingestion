package com.covimyn.ingestion.service;

import com.covimyn.ingestion.entity.UserActionAudit;
import com.covimyn.ingestion.repository.UserActionAuditRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UserActionServiceImpl implements UserActionService {

    private final UserActionAuditRepository userActionAuditRepository;

    public UserActionServiceImpl(@NonNull final UserActionAuditRepository userActionAuditRepository) {
        this.userActionAuditRepository = userActionAuditRepository;
    }

    @Override
    public boolean updateStatus(@NonNull UserActionAudit userActionAudit) {



        return false;
    }

}
