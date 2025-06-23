package Servicio.Microservicio.Prestamos.Model;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "prestamos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un préstamo de libro")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo", unique = true, nullable = false)
    @Schema(description = "Identificador único del préstamo", example = "1")
    private Integer idPrestamo;

    @Column(name = "id_usuario", unique = true, nullable = false)
    @Schema(description = "ID del usuario solicitante", example = "4")
    private Integer idUsuario;

    @Column(nullable = false)
    @Schema(description = "ID del libro prestado", example = "15")
    private Long idLibro;

    @Column(unique = true, length = 12, nullable = false)
    @Schema(description = "RUN del solicitante del préstamo", example = "12345678-9")
    private String runSolicitante;

    @Column(nullable = false)
    @Schema(description = "Fecha en que se solicita el préstamo", example = "2025-06-21")
    private Date fechaSolicitud;

    @Column(nullable = true)
    @Schema(description = "Fecha en que se entregó el libro", example = "2025-06-25")
    private Date fechaEntrega;

    @Column(nullable = false)
    @Schema(description = "Cantidad de días solicitados para el préstamo", example = "7")
    private int cantidadDias;
}
