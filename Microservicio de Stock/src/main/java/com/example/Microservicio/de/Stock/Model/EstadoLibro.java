package com.example.Microservicio.de.Stock.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estado_libro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa el estado actual de un libro (Ej: Disponible, Prestado, Dañado, etc.)")
public class EstadoLibro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado del libro", example = "1")
    private Long idEstadoLibro;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre que describe el estado del libro", example = "Disponible")
    private String nombreEstado;
}
