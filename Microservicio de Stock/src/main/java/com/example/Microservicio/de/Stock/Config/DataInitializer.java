package com.example.Microservicio.de.Stock.Config;

import com.example.Microservicio.de.Stock.Model.Categoria;
import com.example.Microservicio.de.Stock.Model.EstadoLibro;
import com.example.Microservicio.de.Stock.Model.LibroStock;
import com.example.Microservicio.de.Stock.Repository.CategoriaRepository;
import com.example.Microservicio.de.Stock.Repository.EstadoLibroRepository;
import com.example.Microservicio.de.Stock.Repository.LibroStockRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoLibroRepository estadoLibroRepository;

    @Autowired
    private LibroStockRepository libroStockRepository;

    @PostConstruct
    public void init() {
        // Categorías
        Categoria cienciaFiccion = crearCategoriaSiNoExiste("Ciencia Ficción");
        Categoria fantasia = crearCategoriaSiNoExiste("Fantasía");
        Categoria historia = crearCategoriaSiNoExiste("Historia");
        Categoria educacion = crearCategoriaSiNoExiste("Educación");

        // Estados
        EstadoLibro disponible = crearEstadoSiNoExiste("Disponible");
        EstadoLibro agotado = crearEstadoSiNoExiste("Agotado");

        // Libros
        crearLibroSiNoExiste("Dune", "A1", "F1", 5, cienciaFiccion, disponible);
        crearLibroSiNoExiste("Harry Potter", "A2", "F3", 0, fantasia, agotado);
        crearLibroSiNoExiste("La Segunda Guerra Mundial", "B1", "F5", 3, historia, disponible);
        crearLibroSiNoExiste("Introducción a Java", "C1", "F2", 2, educacion, disponible);
    }

    private Categoria crearCategoriaSiNoExiste(String nombre) {
        return categoriaRepository.findAll().stream()
                .filter(c -> c.getNombreCategoria().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, nombre))); // ID null
    }

    private EstadoLibro crearEstadoSiNoExiste(String nombre) {
        return estadoLibroRepository.findAll().stream()
                .filter(e -> e.getNombreEstado().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(() -> estadoLibroRepository.save(new EstadoLibro(null, nombre))); // ID null
    }

    private void crearLibroSiNoExiste(String nombreLibro, String estante, String fila, int cantidad,
                                      Categoria categoria, EstadoLibro estado) {
        boolean yaExiste = libroStockRepository.findAll().stream()
                .anyMatch(l -> l.getNombreLibro().equalsIgnoreCase(nombreLibro));

        if (!yaExiste) {
            LibroStock libro = new LibroStock();
            libro.setNombreLibro(nombreLibro);
            libro.setEstante(estante);
            libro.setFila(fila);
            libro.setCantidad(cantidad);
            libro.setCategoria(categoria);
            libro.setEstado(estado);
            libroStockRepository.save(libro);
        }
    }
}
