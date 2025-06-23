package com.ayuda.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayuda.model.SolicitudAyuda;
import com.ayuda.service.SolicitudAyudaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/ayuda")
public class SolicitudAyudaController {

    @Autowired
    private SolicitudAyudaService service;

    /**
     * GET: Obtiene todas las solicitudes de ayuda.
     * Todos los roles pueden acceder.
     */
    @Operation(summary = "Obtener todas las solicitudes de ayuda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes encontradas", content = @Content(schema = @Schema(implementation = SolicitudAyuda.class)))
    })
    // GET: todos los roles pueden acceder
    @GetMapping
    public ResponseEntity<List<SolicitudAyuda>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    /**
     * GET: Obtiene una solicitud por su ID.
     * Todos los roles pueden acceder.
     */
    @Operation(summary = "Obtener una solicitud de ayuda por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada", content = @Content(schema = @Schema(implementation = SolicitudAyuda.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada", content = @Content)
    })
    // GET por ID: todos los roles pueden acceder
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable int id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET: Obtiene solicitudes de ayuda enviadas por un usuario.
     * Todos los roles pueden acceder.
     */
    @Operation(summary = "Obtener solicitudes de ayuda por correo de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes encontradas", content = @Content(schema = @Schema(implementation = SolicitudAyuda.class)))
    })
    // GET filtrado por correo: todos los roles pueden acceder
    @GetMapping("/usuario/{correo}")
    public ResponseEntity<List<SolicitudAyuda>> obtenerPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(service.obtenerPorCorreoUsuario(correo));
    }

    /**
     * POST: Crea una nueva solicitud de ayuda.
     * Solo usuarios con rol ESTUDIANTE o DOCENTE pueden realizar esta acción.
     */
    @Operation(summary = "Crear una nueva solicitud de ayuda (solo ESTUDIANTE o DOCENTE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente", content = @Content(schema = @Schema(implementation = SolicitudAyuda.class))),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    // POST: solo ESTUDIANTE o DOCENTE
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String correo = (String) body.get("correo");
            String contrasena = (String) body.get("contrasena");
            String asunto = (String) body.get("asunto");
            String mensaje = (String) body.get("mensaje");

            SolicitudAyuda creada = service.crear(asunto, mensaje, correo, contrasena);
            return ResponseEntity.status(201).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * PUT: Actualiza una solicitud de ayuda por ID.
     * Solo usuarios con rol ADMINISTRADOR o BIBLIOTECARIO pueden realizar esta
     * acción.
     */
    @Operation(summary = "Actualizar una solicitud de ayuda por ID (solo ADMINISTRADOR o BIBLIOTECARIO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud actualizada exitosamente", content = @Content(schema = @Schema(implementation = SolicitudAyuda.class))),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    // PUT: solo ADMINISTRADOR o BIBLIOTECARIO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            String correo = (String) body.get("correo");
            String contrasena = (String) body.get("contrasena");
            String asunto = (String) body.get("asunto");
            String mensaje = (String) body.get("mensaje");

            SolicitudAyuda actualizada = service.actualizar(id, asunto, mensaje, correo, contrasena);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * DELETE: Elimina una solicitud de ayuda por ID.
     * Solo usuarios con rol ADMINISTRADOR o BIBLIOTECARIO pueden realizar esta
     * acción.
     */
    @Operation(summary = "Eliminar una solicitud de ayuda por ID (solo ADMINISTRADOR o BIBLIOTECARIO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitud eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    // DELETE: solo ADMINISTRADOR o BIBLIOTECARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            String contrasena = body.get("contrasena");

            service.eliminarPorId(id, correo, contrasena);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

}
