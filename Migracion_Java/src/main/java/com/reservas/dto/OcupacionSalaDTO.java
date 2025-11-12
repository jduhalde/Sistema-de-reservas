package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadisticas de ocupacion de salas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionSalaDTO {
    private String nombreSala;
    private Double tasaOcupacion; // Porcentaje (0-100)
    private Long horasOcupadas;
    private Long horasDisponibles;
    private Long totalReservas;
}