package com.ayuda.repository;

import com.ayuda.model.SolicitudAyuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudAyudaRepository extends JpaRepository<SolicitudAyuda, Integer> {
    List<SolicitudAyuda> findByCorreoUsuario(String correoUsuario); // Filtro por usuario
}
