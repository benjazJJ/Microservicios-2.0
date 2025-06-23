package Microservicio.Microservicio.Roles.Y.Permisos.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import Microservicio.Microservicio.Roles.Y.Permisos.Model.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Buscar por nombre del rol (ej: "ESTUDIANTE", "DOCENTE", "ADMINISTRADOR", "BIBLIOTECARIO")
    Optional<Rol> findByNombreRol(String nombreRol);
}