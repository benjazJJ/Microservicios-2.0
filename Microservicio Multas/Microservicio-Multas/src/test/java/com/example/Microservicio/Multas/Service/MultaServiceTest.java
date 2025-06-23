package com.example.Microservicio.Multas.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Microservicio.Multas.Model.Multa;
import com.example.Microservicio.Multas.Repository.MultaRepository;
import com.example.Microservicio.Multas.Service.MultaService;
import com.example.Microservicio.Multas.WebClient.MultasMult;
import com.example.Microservicio.Multas.WebClient.ValidacionResponse;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private MultasMult multasMult;

    @InjectMocks
    private MultaService multaService;

    @Test
    void obtenerTodasLasMultas_retornarListaMockeada() {
        List<Multa> mockList = List.of(new Multa(1L, "11111111-1", "Retraso", "Bloqueo 7 días", 5));
        when(multaRepository.findAll()).thenReturn(mockList);

        List<Multa> resultado = multaService.obtenerTodasLasMultas();

        assertThat(resultado).isEqualTo(mockList);
    }

    @Test
    void obtenerMultaPorId_retornaCorrectamente() {
        Multa mockMulta = new Multa(2L, "22222222-2", "Daño", "Bloqueo permanente", 7);
        when(multaRepository.findById(2L)).thenReturn(Optional.of(mockMulta));

        Optional<Multa> resultado = multaService.obtenerMultaPorId(2L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(mockMulta);
    }

    @Test
    void actualizarMulta_modificaYRetornaActualizada() {
        Multa original = new Multa(3L, "33333333-3", "Atraso leve", "Advertencia", 10);
        Multa actualizada = new Multa(null, "33333333-3", "Atraso leve", "Bloqueo 3 días", 10);

        when(multaRepository.findById(3L)).thenReturn(Optional.of(original));
        when(multaRepository.save(any(Multa.class))).thenReturn(actualizada);

        Optional<Multa> resultado = multaService.actualizarMulta(3L, actualizada);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getSancion()).isEqualTo("Bloqueo 3 días");
    }

    @Test
    void eliminarMulta_existente_retornaTrue() {
        when(multaRepository.existsById(4L)).thenReturn(true);

        boolean resultado = multaService.eliminarMulta(4L);

        assertThat(resultado).isTrue();
        verify(multaRepository).deleteById(4L);
    }

    @Test
    void eliminarMulta_noExistente_retornaFalse() {
        when(multaRepository.existsById(5L)).thenReturn(false);

        boolean resultado = multaService.eliminarMulta(5L);

        assertThat(resultado).isFalse();
    }

    @Test
    void crearMulta_validaYGuardaCorrectamente() {
        // Datos de entrada
        Multa multa = new Multa(null, "12345678-9", "Retraso", "Bloqueo 7 días", 1);

        // Simular validación de usuario
        ValidacionResponse mockResponse = new ValidacionResponse();
        mockResponse.setAutenticado(true);
        mockResponse.setRol("ADMINISTRADOR");
        
        // Simular devolución encontrada
        Map<String, Object> mockDevolucion = Map.of("id", 1);

        // Mock de métodos
        Multa multaGuardada = new Multa(10L, multa.getRunUsuario(), multa.getTipoSancion(), multa.getSancion(), multa.getIdDevolucion());

        // Simular comportamientos
        MultaService spyService = spy(multaService);
        doReturn(mockResponse).when(spyService).validarUsuario("correo@admin.cl", "123456");
        when(multasMult.getDevolucionById(1)).thenReturn(mockDevolucion);
        when(multaRepository.save(multa)).thenReturn(multaGuardada);

        // Ejecutar prueba
        Multa resultado = spyService.crearMulta(multa, "correo@admin.cl", "123456");

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getSancion()).isEqualTo("Bloqueo 7 días");
    }

    @Test
    void crearMulta_usuarioNoAutorizado_lanzaExcepcion() {
        Multa multa = new Multa(null, "99999999-9", "Grave", "Bloqueo indefinido", 3);

        ValidacionResponse mockResponse = new ValidacionResponse();
        mockResponse.setAutenticado(true);
        mockResponse.setRol("ESTUDIANTE");

        MultaService spyService = spy(multaService);
        doReturn(mockResponse).when(spyService).validarUsuario("usuario@correo.cl", "clave");

        assertThrows(RuntimeException.class, () -> {
            spyService.crearMulta(multa, "usuario@correo.cl", "clave");
        });
    }
}