package com.covimyn.ingestion.repository;

import com.covimyn.ingestion.domain.FeedbackType;
import com.covimyn.ingestion.entity.UserActionFlag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionFlagRepository extends CrudRepository<UserActionFlag, Long> {

    Optional<UserActionFlag> findByResourceIdAndFeedbackType(Long resourceId, FeedbackType feedbackType);

}
