package Servicio.Microservicio.Recomendacion.Lectura.service;

import Servicio.Microservicio.Recomendacion.Lectura.model.Recomendacion;
import Servicio.Microservicio.Recomendacion.Lectura.repository.RecomendacionRepository;
import Servicio.Microservicio.Recomendacion.Lectura.WebClient.ValidacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class RecomendacionService {

    @Autowired
    private RecomendacionRepository repo;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081") // microservicio de autenticación
            .build();

    // POST: solo DOCENTE o ESTUDIANTE
    public Recomendacion guardar(Recomendacion r, String correo, String contrasena) {
        ValidacionResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/autenticacion/validar")
                        .queryParam("correo", correo)
                        .queryParam("contrasena", contrasena)
                        .build())
                .retrieve()
                .bodyToMono(ValidacionResponse.class)
                .block();

        if (response == null || !response.isAutenticado()) {
            throw new RuntimeException("Credenciales inválidas.");
        }

        if (!response.getRol().equalsIgnoreCase("ESTUDIANTE") &&
            !response.getRol().equalsIgnoreCase("DOCENTE")) {
            throw new RuntimeException("Solo estudiantes o docentes pueden recomendar libros.");
        }

        return repo.save(r);
    }

    public List<Recomendacion> obtenerTodas() {
        return repo.findAll();
    }

    public Optional<Recomendacion> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public List<Recomendacion> obtenerPorCategoria(String categoria) {
        return repo.findByCategoria(categoria);
    }

    // DELETE: solo ADMINISTRADOR o BIBLIOTECARIO
    public void eliminar(int id, String correo, String contrasena) throws Exception {
        ValidacionResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/autenticacion/validar")
                        .queryParam("correo", correo)
                        .queryParam("contrasena", contrasena)
                        .build())
                .retrieve()
                .bodyToMono(ValidacionResponse.class)
                .block();

        if (response == null || !response.isAutenticado()) {
            throw new RuntimeException("Credenciales inválidas.");
        }

        if (!response.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
            !response.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
            throw new RuntimeException("No tienes permisos para eliminar recomendaciones.");
        }

        repo.deleteById(id);
    }
}
