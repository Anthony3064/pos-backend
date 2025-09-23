package com.afb.pos_backend.user.persistence.repository;

import com.afb.pos_backend.common.dto.RoleUser;
import com.afb.pos_backend.user.persistence.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Page<User> findByRole(Pageable pageable, RoleUser role);

    Page<User> findByCreatedBy(Pageable pageable, String uid);

    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User user SET user.active = :value WHERE user.id = :uid")
    int changeActive(@Param("uid") String uid, @Param("value") boolean value);
}
