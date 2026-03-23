package com.legacy.pharmacy.reportes.repository;

import com.legacy.pharmacy.reportes.entity.ParametrosOperativos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParametrosOperativosRepository extends JpaRepository<ParametrosOperativos, Long> {
    Optional<ParametrosOperativos> findBySucursalId(Integer sucursalId);
}
