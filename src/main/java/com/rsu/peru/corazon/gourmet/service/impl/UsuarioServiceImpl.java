package com.rsu.peru.corazon.gourmet.service.impl;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.model.Rol;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;
import com.rsu.peru.corazon.gourmet.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public Optional<Usuario> buscarPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getDni())) {
            throw new RuntimeException("El DNI ya se encuentra registrado en el sistema.");
        }
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(String dni, Usuario usuarioDetalles) {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el DNI: " + dni));

        usuario.setNombre(usuarioDetalles.getNombre());
        usuario.setApellido(usuarioDetalles.getApellido());
        usuario.setTelefono(usuarioDetalles.getTelefono());
        usuario.setRol(usuarioDetalles.getRol());
        
        if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarEstadoActivo(String dni, boolean estado) {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el DNI: " + dni));
        usuario.setActivo(estado);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(String dni) {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el DNI: " + dni));
        usuarioRepository.delete(usuario);
    }
}