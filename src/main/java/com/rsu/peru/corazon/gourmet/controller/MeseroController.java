package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Mesero;
import com.rsu.peru.corazon.gourmet.repository.MenuRepository;
import com.rsu.peru.corazon.gourmet.service.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/mesero")
public class MeseroController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private VentaService ventaService;

    @PostMapping("/guardar-venta")
    public void guardarVenta(@RequestParam Long menuId, 
                             @RequestParam Integer cantidad,
                             @RequestParam(required = false) String entrada,
                             @RequestParam(required = false) String bebida,
                             @RequestParam(defaultValue = "false") boolean esConadis,
                             @RequestParam String clienteNombre, 
                             @RequestParam String clienteDni,
                             HttpSession session, 
                             HttpServletResponse response) throws IOException {

        Mesero mesero = (Mesero) session.getAttribute("usuarioLogueado");
        String dniMesero = (mesero != null) ? mesero.getDni() : "00000000";

        ventaService.procesarVenta(menuId, cantidad, entrada, bebida, esConadis, 
                                   clienteNombre, clienteDni, dniMesero, response);
    }
}