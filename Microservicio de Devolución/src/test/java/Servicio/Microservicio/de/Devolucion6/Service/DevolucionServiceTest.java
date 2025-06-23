package Servicio.Microservicio.de.Devolucion6.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Servicio.Microservicio.de.Devolucion6.Model.Devolucion;
import Servicio.Microservicio.de.Devolucion6.Repository.DevolucionRepository;

@ExtendWith(MockitoExtension.class)
public class DevolucionServiceTest {

    @Mock
    private DevolucionRepository devolucionRepository;

    @InjectMocks
    private DevolucionService devolucionService;

    @Test
    void listarDevoluciones_deberiaRetornarListaDelRepositorio() {
        // Arrange - Creamos una lista ficticia de devoluciones
        List<Devolucion> mockList = Arrays.asList(
            new Devolucion(1, Date.valueOf("2025-06-10"), "Bueno", "Sin observaciones", 5),
            new Devolucion(2, Date.valueOf("2025-06-15"), "Dañado", "Tapa rota", 7)
        );

        // Definir el comportamiento del mock (repositorio)
        when(devolucionRepository.findAll()).thenReturn(mockList);

        // Act - Llamamos al método del servicio
        List<Devolucion> resultado = devolucionService.listarDevoluciones();

        // Verificar el resultado (criterios de aceptacion)
        assertThat(resultado).isEqualTo(mockList);
    }

    @Test
    void buscarDevolucionPorID() {
        // Arrange - Creamos una devolución ficticia
        Devolucion mockDevolucion = new Devolucion(1, Date.valueOf("2025-06-12"), "Bueno", "Sin daños", 3);

        // Simulamos que el repositorio devuelve esa devolución cuando se consulta por ID
        when(devolucionRepository.findById(1)).thenReturn(Optional.of(mockDevolucion));

        // Act - Llamamos al método del servicio
        Devolucion resultado = devolucionService.buscarDevolucionPorID(1);

        // Assert - Verificamos que la devolución obtenida sea igual a la esperada
        assertThat(resultado).isEqualTo(mockDevolucion);
    }


    @Test
    void borrarDevolucion_deberiaLlamarDeleteById() {
        // Act
        devolucionService.borrarDevolucion(1);

        // Assert - Verificamos que el repositorio fue llamado con el ID correcto
        verify(devolucionRepository).deleteById(1);
    }
}
