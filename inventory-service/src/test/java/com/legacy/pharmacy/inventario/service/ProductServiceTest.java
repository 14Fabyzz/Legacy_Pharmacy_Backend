package com.legacy.pharmacy.inventario.service;

import com.legacy.pharmacy.inventario.dto.ProductoDTO;
import com.legacy.pharmacy.inventario.entity.Categoria;
import com.legacy.pharmacy.inventario.entity.Laboratorio;
import com.legacy.pharmacy.inventario.entity.Producto;
import com.legacy.pharmacy.inventario.repository.CategoriaRepository;
import com.legacy.pharmacy.inventario.repository.LaboratorioRepository;
import com.legacy.pharmacy.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceTest {

        @Autowired
        private ProductoService productoService;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private LaboratorioRepository laboratorioRepository;

        @Test
        public void testPersistenciaUnidadesFraccionadas() {
                // PREPARACIÓN: Crear dependencias
                Categoria cat = new Categoria();
                cat.setNombre("Test Categoria");
                cat = categoriaRepository.save(cat);

                Laboratorio lab = new Laboratorio();
                lab.setNombre("Test Laboratorio");
                lab = laboratorioRepository.save(lab);

                // PASO A: Crear producto con unidadesPorCaja = 12
                ProductoDTO dto = new ProductoDTO();
                dto.setCodigoInterno("TEST-FRACC-001");
                dto.setCodigoBarras("777888999");
                dto.setNombreComercial("Test Dolex");
                dto.setCategoriaId(cat.getId());
                dto.setLaboratorioId(lab.getId());
                dto.setPrecioVentaBase(new BigDecimal("20000"));

                // CAMPOS CLAVE
                dto.setEsFraccionable(true);
                dto.setUnidadesPorCaja(12);
                dto.setPrecioVentaUnidad(new BigDecimal("2000"));

                Producto guardado = productoService.guardarProducto(dto);

                // PASO B: Verificar persistencia inicial
                Assertions.assertNotNull(guardado.getId());
                Assertions.assertEquals(12, guardado.getUnidadesPorCaja());
                Assertions.assertTrue(guardado.getEsFraccionable());

                // PASO C: Actualizar SIN tocar las unidades (solo precio)
                ProductoDTO updateDto = new ProductoDTO();
                updateDto.setPrecioVentaBase(new BigDecimal("25000"));
                // No enviamos unidadesPorCaja (null), debería mantener 12

                Producto actualizado = productoService.actualizarProducto(guardado.getId(), updateDto);

                // PASO D1: Verificar que no se borraron los datos
                Assertions.assertEquals(new BigDecimal("25000"), actualizado.getPrecioVentaBase());
                Assertions.assertEquals(12, actualizado.getUnidadesPorCaja()); // CRÍTICO

                // PASO E: Actualizar CAMBIANDO las unidades
                ProductoDTO updateDto2 = new ProductoDTO();
                updateDto2.setUnidadesPorCaja(24);

                Producto actualizado2 = productoService.actualizarProducto(guardado.getId(), updateDto2);

                // PASO D2: Verificar cambio
                Assertions.assertEquals(24, actualizado2.getUnidadesPorCaja());
        }

        // =========================================================================
        // TESTS UNITARIOS PUROS (sin BD) — Validan la cadena de cálculo de precios
        // =========================================================================

        /**
         * Escenario nominal: producto fraccionable con 30 unidades/caja.
         * costoCompra=$300.000 en 10 cajas → costoCaja=$30.000
         *
         * Esperado:
         * precioVentaBase = 30.000 × 1.30 = $39.000 (caja sin IVA)
         * precioVentaTotal = 39.000 × 1.12 = $43.680 (caja con IVA)
         * precioVentaUnidad = ceil(43.680 / 30)=1.456 → redondear50 → $1.500
         * precioVentaBlister = 1.500 × 10 = $15.000
         */
        @Test
        void testRecalcularPrecios_ProductoFraccionable_CadenaCajaUnidad() {
                // PREPARAR: costo por CAJA
                Producto p = new Producto();
                p.setPrecioCompraReferencia(new BigDecimal("30000")); // $300.000 / 10 cajas
                p.setPorcentajeGanancia(new BigDecimal("30"));
                p.setIvaPorcentaje(new BigDecimal("12"));
                p.setEsFraccionable(true);
                p.setUnidadesPorCaja(30);
                p.setUnidadesPorBlister(10);

                p.recalcularPrecios();

                // precioVentaBase (CAJA sin IVA)
                Assertions.assertEquals(new BigDecimal("39000.00"), p.getPrecioVentaBase(),
                                "precioVentaBase debe ser el precio de la CAJA sin IVA");
                // precioVentaTotal (CAJA con IVA)
                Assertions.assertEquals(new BigDecimal("43680.00"), p.getPrecioVentaTotal(),
                                "precioVentaTotal debe ser el precio de la CAJA con IVA");
                // precioVentaUnidad: 43680/30 = 1456 → redondear50 → 1500
                Assertions.assertEquals(new BigDecimal("1500.0"), p.getPrecioVentaUnidad(),
                                "precioVentaUnidad debe ser el PVP de una sola pastilla");
                // precioVentaBlister: 1500 × 10 = 15000
                Assertions.assertEquals(new BigDecimal("15000.0"), p.getPrecioVentaBlister(),
                                "precioVentaBlister debe ser precioUnidad × unidadesPorBlister");
        }

        /**
         * Verifica que redondearCincuentena NO infla artificialmente precios bajos.
         * Antes del fix: precioUnidad=$8.50 → $50 ❌
         * Después del fix: precioUnidad=$9 (techo de centavo, no de cincuentena) ✅
         */
        @Test
        void testRecalcularPrecios_PrecioBajoNoInflaA50() {
                // Costo bajo: $200/caja, 30 unidades/caja, 20% ganancia, 0% IVA
                // precioVentaTotal = 200 × 1.20 = $240 (caja)
                // precioVentaUnidad = ceil(240 / 30) = ceil(8) = $8 → debe quedar < $50
                Producto p = new Producto();
                p.setPrecioCompraReferencia(new BigDecimal("200"));
                p.setPorcentajeGanancia(new BigDecimal("20"));
                p.setIvaPorcentaje(BigDecimal.ZERO);
                p.setEsFraccionable(true);
                p.setUnidadesPorCaja(30);

                p.recalcularPrecios();

                BigDecimal unidad = p.getPrecioVentaUnidad();
                Assertions.assertTrue(
                                unidad.compareTo(new BigDecimal("50")) < 0,
                                "Precios bajos no deben ser inflados a $50. Valor actual: " + unidad);
                Assertions.assertTrue(
                                unidad.compareTo(BigDecimal.ZERO) > 0,
                                "El precio unitario debe ser positivo");
        }
}
