package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.model.CierreCaja;
import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.repository.CierreCajaRepository;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CierreCajaService {

    private final CierreCajaRepository cierreCajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public CierreCajaService(CierreCajaRepository cierreCajaRepository, UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public CierreCaja abrirCaja(Double montoInicial, String cajeroDni) {
        Optional<CierreCaja> cajaAbiertaGlobal = cierreCajaRepository.findByEstado("ABIERTA");
        if (cajaAbiertaGlobal.isPresent()) {
            throw new RuntimeException("Ya existe un turno abierto por otro trabajador. Debe cerrarlo primero.");
        }

        Usuario cajero = usuarioRepository.findById(cajeroDni)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado con el DNI proporcionado."));

        CierreCaja nuevaCaja = new CierreCaja();
        nuevaCaja.setFechaApertura(LocalDateTime.now());
        nuevaCaja.setMontoInicial(montoInicial);
        nuevaCaja.setEstado("ABIERTA");
        nuevaCaja.setCajero(cajero);

        return cierreCajaRepository.save(nuevaCaja);
    }

    public Optional<CierreCaja> obtenerCajaAbierta(String cajeroDni) {
        Optional<CierreCaja> cajaOpt = cierreCajaRepository.findByEstadoAndCajeroDni("ABIERTA", cajeroDni);
        if (cajaOpt.isPresent()) {
            CierreCaja caja = cajaOpt.get();
            Double ventasActuales = pedidoRepository.sumarTotalDesdeFecha(caja.getFechaApertura());
            caja.setIngresosSistema(ventasActuales != null ? ventasActuales : 0.0);
            return Optional.of(caja);
        }
        return cajaOpt;
    }

    public CierreCaja cerrarCaja(String cajeroDni, Double ingresosDeclarados, Double ingresosSistema) {
        CierreCaja caja = cierreCajaRepository.findByEstadoAndCajeroDni("ABIERTA", cajeroDni)
                .orElseThrow(() -> new RuntimeException("No tienes ninguna caja abierta para cerrar."));

        caja.setFechaCierre(LocalDateTime.now());
        caja.setIngresosSistema(ingresosSistema);
        caja.setIngresosDeclarados(ingresosDeclarados);
        caja.setDiferencia(ingresosDeclarados - ingresosSistema);
        caja.setEstado("CERRADA");

        return cierreCajaRepository.save(caja);
    }

    public List<CierreCaja> obtenerHistorialCajas() {
        return cierreCajaRepository.findAllByOrderByFechaAperturaDesc();
    }
}
