package com.example.Microservicio.Multas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Microservicio.Multas.Model.Multa;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {

}
