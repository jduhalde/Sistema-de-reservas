package com.reservas.repository;

import com.reservas.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    Optional<Persona> findByEmail(String email);
    
    List<Persona> findByNombreContainingIgnoreCase(String nombre);
}