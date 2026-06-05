package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    private String dni;

    private String nombre;
    private String apellido;
    private String telefono;
    private String password;
    private boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 30, nullable = false)
    private Rol rol;
}