package com.rsu.peru.corazon.gourmet.config;

import com.rsu.peru.corazon.gourmet.model.Rol;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (!usuarioRepository.existsById("71156519")) {

                Usuario admin = new Usuario();
                admin.setDni("71156519");
                admin.setNombre("Luis Oscar");
                admin.setApellido("Perez Castro");

                admin.setPassword(passwordEncoder.encode("123"));
                admin.setRol(Rol.ADMINISTRADOR);
                admin.setTelefono("922779157");
                admin.setActivo(true);

                usuarioRepository.save(admin);

            }
        };
    }
}
