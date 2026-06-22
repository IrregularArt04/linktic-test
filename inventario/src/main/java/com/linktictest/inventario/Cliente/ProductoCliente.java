package com.linktictest.inventario.Cliente;

import com.linktictest.inventario.DTO.ProductoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import java.time.Duration;

@Component
public class ProductoCliente {

    private final WebClient webClient;
    private final String productosApiUrl;

    public ProductoCliente(@Value("${productosEndpoint}") String productosApiUrl) {
        this.productosApiUrl = productosApiUrl;
        this.webClient = WebClient.builder().baseUrl(productosApiUrl).build();
    }

    public ProductoDTO obtenerProductoPorId(Long productoId, String apiKey) {
        return this.webClient.get()
                .uri("/{id}", productoId)
                .header("X-API-KEY", apiKey) // Requisito de seguridad de la prueba
                .retrieve()
                .bodyToMono(ProductoDTO.class)
                // Implementación de Timeout (ej. 3 segundos)
                .timeout(Duration.ofSeconds(3))
                // Implementación de Reintentos (3 intentos, esperando 1 segundo entre ellos)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block(); // .block() vuelve la llamada sincrónica para adaptarla al flujo transaccional
                          // standard
    }
}
