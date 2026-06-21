package com.linktictest.productos.Controladores;

import com.linktictest.productos.DTO.ProductoDTO;
import com.linktictest.productos.Servicio.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerImplTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoControllerImpl productoController;

    @Test
    public void crearProducto_Success() {
        ProductoDTO requestDTO = new ProductoDTO();
        requestDTO.setNombre("Producto");
        requestDTO.setPrecio(BigDecimal.TEN);

        ProductoDTO savedDTO = new ProductoDTO();
        savedDTO.setId(1L);
        savedDTO.setNombre("Producto");
        savedDTO.setPrecio(BigDecimal.TEN);

        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(savedDTO);

        ResponseEntity<ProductoDTO> response = productoController.crearProducto(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedDTO, response.getBody());
        verify(productoService, times(1)).crearProducto(requestDTO);
    }

    @Test
    public void consultarPorId_Success() {
        Long id = 1L;
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre("P1");

        when(productoService.consultarPorId(id)).thenReturn(Optional.of(dto));

        ResponseEntity<ProductoDTO> response = productoController.consultarPorId(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(productoService, times(1)).consultarPorId(id);
    }

    @Test
    public void consultarPorId_NotFound() {
        Long id = 1L;

        when(productoService.consultarPorId(id)).thenReturn(Optional.empty());

        ResponseEntity<ProductoDTO> response = productoController.consultarPorId(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoService, times(1)).consultarPorId(id);
    }

    @Test
    public void listarTodos_Success() {
        ProductoDTO dto1 = new ProductoDTO();
        dto1.setId(1L);
        dto1.setNombre("P1");

        ProductoDTO dto2 = new ProductoDTO();
        dto2.setId(2L);
        dto2.setNombre("P2");

        List<ProductoDTO> list = Arrays.asList(dto1, dto2);

        when(productoService.listarTodos()).thenReturn(list);

        ResponseEntity<List<ProductoDTO>> response = productoController.listarTodos();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productoService, times(1)).listarTodos();
    }

    @Test
    public void listarTodos_Empty() {
        when(productoService.listarTodos()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductoDTO>> response = productoController.listarTodos();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(productoService, times(1)).listarTodos();
    }
}
