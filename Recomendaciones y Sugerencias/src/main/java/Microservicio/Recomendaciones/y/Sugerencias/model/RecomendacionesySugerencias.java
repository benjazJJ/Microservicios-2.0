package Microservicio.Recomendaciones.y.Sugerencias.model;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Recomendaciones_Sugerencias")
@Schema(description = "Entidad que representa una sugerencia o recomendación enviada por un usuario del sistema")
public class RecomendacionesySugerencias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Encuesta", unique = true, nullable = false)
    @Schema(description = "ID único de la recomendación o sugerencia", example = "1")
    private Integer idEncuesta;

    @Column(name = "id_usuario", unique = true, nullable = false)
    @Schema(description = "ID del usuario que envía la sugerencia", example = "4")
    private Integer idUsuario;

    @Column(name = "correo_usuario", nullable = false, length = 100)
    @Schema(description = "Correo del usuario que envía la sugerencia", example = "usuario@correo.cl")
    private String correo;

    @Column(name = "contrasena_usuario", nullable = false, length = 100)
    @Schema(description = "Contraseña del usuario (se recomienda cifrar o usar solo temporalmente)", example = "123456")
    private String contrasena;

    @Column(name = "mensaje", nullable = false, length = 500)
    @Schema(description = "Mensaje enviado por el usuario con su sugerencia o comentario", example = "Sería bueno contar con más libros digitales.")
    private String mensaje;

    @Column(name = "fecha_envio")
    @Schema(description = "Fecha en que fue enviada la sugerencia", example = "2025-06-21")
    private Date fechaEnvio;

    @Min(1)
    @Max(5)
    @Column(name = "puntuacion", nullable = false)
    @Schema(description = "Puntuación otorgada por el usuario (entre 1 y 5)", example = "4")
    private int puntuacion;
}
