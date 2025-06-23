package Microservicio.Recomendaciones.y.Sugerencias.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Microservicio.Recomendaciones.y.Sugerencias.model.RecomendacionesySugerencias;
import Microservicio.Recomendaciones.y.Sugerencias.service.RecomendacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/sugerencias")
public class RecomendacionController {

    @Autowired
    private RecomendacionService service;

    @Operation(summary = "Crear una nueva sugerencia con validación de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sugerencia creada exitosamente", content = @Content(schema = @Schema(implementation = RecomendacionesySugerencias.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crearSugerencia(@RequestBody Map<String, Object> datos) {
        try {
            String correo = (String) datos.get("correo");
            String contrasena = (String) datos.get("contrasena");

            RecomendacionesySugerencias sugerencia = service.crearRecomendacionSiEsValida(datos, correo, contrasena);
            return ResponseEntity.status(201).body(EntityModel.of(sugerencia,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                            .obtenerSugerenciaPorId(sugerencia.getIdEncuesta())).withSelfRel(),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                            .obtenerSugerencias()).withRel("todas")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener todas las sugerencias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas", content = @Content(schema = @Schema(implementation = RecomendacionesySugerencias.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RecomendacionesySugerencias>>> obtenerSugerencias() {
        List<RecomendacionesySugerencias> sugerencias = service.obtenerTodas();

        List<EntityModel<RecomendacionesySugerencias>> recursos = sugerencias.stream()
                .map(s -> EntityModel.of(s,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                                .obtenerSugerenciaPorId(s.getIdEncuesta())).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<RecomendacionesySugerencias>> collection = CollectionModel.of(
                recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                        .obtenerSugerencias()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener una sugerencia por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sugerencia encontrada", content = @Content(schema = @Schema(implementation = RecomendacionesySugerencias.class))),
        @ApiResponse(responseCode = "404", description = "Sugerencia no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSugerenciaPorId(@PathVariable Integer id) {
        Optional<RecomendacionesySugerencias> sugerenciaOpt = Optional.ofNullable(service.obtenerPorId(id));
        return sugerenciaOpt.map(s -> {
            EntityModel<RecomendacionesySugerencias> recurso = EntityModel.of(s);
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                    .obtenerSugerenciaPorId(id)).withSelfRel());
            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RecomendacionController.class)
                    .obtenerSugerencias()).withRel("todas"));
            return ResponseEntity.ok(recurso);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una sugerencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sugerencia actualizada", content = @Content(schema = @Schema(implementation = RecomendacionesySugerencias.class))),
        @ApiResponse(responseCode = "404", description = "Sugerencia no encontrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Error al actualizar", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarSugerencia(@PathVariable Integer id,
            @RequestBody Map<String, Object> datos) {

        RecomendacionesySugerencias sugerenciaExistente = service.obtenerPorId(id);
        if (sugerenciaExistente == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String nuevoMensaje = datos.get("mensaje").toString();
            int nuevaPuntuacion = Integer.parseInt(datos.get("puntuacion").toString());

            sugerenciaExistente.setMensaje(nuevoMensaje);
            sugerenciaExistente.setPuntuacion(nuevaPuntuacion);

            RecomendacionesySugerencias actualizada = service.actualizarRecomendacion(sugerenciaExistente);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Edición de sugerencia realizada con éxito.");
            respuesta.put("sugerencia", actualizada);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar una sugerencia por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sugerencia eliminada exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Sugerencia no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarSugerencia(@PathVariable Integer id) {
        if (service.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminarPorId(id);
        return ResponseEntity.ok(Map.of("mensaje", "Sugerencia eliminada con éxito"));
    }
}
