package com.reservas.service;

import com.reservas.model.Articulo;
import com.reservas.model.Persona;
import com.reservas.model.Reserva;
import com.reservas.model.Sala;
import com.reservas.repository.ArticuloRepository;
import com.reservas.repository.PersonaRepository;
import com.reservas.repository.ReservaRepository;
import com.reservas.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private ArticuloRepository articuloRepository;

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Persona persona;
    private Sala sala;
    private Articulo articulo;

    @BeforeEach
    void setUp() {
        persona = new Persona();
        persona.setId(1L);
        persona.setNombre("Juan");
        persona.setEmail("juan@test.com");

        sala = new Sala();
        sala.setId(1L);
        sala.setNombre("Sala A");
        sala.setCapacidad(20);
        sala.setDisponible(true);

        articulo = new Articulo();
        articulo.setId(1L);
        articulo.setNombre("Proyector");
        articulo.setDisponible(true);
    }

    @Test
    void obtenerTodasLasReservas_DeberiaRetornarListaDeReservas() {
        // Given
        Reserva r1 = new Reserva();
        r1.setId(1L);
        r1.setPersona(persona);
        r1.setSala(sala);

        Reserva r2 = new Reserva();
        r2.setId(2L);
        r2.setPersona(persona);
        r2.setSala(sala);

        List<Reserva> reservas = Arrays.asList(r1, r2);
        when(reservaRepository.findAll()).thenReturn(reservas);

        // When
        List<Reserva> resultado = reservaService.obtenerTodasLasReservas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void obtenerReservaPorId_CuandoExiste_DeberiaRetornarReserva() {
        // Given
        Long id = 1L;
        Reserva r = new Reserva();
        r.setId(id);
        r.setPersona(persona);
        r.setSala(sala);
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        when(reservaRepository.findById(id)).thenReturn(Optional.of(r));

        // When
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(persona.getId(), resultado.get().getPersona().getId());
        verify(reservaRepository, times(1)).findById(id);
    }

    @Test
    void obtenerReservaPorId_CuandoNoExiste_DeberiaRetornarVacio() {
        // Given
        Long id = 99L;
        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(id);

        // Then
        assertFalse(resultado.isPresent());
        verify(reservaRepository, times(1)).findById(id);
    }

    @Test
    void crearReserva_CuandoEsValida_DeberiaGuardarReserva() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setSala(sala);
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        Reserva rGuardada = new Reserva();
        rGuardada.setId(1L);
        rGuardada.setPersona(persona);
        rGuardada.setSala(sala);
        rGuardada.setFechaHoraInicio(r.getFechaHoraInicio());
        rGuardada.setFechaHoraFin(r.getFechaHoraFin());

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.of(persona));
        when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));
        when(reservaRepository.findAll()).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenReturn(rGuardada);

        // When
        Reserva resultado = reservaService.crearReserva(r);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        verify(reservaRepository, times(1)).save(r);
    }

    @Test
    void crearReserva_CuandoPersonaEsNula_DeberiaLanzarExcepcion() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(null);
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(r);
        });
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_CuandoPersonaNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(r);
        });
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_CuandoFechaInicioEsNula_DeberiaLanzarExcepcion() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setFechaHoraInicio(null);
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1));

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(r);
        });
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_CuandoFechaInicioEsPosteriorAFechaFin_DeberiaLanzarExcepcion() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(2));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1));

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(r);
        });
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_CuandoFechaEsEnElPasado_DeberiaLanzarExcepcion() {
        // Given
        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setFechaHoraInicio(LocalDateTime.now().minusDays(1));
        r.setFechaHoraFin(LocalDateTime.now());

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(r);
        });
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void actualizarReserva_CuandoExiste_DeberiaActualizarReserva() {
        // Given
        Long id = 1L;
        Reserva rExistente = new Reserva();
        rExistente.setId(id);
        rExistente.setPersona(persona);
        rExistente.setSala(sala);
        rExistente.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        rExistente.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        Reserva rActualizada = new Reserva();
        rActualizada.setFechaHoraInicio(LocalDateTime.now().plusDays(2));
        rActualizada.setFechaHoraFin(LocalDateTime.now().plusDays(2).plusHours(3));

        when(reservaRepository.findById(id)).thenReturn(Optional.of(rExistente));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(rExistente);

        // When
        Reserva resultado = reservaService.actualizarReserva(id, rActualizada);

        // Then
        assertNotNull(resultado);
        verify(reservaRepository, times(1)).findById(id);
        verify(reservaRepository, times(1)).save(rExistente);
    }

    @Test
    void actualizarReserva_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        Reserva r = new Reserva();
        r.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        r.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.actualizarReserva(id, r);
        });
        verify(reservaRepository, times(1)).findById(id);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void eliminarReserva_CuandoExiste_DeberiaEliminarReserva() {
        // Given
        Long id = 1L;
        Reserva r = new Reserva();
        r.setId(id);
        when(reservaRepository.findById(id)).thenReturn(Optional.of(r));
        doNothing().when(reservaRepository).delete(r);

        // When
        reservaService.eliminarReserva(id);

        // Then
        verify(reservaRepository, times(1)).findById(id);
        verify(reservaRepository, times(1)).delete(r);
    }

    @Test
    void eliminarReserva_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Given
        Long id = 99L;
        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.eliminarReserva(id);
        });
        verify(reservaRepository, times(1)).findById(id);
        verify(reservaRepository, never()).delete(any());
    }

    @Test
    void obtenerReservasPorPersona_DeberiaRetornarReservasDeLaPersona() {
        // Given
        Long personaId = 1L;
        Reserva r1 = new Reserva();
        r1.setId(1L);
        r1.setPersona(persona);
        r1.setSala(sala);

        Reserva r2 = new Reserva();
        r2.setId(2L);
        r2.setPersona(persona);
        r2.setSala(sala);

        when(reservaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        // When
        List<Reserva> resultado = reservaService.obtenerReservasPorPersona(personaId);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> r.getPersona().getId().equals(personaId)));
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void obtenerReservasPorSala_DeberiaRetornarReservasDeLaSala() {
        // Given
        Long salaId = 1L;
        Reserva r1 = new Reserva();
        r1.setId(1L);
        r1.setPersona(persona);
        r1.setSala(sala);

        when(reservaRepository.findAll()).thenReturn(Arrays.asList(r1));

        // When
        List<Reserva> resultado = reservaService.obtenerReservasPorSala(salaId);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(salaId, resultado.get(0).getSala().getId());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void crearReserva_CuandoHayConflictoDeHorario_DeberiaLanzarExcepcion() {
        // Given
        Reserva rExistente = new Reserva();
        rExistente.setId(1L);
        rExistente.setPersona(persona);
        rExistente.setSala(sala);
        rExistente.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        rExistente.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

        Reserva rNueva = new Reserva();
        rNueva.setPersona(persona);
        rNueva.setSala(sala);
        rNueva.setFechaHoraInicio(LocalDateTime.now().plusDays(1).plusHours(1));
        rNueva.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(3));

        when(personaRepository.findById(persona.getId())).thenReturn(Optional.of(persona));
        when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));
        when(reservaRepository.findAll()).thenReturn(Arrays.asList(rExistente));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(rNueva);
        });
        verify(reservaRepository, never()).save(any());
    }
}
