package com.afb.pos_backend.payment.service;

import com.afb.pos_backend.payment.dto.PaymentMethodDTO;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethodDTO> findAllPaymentMethods();
}
