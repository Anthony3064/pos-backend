package com.afb.pos_backend.item.service.implement;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.DuplicateResourceException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.item.dto.ItemDTO;
import com.afb.pos_backend.item.persistence.entity.Item;
import com.afb.pos_backend.item.persistence.repository.ItemRepository;
import com.afb.pos_backend.item.service.ItemService;
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
public class ItemServiceImplementation implements ItemService {
    private final ItemRepository repository;
    private final ModelMapper mapper;

    @Override
    public ItemDTO createItem(ItemDTO item) {
        if (item.getCode() == null
                || item.getCode().isEmpty()
                || item.getCode().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "código"));
        }
        switch (item.getType()) {
            case PRODUCT:
                if (item.getInventory() != null) {
                    item.getInventory().setItem(item);
                } else {
                    throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "inventario"));
                }
                break;
            case SERVICE:
                item.setInventory(null);
                break;
            default:
                throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_TYPE_ITEM, "inventario"));
        }

        boolean existsCodeItem = repository.existsByCodeAndActiveTrue(item.getCode());

        if (existsCodeItem) {
            throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_RESOURCE_ERROR, "código"));
        }

        Item itemEntity = repository.save(mapper.map(item, Item.class));
        return mapper.map(itemEntity, ItemDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsEntities = repository.findAll(pageable);
        return itemsEntities.map(itemEntity -> mapper.map(itemEntity, ItemDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItems(int page, int size, ItemType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsEntities = repository.findByType(pageable, type);
        return itemsEntities.map(itemEntity -> mapper.map(itemEntity, ItemDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItemsOfUser(int page, int size, String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del usuario"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsEntities = repository.findByCreatedBy(pageable, uid);
        return itemsEntities.map(itemEntity -> mapper.map(itemEntity, ItemDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItemsOfUser(int page, int size, ItemType type, String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del usuario"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsEntities = repository.findByTypeAndCreatedByAndActiveTrue(pageable, type, uid);
        return itemsEntities.map(itemEntity -> mapper.map(itemEntity, ItemDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItemsOfUserAndSearch(int page, int size, String uid, String search) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "el identificador del usuario"));
        }
        if (search == null) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "parámetro de búsqueda"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemsEntities = repository.findByCreatedByAndActiveTrueAndNameContainingIgnoreCase(pageable, uid, search);
        return itemsEntities.map(itemEntity -> mapper.map(itemEntity, ItemDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItem(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del artículo"));
        }
        Item itemEntity = repository.findById(uid).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "artículo")));
        return mapper.map(itemEntity, ItemDTO.class);
    }

    @Override
    public ItemDTO updateItem(ItemDTO item) {
        if (item.getId() == null || item.getId().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del artículo"));
        }
        boolean itemExists = repository.existsById(item.getId());
        if (!itemExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "artículo"));
        }

        String databaseCode = repository.findById(item.getId()).map(Item::getCode).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "artículo")));

        if (!databaseCode.equals(item.getCode())) {
            if (repository.existsByCodeAndActiveTrue(item.getCode())) {
                throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_RESOURCE_ERROR, "código"));
            }
        }

        item.setActive(true);
        Item itemEntity = repository.save(mapper.map(item, Item.class));
        return mapper.map(itemEntity, ItemDTO.class);
    }

    @Override
    public String deleteItem(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del artículo"));
        }
        boolean itemExists = repository.existsById(uid);
        if (!itemExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "artículo"));
        }
        int rowsAffected = repository.changeActive(uid, false);
        log.info("Se ha actualizado {} registro.", rowsAffected);
        return String.format("El artículo con identificador: %s ha sido borrado", uid);
    }
}
