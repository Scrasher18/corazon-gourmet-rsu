package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    Optional<Pedido> findByMesaAndEstado(int mesa, String estado);

 
    @Query("SELECT p FROM Pedido p WHERE p.mesa = :mesa AND p.estado = 'ABIERTA'")
    Optional<Pedido> findMesaActivaParaCobro(@Param("mesa") int mesa);

    // Métodos de reportes e historial
    List<Pedido> findAllByOrderByFechaDesc();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE FUNCTION('DATE', p.fecha) = CURRENT_DATE")
    long countPedidosHoy();

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0.0) FROM Pedido p WHERE FUNCTION('DATE', p.fecha) = CURRENT_DATE")
    Double sumarRecaudacionHoy();
}