package com.afb.pos_backend.client.persistence.repository;

import com.afb.pos_backend.client.persistence.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    boolean existsByEmailAndActiveTrue(String email);

    Page<Client> findByCreatedByAndActiveTrueOrderByCompleteName(Pageable pageable, String user);

    Page<Client> findByCreatedByAndActiveTrueAndCompleteNameContainingIgnoreCaseOrderByCompleteName(Pageable pageable, String user, String name);

    @Modifying
    @Query(value = "UPDATE Client c SET c.active = :value WHERE c.id = :uid")
    int changeActive(@Param("uid") String uid, @Param("value") boolean value);
}
