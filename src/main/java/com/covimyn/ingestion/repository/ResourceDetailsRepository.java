package com.covimyn.ingestion.repository;

import com.covimyn.ingestion.entity.ResourceDetails;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceDetailsRepository extends CrudRepository<ResourceDetails, Long> {

    @Query("select rd from ResourceDetails rd")
    List<ResourceDetails>  fetchAllResourceDetails();
}
