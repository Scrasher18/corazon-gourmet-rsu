package com.rsu.peru.corazon.gourmet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_pedido")
@Data
@NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu; 

    private Integer cantidad = 1;
    private Double precioUnitario; 
    private Double subtotal;

    private String entradaSeleccionada; 
    private String bebidaSeleccionada; 
    private String observacion;         
}