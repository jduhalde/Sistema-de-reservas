package com.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.model.Articulo;
import com.reservas.model.Persona;
import com.reservas.model.Reserva;
import com.reservas.model.Sala;
import com.reservas.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
@Disabled //
class ReservaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ReservaService reservaService;

        private Reserva reserva;
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
                sala.setCapacidad(30);

                articulo = new Articulo();
                articulo.setId(1L);
                articulo.setNombre("Proyector");

                reserva = new Reserva();
                reserva.setId(1L);
                reserva.setPersona(persona);
                reserva.setSala(sala);
                reserva.setArticulo(articulo);
                reserva.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
                reserva.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));
        }

        @Test
        void obtenerTodasLasReservas_DeberiaRetornarListaDeReservas() throws Exception {
                // Given
                Reserva r2 = new Reserva();
                r2.setId(2L);
                r2.setPersona(persona);
                r2.setSala(sala);

                List<Reserva> reservas = Arrays.asList(reserva, r2);
                when(reservaService.obtenerTodasLasReservas()).thenReturn(reservas);

                // When & Then
                mockMvc.perform(get("/api/reservas")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[1].id", is(2)));

                verify(reservaService, times(1)).obtenerTodasLasReservas();
        }

        @Test
        void obtenerReservaPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                when(reservaService.obtenerReservaPorId(1L)).thenReturn(Optional.of(reserva));

                // When & Then
                mockMvc.perform(get("/api/reservas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.persona.id", is(1)))
                                .andExpect(jsonPath("$.sala.id", is(1)));

                verify(reservaService, times(1)).obtenerReservaPorId(1L);
        }

        @Test
        void obtenerReservaPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(reservaService.obtenerReservaPorId(99L)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/reservas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(reservaService, times(1)).obtenerReservaPorId(99L);
        }

        @Test
        void obtenerReservasPorPersona_DeberiaRetornarReservasDeLaPersona() throws Exception {
                // Given
                List<Reserva> reservas = Arrays.asList(reserva);
                when(reservaService.obtenerReservasPorPersona(1L)).thenReturn(reservas);

                // When & Then
                mockMvc.perform(get("/api/reservas/persona/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].persona.id", is(1)));

                verify(reservaService, times(1)).obtenerReservasPorPersona(1L);
        }

        @Test
        void obtenerReservasPorSala_DeberiaRetornarReservasDeLaSala() throws Exception {
                // Given
                List<Reserva> reservas = Arrays.asList(reserva);
                when(reservaService.obtenerReservasPorSala(1L)).thenReturn(reservas);

                // When & Then
                mockMvc.perform(get("/api/reservas/sala/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].sala.id", is(1)));

                verify(reservaService, times(1)).obtenerReservasPorSala(1L);
        }

        @Test
        void crearReserva_CuandoEsValida_DeberiaRetornar201() throws Exception {
                // Given
                Reserva nuevaReserva = new Reserva();
                nuevaReserva.setPersona(persona);
                nuevaReserva.setSala(sala);
                nuevaReserva.setFechaHoraInicio(LocalDateTime.now().plusDays(2));
                nuevaReserva.setFechaHoraFin(LocalDateTime.now().plusDays(2).plusHours(2));

                Reserva reservaCreada = new Reserva();
                reservaCreada.setId(3L);
                reservaCreada.setPersona(persona);
                reservaCreada.setSala(sala);
                reservaCreada.setFechaHoraInicio(nuevaReserva.getFechaHoraInicio());
                reservaCreada.setFechaHoraFin(nuevaReserva.getFechaHoraFin());

                when(reservaService.crearReserva(any(Reserva.class))).thenReturn(reservaCreada);

                // When & Then
                mockMvc.perform(post("/api/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaReserva)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(3)))
                                .andExpect(jsonPath("$.persona.id", is(1)))
                                .andExpect(jsonPath("$.sala.id", is(1)));

                verify(reservaService, times(1)).crearReserva(any(Reserva.class));
        }

        @Test
        void crearReserva_CuandoHayConflicto_DeberiaRetornar400() throws Exception {
                // Given
                Reserva nuevaReserva = new Reserva();
                nuevaReserva.setPersona(persona);
                nuevaReserva.setSala(sala);
                nuevaReserva.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
                nuevaReserva.setFechaHoraFin(LocalDateTime.now().plusDays(1).plusHours(2));

                when(reservaService.crearReserva(any(Reserva.class)))
                                .thenThrow(new RuntimeException("La sala ya esta reservada en ese horario"));

                // When & Then
                mockMvc.perform(post("/api/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaReserva)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("La sala ya esta reservada en ese horario"));

                verify(reservaService, times(1)).crearReserva(any(Reserva.class));
        }

        @Test
        void crearReserva_CuandoPersonaNoExiste_DeberiaRetornar400() throws Exception {
                // Given
                Reserva nuevaReserva = new Reserva();
                Persona personaInvalida = new Persona();
                personaInvalida.setId(999L);
                nuevaReserva.setPersona(personaInvalida);

                when(reservaService.crearReserva(any(Reserva.class)))
                                .thenThrow(new RuntimeException("Persona no encontrada con ID: 999"));

                // When & Then
                mockMvc.perform(post("/api/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaReserva)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Persona no encontrada con ID: 999"));

                verify(reservaService, times(1)).crearReserva(any(Reserva.class));
        }

        @Test
        void actualizarReserva_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Reserva reservaActualizada = new Reserva();
                reservaActualizada.setFechaHoraInicio(LocalDateTime.now().plusDays(3));
                reservaActualizada.setFechaHoraFin(LocalDateTime.now().plusDays(3).plusHours(3));

                Reserva reservaResultado = new Reserva();
                reservaResultado.setId(1L);
                reservaResultado.setPersona(persona);
                reservaResultado.setSala(sala);
                reservaResultado.setFechaHoraInicio(reservaActualizada.getFechaHoraInicio());
                reservaResultado.setFechaHoraFin(reservaActualizada.getFechaHoraFin());

                when(reservaService.actualizarReserva(eq(1L), any(Reserva.class))).thenReturn(reservaResultado);

                // When & Then
                mockMvc.perform(put("/api/reservas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reservaActualizada)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)));

                verify(reservaService, times(1)).actualizarReserva(eq(1L), any(Reserva.class));
        }

        @Test
        void actualizarReserva_CuandoNoExiste_DeberiaRetornar400() throws Exception {
                // Given
                Reserva reservaActualizada = new Reserva();
                reservaActualizada.setFechaHoraInicio(LocalDateTime.now().plusDays(2));
                reservaActualizada.setFechaHoraFin(LocalDateTime.now().plusDays(2).plusHours(2));

                when(reservaService.actualizarReserva(eq(99L), any(Reserva.class)))
                                .thenThrow(new RuntimeException("Reserva no encontrada con ID: 99"));

                // When & Then
                mockMvc.perform(put("/api/reservas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reservaActualizada)))
                                .andExpect(status().isBadRequest());

                verify(reservaService, times(1)).actualizarReserva(eq(99L), any(Reserva.class));
        }

        @Test
        void eliminarReserva_CuandoExiste_DeberiaRetornar204() throws Exception {
                // Given
                doNothing().when(reservaService).eliminarReserva(1L);

                // When & Then
                mockMvc.perform(delete("/api/reservas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(reservaService, times(1)).eliminarReserva(1L);
        }

        @Test
        void eliminarReserva_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                doThrow(new RuntimeException("Reserva no encontrada con ID: 99"))
                                .when(reservaService).eliminarReserva(99L);

                // When & Then
                mockMvc.perform(delete("/api/reservas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(reservaService, times(1)).eliminarReserva(99L);
        }
}