package com.afb.pos_backend.item.persistence.entity;

import com.afb.pos_backend.audit.Auditable;
import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.inventory.persistence.entity.Inventory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "items", schema = "pos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private String id;

    @Column(name = "item_code")
    private String code;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType type;

    @Column(name = "item_price")
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;

    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
    }

}
