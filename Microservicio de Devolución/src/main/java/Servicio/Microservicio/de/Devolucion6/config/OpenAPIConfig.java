package Servicio.Microservicio.de.Devolucion6.config;

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
        .title("Devolucion Biblioteca API")
        .version("1.0")
        .description("Documentación de los Endpoint de Devolucion de Libros"));

    }


}
