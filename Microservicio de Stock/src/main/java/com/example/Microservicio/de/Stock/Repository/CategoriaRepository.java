package com.example.Microservicio.de.Stock.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Microservicio.de.Stock.Model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

 }
