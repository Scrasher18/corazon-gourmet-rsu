package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Categoria;
import com.rsu.peru.corazon.gourmet.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> listarTodos() {
        return menuRepository.findAll();
    }

    public List<Menu> listarPorCategoria(Categoria categoria) {
        return menuRepository.findByCategoria(categoria);
    }

    public Optional<Menu> buscarPorId(Long id) {
        return menuRepository.findById(id);
    }

    @Transactional
    public Menu registrarMenu(Menu menu) {
        if (menu.getStockDisponible() == null) {
            menu.setStockDisponible(1);
        }

        if (menu.getCategoria() == Categoria.ENTRADA || menu.getCategoria() == Categoria.BEBIDA) {
            menu.setPrecioNormal(0.0);
        }

        if (menu.getPrecioConadis() == null) {
            menu.setPrecioConadis(0.0);
        }

        return menuRepository.save(menu);
    }

    @Transactional
    public Menu actualizarMenu(Long id, Menu menuDetalles) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato o bebida no encontrado con el ID: " + id));

        menu.setNombreItem(menuDetalles.getNombreItem());
        menu.setCategoria(menuDetalles.getCategoria());
        
        if (menuDetalles.getCategoria() == Categoria.ENTRADA || menuDetalles.getCategoria() == Categoria.BEBIDA) {
            menu.setPrecioNormal(0.0);
        } else {
            menu.setPrecioNormal(menuDetalles.getPrecioNormal());
        }

        menu.setPrecioConadis(menuDetalles.getPrecioConadis() != null ? menuDetalles.getPrecioConadis() : 0.0);
        menu.setStockDisponible(menuDetalles.getStockDisponible() != null ? menuDetalles.getStockDisponible() : 1);

        return menuRepository.save(menu);
    }

    @Transactional
    public void eliminarMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato o bebida no encontrado con el ID: " + id));
        menuRepository.delete(menu);
    }
}