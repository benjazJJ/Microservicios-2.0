package Microservicio.Recomendaciones.y.Sugerencias.Service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import Microservicio.Recomendaciones.y.Sugerencias.model.RecomendacionesySugerencias;
import Microservicio.Recomendaciones.y.Sugerencias.repository.RecomendacionRepository;
import Microservicio.Recomendaciones.y.Sugerencias.service.RecomendacionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecomendacionServiceTest {

    @Mock
    private RecomendacionRepository recomendacionRepository;

    @InjectMocks
    private RecomendacionService recomendacionService;

    @Test
    void crearRecomendacion_valida_retornaRecomendacionGuardada() {
        // Arrange
        RecomendacionesySugerencias sugerencia = new RecomendacionesySugerencias(
                null,
                5,
                "correo@duoc.cl",
                "1234",
                "Me gustaría más libros digitales",
                Date.valueOf(LocalDate.now()),
                4
        );

        when(recomendacionRepository.existsByIdUsuario(5)).thenReturn(false);
        when(recomendacionRepository.save(sugerencia)).thenReturn(sugerencia);

        // Act
        RecomendacionesySugerencias resultado = recomendacionService.crearRecomendacion(sugerencia);

        // Assert
        assertThat(resultado).isSameAs(sugerencia);
    }

    @Test
    void obtenerTodas_retornaListaRecomendaciones() {
        // Arrange
        List<RecomendacionesySugerencias> lista = Arrays.asList(
                new RecomendacionesySugerencias(1, 4, "a@a.cl", "123", "Muy bueno", Date.valueOf(LocalDate.now()), 5),
                new RecomendacionesySugerencias(2, 5, "b@b.cl", "456", "Faltan libros", Date.valueOf(LocalDate.now()), 3)
        );

        when(recomendacionRepository.findAll()).thenReturn(lista);

        // Act
        List<RecomendacionesySugerencias> resultado = recomendacionService.obtenerTodas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).isEqualTo(lista);
    }

    @Test
    void obtenerPorId_existente_retornaRecomendacion() {
        // Arrange
        RecomendacionesySugerencias sugerencia = new RecomendacionesySugerencias(
                10, 7, "test@test.cl", "pass", "Muy útil", Date.valueOf(LocalDate.now()), 5);

        when(recomendacionRepository.findById(10)).thenReturn(Optional.of(sugerencia));

        // Act
        RecomendacionesySugerencias resultado = recomendacionService.obtenerPorId(10);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdEncuesta()).isEqualTo(10);
    }

    @Test
    void obtenerPorId_noExistente_retornaNull() {
        when(recomendacionRepository.findById(99)).thenReturn(Optional.empty());

        RecomendacionesySugerencias resultado = recomendacionService.obtenerPorId(99);

        assertThat(resultado).isNull();
    }

    @Test
    void eliminarPorId_ejecutaEliminacion() {
        // Act
        recomendacionService.eliminarPorId(5);

        // Assert: verificar que se llamó al método del repositorio
        verify(recomendacionRepository).deleteById(5);
    }
}

