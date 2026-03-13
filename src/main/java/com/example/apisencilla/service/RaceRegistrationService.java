package com.example.apisencilla.service;

import com.example.apisencilla.dto.RaceRegistrationRequest;
import com.example.apisencilla.dto.RaceRegistrationResponse;
import com.example.apisencilla.model.Horse;
import com.example.apisencilla.model.RaceRegistration;
import com.example.apisencilla.model.Role;
import com.example.apisencilla.model.UserAccount;
import com.example.apisencilla.repository.RaceRegistrationRepository;
import com.example.apisencilla.repository.UserAccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RaceRegistrationService {

    private final RaceRegistrationRepository raceRegistrationRepository;
    private final UserAccountRepository userAccountRepository;
    private final HorseService horseService;

    public RaceRegistrationService(
            RaceRegistrationRepository raceRegistrationRepository,
            UserAccountRepository userAccountRepository,
            HorseService horseService
    ) {
        this.raceRegistrationRepository = raceRegistrationRepository;
        this.userAccountRepository = userAccountRepository;
        this.horseService = horseService;
    }

    public RaceRegistrationResponse create(RaceRegistrationRequest request, String username) {
        UserAccount user = findUser(username);
        Horse horse = horseService.getEntityById(request.horseId());

        if (raceRegistrationRepository.existsByHorseIdAndRaceNameAndRaceDate(
                horse.getId(), request.raceName(), request.raceDate())) {
            throw new IllegalArgumentException("Ese caballo ya esta inscrito en esa carrera para esa fecha");
        }

        RaceRegistration registration = new RaceRegistration();
        registration.setHorse(horse);
        registration.setUser(user);
        registration.setRaceName(request.raceName());
        registration.setRaceCity(request.raceCity());
        registration.setRaceDate(request.raceDate());
        return toResponse(raceRegistrationRepository.save(registration));
    }

    public List<RaceRegistrationResponse> findByUsername(String username) {
        return raceRegistrationRepository.findByUserUsername(username).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RaceRegistrationResponse> findVisibleFor(String username) {
        UserAccount user = findUser(username);
        List<RaceRegistration> registrations = user.getRole() == Role.ADMIN
                ? raceRegistrationRepository.findAll()
                : raceRegistrationRepository.findByUserUsername(username);
        return registrations.stream().map(this::toResponse).toList();
    }

    private UserAccount findUser(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private RaceRegistrationResponse toResponse(RaceRegistration registration) {
        return new RaceRegistrationResponse(
                registration.getId(),
                registration.getHorse().getId(),
                registration.getHorse().getName(),
                registration.getRaceName(),
                registration.getRaceCity(),
                registration.getRaceDate(),
                registration.getUser().getUsername()
        );
    }
}
