package com.example.Microservicio.Multas.Controller;

import com.example.Microservicio.Multas.Model.Multa;
import com.example.Microservicio.Multas.Service.MultaService;
import com.example.Microservicio.Multas.WebClient.ValidacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/multas")
public class MultaController {

    @Autowired
    private MultaService multaService;

    // GET general – permitido solo a estudiantes y docentes
    @PostMapping("/listar")
    public ResponseEntity<?> listarTodas(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasena = body.get("contrasena");

        ValidacionResponse usuario = multaService.validarUsuario(correo, contrasena);
        if (!usuario.getRol().equalsIgnoreCase("ESTUDIANTE") &&
            !usuario.getRol().equalsIgnoreCase("DOCENTE")) {
            return ResponseEntity.status(403).body("Solo estudiantes o docentes pueden ver las multas.");
        }

        return ResponseEntity.ok(multaService.obtenerTodasLasMultas());
    }

    // GET por ID – permitido solo a estudiantes y docentes
    @PostMapping("/ver/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasena = body.get("contrasena");

        ValidacionResponse usuario = multaService.validarUsuario(correo, contrasena);
        if (!usuario.getRol().equalsIgnoreCase("ESTUDIANTE") &&
            !usuario.getRol().equalsIgnoreCase("DOCENTE")) {
            return ResponseEntity.status(403).body("Solo estudiantes o docentes pueden ver multas.");
        }

        return multaService.obtenerMultaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST – permitido solo a bibliotecarios y administradores
    @PostMapping
    public ResponseEntity<?> crearMulta(@RequestBody Map<String, Object> datos) {
        try {
            String correo = datos.get("correo").toString();
            String contrasena = datos.get("contrasena").toString();

            ValidacionResponse usuario = multaService.validarUsuario(correo, contrasena);
            if (!usuario.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !usuario.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
                return ResponseEntity.status(403).body("Solo administradores o bibliotecarios pueden crear multas.");
            }

            Multa nuevaMulta = new Multa();
            nuevaMulta.setRunUsuario(datos.get("runUsuario").toString());
            nuevaMulta.setTipoSancion(datos.get("tipoSancion").toString());
            nuevaMulta.setSancion(datos.get("sancion").toString());
            nuevaMulta.setIdDevolucion(Integer.parseInt(datos.get("idDevolucion").toString()));

            Multa multaCreada = multaService.crearMulta(nuevaMulta, correo, contrasena);
            return ResponseEntity.status(201).body(multaCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // PUT – permitido solo a bibliotecarios y administradores
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String correo = datos.get("correo").toString();
            String contrasena = datos.get("contrasena").toString();

            ValidacionResponse usuario = multaService.validarUsuario(correo, contrasena);
            if (!usuario.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !usuario.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
                return ResponseEntity.status(403).body("Solo administradores o bibliotecarios pueden actualizar multas.");
            }

            Multa multaActualizada = new Multa();
            multaActualizada.setRunUsuario(datos.get("runUsuario").toString());
            multaActualizada.setTipoSancion(datos.get("tipoSancion").toString());
            multaActualizada.setSancion(datos.get("sancion").toString());

            return multaService.actualizarMulta(id, multaActualizada)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DELETE – permitido solo a bibliotecarios y administradores
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasena = body.get("contrasena");

        try {
            ValidacionResponse usuario = multaService.validarUsuario(correo, contrasena);
            if (!usuario.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
                !usuario.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
                return ResponseEntity.status(403).body("Solo administradores o bibliotecarios pueden eliminar multas.");
            }

            boolean eliminado = multaService.eliminarMulta(id);
            if (eliminado) {
                return ResponseEntity.ok("Multa eliminada correctamente.");
            } else {
                return ResponseEntity.status(404).body("Multa no encontrada.");
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
