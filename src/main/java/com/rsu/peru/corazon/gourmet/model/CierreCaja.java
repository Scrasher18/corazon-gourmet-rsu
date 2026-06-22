package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierre_caja")
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_inicial", nullable = false)
    private Double montoInicial;

    @Column(name = "ingresos_sistema")
    private Double ingresosSistema;

    @Column(name = "ingresos_declarados")
    private Double ingresosDeclarados;

    @Column(name = "diferencia")
    private Double diferencia;

    @Column(name = "estado", nullable = false)
    private String estado; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cajero_dni", referencedColumnName = "dni", nullable = false)
    private Usuario cajero;

    public CierreCaja() {
    }

    public CierreCaja(LocalDateTime fechaApertura, Double montoInicial, String estado, Usuario cajero) {
        this.fechaApertura = fechaApertura;
        this.montoInicial = montoInicial;
        this.estado = estado;
        this.cajero = cajero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(Double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public Double getIngresosSistema() {
        return ingresosSistema;
    }

    public void setIngresosSistema(Double ingresosSistema) {
        this.ingresosSistema = ingresosSistema;
    }

    public Double getIngresosDeclarados() {
        return ingresosDeclarados;
    }

    public void setIngresosDeclarados(Double ingresosDeclarados) {
        this.ingresosDeclarados = ingresosDeclarados;
    }

    public Double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(Double diferencia) {
        this.diferencia = diferencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getCajero() {
        return cajero;
    }

    public void setCajero(Usuario cajero) {
        this.cajero = cajero;
    }
}