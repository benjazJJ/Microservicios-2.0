package Servicio.Microservicio.Recomendacion.Lectura.controller;

import Servicio.Microservicio.Recomendacion.Lectura.model.Recomendacion;
import Servicio.Microservicio.Recomendacion.Lectura.service.RecomendacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recomendaciones")
public class RecomendacionController {

    @Autowired
    private RecomendacionService service;

    /**
     * POST: Crear recomendación si usuario tiene rol ESTUDIANTE o DOCENTE.
     */
    @Operation(summary = "Crear una recomendación (solo ESTUDIANTE o DOCENTE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recomendación creada exitosamente", content = @Content(schema = @Schema(implementation = Recomendacion.class))),
        @ApiResponse(responseCode = "400", description = "Error en los datos o credenciales", content = @Content)
    })
    // POST solo para ESTUDIANTE o DOCENTE
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> datos) {
        try {
            String correo = datos.get("correo").toString();
            String contrasena = datos.get("contrasena").toString();

            Recomendacion r = new Recomendacion();
            r.setTituloLibro(datos.get("tituloLibro").toString());
            r.setAutor(datos.get("autor").toString());
            r.setCategoria(datos.get("categoria").toString());
            r.setMotivo(datos.get("motivo").toString());

            return ResponseEntity.ok(service.guardar(r, correo, contrasena));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET: Obtener todas las recomendaciones.
     */
    @Operation(summary = "Obtener todas las recomendaciones registradas")
    @ApiResponse(responseCode = "200", description = "Recomendaciones encontradas", content = @Content(schema = @Schema(implementation = Recomendacion.class)))
    // GET todos (público)
    @GetMapping
    public List<Recomendacion> obtenerTodas() {
        return service.obtenerTodas();
    }

    /**
     * GET: Obtener recomendación por ID.
     */
    @Operation(summary = "Obtener una recomendación por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recomendación encontrada", content = @Content(schema = @Schema(implementation = Recomendacion.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró la recomendación", content = @Content)
    })
    // GET por ID (público)
    @GetMapping("/{id}")
    public ResponseEntity<Recomendacion> obtenerPorId(@PathVariable int id) {
        Optional<Recomendacion> r = service.obtenerPorId(id);
        return r.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET: Obtener recomendaciones por categoría.
     */
    @Operation(summary = "Obtener recomendaciones por categoría")
    @ApiResponse(responseCode = "200", description = "Recomendaciones encontradas", content = @Content(schema = @Schema(implementation = Recomendacion.class)))
    // GET por categoría (público)
    @GetMapping("/categoria/{categoria}")
    public List<Recomendacion> obtenerPorCategoria(@PathVariable String categoria) {
        return service.obtenerPorCategoria(categoria);
    }

    /**
     * DELETE: Eliminar recomendación (solo ADMIN o BIBLIOTECARIO).
     */
    @Operation(summary = "Eliminar una recomendación por ID (solo ADMIN o BIBLIOTECARIO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recomendación eliminada correctamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "No autorizado para eliminar", content = @Content)
    })
    // DELETE solo para ADMIN o BIBLIOTECARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable int id,
            @RequestBody Map<String, String> credenciales) {
        try {
            String correo = credenciales.get("correo");
            String contrasena = credenciales.get("contrasena");

            service.eliminar(id, correo, contrasena);
            return ResponseEntity.ok("Recomendación eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
