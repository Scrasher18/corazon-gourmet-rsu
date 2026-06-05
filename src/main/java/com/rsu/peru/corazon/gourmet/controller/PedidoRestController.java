package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.dto.PedidoRequestDTO;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.service.PedidoService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "${app.frontend.url}")
public class PedidoRestController {

    private final PedidoService pedidoService;

    public PedidoRestController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
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

    @PutMapping("/cerrar-mesa/{numMesa}")
    public ResponseEntity<?> cerrarMesaConPago(
            @PathVariable int numMesa, 
            @RequestParam String metodoPago) {
        System.out.println("Caja cerró la Mesa: " + numMesa + " mediante pago: " + metodoPago);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Mesa " + numMesa + " pagada y liberada con éxito.");
        return ResponseEntity.ok(response);
    }
}