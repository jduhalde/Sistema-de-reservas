package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para prediccion de disponibilidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDTO {
    private String tipo; // "sala" o "articulo"
    private String nombre;
    private LocalDateTime proximoHorarioDisponible;
    private Boolean disponibleAhora;
    private String mensaje;
}