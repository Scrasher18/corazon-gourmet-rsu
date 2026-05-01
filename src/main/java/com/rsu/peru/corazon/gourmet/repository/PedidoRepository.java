package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findAllByOrderByFechaDesc();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE CAST(p.fecha AS date) = CURRENT_DATE")
    long countPedidosHoy();

    @Query("SELECT SUM(p.montoTotal) FROM Pedido p WHERE CAST(p.fecha AS date) = CURRENT_DATE")
    Double sumarRecaudacionHoy();
}