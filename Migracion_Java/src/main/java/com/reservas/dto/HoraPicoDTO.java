package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para analisis de horas pico (0-23)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoraPicoDTO {
    private Integer hora;
    private Long cantidad;
    private String descripcion; // ej: "08:00 - 09:00"

    public HoraPicoDTO(Integer hora, Long cantidad) {
        this.hora = hora;
        this.cantidad = cantidad;
        this.descripcion = String.format("%02d:00 - %02d:00", hora, hora + 1);
    }
}