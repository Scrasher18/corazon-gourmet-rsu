package com.rsu.peru.corazon.gourmet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class PedidoRequestDTO {

    private int mesa;
    private int totalPersonas;
    private List<DetallePedidoDTO> detalles;
    private double totalPagar;

    @Data
    @NoArgsConstructor
    public static class DetallePedidoDTO {
        private String tipoServicio;
        private String platoSeleccionado;
        private String entradaSeleccionada;
        private String bebidaSeleccionada;
        private double precio;
    }
}