package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.State;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<State, Long>, JpaSpecificationExecutor<State> {

    @Query(value = "select s from State s where s.stateName = :name ")
    State fetchStateIdForName(String name);

}
