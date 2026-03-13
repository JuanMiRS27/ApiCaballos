package com.example.apisencilla.service;

import com.example.apisencilla.dto.AuthRequest;
import com.example.apisencilla.dto.AuthResponse;
import com.example.apisencilla.dto.RegisterRequest;
import com.example.apisencilla.model.Role;
import com.example.apisencilla.model.UserAccount;
import com.example.apisencilla.repository.UserAccountRepository;
import com.example.apisencilla.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya existe");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userAccountRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(jwtService.generateToken(userDetails), user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserAccount user = userAccountRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(jwtService.generateToken(userDetails), user.getUsername(), user.getRole().name());
    }
}
