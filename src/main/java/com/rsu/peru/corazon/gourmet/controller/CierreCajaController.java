package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.CierreCaja;
import com.rsu.peru.corazon.gourmet.service.CierreCajaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/caja")
public class CierreCajaController {

    private final CierreCajaService cierreCajaService;

    public CierreCajaController(CierreCajaService cierreCajaService) {
        this.cierreCajaService = cierreCajaService;
    }

    @GetMapping("/estado")
    public ResponseEntity<?> obtenerEstadoCaja() {
        String cajeroDni = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<CierreCaja> cajaAbierta = cierreCajaService.obtenerCajaAbierta(cajeroDni);
        if (cajaAbierta.isPresent()) {
            return ResponseEntity.ok(cajaAbierta.get());
        } else {
            return ResponseEntity.ok(Map.of("estado", "CERRADA", "mensaje", "No hay caja abierta"));
        }
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaja(@RequestBody Map<String, Object> request) {
        try {
            Double montoInicial = Double.valueOf(request.get("montoInicial").toString());
            String cajeroDni = SecurityContextHolder.getContext().getAuthentication().getName();

            CierreCaja nuevaCaja = cierreCajaService.abrirCaja(montoInicial, cajeroDni);
            return ResponseEntity.ok(nuevaCaja);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cerrar")
    public ResponseEntity<?> cerrarCaja(@RequestBody Map<String, Object> request) {
        try {
            Double ingresosDeclarados = Double.valueOf(request.get("ingresosDeclarados").toString());
            Double ingresosSistema = Double.valueOf(request.get("ingresosSistema").toString());
            String cajeroDni = SecurityContextHolder.getContext().getAuthentication().getName();

            CierreCaja cajaCerrada = cierreCajaService.cerrarCaja(cajeroDni, ingresosDeclarados, ingresosSistema);
            return ResponseEntity.ok(cajaCerrada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<List<CierreCaja>> obtenerHistorial() {
        return ResponseEntity.ok(cierreCajaService.obtenerHistorialCajas());
    }
}
