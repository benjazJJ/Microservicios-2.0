package Servicio.Microservicio.de.Notificaciones7.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una notificación enviada entre usuarios del sistema")
public class Notificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    @Schema(description = "ID único de la notificación", example = "1")
    private int id;

    @Column(nullable = false, length = 255)
    @Schema(description = "Contenido del mensaje de la notificación", example = "Tienes un libro pendiente de devolución")
    private String mensaje;

    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo de notificación", example = "Recordatorio")
    private String tipo;

    @Column(nullable = false, length = 100)
    @Schema(description = "Correo del usuario que recibe la notificación", example = "usuario@correo.cl")
    private String correoReceptor;

    @Column(nullable = false, length = 100)
    @Schema(description = "Correo del usuario que envía la notificación", example = "admin@correo.cl")
    private String correoEmisor;
}
