package com.example.Microservicio.Multas.WebClient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MultasMult {
    private final WebClient webClient;
    
    public MultasMult(@Value("${devolucion-service.url}") String multaServicesurl){
        this.webClient = WebClient.builder().baseUrl(multaServicesurl).build();              
    } 
    
    //Metodo para realizar la consulta GetMapping
    public Map<String, Object> getDevolucionById(Integer idDevolucion){
        return this.webClient.get()
        .uri("/{id}",idDevolucion)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), 
        response -> response.bodyToMono(String.class)
        .map(body -> new RuntimeException("Prestamo no encontrado")))
        .bodyToMono(Map.class).block();
    }

}
