package com.afb.pos_backend.inventory.controller;

import com.afb.pos_backend.inventory.dto.InventoryDTO;
import com.afb.pos_backend.inventory.service.InventoryService;
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
@RequestMapping("/v1/inventories")
@AllArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InventoryDTO> createInventory(@RequestBody @Valid InventoryDTO inventory) {
        return ResponseEntity.status(HttpStatus.OK).body(service.createInventory(inventory));
    }

    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<InventoryDTO>> getAllInventories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllInventories(page, size));
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InventoryDTO> getInventory(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getInventory(id));
    }

    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InventoryDTO> updateInventory(@RequestBody @Valid InventoryDTO inventory) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateInventory(inventory));
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> deleteInventory(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", service.deleteInventory(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
