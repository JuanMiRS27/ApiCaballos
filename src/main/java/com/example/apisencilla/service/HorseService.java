package com.example.apisencilla.service;

import com.example.apisencilla.dto.HorseRequest;
import com.example.apisencilla.dto.HorseResponse;
import com.example.apisencilla.model.Horse;
import com.example.apisencilla.repository.HorseRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HorseService {

    private final HorseRepository horseRepository;

    public HorseService(HorseRepository horseRepository) {
        this.horseRepository = horseRepository;
    }

    public List<HorseResponse> findAll() {
        return horseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public HorseResponse findById(Long id) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caballo no encontrado"));
        return toResponse(horse);
    }

    public HorseResponse create(HorseRequest request) {
        Horse horse = new Horse();
        horse.setName(request.name());
        horse.setBreed(request.breed());
        horse.setAge(request.age());
        horse.setOwnerName(request.ownerName());
        return toResponse(horseRepository.save(horse));
    }

    public Horse getEntityById(Long id) {
        return horseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caballo no encontrado"));
    }

    private HorseResponse toResponse(Horse horse) {
        return new HorseResponse(
                horse.getId(),
                horse.getName(),
                horse.getBreed(),
                horse.getAge(),
                horse.getOwnerName()
        );
    }
}
