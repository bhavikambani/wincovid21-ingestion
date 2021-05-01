package com.covimyn.ingestion.repository;

import com.covimyn.ingestion.domain.FeedbackType;
import com.covimyn.ingestion.entity.UserActionAudit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionAuditRepository extends CrudRepository<UserActionAudit, Long> {
    Optional<UserActionAudit> findByResourceIdAndFeedbackType(Long resourceId, FeedbackType feedbackType);
}
