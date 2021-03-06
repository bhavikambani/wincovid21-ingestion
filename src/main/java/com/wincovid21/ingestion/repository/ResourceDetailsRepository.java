package com.wincovid21.ingestion.repository;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.entity.ResourceDetails;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDetailsRepository extends CrudRepository<ResourceDetails, Long>, JpaSpecificationExecutor<ResourceDetails> {

    @Query(value = "select distinct state, city  from resource_details group by state, city ", nativeQuery = true)
    @Trace(async = true)
    List<Object[]> fetchStateCityDetails();

    @Query(value = "select distinct category, resource_type  from resource_details group by category, resource_type ", nativeQuery = true)
    @Trace(async = true)
    List<Object[]> fetchCategoryResourceMapping();

    @Query(value = "select distinct resourceType  from ResourceDetails")
    @Trace(async = true)
    List<String> getDistinctResourceTypes();

    @Query(value = "select rd from ResourceDetails rd where rd.phone1 = :phoneNumber")
    List<ResourceDetails> fetchResourceDetailsByPhone(String phoneNumber);

    @Query(value = "select * from resource_details where phone_1 = :phoneNumber and resource_type = :resourceType and name= :name and category = :category", nativeQuery = true)
    ResourceDetails fetchResourceByPrimaryKey(String phoneNumber, String name, Long resourceType, Long category);

    @Query(value = "select * from resource_details where phone_1 = :phoneNumber and resource_type = :resourceType and category = :category", nativeQuery = true)
    ResourceDetails fetchResourceForDedup(String phoneNumber, Long resourceType, Long category);

}
