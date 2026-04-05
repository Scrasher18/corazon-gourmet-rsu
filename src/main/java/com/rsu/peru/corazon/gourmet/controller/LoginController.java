package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Usuario;
import com.rsu.peru.corazon.gourmet.model.Rol;
import com.rsu.peru.corazon.gourmet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }
    
    @PostMapping("/login")
    public String autenticar(@RequestParam String dni, @RequestParam String password, HttpSession session, Model model) {
        Optional<Usuario> userOpt = usuarioRepository.findById(dni);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            Usuario user = userOpt.get();
            session.setAttribute("usuarioLogueado", user);
            if (user.getRol() == Rol.ADMINISTRADOR) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/mesero/ventas";
            }
        }
        model.addAttribute("error", "DNI o contraseña incorrectos");
        return "login";
    }
}
