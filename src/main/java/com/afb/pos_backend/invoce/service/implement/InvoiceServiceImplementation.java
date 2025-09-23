package com.afb.pos_backend.invoce.service.implement;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.DuplicateResourceException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.invoce.dto.InvoiceDTO;
import com.afb.pos_backend.invoce.dto.InvoiceDetailDTO;
import com.afb.pos_backend.invoce.persistence.entity.Invoice;
import com.afb.pos_backend.invoce.persistence.repository.InvoiceRepository;
import com.afb.pos_backend.invoce.service.InvoiceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
@AllArgsConstructor
@Log4j2
public class InvoiceServiceImplementation implements InvoiceService {

    private final InvoiceRepository repository;
    private final ModelMapper mapper;

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoice) {

        for (InvoiceDetailDTO detail : invoice.getDetails()) {
            if (detail.getItem().getType().equals(ItemType.PRODUCT)) {
                Integer itemStock = repository.getStockOfItemById(detail.getItem().getId());
                if (itemStock < detail.getAmount()) {
                    throw new DuplicateResourceException(String.format(MessageConstant.NO_STOCK_ERROR_MESSAGE, detail.getItem().getName()));
                }
            }

            detail.setInvoice(invoice);
        }

        Invoice invoiceEntity = repository.save(mapper.map(invoice, Invoice.class));
        return mapper.map(invoiceEntity, InvoiceDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoiceEntities = repository.findAll(pageable);
        return invoiceEntities.map(invoiceEntity -> mapper.map(invoiceEntity, InvoiceDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoicesOfUser(int page, int size, String uid, LocalDate initialDate, LocalDate endDate) {
        if (uid == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del usuario"));
        }
        if (initialDate == null || endDate == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "la fecha inicial y la fecha final"));
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime initialDateParam = initialDate.atStartOfDay();
        LocalDateTime endDateParam = endDate.atTime(LocalTime.MAX);
        Page<Invoice> invoiceEntities = repository.findByCreatedByAndDateBetweenAndAnnulledFalse(pageable, uid, initialDateParam, endDateParam);
        return invoiceEntities.map(invoiceEntity -> mapper.map(invoiceEntity, InvoiceDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoice(String uid) {
        if (uid == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador de la factura"));
        }
        Invoice invoiceEntity = repository.findById(uid).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "factura")));
        return mapper.map(invoiceEntity, InvoiceDTO.class);
    }

    @Override
    public InvoiceDTO updateInvoice(InvoiceDTO invoice) {
        if (invoice.getId() == null || invoice.getId().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador de la factura"));
        }
        boolean invoiceExists = repository.existsById(invoice.getId());
        if (!invoiceExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "factura"));
        }
        Invoice invoiceEntity = repository.save(mapper.map(invoice, Invoice.class));
        return mapper.map(invoiceEntity, InvoiceDTO.class);
    }

    @Override
    public String deleteInvoice(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador de la factura"));
        }
        boolean invoiceExists = repository.existsById(uid);
        if (!invoiceExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "factura"));
        }
        int rowsAffected = repository.annulled(uid, true);
        log.info("Se ha actualizado {}.", rowsAffected);
        return String.format("La factura con identificador: %s ha sido borrado", uid);
    }
}
