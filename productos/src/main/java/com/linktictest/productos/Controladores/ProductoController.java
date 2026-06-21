package com.linktictest.productos.Controladores;

import com.linktictest.productos.Entidades.ProductoEntity;
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
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Crea un producto en la base de datos y retorna el producto creado.")
    public ResponseEntity<ProductoEntity> crearProducto(@RequestBody ProductoEntity producto) {
        ProductoEntity nuevo = productoService.crearProducto(producto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar producto por ID", description = "Busca un producto por su ID único. Retorna 404 si no es encontrado.")
    public ResponseEntity<ProductoEntity> consultarPorId(@PathVariable Long id) {
        return productoService.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna una lista con todos los productos registrados.")
    public ResponseEntity<List<ProductoEntity>> listarTodos() {
        List<ProductoEntity> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }
}
