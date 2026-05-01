package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PedidoRepository pedidoRepository;

    public AdminController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Pedido> todos = pedidoRepository.findAllByOrderByFechaDesc();
        LocalDateTime hoy = LocalDateTime.now();

        
        List<Pedido> pedidosHoy = todos.stream()
                .filter(p -> p.getFecha() != null && 
                        p.getFecha().toLocalDate().isEqual(hoy.toLocalDate()))
                .collect(Collectors.toList());

        double totalHoy = pedidosHoy.stream()
                .mapToDouble(Pedido::getMontoTotal)
                .sum();

        model.addAttribute("pedidos", pedidosHoy); 
        model.addAttribute("totalRecaudado", totalHoy);
        
        return "admin/dashboard";
    }

    @GetMapping("/historial")
    public String verHistorial(
            @RequestParam(name = "periodo", defaultValue = "hoy") String periodo,
            @RequestParam(name = "mes", required = false) Integer mes,
            @RequestParam(name = "anio", required = false) Integer anio,
            Model model) {
        
        List<Pedido> todos = pedidoRepository.findAllByOrderByFechaDesc();
        LocalDateTime ahora = LocalDateTime.now();
        
        int m = (mes != null) ? mes : ahora.getMonthValue();
        int a = (anio != null) ? anio : ahora.getYear();

        List<Pedido> filtrados = todos.stream().filter(p -> {
            if (p.getFecha() == null) return false;
            
            switch (periodo) {
                case "hoy":
                    return p.getFecha().toLocalDate().isEqual(ahora.toLocalDate());
                case "mes":
                    return p.getFecha().getMonthValue() == m && p.getFecha().getYear() == a;
                case "anio":
                    return p.getFecha().getYear() == a;
                case "todo":
                    return true;
                default:
                    return p.getFecha().toLocalDate().isEqual(ahora.toLocalDate());
            }
        }).collect(Collectors.toList());

        double total = filtrados.stream()
                .mapToDouble(Pedido::getMontoTotal)
                .sum();

        model.addAttribute("pedidos", filtrados);
        model.addAttribute("totalRecaudado", total);
        model.addAttribute("periodo", periodo);
        model.addAttribute("mesSeleccionado", m);
        model.addAttribute("anioSeleccionado", a);
        
        return "admin/historial";
    }
}