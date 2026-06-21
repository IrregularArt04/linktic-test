package com.linktictest.productos.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private LocalDateTime creadoEn;
}
