package Servicio.Microservicio.de.Solicitudes.de.Ayuda.Controller;



import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.ayuda.controller.SolicitudAyudaController;
import com.ayuda.model.SolicitudAyuda;
import com.ayuda.service.SolicitudAyudaService;

import org.springframework.http.MediaType;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(SolicitudAyudaController.class)
public class SolicitudAyudaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudAyudaService service;

    @Test
    void obtenerTodas_retornaListaDeSolicitudes() throws Exception {
        SolicitudAyuda solicitud = new SolicitudAyuda(1, "user@correo.cl", "Consulta", "Ayuda por favor", Date.valueOf("2025-06-21"));
        when(service.obtenerTodas()).thenReturn(List.of(solicitud));

        mockMvc.perform(get("/api/v1/ayuda")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSolicitud").value(1))
                .andExpect(jsonPath("$[0].correoUsuario").value("user@correo.cl"));
    }

    @Test
    void obtenerPorId_existente_retornaSolicitud() throws Exception {
        SolicitudAyuda solicitud = new SolicitudAyuda(2, "otro@correo.cl", "Problema", "Sistema caído", Date.valueOf("2025-06-20"));
        when(service.obtenerPorId(2)).thenReturn(Optional.of(solicitud));

        mockMvc.perform(get("/api/v1/ayuda/2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSolicitud").value(2))
                .andExpect(jsonPath("$.asunto").value("Problema"));
    }

    @Test
    void obtenerPorId_noExistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ayuda/99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorCorreoUsuario_retornaSolicitudesFiltradas() throws Exception {
        SolicitudAyuda solicitud = new SolicitudAyuda(3, "filtrado@correo.cl", "Asunto", "Mensaje", Date.valueOf("2025-06-18"));
        when(service.obtenerPorCorreoUsuario("filtrado@correo.cl")).thenReturn(List.of(solicitud));

        mockMvc.perform(get("/api/v1/ayuda/usuario/filtrado@correo.cl")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correoUsuario").value("filtrado@correo.cl"));
    }

    @Test
    void crearSolicitud_valida_retorna201() throws Exception {
        SolicitudAyuda creada = new SolicitudAyuda(4, "nuevo@correo.cl", "Duda", "¿Cómo renovar el libro?", Date.valueOf("2025-06-21"));
        when(service.crear("Duda", "¿Cómo renovar el libro?", "nuevo@correo.cl", "1234")).thenReturn(creada);

        String json = """
            {
                "correo": "nuevo@correo.cl",
                "contrasena": "1234",
                "asunto": "Duda",
                "mensaje": "¿Cómo renovar el libro?"
            }
        """;

        mockMvc.perform(post("/api/v1/ayuda")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSolicitud").value(4))
                .andExpect(jsonPath("$.asunto").value("Duda"));
    }

    @Test
    void actualizarSolicitud_valida_retorna200() throws Exception {
        SolicitudAyuda actualizada = new SolicitudAyuda(5, "admin@correo.cl", "Actualizado", "Texto nuevo", Date.valueOf("2025-06-21"));
        when(service.actualizar(5, "Actualizado", "Texto nuevo", "admin@correo.cl", "adminpass")).thenReturn(actualizada);

        String json = """
            {
                "correo": "admin@correo.cl",
                "contrasena": "adminpass",
                "asunto": "Actualizado",
                "mensaje": "Texto nuevo"
            }
        """;

        mockMvc.perform(put("/api/v1/ayuda/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value("Actualizado"));
    }

    @Test
    void eliminarSolicitud_valida_retorna204() throws Exception {
        doNothing().when(service).eliminarPorId(6, "admin@correo.cl", "adminpass");

        String json = """
            {
                "correo": "admin@correo.cl",
                "contrasena": "adminpass"
            }
        """;

        mockMvc.perform(delete("/api/v1/ayuda/6")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());
    }
}
