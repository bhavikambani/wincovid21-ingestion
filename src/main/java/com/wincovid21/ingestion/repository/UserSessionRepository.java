package com.wincovid21.ingestion.repository;

import com.wincovid21.ingestion.entity.UserSession;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, Long>, JpaSpecificationExecutor<UserSession> {

    Optional<UserSession> findByTokenId(String tokenId);

}
