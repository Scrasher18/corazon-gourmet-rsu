package com.rsu.peru.corazon.gourmet.config;

import com.rsu.peru.corazon.gourmet.service.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                String username = jwtUtils.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtils.validateToken(token)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 🚀 ¡AUDITORÍA AÑADIDA! Imprime directo en la consola de Spring Boot
                        System.out.println("\n========== [AUDITORÍA JWT DETECTADA] ==========");
                        System.out.println("🔗 URL Solicitada: " + request.getRequestURI());
                        System.out.println("👤 Usuario Extraído: " + username);
                        System.out.println("🎖️ Autoridades en Spring: " + userDetails.getAuthorities());
                        System.out.println("================================================\n");
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        System.out.println("⚠️ [ALERTA JWT]: El token no pasó la validación de validateToken().");
                    }
                }
            } catch (Exception e) {
                logger.error("No se pudo establecer la autenticación del usuario por JWT: " + e.getMessage());
            }
        } else if (request.getRequestURI().contains("/api/usuarios")) {
            System.out.println("⚠️ [ALERTA HTTP]: Se intentó ingresar a /api/usuarios pero el 'Authorization' Header llegó NULO o sin 'Bearer '");
        }

        filterChain.doFilter(request, response);
    }
}