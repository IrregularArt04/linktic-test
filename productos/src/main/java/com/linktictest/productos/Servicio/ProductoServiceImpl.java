package com.linktictest.productos.Servicio;

import com.linktictest.productos.DTO.ProductoDTO;
import com.linktictest.productos.Entidades.ProductoEntity;
import com.linktictest.productos.Repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public ProductoDTO crearProducto(ProductoDTO dto) {
        ProductoEntity entity = toEntity(dto);
        entity.setId(null); // Asegurar que siempre sea una nueva inserción
        ProductoEntity saved = productoRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public Optional<ProductoDTO> consultarPorId(Long id) {
        return productoRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductoDTO toDTO(ProductoEntity entity) {
        if (entity == null) return null;
        ProductoDTO dto = new ProductoDTO();
        dto.setId(entity.getId());
        dto.setCodigoBarras(entity.getCodigoBarras());
        dto.setNombre(entity.getNombre());
        dto.setPrecio(entity.getPrecio());
        dto.setDescripcion(entity.getDescripcion());
        dto.setCreadoEn(entity.getCreadoEn());
        return dto;
    }

    private ProductoEntity toEntity(ProductoDTO dto) {
        if (dto == null) return null;
        ProductoEntity entity = new ProductoEntity();
        entity.setId(dto.getId());
        entity.setCodigoBarras(dto.getCodigoBarras());
        entity.setNombre(dto.getNombre());
        entity.setPrecio(dto.getPrecio());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCreadoEn(dto.getCreadoEn());
        return entity;
    }
}
