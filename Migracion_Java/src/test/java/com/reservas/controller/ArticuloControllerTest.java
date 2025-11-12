package com.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservas.model.Articulo;
import com.reservas.service.ArticuloService;
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

@WebMvcTest(ArticuloController.class)
@Disabled //
class ArticuloControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ArticuloService articuloService;

        private Articulo articulo;

        @BeforeEach
        void setUp() {
                articulo = new Articulo();
                articulo.setId(1L);
                articulo.setNombre("Proyector");
                articulo.setDescripcion("Proyector Full HD");
                articulo.setCategoria("Audiovisual");
                articulo.setDisponible(true);
        }

        @Test
        void obtenerTodosLosArticulos_DeberiaRetornarListaDeArticulos() throws Exception {
                // Given
                Articulo a2 = new Articulo();
                a2.setId(2L);
                a2.setNombre("Laptop");
                a2.setCategoria("Informática");

                List<Articulo> articulos = Arrays.asList(articulo, a2);
                when(articuloService.obtenerTodos()).thenReturn(articulos);

                // When & Then
                mockMvc.perform(get("/api/articulos")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Proyector")))
                                .andExpect(jsonPath("$[0].categoria", is("Audiovisual")))
                                .andExpect(jsonPath("$[1].id", is(2)));

                verify(articuloService, times(1)).obtenerTodos();
        }

        @Test
        void obtenerArticulosDisponibles_DeberiaRetornarSoloDisponibles() throws Exception {
                // Given
                List<Articulo> articulosDisponibles = Arrays.asList(articulo);
                when(articuloService.obtenerDisponibles()).thenReturn(articulosDisponibles);

                // When & Then
                mockMvc.perform(get("/api/articulos/disponibles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].disponible", is(true)));

                verify(articuloService, times(1)).obtenerDisponibles();
        }

        @Test
        void obtenerArticuloPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                when(articuloService.obtenerPorId(1L)).thenReturn(Optional.of(articulo));

                // When & Then
                mockMvc.perform(get("/api/articulos/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Proyector")))
                                .andExpect(jsonPath("$.categoria", is("Audiovisual")));

                verify(articuloService, times(1)).obtenerPorId(1L);
        }

        @Test
        void obtenerArticuloPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(articuloService.obtenerPorId(99L)).thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/api/articulos/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(articuloService, times(1)).obtenerPorId(99L);
        }

        @Test
        void buscarArticulosPorNombre_DeberiaRetornarListaCoincidente() throws Exception {
                // Given
                List<Articulo> articulos = Arrays.asList(articulo);
                when(articuloService.buscarPorNombre("Proyector")).thenReturn(articulos);

                // When & Then
                mockMvc.perform(get("/api/articulos/buscar/Proyector")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].nombre", is("Proyector")));

                verify(articuloService, times(1)).buscarPorNombre("Proyector");
        }

        @Test
        void obtenerArticulosPorCategoria_DeberiaRetornarArticulosDeLaCategoria() throws Exception {
                // Given
                List<Articulo> articulos = Arrays.asList(articulo);
                when(articuloService.obtenerPorCategoria("Audiovisual")).thenReturn(articulos);

                // When & Then
                mockMvc.perform(get("/api/articulos/categoria/Audiovisual")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].categoria", is("Audiovisual")));

                verify(articuloService, times(1)).obtenerPorCategoria("Audiovisual");
        }

        @Test
        void crearArticulo_CuandoEsValido_DeberiaRetornar201() throws Exception {
                // Given
                Articulo nuevoArticulo = new Articulo();
                nuevoArticulo.setNombre("Micrófono");
                nuevoArticulo.setCategoria("Audio");

                Articulo articuloCreado = new Articulo();
                articuloCreado.setId(3L);
                articuloCreado.setNombre("Micrófono");
                articuloCreado.setCategoria("Audio");

                when(articuloService.crear(any(Articulo.class))).thenReturn(articuloCreado);

                // When & Then
                mockMvc.perform(post("/api/articulos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoArticulo)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(3)))
                                .andExpect(jsonPath("$.nombre", is("Micrófono")))
                                .andExpect(jsonPath("$.categoria", is("Audio")));

                verify(articuloService, times(1)).crear(any(Articulo.class));
        }

        @Test
        void crearArticulo_CuandoDatosInvalidos_DeberiaRetornar400() throws Exception {
                // Given
                Articulo nuevoArticulo = new Articulo();
                nuevoArticulo.setNombre("");

                when(articuloService.crear(any(Articulo.class)))
                                .thenThrow(new IllegalArgumentException("Datos inválidos"));

                // When & Then
                mockMvc.perform(post("/api/articulos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoArticulo)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Datos inválidos"));

                verify(articuloService, times(1)).crear(any(Articulo.class));
        }

        @Test
        void actualizarArticulo_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Articulo articuloActualizado = new Articulo();
                articuloActualizado.setNombre("Proyector 4K");
                articuloActualizado.setCategoria("Audiovisual");

                Articulo articuloResultado = new Articulo();
                articuloResultado.setId(1L);
                articuloResultado.setNombre("Proyector 4K");
                articuloResultado.setCategoria("Audiovisual");

                when(articuloService.actualizar(eq(1L), any(Articulo.class))).thenReturn(articuloResultado);

                // When & Then
                mockMvc.perform(put("/api/articulos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(articuloActualizado)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Proyector 4K")));

                verify(articuloService, times(1)).actualizar(eq(1L), any(Articulo.class));
        }

        @Test
        void actualizarArticulo_CuandoNoExiste_DeberiaRetornar400() throws Exception {
                // Given
                Articulo articuloActualizado = new Articulo();
                articuloActualizado.setNombre("Articulo X");

                when(articuloService.actualizar(eq(99L), any(Articulo.class)))
                                .thenThrow(new IllegalArgumentException("Artículo no encontrado"));

                // When & Then
                mockMvc.perform(put("/api/articulos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(articuloActualizado)))
                                .andExpect(status().isBadRequest());

                verify(articuloService, times(1)).actualizar(eq(99L), any(Articulo.class));
        }

        @Test
        void cambiarDisponibilidad_CuandoExiste_DeberiaRetornar200() throws Exception {
                // Given
                Articulo articuloModificado = new Articulo();
                articuloModificado.setId(1L);
                articuloModificado.setNombre("Proyector");
                articuloModificado.setDisponible(false);

                when(articuloService.cambiarDisponibilidad(1L, false)).thenReturn(articuloModificado);

                // When & Then
                mockMvc.perform(patch("/api/articulos/1/disponibilidad")
                                .param("disponible", "false")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.disponible", is(false)));

                verify(articuloService, times(1)).cambiarDisponibilidad(1L, false);
        }

        @Test
        void cambiarDisponibilidad_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                when(articuloService.cambiarDisponibilidad(99L, true))
                                .thenThrow(new IllegalArgumentException("Artículo no encontrado"));

                // When & Then
                mockMvc.perform(patch("/api/articulos/99/disponibilidad")
                                .param("disponible", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(articuloService, times(1)).cambiarDisponibilidad(99L, true);
        }

        @Test
        void eliminarArticulo_CuandoExiste_DeberiaRetornar204() throws Exception {
                // Given
                doNothing().when(articuloService).eliminar(1L);

                // When & Then
                mockMvc.perform(delete("/api/articulos/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(articuloService, times(1)).eliminar(1L);
        }

        @Test
        void eliminarArticulo_CuandoNoExiste_DeberiaRetornar404() throws Exception {
                // Given
                doThrow(new IllegalArgumentException("Artículo no encontrado"))
                                .when(articuloService).eliminar(99L);

                // When & Then
                mockMvc.perform(delete("/api/articulos/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(articuloService, times(1)).eliminar(99L);
        }

        @Test
        void contarDisponibles_DeberiaRetornarCantidad() throws Exception {
                // Given
                when(articuloService.contarDisponibles()).thenReturn(8L);

                // When & Then
                mockMvc.perform(get("/api/articulos/contar-disponibles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("8"));

                verify(articuloService, times(1)).contarDisponibles();
        }
}