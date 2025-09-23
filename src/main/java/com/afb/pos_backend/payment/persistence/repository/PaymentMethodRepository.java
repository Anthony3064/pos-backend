package com.afb.pos_backend.payment.persistence.repository;

import com.afb.pos_backend.payment.persistence.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
    List<PaymentMethod> findAllByVisibleTrueOrderByCode();
}
