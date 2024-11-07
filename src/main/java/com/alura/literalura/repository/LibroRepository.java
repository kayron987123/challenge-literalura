package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l WHERE 'en' MEMBER OF l.idiomas")
    List<Libro> getLibrosWhereIdiomasIsEn();

    @Query("SELECT l FROM Libro l WHERE 'fr' MEMBER OF l.idiomas")
    List<Libro> getLibrosPorIdiomasEnFrances();

    List<Libro> findTop10ByOrderByNumeroDescargaDesc();
}
