package com.example.Microservicio.de.Stock.Service;

import lombok.Data;

@Data
public class ValidacionResponse {
    private boolean autenticado;
    private int idUsuario;
    private String correo;
    private String rol;

    // Constructor personalizado para usar en los tests
    public ValidacionResponse(boolean autenticado, String rol) {
        this.autenticado = autenticado;
        this.rol = rol;
    }

    // Constructor vacío (requerido por Jackson u otros frameworks)
    public ValidacionResponse() {}
}