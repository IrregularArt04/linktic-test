package com.linktictest.inventario.Controller;

import com.linktictest.inventario.DTO.CompraRequestDTO;
import com.linktictest.inventario.Entity.InventarioEntity;
import org.springframework.http.ResponseEntity;

public interface InventarioController {

    ResponseEntity<InventarioEntity> consultarInventario(Long productoId);
    ResponseEntity<InventarioEntity> actualizarCantidad(Long productoId, Integer cantidad);
    ResponseEntity<String> procesarCompra(CompraRequestDTO request, String apiKey);
}
