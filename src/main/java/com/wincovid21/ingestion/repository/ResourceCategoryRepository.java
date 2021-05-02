package com.wincovid21.ingestion.repository;


import com.wincovid21.ingestion.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCategoryRepository extends CrudRepository<ResourceCategory, Long>, JpaSpecificationExecutor<ResourceCategory> {

}

