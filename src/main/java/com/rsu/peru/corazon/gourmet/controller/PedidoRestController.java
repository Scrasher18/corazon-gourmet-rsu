package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.dto.PedidoRequestDTO;
import com.rsu.peru.corazon.gourmet.model.CierreCaja;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.service.CierreCajaService;
import com.rsu.peru.corazon.gourmet.service.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "${app.frontend.url}")
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final CierreCajaService cierreCajaService;

    public PedidoRestController(PedidoService pedidoService, CierreCajaService cierreCajaService) {
        this.pedidoService = pedidoService;
        this.cierreCajaService = cierreCajaService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        pedidoService.guardarOActualizarPedidoMesa(pedidoDTO);
        System.out.println("Pedido guardado y procesado para la Mesa: " + pedidoDTO.getMesa());
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Pedido agregado a la mesa " + pedidoDTO.getMesa() + " correctamente.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Pedido>> obtenerHistorial() {
        List<Pedido> historial = pedidoService.obtenerHistorialPedidos();
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PedidoRequestDTO>> obtenerMesasActivas() {
        List<PedidoRequestDTO> mesasActivas = pedidoService.obtenerPedidosActivosDTO();
        return ResponseEntity.ok(mesasActivas);
    }

    @GetMapping("/mesa-activa/{numMesa}")
    public ResponseEntity<Pedido> obtenerPedidoActivoPorMesa(@PathVariable int numMesa) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoActivoPorMesa(numMesa));
    }

    @PutMapping("/cerrar-mesa/{numMesa}")
    public void cerrarMesaConPago(
            @PathVariable int numMesa, 
            @RequestParam String metodoPago,
            HttpServletResponse response) throws IOException {

        String cajeroDni = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<CierreCaja> miCaja = cierreCajaService.obtenerCajaAbierta(cajeroDni);

        if (miCaja.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"Operación denegada: No puedes procesar pagos porque tu turno de caja está cerrado.\"}");
            return;
        }

        System.out.println("Caja cerró la Mesa: " + numMesa + " mediante pago: " + metodoPago);
        pedidoService.cerrarMesaConPago(numMesa, metodoPago, response);
    }
}