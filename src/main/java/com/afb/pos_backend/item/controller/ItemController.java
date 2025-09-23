package com.afb.pos_backend.item.controller;

import com.afb.pos_backend.common.dto.ItemType;
import com.afb.pos_backend.item.dto.ItemDTO;
import com.afb.pos_backend.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> createItem(@RequestBody @Valid ItemDTO item) {
        return ResponseEntity.status(HttpStatus.OK).body(service.createItem(item));
    }

    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ItemDTO>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "type", required = false) ItemType type) {
        Page<ItemDTO> items;
        if (type == null) {
            items = service.getAllItems(page, size);
        } else {
            items = service.getAllItems(page, size, type);
        }
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping(path = "/byUser/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ItemDTO>> getAllItemsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "type", required = false) ItemType type,
            @PathVariable String username) {
        Page<ItemDTO> items;
        if (type == null) {
            items = service.getAllItemsOfUser(page, size, username);
        } else {
            items = service.getAllItemsOfUser(page, size, type, username);
        }
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping(path = "/byUser/{username}/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ItemDTO>> getAllItemsByUserAndSearch(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam String search) {
        Page<ItemDTO> items;
        items = service.getAllItemsOfUserAndSearch(page, size, username, search);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> getItem(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getItem(id));
    }

    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> updateItem(@RequestBody @Valid ItemDTO item) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateItem(item));
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", service.deleteItem(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
