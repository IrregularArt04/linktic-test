package com.linktictest.inventario.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linktictest.inventario.Cliente.ProductoCliente;
import com.linktictest.inventario.DTO.ProductoDTO;
import com.linktictest.inventario.Entity.CompraEntity;
import com.linktictest.inventario.Entity.InventarioEntity;
import com.linktictest.inventario.Repository.CompraRepository;
import com.linktictest.inventario.Repository.InventarioRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoCliente productoClient;

    @Override
    @Transactional
    public String procesarCompra(Long productoId, Integer cantidad, String apiKey) {
        ProductoDTO producto = productoClient.obtenerProductoPorId(productoId, apiKey);
        if (producto == null) {
            throw new RuntimeException("El producto no existe en el catálogo maestro.");
        }

        // 2. Validar disponibilidad de stock localmente
        InventarioEntity inventario = inventarioRepository.findByProducto(productoId)
                .orElseThrow(() -> new RuntimeException("Producto sin registro de inventario."));

        if (inventario.getCantidad() < cantidad) {
            throw new RuntimeException("Stock insuficiente para realizar la compra.");
        }

        // 3. Modificar el inventario
        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventarioRepository.save(inventario);

        // 4. Registrar la compra con los datos combinados
        CompraEntity compra = new CompraEntity();
        compra.setIdproducto(productoId);
        compra.setCantidadComprada(cantidad);
        compra.setPrecioUnitario(producto.getPrecio()); // Viene del otro microservicio
        compraRepository.save(compra);

        return "Compra procesada con éxito para el producto: " + producto.getNombre();
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioEntity consultarInventario(Long productoId) {
        return inventarioRepository.findByProducto(productoId)
                .orElseThrow(() -> new RuntimeException("Producto sin registro de inventario."));
    }

    @Override
    @Transactional
    public InventarioEntity actualizarCantidad(Long productoId, Integer cantidad) {
        InventarioEntity inventario = inventarioRepository.findByProducto(productoId)
                .orElseGet(() -> {
                    InventarioEntity nuevo = new InventarioEntity();
                    nuevo.setProducto(productoId);
                    return nuevo;
                });
        inventario.setCantidad(cantidad);
        return inventarioRepository.save(inventario);
    }
}
