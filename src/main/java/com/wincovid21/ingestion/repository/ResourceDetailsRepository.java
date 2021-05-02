package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.ResourceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDetailsRepository extends JpaRepository<ResourceDetails, Long>, JpaSpecificationExecutor<ResourceDetails> {

    @Query(value = "select rd from ResourceDetails rd where rd.phone1 = :phoneNumber")
    List<ResourceDetails>  fetchResourceDetailsByPhone(String phoneNumber);

    @Query(value = "select rd from ResourceDetails rd where rd.phone1 = :phoneNumber and rd.resourceType = :resourceType and rd.name= :name and rd.category = :category")
    ResourceDetails fetchResourceByPrimaryKey(String phoneNumber, String name, String resourceType, String category);
}
