package com.linktictest.inventario.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/Inventario/")
public class InventarioController {

    @GetMapping("/GetAll")
    public void getAllInventario(){
        
    }
}
