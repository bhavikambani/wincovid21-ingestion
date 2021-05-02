package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.State;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<State, Long>, JpaSpecificationExecutor<State> {

}
