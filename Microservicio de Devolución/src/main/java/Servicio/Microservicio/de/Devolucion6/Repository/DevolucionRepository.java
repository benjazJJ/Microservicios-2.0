package Servicio.Microservicio.de.Devolucion6.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Servicio.Microservicio.de.Devolucion6.Model.Devolucion;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {
     boolean existsByIdPrestamo(Integer idPrestamo); // ✅ Agregado para evitar duplicación de devoluciones
}