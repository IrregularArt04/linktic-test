package com.linktictest.productos.Controladores;

import com.linktictest.productos.DTO.ProductoDTO;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface ProductoController {
    ResponseEntity<ProductoDTO> crearProducto(ProductoDTO producto);
    ResponseEntity<ProductoDTO> consultarPorId(Long id);
    ResponseEntity<List<ProductoDTO>> listarTodos();
}
