package com.linktictest.inventario.Controller;

import com.linktictest.inventario.DTO.CompraRequestDTO;
import com.linktictest.inventario.Entity.InventarioEntity;
import com.linktictest.inventario.Service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "API para la gestión de inventario y flujo de compras")
public class InventarioControllerImpl implements InventarioController {

    @Autowired
    private InventarioService inventarioService;



    @Override
    @GetMapping("/{productoId}")
    @Operation(summary = "Consultar inventario de un producto", description = "Busca y retorna el registro de inventario (stock) asociado al ID del producto especificado.")
    public ResponseEntity<InventarioEntity> consultarInventario(@PathVariable Long productoId) {
        try {
            InventarioEntity inventario = inventarioService.consultarInventario(productoId);
            return ResponseEntity.ok(inventario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @PutMapping("/{productoId}")
    @Operation(summary = "Actualizar cantidad de inventario", description = "Actualiza el stock disponible del inventario para un producto específico.")
    public ResponseEntity<InventarioEntity> actualizarCantidad(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        InventarioEntity inventario = inventarioService.actualizarCantidad(productoId, cantidad);
        return ResponseEntity.ok(inventario);
    }

    @Override
    @PostMapping("/compra")
    @Operation(summary = "Registrar y procesar flujo de compra", description = "Valida la existencia del producto en el Microservicio de Productos, comprueba el stock disponible localmente, descuenta la cantidad solicitada y registra la compra.")
    public ResponseEntity<String> procesarCompra(
            @RequestBody CompraRequestDTO request,
            @RequestHeader(value = "X-API-KEY", required = true) String apiKey) {
        try {
            String resultado = inventarioService.procesarCompra(request.getProductoId(), request.getCantidad(), apiKey);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
