package com.reservas.service;

import com.reservas.model.Sala;
import com.reservas.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaService {

    private final SalaRepository salaRepository;

    /**
     * Obtener todas las salas
     */
    public List<Sala> obtenerTodas() {
        return salaRepository.findAll();
    }

    /**
     * Obtener sala por ID
     */
    public Optional<Sala> obtenerPorId(Long id) {
        return salaRepository.findById(id);
    }

    /**
     * Buscar salas por nombre
     */
    public List<Sala> buscarPorNombre(String nombre) {
        return salaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtener salas disponibles
     */
    public List<Sala> obtenerDisponibles() {
        return salaRepository.findByDisponibleTrue();
    }

    /**
     * Obtener salas por capacidad mínima
     */
    public List<Sala> obtenerPorCapacidadMinima(int capacidad) {
        return salaRepository.findByCapacidadGreaterThanEqual(capacidad);
    }

    /**
     * Obtener salas por ubicación
     */
    public List<Sala> obtenerPorUbicacion(String ubicacion) {
        return salaRepository.findByUbicacionContainingIgnoreCase(ubicacion);
    }

    /**
     * Crear nueva sala
     */
    public Sala crear(Sala sala) {
        // Validar datos básicos
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sala es obligatorio");
        }
        if (sala.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        return salaRepository.save(sala);
    }

    /**
     * Actualizar sala existente
     */
    public Sala actualizar(Long id, Sala salaActualizada) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada con ID: " + id));

        sala.setNombre(salaActualizada.getNombre());
        sala.setUbicacion(salaActualizada.getUbicacion());
        sala.setCapacidad(salaActualizada.getCapacidad());
        sala.setDisponible(salaActualizada.isDisponible());

        return salaRepository.save(sala);
    }

    /**
     * Cambiar disponibilidad de una sala
     */
    public Sala cambiarDisponibilidad(Long id, boolean disponible) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada con ID: " + id));
        
        sala.setDisponible(disponible);
        return salaRepository.save(sala);
    }

    /**
     * Eliminar sala
     */
    public void eliminar(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new IllegalArgumentException("Sala no encontrada con ID: " + id);
        }
        salaRepository.deleteById(id);
    }

    /**
     * Contar salas disponibles
     */
    public long contarDisponibles() {
        return salaRepository.countByDisponibleTrue();
    }
}