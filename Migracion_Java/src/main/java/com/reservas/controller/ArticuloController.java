package com.reservas.controller;

import com.reservas.model.Articulo;
import com.reservas.service.ArticuloService;
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
@RequestMapping("/api/articulos")
@Tag(name = "Gestión de Artículos", description = "Endpoints para administrar artículos disponibles para reserva (proyectores, notebooks, etc.)")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @Operation(summary = "Obtener todos los artículos", description = "Retorna la lista completa de artículos registrados en el sistema, incluyendo disponibles y no disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de artículos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Articulo>> obtenerTodosLosArticulos() {
        List<Articulo> articulos = articuloService.obtenerTodos();
        return ResponseEntity.ok(articulos);
    }

    @Operation(summary = "Obtener artículos disponibles", description = "Retorna únicamente los artículos que están actualmente disponibles para ser reservados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de artículos disponibles obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<Articulo>> obtenerArticulosDisponibles() {
        List<Articulo> articulosDisponibles = articuloService.obtenerDisponibles();
        return ResponseEntity.ok(articulosDisponibles);
    }

    @Operation(summary = "Obtener artículo por ID", description = "Busca y retorna un artículo específico mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "404", description = "Artículo no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Articulo> obtenerArticuloPorId(
            @Parameter(description = "ID único del artículo", required = true, example = "1") @PathVariable Long id) {
        return articuloService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar artículos por nombre", description = "Busca artículos cuyo nombre contenga el texto especificado (búsqueda case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class)))
    })
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<Articulo>> buscarArticulosPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true, example = "Proyector") @PathVariable String nombre) {
        List<Articulo> articulos = articuloService.buscarPorNombre(nombre);
        return ResponseEntity.ok(articulos);
    }

    @Operation(summary = "Buscar artículos por categoría", description = "Filtra y retorna artículos que pertenecen a una categoría específica (ej: Audiovisual, Informática, Audio)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículos filtrados por categoría exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "400", description = "Categoría inválida o vacía")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Articulo>> obtenerArticulosPorCategoria(
            @Parameter(description = "Categoría del artículo", required = true, example = "Audiovisual") @PathVariable String categoria) {
        List<Articulo> articulos = articuloService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(articulos);
    }

    @Operation(summary = "Crear nuevo artículo", description = "Registra un nuevo artículo en el sistema. Debe incluir nombre, categoría y estado de disponibilidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artículo creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearArticulo(
            @Parameter(description = "Datos del nuevo artículo", required = true) @RequestBody Articulo articulo) {
        try {
            Articulo nuevoArticulo = articuloService.crear(articulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoArticulo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar artículo existente", description = "Actualiza los datos de un artículo existente. Se debe proporcionar el ID y los nuevos datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artículo actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "404", description = "Artículo no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarArticulo(
            @Parameter(description = "ID del artículo a actualizar", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevos datos del artículo", required = true) @RequestBody Articulo articulo) {
        try {
            Articulo articuloActualizado = articuloService.actualizar(id, articulo);
            return ResponseEntity.ok(articuloActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Cambiar disponibilidad del artículo", description = "Actualiza el estado de disponibilidad de un artículo (disponible/no disponible)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articulo.class))),
            @ApiResponse(responseCode = "404", description = "Artículo no encontrado")
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<?> cambiarDisponibilidad(
            @Parameter(description = "ID del artículo", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de disponibilidad", required = true, example = "true") @RequestParam boolean disponible) {
        try {
            Articulo articulo = articuloService.cambiarDisponibilidad(id, disponible);
            return ResponseEntity.ok(articulo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar artículo", description = "Elimina permanentemente un artículo del sistema mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Artículo no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar - artículo tiene reservas asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarArticulo(
            @Parameter(description = "ID del artículo a eliminar", required = true, example = "1") @PathVariable Long id) {
        try {
            articuloService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Contar artículos disponibles", description = "Retorna la cantidad total de artículos actualmente disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/contar-disponibles")
    public ResponseEntity<Long> contarDisponibles() {
        long cantidad = articuloService.contarDisponibles();
        return ResponseEntity.ok(cantidad);
    }
}