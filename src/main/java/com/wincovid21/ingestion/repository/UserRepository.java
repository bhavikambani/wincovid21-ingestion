package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserDetails, Long>, JpaSpecificationExecutor<UserDetails> {

    Optional<UserDetails> findByUserNameAndPassword(String userName, String password);
}
