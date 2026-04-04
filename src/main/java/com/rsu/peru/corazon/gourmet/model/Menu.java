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
    private Integer stockDisponible;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
}
