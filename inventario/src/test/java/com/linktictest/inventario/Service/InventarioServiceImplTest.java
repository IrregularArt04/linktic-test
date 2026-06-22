package com.linktictest.inventario.Service;

import com.linktictest.inventario.Cliente.ProductoCliente;
import com.linktictest.inventario.DTO.ProductoDTO;
import com.linktictest.inventario.Entity.CompraEntity;
import com.linktictest.inventario.Entity.InventarioEntity;
import com.linktictest.inventario.Repository.CompraRepository;
import com.linktictest.inventario.Repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProductoCliente productoClient;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    public void consultarInventario_Success() {
        Long productoId = 1L;
        InventarioEntity inventario = new InventarioEntity();
        inventario.setProducto(productoId);
        inventario.setCantidad(50);

        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.of(inventario));

        InventarioEntity result = inventarioService.consultarInventario(productoId);

        assertNotNull(result);
        assertEquals(productoId, result.getProducto());
        assertEquals(50, result.getCantidad());
        verify(inventarioRepository, times(1)).findByProducto(productoId);
    }

    @Test
    public void consultarInventario_NotFound() {
        Long productoId = 1L;
        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            inventarioService.consultarInventario(productoId);
        });
        verify(inventarioRepository, times(1)).findByProducto(productoId);
    }

    @Test
    public void actualizarCantidad_ExistingProduct() {
        Long productoId = 1L;
        Integer nuevaCantidad = 100;

        InventarioEntity existente = new InventarioEntity();
        existente.setProducto(productoId);
        existente.setCantidad(20);

        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.of(existente));
        when(inventarioRepository.save(any(InventarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventarioEntity result = inventarioService.actualizarCantidad(productoId, nuevaCantidad);

        assertNotNull(result);
        assertEquals(productoId, result.getProducto());
        assertEquals(nuevaCantidad, result.getCantidad());
        verify(inventarioRepository, times(1)).findByProducto(productoId);
        verify(inventarioRepository, times(1)).save(existente);
    }

    @Test
    public void actualizarCantidad_NewProduct() {
        Long productoId = 2L;
        Integer nuevaCantidad = 30;

        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(InventarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventarioEntity result = inventarioService.actualizarCantidad(productoId, nuevaCantidad);

        assertNotNull(result);
        assertEquals(productoId, result.getProducto());
        assertEquals(nuevaCantidad, result.getCantidad());
        verify(inventarioRepository, times(1)).findByProducto(productoId);
        verify(inventarioRepository, times(1)).save(any(InventarioEntity.class));
    }

    @Test
    public void procesarCompra_Success() {
        Long productoId = 1L;
        Integer cantidadAComprar = 5;
        String apiKey = "test-token";

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(productoId);
        productoDTO.setNombre("Producto de prueba");
        productoDTO.setPrecio(BigDecimal.valueOf(10.50));

        InventarioEntity inventario = new InventarioEntity();
        inventario.setProducto(productoId);
        inventario.setCantidad(10);

        when(productoClient.obtenerProductoPorId(productoId, apiKey)).thenReturn(productoDTO);
        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(compraRepository.save(any(CompraEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = inventarioService.procesarCompra(productoId, cantidadAComprar, apiKey);

        assertNotNull(result);
        assertTrue(result.contains("Compra procesada con éxito"));
        assertTrue(result.contains("Producto de prueba"));
        assertEquals(5, inventario.getCantidad()); // 10 - 5

        verify(productoClient, times(1)).obtenerProductoPorId(productoId, apiKey);
        verify(inventarioRepository, times(1)).findByProducto(productoId);
        verify(inventarioRepository, times(1)).save(inventario);
        verify(compraRepository, times(1)).save(any(CompraEntity.class));
    }

    @Test
    public void procesarCompra_ProductoNotFound() {
        Long productoId = 1L;
        Integer cantidad = 5;
        String apiKey = "test-token";

        when(productoClient.obtenerProductoPorId(productoId, apiKey)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            inventarioService.procesarCompra(productoId, cantidad, apiKey);
        });

        verify(productoClient, times(1)).obtenerProductoPorId(productoId, apiKey);
        verify(inventarioRepository, never()).findByProducto(anyLong());
    }

    @Test
    public void procesarCompra_InsufficientStock() {
        Long productoId = 1L;
        Integer cantidadAComprar = 15;
        String apiKey = "test-token";

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(productoId);
        productoDTO.setNombre("Producto de prueba");
        productoDTO.setPrecio(BigDecimal.valueOf(10.50));

        InventarioEntity inventario = new InventarioEntity();
        inventario.setProducto(productoId);
        inventario.setCantidad(10); // stock 10 < compra 15

        when(productoClient.obtenerProductoPorId(productoId, apiKey)).thenReturn(productoDTO);
        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.of(inventario));

        assertThrows(RuntimeException.class, () -> {
            inventarioService.procesarCompra(productoId, cantidadAComprar, apiKey);
        });

        verify(productoClient, times(1)).obtenerProductoPorId(productoId, apiKey);
        verify(inventarioRepository, times(1)).findByProducto(productoId);
        verify(inventarioRepository, never()).save(any(InventarioEntity.class));
        verify(compraRepository, never()).save(any(CompraEntity.class));
    }

    @Test
    public void procesarCompra_ProductoSinInventario() {
        Long productoId = 1L;
        Integer cantidadAComprar = 5;
        String apiKey = "test-token";

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(productoId);
        productoDTO.setNombre("Producto de prueba");
        productoDTO.setPrecio(BigDecimal.valueOf(10.50));

        when(productoClient.obtenerProductoPorId(productoId, apiKey)).thenReturn(productoDTO);
        when(inventarioRepository.findByProducto(productoId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventarioService.procesarCompra(productoId, cantidadAComprar, apiKey);
        });

        assertEquals("Producto sin registro de inventario.", exception.getMessage());

        verify(productoClient, times(1)).obtenerProductoPorId(productoId, apiKey);
        verify(inventarioRepository, times(1)).findByProducto(productoId);
        verify(inventarioRepository, never()).save(any(InventarioEntity.class));
        verify(compraRepository, never()).save(any(CompraEntity.class));
    }
}
