package Microservicio.Microservicio.Roles.Y.Permisos.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un rol dentro del sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "Identificador único del rol", example = "1")
    private int idRol;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre del rol", example = "ADMINISTRADOR")
    private String nombreRol; // ADMINISTRADOR, DOCENTE, BIBLIOTECARIO, ESTUDIANTE
}