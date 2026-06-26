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

    @Query(value = "SELECT CAST(HOUR(fecha) AS CHAR) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE DATE(fecha) = DATE(NOW()) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY CAST(HOUR(fecha) AS CHAR) "
            + "ORDER BY CAST(HOUR(fecha) AS CHAR)", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasHoy();

    @Query(value = "SELECT DAY(fecha) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE MONTH(fecha) = MONTH(NOW()) "
            + "AND YEAR(fecha) = YEAR(NOW()) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY etiqueta ORDER BY etiqueta", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasMes();

    @Query(value = "SELECT MONTH(fecha) AS etiqueta, SUM(monto_total) AS total "
            + "FROM pedidos "
            + "WHERE YEAR(fecha) = YEAR(NOW()) "
            + "AND estado = 'PAGADA' "
            + "GROUP BY etiqueta ORDER BY etiqueta", nativeQuery = true)
    List<VentaAgrupadaDTO> obtenerVentasAnio();

    @Query(value = "SELECT COALESCE(SUM(monto_total), 0.0) "
            + "FROM pedidos "
            + "WHERE DATE(fecha) = DATE(NOW()) "
            + "AND estado = 'PAGADA'", nativeQuery = true)
    Double sumarRecaudacionHoy();

    @Query(value = "SELECT COUNT(*) "
            + "FROM pedidos "
            + "WHERE DATE(fecha) = DATE(NOW()) "
            + "AND estado = 'PAGADA'", nativeQuery = true)
    long countPedidosHoy();

    @Query("SELECT SUM(p.montoTotal) FROM Pedido p WHERE p.estado = 'PAGADA' AND p.fecha >= :fechaApertura")
    Double sumarTotalDesdeFecha(@Param("fechaApertura") java.time.LocalDateTime fechaApertura);
}