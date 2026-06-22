package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {

    Optional<CierreCaja> findByEstado(String estado); 

    List<CierreCaja> findByCajeroDni(String dni);
    
    Optional<CierreCaja> findByEstadoAndCajeroDni(String estado, String dni);
    
    List<CierreCaja> findAllByOrderByFechaAperturaDesc();
}