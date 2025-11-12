package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role; // 🆕 NUEVO: Incluir el rol en la respuesta
    private String email; // 🆕 NUEVO: Email del usuario
}