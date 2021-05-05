package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.FeedbackType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackTypesRepository extends CrudRepository<FeedbackType, Long>, JpaSpecificationExecutor<FeedbackType> {

    Optional<FeedbackType> findByFeedbackMessage(String feedbackMessage);

    Optional<FeedbackType> findByFeedbackMessageAndFeedbackCode(String feedbackMessage, String feedbackCode);
}
