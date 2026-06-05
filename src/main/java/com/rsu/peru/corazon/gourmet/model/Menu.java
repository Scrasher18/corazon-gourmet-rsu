package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreItem;
    private Double precioNormal;
    private Double precioConadis;
    
    @Column(nullable = false)
    private Integer stockDisponible = 1; 

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
}