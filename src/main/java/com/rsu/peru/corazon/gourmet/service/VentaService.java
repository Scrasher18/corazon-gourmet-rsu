package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.model.PedidoItem;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface VentaService {
    
    void procesarVenta(Long menuId, Integer cantidad, String entrada, String bebida, 
                       boolean esConadis, String clienteNombre, String clienteDni, 
                       String dniMesero, HttpServletResponse response) throws IOException;

    void procesarVentaGrupal(List<PedidoItem> carrito, String clienteNombre, 
                             String clienteDni, String dniMesero, 
                             HttpServletResponse response) throws IOException;
}