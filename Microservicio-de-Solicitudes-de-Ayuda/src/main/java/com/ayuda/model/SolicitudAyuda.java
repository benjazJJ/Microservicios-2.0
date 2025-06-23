package com.ayuda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "solicitudes_ayuda")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa una solicitud de ayuda realizada por un usuario")
public class SolicitudAyuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    @Schema(description = "ID único de la solicitud de ayuda", example = "1")
    private int idSolicitud;

    @Column(name = "correo_usuario", nullable = false, length = 100)
    @Schema(description = "Correo del usuario que realiza la solicitud", example = "usuario@ejemplo.cl")
    private String correoUsuario;

    @Column(name = "asunto", nullable = false, length = 100)
    @Schema(description = "Asunto de la solicitud de ayuda", example = "Problema con la devolución del libro")
    private String asunto;

    @Column(name = "mensaje", nullable = false, length = 500)
    @Schema(description = "Mensaje detallado de la solicitud", example = "No aparece registrada mi devolución en el sistema")
    private String mensaje;

    @Column(name = "fecha_envio")
    @Schema(description = "Fecha en que se envió la solicitud", example = "2025-06-21")
    private Date fechaEnvio;
}
