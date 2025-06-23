package Servicio.Microservicio.de.Notificaciones7.Service;

import Servicio.Microservicio.de.Notificaciones7.Model.Notificaciones;
import Servicio.Microservicio.de.Notificaciones7.Repository.NotificacionesRepository;
import Servicio.Microservicio.de.Notificaciones7.WebClient.ValidacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionesService {

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081") // URL del microservicio de autenticación
            .build();

    private ValidacionResponse validarUsuario(String correo, String contrasena) {
        try {
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
                throw new RuntimeException("Credenciales inválidas o usuario no encontrado.");
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar credenciales: " + e.getMessage());
        }
    }

    private void validarAdministradorOBibliotecario(String correo, String contrasena) {
        ValidacionResponse r = validarUsuario(correo, contrasena);
        if (!r.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
            !r.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
            throw new RuntimeException("Acceso denegado: solo ADMINISTRADOR o BIBLIOTECARIO puede realizar esta acción.");
        }
    }

    //ESTE METODO YA FUÉ TESTEADO
    public List<Notificaciones> obtenerTodas() {
        return notificacionesRepository.findAll();
    }
    //ESTE METODO YA FUÉ TESTEADO
    public Optional<Notificaciones> obtenerPorId(int id) {
        return notificacionesRepository.findById(id);
    }

    public Notificaciones crear(Notificaciones notificacion, String correo, String contrasena) {
        validarAdministradorOBibliotecario(correo, contrasena);
        return notificacionesRepository.save(notificacion);
    }

    public Notificaciones actualizar(int id, Notificaciones notificacionActualizada, String correo, String contrasena) {
        validarAdministradorOBibliotecario(correo, contrasena);
        notificacionActualizada.setId(id);
        return notificacionesRepository.save(notificacionActualizada);
    }

    public String eliminar(int id, String correo, String contrasena) {
        validarAdministradorOBibliotecario(correo, contrasena);
        if (notificacionesRepository.existsById(id)) {
            notificacionesRepository.deleteById(id);
            return "Notificación eliminada correctamente";
        } else {
            return "No se encontró la notificación con el ID proporcionado";
        }
    }
    //ESTE METODO YA FUÉ TESTEADO
    public List<Notificaciones> obtenerPorEmisor(String correoEmisor) {
        return notificacionesRepository.findByCorreoEmisor(correoEmisor);
    }
    //ESTE METODO YA FUÉ TESTEADO
    public List<Notificaciones> obtenerPorReceptor(String correoReceptor) {
        return notificacionesRepository.findByCorreoReceptor(correoReceptor);
    }
}
