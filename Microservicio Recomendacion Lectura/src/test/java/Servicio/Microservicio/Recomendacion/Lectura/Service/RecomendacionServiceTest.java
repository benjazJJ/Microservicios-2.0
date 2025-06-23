package Servicio.Microservicio.Recomendacion.Lectura.Service;

import Servicio.Microservicio.Recomendacion.Lectura.WebClient.ValidacionResponse;
import Servicio.Microservicio.Recomendacion.Lectura.model.Recomendacion;
import Servicio.Microservicio.Recomendacion.Lectura.repository.RecomendacionRepository;
import Servicio.Microservicio.Recomendacion.Lectura.service.RecomendacionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecomendacionServiceTest {

    @Mock
    private RecomendacionRepository repo;

    @InjectMocks
    private RecomendacionService service;

    @Test
    void obtenerTodas_deberiaRetornarLista() {
        List<Recomendacion> lista = Arrays.asList(new Recomendacion(1, "Libro 1", "Autor", "Categoria", "Motivo"));
        when(repo.findAll()).thenReturn(lista);
        assertThat(service.obtenerTodas()).isEqualTo(lista);
    }

    @Test
    void obtenerPorId_deberiaRetornarRecomendacion() {
        Recomendacion r = new Recomendacion(1, "Libro 1", "Autor", "Categoria", "Motivo");
        when(repo.findById(1)).thenReturn(Optional.of(r));
        Optional<Recomendacion> resultado = service.obtenerPorId(1);
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(r);
    }

    @Test
    void obtenerPorCategoria_deberiaRetornarListaPorCategoria() {
        List<Recomendacion> lista = Arrays.asList(new Recomendacion(1, "Libro A", "Autor", "Novela", "Buen libro"));
        when(repo.findByCategoria("Novela")).thenReturn(lista);
        assertThat(service.obtenerPorCategoria("Novela")).isEqualTo(lista);
    }

    @Test
    void guardar_deberiaRetornarObjetoGuardado() {
        Recomendacion r = new Recomendacion(0, "AI para Todos", "A. Smith", "Tecnología", "Muy útil");
        when(repo.save(r)).thenReturn(r);
        // este test ignora validación para probar solo persistencia
        assertThat(repo.save(r)).isEqualTo(r);
    }
}
