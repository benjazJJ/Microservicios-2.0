package Servicio.Microservicio.de.Notificaciones7.WebClient;

import lombok.Data;

@Data
public class ValidacionResponse {
    private boolean autenticado;
    private String rol;
    private int idUsuario;
    private String correo;
}
