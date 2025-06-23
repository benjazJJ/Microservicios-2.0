package com.example.Microservicio.de.Stock.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "libro_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa un libro disponible en stock con su ubicación, cantidad, categoría y estado")
public class LibroStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del registro de libro en stock", example = "101")
    private Long idLibroStock;

    @Column(nullable = false, length = 50, name = "nombre_libro")
    @Schema(description = "Nombre del libro", example = "Cien años de soledad")
    private String nombreLibro;

    @Column(nullable = false, length = 20)
    @Schema(description = "Estante donde se encuentra el libro", example = "E4")
    private String estante;

    @Column(nullable = false, length = 10)
    @Schema(description = "Fila donde se encuentra el libro", example = "2")
    private String fila;

    @Column(nullable = false, length = 10)
    @Schema(description = "Cantidad disponible en stock", example = "12")
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @Schema(description = "Categoría del libro (relación con tabla 'categoria')")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    @Schema(description = "Estado actual del libro (relación con tabla 'estado_libro')")
    private EstadoLibro estado;
}
