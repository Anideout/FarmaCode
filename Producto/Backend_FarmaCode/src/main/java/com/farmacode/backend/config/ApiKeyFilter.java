package com.farmacode.backend.config;

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

    @Value("${app.api.key:}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Permitir Swagger, health check y auth sin autenticación
        if (path.startsWith("/swagger-ui") || path.startsWith("/api-docs")
                || path.equals("/") || path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (apiKey.isBlank()) {
            // Si no hay key configurada, se permite todo (modo desarrollo local)
            filterChain.doFilter(request, response);
            return;
        }

        String headerKey = request.getHeader("X-Api-Key");
        if (apiKey.equals(headerKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso no autorizado\"}");
        }
    }
}
