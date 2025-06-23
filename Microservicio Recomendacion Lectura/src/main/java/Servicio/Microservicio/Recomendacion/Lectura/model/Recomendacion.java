package Servicio.Microservicio.Recomendacion.Lectura.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recomendacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una recomendación de lectura realizada por un usuario.")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Identificador único de la recomendación", example = "1")
    private int id;

    @Column(name = "titulo_libro")
    @Schema(description = "Título del libro recomendado", example = "Cien años de soledad")
    private String tituloLibro;

    @Column(name = "autor")
    @Schema(description = "Autor del libro recomendado", example = "Gabriel García Márquez")
    private String autor;

    @Column(name = "categoria")
    @Schema(description = "Categoría o género del libro", example = "Realismo mágico")
    private String categoria;

    @Column(name = "motivo")
    @Schema(description = "Motivo por el cual se recomienda este libro", example = "Es una obra maestra de la literatura hispanoamericana")
    private String motivo;
}
