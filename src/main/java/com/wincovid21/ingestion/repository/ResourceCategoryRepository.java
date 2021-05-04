package com.wincovid21.ingestion.repository;


import com.wincovid21.ingestion.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCategoryRepository extends CrudRepository<ResourceCategory, Long>, JpaSpecificationExecutor<ResourceCategory> {

    @Query(value = "select rc from ResourceCategory rc where rc.categoryName = :name")
    ResourceCategory fetchCategoryIdForName(String name);

    @Query(value = "select rc from ResourceCategory rc where rc.id = :id ")
    ResourceCategory findResourceCategoryById(Long id);

}

