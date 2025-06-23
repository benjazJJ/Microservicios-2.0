package Servicio.Microservicio.de.Devolucion6.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "devolucion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una devolución de un libro por parte de un usuario")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    @Schema(description = "ID único de la devolución", example = "1")
    private Integer idDevolucion;

    @Column(nullable = false)
    @Schema(description = "Fecha en que se realizó la devolución", example = "2025-06-21")
    private Date fechaDevolucion;

    @Column(nullable = false, length = 100)
    @Schema(description = "Estado del libro al momento de devolverlo", example = "Bueno")
    private String estadoLibro;

    @Column(nullable = true, length = 100)
    @Schema(description = "Observaciones adicionales sobre la devolución", example = "Leve desgaste en la portada")
    private String observaciones;

    @Column(name = "id_prestamo", nullable = false)
    @Schema(description = "ID del préstamo asociado a esta devolución", example = "15")
    private Integer idPrestamo;
}
