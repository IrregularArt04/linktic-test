package com.linktictest.inventario.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {
    private Long productoId;
    private Integer cantidad;
}
