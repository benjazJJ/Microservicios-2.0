package Servicio.Microservicio.de.Solicitudes.de.Ayuda.Service;

import com.ayuda.model.SolicitudAyuda;
import com.ayuda.repository.SolicitudAyudaRepository;
import com.ayuda.service.SolicitudAyudaService;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SolicitudAyudaServiceTest {

    @Mock
    private SolicitudAyudaRepository repository;

    @InjectMocks
    private SolicitudAyudaService service;

    @Test // identificamos que la función es una prueba unitaria
    void obtenerTodas_retornaListaDesdeElRepositorio() {
        // Crear una solicitud ficticia para simular la respuesta del repositorio
        List<SolicitudAyuda> mockList = Arrays.asList(
            new SolicitudAyuda(1, "alumno@correo.cl", "Acceso", "No puedo ingresar", Date.valueOf("2025-06-10"))
        );

        // Definir el comportamiento del mock (repositorio)
        when(repository.findAll()).thenReturn(mockList);

        // Ejecutar el método a probar
        List<SolicitudAyuda> resultado = service.obtenerTodas();

        // Verificar el resultado
        assertThat(resultado).isEqualTo(mockList);
    }

    @Test
    void obtenerPorId_retornaSolicitudCorrectaSiExiste() {
        // Crear solicitud ficticia
        SolicitudAyuda mockSolicitud = new SolicitudAyuda(2, "user@correo.cl", "Consulta", "¿Cómo renovar?", Date.valueOf("2025-06-11"));

        // Simular comportamiento del repositorio
        when(repository.findById(2)).thenReturn(Optional.of(mockSolicitud));

        // Ejecutar método del servicio
        Optional<SolicitudAyuda> resultado = service.obtenerPorId(2);

        // Verificar que esté presente y sea igual
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(mockSolicitud);
    }

    @Test
    void obtenerPorCorreoUsuario_retornaListaDeSolicitudesDelCorreo() {
        // Crear solicitud ficticia
        List<SolicitudAyuda> mockList = Arrays.asList(
            new SolicitudAyuda(3, "usuario@correo.cl", "Ayuda", "No encuentro libro", Date.valueOf("2025-06-12"))
        );

        // Simular comportamiento del repositorio
        when(repository.findByCorreoUsuario("usuario@correo.cl")).thenReturn(mockList);

        // Ejecutar método del servicio
        List<SolicitudAyuda> resultado = service.obtenerPorCorreoUsuario("usuario@correo.cl");

        // Verificar que la respuesta coincida con el mock
        assertThat(resultado).isEqualTo(mockList);
    }
}
