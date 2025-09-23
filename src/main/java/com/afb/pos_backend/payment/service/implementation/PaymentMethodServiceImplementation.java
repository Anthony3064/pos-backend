package com.afb.pos_backend.payment.service.implementation;

import com.afb.pos_backend.payment.dto.PaymentMethodDTO;
import com.afb.pos_backend.payment.persistence.entity.PaymentMethod;
import com.afb.pos_backend.payment.persistence.repository.PaymentMethodRepository;
import com.afb.pos_backend.payment.service.PaymentMethodService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentMethodServiceImplementation implements PaymentMethodService {
    private final ModelMapper mapper;

    private final PaymentMethodRepository repository;

    @Override
    public List<PaymentMethodDTO> findAllPaymentMethods() {
        List<PaymentMethod> paymentMethodsEntities = repository.findAllByVisibleTrueOrderByCode();
        return paymentMethodsEntities.stream().map(paymentMethod -> mapper.map(paymentMethod, PaymentMethodDTO.class)).toList();
    }
}
