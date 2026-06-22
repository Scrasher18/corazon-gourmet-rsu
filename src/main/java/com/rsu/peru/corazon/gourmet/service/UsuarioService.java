package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.dto.PasswordChangeDTO;
import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.model.Rol;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listarTodos();
    List<Usuario> listarActivos();
    List<Usuario> listarPorRol(Rol rol);
    Optional<Usuario> buscarPorDni(String dni);
    Usuario registrarUsuario(Usuario usuario);
    Usuario actualizarUsuario(String dni, Usuario usuarioDetalles);
    void cambiarEstadoActivo(String dni, boolean estado);
    void eliminarUsuario(String dni);
    void cambiarPassword(PasswordChangeDTO dto);
}