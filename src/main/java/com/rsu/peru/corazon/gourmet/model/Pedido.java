package com.rsu.peru.corazon.gourmet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mesa; 
    
    @Column(length = 20)
    private String estado = "ABIERTA"; 

    @Column(length = 30)
    private String metodoPago; 

    private LocalDateTime fecha = LocalDateTime.now();
    private Double montoTotal = 0.0;
    
    private Integer cantidadMenu = 0;       
    private Integer cantidadEspecial = 0;  

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetallePedido> detalles;

    @ManyToOne
    @JoinColumn(name = "usuario_dni", referencedColumnName = "dni")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cajero_dni", referencedColumnName = "dni")
    private Usuario cajero;
}