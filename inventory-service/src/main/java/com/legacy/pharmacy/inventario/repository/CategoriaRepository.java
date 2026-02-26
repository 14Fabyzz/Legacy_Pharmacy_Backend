package com.legacy.pharmacy.inventario.repository;

import com.legacy.pharmacy.inventario.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /** Para el combobox/select del frontend: solo las categorías activas */
    List<Categoria> findByActivaTrueOrderByNombreAsc();

    /** Para validar nombres duplicados (case-insensitive) */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}