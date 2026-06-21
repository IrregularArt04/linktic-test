package com.linktictest.inventario.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventarios", indexes = {
    @Index(name = "idx_inventarios_producto", columnList = "producto_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}
