package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionAuditRepository extends CrudRepository<UserActionAudit, Long> {
    Optional<UserActionAudit> findByResourceIdAndFeedbackType(Long resourceId, FeedbackType feedbackType);
}
