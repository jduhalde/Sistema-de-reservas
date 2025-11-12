package com.reservas.service;

import com.reservas.dto.LoginRequest;
import com.reservas.dto.LoginResponse;
import com.reservas.dto.RegisterRequest;
import com.reservas.model.Persona;
import com.reservas.model.Role;
import com.reservas.model.User;
import com.reservas.repository.PersonaRepository;
import com.reservas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El email ya se encuentra registrado: " + request.getEmail());
            }

            if (personaRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El email ya está asociado a una persona existente: " + request.getEmail());
            }

            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_USER)
                    .build();

            userRepository.save(user);

            Persona persona = new Persona();
            persona.setNombre(request.getNombre());
            persona.setEmail(request.getEmail());
            persona.setTelefono(request.getTelefono());

            personaRepository.save(persona);

            String token = jwtService.generateToken(user);

            return LoginResponse.builder()
                    .token(token)
                    .role(user.getRole().name())
                    .email(user.getEmail())
                    .build();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno al registrar: " + e.getMessage(),
                    e);
        }
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .build();
    }
}