# Scripts de Utilidad - Legacy Pharmacy

Esta carpeta contiene scripts de PowerShell y SQL para facilitar el desarrollo, pruebas y configuración del entorno local.

## Scripts Disponibles

### Pruebas y Verificación
*   **`verify_pre_merge.ps1`**: Script maestro de verificación. Ejecuta un flujo completo de venta, pruebas de carga de stock, y validación de seguridad/errores. **Ejecutar esto antes de subir cambios.**
*   **`test_sales_flow.ps1`**: Prueba "Happy Path" específica para el microservicio de ventas.

### Configuración y Datos (Seeds)
*   **`setup_inventory.ps1` / `simple_setup.ps1`**: Scripts para poblar el inventario con productos de prueba a través de la API (útil si la BD está vacía).
*   **`seed_inventory.sql`**: Script SQL directo para insertar Categorías, Laboratorios y Principios Activos en la base de datos MySQL.
*   **`check_stock.ps1`**: Utilidad rápida para consultar el stock de un producto específico y depurar respuestas JSON.

## Uso
Desde la raíz del proyecto:
```powershell
./scripts/verify_pre_merge.ps1
```
