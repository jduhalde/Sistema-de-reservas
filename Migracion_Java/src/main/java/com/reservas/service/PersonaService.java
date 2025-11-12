package com.reservas.service;

import com.reservas.model.Persona;
import com.reservas.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;

    /**
     * Obtener todas las personas
     */
    public List<Persona> obtenerTodas() {
        return personaRepository.findAll();
    }

    /**
     * Obtener persona por ID
     */
    public Optional<Persona> obtenerPorId(Long id) {
        return personaRepository.findById(id);
    }

    /**
     * Buscar persona por email
     */
    public Optional<Persona> buscarPorEmail(String email) {
        return personaRepository.findByEmail(email);
    }

    /**
     * Buscar personas por nombre
     */
    public List<Persona> buscarPorNombre(String nombre) {
        return personaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Crear nueva persona
     */
    public Persona crear(Persona persona) {
        // Validar que el email no exista
        if (personaRepository.findByEmail(persona.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona con el email: " + persona.getEmail());
        }
        return personaRepository.save(persona);
    }

    /**
     * Actualizar persona existente
     */
    public Persona actualizar(Long id, Persona personaActualizada) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + id));

        // Validar email único (si cambió)
        if (!persona.getEmail().equals(personaActualizada.getEmail())) {
            personaRepository.findByEmail(personaActualizada.getEmail())
                    .ifPresent(p -> {
                        throw new IllegalArgumentException("El email ya está en uso");
                    });
        }

        persona.setNombre(personaActualizada.getNombre());
        persona.setEmail(personaActualizada.getEmail());
        persona.setTelefono(personaActualizada.getTelefono());
        
        return personaRepository.save(persona);
    }

    /**
     * Eliminar persona
     */
    public void eliminar(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new IllegalArgumentException("Persona no encontrada con ID: " + id);
        }
        personaRepository.deleteById(id);
    }

    /**
     * Verificar si existe persona por email
     */
    public boolean existeEmail(String email) {
        return personaRepository.findByEmail(email).isPresent();
    }
}