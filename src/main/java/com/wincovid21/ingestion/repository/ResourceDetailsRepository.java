package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.ResourceDetails;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.newrelic.api.agent.Trace;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDetailsRepository extends CrudRepository<ResourceDetails, Long>, JpaSpecificationExecutor<ResourceDetails> {

    @Query(value = "select distinct state_id, city_id  from resource_details group by state_id, city_id ", nativeQuery = true)
    @Trace(async = true)
    List<Object[]> fetchStateCityDetails();

    @Query(value = "select distinct resourceType  from ResourceDetails")
    @Trace(async = true)
    List<String> getDistinctResourceTypes();

    @Query(value = "select rd from ResourceDetails rd where rd.phone1 = :phoneNumber")
    List<ResourceDetails> fetchResourceDetailsByPhone(String phoneNumber);

    @Query(value = "select rd from ResourceDetails rd where rd.phone1 = :phoneNumber and rd.resourceType = :resourceType and rd.name= :name and rd.category = :category")
    ResourceDetails fetchResourceByPrimaryKey(String phoneNumber, String name, String resourceType, String category);

}
