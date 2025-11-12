package com.reservas.controller;

import com.reservas.model.Persona;
import com.reservas.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@Tag(name = "Gestión de Personas", description = "Endpoints para administrar personas del sistema de reservas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Operation(summary = "Obtener todas las personas", description = "Retorna una lista completa de todas las personas registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de personas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Persona>> obtenerTodasLasPersonas() {
        List<Persona> personas = personaService.obtenerTodas();
        return ResponseEntity.ok(personas);
    }

    @Operation(summary = "Obtener persona por ID", description = "Busca y retorna una persona específica mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersonaPorId(
            @Parameter(description = "ID único de la persona", required = true, example = "1") @PathVariable Long id) {
        return personaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar persona por email", description = "Busca y retorna una persona mediante su dirección de correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada con el email proporcionado"),
            @ApiResponse(responseCode = "400", description = "Email inválido o vacío")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Persona> obtenerPersonaPorEmail(
            @Parameter(description = "Email de la persona", required = true, example = "juan.perez@uba.ar") @PathVariable String email) {
        return personaService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar personas por nombre", description = "Busca personas cuyo nombre contenga el texto especificado (búsqueda case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class)))
    })
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<Persona>> buscarPersonasPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true, example = "Juan") @PathVariable String nombre) {
        List<Persona> personas = personaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(personas);
    }

    @Operation(summary = "Crear nueva persona", description = "Registra una nueva persona en el sistema. El email debe ser único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Persona creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearPersona(
            @Parameter(description = "Datos de la nueva persona", required = true) @RequestBody Persona persona) {
        try {
            Persona nuevaPersona = personaService.crear(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar persona existente", description = "Actualiza los datos de una persona existente. Se debe proporcionar el ID y los nuevos datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Persona.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPersona(
            @Parameter(description = "ID de la persona a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la persona", required = true) @RequestBody Persona persona) {
        try {
            Persona personaActualizada = personaService.actualizar(id, persona);
            return ResponseEntity.ok(personaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar persona", description = "Elimina permanentemente una persona del sistema mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Persona eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar - persona tiene reservas activas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPersona(
            @Parameter(description = "ID de la persona a eliminar", required = true, example = "1") @PathVariable Long id) {
        try {
            personaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Verificar si existe un email", description = "Verifica si ya existe una persona registrada con el email especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente")
    })
    @GetMapping("/existe-email/{email}")
    public ResponseEntity<Boolean> existeEmail(
            @Parameter(description = "Email a verificar", required = true, example = "juan.perez@uba.ar") @PathVariable String email) {
        boolean existe = personaService.existeEmail(email);
        return ResponseEntity.ok(existe);
    }
}