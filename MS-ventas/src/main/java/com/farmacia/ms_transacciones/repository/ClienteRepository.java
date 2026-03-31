package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNumeroIdentificacion(String numeroIdentificacion);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    List<Cliente> findByActivoTrue();

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND (" +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "c.numeroIdentificacion LIKE CONCAT('%', :termino, '%'))")
    List<Cliente> buscarDinamicamenteActivos(@Param("termino") String termino);
}