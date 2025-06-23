package Microservicio.Recomendaciones.y.Sugerencias.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import Microservicio.Recomendaciones.y.Sugerencias.model.RecomendacionesySugerencias;
import Microservicio.Recomendaciones.y.Sugerencias.repository.RecomendacionRepository;
import Microservicio.Recomendaciones.y.Sugerencias.webclient.RecomendacionRec;

@Service
public class RecomendacionService {

    @Autowired
    private RecomendacionRepository recomendacionRepository;

    @Autowired
    private RecomendacionRec recomendacionRec;

    public RecomendacionesySugerencias crearRecomendacionSiEsValida(Map<String, Object> datos, String correo,
            String contrasena) {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();

        ValidacionResponse respuesta;

        try {
            respuesta = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/autenticacion/validar")
                            .queryParam("correo", correo)
                            .queryParam("contrasena", contrasena)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.value() == 401, response -> response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new IllegalArgumentException(
                                    "Error 401: El correo o la contraseña ingresados no son válidos, o el usuario no existe."))))
                    .bodyToMono(ValidacionResponse.class)
                    .block();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de autenticación.");
        }

        if (!(respuesta.rol.equalsIgnoreCase("ESTUDIANTE") || respuesta.rol.equalsIgnoreCase("DOCENTE"))) {
            throw new IllegalArgumentException("Solo estudiantes o docentes pueden enviar recomendaciones.");
        }

        if (!recomendacionRec.validarUsuarioPorId(respuesta.idUsuario)) {
            throw new RuntimeException("No existe un usuario con el ID: " + respuesta.idUsuario);
        }

        // Validación de puntuación (1 a 5)
        int puntuacion = Integer.parseInt(datos.get("puntuacion").toString());
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        RecomendacionesySugerencias sugerencia = new RecomendacionesySugerencias();
        sugerencia.setIdUsuario(respuesta.idUsuario);
        sugerencia.setCorreo(correo);
        sugerencia.setContrasena(contrasena);
        sugerencia.setMensaje(datos.get("mensaje").toString());
        sugerencia.setPuntuacion(puntuacion);
        sugerencia.setFechaEnvio(Date.valueOf(LocalDate.now()));

        return crearRecomendacion(sugerencia);
    }

    public RecomendacionesySugerencias crearRecomendacion(RecomendacionesySugerencias sugerencia) {
        if (recomendacionRepository.existsByIdUsuario(sugerencia.getIdUsuario())) {
            throw new RuntimeException("Ya existe una recomendación enviada por este usuario.");
        }

        if (sugerencia.getMensaje() == null || sugerencia.getMensaje().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío.");
        }

        return recomendacionRepository.save(sugerencia);
    }

    public List<RecomendacionesySugerencias> obtenerTodas() {
        return recomendacionRepository.findAll();
    }

    public RecomendacionesySugerencias obtenerPorId(int id) {
        Optional<RecomendacionesySugerencias> sugerencia = recomendacionRepository.findById(id);
        return sugerencia.orElse(null);
    }

    public void eliminarPorId(int id) {
        recomendacionRepository.deleteById(id);
    }

    public RecomendacionesySugerencias actualizarRecomendacion(RecomendacionesySugerencias sugerencia) {
        if (sugerencia.getPuntuacion() < 1 || sugerencia.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        if (sugerencia.getMensaje() == null || sugerencia.getMensaje().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío.");
        }

        return recomendacionRepository.save(sugerencia);
    }

    public static class ValidacionResponse {
        public boolean autenticado;
        public int idUsuario;
        public String correo;
        public String rol;
    }
}
