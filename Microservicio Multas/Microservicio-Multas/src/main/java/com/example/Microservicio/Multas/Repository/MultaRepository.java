package com.example.Microservicio.Multas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Microservicio.Multas.Model.Multa;

public interface MultaRepository extends JpaRepository<Multa, Long> {

}
