package Servicio.Microservicio.Recomendacion.Lectura.repository;

import Servicio.Microservicio.Recomendacion.Lectura.model.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecomendacionRepository extends JpaRepository<Recomendacion, Integer> {
    List<Recomendacion> findByCategoria(String categoria);
}
