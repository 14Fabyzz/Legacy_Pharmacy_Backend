package com.legacy.pharmacy.inventario.controller;

import com.legacy.pharmacy.inventario.entity.PrincipioActivo;
import com.legacy.pharmacy.inventario.entity.Sucursal;
import com.legacy.pharmacy.inventario.repository.PrincipioActivoRepository;
import com.legacy.pharmacy.inventario.repository.SucursalRepository;
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

    @Autowired
    private SucursalRepository sucursalRepository;

    // GET http://localhost:8081/api/v1/inventario/principios-activos
    @GetMapping("/principios-activos")
    public ResponseEntity<List<PrincipioActivo>> listarPrincipios() {
        return ResponseEntity.ok(principioActivoRepository.findAll());
    }

    // GET http://localhost:8081/api/v1/inventario/sucursales
    @GetMapping("/sucursales")
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        return ResponseEntity.ok(sucursalRepository.findAll());
    }
}