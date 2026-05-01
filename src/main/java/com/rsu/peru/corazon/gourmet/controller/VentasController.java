package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.PedidoItem;
import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Mesero;
import com.rsu.peru.corazon.gourmet.repository.MenuRepository;
import com.rsu.peru.corazon.gourmet.service.CarritoService;
import com.rsu.peru.corazon.gourmet.service.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/mesero")
public class VentasController {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private VentaService ventaService;

    @GetMapping("/ventas")
    public String mostrarPanelVentas(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/";
        }
        model.addAttribute("listaMenu", menuRepository.findAll());
        model.addAttribute("itemsCarrito", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        return "mesero/ventas";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam Long id, Model model) {
        Menu plato = menuRepository.findById(id).orElseThrow();
        PedidoItem item = new PedidoItem(
            plato.getId(), 
            plato.getNombreItem(), 
            plato.getPrecioNormal(), 
            plato.getPrecioConadis(),
            plato.getCategoria().name()
        );
        carritoService.agregar(item);
        actualizarModeloCarrito(model);
        return "mesero/ventas :: #cart-panel-content"; 
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam int index, Model model) {
        carritoService.eliminar(index);
        actualizarModeloCarrito(model);
        return "mesero/ventas :: #cart-panel-content";
    }

    @PostMapping("/carrito/conadis-item")
    public String toggleConadisItem(@RequestParam int index, Model model) {
        if (index >= 0 && index < carritoService.getItems().size()) {
            PedidoItem item = carritoService.getItems().get(index);
            item.setEsConadis(!item.isEsConadis());
        }
        actualizarModeloCarrito(model);
        return "mesero/ventas :: #cart-panel-content";
    }

    @PostMapping("/carrito/limpiar")
    public String limpiarCarrito(Model model) {
        carritoService.limpiar();
        actualizarModeloCarrito(model);
        return "mesero/ventas :: #cart-panel-content";
    }

    private void actualizarModeloCarrito(Model model) {
        model.addAttribute("itemsCarrito", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
    }

    @PostMapping("/guardar-venta-grupal")
    public void guardarVentaGrupal(@RequestParam String clienteNombre, 
                                   @RequestParam String clienteDni,
                                   HttpSession session,
                                   HttpServletResponse response) throws IOException {
        
        if (carritoService.getItems().isEmpty()) {
            response.sendRedirect("/mesero/ventas?error=vacio");
            return;
        }

        Mesero mesero = (Mesero) session.getAttribute("usuarioLogueado");
        String dniMesero = (mesero != null) ? mesero.getDni() : "00000000";

        ventaService.procesarVentaGrupal(
            carritoService.getItems(), 
            clienteNombre, 
            clienteDni, 
            dniMesero, 
            response
        );

        carritoService.limpiar();
    }
}