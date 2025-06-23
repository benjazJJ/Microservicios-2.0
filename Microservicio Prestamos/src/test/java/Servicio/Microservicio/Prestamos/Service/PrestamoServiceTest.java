package Servicio.Microservicio.Prestamos.Service;

import Servicio.Microservicio.Prestamos.Model.Prestamo;
import Servicio.Microservicio.Prestamos.Repository.PrestamoRepository;
import Servicio.Microservicio.Prestamos.webclient.CuentasClient;
import Servicio.Microservicio.Prestamos.webclient.PedidoPed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private CuentasClient cuentasClient;

    @Mock
    private PedidoPed pedidoPed;

    @InjectMocks
    private PrestamoService prestamoService;

    @Test
    void obtenerTodosLosPrestamos_deberiaRetornarLista() {
        List<Prestamo> lista = List.of(new Prestamo(1, 2, 1L, "11111111-1", Date.valueOf(LocalDate.now()), null, 5));
        when(prestamoRepository.findAll()).thenReturn(lista);

        List<Prestamo> resultado = prestamoService.obtenerTodosLosPrestamos();

        assertThat(resultado).isEqualTo(lista);
    }

    @Test
    void obtenerPrestamoPorId_existente_deberiaRetornarPrestamo() {
        Prestamo prestamo = new Prestamo(1, 2, 1L, "11111111-1", Date.valueOf(LocalDate.now()), null, 5);
        when(prestamoRepository.findById(1)).thenReturn(Optional.of(prestamo));

        Prestamo resultado = prestamoService.obtenerPrestamoPorId(1);

        assertThat(resultado).isEqualTo(prestamo);
    }

    @Test
    void obtenerPrestamosPorRun_existente_deberiaRetornarLista() {
        List<Prestamo> lista = List.of(new Prestamo(1, 2, 1L, "11111111-1", Date.valueOf(LocalDate.now()), null, 5));
        when(prestamoRepository.findByRunSolicitante("11111111-1")).thenReturn(lista);

        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorRun("11111111-1");

        assertThat(resultado).isEqualTo(lista);
    }

    @Test
    void eliminarPrestamo_existente_deberiaEjecutarseSinError() {
        doNothing().when(prestamoRepository).deleteById(1);

        prestamoService.eliminarPrestamo(1);

        verify(prestamoRepository, times(1)).deleteById(1);
    }

    @Test
    void actualizarPrestamo_deberiaGuardarYRetornarPrestamo() {
        Prestamo prestamo = new Prestamo(1, 2, 1L, "11111111-1", Date.valueOf(LocalDate.now()), null, 5);
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);

        Prestamo resultado = prestamoService.actualizarPrestamo(prestamo);

        assertThat(resultado).isEqualTo(prestamo);
    }

    @Test
    void crearPrestamo_usuarioInvalido_lanzaExcepcion() {
        Prestamo prestamo = new Prestamo(null, 99, 2L, "11111111-1", Date.valueOf(LocalDate.now()), null, 7);
        when(cuentasClient.validarUsuarioPorId(99)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> prestamoService.crearPrestamo(prestamo));
    }

    @Test
    void crearPrestamo_libroSinStock_lanzaExcepcion() {
        Prestamo prestamo = new Prestamo(null, 1, 2L, "11111111-1", Date.valueOf(LocalDate.now()), null, 7);

        when(cuentasClient.validarUsuarioPorId(1)).thenReturn(true);
        when(cuentasClient.validarUsuarioPorRut("11111111-1")).thenReturn(true);
        when(prestamoRepository.existsByRunSolicitante("11111111-1")).thenReturn(false);
        when(pedidoPed.getLibroById(2L)).thenReturn(Map.of("cantidad", 0));

        assertThrows(RuntimeException.class, () -> prestamoService.crearPrestamo(prestamo));
    }
}

