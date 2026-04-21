package com.farmacode.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque del backend de FarmaCode.
 * Inicia el contexto de Spring Boot y levanta el servidor embebido Tomcat.
 */
@SpringBootApplication
public class BackendFarmacodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendFarmacodeApplication.class, args);
    }
}
