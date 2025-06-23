package com.example.Microservicio.de.Stock.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa una categoría de libros en el sistema de stock")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la categoría", example = "1")
    private Long idCategoria;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre de la categoría", example = "Ciencia Ficción")
    private String nombreCategoria;
}
