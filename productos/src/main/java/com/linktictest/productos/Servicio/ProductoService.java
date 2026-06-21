package com.linktictest.productos.Servicio;

import com.linktictest.productos.DTO.ProductoDTO;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ProductoDTO crearProducto(ProductoDTO producto);
    Optional<ProductoDTO> consultarPorId(Long id);
    List<ProductoDTO> listarTodos();
}
