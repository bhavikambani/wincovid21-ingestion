package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.ResourceSubCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceSubcategoryRepository extends CrudRepository<ResourceSubCategory, Long>, JpaSpecificationExecutor<ResourceSubCategory> {

    @Query(value = "select rsc from ResourceSubCategory rsc where rsc.subCategoryName = :name")
    ResourceSubCategory fetchResourceTypeIdForName(String name);

    @Query(value = "select rsc from ResourceSubCategory rsc where rsc.id = :id ")
    ResourceSubCategory findResourceSubCategoryById(Long id);

}

