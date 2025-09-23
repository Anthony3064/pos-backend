package com.afb.pos_backend.invoce.dto;

import com.afb.pos_backend.client.dto.ClientDTO;
import com.afb.pos_backend.payment.dto.PaymentMethodDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private String id;

    @NotNull(message = "La referencia al cliente es obligatario.")
    private ClientDTO client;

    @NotNull(message = "El total es obligatorio.")
    @DecimalMin(value = "1.0", message = "El total debe ser mayor a 0.")
    private BigDecimal total;

    @NotNull(message = "El total es obligatorio.")
    @DecimalMin(value = "1.0", message = "El total debe ser mayor a 0.")
    private BigDecimal subtotal;

    @NotNull(message = "La fecha es obligatoria.")
    private LocalDateTime date;

    @NotNull(message = "La lista de detalles no puede ser nula.")
    @Size(min = 1, message = "La factura debe tener al menos un detalle.")
    @JsonManagedReference("invoice-details")
    @Valid
    private List<InvoiceDetailDTO> details = new ArrayList<>();

    @NotNull(message = "El método de pago es requerido")
    private PaymentMethodDTO paymentMethod;
}
