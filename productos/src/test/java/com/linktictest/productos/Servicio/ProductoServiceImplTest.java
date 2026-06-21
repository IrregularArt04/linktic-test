package com.linktictest.productos.Servicio;

import com.linktictest.productos.DTO.ProductoDTO;
import com.linktictest.productos.Entidades.ProductoEntity;
import com.linktictest.productos.Repositorios.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    public void crearProducto_Success() {
        ProductoDTO requestDTO = new ProductoDTO();
        requestDTO.setId(10L); // Tendrá que ser ignorado / forzado a null
        requestDTO.setCodigoBarras("123456789");
        requestDTO.setNombre("Producto Unit Test");
        requestDTO.setPrecio(BigDecimal.valueOf(99.99));
        requestDTO.setDescripcion("Una descripción");

        ProductoEntity savedEntity = new ProductoEntity();
        savedEntity.setId(1L); // Autogenerado por DB
        savedEntity.setCodigoBarras("123456789");
        savedEntity.setNombre("Producto Unit Test");
        savedEntity.setPrecio(BigDecimal.valueOf(99.99));
        savedEntity.setDescripcion("Una descripción");
        savedEntity.setCreadoEn(LocalDateTime.now());

        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(savedEntity);

        ProductoDTO result = productoService.crearProducto(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId()); // Comprueba que se retornó la ID autogenerada
        assertEquals("Producto Unit Test", result.getNombre());
        assertEquals("123456789", result.getCodigoBarras());

        verify(productoRepository, times(1)).save(argThat(entity -> {
            assertNull(entity.getId()); // La ID fue forzada a null antes de guardar
            return true;
        }));
    }

    @Test
    public void consultarPorId_Success() {
        Long id = 1L;
        ProductoEntity entity = new ProductoEntity();
        entity.setId(id);
        entity.setNombre("Nombre Producto");
        entity.setPrecio(BigDecimal.TEN);

        when(productoRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<ProductoDTO> result = productoService.consultarPorId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("Nombre Producto", result.get().getNombre());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    public void consultarPorId_NotFound() {
        Long id = 1L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ProductoDTO> result = productoService.consultarPorId(id);

        assertFalse(result.isPresent());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    public void listarTodos_Success() {
        ProductoEntity entity1 = new ProductoEntity();
        entity1.setId(1L);
        entity1.setNombre("P1");

        ProductoEntity entity2 = new ProductoEntity();
        entity2.setId(2L);
        entity2.setNombre("P2");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<ProductoDTO> result = productoService.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("P1", result.get(0).getNombre());
        assertEquals("P2", result.get(1).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    public void listarTodos_Empty() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductoDTO> result = productoService.listarTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productoRepository, times(1)).findAll();
    }
}
