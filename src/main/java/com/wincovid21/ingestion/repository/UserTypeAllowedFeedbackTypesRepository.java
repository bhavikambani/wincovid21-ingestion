package com.wincovid21.ingestion.repository;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.entity.UserTypeAllowedFeedbackTypes;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTypeAllowedFeedbackTypesRepository extends CrudRepository<UserTypeAllowedFeedbackTypes, Long>, JpaSpecificationExecutor<UserTypeAllowedFeedbackTypes> {

    @Query(value = "select * from user_allowed_feedback_types rd where rd.user_type = :userType", nativeQuery = true)
    @Trace
    List<UserTypeAllowedFeedbackTypes> findAllByUserType(@NonNull final Long userType);

    @Query(value = "select * from user_allowed_feedback_types rd where rd.feedback_type = :feedbackType", nativeQuery = true)
    @Trace
    List<UserTypeAllowedFeedbackTypes> findAllByFeedbackType(@NonNull final Long feedbackType);

}
