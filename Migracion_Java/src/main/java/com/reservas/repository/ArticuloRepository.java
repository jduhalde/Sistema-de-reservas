package com.reservas.repository;

import com.reservas.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    
    List<Articulo> findByNombreContainingIgnoreCase(String nombre);
    
    List<Articulo> findByDisponibleTrue();
    
    List<Articulo> findByCategoriaIgnoreCase(String categoria);
    
    long countByDisponibleTrue();
}