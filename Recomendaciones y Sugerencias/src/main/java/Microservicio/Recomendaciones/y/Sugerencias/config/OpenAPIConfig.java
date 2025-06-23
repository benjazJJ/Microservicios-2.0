package Microservicio.Recomendaciones.y.Sugerencias.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI apiConfig(){
        return new OpenAPI()
        .info( new Info()
        .title("Recomendaciones y Sugerenciasd de la Biblioteca API")
        .version("1.0")
        .description("Documentación de los Endpoint de Recomendaciones y Sugerencias de la Biblioteca"));

    }


}
