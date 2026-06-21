package com.linktictest.inventario.Repository;

import com.linktictest.inventario.Entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {
}
