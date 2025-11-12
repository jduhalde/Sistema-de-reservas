package com.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.model.Sala;
import com.reservas.service.SalaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(SalaController.class)
@Disabled
class SalaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private SalaService salaService;

        private Sala sala;

        @BeforeEach
        void setUp() {
                sala = new Sala();
                sala.setId(1L);
                sala.setNombre("Sala A");
                sala.setCapacidad(30);
                sala.setUbicacion("Piso 1");
                sala.setDisponible(true);
        }

        @Test
        void obtenerTodasLasSalas_DeberiaRetornarListaDeSalas() throws Exception {
                // Given
                Sala s2 = new Sala();
                s2.setId(2L);
                s2.setNombre("Sala B");
                s2.setCapacidad(20);

                List<Sala> salas = Arrays.asList(sala, s2);
                when(salaService.obtenerTodas()).thenReturn(salas);

                // When & Then
                mockMvc.perform(get("/api/salas")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Sala A")))
                                .andExpect(jsonPath("$[0].capacidad", is(30)))
                                .andExpect(jsonPath("$[1].id", is(2)));

                verify(salaService, times(1)).obtenerTodas();
        }

        @Test
        void obtenerSalasDisponibles_DeberiaRetornarSoloDisponibles() throws Exception {
                // Given
                List<Sala> salasDisponibles = Arrays.asList(sala);
                when(salaService.obtenerDisponibles()).thenReturn(salasDisponibles);

                // When & Then
                mockMvc.perform(get("/api/salas/disponibles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].disponible", is(true)));

                verify(salaService, times(1)).obtenerDisponibles();
        }

        @Test
        void obtenerSalaPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                when(salaService.obtenerPorId(1L)).thenReturn(Optional.of(sala));

                // When & Then
                mockMvc.perform(get("/api/salas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Sala A")))
                                .andExpect(jsonPath("$.capacidad", is(30)))
                                .andExpect(jsonPath("$.ubicacion", is("Piso 1")));

                verify(salaService, times(1)).obtenerPorId(1L);
        }

        @Test
        void obtenerSalaPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(salaService.obtenerPorId(99L)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/salas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(salaService, times(1)).obtenerPorId(99L);
        }

        @Test
        void buscarSalasPorNombre_DeberiaRetornarListaCoincidente() throws Exception {
                // Given
                List<Sala> salas = Arrays.asList(sala);
                when(salaService.buscarPorNombre("Sala A")).thenReturn(salas);

                // When & Then
                mockMvc.perform(get("/api/salas/buscar/Sala A")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Sala A")));

                verify(salaService, times(1)).buscarPorNombre("Sala A");
        }

        @Test
        void obtenerSalasPorCapacidad_DeberiaRetornarSalasConCapacidadMinima() throws Exception {
                // Given
                List<Sala> salas = Arrays.asList(sala);
                when(salaService.obtenerPorCapacidadMinima(25)).thenReturn(salas);

                // When & Then
                mockMvc.perform(get("/api/salas/capacidad/25")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].capacidad", is(30)));

                verify(salaService, times(1)).obtenerPorCapacidadMinima(25);
        }

        @Test
        void obtenerSalasPorUbicacion_DeberiaRetornarSalasDeLaUbicacion() throws Exception {
                // Given
                List<Sala> salas = Arrays.asList(sala);
                when(salaService.obtenerPorUbicacion("Piso 1")).thenReturn(salas);

                // When & Then
                mockMvc.perform(get("/api/salas/ubicacion/Piso 1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].ubicacion", is("Piso 1")));

                verify(salaService, times(1)).obtenerPorUbicacion("Piso 1");
        }

        @Test
        void crearSala_CuandoEsValida_DeberiaRetornar201() throws Exception {
                // Given
                Sala nuevaSala = new Sala();
                nuevaSala.setNombre("Sala C");
                nuevaSala.setCapacidad(40);
                nuevaSala.setUbicacion("Piso 2");

                Sala salaCreada = new Sala();
                salaCreada.setId(3L);
                salaCreada.setNombre("Sala C");
                salaCreada.setCapacidad(40);
                salaCreada.setUbicacion("Piso 2");

                when(salaService.crear(any(Sala.class))).thenReturn(salaCreada);

                // When & Then
                mockMvc.perform(post("/api/salas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaSala)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(3)))
                                .andExpect(jsonPath("$.nombre", is("Sala C")))
                                .andExpect(jsonPath("$.capacidad", is(40)));

                verify(salaService, times(1)).crear(any(Sala.class));
        }

        @Test
        void crearSala_CuandoDatosInvalidos_DeberiaRetornar400() throws Exception {
                // Given
                Sala nuevaSala = new Sala();
                nuevaSala.setNombre("");
                nuevaSala.setCapacidad(0);

                when(salaService.crear(any(Sala.class)))
                                .thenThrow(new IllegalArgumentException("Datos inválidos"));

                // When & Then
                mockMvc.perform(post("/api/salas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaSala)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Datos inválidos"));

                verify(salaService, times(1)).crear(any(Sala.class));
        }

        @Test
        void actualizarSala_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Sala salaActualizada = new Sala();
                salaActualizada.setNombre("Sala A Modificada");
                salaActualizada.setCapacidad(35);
                salaActualizada.setUbicacion("Piso 1");

                Sala salaResultado = new Sala();
                salaResultado.setId(1L);
                salaResultado.setNombre("Sala A Modificada");
                salaResultado.setCapacidad(35);

                when(salaService.actualizar(eq(1L), any(Sala.class))).thenReturn(salaResultado);

                // When & Then
                mockMvc.perform(put("/api/salas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(salaActualizada)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Sala A Modificada")))
                                .andExpect(jsonPath("$.capacidad", is(35)));

                verify(salaService, times(1)).actualizar(eq(1L), any(Sala.class));
        }

        @Test
        void actualizarSala_CuandoNoExiste_DeberiaRetornar400() throws Exception {
                // Given
                Sala salaActualizada = new Sala();
                salaActualizada.setNombre("Sala X");

                when(salaService.actualizar(eq(99L), any(Sala.class)))
                                .thenThrow(new IllegalArgumentException("Sala no encontrada"));

                // When & Then
                mockMvc.perform(put("/api/salas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(salaActualizada)))
                                .andExpect(status().isBadRequest());

                verify(salaService, times(1)).actualizar(eq(99L), any(Sala.class));
        }

        @Test
        void cambiarDisponibilidad_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Sala salaModificada = new Sala();
                salaModificada.setId(1L);
                salaModificada.setNombre("Sala A");
                salaModificada.setDisponible(false);

                when(salaService.cambiarDisponibilidad(1L, false)).thenReturn(salaModificada);

                // When & Then
                mockMvc.perform(patch("/api/salas/1/disponibilidad")
                                .param("disponible", "false")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.disponible", is(false)));

                verify(salaService, times(1)).cambiarDisponibilidad(1L, false);
        }

        @Test
        void cambiarDisponibilidad_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(salaService.cambiarDisponibilidad(99L, true))
                                .thenThrow(new IllegalArgumentException("Sala no encontrada"));

                // When & Then
                mockMvc.perform(patch("/api/salas/99/disponibilidad")
                                .param("disponible", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(salaService, times(1)).cambiarDisponibilidad(99L, true);
        }

        @Test
        void eliminarSala_CuandoExiste_DeberiaRetornar204() throws Exception {
                // Given
                doNothing().when(salaService).eliminar(1L);

                // When & Then
                mockMvc.perform(delete("/api/salas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(salaService, times(1)).eliminar(1L);
        }

        @Test
        void eliminarSala_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                doThrow(new IllegalArgumentException("Sala no encontrada"))
                                .when(salaService).eliminar(99L);

                // When & Then
                mockMvc.perform(delete("/api/salas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(salaService, times(1)).eliminar(99L);
        }

        @Test
        void contarDisponibles_DeberiaRetornarCantidad() throws Exception {
                // Given
                when(salaService.contarDisponibles()).thenReturn(5L);

                // When & Then
                mockMvc.perform(get("/api/salas/contar-disponibles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("5"));

                verify(salaService, times(1)).contarDisponibles();
        }
}