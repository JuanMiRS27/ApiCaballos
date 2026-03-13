package com.example.apisencilla.dto;

import java.time.LocalDate;

public record RaceRegistrationResponse(
        Long id,
        Long horseId,
        String horseName,
        String raceName,
        String raceCity,
        LocalDate raceDate,
        String registeredBy
) {
}
