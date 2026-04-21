package com.farmacode.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de SpringDoc OpenAPI (Swagger UI) para la documentación interactiva de la API.
 * Accesible en: http://localhost:8080/swagger-ui.html
 * JSON de la spec en: http://localhost:8080/api-docs
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define los metadatos generales de la API que aparecen en la documentación Swagger.
     *
     * @return instancia de {@link OpenAPI} configurada con título, descripción y versión
     */
    @Bean
    public OpenAPI farmacodeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FarmaCode API")
                        .description("API REST para identificación de medicamentos bioequivalentes en Chile. " +
                                "Permite buscar por nombre comercial o texto OCR, consultar precios comparativos " +
                                "y gestionar el historial de búsquedas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FarmaCode Team")
                                .email("contacto@farmacode.cl")));
    }
}
