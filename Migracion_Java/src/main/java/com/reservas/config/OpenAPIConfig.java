package com.reservas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema de Gestion de Reservas")
                        .version("1.0.0")
                        .description(
                                "API REST para gestion de reservas con modulo predictor de analisis estadistico. Proyecto de migracion Java 8 a Java 17 + Spring Boot 3.2.0")
                        .contact(new Contact()
                                .name("Universidad de Buenos Aires")
                                .email("tu-email@ejemplo.com")));
    }
}