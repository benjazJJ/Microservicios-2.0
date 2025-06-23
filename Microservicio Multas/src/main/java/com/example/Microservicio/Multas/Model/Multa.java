package com.example.Microservicio.Multas.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "multa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa una multa impuesta a un usuario por una devolución tardía u otra infracción.")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la multa", example = "1")
    private Long id;

    @Column(nullable = false, length = 12)
    @Schema(description = "RUN del usuario sancionado", example = "12345678-9", required = true)
    private String runUsuario;

    @Column(nullable = false, length = 100)
    @Schema(description = "Tipo de sanción aplicada", example = "Suspensión de préstamos", required = true)
    private String tipoSancion;

    @Column(nullable = false, length = 100)
    @Schema(description = "Detalle o descripción de la sanción", example = "No podrá solicitar libros por 30 días", required = true)
    private String sancion;

    @Column(name = "id_Devolucion", nullable = false)
    @Schema(description = "ID de la devolución asociada a la multa", example = "10", required = true)
    private Integer idDevolucion;
}
