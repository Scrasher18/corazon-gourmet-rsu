package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "meseros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesero {
    @Id
    @Column(length = 8)
    private String dni;
    
    private String nombre;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
}