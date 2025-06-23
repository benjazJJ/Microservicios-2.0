package Microservicio.Microservicio.Roles.Y.Permisos.Controller;

import Microservicio.Microservicio.Roles.Y.Permisos.Model.Rol;
import Microservicio.Microservicio.Roles.Y.Permisos.Model.Usuario;
import Microservicio.Microservicio.Roles.Y.Permisos.Service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(AutenticacionController.class)
@ExtendWith(SpringExtension.class)
public class AutenticacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registrarUsuario_retornaOk() throws Exception {
        Usuario nuevo = new Usuario(1, "test@correo.cl", "1234", "Juan", "123", "11.111.111-1", new Rol(2, "ESTUDIANTE"));

        when(usuarioService.registrar(nuevo)).thenReturn(nuevo);

        mockMvc.perform(post("/api/v1/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("test@correo.cl"));
    }

    @Test
    void login_correcto_retornaOk() throws Exception {
        Usuario login = new Usuario();
        login.setCorreo("correo@correo.cl");
        login.setContrasena("1234");

        when(usuarioService.autenticar("correo@correo.cl", "1234")).thenReturn(true);

        mockMvc.perform(post("/api/v1/autenticacion/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().string("Autenticación exitosa"));
    }

    @Test
    void obtenerUsuarioPorId_existente_retornaOk() throws Exception {
        Usuario usuario = new Usuario(5, "juan@correo.cl", "123", "Juan", "987", "22.222.222-2", new Rol(3, "DOCENTE"));

        when(usuarioService.obtenerPorId(5)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/autenticacion/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("juan@correo.cl"));
    }

    @Test
    void validarCredenciales_retornaValidacionResponse() throws Exception {
        Usuario usuario = new Usuario(10, "admin@correo.cl", "hash123", "Admin", "999", "33.333.333-3", new Rol(1, "ADMINISTRADOR"));

        when(usuarioService.autenticarYObtener("admin@correo.cl", "1234")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/autenticacion/validar")
                        .param("correo", "admin@correo.cl")
                        .param("contrasena", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.correo").value("admin@correo.cl"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    void editarCuenta_datosValidos_retornaOk() throws Exception {
        Usuario original = new Usuario(4, "user@correo.cl", "oldpass", "User", "111", "44.444.444-4", new Rol(2, "ESTUDIANTE"));
        Usuario cambios = new Usuario(4, "user@correo.cl", "nuevaclave", "NuevoNombre", "222", "44.444.444-4", null);

        when(usuarioService.obtenerPorId(4)).thenReturn(original);
        when(usuarioService.registrar(any(Usuario.class))).thenReturn(original);

        mockMvc.perform(put("/api/v1/autenticacion/editarcuenta/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("user@correo.cl"));
    }

    @Test
    void eliminarCuenta_autenticadoAdmin_retornaOk() throws Exception {
        Usuario admin = new Usuario(1, "admin@correo.cl", "hash", "Admin", "999", "33.333.333-3", new Rol(1, "ADMINISTRADOR"));
        Usuario aEliminar = new Usuario(2, "otro@correo.cl", "hash", "Otro", "000", "55.555.555-5", new Rol(2, "ESTUDIANTE"));

        when(usuarioService.autenticarYObtener("admin@correo.cl", "1234")).thenReturn(Optional.of(admin));
        when(usuarioService.obtenerPorId(2)).thenReturn(aEliminar);

        mockMvc.perform(delete("/api/v1/autenticacion/usuario/2")
                        .param("correo", "admin@correo.cl")
                        .param("contrasena", "1234"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cuenta eliminada correctamente"));
    }
}
