package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.model.Rol;
import com.rsu.peru.corazon.gourmet.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")

public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> listarActivos() {
        return ResponseEntity.ok(usuarioService.listarActivos());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> listarPorRol(@PathVariable String rol) {
        try {
 
            Rol rolEnum = Rol.valueOf(rol.toUpperCase().trim());
            return ResponseEntity.ok(usuarioService.listarPorRol(rolEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Usuario> buscarPorDni(@PathVariable String dni) {
        return usuarioService.buscarPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable String dni, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuario(dni, usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{dni}/estado")
    public ResponseEntity<Void> cambiarEstadoActivo(@PathVariable String dni, @RequestBody Map<String, Boolean> body) {
        try {
            boolean activo = body.get("activo");
            usuarioService.cambiarEstadoActivo(dni, activo);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String dni) {
        try {
            usuarioService.eliminarUsuario(dni);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}