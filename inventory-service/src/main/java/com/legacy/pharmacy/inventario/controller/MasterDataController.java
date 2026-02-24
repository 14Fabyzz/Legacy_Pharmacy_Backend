package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.entity.PrincipioActivo;
import com.legacy.pharmacy.inventario.repository.PrincipioActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para datos maestros generales.
 * NOTA: Categorias y Laboratorios ahora tienen su propio controlador dedicado:
 * → CategoriaController (/api/categorias)
 * → LaboratorioController (/api/laboratorios)
 */
@RestController
@RequestMapping("")
public class MasterDataController {

    @Autowired
    private PrincipioActivoRepository principioActivoRepository;

    // GET http://localhost:8080/api/inventario/principios-activos
    @GetMapping("/principios-activos")
    public ResponseEntity<List<PrincipioActivo>> listarPrincipios() {
        return ResponseEntity.ok(principioActivoRepository.findAll());
    }
}