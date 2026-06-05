package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Menu;
import com.rsu.peru.corazon.gourmet.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
   
    
    Optional<Menu> findByNombreItem(String nombreItem);

    List<Menu> findByCategoria(Categoria categoria);
}