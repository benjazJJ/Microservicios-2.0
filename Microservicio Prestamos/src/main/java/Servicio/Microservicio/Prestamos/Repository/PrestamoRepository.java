package Servicio.Microservicio.Prestamos.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Servicio.Microservicio.Prestamos.Model.Prestamo;
import io.micrometer.common.lang.NonNull;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {
    
    //Buscamos un prestamo por su id
    Optional<Prestamo> findById(@NonNull Integer idPrestamo);

    //Buscamos los prestamos por el RUN del solicitante
    List<Prestamo> findByRunSolicitante(String runSolicitante);

    //Buscamos prestamos por id del libro (En caso de que se necesite consultar los prestamos de un libro especifico)
    List<Prestamo> findByIdLibro(int idLibro);

    //Buscamos prestamos que aun no se han entregado(fechaEntrega es null)
    List<Prestamo> findByFechaEntregaIsNull();

    boolean existsByIdUsuario(Integer idUsuario);
    boolean existsByRunSolicitante(String runSolicitante);

}
