package com.linktictest.inventario.Filtros;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${apiToken}")
    private String apiToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Aplicamos el filtro únicamente a los endpoints que comiencen con /api/
        if (path.startsWith("/api/")) {
            String requestApiKey = request.getHeader("X-API-KEY");

            if (apiToken == null || !apiToken.equals(requestApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"No autorizado - X-API-KEY inválida o ausente\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
