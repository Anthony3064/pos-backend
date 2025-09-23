package com.afb.pos_backend.item.dto;

import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.inventory.dto.InventoryDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class ItemDTO {
    private String id;

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    private String description;

    @NotNull(message = "El tipo de artículo es requerido")
    private ItemType type;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "1.0", message = "El precio debe ser mayor a 0.")
    private BigDecimal price;

    private String imageUrl;

    private Boolean active;

    @JsonManagedReference
    private InventoryDTO inventory;
}
