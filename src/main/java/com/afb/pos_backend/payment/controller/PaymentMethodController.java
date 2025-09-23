package com.afb.pos_backend.payment.controller;

import com.afb.pos_backend.payment.dto.PaymentMethodDTO;
import com.afb.pos_backend.payment.service.PaymentMethodService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/payment-methods")
@AllArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService service;

    @GetMapping(path = {"", "/"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<PaymentMethodDTO>> getAllPaymentMethods() {
        return ResponseEntity.ok(service.findAllPaymentMethods());
    }

}
