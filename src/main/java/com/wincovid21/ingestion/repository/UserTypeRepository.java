package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.UserType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeRepository extends CrudRepository<UserType, Long>, JpaSpecificationExecutor<UserType> {

    Optional<UserType> findByUserType(String userType);

}
