package Servicio.Microservicio.Recomendacion.Lectura.WebClient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ValidacionClient {

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    public ValidacionResponse validarUsuario(String correo, String contrasena) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/autenticacion/validar")
                        .queryParam("correo", correo)
                        .queryParam("contrasena", contrasena)
                        .build())
                .retrieve()
                .bodyToMono(ValidacionResponse.class)
                .block();
    }

    
}
