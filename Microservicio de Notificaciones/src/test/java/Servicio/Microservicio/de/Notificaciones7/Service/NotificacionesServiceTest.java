package Servicio.Microservicio.de.Notificaciones7.Service;

import Servicio.Microservicio.de.Notificaciones7.Model.Notificaciones;
import Servicio.Microservicio.de.Notificaciones7.Repository.NotificacionesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NotificacionesServiceTest {

    @Mock
    private NotificacionesRepository notificacionesRepository;

    @InjectMocks
    private NotificacionesService notificacionesService;

    @Test
    public void testObtenerTodasNotificaciones() {
        List<Notificaciones> mockList = Arrays.asList(
                new Notificaciones(1, "Tu libro ha sido devuelto", "INFO", "usuario@correo.cl", "admin@biblioteca.cl"),
                new Notificaciones(2, "Tienes una multa pendiente", "ALERTA", "cliente@correo.cl", "admin@biblioteca.cl")
        );

        when(notificacionesRepository.findAll()).thenReturn(mockList);

        List<Notificaciones> resultado = notificacionesService.obtenerTodas();

        assertThat(resultado).isEqualTo(mockList);
    }

    @Test
    public void testObtenerNotificacionPorId() {
        Notificaciones mockNotificacion = new Notificaciones(
                3, "Devolución confirmada", "INFO", "estudiante@correo.cl", "bibliotecario@correo.cl"
        );

        when(notificacionesRepository.findById(3)).thenReturn(Optional.of(mockNotificacion));

        Optional<Notificaciones> resultado = notificacionesService.obtenerPorId(3);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(3);
        assertThat(resultado.get().getMensaje()).isEqualTo("Devolución confirmada");
    }

    @Test
    public void testObtenerPorEmisor() {
        String emisor = "admin@biblioteca.cl";
        List<Notificaciones> mockList = Arrays.asList(
                new Notificaciones(4, "Nueva reserva", "INFO", "usuario@correo.cl", emisor)
        );

        when(notificacionesRepository.findByCorreoEmisor(emisor)).thenReturn(mockList);

        List<Notificaciones> resultado = notificacionesService.obtenerPorEmisor(emisor);

        assertThat(resultado).isEqualTo(mockList);
        assertThat(resultado.get(0).getCorreoEmisor()).isEqualTo(emisor);
    }

    @Test
    public void testObtenerPorReceptor() {
        String receptor = "usuario@correo.cl";
        List<Notificaciones> mockList = Arrays.asList(
                new Notificaciones(5, "Libro retrasado", "ALERTA", receptor, "admin@biblioteca.cl")
        );

        when(notificacionesRepository.findByCorreoReceptor(receptor)).thenReturn(mockList);

        List<Notificaciones> resultado = notificacionesService.obtenerPorReceptor(receptor);

        assertThat(resultado).isEqualTo(mockList);
        assertThat(resultado.get(0).getCorreoReceptor()).isEqualTo(receptor);
    }
}
