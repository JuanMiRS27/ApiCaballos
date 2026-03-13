package com.example.apisencilla.repository;

import com.example.apisencilla.model.RaceRegistration;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceRegistrationRepository extends JpaRepository<RaceRegistration, Long> {

    @EntityGraph(attributePaths = {"horse", "user"})
    List<RaceRegistration> findByUserUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"horse", "user"})
    List<RaceRegistration> findAll();

    boolean existsByHorseIdAndRaceNameAndRaceDate(Long horseId, String raceName, LocalDate raceDate);
}
