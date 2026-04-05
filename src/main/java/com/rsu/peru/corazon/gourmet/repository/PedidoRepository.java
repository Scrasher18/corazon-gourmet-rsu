
package com.rsu.peru.corazon.gourmet.repository;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
