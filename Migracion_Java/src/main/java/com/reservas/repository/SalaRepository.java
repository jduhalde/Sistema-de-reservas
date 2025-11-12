package com.reservas.repository;

import com.reservas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    List<Sala> findByNombreContainingIgnoreCase(String nombre);
    
    List<Sala> findByDisponibleTrue();
    
    List<Sala> findByCapacidadGreaterThanEqual(int capacidad);
    
    List<Sala> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    long countByDisponibleTrue();
}