package com.reservas.service;

import com.reservas.model.Articulo;
import com.reservas.repository.ArticuloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticuloServiceTest {

    @Mock
    private ArticuloRepository articuloRepository;

    @InjectMocks
    private ArticuloService articuloService;

    @Test
    void obtenerTodos_DeberiaRetornarListaDeArticulos() {
        // Given
        Articulo a1 = new Articulo();
        a1.setId(1L);
        a1.setNombre("Proyector");

        Articulo a2 = new Articulo();
        a2.setId(2L);
        a2.setNombre("Laptop");

        List<Articulo> articulos = Arrays.asList(a1, a2);
        when(articuloRepository.findAll()).thenReturn(articulos);

        // When
        List<Articulo> resultado = articuloService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(articuloRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarArticulo() {
        // Given
        Long id = 1L;
        Articulo a = new Articulo();
        a.setId(id);
        a.setNombre("Proyector");
        when(articuloRepository.findById(id)).thenReturn(Optional.of(a));

        // When
        Optional<Articulo> resultado = articuloService.obtenerPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals("Proyector", resultado.get().getNombre());
        verify(articuloRepository, times(1)).findById(id);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornarVacio() {
        // Given
        Long id = 99L;
        when(articuloRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Articulo> resultado = articuloService.obtenerPorId(id);

        // Then
        assertFalse(resultado.isPresent());
        verify(articuloRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorNombre_DeberiaRetornarArticulosCoincidentes() {
        // Given
        String nombre = "Proyector";
        Articulo a = new Articulo();
        a.setId(1L);
        a.setNombre(nombre);
        when(articuloRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(Arrays.asList(a));

        // When
        List<Articulo> resultado = articuloService.buscarPorNombre(nombre);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(nombre, resultado.get(0).getNombre());
        verify(articuloRepository, times(1)).findByNombreContainingIgnoreCase(nombre);
    }

    @Test
    void crear_CuandoEsValido_DeberiaGuardarArticulo() {
        // Given
        Articulo a = new Articulo();
        a.setNombre("Proyector");
        a.setDescripcion("Proyector Full HD");
        a.setDisponible(true);

        Articulo aGuardado = new Articulo();
        aGuardado.setId(1L);
        aGuardado.setNombre("Proyector");
        aGuardado.setDescripcion("Proyector Full HD");
        aGuardado.setDisponible(true);

        when(articuloRepository.save(any(Articulo.class))).thenReturn(aGuardado);

        // When
        Articulo resultado = articuloService.crear(a);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Proyector", resultado.getNombre());
        verify(articuloRepository, times(1)).save(a);
    }

    @Test
    void crear_CuandoNombreEsNulo_DeberiaLanzarExcepcion() {
        // Given
        Articulo a = new Articulo();
        a.setNombre(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            articuloService.crear(a);
        });
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void crear_CuandoNombreEsVacio_DeberiaLanzarExcepcion() {
        // Given
        Articulo a = new Articulo();
        a.setNombre("   ");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            articuloService.crear(a);
        });
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void actualizar_CuandoExiste_DeberiaActualizarArticulo() {
        // Given
        Long id = 1L;
        Articulo aExistente = new Articulo();
        aExistente.setId(id);
        aExistente.setNombre("Proyector Viejo");
        aExistente.setDescripcion("Descripción antigua");
        aExistente.setDisponible(true);

        Articulo aActualizado = new Articulo();
        aActualizado.setNombre("Proyector Nuevo");
        aActualizado.setDescripcion("Descripción nueva");
        aActualizado.setCategoria("Tecnología");
        aActualizado.setDisponible(false);

        when(articuloRepository.findById(id)).thenReturn(Optional.of(aExistente));
        when(articuloRepository.save(any(Articulo.class))).thenReturn(aExistente);

        // When
        Articulo resultado = articuloService.actualizar(id, aActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("Proyector Nuevo", resultado.getNombre());
        assertEquals("Descripción nueva", resultado.getDescripcion());
        assertFalse(resultado.isDisponible());
        verify(articuloRepository, times(1)).findById(id);
        verify(articuloRepository, times(1)).save(aExistente);
    }

    @Test
    void actualizar_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        Articulo a = new Articulo();
        a.setNombre("Proyector");
        when(articuloRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            articuloService.actualizar(id, a);
        });
        verify(articuloRepository, times(1)).findById(id);
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void eliminar_CuandoExiste_DeberiaEliminarArticulo() {
        // Given
        Long id = 1L;
        when(articuloRepository.existsById(id)).thenReturn(true);
        doNothing().when(articuloRepository).deleteById(id);

        // When
        articuloService.eliminar(id);

        // Then
        verify(articuloRepository, times(1)).existsById(id);
        verify(articuloRepository, times(1)).deleteById(id);
    }

    @Test
    void eliminar_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        when(articuloRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            articuloService.eliminar(id);
        });
        verify(articuloRepository, times(1)).existsById(id);
        verify(articuloRepository, never()).deleteById(any());
    }

    @Test
    void obtenerDisponibles_DeberiaRetornarSoloArticulosDisponibles() {
        // Given
        Articulo a1 = new Articulo();
        a1.setId(1L);
        a1.setNombre("Proyector");
        a1.setDisponible(true);

        Articulo a2 = new Articulo();
        a2.setId(2L);
        a2.setNombre("Laptop");
        a2.setDisponible(true);

        when(articuloRepository.findByDisponibleTrue())
                .thenReturn(Arrays.asList(a1, a2));

        // When
        List<Articulo> resultado = articuloService.obtenerDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(Articulo::isDisponible));
        verify(articuloRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    void cambiarDisponibilidad_CuandoExiste_DeberiaActualizarEstado() {
        // Given
        Long id = 1L;
        Articulo a = new Articulo();
        a.setId(id);
        a.setNombre("Proyector");
        a.setDisponible(false);

        when(articuloRepository.findById(id)).thenReturn(Optional.of(a));
        when(articuloRepository.save(any(Articulo.class))).thenReturn(a);

        // When
        articuloService.cambiarDisponibilidad(id, true);

        // Then
        assertTrue(a.isDisponible());
        verify(articuloRepository, times(1)).findById(id);
        verify(articuloRepository, times(1)).save(a);
    }

    @Test
    void obtenerPorCategoria_DeberiaRetornarArticulosDeLaCategoria() {
        // Given
        String categoria = "Tecnología";
        Articulo a = new Articulo();
        a.setId(1L);
        a.setNombre("Laptop");
        a.setCategoria(categoria);

        when(articuloRepository.findByCategoriaIgnoreCase(categoria))
                .thenReturn(Arrays.asList(a));

        // When
        List<Articulo> resultado = articuloService.obtenerPorCategoria(categoria);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(categoria, resultado.get(0).getCategoria());
        verify(articuloRepository, times(1)).findByCategoriaIgnoreCase(categoria);
    }

    @Test
    void contarDisponibles_DeberiaRetornarCantidadCorrecta() {
        // Given
        when(articuloRepository.countByDisponibleTrue()).thenReturn(5L);

        // When
        long resultado = articuloService.contarDisponibles();

        // Then
        assertEquals(5L, resultado);
        verify(articuloRepository, times(1)).countByDisponibleTrue();
    }
}
