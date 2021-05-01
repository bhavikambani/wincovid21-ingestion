package com.covimyn.ingestion.repository;

import com.covimyn.ingestion.entity.FeedbackType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackTypesRepository extends CrudRepository<FeedbackType, Long> {
}
