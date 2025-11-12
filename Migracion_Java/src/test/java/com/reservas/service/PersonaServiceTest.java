package com.reservas.service;

import com.reservas.model.Persona;
import com.reservas.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio PersonaService")
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaService personaService;

    private Persona personaEjemplo;

    @BeforeEach
    void setUp() {
        personaEjemplo = new Persona();
        personaEjemplo.setId(1L);
        personaEjemplo.setNombre("Juan Perez");
        personaEjemplo.setEmail("juan.perez@example.com");
        personaEjemplo.setTelefono("555-1234");
    }

    @Test
    @DisplayName("Debe listar todas las personas correctamente")
    void testObtenerTodasLasPersonas() {
        Persona persona2 = new Persona();
        persona2.setId(2L);
        persona2.setNombre("Maria Gomez");
        persona2.setEmail("maria.gomez@example.com");

        List<Persona> personasEsperadas = Arrays.asList(personaEjemplo, persona2);
        when(personaRepository.findAll()).thenReturn(personasEsperadas);

        List<Persona> personasObtenidas = personaService.obtenerTodas();

        assertNotNull(personasObtenidas);
        assertEquals(2, personasObtenidas.size());
        assertEquals("Juan Perez", personasObtenidas.get(0).getNombre());
        assertEquals("Maria Gomez", personasObtenidas.get(1).getNombre());

        verify(personaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una persona por ID cuando existe")
    void testObtenerPorId_CuandoExiste() {
        when(personaRepository.findById(1L)).thenReturn(Optional.of(personaEjemplo));

        Optional<Persona> resultado = personaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombre());
        assertEquals("juan.perez@example.com", resultado.get().getEmail());

        verify(personaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar Optional vacio cuando la persona no existe")
    void testObtenerPorId_CuandoNoExiste() {
        when(personaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Persona> resultado = personaService.obtenerPorId(999L);

        assertFalse(resultado.isPresent());
        verify(personaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear una nueva persona correctamente")
    void testCrearPersona() {
        Persona personaNueva = new Persona();
        personaNueva.setNombre("Carlos Rodriguez");
        personaNueva.setEmail("carlos.rod@example.com");
        personaNueva.setTelefono("555-5678");

        Persona personaGuardada = new Persona();
        personaGuardada.setId(3L);
        personaGuardada.setNombre("Carlos Rodriguez");
        personaGuardada.setEmail("carlos.rod@example.com");
        personaGuardada.setTelefono("555-5678");

        when(personaRepository.findByEmail("carlos.rod@example.com")).thenReturn(Optional.empty());
        when(personaRepository.save(any(Persona.class))).thenReturn(personaGuardada);

        Persona resultado = personaService.crear(personaNueva);

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Carlos Rodriguez", resultado.getNombre());
        assertEquals("carlos.rod@example.com", resultado.getEmail());

        verify(personaRepository, times(1)).findByEmail("carlos.rod@example.com");
        verify(personaRepository, times(1)).save(any(Persona.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al crear persona con email duplicado")
    void testCrearPersona_EmailDuplicado() {
        Persona personaNueva = new Persona();
        personaNueva.setNombre("Otro Juan");
        personaNueva.setEmail("juan.perez@example.com");

        when(personaRepository.findByEmail("juan.perez@example.com"))
                .thenReturn(Optional.of(personaEjemplo));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            personaService.crear(personaNueva);
        });

        assertTrue(exception.getMessage().contains("Ya existe una persona con el email"));
        verify(personaRepository, times(1)).findByEmail("juan.perez@example.com");
        verify(personaRepository, never()).save(any(Persona.class));
    }

    @Test
    @DisplayName("Debe actualizar una persona existente")
    void testActualizarPersona() {
        Persona personaActualizada = new Persona();
        personaActualizada.setNombre("Juan Carlos Perez");
        personaActualizada.setEmail("juan.perez@example.com");
        personaActualizada.setTelefono("555-9999");

        when(personaRepository.findById(1L)).thenReturn(Optional.of(personaEjemplo));
        when(personaRepository.save(any(Persona.class))).thenReturn(personaActualizada);

        Persona resultado = personaService.actualizar(1L, personaActualizada);

        assertNotNull(resultado);
        assertEquals("Juan Carlos Perez", resultado.getNombre());
        assertEquals("555-9999", resultado.getTelefono());

        verify(personaRepository, times(1)).findById(1L);
        verify(personaRepository, times(1)).save(any(Persona.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar persona inexistente")
    void testActualizarPersona_NoExiste() {
        Persona personaActualizada = new Persona();
        personaActualizada.setNombre("Test");
        personaActualizada.setEmail("test@example.com");

        when(personaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            personaService.actualizar(999L, personaActualizada);
        });

        assertTrue(exception.getMessage().contains("Persona no encontrada"));
        verify(personaRepository, times(1)).findById(999L);
        verify(personaRepository, never()).save(any(Persona.class));
    }

    @Test
    @DisplayName("Debe eliminar una persona existente")
    void testEliminarPersona() {
        when(personaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(personaRepository).deleteById(1L);

        personaService.eliminar(1L);

        verify(personaRepository, times(1)).existsById(1L);
        verify(personaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al eliminar persona inexistente")
    void testEliminarPersona_NoExiste() {
        when(personaRepository.existsById(999L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            personaService.eliminar(999L);
        });

        assertTrue(exception.getMessage().contains("Persona no encontrada"));
        verify(personaRepository, times(1)).existsById(999L);
        verify(personaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe buscar persona por email")
    void testBuscarPorEmail() {
        when(personaRepository.findByEmail("juan.perez@example.com"))
                .thenReturn(Optional.of(personaEjemplo));

        Optional<Persona> resultado = personaService.buscarPorEmail("juan.perez@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombre());
        verify(personaRepository, times(1)).findByEmail("juan.perez@example.com");
    }

    @Test
    @DisplayName("Debe buscar personas por nombre")
    void testBuscarPorNombre() {
        List<Persona> personasEsperadas = Arrays.asList(personaEjemplo);
        when(personaRepository.findByNombreContainingIgnoreCase("Juan"))
                .thenReturn(personasEsperadas);

        List<Persona> resultado = personaService.buscarPorNombre("Juan");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombre());
        verify(personaRepository, times(1)).findByNombreContainingIgnoreCase("Juan");
    }

    @Test
    @DisplayName("Debe verificar si existe email")
    void testExisteEmail() {
        when(personaRepository.findByEmail("juan.perez@example.com"))
                .thenReturn(Optional.of(personaEjemplo));

        boolean existe = personaService.existeEmail("juan.perez@example.com");

        assertTrue(existe);
        verify(personaRepository, times(1)).findByEmail("juan.perez@example.com");
    }

    @Test
    @DisplayName("Debe retornar false cuando email no existe")
    void testExisteEmail_NoExiste() {
        when(personaRepository.findByEmail("noexiste@example.com"))
                .thenReturn(Optional.empty());

        boolean existe = personaService.existeEmail("noexiste@example.com");

        assertFalse(existe);
        verify(personaRepository, times(1)).findByEmail("noexiste@example.com");
    }
}