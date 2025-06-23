package Servicio.Microservicio.de.Devolucion6.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import Servicio.Microservicio.de.Devolucion6.Model.Devolucion;
import Servicio.Microservicio.de.Devolucion6.Repository.DevolucionRepository;
import Servicio.Microservicio.de.Devolucion6.WebClient.DevolucionDev;
import Servicio.Microservicio.de.Devolucion6.WebClient.ValidacionResponse;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DevolucionService {

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private DevolucionDev devolucionDev;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081")
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
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException(
                                            "Credenciales inválidas o usuario no encontrado.")))
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

    public void validarAdministradorOBibliotecario(String correo, String contrasena) {
        ValidacionResponse response = validarUsuario(correo, contrasena);
        String rol = response.getRol().toUpperCase();

        if (!rol.equals("ADMINISTRADOR") && !rol.equals("BIBLIOTECARIO")) {
            throw new RuntimeException("Acceso denegado: se requiere rol ADMINISTRADOR o BIBLIOTECARIO.");
        }
    }

    //ESTE METODO YA FUÉ PROBADO CON @TEST 
    public List<Devolucion> listarDevoluciones() {
        return devolucionRepository.findAll();
    }

    //ESTE METODO YA FUÉ TESTEADO
    public Devolucion buscarDevolucionPorID(Integer id) {
        return devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
    }

    public Devolucion crearDevolucion(Devolucion devolucion, String correo, String contrasena) {
        ValidacionResponse response = validarUsuario(correo, contrasena);
        String rol = response.getRol().toUpperCase();

        if (!rol.equals("ESTUDIANTE") && !rol.equals("DOCENTE")) {
            throw new RuntimeException("Solo estudiantes o docentes pueden registrar devoluciones.");
        }

        // Validar que no exista devolución para ese préstamo
        if (devolucionRepository.existsByIdPrestamo(devolucion.getIdPrestamo())) {
            throw new RuntimeException("Ya existe una devolución registrada para este préstamo.");
        }

        // Consultar datos del préstamo
        Map<String, Object> prestamo = devolucionDev.getPrestamoById(devolucion.getIdPrestamo());
        if (prestamo == null || prestamo.isEmpty()) {
            throw new RuntimeException("Préstamo no encontrado. No se puede agregar la devolución.");
        }

        // Extraer el ID del usuario desde el préstamo
        Integer idUsuarioDelPrestamo = (Integer) prestamo.get("idUsuario");

        // Comparar con el usuario autenticado
        if (!idUsuarioDelPrestamo.equals(response.getIdUsuario())) {
            throw new RuntimeException("El préstamo no pertenece al usuario autenticado.");
        }

        return devolucionRepository.save(devolucion);
    }

    public Devolucion actualizarDevolucion(Integer id, Devolucion dev, String correo, String contrasena) {
        ValidacionResponse response = validarUsuario(correo, contrasena);
        if (!response.getRol().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new RuntimeException("Solo administradores pueden actualizar devoluciones.");
        }

        Devolucion existente = buscarDevolucionPorID(id);
        existente.setFechaDevolucion(dev.getFechaDevolucion());
        existente.setEstadoLibro(dev.getEstadoLibro());
        existente.setObservaciones(dev.getObservaciones());

        return devolucionRepository.save(existente);
    }
    //ESTE METODO YA FUÉ TESTEADO
    public void borrarDevolucion(Integer idDevolucion) {
        devolucionRepository.deleteById(idDevolucion);
    }
}
