package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.dto.VentaAgrupadaDTO;
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

    List<Pedido> findAllByOrderByFechaDesc();

    List<Pedido> findByEstado(String estado);

    @Query(value = "SELECT CAST(HOUR(fecha - INTERVAL 5 HOUR) AS CHAR) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE DATE(fecha - INTERVAL 5 HOUR) = DATE(NOW() - INTERVAL 5 HOUR) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY CAST(HOUR(fecha - INTERVAL 5 HOUR) AS CHAR) "
            + "ORDER BY CAST(HOUR(fecha - INTERVAL 5 HOUR) AS CHAR)", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasHoy();

    @Query(value = "SELECT DAY(fecha - INTERVAL 5 HOUR) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE MONTH(fecha - INTERVAL 5 HOUR) = MONTH(NOW() - INTERVAL 5 HOUR) "
            + "AND YEAR(fecha - INTERVAL 5 HOUR) = YEAR(NOW() - INTERVAL 5 HOUR) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY etiqueta ORDER BY etiqueta", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasMes();

    @Query(value = "SELECT MONTH(fecha - INTERVAL 5 HOUR) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE YEAR(fecha - INTERVAL 5 HOUR) = YEAR(NOW() - INTERVAL 5 HOUR) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY etiqueta ORDER BY etiqueta", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasAnio();

    @Query(value = "SELECT COALESCE(SUM(monto_total), 0.0) "
            + "FROM pedidos "
            + "WHERE DATE(fecha - INTERVAL 5 HOUR) = DATE(NOW() - INTERVAL 5 HOUR) "
            + "AND estado = 'PAGADA'", nativeQuery = true)
    Double sumarRecaudacionHoy();

    @Query(value = "SELECT COUNT(*) "
            + "FROM pedidos "
            + "WHERE DATE(fecha - INTERVAL 5 HOUR) = DATE(NOW() - INTERVAL 5 HOUR) "
            + "AND estado = 'PAGADA'", nativeQuery = true)
    long countPedidosHoy();

    @Query("SELECT SUM(p.montoTotal) FROM Pedido p WHERE p.estado = 'PAGADA' AND p.fecha >= :fechaApertura")
    Double sumarTotalDesdeFecha(@Param("fechaApertura") java.time.LocalDateTime fechaApertura);
}
