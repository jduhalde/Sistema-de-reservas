package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que agrupa todas las estadisticas del sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasGeneralesDTO {
    private Long totalReservas;
    private Double duracionPromedioMinutos;
    private List<EstadisticaDTO> salasPopulares;
    private List<EstadisticaDTO> articulosPopulares;
    private List<HoraPicoDTO> horasPico;
    private List<TendenciaSemanalDTO> tendenciaSemanal;
    private List<OcupacionSalaDTO> ocupacionSalas;
}