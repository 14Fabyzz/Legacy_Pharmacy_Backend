#!/bin/bash

# Configuración
API_URL="http://localhost:8080/api/v1"
echo "--- 🧪 INICIANDO PRUEBA DE BACKEND: LA TRIADA DEL COSTO ---"

# 1. CREAR PRODUCTO 'PARACETAMOL TEST' (Caja x 30, Fraccionable)
echo -e "\n1. Creando Producto de Prueba (Caja x 30)..."
curl -X POST "$API_URL/productos" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoInterno": "TEST-PARA-001",
    "nombreComercial": "Paracetamol 500mg (Test Backend)",
    "unidadesPorCaja": 30,
    "esFraccionable": true,
    "precioVentaBase": 5000,
    "categoriaId": 1,
    "laboratorioId": 1,
    "estado": "ACTIVO"
}'

# (Asumimos que el ID del producto creado es el último, ej: 100. 
# Ajusta este ID manualmente si es necesario).
PRODUCTO_ID=100 
echo -e "\n\n(Nota: Asumiremos que el ID del producto creado es $PRODUCTO_ID. Si falla, ajusta el script)."

# 2. REGISTRAR ENTRADA (COMPRA)
# Enviamos "1" en cantidad. Si el backend funciona, debe guardar 30.
echo -e "\n2. Comprando 1 CAJA (Entrada de Almacén)..."
curl -X POST "$API_URL/inventario/entrada" \
  -H "Content-Type: application/json" \
  -d "{
    \"productoId\": $PRODUCTO_ID,
    \"numeroLote\": \"LOTE-TEST-2026\",
    \"cantidad\": 1,
    \"costoCompra\": 50000,
    \"fechaVencimiento\": \"2028-12-31\",
    \"usuarioResponsable\": \"ADMIN_TEST\"
}"

# 3. VERIFICAR LA VERDAD (CONSULTA DE STOCK)
echo -e "\n\n3. 🕵️‍♂️ CONSULTANDO LA VERDAD (Stock Real)..."
curl -s "$API_URL/lotes/producto/$PRODUCTO_ID" | grep -o '"cantidadActual":[0-9]*'

echo -e "\n\n--- RESULTADO FINAL ---"
echo "Si ves 'cantidadActual':30 -> ✅ ¡ÉXITO! El Backend multiplicó."
echo "Si ves 'cantidadActual':1  -> ❌ ERROR. El Backend guardó cajas crudas."