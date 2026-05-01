package com.rsu.peru.corazon.gourmet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private double precioNormal;  
    private double precioConadis; 
    private boolean esConadis;
    private String categoria; 

    public PedidoItem(Long id, String nombre, double precioNormal, double precioConadis, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precioNormal = precioNormal;
        this.precioConadis = precioConadis;
        this.categoria = categoria;
        this.esConadis = false;
    }
}