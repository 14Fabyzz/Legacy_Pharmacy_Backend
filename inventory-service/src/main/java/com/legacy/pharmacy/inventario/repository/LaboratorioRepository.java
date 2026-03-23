package com.legacy.pharmacy.inventario.repository;

import com.legacy.pharmacy.inventario.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {

    /** Para el combobox/select del frontend: solo los laboratorios activos */
    List<Laboratorio> findByActivoTrueOrderByNombreAsc();

    /** Para validar nombres duplicados (case-insensitive) */
    Optional<Laboratorio> findByNombreIgnoreCase(String nombre);
}