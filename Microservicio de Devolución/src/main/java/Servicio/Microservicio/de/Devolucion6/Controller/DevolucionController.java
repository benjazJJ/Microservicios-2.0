package Servicio.Microservicio.de.Devolucion6.Controller;

import Servicio.Microservicio.de.Devolucion6.Model.Devolucion;
import Servicio.Microservicio.de.Devolucion6.Service.DevolucionService;
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

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/devoluciones")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    @Operation(summary = "Listar todas las devoluciones registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(schema = @Schema(implementation = Devolucion.class))),
        @ApiResponse(responseCode = "204", description = "No hay devoluciones registradas", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Devolucion>>> listar() {
        List<Devolucion> lista = devolucionService.listarDevoluciones();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Devolucion>> recursos = lista.stream()
            .map(dev -> EntityModel.of(dev,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DevolucionController.class).buscarDevolucionPorId(dev.getIdDevolucion())).withSelfRel()
            ))
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Devolucion>> collection = CollectionModel.of(recursos);
        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener una devolución por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devolución encontrada", content = @Content(schema = @Schema(implementation = Devolucion.class))),
        @ApiResponse(responseCode = "404", description = "Devolución no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDevolucionPorId(@PathVariable Integer id) {
        try {
            Devolucion dev = devolucionService.buscarDevolucionPorID(id);
            EntityModel<Devolucion> recurso = EntityModel.of(dev);
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DevolucionController.class).buscarDevolucionPorId(id)).withSelfRel());
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DevolucionController.class).listar()).withRel("todas"));
            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear una nueva devolución (solo ESTUDIANTE o DOCENTE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Devolución creada exitosamente", content = @Content(schema = @Schema(implementation = Devolucion.class))),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crearnuevaDevolucion(@RequestBody Map<String, Object> body) {
        try {
            Devolucion nuevaDevolucion = new Devolucion();
            nuevaDevolucion.setFechaDevolucion(Date.valueOf(body.get("fechaDevolucion").toString()));
            nuevaDevolucion.setEstadoLibro(body.get("estadoLibro").toString());
            nuevaDevolucion.setObservaciones(body.get("observaciones").toString());
            nuevaDevolucion.setIdPrestamo(Integer.parseInt(body.get("idPrestamo").toString()));

            String correo = body.get("correo").toString();
            String contrasena = body.get("contrasena").toString();

            Devolucion devolucion = devolucionService.crearDevolucion(nuevaDevolucion, correo, contrasena);

            EntityModel<Devolucion> recurso = EntityModel.of(devolucion);
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DevolucionController.class).buscarDevolucionPorId(devolucion.getIdDevolucion())).withSelfRel());
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DevolucionController.class).listar()).withRel("todas"));

            return ResponseEntity.status(201).body(recurso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una devolución (solo ADMINISTRADOR o BIBLIOTECARIO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Devolución eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarDevolucion(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            String correo = body.get("correo").toString();
            String contrasena = body.get("contrasena").toString();

            devolucionService.validarAdministradorOBibliotecario(correo, contrasena);
            devolucionService.borrarDevolucion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar una devolución (solo ADMINISTRADOR)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devolución actualizada exitosamente", content = @Content(schema = @Schema(implementation = Devolucion.class))),
        @ApiResponse(responseCode = "403", description = "Usuario no autorizado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDevolucionPorID(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            String correo = body.get("correo").toString();
            String contrasena = body.get("contrasena").toString();

            Devolucion dev = new Devolucion();
            dev.setFechaDevolucion(Date.valueOf(body.get("fechaDevolucion").toString()));
            dev.setEstadoLibro(body.get("estadoLibro").toString());
            dev.setObservaciones(body.get("observaciones").toString());

            Devolucion actualizada = devolucionService.actualizarDevolucion(id, dev, correo, contrasena);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
