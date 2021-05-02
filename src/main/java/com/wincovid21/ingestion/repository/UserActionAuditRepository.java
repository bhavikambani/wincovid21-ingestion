package com.wincovid21.ingestion.repository;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActionAuditRepository extends CrudRepository<UserActionAudit, Long> {
    Optional<UserActionAudit> findByResourceIdAndFeedbackType(Long resourceId, FeedbackType feedbackType);

    @Query(value = "select resourceId, feedbackType  from UserActionAudit group by resourceId, feedbackType ")
    @Trace
    List<Object[]> fetchDetails();

}
