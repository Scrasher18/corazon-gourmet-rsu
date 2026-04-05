
package com.rsu.peru.corazon.gourmet.service;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    public void registrarPedido(Pedido pedido){
        pedidoRepository.save(pedido);
    }
}
