package com.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;

    // 🆕 Nuevos campos para crear la Persona
    private String nombre; // Nombre completo: "Juan Pérez"
    private String telefono; // Teléfono: "11-2345-6789"
}