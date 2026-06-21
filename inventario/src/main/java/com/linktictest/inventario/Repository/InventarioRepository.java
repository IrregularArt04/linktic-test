package com.linktictest.inventario.Repository;

import com.linktictest.inventario.Entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {
    Optional<InventarioEntity> findByProducto(Long producto);
}
