package com.linktictest.productos.Servicio;

import com.linktictest.productos.Entidades.ProductoEntity;
import com.linktictest.productos.Repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoEntity crearProducto(ProductoEntity producto) {
        return productoRepository.save(producto);
    }

    public Optional<ProductoEntity> consultarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<ProductoEntity> listarTodos() {
        return productoRepository.findAll();
    }
}

