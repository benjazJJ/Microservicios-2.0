package Microservicio.Recomendaciones.y.Sugerencias.Controller;

import Microservicio.Recomendaciones.y.Sugerencias.controller.RecomendacionController;
import Microservicio.Recomendaciones.y.Sugerencias.model.RecomendacionesySugerencias;
import Microservicio.Recomendaciones.y.Sugerencias.service.RecomendacionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@WebMvcTest(RecomendacionController.class)
public class RecomendacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecomendacionService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllSugerencias_returnsOkAndHateoasJson() throws Exception {
        List<RecomendacionesySugerencias> lista = Arrays.asList(
                new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Muy bueno", Date.valueOf(LocalDate.now()), 4)
        );

        when(service.obtenerTodas()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/sugerencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.recomendacionesySugerenciasList[0].idEncuesta").value(1))
                .andExpect(jsonPath("$._embedded.recomendacionesySugerenciasList[0].mensaje").value("Muy bueno"))
                .andExpect(jsonPath("$._embedded.recomendacionesySugerenciasList[0]._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));
    }

    @Test
    void getSugerenciaById_returnsOkWithLinks() throws Exception {
        RecomendacionesySugerencias sugerencia = new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Buen libro", Date.valueOf(LocalDate.now()), 4);
        when(service.obtenerPorId(1)).thenReturn(sugerencia);

        mockMvc.perform(get("/api/v1/sugerencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Buen libro"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.todas.href", notNullValue()));
    }

    @Test
    void postSugerencia_returnsCreatedWithLinks() throws Exception {
        Map<String, Object> datos = new HashMap<>();
        datos.put("correo", "a@a.cl");
        datos.put("contrasena", "123");
        datos.put("mensaje", "Buen libro");
        datos.put("puntuacion", 4);

        RecomendacionesySugerencias sugerencia = new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Buen libro", Date.valueOf(LocalDate.now()), 4);
        when(service.crearRecomendacionSiEsValida(datos, "a@a.cl", "123")).thenReturn(sugerencia);

        mockMvc.perform(post("/api/v1/sugerencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Buen libro"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.todas.href", notNullValue()));
    }

    @Test
    void putSugerencia_returnsUpdatedMessage() throws Exception {
        RecomendacionesySugerencias existente = new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Antiguo", Date.valueOf(LocalDate.now()), 2);
        RecomendacionesySugerencias actualizado = new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Nuevo mensaje", Date.valueOf(LocalDate.now()), 5);

        when(service.obtenerPorId(1)).thenReturn(existente);
        when(service.actualizarRecomendacion(existente)).thenReturn(actualizado);

        Map<String, Object> datos = new HashMap<>();
        datos.put("mensaje", "Nuevo mensaje");
        datos.put("puntuacion", 5);

        mockMvc.perform(put("/api/v1/sugerencias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Edición de sugerencia realizada con éxito."));
    }

    @Test
    void deleteSugerencia_returnsSuccessMessage() throws Exception {
        RecomendacionesySugerencias existente = new RecomendacionesySugerencias(1, 2, "a@a.cl", "123", "Eliminar", Date.valueOf(LocalDate.now()), 3);
        when(service.obtenerPorId(1)).thenReturn(existente);

        mockMvc.perform(delete("/api/v1/sugerencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Sugerencia eliminada con éxito"));
    }
}
