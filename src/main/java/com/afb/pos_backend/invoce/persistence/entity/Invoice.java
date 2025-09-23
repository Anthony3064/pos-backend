package com.afb.pos_backend.invoce.persistence.entity;

import com.afb.pos_backend.audit.Auditable;
import com.afb.pos_backend.client.persistence.entity.Client;
import com.afb.pos_backend.payment.persistence.entity.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", schema = "pos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends Auditable {
    @Id
    @Column(name = "invoice_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "invoice_date")
    private LocalDateTime date;

    @Column(name = "annulled")
    private Boolean annulled;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceDetail> details = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    public void addDetail(InvoiceDetail detail) {
        detail.setInvoice(this);
        this.details.add(detail);
    }

    public void removeDetail(InvoiceDetail detail) {
        detail.setInvoice(null);
        this.details.remove(detail);
    }

    @PrePersist
    protected void onCreate() {
        if (annulled == null) {
            this.annulled = false;
        }
    }

}
