package Microservicio.Recomendaciones.y.Sugerencias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Microservicio.Recomendaciones.y.Sugerencias.model.RecomendacionesySugerencias;
import io.micrometer.common.lang.NonNull;

public interface RecomendacionRepository extends JpaRepository<RecomendacionesySugerencias, Integer> {

    Optional<RecomendacionesySugerencias> findById(@NonNull Integer idEncuesta);

    boolean existsByIdUsuario(Integer idUsuario);
}

