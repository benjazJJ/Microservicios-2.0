package Servicio.Microservicio.de.Devolucion6.Controller;

import Servicio.Microservicio.de.Devolucion6.Model.Devolucion;
import Servicio.Microservicio.de.Devolucion6.Service.DevolucionService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@WebMvcTest(DevolucionController.class)
public class DevolucionControllerTest {

    @MockBean
    private DevolucionService devolucionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllDevoluciones_returnsOkAndJsonWithLinks() throws Exception {
        List<Devolucion> devoluciones = Arrays.asList(
                new Devolucion(1, Date.valueOf("2025-06-01"), "Bueno", null, 10)
        );

        when(devolucionService.listarDevoluciones()).thenReturn(devoluciones);

        mockMvc.perform(get("/api/v1/devoluciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.devolucionList[0].idDevolucion").value(1))
                .andExpect(jsonPath("$._embedded.devolucionList[0].estadoLibro").value("Bueno"))
                .andExpect(jsonPath("$._embedded.devolucionList[0]._links.self.href", notNullValue()));
    }

    @Test
    void getDevolucionPorId_returnsOkAndJsonWithLinks() throws Exception {
        Devolucion devolucion = new Devolucion(2, Date.valueOf("2025-06-15"), "Dañado", "Tapa rota", 20);

        when(devolucionService.buscarDevolucionPorID(2)).thenReturn(devolucion);

        mockMvc.perform(get("/api/v1/devoluciones/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevolucion").value(2))
                .andExpect(jsonPath("$.estadoLibro").value("Dañado"))
                .andExpect(jsonPath("$.observaciones").value("Tapa rota"))
                .andExpect(jsonPath("$._links.self.href", notNullValue()))
                .andExpect(jsonPath("$._links.todas.href", notNullValue()));
    }

    @Test
    void getDevolucionPorId_noExiste_returnsNotFound() throws Exception {
        when(devolucionService.buscarDevolucionPorID(99)).thenThrow(new RuntimeException("No encontrada"));

        mockMvc.perform(get("/api/v1/devoluciones/99"))
                .andExpect(status().isNotFound());
    }
}
