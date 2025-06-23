package com.example.Microservicio.de.Stock.Service;

import com.example.Microservicio.de.Stock.Model.Categoria;
import com.example.Microservicio.de.Stock.Model.EstadoLibro;
import com.example.Microservicio.de.Stock.Model.LibroStock;
import com.example.Microservicio.de.Stock.Repository.CategoriaRepository;
import com.example.Microservicio.de.Stock.Repository.EstadoLibroRepository;
import com.example.Microservicio.de.Stock.Repository.LibroStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LibroStockServiceTest {

    @InjectMocks
    private LibroStockService service;

    @Mock
    private LibroStockRepository libroStockRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EstadoLibroRepository estadoLibroRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void guardarLibroStock_valido_deberiaGuardar() {
        LibroStock libro = new LibroStock(null, "Clean Code", "B1", "2", 5, new Categoria(), new EstadoLibro());
        when(libroStockRepository.save(libro)).thenReturn(libro);
        LibroStock guardado = service.guardarLibroStock(libro);
        assertEquals("Clean Code", guardado.getNombreLibro());
    }

    @Test
    void guardarLibroStock_nulo_deberiaLanzarExcepcion() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.guardarLibroStock(null));
        assertEquals("El libro no puede ser nulo.", e.getMessage());
    }

    @Test
    void actualizar_valido_deberiaActualizar() {
        LibroStock existente = new LibroStock(1L, "Libro Antiguo", "A1", "1", 2, new Categoria(), new EstadoLibro());
        LibroStock actualizado = new LibroStock(null, "Libro Nuevo", "B2", "3", 5, new Categoria(), new EstadoLibro());

        when(libroStockRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(libroStockRepository.save(any())).thenReturn(actualizado);

        Optional<LibroStock> resultado = service.actualizar(1L, actualizado);
        assertTrue(resultado.isPresent());
        assertEquals("Libro Nuevo", resultado.get().getNombreLibro());
    }

    @Test
    void eliminar_conIdInvalido_deberiaLanzarExcepcion() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.eliminar(-1L));
        assertEquals("ID inválido.", e.getMessage());
    }

    @Test
    void eliminar_noExiste_deberiaLanzarExcepcion() {
        when(libroStockRepository.existsById(10L)).thenReturn(false);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.eliminar(10L));
        assertEquals("El libro con ID 10 no existe.", e.getMessage());
    }

    @Test
    void obtenerId_valido_deberiaRetornarLibro() {
        LibroStock libro = new LibroStock(1L, "Libro", "X1", "1", 1, new Categoria(), new EstadoLibro());
        when(libroStockRepository.findById(1L)).thenReturn(Optional.of(libro));
        Optional<LibroStock> resultado = service.obtenerId(1L);
        assertTrue(resultado.isPresent());
    }

    @Test
    void mapToLibroStock_valido_deberiaMapearCorrectamente() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombreLibro", "El Principito");
        datos.put("estante", "Z1");
        datos.put("fila", "A");
        datos.put("cantidad", 3);
        datos.put("categoria", "Infantil");
        datos.put("estado", "Disponible");

        Categoria categoria = new Categoria(1L, "Infantil");
        EstadoLibro estado = new EstadoLibro(1L, "Disponible");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        when(estadoLibroRepository.findAll()).thenReturn(List.of(estado));

        LibroStock resultado = service.mapToLibroStock(datos);
        assertEquals("El Principito", resultado.getNombreLibro());
    }

    @Test
    void validarUsuario_credencialesInvalidas_deberiaRetornarNoAutenticado() {
        // Simula respuesta de WebClient ante credenciales inválidas (Unauthorized)
        LibroStockService spyService = spy(service);
        doReturn(new ValidacionResponse(false, "DESCONOCIDO"))
                .when(spyService)
                .validarUsuario("correo@falso.cl", "claveIncorrecta");

        ValidacionResponse respuesta = spyService.validarUsuario("correo@falso.cl", "claveIncorrecta");

        assertFalse(respuesta.isAutenticado());
        assertEquals("DESCONOCIDO", respuesta.getRol());
    }
}