package com.linktictest.productos.Controladores;

import com.linktictest.productos.DTO.ProductoDTO;
import com.linktictest.productos.Servicio.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductoControllerImpl implements ProductoController {

    @Autowired
    private ProductoService productoService;

    @Override
    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un producto en la base de datos y retorna el producto creado como DTO.")
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO producto) {
        ProductoDTO nuevo = productoService.crearProducto(producto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Consultar producto por ID", description = "Busca un producto por su ID único. Retorna 404 si no es encontrado.")
    public ResponseEntity<ProductoDTO> consultarPorId(@PathVariable Long id) {
        return productoService.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna una lista con todos los productos registrados en formato DTO.")
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }
}
