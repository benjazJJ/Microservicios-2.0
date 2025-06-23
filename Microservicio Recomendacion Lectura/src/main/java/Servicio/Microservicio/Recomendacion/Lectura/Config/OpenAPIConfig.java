package Servicio.Microservicio.Recomendacion.Lectura.Config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI apiConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recomendación Lectura API")
                        .version("1.0")
                        .description("Documentación de endpoints del microservicio de Recomendación de Lectura"));
    }
}
