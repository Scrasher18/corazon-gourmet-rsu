package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.dto.VentaAgrupadaDTO;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository; 
import com.rsu.peru.corazon.gourmet.service.UsuarioService;
import com.rsu.peru.corazon.gourmet.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "${app.frontend.url}")
public class DashboardRestController {

    private final UsuarioService usuarioService;
    private final MenuService menuService;
    private final PedidoRepository pedidoRepository; 

    public DashboardRestController(UsuarioService usuarioService,
            MenuService menuService,
            PedidoRepository pedidoRepository) {
        this.usuarioService = usuarioService;
        this.menuService = menuService;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> obtenerMetricas() {
        Map<String, Object> metricas = new HashMap<>();

        try {

            long totalPersonal = usuarioService.listarActivos().size();

            long totalPlatos = menuService.listarTodos().size();

            long pedidosHoy = pedidoRepository.countPedidosHoy();
            Double recaudacionHoy = pedidoRepository.sumarRecaudacionHoy();

            metricas.put("totalPersonal", totalPersonal);
            metricas.put("totalPlatos", totalPlatos);
            metricas.put("pedidosDelDia", pedidosHoy);
            metricas.put("ventasDelDia", recaudacionHoy);

            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            metricas.put("error", "No se pudieron cargar las métricas del panel de administración.");
            return ResponseEntity.internalServerError().body(metricas);
        }
    }

    
    @GetMapping("/grafico-ventas")
    public ResponseEntity<List<VentaAgrupadaDTO>> obtenerGraficoVentas(@RequestParam(defaultValue = "hoy") String periodo) {
        if ("mes".equalsIgnoreCase(periodo)) {
            return ResponseEntity.ok(pedidoRepository.obtenerVentasMes());
        } else if ("anio".equalsIgnoreCase(periodo)) {
            return ResponseEntity.ok(pedidoRepository.obtenerVentasAnio());
        }
        return ResponseEntity.ok(pedidoRepository.obtenerVentasHoy());
    }
}