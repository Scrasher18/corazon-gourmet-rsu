package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.model.PedidoItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CarritoService {

    private List<PedidoItem> items = new ArrayList<>();

    public void agregar(PedidoItem item) {
        this.items.add(item);
    }

    public void eliminar(int index) {
        if (index >= 0 && index < items.size()) {
            this.items.remove(index);
        }
    }

    public void limpiar() {
        this.items.clear();
    }

    public List<PedidoItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> {
                 
                    if ("MENU_ECONOMICO".equals(item.getCategoria()) || 
                        "MENU_ESPECIAL".equals(item.getCategoria())) {
                        
                        return item.isEsConadis() ? item.getPrecioConadis() : item.getPrecioNormal();
                    }
                    
                    
                    return 0.0;
                })
                .sum();
    }

    public int getCantidadItems() {
        return items.size();
    }
}