package com.example.Microservicio.Multas.WebClient;

public class ValidacionResponse {
    private boolean autenticado;
    private int idUsuario;
    private String correo;
    private String rol;

    // Constructor vacío
    public ValidacionResponse() {}

    // Constructor completo
    public ValidacionResponse(boolean autenticado, int idUsuario, String correo, String rol) {
        this.autenticado = autenticado;
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rol = rol;
    }

    // Constructor parcial (opcional, para tests)
    public ValidacionResponse(boolean autenticado, String rol) {
        this.autenticado = autenticado;
        this.rol = rol;
    }

    // Getters y setters
    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}