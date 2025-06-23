package Servicio.Microservicio.de.Devolucion6.WebClient;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DevolucionDev {

    private final WebClient webClient;

    public DevolucionDev(@Value("${prestamo-service.url}") String devolucionServiceurl){
        this.webClient = WebClient.builder().baseUrl(devolucionServiceurl).build();
    }

    //Metodo para realizar la consulta getmaping
    public Map<String, Object> getPrestamoById(Integer idPrestamo){
        return this.webClient.get()
        .uri("/{id}",idPrestamo)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), 
        response -> response.bodyToMono(String.class)
        .map(body -> new RuntimeException("Prestamo no encontrado")))
        .bodyToMono(Map.class).block();
    }

}
