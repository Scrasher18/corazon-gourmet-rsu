package com.rsu.peru.corazon.gourmet.dto;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialResponseDTO {
    private List<Pedido> pedidos;
    private double totalRecaudado;
    private String periodo;
    private int mesSeleccionado;
    private int anioSeleccionado;
}