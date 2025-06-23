package Servicio.Microservicio.Prestamos.webclient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class PedidoPed {

    private final WebClient webClient;

    public PedidoPed(@Value("${libro-service.url}") String pedidoServiceurl){
        this.webClient = WebClient.builder().baseUrl(pedidoServiceurl).build();
    }

    //Metodo para realizar la consulta getmaping
    public Map<String, Object> getLibroById(Long idLibroStock){
        return this.webClient.get()
        .uri("/{id}",idLibroStock)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), 
        response -> response.bodyToMono(String.class)
        .map(body -> new RuntimeException("Libro no encontrado")))
        .bodyToMono(Map.class).block();
    }
}
