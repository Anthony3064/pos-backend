package com.afb.pos_backend.inventory.dto;

import com.afb.pos_backend.item.dto.ItemDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
    private String id;

    @NotNull(message = "La referencia al artículo es obligatorio.")
    @JsonBackReference
    private ItemDTO item;

    @NotNull(message = "La cantidad de stock es obligatoria.")
    @Min(value = 0, message = "La cantidad de stock debe ser mayor o igual a 0.")
    private Integer stock;

    private Integer minimumStock;

    private Integer maxStock;

}
