package com.afb.pos_backend.item.service;

import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.item.dto.ItemDTO;
import org.springframework.data.domain.Page;


public interface ItemService {

    ItemDTO createItem(ItemDTO item);

    Page<ItemDTO> getAllItems(int page, int size);

    Page<ItemDTO> getAllItems(int page, int size, ItemType type);

    Page<ItemDTO> getAllItemsOfUser(int page, int size, String uid);

    Page<ItemDTO> getAllItemsOfUser(int page, int size, ItemType type, String uid);

    Page<ItemDTO> getAllItemsOfUserAndSearch(int page, int size, String uid, String search);

    ItemDTO getItem(String uid);

    ItemDTO updateItem(ItemDTO item);

    String deleteItem(String uid);

}
