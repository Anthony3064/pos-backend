package com.afb.pos_backend.item.persistence.repository;

import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.item.persistence.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    @Modifying
    @Query(value = "UPDATE Item item SET item.active = :value WHERE item.id = :uid")
    int changeActive(@Param("uid") String id, @Param("value") boolean value);

    Page<Item> findByCreatedBy(Pageable pageable, @Param("createdBy") String user);

    Page<Item> findByType(Pageable pageable, @Param("type") ItemType type);

    Page<Item> findByTypeAndCreatedByAndActiveTrue(Pageable pageable, @Param("type") ItemType type, @Param("username") String username);

    Page<Item> findByCreatedByAndActiveTrueAndNameContainingIgnoreCase(Pageable pageable, @Param("createdBy") String user, @Param("name") String name);

    boolean existsByCodeAndActiveTrue(String code);
}
