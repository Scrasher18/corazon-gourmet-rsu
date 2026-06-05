package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByDni(String dni);
    
    List<Usuario> findByRol(Rol rol);
    
    List<Usuario> findByActivoTrue();
}