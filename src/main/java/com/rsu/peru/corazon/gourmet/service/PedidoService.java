package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.dto.PedidoRequestDTO;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface PedidoService {

    void guardarOActualizarPedidoMesa(PedidoRequestDTO pedidoDTO);

    Pedido obtenerPedidoActivoPorMesa(int numMesa);

    void cerrarMesaConPago(int numMesa, String metodoPago, HttpServletResponse response) throws IOException;

    List<Pedido> obtenerHistorialPedidos();

    List<PedidoRequestDTO> obtenerPedidosActivosDTO();
}
