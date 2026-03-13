package com.example.apisencilla.dto;

public record HorseResponse(
        Long id,
        String name,
        String breed,
        Integer age,
        String ownerName
) {
}
