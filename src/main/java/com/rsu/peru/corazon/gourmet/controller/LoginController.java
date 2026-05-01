package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Admin;
import com.rsu.peru.corazon.gourmet.model.Mesero;
import com.rsu.peru.corazon.gourmet.repository.AdminRepository;
import com.rsu.peru.corazon.gourmet.repository.MeseroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private MeseroRepository meseroRepository;

    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String dni, @RequestParam String password, HttpSession session, Model model) {
        
       
        Optional<Admin> adminOpt = adminRepository.findById(dni.trim());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(password)) {
                session.setAttribute("usuarioLogueado", admin);
                return "redirect:/admin/dashboard";
            }
        }

        
        Optional<Mesero> meseroOpt = meseroRepository.findById(dni.trim());
        if (meseroOpt.isPresent()) {
            Mesero mesero = meseroOpt.get();
            if (mesero.getPassword().equals(password)) {
                session.setAttribute("usuarioLogueado", mesero);
                return "redirect:/mesero/ventas";
            }
        }

        
        model.addAttribute("error", "DNI o contraseña incorrectos");
        return "login";
    }
}