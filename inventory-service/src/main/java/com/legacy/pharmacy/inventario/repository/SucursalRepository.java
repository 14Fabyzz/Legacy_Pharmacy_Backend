package com.legacy.pharmacy.inventario.repository;

import com.legacy.pharmacy.inventario.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
}
