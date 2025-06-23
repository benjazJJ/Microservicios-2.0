package Servicio.Microservicio.de.Notificaciones7.Controller;

import Servicio.Microservicio.de.Notificaciones7.Model.Notificaciones;
import Servicio.Microservicio.de.Notificaciones7.Service.NotificacionesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de pruebas para el controlador NotificacionesController.
 * Se prueban los endpoints GET utilizando MockMvc.
 */
@WebMvcTest(NotificacionesController.class) // Carga solo el controlador NotificacionesController para testearlo
public class NotificacionesControllerTest {

    @MockBean
    private NotificacionesService notificacionesService; // Se simula el comportamiento del servicio

    @Autowired
    private MockMvc mockMvc; // Se usa para simular peticiones HTTP

    /**
     * Prueba el endpoint GET /api/v1/notificaciones
     * Verifica que se obtengan todas las notificaciones con sus campos y enlaces HATEOAS.
     */
    @Test
    void getAllNotificaciones_returnsOKAndJson() throws Exception {
        List<Notificaciones> lista = Arrays.asList(
                new Notificaciones(1, "Mensaje 1", "INFO", "receptor@correo.cl", "emisor@correo.cl")
        );

        // Simula el resultado del servicio cuando se piden todas las notificaciones
        when(notificacionesService.obtenerTodas()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$._embedded.notificacionesList[0].mensaje").value("Mensaje 1")) // Verifica contenido
                .andExpect(jsonPath("$._embedded.notificacionesList[0].correoReceptor").value("receptor@correo.cl"))
                .andExpect(jsonPath("$._embedded.notificacionesList[0]._links.self.href").exists()); // Verifica link HATEOAS
    }

    /**
     * Prueba el endpoint GET /api/v1/notificaciones/2
     * Verifica que se obtenga una notificación específica con enlaces HATEOAS completos.
     */
    @Test
    void getNotificacionPorId_returnsOK() throws Exception {
        Notificaciones noti = new Notificaciones(2, "Mensaje 2", "ALERTA", "receptor@correo.cl", "emisor@correo.cl");

        when(notificacionesService.obtenerPorId(2)).thenReturn(Optional.of(noti));

        mockMvc.perform(get("/api/v1/notificaciones/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Mensaje 2")) // Verifica que el mensaje sea correcto
                .andExpect(jsonPath("$._links.self.href").exists()) // Link a sí mismo
                .andExpect(jsonPath("$._links.todas.href").exists()) // Link a todas las notificaciones
                .andExpect(jsonPath("$._links.eliminar.href").exists()); // Link al endpoint de eliminación
    }

    /**
     * Prueba el endpoint GET /api/v1/notificaciones/emisor/admin@correo.cl
     * Verifica que se devuelvan correctamente las notificaciones enviadas por un emisor.
     */
    @Test
    void getNotificacionesPorEmisor_returnsOK() throws Exception {
        List<Notificaciones> lista = Arrays.asList(
                new Notificaciones(3, "Mensaje desde emisor", "INFO", "destinatario@correo.cl", "admin@correo.cl")
        );

        when(notificacionesService.obtenerPorEmisor("admin@correo.cl")).thenReturn(lista);

        mockMvc.perform(get("/api/v1/notificaciones/emisor/admin@correo.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificacionesList[0].correoEmisor").value("admin@correo.cl"))
                .andExpect(jsonPath("$._embedded.notificacionesList[0]._links.self.href").exists());
    }

    /**
     * Prueba el endpoint GET /api/v1/notificaciones/receptor/cliente@correo.cl
     * Verifica que se devuelvan las notificaciones recibidas por un receptor específico.
     */
    @Test
    void getNotificacionesPorReceptor_returnsOK() throws Exception {
        List<Notificaciones> lista = Arrays.asList(
                new Notificaciones(4, "Mensaje hacia receptor", "INFO", "cliente@correo.cl", "admin@correo.cl")
        );

        when(notificacionesService.obtenerPorReceptor("cliente@correo.cl")).thenReturn(lista);

        mockMvc.perform(get("/api/v1/notificaciones/receptor/cliente@correo.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificacionesList[0].correoReceptor").value("cliente@correo.cl"))
                .andExpect(jsonPath("$._embedded.notificacionesList[0]._links.self.href").exists());
    }
}
