package Servicio.Microservicio.Prestamos.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import Servicio.Microservicio.Prestamos.Model.Prestamo;
import Servicio.Microservicio.Prestamos.Repository.PrestamoRepository;
import Servicio.Microservicio.Prestamos.webclient.PedidoPed;
import Servicio.Microservicio.Prestamos.webclient.CuentasClient;
import Servicio.Microservicio.Prestamos.Service.PrestamoService.ValidacionResponse;


@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private PedidoPed PedidoPed;

    @Autowired
    private CuentasClient cuentasClient;

    public Prestamo crearPrestamoSiEsValido(Map<String, Object> datos, String correo, String contrasena) {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();

        ValidacionResponse respuesta = client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/autenticacion/validar")
                        .queryParam("correo", correo)
                        .queryParam("contrasena", contrasena)
                        .build())
                .retrieve()
                .bodyToMono(ValidacionResponse.class)
                .block();

        if (respuesta == null || !respuesta.autenticado) {
            throw new IllegalArgumentException("Credenciales inválidas o usuario no encontrado");
        }

        if (!(respuesta.rol.equalsIgnoreCase("ESTUDIANTE") || respuesta.rol.equalsIgnoreCase("DOCENTE"))) {
            throw new IllegalArgumentException("Solo estudiantes o docentes pueden solicitar préstamos");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setIdUsuario(respuesta.idUsuario);
        prestamo.setRunSolicitante((String) datos.get("runSolicitante"));
        prestamo.setIdLibro(Long.valueOf(datos.get("idLibro").toString()));
        prestamo.setCantidadDias((int) datos.get("cantidadDias"));
        prestamo.setFechaSolicitud(Date.valueOf(LocalDate.now()));
        prestamo.setFechaEntrega(null);

        return crearPrestamo(prestamo);
    }

    public Prestamo crearPrestamo(Prestamo prestamo) {
        if (!cuentasClient.validarUsuarioPorId(prestamo.getIdUsuario())) {
            throw new RuntimeException("No existe un usuario con el ID: " + prestamo.getIdUsuario());
        }

        if (!cuentasClient.validarUsuarioPorRut(prestamo.getRunSolicitante())) {
            throw new RuntimeException("No existe un usuario con el RUT: " + prestamo.getRunSolicitante());
        }

        if (prestamoRepository.existsByRunSolicitante(prestamo.getRunSolicitante())) {
            throw new RuntimeException("Ya existe un préstamo asignado al RUT: " + prestamo.getRunSolicitante());
        }

        Map<String, Object> libro = PedidoPed.getLibroById(prestamo.getIdLibro());

        if (libro == null || libro.isEmpty()) {
            throw new RuntimeException("Libro no encontrado. No se puede registrar el préstamo.");
        }

        int cantidadActual = Integer.parseInt(libro.get("cantidad").toString());
        if (cantidadActual < 1) {
            throw new RuntimeException("El libro no está disponible. No hay stock suficiente.");
        }

        // Descontar 1 del stock
        int nuevaCantidad = cantidadActual - 1;
        String urlStock = "http://localhost:8087/api/v1/librostock/actualizar-stock/" + prestamo.getIdLibro();

        WebClient.create()
            .put()
            .uri(urlStock)
            .bodyValue(Map.of("cantidad", nuevaCantidad))
            .retrieve()
            .bodyToMono(Void.class)
            .block();

        return prestamoRepository.save(prestamo);
    }

    public List<Prestamo> obtenerTodosLosPrestamos() {
        return prestamoRepository.findAll();
    }

    public Prestamo obtenerPrestamoPorId(Integer idPrestamo) {
        return prestamoRepository.findById(idPrestamo).orElse(null);
    }

    public List<Prestamo> obtenerPrestamosPorRun(String runSolicitante) {
        return prestamoRepository.findByRunSolicitante(runSolicitante);
    }

    public List<Prestamo> obtenerPrestamoPendientes() {
        return prestamoRepository.findByFechaEntregaIsNull();
    }

    public Prestamo actualizarPrestamo(Prestamo prestamo) {
        return prestamoRepository.save(prestamo);
    }

    public void eliminarPrestamo(Integer idPrestamo) {
        prestamoRepository.deleteById(idPrestamo);
    }

    public static class ValidacionResponse {
        public boolean autenticado;
        public int idUsuario;
        public String correo;
        public String rol;
    }
}
