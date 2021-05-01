package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.FeedbackType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackTypesRepository extends CrudRepository<FeedbackType, Long> {
}
