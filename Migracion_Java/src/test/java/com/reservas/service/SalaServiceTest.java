package com.reservas.service;

import com.reservas.model.Sala;
import com.reservas.repository.SalaRepository;
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
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    void obtenerTodas_DeberiaRetornarListaDeSalas() {
        // Given
        Sala s1 = new Sala();
        s1.setId(1L);
        s1.setNombre("Sala A");
        s1.setCapacidad(20);

        Sala s2 = new Sala();
        s2.setId(2L);
        s2.setNombre("Sala B");
        s2.setCapacidad(30);

        List<Sala> salas = Arrays.asList(s1, s2);
        when(salaRepository.findAll()).thenReturn(salas);

        // When
        List<Sala> resultado = salaService.obtenerTodas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(salaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarSala() {
        // Given
        Long id = 1L;
        Sala s = new Sala();
        s.setId(id);
        s.setNombre("Sala A");
        s.setCapacidad(20);
        when(salaRepository.findById(id)).thenReturn(Optional.of(s));

        // When
        Optional<Sala> resultado = salaService.obtenerPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals("Sala A", resultado.get().getNombre());
        assertEquals(20, resultado.get().getCapacidad());
        verify(salaRepository, times(1)).findById(id);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornarVacio() {
        // Given
        Long id = 99L;
        when(salaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Sala> resultado = salaService.obtenerPorId(id);

        // Then
        assertFalse(resultado.isPresent());
        verify(salaRepository, times(1)).findById(id);
    }

    @Test
    void buscarPorNombre_DeberiaRetornarSalasCoincidentes() {
        // Given
        String nombre = "Sala A";
        Sala s = new Sala();
        s.setId(1L);
        s.setNombre(nombre);
        s.setCapacidad(20);
        when(salaRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(Arrays.asList(s));

        // When
        List<Sala> resultado = salaService.buscarPorNombre(nombre);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(nombre, resultado.get(0).getNombre());
        verify(salaRepository, times(1)).findByNombreContainingIgnoreCase(nombre);
    }

    @Test
    void crear_CuandoEsValida_DeberiaGuardarSala() {
        // Given
        Sala s = new Sala();
        s.setNombre("Sala A");
        s.setCapacidad(20);
        s.setUbicacion("Piso 1");
        s.setDisponible(true);

        Sala sGuardada = new Sala();
        sGuardada.setId(1L);
        sGuardada.setNombre("Sala A");
        sGuardada.setCapacidad(20);
        sGuardada.setUbicacion("Piso 1");
        sGuardada.setDisponible(true);

        when(salaRepository.save(any(Sala.class))).thenReturn(sGuardada);

        // When
        Sala resultado = salaService.crear(s);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Sala A", resultado.getNombre());
        assertEquals(20, resultado.getCapacidad());
        verify(salaRepository, times(1)).save(s);
    }

    @Test
    void crear_CuandoNombreEsNulo_DeberiaLanzarExcepcion() {
        // Given
        Sala s = new Sala();
        s.setNombre(null);
        s.setCapacidad(20);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            salaService.crear(s);
        });
        verify(salaRepository, never()).save(any());
    }

    @Test
    void crear_CuandoCapacidadEsCero_DeberiaLanzarExcepcion() {
        // Given
        Sala s = new Sala();
        s.setNombre("Sala A");
        s.setCapacidad(0);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            salaService.crear(s);
        });
        verify(salaRepository, never()).save(any());
    }

    @Test
    void actualizar_CuandoExiste_DeberiaActualizarSala() {
        // Given
        Long id = 1L;
        Sala sExistente = new Sala();
        sExistente.setId(id);
        sExistente.setNombre("Sala Vieja");
        sExistente.setCapacidad(20);
        sExistente.setUbicacion("Piso 1");
        sExistente.setDisponible(true);

        Sala sActualizada = new Sala();
        sActualizada.setNombre("Sala Nueva");
        sActualizada.setCapacidad(30);
        sActualizada.setUbicacion("Piso 2");
        sActualizada.setDisponible(false);

        when(salaRepository.findById(id)).thenReturn(Optional.of(sExistente));
        when(salaRepository.save(any(Sala.class))).thenReturn(sExistente);

        // When
        Sala resultado = salaService.actualizar(id, sActualizada);

        // Then
        assertNotNull(resultado);
        assertEquals("Sala Nueva", resultado.getNombre());
        assertEquals(30, resultado.getCapacidad());
        assertEquals("Piso 2", resultado.getUbicacion());
        assertFalse(resultado.isDisponible());
        verify(salaRepository, times(1)).findById(id);
        verify(salaRepository, times(1)).save(sExistente);
    }

    @Test
    void actualizar_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        Sala s = new Sala();
        s.setNombre("Sala A");
        when(salaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            salaService.actualizar(id, s);
        });
        verify(salaRepository, times(1)).findById(id);
        verify(salaRepository, never()).save(any());
    }

    @Test
    void eliminar_CuandoExiste_DeberiaEliminarSala() {
        // Given
        Long id = 1L;
        when(salaRepository.existsById(id)).thenReturn(true);
        doNothing().when(salaRepository).deleteById(id);

        // When
        salaService.eliminar(id);

        // Then
        verify(salaRepository, times(1)).existsById(id);
        verify(salaRepository, times(1)).deleteById(id);
    }

    @Test
    void eliminar_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        when(salaRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            salaService.eliminar(id);
        });
        verify(salaRepository, times(1)).existsById(id);
        verify(salaRepository, never()).deleteById(any());
    }

    @Test
    void obtenerDisponibles_DeberiaRetornarSoloSalasDisponibles() {
        // Given
        Sala s1 = new Sala();
        s1.setId(1L);
        s1.setNombre("Sala A");
        s1.setDisponible(true);

        Sala s2 = new Sala();
        s2.setId(2L);
        s2.setNombre("Sala B");
        s2.setDisponible(true);

        when(salaRepository.findByDisponibleTrue())
                .thenReturn(Arrays.asList(s1, s2));

        // When
        List<Sala> resultado = salaService.obtenerDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(Sala::isDisponible));
        verify(salaRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    void obtenerPorCapacidadMinima_DeberiaRetornarSalasConCapacidadSuficiente() {
        // Given
        int capacidadMinima = 25;
        Sala s1 = new Sala();
        s1.setId(1L);
        s1.setNombre("Sala Grande");
        s1.setCapacidad(30);

        Sala s2 = new Sala();
        s2.setId(2L);
        s2.setNombre("Sala Mediana");
        s2.setCapacidad(25);

        when(salaRepository.findByCapacidadGreaterThanEqual(capacidadMinima))
                .thenReturn(Arrays.asList(s1, s2));

        // When
        List<Sala> resultado = salaService.obtenerPorCapacidadMinima(capacidadMinima);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(s -> s.getCapacidad() >= capacidadMinima));
        verify(salaRepository, times(1)).findByCapacidadGreaterThanEqual(capacidadMinima);
    }

    @Test
    void cambiarDisponibilidad_CuandoExiste_DeberiaActualizarEstado() {
        // Given
        Long id = 1L;
        Sala s = new Sala();
        s.setId(id);
        s.setNombre("Sala A");
        s.setDisponible(false);

        when(salaRepository.findById(id)).thenReturn(Optional.of(s));
        when(salaRepository.save(any(Sala.class))).thenReturn(s);

        // When
        salaService.cambiarDisponibilidad(id, true);

        // Then
        assertTrue(s.isDisponible());
        verify(salaRepository, times(1)).findById(id);
        verify(salaRepository, times(1)).save(s);
    }

    @Test
    void obtenerPorUbicacion_DeberiaRetornarSalasDeLaUbicacion() {
        // Given
        String ubicacion = "Piso 1";
        Sala s = new Sala();
        s.setId(1L);
        s.setNombre("Sala A");
        s.setUbicacion(ubicacion);

        when(salaRepository.findByUbicacionContainingIgnoreCase(ubicacion))
                .thenReturn(Arrays.asList(s));

        // When
        List<Sala> resultado = salaService.obtenerPorUbicacion(ubicacion);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(ubicacion, resultado.get(0).getUbicacion());
        verify(salaRepository, times(1)).findByUbicacionContainingIgnoreCase(ubicacion);
    }

    @Test
    void contarDisponibles_DeberiaRetornarCantidadCorrecta() {
        // Given
        when(salaRepository.countByDisponibleTrue()).thenReturn(3L);

        // When
        long resultado = salaService.contarDisponibles();

        // Then
        assertEquals(3L, resultado);
        verify(salaRepository, times(1)).countByDisponibleTrue();
    }
}
