package com.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.model.Persona;
import com.reservas.service.PersonaService;
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

@WebMvcTest(PersonaController.class)
@Disabled //
class PersonaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private PersonaService personaService;

        private Persona persona;

        @BeforeEach
        void setUp() {
                persona = new Persona();
                persona.setId(1L);
                persona.setNombre("Juan");
                persona.setEmail("juan@test.com");
                persona.setTelefono("1234567890");
        }

        @Test
        void obtenerTodasLasPersonas_DeberiaRetornarListaDePersonas() throws Exception {
                // Given
                Persona p2 = new Persona();
                p2.setId(2L);
                p2.setNombre("María");
                p2.setEmail("maria@test.com");

                List<Persona> personas = Arrays.asList(persona, p2);
                when(personaService.obtenerTodas()).thenReturn(personas);

                // When & Then
                mockMvc.perform(get("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Juan")))
                                .andExpect(jsonPath("$[0].email", is("juan@test.com")))
                                .andExpect(jsonPath("$[1].id", is(2)))
                                .andExpect(jsonPath("$[1].nombre", is("María")));

                verify(personaService, times(1)).obtenerTodas();
        }

        @Test
        void obtenerPersonaPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                when(personaService.obtenerPorId(1L)).thenReturn(Optional.of(persona));

                // When & Then
                mockMvc.perform(get("/api/personas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Juan")))
                                .andExpect(jsonPath("$.email", is("juan@test.com")));

                verify(personaService, times(1)).obtenerPorId(1L);
        }

        @Test
        void obtenerPersonaPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(personaService.obtenerPorId(99L)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/personas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(personaService, times(1)).obtenerPorId(99L);
        }

        @Test
        void obtenerPersonaPorEmail_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                when(personaService.buscarPorEmail("juan@test.com")).thenReturn(Optional.of(persona));

                // When & Then
                mockMvc.perform(get("/api/personas/email/juan@test.com")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.email", is("juan@test.com")));

                verify(personaService, times(1)).buscarPorEmail("juan@test.com");
        }

        @Test
        void obtenerPersonaPorEmail_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(personaService.buscarPorEmail("noexiste@test.com")).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/personas/email/noexiste@test.com")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(personaService, times(1)).buscarPorEmail("noexiste@test.com");
        }

        @Test
        void buscarPersonasPorNombre_DeberiaRetornarListaCoincidente() throws Exception {
                // Given
                List<Persona> personas = Arrays.asList(persona);
                when(personaService.buscarPorNombre("Juan")).thenReturn(personas);

                // When & Then
                mockMvc.perform(get("/api/personas/buscar/Juan")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Juan")));

                verify(personaService, times(1)).buscarPorNombre("Juan");
        }

        @Test
        void crearPersona_CuandoEsValida_DeberiaRetornar201() throws Exception {
                // Given
                Persona nuevaPersona = new Persona();
                nuevaPersona.setNombre("Pedro");
                nuevaPersona.setEmail("pedro@test.com");

                Persona personaCreada = new Persona();
                personaCreada.setId(3L);
                personaCreada.setNombre("Pedro");
                personaCreada.setEmail("pedro@test.com");

                when(personaService.crear(any(Persona.class))).thenReturn(personaCreada);

                // When & Then
                mockMvc.perform(post("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaPersona)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(3)))
                                .andExpect(jsonPath("$.nombre", is("Pedro")))
                                .andExpect(jsonPath("$.email", is("pedro@test.com")));

                verify(personaService, times(1)).crear(any(Persona.class));
        }

        @Test
        void crearPersona_CuandoEmailDuplicado_DeberiaRetornar400() throws Exception {
                // Given
                Persona nuevaPersona = new Persona();
                nuevaPersona.setNombre("Pedro");
                nuevaPersona.setEmail("juan@test.com");

                when(personaService.crear(any(Persona.class)))
                                .thenThrow(new IllegalArgumentException("El email ya existe"));

                // When & Then
                mockMvc.perform(post("/api/personas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevaPersona)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("El email ya existe"));

                verify(personaService, times(1)).crear(any(Persona.class));
        }

        @Test
        void actualizarPersona_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Persona personaActualizada = new Persona();
                personaActualizada.setNombre("Juan Carlos");
                personaActualizada.setEmail("juan.carlos@test.com");

                Persona personaResultado = new Persona();
                personaResultado.setId(1L);
                personaResultado.setNombre("Juan Carlos");
                personaResultado.setEmail("juan.carlos@test.com");

                when(personaService.actualizar(eq(1L), any(Persona.class))).thenReturn(personaResultado);

                // When & Then
                mockMvc.perform(put("/api/personas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personaActualizada)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Juan Carlos")))
                                .andExpect(jsonPath("$.email", is("juan.carlos@test.com")));

                verify(personaService, times(1)).actualizar(eq(1L), any(Persona.class));
        }

        @Test
        void actualizarPersona_CuandoNoExiste_DeberiaRetornar400() throws Exception {
                // Given
                Persona personaActualizada = new Persona();
                personaActualizada.setNombre("Pedro");

                when(personaService.actualizar(eq(99L), any(Persona.class)))
                                .thenThrow(new IllegalArgumentException("Persona no encontrada"));

                // When & Then
                mockMvc.perform(put("/api/personas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personaActualizada)))
                                .andExpect(status().isBadRequest());

                verify(personaService, times(1)).actualizar(eq(99L), any(Persona.class));
        }

        @Test
        void eliminarPersona_CuandoExiste_DeberiaRetornar204() throws Exception {
                // Given
                doNothing().when(personaService).eliminar(1L);

                // When & Then
                mockMvc.perform(delete("/api/personas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(personaService, times(1)).eliminar(1L);
        }

        @Test
        void eliminarPersona_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                doThrow(new IllegalArgumentException("Persona no encontrada"))
                                .when(personaService).eliminar(99L);

                // When & Then
                mockMvc.perform(delete("/api/personas/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(personaService, times(1)).eliminar(99L);
        }

        @Test
        void existeEmail_CuandoExiste_DeberiaRetornarTrue() throws Exception {
                // Given
                when(personaService.existeEmail("juan@test.com")).thenReturn(true);

                // When & Then
                mockMvc.perform(get("/api/personas/existe-email/juan@test.com")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("true"));

                verify(personaService, times(1)).existeEmail("juan@test.com");
        }

        @Test
        void existeEmail_CuandoNoExiste_DeberiaRetornarFalse() throws Exception {
                // Given
                when(personaService.existeEmail("noexiste@test.com")).thenReturn(false);

                // When & Then
                mockMvc.perform(get("/api/personas/existe-email/noexiste@test.com")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("false"));

                verify(personaService, times(1)).existeEmail("noexiste@test.com");
        }
}