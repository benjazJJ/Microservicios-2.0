package Microservicio.Microservicio.Roles.Y.Permisos.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "Cuentas_usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un usuario dentro del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @Schema(description = "Identificador único del usuario", example = "1")
    private int id;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.cl")
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 200)
    @Schema(description = "Contraseña del usuario (encriptada)", example = "$2a$10$abcdef...")
    private String contrasena;

    @Column(name = "nombre", nullable = false, length = 100)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Column(name = "telefono", length = 20)
    @Schema(description = "Teléfono de contacto del usuario", example = "+56912345678")
    private String telefono;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    @Schema(description = "RUT chileno del usuario", example = "12345678-9")
    private String rut;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    @Schema(description = "Rol asignado al usuario")
    private Rol rol;
}