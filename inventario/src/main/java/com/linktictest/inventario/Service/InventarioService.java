package com.linktictest.inventario.Service;

import com.linktictest.inventario.Entity.InventarioEntity;

public interface InventarioService {
    String procesarCompra(Long productoId, Integer cantidad, String apiKey);
    InventarioEntity consultarInventario(Long productoId);
    InventarioEntity actualizarCantidad(Long productoId, Integer cantidad);
}
