package com.afb.pos_backend.invoce.controller;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.common.email.service.EmailService;
import com.afb.pos_backend.invoce.dto.InvoiceDTO;
import com.afb.pos_backend.invoce.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/invoices")
@Log4j2
public class InvoiceController {
    private final InvoiceService service;
    private final EmailService emailService;

    // For emails
    private final String companyName;
    private final String companyAddress;
    private final String companyPhone;
    private final String companyEmail;

    public InvoiceController(InvoiceService service, EmailService emailService, @Value("${app.company-name}") String companyName, @Value("${app.company-address}") String companyAddress, @Value("${app.company-phone}") String companyPhone, @Value("${app.company-email}") String companyEmail) {
        this.service = service;
        this.emailService = emailService;

        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
    }

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody @Valid InvoiceDTO invoice) {
        InvoiceDTO invoiceCreated = service.createInvoice(invoice);
        if (invoiceCreated != null
                && invoiceCreated.getClient().getEmail() != null
                && !invoiceCreated.getClient().getEmail().isEmpty()
                && !invoiceCreated.getClient().getEmail().isBlank()) {
            sendEmailInvoice(invoiceCreated);
        }

        return ResponseEntity.status(HttpStatus.OK).body(invoiceCreated);
    }

    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllInvoices(page, size));
    }

    @GetMapping(path = "/byUser/{uid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoicesByCreatedBy(
            @PathVariable String uid,
            @RequestParam LocalDate initialDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllInvoicesOfUser(page, size, uid, initialDate, endDate));
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getInvoice(id));
    }

    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InvoiceDTO> updateInventory(@RequestBody @Valid InvoiceDTO invoice) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateInvoice(invoice));
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> deleteInvoice(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", service.deleteInvoice(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private void sendEmailInvoice(InvoiceDTO invoice) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("companyName", companyName);
        context.setVariable("companyAddress", companyAddress);
        context.setVariable("companyPhone", companyPhone);
        context.setVariable("companyEmail", companyEmail);
        try {
            emailService.sendHtmlEmailWithLogo(
                    invoice.getClient().getEmail(),
                    "Factura #" + invoice.getId(),
                    "invoice",
                    context
            );
        } catch (Exception e) {
            log.error(String.format(MessageConstant.ERROR_MESSAGE_SEND_HTML_EMAIL, "la factura #" + invoice.getId(), "el correo " + invoice.getClient().getEmail()), e.getMessage());
        }
    }

}
