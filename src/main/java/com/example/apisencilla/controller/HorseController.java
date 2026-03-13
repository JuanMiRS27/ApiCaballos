package com.example.apisencilla.controller;

import com.example.apisencilla.dto.HorseRequest;
import com.example.apisencilla.dto.HorseResponse;
import com.example.apisencilla.service.HorseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    public HorseController(HorseService horseService) {
        this.horseService = horseService;
    }

    @GetMapping
    public List<HorseResponse> findAll() {
        return horseService.findAll();
    }

    @GetMapping("/{id}")
    public HorseResponse findById(@PathVariable Long id) {
        return horseService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public HorseResponse create(@Valid @RequestBody HorseRequest request) {
        return horseService.create(request);
    }
}
