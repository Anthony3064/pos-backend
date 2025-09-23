package com.afb.pos_backend.invoce.service;

import com.afb.pos_backend.invoce.dto.InvoiceDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface InvoiceService {
    InvoiceDTO createInvoice(InvoiceDTO invoice);

    Page<InvoiceDTO> getAllInvoices(int page, int size);

    Page<InvoiceDTO> getAllInvoicesOfUser(int page, int size, String uid, LocalDate initialDate, LocalDate endDate);

    InvoiceDTO getInvoice(String uid);

    InvoiceDTO updateInvoice(InvoiceDTO invoice);

    String deleteInvoice(String uid);
}
