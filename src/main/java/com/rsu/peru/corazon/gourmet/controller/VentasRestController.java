package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.service.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "${app.frontend.url}")
public class VentasRestController {

    private final PedidoService pedidoService;

    public VentasRestController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

  
    @GetMapping("/mesa-activa/{numMesa}")
    public ResponseEntity<Pedido> obtenerPedidoActivoPorMesa(@PathVariable int numMesa) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoActivoPorMesa(numMesa));
    }

    
    @PostMapping("/procesar-cierre/{numMesa}")
    public void procesarCierreMesa(
            @PathVariable int numMesa,
            @RequestParam String metodoPago,
            HttpServletResponse response) throws IOException {

        pedidoService.cerrarMesaConPago(numMesa, metodoPago, response);
    }

   
    @GetMapping("/historial")
    public ResponseEntity<List<Pedido>> listarHistorial() {
        return ResponseEntity.ok(pedidoService.obtenerHistorialPedidos());
    }
}