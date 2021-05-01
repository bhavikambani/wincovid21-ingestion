package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.ResourceDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDetailsRepository extends CrudRepository<ResourceDetails, Long> {

    @Query("select rd from ResourceDetails rd")
    List<ResourceDetails>  fetchAllResourceDetails();
}
