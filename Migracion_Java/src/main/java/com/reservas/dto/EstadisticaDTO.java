package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadisticas generales con nombre y valor numerico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaDTO {
    private String nombre;
    private Long cantidad;

    // Constructor alternativo para valores decimales
    public EstadisticaDTO(String nombre, Double valor) {
        this.nombre = nombre;
        this.cantidad = valor != null ? valor.longValue() : 0L;
    }
}