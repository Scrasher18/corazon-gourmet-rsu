package com.rsu.peru.corazon.gourmet.service.impl;

import com.rsu.peru.corazon.gourmet.model.*;
import com.rsu.peru.corazon.gourmet.repository.*;
import com.rsu.peru.corazon.gourmet.service.BoletaService;
import com.rsu.peru.corazon.gourmet.service.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public void procesarVenta(Long menuId, Integer cantidad, String entrada, String bebida, 
                              boolean esConadis, String clienteNombre, String clienteDni, 
                              String dniMesero, HttpServletResponse response) throws IOException {
    }

    @Override
    @Transactional
    public void procesarVentaGrupal(List<PedidoItem> carrito, String clienteNombre, 
                                     String clienteDni, String dniMesero, 
                                     HttpServletResponse response) throws IOException {
        
        Pedido pedido = new Pedido();
        pedido.setDniCliente(clienteDni);
        pedido.setDniMesero(dniMesero);
        pedido.setFecha(LocalDateTime.now());
        pedido.setDetalles(new ArrayList<>());
        
        double totalAcumulado = 0;

        for (PedidoItem item : carrito) {
            Menu plato = menuRepository.findById(item.getId()).orElseThrow();
            
            double precioAplicado = item.isEsConadis() ? plato.getPrecioConadis() : plato.getPrecioNormal();
            totalAcumulado += precioAplicado;

            DetallePedido detalle = new DetallePedido();
            detalle.setMenu(plato);
            detalle.setCantidad(1);
            detalle.setPrecioAplicado(precioAplicado);
            detalle.setSubtotal(precioAplicado);
            detalle.setPedido(pedido);
            
            pedido.getDetalles().add(detalle);

            plato.setStockDisponible(plato.getStockDisponible() - 1);
            menuRepository.save(plato);
        }

        pedido.setMontoTotal(totalAcumulado);
        pedidoRepository.save(pedido);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=boleta_" + clienteDni + ".pdf");

        BoletaService exporter = new BoletaService(carrito, totalAcumulado, clienteNombre, clienteDni);
        exporter.export(response);
    }
}