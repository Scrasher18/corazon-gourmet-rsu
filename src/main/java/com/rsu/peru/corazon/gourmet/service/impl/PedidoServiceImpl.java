package com.rsu.peru.corazon.gourmet.service.impl;

import com.rsu.peru.corazon.gourmet.dto.PedidoRequestDTO;
import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.model.DetallePedido;
import com.rsu.peru.corazon.gourmet.repository.MenuRepository;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository;
import com.rsu.peru.corazon.gourmet.service.BoletaService;
import com.rsu.peru.corazon.gourmet.service.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final MenuRepository menuRepository;
    private final PedidoRepository pedidoRepository;
    private final BoletaService boletaService;

    public PedidoServiceImpl(MenuRepository menuRepository,
            PedidoRepository pedidoRepository,
            BoletaService boletaService) {
        this.menuRepository = menuRepository;
        this.pedidoRepository = pedidoRepository;
        this.boletaService = boletaService;
    }

    @Override
    @Transactional
    public void guardarOActualizarPedidoMesa(PedidoRequestDTO pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getDetalles() == null || pedidoDTO.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene ningún plato seleccionado.");
        }

        
        Pedido pedido = pedidoRepository.findByMesaAndEstado(pedidoDTO.getMesa(), "ABIERTA")
                .orElse(null);

        if (pedido == null) {
            pedido = new Pedido();
            pedido.setMesa(pedidoDTO.getMesa());
            pedido.setCantidadMenu(0);
            pedido.setCantidadEspecial(0);
            pedido.setMontoTotal(0.0);
            pedido.setEstado("ABIERTA");
            pedido.setFecha(LocalDateTime.now());
            pedido.setDetalles(new ArrayList<>());
        }

        double nuevoMontoAcumulado = pedido.getMontoTotal();
        int contadorMenu = pedido.getCantidadMenu();
        int contadorEspecial = pedido.getCantidadEspecial();

        for (PedidoRequestDTO.DetallePedidoDTO itemDTO : pedidoDTO.getDetalles()) {
            Menu plato = menuRepository.findByNombreItem(itemDTO.getPlatoSeleccionado())
                    .orElseThrow(() -> new RuntimeException("El plato '" + itemDTO.getPlatoSeleccionado() + "' no existe."));

            if ("MENU".equalsIgnoreCase(itemDTO.getTipoServicio()) || "PLATO_FONDO".equals(plato.getCategoria().toString())) {
                contadorMenu++;
            } else if ("CARTA".equalsIgnoreCase(itemDTO.getTipoServicio()) || "PLATO_ESPECIAL".equals(plato.getCategoria().toString())) {
                contadorEspecial++;
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(itemDTO.getPrecio());
            detalle.setSubtotal(itemDTO.getPrecio());
            detalle.setMenu(plato);
            detalle.setPedido(pedido);
            detalle.setEntradaSeleccionada(itemDTO.getEntradaSeleccionada());
            detalle.setBebidaSeleccionada(itemDTO.getBebidaSeleccionada());

            pedido.getDetalles().add(detalle);
            nuevoMontoAcumulado += detalle.getSubtotal();
        }

        pedido.setCantidadMenu(contadorMenu);
        pedido.setCantidadEspecial(contadorEspecial);
        pedido.setMontoTotal(nuevoMontoAcumulado);

        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoActivoPorMesa(int numMesa) {
        return pedidoRepository.findByMesaAndEstado(numMesa, "ABIERTA")
                .orElseThrow(() -> new RuntimeException("No se encontró ningún pedido activo (ABIERTA) para la mesa " + numMesa));
    }

    @Override
    @Transactional
    public void cerrarMesaConPago(int numMesa, String metodoPago, HttpServletResponse response) throws IOException {
        Pedido pedido = pedidoRepository.findByMesaAndEstado(numMesa, "ABIERTA")
                .orElseThrow(() -> new RuntimeException("La mesa " + numMesa + " no tiene ninguna cuenta activa para cerrar."));

        pedido.setEstado("PAGADA");
        pedido.setMetodoPago(metodoPago.toUpperCase());

        Pedido pedidoPagado = pedidoRepository.save(pedido);

        configurarHeadersResponse(response, numMesa);
        boletaService.export(response, pedidoPagado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidos() {

        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    private void configurarHeadersResponse(HttpServletResponse response, int numMesa) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=boleta_mesa_" + numMesa + ".pdf";
        response.setHeader(headerKey, headerValue);
    }
}
