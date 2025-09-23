package com.afb.pos_backend.inventory.persistence.entity;

import com.afb.pos_backend.audit.Auditable;
import com.afb.pos_backend.item.persistence.entity.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventories", schema = "pos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @Column(name = "stock")
    private int stock;

    @Column(name = "minimum_stock")
    private Integer minimumStock;

    @Column(name = "max_stock")
    private Integer maxStock;

}
