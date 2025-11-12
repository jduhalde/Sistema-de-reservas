package com.reservas.dto;

import com.reservas.model.Reserva;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {
    private List<Reserva> reservas;
}
