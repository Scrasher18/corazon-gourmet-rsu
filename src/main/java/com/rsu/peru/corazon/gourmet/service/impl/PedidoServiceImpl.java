package com.rsu.peru.corazon.gourmet.service.impl;

import com.rsu.peru.corazon.gourmet.dto.PedidoRequestDTO;
import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.model.DetallePedido;
import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.repository.MenuRepository;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;
import com.rsu.peru.corazon.gourmet.service.BoletaService;
import com.rsu.peru.corazon.gourmet.service.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final MenuRepository menuRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoletaService boletaService;

    public PedidoServiceImpl(MenuRepository menuRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            BoletaService boletaService) {
        this.menuRepository = menuRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.boletaService = boletaService;
    }

    @Override
    @Transactional
    public void guardarOActualizarPedidoMesa(PedidoRequestDTO pedidoDTO) {
        if (pedidoDTO == null) {
            throw new IllegalArgumentException("El pedido no contiene ningún producto seleccionado.");
        }

        Pedido pedido = pedidoRepository.findByMesaAndEstado(pedidoDTO.getMesa(), "ABIERTA")
                .orElse(null);

        boolean esNuevoPedido = (pedido == null);

        if (esNuevoPedido) {
            pedido = new Pedido();
            pedido.setMesa(pedidoDTO.getMesa());
            pedido.setEstado("ABIERTA");
            pedido.setFecha(LocalDateTime.now());
            pedido.setDetalles(new ArrayList<>());

            if (pedidoDTO.getUsuarioDni() != null) {
                Usuario usuario = usuarioRepository.findById(pedidoDTO.getUsuarioDni()).orElse(null);
                pedido.setUsuario(usuario);
            }
        }

        Usuario usuarioActuante = usuarioRepository.findById(pedidoDTO.getUsuarioDni()).orElse(null);
        boolean esAdmin = usuarioActuante != null && "ADMINISTRADOR".equals(usuarioActuante.getRol());
        boolean esDiferenteMesero = false;
        Map<Long, Integer> cantidadesAnteriores = new HashMap<>();

        if (!esNuevoPedido && pedido.getUsuario() != null && !pedido.getUsuario().getDni().equals(pedidoDTO.getUsuarioDni()) && !esAdmin) {
            esDiferenteMesero = true;
            for (DetallePedido dp : pedido.getDetalles()) {
                Long menuId = dp.getMenu().getId();
                cantidadesAnteriores.put(menuId, cantidadesAnteriores.getOrDefault(menuId, 0) + dp.getCantidad());
            }
        }

        int contadorMenu = 0;
        int contadorEspecial = 0;

        pedido.getDetalles().clear();

        if (pedidoDTO.getDetalles() != null) {
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
            }
        }

        if (pedidoDTO.getExtrasMesa() != null) {
            for (PedidoRequestDTO.ExtraDTO extraDTO : pedidoDTO.getExtrasMesa()) {
                Menu extraMenu = menuRepository.findByNombreItem(extraDTO.getItem())
                        .orElseThrow(() -> new RuntimeException("El extra '" + extraDTO.getItem() + "' no existe."));

                DetallePedido detalleExtra = new DetallePedido();
                detalleExtra.setCantidad(extraDTO.getCantidad());

                double precioUnitario = extraDTO.getTotal() / extraDTO.getCantidad();
                detalleExtra.setPrecioUnitario(precioUnitario);
                detalleExtra.setSubtotal(extraDTO.getTotal());
                detalleExtra.setMenu(extraMenu);
                detalleExtra.setPedido(pedido);

                pedido.getDetalles().add(detalleExtra);
            }
        }

        if (pedidoDTO.getPostres() != null) {
            for (PedidoRequestDTO.ItemExtraDTO postreDTO : pedidoDTO.getPostres()) {
                Menu postreMenu = menuRepository.findByNombreItem(postreDTO.getItem().getNombreItem())
                        .orElseThrow(() -> new RuntimeException("El postre '" + postreDTO.getItem().getNombreItem() + "' no existe."));

                DetallePedido detallePostre = new DetallePedido();
                detallePostre.setCantidad(postreDTO.getCantidad());
                detallePostre.setPrecioUnitario(postreDTO.getItem().getPrecioNormal());
                detallePostre.setSubtotal(postreDTO.getItem().getPrecioNormal() * postreDTO.getCantidad());
                detallePostre.setMenu(postreMenu);
                detallePostre.setPedido(pedido);

                pedido.getDetalles().add(detallePostre);
            }
        }

        if (pedidoDTO.getBebidasExtra() != null) {
            for (PedidoRequestDTO.ItemExtraDTO bebidaDTO : pedidoDTO.getBebidasExtra()) {
                Menu bebidaMenu = menuRepository.findByNombreItem(bebidaDTO.getItem().getNombreItem())
                        .orElseThrow(() -> new RuntimeException("La bebida extra '" + bebidaDTO.getItem().getNombreItem() + "' no existe."));

                DetallePedido detalleBebida = new DetallePedido();
                detalleBebida.setCantidad(bebidaDTO.getCantidad());
                detalleBebida.setPrecioUnitario(bebidaDTO.getItem().getPrecioNormal());
                detalleBebida.setSubtotal(bebidaDTO.getItem().getPrecioNormal() * bebidaDTO.getCantidad());
                detalleBebida.setMenu(bebidaMenu);
                detalleBebida.setPedido(pedido);

                pedido.getDetalles().add(detalleBebida);
            }
        }

        if (esDiferenteMesero) {
            Map<Long, Integer> cantidadesNuevas = new HashMap<>();
            for (DetallePedido dp : pedido.getDetalles()) {
                Long menuId = dp.getMenu().getId();
                cantidadesNuevas.put(menuId, cantidadesNuevas.getOrDefault(menuId, 0) + dp.getCantidad());
            }

            for (Map.Entry<Long, Integer> entry : cantidadesAnteriores.entrySet()) {
                Long menuId = entry.getKey();
                int cantAnterior = entry.getValue();
                int cantNueva = cantidadesNuevas.getOrDefault(menuId, 0);

                if (cantNueva < cantAnterior) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Operación denegada: Solo tienes permitido añadir productos nuevos a las comandas de tus compañeros. No puedes eliminar o reducir cantidades.");
                }
            }
        }

        pedido.setCantidadMenu(contadorMenu);
        pedido.setCantidadEspecial(contadorEspecial);
        pedido.setMontoTotal(pedidoDTO.getTotalPagar());

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

        String cbDni = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cajero = usuarioRepository.findById(cbDni).orElse(null);
        pedido.setCajero(cajero);

        Pedido pedidoPagado = pedidoRepository.save(pedido);

        configurarHeadersResponse(response, numMesa);
        boletaService.export(response, pedidoPagado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoRequestDTO> obtenerPedidosActivosDTO() {
        List<Pedido> pedidosAbiertos = pedidoRepository.findByEstado("ABIERTA");
        List<PedidoRequestDTO> activos = new ArrayList<>();

        for (Pedido p : pedidosAbiertos) {
            PedidoRequestDTO dto = new PedidoRequestDTO();
            dto.setMesa(p.getMesa());
            dto.setTotalPagar(p.getMontoTotal());

            if (p.getUsuario() != null) {
                dto.setUsuarioDni(p.getUsuario().getDni());
            }

            int totalPersonas = 0;
            List<PedidoRequestDTO.DetallePedidoDTO> detallesDTO = new ArrayList<>();
            List<PedidoRequestDTO.ItemExtraDTO> postresDTO = new ArrayList<>();
            List<PedidoRequestDTO.ItemExtraDTO> bebidasExtraDTO = new ArrayList<>();

            for (DetallePedido dp : p.getDetalles()) {
                String categoria = dp.getMenu().getCategoria().toString();

                if ("PLATO_FONDO".equals(categoria) || "PLATO_ESPECIAL".equals(categoria)) {
                    totalPersonas++;
                    PedidoRequestDTO.DetallePedidoDTO item = new PedidoRequestDTO.DetallePedidoDTO();
                    item.setPlatoSeleccionado(dp.getMenu().getNombreItem());
                    item.setPrecio(dp.getPrecioUnitario());
                    item.setEntradaSeleccionada(dp.getEntradaSeleccionada());
                    item.setBebidaSeleccionada(dp.getBebidaSeleccionada());
                    item.setTipoServicio("PLATO_FONDO".equals(categoria) ? "MENU" : "CARTA");
                    detallesDTO.add(item);
                } else if ("POSTRE_ADICIONAL".equals(categoria)) {
                    PedidoRequestDTO.ItemExtraDTO postre = new PedidoRequestDTO.ItemExtraDTO();
                    PedidoRequestDTO.MenuResumenDTO menuResumen = new PedidoRequestDTO.MenuResumenDTO();
                    menuResumen.setNombreItem(dp.getMenu().getNombreItem());
                    menuResumen.setPrecioNormal(dp.getPrecioUnitario());
                    postre.setItem(menuResumen);
                    postre.setCantidad(dp.getCantidad());
                    postresDTO.add(postre);
                } else if ("BEBIDA_EXTRA".equals(categoria)) {
                    PedidoRequestDTO.ItemExtraDTO bebida = new PedidoRequestDTO.ItemExtraDTO();
                    PedidoRequestDTO.MenuResumenDTO menuResumen = new PedidoRequestDTO.MenuResumenDTO();
                    menuResumen.setNombreItem(dp.getMenu().getNombreItem());
                    menuResumen.setPrecioNormal(dp.getPrecioUnitario());
                    bebida.setItem(menuResumen);
                    bebida.setCantidad(dp.getCantidad());
                    bebidasExtraDTO.add(bebida);
                }
            }

            dto.setTotalPersonas(totalPersonas);
            dto.setDetalles(detallesDTO);
            dto.setPostres(postresDTO);
            dto.setBebidasExtra(bebidasExtraDTO);
            dto.setExtrasMesa(new ArrayList<>());

            activos.add(dto);
        }

        return activos;
    }

    private void configurarHeadersResponse(HttpServletResponse response, int numMesa) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=boleta_mesa_" + numMesa + ".pdf";
        response.setHeader(headerKey, headerValue);
    }
}
