package com.example.Microservicio.Multas.Service;

import com.example.Microservicio.Multas.Model.Multa;
import com.example.Microservicio.Multas.Repository.MultaRepository;
import com.example.Microservicio.Multas.WebClient.MultasMult;
import com.example.Microservicio.Multas.WebClient.ValidacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MultaService {

    @Autowired
    private MultaRepository multaRepository;

    @Autowired
    private MultasMult multasMult;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081") // Microservicio de autenticación
            .build();

    public List<Multa> obtenerTodasLasMultas() {
        return multaRepository.findAll();
    }

    public Optional<Multa> obtenerMultaPorId(Long id) {
        return multaRepository.findById(id);
    }

    public Multa crearMulta(Multa multa, String correo, String contrasena) {
        ValidacionResponse response = validarUsuario(correo, contrasena);

        if (!response.getRol().equalsIgnoreCase("ADMINISTRADOR") &&
            !response.getRol().equalsIgnoreCase("BIBLIOTECARIO")) {
            throw new RuntimeException("Solo administradores o bibliotecarios pueden registrar multas.");
        }

        Map<String, Object> mult = multasMult.getDevolucionById(multa.getIdDevolucion());
        if (mult == null || mult.isEmpty()) {
            throw new RuntimeException("Devolución no encontrada. No se puede asignar la multa.");
        }

        return multaRepository.save(multa);
    }

    public Optional<Multa> actualizarMulta(Long id, Multa multaActualizada) {
        return multaRepository.findById(id).map(multa -> {
            multa.setRunUsuario(multaActualizada.getRunUsuario());
            multa.setTipoSancion(multaActualizada.getTipoSancion());
            multa.setSancion(multaActualizada.getSancion());
            return multaRepository.save(multa);
        });
    }

    public boolean eliminarMulta(Long id) {
        if (multaRepository.existsById(id)) {
            multaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Método para validar usuario con correo y contraseña
    public ValidacionResponse validarUsuario(String correo, String contrasena) {
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
    }
}
