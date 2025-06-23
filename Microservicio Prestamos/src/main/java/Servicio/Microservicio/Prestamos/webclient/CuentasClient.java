package Servicio.Microservicio.Prestamos.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CuentasClient {

    private final WebClient webClient;

    public CuentasClient(@Value("${cuentas-service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public boolean validarUsuarioPorId(Integer idUsuario) {
        try {
            webClient.get()
                    .uri("/usuario/" + idUsuario)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("No existe un usuario con el ID: " + idUsuario)))
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean validarUsuarioPorRut(String rut) {
        try {
            webClient.get()
                    .uri("/usuario/rut/" + rut)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("No existe un usuario con el RUT: " + rut)))
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
