package com.example.apisencilla.controller;

import com.example.apisencilla.dto.RaceRegistrationRequest;
import com.example.apisencilla.dto.RaceRegistrationResponse;
import com.example.apisencilla.service.RaceRegistrationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registrations")
public class RaceRegistrationController {

    private final RaceRegistrationService raceRegistrationService;

    public RaceRegistrationController(RaceRegistrationService raceRegistrationService) {
        this.raceRegistrationService = raceRegistrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RaceRegistrationResponse register(
            @Valid @RequestBody RaceRegistrationRequest request,
            Authentication authentication
    ) {
        return raceRegistrationService.create(request, authentication.getName());
    }

    @GetMapping("/mine")
    public List<RaceRegistrationResponse> mine(Authentication authentication) {
        return raceRegistrationService.findByUsername(authentication.getName());
    }

    @GetMapping
    public List<RaceRegistrationResponse> all(Authentication authentication) {
        return raceRegistrationService.findVisibleFor(authentication.getName());
    }
}
