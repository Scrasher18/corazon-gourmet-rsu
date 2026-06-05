package com.rsu.peru.corazon.gourmet.service.impl;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByDni(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (!usuario.isActivo()) {
                throw new DisabledException("El usuario se encuentra inhabilitado en el sistema.");
            }
            
            String rolFinal = "ROLE_" + usuario.getRol().name();
            
            return new User(
                    usuario.getDni(), 
                    usuario.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(rolFinal))
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado con el DNI: " + username);
    }
}