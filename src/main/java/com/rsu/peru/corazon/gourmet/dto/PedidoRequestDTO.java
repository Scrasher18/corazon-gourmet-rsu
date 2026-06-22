package com.rsu.peru.corazon.gourmet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class PedidoRequestDTO {

    private int mesa;
    private int totalPersonas;
    private double totalPagar;
    
    private String usuarioDni; 
    
    private List<DetallePedidoDTO> detalles;
    
    private List<ExtraDTO> extrasMesa; 
    
    private List<ItemExtraDTO> postres;
    private List<ItemExtraDTO> bebidasExtra;

    @Data
    @NoArgsConstructor
    public static class DetallePedidoDTO {
        private String tipoServicio;
        private String platoSeleccionado;
        private String entradaSeleccionada;
        private String bebidaSeleccionada;
        private double precio;
        private boolean isConadis; 
    }

  
    @Data
    @NoArgsConstructor
    public static class ExtraDTO {
        private String item;
        private int cantidad;
        private double total;
    }

 
    @Data
    @NoArgsConstructor
    public static class ItemExtraDTO {
        private MenuResumenDTO item;
        private int cantidad;
    }

    @Data
    @NoArgsConstructor
    public static class MenuResumenDTO {
        private String nombreItem;
        private double precioNormal;
    }
}