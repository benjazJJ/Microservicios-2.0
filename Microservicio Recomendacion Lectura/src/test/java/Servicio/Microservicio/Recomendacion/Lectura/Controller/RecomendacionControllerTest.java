package Servicio.Microservicio.Recomendacion.Lectura.Controller;

import Servicio.Microservicio.Recomendacion.Lectura.controller.RecomendacionController;
import Servicio.Microservicio.Recomendacion.Lectura.model.Recomendacion;
import Servicio.Microservicio.Recomendacion.Lectura.service.RecomendacionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecomendacionController.class)
public class RecomendacionControllerTest {

    @MockBean
    private RecomendacionService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void obtenerTodas_deberiaRetornarOKYListaJson() throws Exception {
        List<Recomendacion> lista = List.of(new Recomendacion(1, "1984", "George Orwell", "Ficción", "Gran libro"));
        when(service.obtenerTodas()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/recomendaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerPorId_deberiaRetornarOKSiExiste() throws Exception {
        Recomendacion r = new Recomendacion(2, "Clean Code", "Robert C. Martin", "Programación", "Lectura obligada");
        when(service.obtenerPorId(2)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/v1/recomendaciones/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void obtenerPorId_deberiaRetornar404SiNoExiste() throws Exception {
        when(service.obtenerPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/recomendaciones/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorCategoria_deberiaRetornarOKYFiltrado() throws Exception {
        List<Recomendacion> lista = List.of(
                new Recomendacion(3, "El Principito", "Saint-Exupéry", "Infantil", "Para reflexionar"));
        when(service.obtenerPorCategoria("Infantil")).thenReturn(lista);

        mockMvc.perform(get("/api/v1/recomendaciones/categoria/Infantil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Infantil"));
    }

    @Test
    void crear_deberiaRetornarOK() throws Exception {
        Recomendacion r = new Recomendacion(4, "Java Básico", "Juan Pérez", "Programación", "Fácil de entender");
        Map<String, Object> body = new HashMap<>();
        body.put("correo", "estudiante@correo.cl");
        body.put("contrasena", "1234");
        body.put("tituloLibro", r.getTituloLibro());
        body.put("autor", r.getAutor());
        body.put("categoria", r.getCategoria());
        body.put("motivo", r.getMotivo());

        when(service.guardar(any(Recomendacion.class), eq("estudiante@correo.cl"), eq("1234")))
                .thenReturn(r);

        mockMvc.perform(post("/api/v1/recomendaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tituloLibro").value("Java Básico"));
    }

    @Test
    void eliminar_deberiaRetornarOK() throws Exception {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("correo", "admin@correo.cl");
        credenciales.put("contrasena", "123456");

        mockMvc.perform(delete("/api/v1/recomendaciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isOk());
    }
}
