package Microservicio.Microservicio.Roles.Y.Permisos.Service;

import Microservicio.Microservicio.Roles.Y.Permisos.Model.Usuario;
import Microservicio.Microservicio.Roles.Y.Permisos.Model.Rol;
import Microservicio.Microservicio.Roles.Y.Permisos.Repository.UsuarioRepository;
import Microservicio.Microservicio.Roles.Y.Permisos.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Registra un nuevo usuario como estudiante
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalStateException("Ya existe una cuenta con este correo.");
        }

        if (usuarioRepository.findByRut(usuario.getRut()).isPresent()) {
            throw new IllegalStateException("Ya existe una cuenta con este RUT.");
        }

        // Asignar rol ESTUDIANTE automáticamente
        Rol rolEstudiante = rolRepository.findByNombreRol("ESTUDIANTE")
                .orElseThrow(() -> new RuntimeException("Rol ESTUDIANTE no encontrado."));
        usuario.setRol(rolEstudiante);

        String encriptada = Encriptador.encriptar(usuario.getContrasena());
        usuario.setContrasena(encriptada);

        return usuarioRepository.save(usuario);
    }

    // Autenticar y devolver usuario completo (para /auth/validar)
    public Optional<Usuario> autenticarYObtener(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent() &&
            Encriptador.comparar(contrasena, usuarioOpt.get().getContrasena())) {
            return usuarioOpt;
        }
        return Optional.empty();
    }

    public Usuario obtenerPorId(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario obtenerPorRut(String rut) {
        return usuarioRepository.findByRut(rut).orElse(null);
    }

    // Método para autenticar sin obtener el usuario
    public boolean autenticar(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = autenticarYObtener(correo, contrasena);
        return usuarioOpt.isPresent();
    }

    // Agrega este método para eliminar un usuario por ID
    public void eliminar(int id) {
        // Implementa la lógica para eliminar el usuario, por ejemplo:
        // usuarioRepository.deleteById(id);
        // Si usas JPA, descomenta la línea de arriba y asegúrate de tener usuarioRepository definido.
        throw new UnsupportedOperationException("Método eliminar aún no implementado");
    }
}
