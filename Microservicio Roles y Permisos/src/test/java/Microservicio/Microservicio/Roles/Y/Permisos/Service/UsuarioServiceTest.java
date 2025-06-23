package Microservicio.Microservicio.Roles.Y.Permisos.Service;

import Microservicio.Microservicio.Roles.Y.Permisos.Model.Rol;
import Microservicio.Microservicio.Roles.Y.Permisos.Model.Usuario;
import Microservicio.Microservicio.Roles.Y.Permisos.Repository.UsuarioRepository;
import Microservicio.Microservicio.Roles.Y.Permisos.Repository.RolRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrar_usuarioNuevo_deberiaGuardarYRetornarUsuario() {
        Usuario usuario = new Usuario(0, "correo@test.com", "1234", "Juan", "123456789", "11.111.111-1", null);
        Rol rolEstudiante = new Rol(2, "ESTUDIANTE");

        when(usuarioRepository.findByCorreo("correo@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByRut("11.111.111-1")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.registrar(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isEqualTo("correo@test.com");
        assertThat(resultado.getRol().getNombreRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    void autenticar_conCredencialesValidas_deberiaRetornarTrue() {
        String clave = "1234";
        String hash = Encriptador.encriptar(clave);
        Usuario usuario = new Usuario(1, "correo@test.com", hash, "Pedro", "999", "22.222.222-2", new Rol(2, "ESTUDIANTE"));

        when(usuarioRepository.findByCorreo("correo@test.com")).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.autenticar("correo@test.com", "1234");

        assertThat(resultado).isTrue();
    }

    @Test
    void autenticar_conCredencialesInvalidas_deberiaRetornarFalse() {
        Usuario usuario = new Usuario(1, "correo@test.com", Encriptador.encriptar("1234"), "Pedro", "999", "22.222.222-2", new Rol(2, "ESTUDIANTE"));

        when(usuarioRepository.findByCorreo("correo@test.com")).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.autenticar("correo@test.com", "incorrecta");

        assertThat(resultado).isFalse();
    }

    @Test
    void obtenerPorRut_existente_deberiaRetornarUsuario() {
        Usuario usuario = new Usuario(1, "correo@test.com", "hash", "Pedro", "999", "22.222.222-2", new Rol(2, "ESTUDIANTE"));

        when(usuarioRepository.findByRut("22.222.222-2")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerPorRut("22.222.222-2");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Pedro");
    }

    @Test
    void obtenerPorId_existente_deberiaRetornarUsuario() {
        Usuario usuario = new Usuario(1, "correo@test.com", "hash", "Pedro", "999", "22.222.222-2", new Rol(2, "ESTUDIANTE"));

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isEqualTo("correo@test.com");
    }
}
