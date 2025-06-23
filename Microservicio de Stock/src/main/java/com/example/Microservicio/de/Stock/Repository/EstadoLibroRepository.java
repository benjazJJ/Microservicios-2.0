package com.example.Microservicio.de.Stock.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Microservicio.de.Stock.Model.EstadoLibro;

public interface EstadoLibroRepository extends JpaRepository<EstadoLibro, Long> {

 }
