package com.linktictest.inventario.Controller;

import com.linktictest.inventario.DTO.CompraRequestDTO;
import com.linktictest.inventario.Entity.InventarioEntity;
import com.linktictest.inventario.Service.InventarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioControllerImplTest {

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioControllerImpl inventarioController;

    @Test
    public void consultarInventario_Success() {
        Long productoId = 1L;
        InventarioEntity inventario = new InventarioEntity();
        inventario.setProducto(productoId);
        inventario.setCantidad(50);

        when(inventarioService.consultarInventario(productoId)).thenReturn(inventario);

        ResponseEntity<InventarioEntity> response = inventarioController.consultarInventario(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventario, response.getBody());
        verify(inventarioService, times(1)).consultarInventario(productoId);
    }

    @Test
    public void consultarInventario_NotFound() {
        Long productoId = 1L;

        when(inventarioService.consultarInventario(productoId)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<InventarioEntity> response = inventarioController.consultarInventario(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventarioService, times(1)).consultarInventario(productoId);
    }

    @Test
    public void actualizarCantidad_Success() {
        Long productoId = 1L;
        Integer cantidad = 100;
        InventarioEntity inventario = new InventarioEntity();
        inventario.setProducto(productoId);
        inventario.setCantidad(cantidad);

        when(inventarioService.actualizarCantidad(productoId, cantidad)).thenReturn(inventario);

        ResponseEntity<InventarioEntity> response = inventarioController.actualizarCantidad(productoId, cantidad);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventario, response.getBody());
        verify(inventarioService, times(1)).actualizarCantidad(productoId, cantidad);
    }

    @Test
    public void procesarCompra_Success() {
        CompraRequestDTO request = new CompraRequestDTO(1L, 5);
        String apiKey = "test-token";
        String mensajeExito = "Compra procesada con éxito";

        when(inventarioService.procesarCompra(request.getProductoId(), request.getCantidad(), apiKey))
                .thenReturn(mensajeExito);

        ResponseEntity<String> response = inventarioController.procesarCompra(request, apiKey);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mensajeExito, response.getBody());
        verify(inventarioService, times(1)).procesarCompra(request.getProductoId(), request.getCantidad(), apiKey);
    }

    @Test
    public void procesarCompra_BadRequest() {
        CompraRequestDTO request = new CompraRequestDTO(1L, 5);
        String apiKey = "test-token";
        String mensajeError = "Stock insuficiente";

        when(inventarioService.procesarCompra(request.getProductoId(), request.getCantidad(), apiKey))
                .thenThrow(new RuntimeException(mensajeError));

        ResponseEntity<String> response = inventarioController.procesarCompra(request, apiKey);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mensajeError, response.getBody());
        verify(inventarioService, times(1)).procesarCompra(request.getProductoId(), request.getCantidad(), apiKey);
    }
}
