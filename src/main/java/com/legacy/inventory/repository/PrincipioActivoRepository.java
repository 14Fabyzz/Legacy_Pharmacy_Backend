package com.legacy.inventory.repository;

import com.legacy.inventory.entity.PrincipioActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrincipioActivoRepository extends JpaRepository<PrincipioActivo, Integer> {
}