package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario ,String> {
    Optional<Usuario> findByNombre(String nombre);
}
