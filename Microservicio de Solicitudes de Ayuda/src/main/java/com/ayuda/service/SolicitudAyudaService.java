package com.ayuda.service;

import com.ayuda.model.SolicitudAyuda;
import com.ayuda.repository.SolicitudAyudaRepository;
import com.ayuda.WebClient.ValidacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudAyudaService {

    @Autowired
    private SolicitudAyudaRepository repository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    //Validar usuario desde autenticación
    private ValidacionResponse validarUsuario(String correo, String contrasena) {
        try {
            ValidacionResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/autenticacion/validar")
                            .queryParam("correo", correo)
                            .queryParam("contrasena", contrasena)
                            .build())
                    .retrieve()
                    .bodyToMono(ValidacionResponse.class)
                    .block();

            if (response == null || !response.isAutenticado()) {
                throw new RuntimeException("Credenciales inválidas o usuario no encontrado.");
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar credenciales: " + e.getMessage());
        }
    }

    //Crear solicitud (solo ESTUDIANTE o DOCENTE)
    public SolicitudAyuda crear(String asunto, String mensaje, String correo, String contrasena) {
        ValidacionResponse user = validarUsuario(correo, contrasena);
        String rol = user.getRol().toUpperCase();

        if (!rol.equals("ESTUDIANTE") && !rol.equals("DOCENTE")) {
            throw new RuntimeException("Solo estudiantes o docentes pueden enviar solicitudes.");
        }

        SolicitudAyuda solicitud = new SolicitudAyuda();
        solicitud.setCorreoUsuario(correo);
        solicitud.setAsunto(asunto);
        solicitud.setMensaje(mensaje);
        solicitud.setFechaEnvio(Date.valueOf(java.time.LocalDate.now()));

        return repository.save(solicitud);
    }

    //Actualizar solicitud (solo ADMIN o BIBLIOTECARIO)
    public SolicitudAyuda actualizar(int id, String asunto, String mensaje, String correo, String contrasena) {
        ValidacionResponse user = validarUsuario(correo, contrasena);
        String rol = user.getRol().toUpperCase();

        if (!rol.equals("ADMINISTRADOR") && !rol.equals("BIBLIOTECARIO")) {
            throw new RuntimeException("Solo administradores o bibliotecarios pueden actualizar solicitudes.");
        }

        SolicitudAyuda existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        existente.setAsunto(asunto);
        existente.setMensaje(mensaje);

        return repository.save(existente);
    }

    //Eliminar solicitud (solo ADMIN o BIBLIOTECARIO)
    public void eliminarPorId(int id, String correo, String contrasena) {
        ValidacionResponse user = validarUsuario(correo, contrasena);
        String rol = user.getRol().toUpperCase();

        if (!rol.equals("ADMINISTRADOR") && !rol.equals("BIBLIOTECARIO")) {
            throw new RuntimeException("Solo administradores o bibliotecarios pueden eliminar solicitudes.");
        }

        repository.deleteById(id);
    }

    //Obtener todas
    public List<SolicitudAyuda> obtenerTodas() {
        return repository.findAll();
    }

    //Obtener por ID
    public Optional<SolicitudAyuda> obtenerPorId(int id) {
        return repository.findById(id);
    }

    //Obtener por correo
    public List<SolicitudAyuda> obtenerPorCorreoUsuario(String correoUsuario) {
        return repository.findByCorreoUsuario(correoUsuario);
    }
}
