package com.rsu.peru.corazon.gourmet.controller;

import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Categoria;
import com.rsu.peru.corazon.gourmet.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "${app.frontend.url}")
public class MenuRestController {

    private final MenuService menuService;

    public MenuRestController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<Menu>> listarTodos() {
        return ResponseEntity.ok(menuService.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Menu>> listarPorCategoria(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(menuService.listarPorCategoria(categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> buscarPorId(@PathVariable Long id) {
        return menuService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Menu> registrarMenu(@RequestBody Menu menu) {
        return new ResponseEntity<>(menuService.registrarMenu(menu), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> actualizarMenu(@PathVariable Long id, @RequestBody Menu menu) {
        try {
            return ResponseEntity.ok(menuService.actualizarMenu(id, menu));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMenu(@PathVariable Long id) {
        try {
            menuService.eliminarMenu(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}