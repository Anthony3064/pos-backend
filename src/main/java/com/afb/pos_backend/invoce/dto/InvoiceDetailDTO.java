package com.afb.pos_backend.invoce.dto;

import com.afb.pos_backend.item.dto.ItemDTO;
import com.afb.pos_backend.item.persistence.entity.Item;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailDTO {
    private String id;

    @NotNull(message = "La referencia a la factura es obligatoria.")
    @JsonBackReference("invoice-details")
    private InvoiceDTO invoice;

    @NotNull(message = "La referencia al artículo es obligatorio.")
    private ItemDTO item;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0.")
    private int amount;

    @NotNull(message = "El subtotal es obligatorio.")
    @DecimalMin(value = "1.0", message = "El subtotal debe ser mayor a 0.")
    private BigDecimal subtotal;

    @NotNull(message = "El total es obligatorio.")
    @DecimalMin(value = "1.0", message = "El total debe ser mayor a 0.")
    private BigDecimal total;

}
