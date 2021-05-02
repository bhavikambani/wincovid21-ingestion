package com.wincovid21.ingestion.repository;


import com.wincovid21.ingestion.entity.City;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, Long>, JpaSpecificationExecutor<City> {

}

