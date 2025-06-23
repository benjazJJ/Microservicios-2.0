package Servicio.Microservicio.de.Notificaciones7.Controller;

import Servicio.Microservicio.de.Notificaciones7.Model.Notificaciones;
import Servicio.Microservicio.de.Notificaciones7.Service.NotificacionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionesController {

    @Autowired
    private NotificacionesService notificacionesService;

    /**
     * GET: Obtiene todas las notificaciones almacenadas.
     * Devuelve una colección de notificaciones con links HATEOAS hacia sí mismas.
     */
    @Operation(summary = "Obtener todas las notificaciones registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones encontradas", content = @Content(schema = @Schema(implementation = Notificaciones.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Notificaciones>>> obtenerTodas() {
        List<Notificaciones> lista = notificacionesService.obtenerTodas();

        List<EntityModel<Notificaciones>> recursos = lista.stream()
            .map(noti -> EntityModel.of(noti,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerPorId(noti.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Notificaciones>> collection = CollectionModel.of(recursos);
        return ResponseEntity.ok(collection);
    }

    /**
     * GET: Obtiene una notificación específica por su ID.
     * Incluye links HATEOAS para ver todas, eliminar o acceder a sí misma.
     */
    @Operation(summary = "Obtener una notificación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación encontrada", content = @Content(schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable int id) {
        return notificacionesService.obtenerPorId(id)
            .map(noti -> {
                EntityModel<Notificaciones> recurso = EntityModel.of(noti);
                recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerPorId(id)).withSelfRel());
                recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerTodas()).withRel("todas"));
                recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).eliminar(id, null)).withRel("eliminar"));
                return ResponseEntity.ok(recurso);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET: Obtiene todas las notificaciones enviadas por un correo emisor específico.
     * Cada notificación contiene link HATEOAS a su detalle.
     */
    @Operation(summary = "Obtener notificaciones por correo del emisor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones encontradas", content = @Content(schema = @Schema(implementation = Notificaciones.class)))
    })
    @GetMapping("/emisor/{correo}")
    public ResponseEntity<CollectionModel<EntityModel<Notificaciones>>> obtenerPorEmisor(@PathVariable String correo) {
        List<Notificaciones> lista = notificacionesService.obtenerPorEmisor(correo);

        List<EntityModel<Notificaciones>> recursos = lista.stream()
            .map(noti -> EntityModel.of(noti,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerPorId(noti.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Notificaciones>> collection = CollectionModel.of(recursos);
        return ResponseEntity.ok(collection);
    }

    /**
     * GET: Obtiene todas las notificaciones recibidas por un correo receptor específico.
     * Cada notificación contiene link HATEOAS a su detalle.
     */
    @Operation(summary = "Obtener notificaciones por correo del receptor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones encontradas", content = @Content(schema = @Schema(implementation = Notificaciones.class)))
    })
    @GetMapping("/receptor/{correo}")
    public ResponseEntity<CollectionModel<EntityModel<Notificaciones>>> obtenerPorReceptor(@PathVariable String correo) {
        List<Notificaciones> lista = notificacionesService.obtenerPorReceptor(correo);

        List<EntityModel<Notificaciones>> recursos = lista.stream()
            .map(noti -> EntityModel.of(noti,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerPorId(noti.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Notificaciones>> collection = CollectionModel.of(recursos);
        return ResponseEntity.ok(collection);
    }

    /**
     * POST: Crea una nueva notificación.
     * Solo usuarios con rol ADMINISTRADOR o BIBLIOTECARIO pueden realizar esta acción.
     * Devuelve la notificación creada con links HATEOAS a sí misma y a la lista completa.
     */
    @Operation(summary = "Crear una nueva notificación (solo ADMINISTRADOR o BIBLIOTECARIO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente", content = @Content(schema = @Schema(implementation = Notificaciones.class))),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            // Mapear campos desde el JSON recibido
            Notificaciones noti = new Notificaciones();
            noti.setMensaje((String) body.get("mensaje"));
            noti.setTipo((String) body.get("tipo"));
            noti.setCorreoEmisor((String) body.get("correoEmisor"));
            noti.setCorreoReceptor((String) body.get("correoReceptor"));

            // Credenciales para validar permisos
            String correo = (String) body.get("correo");
            String contrasena = (String) body.get("contrasena");

            Notificaciones creada = notificacionesService.crear(noti, correo, contrasena);

            // Construcción del modelo con HATEOAS
            EntityModel<Notificaciones> recurso = EntityModel.of(creada);
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerPorId(creada.getId())).withSelfRel());
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(NotificacionesController.class).obtenerTodas()).withRel("todas"));

            return ResponseEntity.status(201).body(recurso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * DELETE: Elimina una notificación por ID.
     * Solo pueden hacerlo ADMINISTRADOR o BIBLIOTECARIO.
     */
    @Operation(summary = "Eliminar una notificación por su ID (solo ADMINISTRADOR o BIBLIOTECARIO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            String correo = body.get("correo");
            String contrasena = body.get("contrasena");

            notificacionesService.eliminar(id, correo, contrasena);
            return ResponseEntity.ok("Notificación eliminada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
