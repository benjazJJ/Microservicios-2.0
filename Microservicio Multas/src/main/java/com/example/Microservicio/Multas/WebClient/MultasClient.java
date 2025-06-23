package com.example.Microservicio.Multas.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class MultasClient {
     private final WebClient webClient;

    public MultasClient(@Value("${cuentas-service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public boolean validarUsuarioPorRut(String rut) {
        try {
            webClient.get()
                .uri("/usuario/rut/" + rut)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError(),
                    response -> Mono.error(new RuntimeException("No existe un usuario con el RUT: " + rut))
                )
                .bodyToMono(Void.class)
                .block();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
