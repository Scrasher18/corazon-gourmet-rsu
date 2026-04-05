
package com.rsu.peru.corazon.gourmet.repository;

import com.rsu.peru.corazon.gourmet.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu , Long> {

}   
