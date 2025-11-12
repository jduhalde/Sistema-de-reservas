package com.reservas.controller;

import com.reservas.model.Sala;
import com.reservas.service.SalaService;
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
@RequestMapping("/api/salas")
@Tag(name = "Gestión de Salas", description = "Endpoints para administrar salas y espacios disponibles para reserva")
public class SalaController {

    @Autowired
    private SalaService salaService;

    @Operation(summary = "Obtener todas las salas", description = "Retorna la lista completa de salas registradas en el sistema, incluyendo disponibles y no disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de salas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Sala>> obtenerTodasLasSalas() {
        List<Sala> salas = salaService.obtenerTodas();
        return ResponseEntity.ok(salas);
    }

    @Operation(summary = "Obtener salas disponibles", description = "Retorna únicamente las salas que están actualmente disponibles para ser reservadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de salas disponibles obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<Sala>> obtenerSalasDisponibles() {
        List<Sala> salasDisponibles = salaService.obtenerDisponibles();
        return ResponseEntity.ok(salasDisponibles);
    }

    @Operation(summary = "Obtener sala por ID", description = "Busca y retorna una sala específica mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Sala> obtenerSalaPorId(
            @Parameter(description = "ID único de la sala", required = true, example = "1") @PathVariable Long id) {
        return salaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar salas por nombre", description = "Busca salas cuyo nombre contenga el texto especificado (búsqueda case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class)))
    })
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<Sala>> buscarSalasPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true, example = "Conferencias") @PathVariable String nombre) {
        List<Sala> salas = salaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(salas);
    }

    @Operation(summary = "Buscar salas por capacidad mínima", description = "Filtra y retorna salas que tienen al menos la capacidad especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salas filtradas por capacidad exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "400", description = "Capacidad inválida (debe ser mayor a 0)")
    })
    @GetMapping("/capacidad/{minCapacidad}")
    public ResponseEntity<List<Sala>> obtenerSalasPorCapacidad(
            @Parameter(description = "Capacidad mínima requerida", required = true, example = "30") @PathVariable int minCapacidad) {
        List<Sala> salas = salaService.obtenerPorCapacidadMinima(minCapacidad);
        return ResponseEntity.ok(salas);
    }

    @Operation(summary = "Buscar salas por ubicación", description = "Filtra y retorna salas que están en una ubicación específica (piso, edificio, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salas filtradas por ubicación exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "400", description = "Ubicación inválida o vacía")
    })
    @GetMapping("/ubicacion/{ubicacion}")
    public ResponseEntity<List<Sala>> obtenerSalasPorUbicacion(
            @Parameter(description = "Ubicación de la sala", required = true, example = "Piso 3") @PathVariable String ubicacion) {
        List<Sala> salas = salaService.obtenerPorUbicacion(ubicacion);
        return ResponseEntity.ok(salas);
    }

    @Operation(summary = "Crear nueva sala", description = "Registra una nueva sala en el sistema. Debe incluir nombre, capacidad y ubicación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sala creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearSala(
            @Parameter(description = "Datos de la nueva sala", required = true) @RequestBody Sala sala) {
        try {
            Sala nuevaSala = salaService.crear(sala);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSala);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar sala existente", description = "Actualiza los datos de una sala existente. Se debe proporcionar el ID y los nuevos datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSala(
            @Parameter(description = "ID de la sala a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la sala", required = true) @RequestBody Sala sala) {
        try {
            Sala salaActualizada = salaService.actualizar(id, sala);
            return ResponseEntity.ok(salaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Cambiar disponibilidad de la sala", description = "Actualiza el estado de disponibilidad de una sala (disponible/no disponible)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sala.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada")
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<?> cambiarDisponibilidad(
            @Parameter(description = "ID de la sala", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de disponibilidad", required = true, example = "true") @RequestParam boolean disponible) {
        try {
            Sala sala = salaService.cambiarDisponibilidad(id, disponible);
            return ResponseEntity.ok(sala);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar sala", description = "Elimina permanentemente una sala del sistema mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sala eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar - sala tiene reservas asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSala(
            @Parameter(description = "ID de la sala a eliminar", required = true, example = "1") @PathVariable Long id) {
        try {
            salaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Contar salas disponibles", description = "Retorna la cantidad total de salas actualmente disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/contar-disponibles")
    public ResponseEntity<Long> contarDisponibles() {
        long cantidad = salaService.contarDisponibles();
        return ResponseEntity.ok(cantidad);
    }
}