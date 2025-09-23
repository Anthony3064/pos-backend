package com.afb.pos_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class MainController {

    public final String COMPANY_NAME;

    public MainController(@Value("app.company-name") String companyName) {
        this.COMPANY_NAME = companyName;
    }

    @GetMapping(path = {"/", ""}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> getMain() {
        Map<String, Object> response = Map.of("message", String.format("%s BACKEND IS RUNNING!", COMPANY_NAME), "date", LocalDate.now());
        return ResponseEntity.ok(response);
    }

}
