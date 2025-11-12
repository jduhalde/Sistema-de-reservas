package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para tendencia de reservas por dia de la semana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TendenciaSemanalDTO {
    private String dia;
    private Long cantidad;

    public TendenciaSemanalDTO(Integer numeroDia, Long cantidad) {
        this.dia = convertirNumeroDia(numeroDia);
        this.cantidad = cantidad;
    }

    private String convertirNumeroDia(Integer numero) {
        // MySQL DAYOFWEEK: 1=Domingo, 2=Lunes, ..., 7=Sabado
        String[] dias = { "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado" };
        return numero != null && numero >= 1 && numero <= 7 ? dias[numero - 1] : "Desconocido";
    }
}