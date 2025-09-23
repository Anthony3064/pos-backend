package com.afb.pos_backend.inventory.service;

import com.afb.pos_backend.inventory.dto.InventoryDTO;
import org.springframework.data.domain.Page;

public interface InventoryService {

    InventoryDTO createInventory(InventoryDTO inventory);

    Page<InventoryDTO> getAllInventories(int page, int size);

    Page<InventoryDTO> getAllInventoriesOfUser(int page, int size, String uid);

    InventoryDTO getInventory(String uid);

    InventoryDTO updateInventory(InventoryDTO inventory);

    String deleteInventory(String uid);
}
