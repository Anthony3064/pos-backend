package com.afb.pos_backend.invoce.persistence.repository;

import com.afb.pos_backend.invoce.persistence.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    Page<Invoice> findByCreatedByAndDateBetweenAndAnnulledFalse(Pageable pageable, String uid, LocalDateTime initialDate, LocalDateTime endDate);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Invoice invoice SET invoice.annulled = :value WHERE invoice.id = :uid")
    int annulled(@Param("uid") String uid, @Param("value") boolean value);

    @Query(value = "SELECT item.inventory.stock FROM Item item WHERE item.id = :itemId")
    Integer getStockOfItemById(@Param("itemId") String id);

}
