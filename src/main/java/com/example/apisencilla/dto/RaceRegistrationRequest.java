package com.example.apisencilla.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RaceRegistrationRequest(
        @NotNull Long horseId,
        @NotBlank String raceName,
        @NotBlank String raceCity,
        @NotNull @Future LocalDate raceDate
) {
}
