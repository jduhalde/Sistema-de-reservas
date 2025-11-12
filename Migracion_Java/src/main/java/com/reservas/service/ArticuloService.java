package com.reservas.service;

import com.reservas.model.Articulo;
import com.reservas.repository.ArticuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    /**
     * Obtener todos los artículos
     */
    public List<Articulo> obtenerTodos() {
        return articuloRepository.findAll();
    }

    /**
     * Obtener artículo por ID
     */
    public Optional<Articulo> obtenerPorId(Long id) {
        return articuloRepository.findById(id);
    }

    /**
     * Buscar artículos por nombre
     */
    public List<Articulo> buscarPorNombre(String nombre) {
        return articuloRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Obtener artículos disponibles
     */
    public List<Articulo> obtenerDisponibles() {
        return articuloRepository.findByDisponibleTrue();
    }

    /**
     * Obtener artículos por categoría
     */
    public List<Articulo> obtenerPorCategoria(String categoria) {
        return articuloRepository.findByCategoriaIgnoreCase(categoria);
    }

    /**
     * Crear nuevo artículo
     */
    public Articulo crear(Articulo articulo) {
        // Validar datos básicos
        if (articulo.getNombre() == null || articulo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artículo es obligatorio");
        }
        return articuloRepository.save(articulo);
    }

    /**
     * Actualizar artículo existente
     */
    public Articulo actualizar(Long id, Articulo articuloActualizado) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado con ID: " + id));

        articulo.setNombre(articuloActualizado.getNombre());
        articulo.setDescripcion(articuloActualizado.getDescripcion());
        articulo.setCategoria(articuloActualizado.getCategoria());
        articulo.setDisponible(articuloActualizado.isDisponible());

        return articuloRepository.save(articulo);
    }

    /**
     * Cambiar disponibilidad de un artículo
     */
    public Articulo cambiarDisponibilidad(Long id, boolean disponible) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado con ID: " + id));
        
        articulo.setDisponible(disponible);
        return articuloRepository.save(articulo);
    }

    /**
     * Eliminar artículo
     */
    public void eliminar(Long id) {
        if (!articuloRepository.existsById(id)) {
            throw new IllegalArgumentException("Artículo no encontrado con ID: " + id);
        }
        articuloRepository.deleteById(id);
    }

    /**
     * Contar artículos disponibles
     */
    public long contarDisponibles() {
        return articuloRepository.countByDisponibleTrue();
    }
}