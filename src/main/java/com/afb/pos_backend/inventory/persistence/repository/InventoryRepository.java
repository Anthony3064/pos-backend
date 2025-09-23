package com.afb.pos_backend.inventory.persistence.repository;

import com.afb.pos_backend.inventory.persistence.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Page<Inventory> findByCreatedBy(Pageable pageable, String uid);

    @Query("SELECT CASE WHEN COUNT(item) > 0 THEN true ELSE false END FROM Item item WHERE item.id = :item")
    boolean existsItem(@Param("item") String item);
}
