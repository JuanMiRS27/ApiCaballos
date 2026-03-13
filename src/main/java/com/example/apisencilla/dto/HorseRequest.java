package com.example.apisencilla.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HorseRequest(
        @NotBlank String name,
        @NotBlank String breed,
        @NotNull @Min(2) @Max(30) Integer age,
        @NotBlank String ownerName
) {
}
