package com.afb.pos_backend.inventory.service.implement;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.inventory.dto.InventoryDTO;
import com.afb.pos_backend.inventory.persistence.entity.Inventory;
import com.afb.pos_backend.inventory.persistence.repository.InventoryRepository;
import com.afb.pos_backend.inventory.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Log4j2
public class InventoryServiceImplementation implements InventoryService {

    private final InventoryRepository repository;
    private final ModelMapper mapper;

    @Override
    public InventoryDTO createInventory(InventoryDTO inventory) {
        boolean itemExists = repository.existsItem(inventory.getItem().getId());
        if (!itemExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "artículo"));
        }
        Inventory inventoryEntity = repository.save(mapper.map(inventory, Inventory.class));
        return mapper.map(inventoryEntity, InventoryDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDTO> getAllInventories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryEntities = repository.findAll(pageable);
        return inventoryEntities.map(inventoryEntity -> mapper.map(inventoryEntity, InventoryDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDTO> getAllInventoriesOfUser(int page, int size, String uid) {
        if (uid == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del usuario"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryEntities = repository.findByCreatedBy(pageable, uid);
        return inventoryEntities.map(inventoryEntity -> mapper.map(inventoryEntity, InventoryDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getInventory(String uid) {
        if (uid == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del inventario"));
        }
        Inventory inventoryEntity = repository.findById(uid).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "inventario")));
        return mapper.map(inventoryEntity, InventoryDTO.class);
    }

    @Override
    public InventoryDTO updateInventory(InventoryDTO inventory) {
        if (inventory.getId() == null || inventory.getId().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del inventario"));
        }
        boolean inventoryExists = repository.existsById(inventory.getId());
        if (!inventoryExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "inventario"));
        }
        Inventory inventoryEntity = repository.save(mapper.map(inventory, Inventory.class));
        return mapper.map(inventoryEntity, InventoryDTO.class);
    }

    @Override
    public String deleteInventory(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del inventario"));
        }
        boolean inventoryExists = repository.existsById(uid);
        if (!inventoryExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "inventario"));
        }
        repository.deleteById(uid);
        log.info("Se ha eliminado el inventario con identificador {}.", uid);
        return String.format("El inventario con identificador: %s ha sido borrado", uid);
    }
}
