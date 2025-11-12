package com.reservas.controller;

import com.reservas.model.Reserva;
import com.reservas.service.ReservaService;
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
import com.reservas.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Gestión de Reservas", description = "Endpoints para crear, consultar y administrar reservas de salas y artículos")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(summary = "Obtener todas las reservas", description = "Retorna la lista completa de todas las reservas registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas() {
        List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener reserva por ID", description = "Busca y retorna una reserva específica mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(
            @Parameter(description = "ID único de la reserva", required = true, example = "1") @PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener reservas por persona", description = "Retorna todas las reservas realizadas por una persona específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas de la persona obtenidas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorPersona(
            @Parameter(description = "ID de la persona", required = true, example = "1") @PathVariable Long personaId) {
        List<Reserva> reservas = reservaService.obtenerReservasPorPersona(personaId);
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener reservas por sala", description = "Retorna todas las reservas asociadas a una sala específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas de la sala obtenidas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "404", description = "Sala no encontrada")
    })
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorSala(
            @Parameter(description = "ID de la sala", required = true, example = "1") @PathVariable Long salaId) {
        List<Reserva> reservas = reservaService.obtenerReservasPorSala(salaId);
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Crear nueva reserva", description = "Registra una nueva reserva en el sistema. Debe incluir persona, fechas y al menos una sala o artículo. Valida disponibilidad automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o conflicto de horarios"),
            @ApiResponse(responseCode = "409", description = "Sala o artículo no disponible en el horario solicitado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearReserva(
            @Parameter(description = "Datos de la nueva reserva", required = true) @RequestBody Reserva reserva) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar reserva existente", description = "Actualiza los datos de una reserva existente. Valida disponibilidad si se modifican fechas, sala o artículo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto de horarios con la actualización")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(
            @Parameter(description = "ID de la reserva a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la reserva", required = true) @RequestBody Reserva reserva) {
        try {
            Reserva reservaActualizada = reservaService.actualizarReserva(id, reserva);
            return ResponseEntity.ok(reservaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar reserva", description = "Elimina permanentemente una reserva del sistema mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada con el ID proporcionado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(
            @Parameter(description = "ID de la reserva a eliminar", required = true, example = "1") @PathVariable Long id) {
        try {
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Sincronizar datos con servicio de analítica", description = "Envía todas las reservas al microservicio Python para análisis predictivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sincronización exitosa"),
            @ApiResponse(responseCode = "500", description = "Error al sincronizar")
    })
    @PostMapping("/sync-analytics")
    public ResponseEntity<String> sincronizarAnalytics() {
        String resultado = analyticsService.sincronizarDatosConPython();
        return ResponseEntity.ok(resultado);
    }
}