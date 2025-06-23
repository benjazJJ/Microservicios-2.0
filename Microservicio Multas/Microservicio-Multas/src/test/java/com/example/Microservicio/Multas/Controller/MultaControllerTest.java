package com.example.Microservicio.Multas.Controller;

import com.example.Microservicio.Multas.Model.Multa;
import com.example.Microservicio.Multas.Service.MultaService;
import com.example.Microservicio.Multas.WebClient.ValidacionResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MultaController.class)
public class MultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MultaService multaService;

    @Test
    void listarTodas_retornaMultasParaEstudiante() throws Exception {
        ValidacionResponse auth = new ValidacionResponse(true, 0, "correo@correo.cl", "ESTUDIANTE");
        List<Multa> lista = List.of(new Multa(1L, "12345678-9", "Retraso", "Bloqueo 7 días", 1));

        when(multaService.validarUsuario("correo@correo.cl", "1234")).thenReturn(auth);
        when(multaService.obtenerTodasLasMultas()).thenReturn(lista);

        String json = "{\"correo\":\"correo@correo.cl\",\"contrasena\":\"1234\"}";

        mockMvc.perform(post("/api/v1/multas/listar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerPorId_retornaMultaParaDocente() throws Exception {
        ValidacionResponse auth = new ValidacionResponse(true, 1, "correo@correo.cl", "DOCENTE");
        Multa multa = new Multa(2L, "22222222-2", "Grave", "Suspensión", 2);

        when(multaService.validarUsuario("correo@correo.cl", "1234")).thenReturn(auth);
        when(multaService.obtenerMultaPorId(2L)).thenReturn(Optional.of(multa));

        String json = "{\"correo\":\"correo@correo.cl\",\"contrasena\":\"1234\"}";

        mockMvc.perform(post("/api/v1/multas/ver/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void crearMulta_retorna201ParaAdmin() throws Exception {
        ValidacionResponse auth = new ValidacionResponse(true, 2, "admin@correo.cl", "ADMINISTRADOR");
        Multa nueva = new Multa(10L, "88888888-8", "Grave", "Bloqueo indefinido", 4);

        when(multaService.validarUsuario("admin@correo.cl", "admin123")).thenReturn(auth);
        when(multaService.crearMulta(any(Multa.class), eq("admin@correo.cl"), eq("admin123"))).thenReturn(nueva);

        String json = "{" +
                "\"correo\":\"admin@correo.cl\"," +
                "\"contrasena\":\"admin123\"," +
                "\"runUsuario\":\"88888888-8\"," +
                "\"tipoSancion\":\"Grave\"," +
                "\"sancion\":\"Bloqueo indefinido\"," +
                "\"idDevolucion\":4}";

        mockMvc.perform(post("/api/v1/multas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void actualizarMulta_retornaOKParaBiblio() throws Exception {
        ValidacionResponse auth = new ValidacionResponse(true, 3, "biblio@correo.cl", "BIBLIOTECARIO");
        Multa actualizada = new Multa(5L, "99999999-9", "Media", "Bloqueo 3 días", 3);

        when(multaService.validarUsuario("biblio@correo.cl", "1234")).thenReturn(auth);
        when(multaService.actualizarMulta(eq(5L), any(Multa.class))).thenReturn(Optional.of(actualizada));

        String json = "{" +
                "\"correo\":\"biblio@correo.cl\"," +
                "\"contrasena\":\"1234\"," +
                "\"runUsuario\":\"99999999-9\"," +
                "\"tipoSancion\":\"Media\"," +
                "\"sancion\":\"Bloqueo 3 días\"}";

        mockMvc.perform(put("/api/v1/multas/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sancion").value("Bloqueo 3 días"));
    }

    @Test
    void eliminarMulta_retornaConfirmacion() throws Exception {
        ValidacionResponse auth = new ValidacionResponse(true, 4, "admin@correo.cl", "ADMINISTRADOR");

        when(multaService.validarUsuario("admin@correo.cl", "admin123")).thenReturn(auth);
        when(multaService.eliminarMulta(6L)).thenReturn(true);

        String json = "{\"correo\":\"admin@correo.cl\",\"contrasena\":\"admin123\"}";

        mockMvc.perform(delete("/api/v1/multas/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Multa eliminada correctamente."));
    }
}