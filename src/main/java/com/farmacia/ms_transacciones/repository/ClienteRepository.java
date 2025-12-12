package com.farmacia.ms_transacciones.repository;

import com.farmacia.ms_transacciones.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> { // <--- Recuerda usar Long

    Optional<Cliente> findByNumeroIdentificacion(String numeroIdentificacion);

    // SOLUCIÓN ELEGANTE: Usar @Query en lugar de un nombre de método kilométrico
    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "c.numeroIdentificacion LIKE CONCAT('%', :busqueda, '%') OR " +
            "c.telefono LIKE CONCAT('%', :busqueda, '%')")
    List<Cliente> buscarPorCriterio(@Param("busqueda") String busqueda);
}
