package com.rsu.peru.corazon.gourmet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("#{'${app.frontend.url:http://localhost:4200}'.split(',')}")
    private List<String> frontendUrl;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/cambiar-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menus", "/api/menus/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/pedidos", "/api/pedidos/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/ventas", "/api/ventas/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/cerrar-mesa/**").hasAnyAuthority("CAJA", "ROLE_CAJA","ROLE_ADMINISTRADOR","ADMINISTRADOR")
                .requestMatchers("/api/caja/**").hasAnyAuthority("CAJA", "ROLE_CAJA", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/menus", "/api/menus/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/pedidos", "/api/pedidos/**").hasAnyAuthority("MESERO", "ROLE_MESERO", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/ventas", "/api/ventas/**").hasAnyAuthority("CAJA", "ROLE_CAJA", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/dashboard", "/api/dashboard/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/usuarios", "/api/usuarios/**").hasAnyAuthority("ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .requestMatchers("/api/caja", "/api/caja/**").hasAnyAuthority("CAJA", "ROLE_CAJA", "ADMINISTRADOR", "ROLE_ADMINISTRADOR")
                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(frontendUrl);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
