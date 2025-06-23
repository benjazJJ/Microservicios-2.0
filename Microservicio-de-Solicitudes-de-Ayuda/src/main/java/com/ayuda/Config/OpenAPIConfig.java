package com.ayuda.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI apiConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ayuda API")
                        .version("1.0")
                        .description("Documentación de endpoints del microservicio de Solicitudes de Ayuda"));
    }
}
