package com.reservas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "articulos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(length = 50)
    private String categoria;
    
    @Column(nullable = false)
    private boolean disponible = true;
}