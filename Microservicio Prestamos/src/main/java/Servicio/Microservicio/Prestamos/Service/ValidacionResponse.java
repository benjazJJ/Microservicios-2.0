package Servicio.Microservicio.Prestamos.Service;

public class ValidacionResponse {
    public boolean autenticado;
    public int idUsuario;
    public String correo;
    public String rol;

    public boolean esAutenticado() {
        return autenticado;
    }

    public String getRol() {
        return rol;
    }
}