package com.example.Microservicio.de.Stock.Controller;

import com.example.Microservicio.de.Stock.Model.Categoria;
import com.example.Microservicio.de.Stock.Model.EstadoLibro;
import com.example.Microservicio.de.Stock.Model.LibroStock;
import com.example.Microservicio.de.Stock.Repository.EstadoLibroRepository;
import com.example.Microservicio.de.Stock.Repository.LibroStockRepository;
import com.example.Microservicio.de.Stock.Service.LibroStockService;
import com.example.Microservicio.de.Stock.Service.ValidacionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LibroStockController.class)
public class LibroStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibroStockRepository libroStockRepository;

    @MockBean
    private EstadoLibroRepository estadoLibroRepository;

    @MockBean
    private LibroStockService libroStockService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void obtenerTodos_deberiaRetornarOKYListaJson() throws Exception {
        Categoria categoria = new Categoria(1L, "Novela");
        EstadoLibro estado = new EstadoLibro(1L, "Disponible");
        List<LibroStock> lista = List.of(
                new LibroStock(1L, "El Quijote", "A1", "2", 5, categoria, estado)
        );

        when(libroStockRepository.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/librostock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.libroStockList[0].nombreLibro").value("El Quijote"))
                .andExpect(jsonPath("$._embedded.libroStockList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.libroStockList[0].estante").value("A1"));
    }

    @Test
    void obtenerPorId_existente_deberiaRetornarOK() throws Exception {
        LibroStock libro = new LibroStock(1L, "1984", "D1", "4", 7, new Categoria(), new EstadoLibro());
        when(libroStockRepository.findById(1L)).thenReturn(Optional.of(libro));

        mockMvc.perform(get("/api/v1/librostock/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreLibro").value("1984"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos.href").exists());
    }

    @Test
    void obtenerPorId_noExiste_deberiaRetornar404() throws Exception {
        when(libroStockRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/librostock/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearLibroStock_autorizado_deberiaRetornarLibro() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("correo", "admin@mail.com");
        body.put("contrasena", "123456");
        body.put("nombreLibro", "Nuevo Libro");
        body.put("estante", "Z1");
        body.put("fila", "B");
        body.put("cantidad", 3);
        body.put("categoria", "Novela");
        body.put("estado", "Disponible");

        ValidacionResponse valido = new ValidacionResponse(true, "ADMINISTRADOR");
        LibroStock libro = new LibroStock(1L, "Nuevo Libro", "Z1", "B", 3, new Categoria(), new EstadoLibro());

        when(libroStockService.validarUsuario("admin@mail.com", "123456")).thenReturn(valido);
        when(libroStockService.mapToLibroStock(anyMap())).thenReturn(libro);
        when(libroStockRepository.findAll()).thenReturn(List.of());
        when(libroStockRepository.save(any())).thenReturn(libro);

        mockMvc.perform(post("/api/v1/librostock/crear")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreLibro").value("Nuevo Libro"));
    }

    @Test
    void eliminarLibro_existenteYAutorizado_deberiaRetornarOk() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("correo", "admin@mail.com");
        body.put("contrasena", "123456");

        ValidacionResponse valido = new ValidacionResponse(true, "BIBLIOTECARIO");

        when(libroStockService.validarUsuario("admin@mail.com", "123456")).thenReturn(valido);
        when(libroStockRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/librostock/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().string("Libro eliminado del stock"));
    }

    @Test
    void actualizarCantidadLibro_valido_deberiaRetornarOk() throws Exception {
        LibroStock libro = new LibroStock(1L, "Libro Editado", "B1", "2", 3, new Categoria(), new EstadoLibro());

        when(libroStockRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroStockRepository.save(any())).thenReturn(libro);

        Map<String, Integer> body = new HashMap<>();
        body.put("cantidad", 10);

        mockMvc.perform(put("/api/v1/librostock/actualizar-stock/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cantidad actualizada correctamente"));
    }
}
