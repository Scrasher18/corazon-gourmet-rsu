package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @Column(length = 8)
    private String dni;
    
    private String nombre;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
